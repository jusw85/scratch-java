package demo;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RomeRssDemo {

    public static void main(String[] args) throws Exception {
        InputStream in = new URL("http://lorem-rss.herokuapp.com/feed").openStream();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(in));
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry entry : entries) {
            System.out.println(entry);
        }
    }

}
