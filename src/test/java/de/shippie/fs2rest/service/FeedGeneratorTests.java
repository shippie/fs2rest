package de.shippie.fs2rest.service;

import com.rometools.rome.io.FeedException;
import de.shippie.fs2rest.model.Article;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeedGeneratorTests {

    private final FeedGenerator feedGenerator = new FeedGenerator();

    @Test
    void shouldGenerateAtomFeed() throws FeedException {
        Article article = new Article(1L, "Test Article", "Test content", LocalDateTime.now(), "Test Author");
        List<Article> articles = List.of(article);
        String baseUrl = "http://localhost:8080";

        String atomFeed = feedGenerator.generateAtomFeed(articles, baseUrl);

        assertNotNull(atomFeed);
        assertTrue(atomFeed.contains("Test Article"));
        assertTrue(atomFeed.contains("Test content"));
        assertTrue(atomFeed.contains("atom"));
    }

    @Test
    void shouldGenerateRssFeed() throws FeedException {
        Article article = new Article(1L, "Test Article", "Test content", LocalDateTime.now(), "Test Author");
        List<Article> articles = List.of(article);
        String baseUrl = "http://localhost:8080";

        String rssFeed = feedGenerator.generateRssFeed(articles, baseUrl);

        assertNotNull(rssFeed);
        assertTrue(rssFeed.contains("Test Article"));
        assertTrue(rssFeed.contains("Test content"));
        assertTrue(rssFeed.contains("rss"));
    }

    @Test
    void shouldGenerateEmptyAtomFeed() throws FeedException {
        List<Article> articles = List.of();
        String baseUrl = "http://localhost:8080";

        String atomFeed = feedGenerator.generateAtomFeed(articles, baseUrl);

        assertNotNull(atomFeed);
        assertTrue(atomFeed.contains("fs2rest Articles"));
    }

    @Test
    void shouldGenerateEmptyRssFeed() throws FeedException {
        List<Article> articles = List.of();
        String baseUrl = "http://localhost:8080";

        String rssFeed = feedGenerator.generateRssFeed(articles, baseUrl);

        assertNotNull(rssFeed);
        assertTrue(rssFeed.contains("fs2rest Articles"));
    }
}
