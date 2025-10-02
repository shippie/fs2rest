package de.shippie.fs2rest.service;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service for generating Atom and RSS feeds.
 * According to ARC42: Creates RSS/Atom-Feed from current files.
 * This service will be extended to generate feeds from file system topics.
 */
@Service
public class FeedGenerator {

    // TODO: Implement feed generation from file system topics
    // - Generate Atom feed from topics (folders) and files
    // - Generate RSS feed from topics (folders) and files
    // - Include file metadata (last modified date, size, etc.)
    
    /**
     * Generates an Atom feed.
     * To be implemented: Generate from file system topics.
     */
    public String generateAtomFeed(String baseUrl) throws FeedException {
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        feed.setTitle("fs2rest Topics");
        feed.setId(baseUrl + "/feed/atom");
        
        Link link = new Link();
        link.setHref(baseUrl + "/feed/atom");
        link.setRel("self");
        feed.setAlternateLinks(List.of(link));
        
        feed.setUpdated(new Date());
        
        // TODO: Add entries from file system topics
        feed.setEntries(List.of());

        WireFeedOutput output = new WireFeedOutput();
        return output.outputString(feed);
    }

    /**
     * Generates an RSS feed.
     * To be implemented: Generate from file system topics.
     */
    public String generateRssFeed(String baseUrl) throws FeedException {
        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle("fs2rest Topics");
        channel.setDescription("RSS feed for fs2rest topics");
        channel.setLink(baseUrl + "/feed/rss");
        channel.setPubDate(new Date());

        // TODO: Add items from file system topics
        channel.setItems(List.of());

        WireFeedOutput output = new WireFeedOutput();
        return output.outputString(channel);
    }
}
