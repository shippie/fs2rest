package de.shippie.fs2rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllArticles() throws Exception {
        mockMvc.perform(get("/api/articles"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/hal+json"))
            .andExpect(jsonPath("$._embedded.articleList", hasSize(greaterThanOrEqualTo(0))))
            .andExpect(jsonPath("$._links.self.href", notNullValue()));
    }

    @Test
    void shouldCreateArticle() throws Exception {
        String articleJson = """
            {
                "title": "Test Article",
                "content": "This is a test article",
                "author": "Test Author"
            }
            """;

        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(articleJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("Test Article")))
            .andExpect(jsonPath("$.content", is("This is a test article")))
            .andExpect(jsonPath("$.author", is("Test Author")))
            .andExpect(jsonPath("$._links.self.href", notNullValue()));
    }

    @Test
    void shouldGetArticleById() throws Exception {
        // First create an article
        String articleJson = """
            {
                "title": "Test Article for Get",
                "content": "This is a test article for get",
                "author": "Test Author"
            }
            """;

        String location = mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(articleJson))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");

        // Then get it
        mockMvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Test Article for Get")))
            .andExpect(jsonPath("$._links.self.href", notNullValue()));
    }

    @Test
    void shouldReturn404ForNonExistentArticle() throws Exception {
        mockMvc.perform(get("/api/articles/999999"))
            .andExpect(status().isNotFound());
    }
}
