package test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DateTimeUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shipspotting {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Random RANDOM = new Random();
    private static final int NUM_RETRIES = 3;

    private static final long MIN_REQUEST_DELAY = 20000L;
    private static final long MAX_REQUEST_DELAY = 30000L;

    public static final int PHOTOS_START = 1160064;
    public static final int PHOTOS_END = 2012798;

    private static final String[] KEYS = {"photoId", "shipName", "imo"};
    private static final Type[] TYPES = {String.class, String.class, String.class};

    private static Connection connection;
    private static String dbFile = "./db/marinetraffic";

    private static final Pattern IMO_PATTERN = Pattern.compile("(?i)IMO:\\s*(\\d+)");
    private static final Pattern ID_PATTERN = Pattern.compile("(?i)lid=(\\d+)");

//    http://www.shipspotting.com/gallery/photo.php?lid=2414382
//    http://www.shipspotting.com/photos/small/2/8/3/2414382.jpg
//    http://www.shipspotting.com/photos/middle/1/8/3/2414381.jpg

    public static void main(String[] args) throws Exception {
        initDb();
        scrape();

//        String html = FileUtils.readFileToString(new File("./res/shipspotting.html"));
//        JSONObject json = transformDetails(html);
//        System.out.println(json);
    }


    private static List<JSONObject> transformDetails(String html) throws Exception {
        List<JSONObject> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("td.whiteboxstroke > table > tbody > tr");
        Matcher m;
        for (Element element : elements) {
            if (element.childNodeSize() < 3) {
                LOGGER.warn("Invalid size: {}", element.text());
                continue;
            }

            Element zeroth = element.child(0);
            String photoUrl = zeroth.child(0).attr("href");
            m = ID_PATTERN.matcher(photoUrl);
            String photoId;
            if (m.find()) {
                photoId = m.group(1);
            } else {
                LOGGER.warn("Cant find id: {}", photoUrl);
                continue;
            }
            String shipName = zeroth.child(0).child(0).attr("alt");

            Element second = element.child(2);
            String txt = second.text();
            m = IMO_PATTERN.matcher(txt);
            String imo = "";
            if (m.find()) {
                imo = m.group(1);
            }
            JSONObject object = new JSONObject();
            object.put("photoId", photoId);
            object.put("shipName", shipName);
            object.put("imo", imo);
            list.add(object);
        }
        return list;
    }

    private static String BASE_URL = "http://www.shipspotting.com/gallery/search.php?" +
            "page_limit=192&limitstart=$1&" +
            "search_title=&search_title_option=1&" +
            "search_imo=&search_pen_no=&" +
            "search_mmsi=&search_eni=&search_callsign=&" +
            "search_category_1=-99&search_cat1childs=&search_uid=&" +
            "search_country=&search_port=&search_subports=&search_flag=&search_homeport=&" +
            "search_adminstatus=&search_classsociety=&search_builder=&search_buildyear1=&" +
            "search_owner=&search_manager=&sortkey=p.lid&sortorder=desc&page_limit=192&viewtype=1";

    private static void scrape() throws Exception {
        for (int i = PHOTOS_START; i < PHOTOS_END; i+= 192) {
            String url = BASE_URL.replace("$1", String.valueOf(i));
            String html = getHtml(url);
            if (html == null)
                continue;
            List<JSONObject> objs = transformDetails(html);
            for (JSONObject obj : objs) {
                insertShip(obj);
            }
            Thread.sleep((long) (RANDOM.nextDouble() * (MAX_REQUEST_DELAY - MIN_REQUEST_DELAY)) + MIN_REQUEST_DELAY);
        }
    }

    private static void prepareStatement(PreparedStatement stmt, int startIdx,
                                         JSONObject obj, String[] keys, Type[] types) throws SQLException {
        int maxIdx = Math.min(keys.length, types.length);
        for (int i = 0, j = startIdx; i < maxIdx; i++, j++) {
            String key = keys[i];
            Type type = types[i];

            if (type.equals(String.class)) {
                if (obj.has(key)) {
                    stmt.setString(j, obj.getString(key));
                } else {
                    stmt.setNull(j, Types.VARCHAR);
                }
            } else if (type.equals(Date.class)) {
                boolean isSet = false;
                if (obj.has(key)) {
                    Optional<Date> optDate = DateTimeUtil.parseISO8601(obj.getString(key));
                    if (optDate.isPresent()) {
                        long dateTime = optDate.get().getTime();
                        Timestamp timestamp = new Timestamp(dateTime);
                        stmt.setTimestamp(j, timestamp, DateTimeUtil.UTCCalendar());
                        isSet = true;
                    }
                }
                if (!isSet) {
                    stmt.setNull(j, Types.TIMESTAMP);
                }
            } else if (type.equals(UUID.class)) {
                boolean isSet = false;
                if (obj.has(key)) {
                    try {
                        UUID uuid = UUID.fromString(obj.getString(key));
                        stmt.setObject(j, uuid);
                        isSet = true;
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn(e);
                    }
                }
                if (!isSet) {
                    stmt.setNull(j, Types.OTHER);
                }
            }

        }
    }

    private static void initDb() throws Exception {
        FileUtils.touch(new File(dbFile));
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ships " +
                        "(photoId TEXT UNIQUE, shipName TEXT, imo TEXT)");) {
            statement.executeUpdate();
        }
    }

    private static void insertShip(JSONObject obj) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR REPLACE INTO ships VALUES (?, ?, ?)");) {
            prepareStatement(statement, 1, obj, KEYS, TYPES);
            statement.executeUpdate();
        }
    }

    private static String getHtml(String url) throws Exception {
        BrowserVersion browserVersion;
        switch (RANDOM.nextInt(2)) {
            case 0:
                browserVersion = BrowserVersion.CHROME;
                break;
            case 1:
                browserVersion = BrowserVersion.EDGE;
                break;
            default:
                browserVersion = BrowserVersion.FIREFOX_38;
        }
        try (WebClient webClient = new WebClient(browserVersion);) {
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setAppletEnabled(false);

            WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
            try {
                Page page = getPage(webClient, request);
                String html = page.getWebResponse().getContentAsString();
                return html;
            } catch (IOException | FailingHttpStatusCodeException e) {
            }
        }
        return null;
    }

    private static <P extends Page> P getPage(WebClient webClient, WebRequest request)
            throws FailingHttpStatusCodeException, IOException {
        LOGGER.info("Grabbing {}", request.getUrl());
        int i = NUM_RETRIES;
        while (true) {
            try {
                return webClient.getPage(request);
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                LOGGER.warn(e);
                if (i-- <= 0) throw e;
            } catch (FailingHttpStatusCodeException e) {
                LOGGER.warn(e);
                if (e.getStatusCode() == 404) {
                    throw e;
                } else {
                    String html = e.getResponse().getContentAsString();
                    LOGGER.info(html);
                    scrapewait();
                    if (i-- <= 0) throw e;
                }
            }
        }
    }

    private static void scrapewait() {
        try {
            Thread.sleep((long) (RANDOM.nextDouble() * (MAX_REQUEST_DELAY - MIN_REQUEST_DELAY)) + MIN_REQUEST_DELAY);
        } catch (InterruptedException e) {
        }
    }

}
