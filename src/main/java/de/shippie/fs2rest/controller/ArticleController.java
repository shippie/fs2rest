package de.shippie.fs2rest.controller;

import de.shippie.fs2rest.model.Article;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final Map<Long, Article> articles = new HashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public ArticleController() {
        // Initialize with some sample data
        createArticle(new Article(null, "Welcome to fs2rest", 
            "This is a sample article demonstrating REST with HATEOAS and Feed support.", 
            LocalDateTime.now(), "Admin"));
        createArticle(new Article(null, "Spring Boot 3", 
            "Building modern web applications with Spring Boot 3 and Java 25.", 
            LocalDateTime.now().minusDays(1), "Admin"));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<Article>> getAllArticles() {
        List<Article> articleList = new ArrayList<>();
        
        for (Article article : articles.values()) {
            Article articleWithLinks = new Article(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getPublishedDate(),
                article.getAuthor()
            );
            articleWithLinks.add(linkTo(methodOn(ArticleController.class).getArticle(article.getId())).withSelfRel());
            articleList.add(articleWithLinks);
        }
        
        CollectionModel<Article> collectionModel = CollectionModel.of(articleList);
        collectionModel.add(linkTo(methodOn(ArticleController.class).getAllArticles()).withSelfRel());
        collectionModel.add(Link.of("/feed/atom").withRel("atom-feed"));
        collectionModel.add(Link.of("/feed/rss").withRel("rss-feed"));
        
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        Article article = articles.get(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        
        Article articleWithLinks = new Article(
            article.getId(),
            article.getTitle(),
            article.getContent(),
            article.getPublishedDate(),
            article.getAuthor()
        );
        articleWithLinks.add(linkTo(methodOn(ArticleController.class).getArticle(id)).withSelfRel());
        articleWithLinks.add(linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("all-articles"));
        
        return ResponseEntity.ok(articleWithLinks);
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Long id = counter.incrementAndGet();
        Article newArticle = new Article(
            id,
            article.getTitle(),
            article.getContent(),
            article.getPublishedDate() != null ? article.getPublishedDate() : LocalDateTime.now(),
            article.getAuthor()
        );
        articles.put(id, newArticle);
        
        Article articleWithLinks = new Article(
            newArticle.getId(),
            newArticle.getTitle(),
            newArticle.getContent(),
            newArticle.getPublishedDate(),
            newArticle.getAuthor()
        );
        articleWithLinks.add(linkTo(methodOn(ArticleController.class).getArticle(id)).withSelfRel());
        articleWithLinks.add(linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("all-articles"));
        
        return ResponseEntity.created(articleWithLinks.getRequiredLink("self").toUri()).body(articleWithLinks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        if (!articles.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Article updatedArticle = new Article(
            id,
            article.getTitle(),
            article.getContent(),
            article.getPublishedDate(),
            article.getAuthor()
        );
        articles.put(id, updatedArticle);
        
        Article articleWithLinks = new Article(
            updatedArticle.getId(),
            updatedArticle.getTitle(),
            updatedArticle.getContent(),
            updatedArticle.getPublishedDate(),
            updatedArticle.getAuthor()
        );
        articleWithLinks.add(linkTo(methodOn(ArticleController.class).getArticle(id)).withSelfRel());
        articleWithLinks.add(linkTo(methodOn(ArticleController.class).getAllArticles()).withRel("all-articles"));
        
        return ResponseEntity.ok(articleWithLinks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        if (!articles.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        articles.remove(id);
        return ResponseEntity.noContent().build();
    }

    public List<Article> getArticlesForFeed() {
        return new ArrayList<>(articles.values());
    }
}
