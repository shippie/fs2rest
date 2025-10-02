package de.shippie.fs2rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FeedControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAtomFeed() throws Exception {
        mockMvc.perform(get("/feed/atom"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/atom+xml"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("<feed")));
    }

    @Test
    void shouldReturnRssFeed() throws Exception {
        mockMvc.perform(get("/feed/rss"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/rss+xml"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("<rss")));
    }
}
