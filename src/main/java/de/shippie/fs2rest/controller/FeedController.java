package de.shippie.fs2rest.controller;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import de.shippie.fs2rest.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private ArticleController articleController;

    @GetMapping(value = "/atom", produces = "application/atom+xml")
    public ResponseEntity<String> getAtomFeed(HttpServletRequest request) {
        try {
            Feed feed = new Feed();
            feed.setFeedType("atom_1.0");
            feed.setTitle("fs2rest Articles");
            feed.setId(getBaseUrl(request) + "/feed/atom");
            
            Link link = new Link();
            link.setHref(getBaseUrl(request) + "/feed/atom");
            link.setRel("self");
            feed.setAlternateLinks(List.of(link));
            
            feed.setUpdated(new Date());

            List<Entry> entries = new ArrayList<>();
            for (Article article : articleController.getArticlesForFeed()) {
                Entry entry = new Entry();
                entry.setTitle(article.getTitle());
                entry.setId(getBaseUrl(request) + "/api/articles/" + article.getId());
                
                Link entryLink = new Link();
                entryLink.setHref(getBaseUrl(request) + "/api/articles/" + article.getId());
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
            String feedString = output.outputString(feed);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "atom+xml"));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(feedString);
                
        } catch (FeedException e) {
            return ResponseEntity.internalServerError().body("Error generating Atom feed: " + e.getMessage());
        }
    }

    @GetMapping(value = "/rss", produces = "application/rss+xml")
    public ResponseEntity<String> getRssFeed(HttpServletRequest request) {
        try {
            Channel channel = new Channel();
            channel.setFeedType("rss_2.0");
            channel.setTitle("fs2rest Articles");
            channel.setDescription("RSS feed for fs2rest articles");
            channel.setLink(getBaseUrl(request) + "/feed/rss");
            channel.setPubDate(new Date());

            List<Item> items = new ArrayList<>();
            for (Article article : articleController.getArticlesForFeed()) {
                Item item = new Item();
                item.setTitle(article.getTitle());
                item.setLink(getBaseUrl(request) + "/api/articles/" + article.getId());
                
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
            String feedString = output.outputString(channel);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "rss+xml"));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(feedString);
                
        } catch (FeedException e) {
            return ResponseEntity.internalServerError().body("Error generating RSS feed: " + e.getMessage());
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);
        return url.toString();
    }
}
