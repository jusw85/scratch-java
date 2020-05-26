package scrapers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Random;

public class WebClientWrapper implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Random RANDOM = new Random();

    private long minRequestDelay = 20000L;
    private long maxRequestDelay = 30000L;
    private int numRetries = 3;

    private WebClient webClient;

    public WebClientWrapper(boolean useRandomBrowser) {
        BrowserVersion browserVersion;
        if (useRandomBrowser) {
            browserVersion = getRandomBrowserVersion();
        } else {
            browserVersion = BrowserVersion.BEST_SUPPORTED;
        }
        webClient = new WebClient(browserVersion);
        WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled(false);
        options.setCssEnabled(false);
    }

    private BrowserVersion getRandomBrowserVersion() {
        BrowserVersion browserVersion;
        switch (RANDOM.nextInt(2)) {
            case 0:
                browserVersion = BrowserVersion.CHROME;
                break;
            case 1:
                browserVersion = BrowserVersion.INTERNET_EXPLORER;
                break;
            default:
                browserVersion = BrowserVersion.FIREFOX_68;
        }
        return browserVersion;
    }

    public String getHtml(String url) throws IOException, InterruptedException {
        WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
        Page page = getPage(webClient, request);
        String html = page.getWebResponse().getContentAsString();
        return html;
    }

    private <P extends Page> P getPage(WebClient webClient, WebRequest request)
            throws IOException, InterruptedException {
        LOGGER.info("Grabbing {}", request.getUrl());
        int i = numRetries;
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
                    LOGGER.debug(html);
                    sleep();
                    if (i-- <= 0) throw e;
                }
            }
        }
    }

    public void sleep() throws InterruptedException {
        Thread.sleep((long) (RANDOM.nextDouble() * (maxRequestDelay - minRequestDelay)) + minRequestDelay);
    }

    @Override
    public void close() {
        if (webClient != null) {
            webClient.close();
        }
    }

    public int getNumRetries() {
        return numRetries;
    }

    public void setNumRetries(int numRetries) {
        this.numRetries = numRetries;
    }

    public long getMaxRequestDelay() {
        return maxRequestDelay;
    }

    public void setMaxRequestDelay(long maxRequestDelay) {
        this.maxRequestDelay = maxRequestDelay;
    }

    public long getMinRequestDelay() {
        return minRequestDelay;
    }

    public void setMinRequestDelay(long minRequestDelay) {
        this.minRequestDelay = minRequestDelay;
    }

}
