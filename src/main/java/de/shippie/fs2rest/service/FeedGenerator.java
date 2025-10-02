package de.shippie.fs2rest.service;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import de.shippie.fs2rest.model.Article;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FeedGenerator {

    public String generateAtomFeed(List<Article> articles, String baseUrl) throws FeedException {
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        feed.setTitle("fs2rest Articles");
        feed.setId(baseUrl + "/feed/atom");
        
        Link link = new Link();
        link.setHref(baseUrl + "/feed/atom");
        link.setRel("self");
        feed.setAlternateLinks(List.of(link));
        
        feed.setUpdated(new Date());

        List<Entry> entries = new ArrayList<>();
        for (Article article : articles) {
            Entry entry = new Entry();
            entry.setTitle(article.getTitle());
            entry.setId(baseUrl + "/api/articles/" + article.getId());
            
            Link entryLink = new Link();
            entryLink.setHref(baseUrl + "/api/articles/" + article.getId());
            entry.setAlternateLinks(List.of(entryLink));
            
            Content content = new Content();
            content.setType("text");
            content.setValue(article.getContent());
            entry.setContents(List.of(content));
            
            if (article.getPublishedDate() != null) {
                entry.setPublished(Date.from(article.getPublishedDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            
            entries.add(entry);
        }
        feed.setEntries(entries);

        WireFeedOutput output = new WireFeedOutput();
        return output.outputString(feed);
    }

    public String generateRssFeed(List<Article> articles, String baseUrl) throws FeedException {
        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle("fs2rest Articles");
        channel.setDescription("RSS feed for fs2rest articles");
        channel.setLink(baseUrl + "/feed/rss");
        channel.setPubDate(new Date());

        List<Item> items = new ArrayList<>();
        for (Article article : articles) {
            Item item = new Item();
            item.setTitle(article.getTitle());
            item.setLink(baseUrl + "/api/articles/" + article.getId());
            
            Description description = new Description();
            description.setValue(article.getContent());
            item.setDescription(description);
            
            if (article.getPublishedDate() != null) {
                item.setPubDate(Date.from(article.getPublishedDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            
            items.add(item);
        }
        channel.setItems(items);

        WireFeedOutput output = new WireFeedOutput();
        return output.outputString(channel);
    }
}
