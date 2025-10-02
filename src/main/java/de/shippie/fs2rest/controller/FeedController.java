package de.shippie.fs2rest.controller;

import com.rometools.rome.io.FeedException;
import de.shippie.fs2rest.model.Article;
import de.shippie.fs2rest.service.FeedGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private ArticleController articleController;

    @Autowired
    private FeedGenerator feedGenerator;

    @GetMapping(value = "/atom", produces = "application/atom+xml")
    public ResponseEntity<String> getAtomFeed(HttpServletRequest request) {
        try {
            List<Article> articles = articleController.getArticlesForFeed();
            String feedString = feedGenerator.generateAtomFeed(articles, getBaseUrl(request));

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
            List<Article> articles = articleController.getArticlesForFeed();
            String feedString = feedGenerator.generateRssFeed(articles, getBaseUrl(request));

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
