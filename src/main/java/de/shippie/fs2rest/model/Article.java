package de.shippie.fs2rest.model;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class Article extends RepresentationModel<Article> {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime publishedDate;
    private final String author;

    @JsonCreator
    public Article(@JsonProperty("id") Long id,
                   @JsonProperty("title") String title,
                   @JsonProperty("content") String content,
                   @JsonProperty("publishedDate") LocalDateTime publishedDate,
                   @JsonProperty("author") String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishedDate = publishedDate;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
