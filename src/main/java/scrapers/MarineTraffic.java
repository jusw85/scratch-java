package scrapers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Scanner;
import java.util.UUID;

public class MarineTraffic {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Random RANDOM = new Random();
    private static final int NUM_RETRIES = 3;

    private static final long MIN_WAIT = 3000L;
    private static final long MAX_WAIT = 2000L;

    private static final String URL_DETAILS = "http://www.marinetraffic.com/en/ais/details/ships";
    private static final String URL_PHOTOS = "http://www.marinetraffic.com/en/photos/of/ships/per_page:50";
//  http://photos.marinetraffic.com/ais/showphoto.aspx?photoid=2026301

    private static final String[] KEYS = {"shipid", "IMO", "MMSI", "Call Sign",
            "Flag", "AIS Type", "Gross Tonnage", "Deadweight", "Length Overall x Breadth Extreme",
            "Year Built", "photoIds"};
    private static final Type[] TYPES = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};
    private static Connection connection;
    private static String dbFile = "./db/marinetraffic";

    public static void main(String[] args) throws Exception {
        initDb();
        scrape();

//        String html = FileUtils.readFileToString(new File("./res/marinetraffic_details.html"));
//        JSONObject json = transformDetails(html);
//        System.out.println(json);

//        String html = FileUtils.readFileToString(new File("./res/marinetraffic_photos_1page.html"));
//        String html = FileUtils.readFileToString(new File("./res/marinetraffic_photos_page1.html"));
//        String html = FileUtils.readFileToString(new File("./res/marinetraffic_photos_none.html"));
//        String html = FileUtils.readFileToString(new File("./res/marinetraffic_photos_page3.html"));
//        boolean hasMore = transformPictures(html, new ArrayList<>());
//        System.out.println(hasMore);
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
                        "(shipid TEXT PRIMARY KEY, imo TEXT, mmsi TEXT, callsign TEXT, flag TEXT, " +
                        "aistype TEXT, gt TEXT, dwt TEXT, lb TEXT, yearbuilt TEXT, photoids TEXT)");) {
            statement.executeUpdate();
        }
    }

    private static void insertShip(JSONObject obj) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR REPLACE INTO ships VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
            prepareStatement(statement, 1, obj, KEYS, TYPES);
            statement.executeUpdate();
        }
    }

    private static void scrape() throws Exception {
        try (Scanner sc = new Scanner(new File("./res/shuffle1000000"))) {
            while (sc.hasNextInt()) {
                int shipId = sc.nextInt();
                String url = URL_DETAILS + "/shipid:" + shipId;
                String html = getHtml(url);
                if (html == null)
                    continue;
                JSONObject object = transformDetails(html);
                if (object.keySet().isEmpty()) {
                    continue;
                }
                int pageNumber = 1;
                List<String> photoIds = new ArrayList<>();
                do {
                    url = URL_PHOTOS + "/page:" + pageNumber++ + "/shipid:" + shipId;
                    html = getHtml(url);
                    if (html == null) {
                        break;
                    }
                } while (transformPictures(html, photoIds));


                if (photoIds.size() > 0) {
                    String prefix = "";
                    StringBuilder sb = new StringBuilder();
                    for (String photoId : photoIds) {
                        sb.append(prefix).append(photoId);
                        prefix = ",";
                    }
                    object.put("photoIds", sb.toString());
                }
                object.put("shipid", String.valueOf(shipId));
                object.remove("Status");

                insertShip(object);

                Thread.sleep((long) (RANDOM.nextDouble() * (MAX_WAIT - MIN_WAIT)) + MIN_WAIT);
            }
        }
    }

    private static boolean transformPictures(String html, List<String> ids) throws Exception {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div#gallery > div > a > img");
        for (Element element : elements) {
            String id = element.attr("id").replaceAll("^photo-", "");
            ids.add(id);
        }
        if (elements.size() > 0) {
            Elements nextpage = doc.select("div.page-nav-panel > div.row > div.col-sm-4.page-nav.group-ib.vertical-offset-10");
            Elements nextdisabled = doc.select("div.page-nav-panel > div.row > div.col-sm-4.page-nav.group-ib.vertical-offset-10 > span.next.disabled ");
            if (!StringUtils.isBlank(nextpage.text()) && nextdisabled.size() <= 0) {
                return true;
            }
        }
        return false;
    }

    private static JSONObject transformDetails(String html) throws Exception {
        JSONObject object = new JSONObject();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div.bg-info.bg-light.padding-10.radius-4.text-left > div.row.equal-height > div.col-xs-6");
        for (Element element : elements) {
            Elements fields = element.select("div.group-ib");
            for (Element field : fields) {
                String key = field.child(0).text().trim();
                String value = field.child(1).text().trim();
                key = key.replaceAll(":$", "");
                value = value.equals("-") ? null : value;
                object.put(key, value);
            }
        }
        return object;
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
                browserVersion = BrowserVersion.FIREFOX_45;
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
            Thread.sleep((long) (RANDOM.nextDouble() * (MAX_WAIT - MIN_WAIT)) + MIN_WAIT);
        } catch (InterruptedException e) {
        }
    }

}
