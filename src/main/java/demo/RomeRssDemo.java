package demo;


import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RomeRssDemo {

    public static void main(String[] args) throws Exception {
        InputStream in = new URL("http://lorem-rss.herokuapp.com/feed").openStream();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(in));
        List<SyndEntry> entries = feed.getEntries();
        entries.forEach(System.out::println);
    }

}
