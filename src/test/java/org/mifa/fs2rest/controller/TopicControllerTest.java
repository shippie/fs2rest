package org.mifa.fs2rest.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mifa.fs2rest.model.FileNode;
import org.mifa.fs2rest.model.Topic;
import org.mifa.fs2rest.model.TopicFile;
import org.mifa.fs2rest.service.FileService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TopicControllerTest {

  private MockMvc mockMvc;

  @Mock private FileService fileService;

  @InjectMocks private TopicController topicController;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(topicController).build();
  }

  @Test
  @DisplayName("GET /topics gibt alle Topics mit Links zurück")
  void getAllTopics() throws Exception {
    Topic topic = new Topic("test", "/pfad/test", Instant.now(), false);
    given(fileService.getAllTopics()).willReturn(List.of(topic));

    mockMvc
        .perform(get("/topics").accept("application/hal+json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("content[0].name").value("test"))
        .andExpect(jsonPath("content[0].links[?(@.rel=='self')].href").exists())
        .andExpect(jsonPath("links[?(@.rel=='self')].href").exists());
  }

  @Test
  @DisplayName("GET /topics/{topicName} gibt Topic mit Links zurück")
  void getTopic() throws Exception {
    Topic topic = new Topic("test", "/pfad/test", Instant.now(), false);
    given(fileService.getTopic("test")).willReturn(Optional.of(topic));

    mockMvc
        .perform(get("/topics/test").accept("application/hal+json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").value("test"))
        .andExpect(jsonPath("links[?(@.rel=='self')].href").exists())
        .andExpect(jsonPath("links[?(@.rel=='files')].href").exists());
  }

  @Test
  @DisplayName("GET /topics/{topicName} gibt 404 für unbekanntes Topic")
  void getTopicNotFound() throws Exception {
    given(fileService.getTopic(anyString())).willReturn(Optional.empty());
    mockMvc.perform(get("/topics/unknown")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /topics/{topicName}/files gibt flache Dateiliste zurück")
  void getTopicFilesFlat() throws Exception {
    Topic topic = new Topic("flat", "/pfad/flat", Instant.now(), true);
    given(fileService.getTopic("flat")).willReturn(Optional.of(topic));
    given(fileService.getTopicFilesFlat("flat"))
        .willReturn(
            List.of(new TopicFile("file1.txt", "/pfad/flat/file1.txt", 123, Instant.now(), false)));

    mockMvc
        .perform(get("/topics/flat/files").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("[0].name").value("file1.txt"));
  }

  @Test
  @DisplayName("GET /topics/{topicName}/files gibt Baumstruktur zurück")
  void getTopicFilesTree() throws Exception {
    Topic topic = new Topic("tree", "/pfad/tree", Instant.now(), false);
    given(fileService.getTopic("tree")).willReturn(Optional.of(topic));
    FileNode root =
        new FileNode(new TopicFile("tree", "/pfad/tree", 0, Instant.now(), true), List.of());
    given(fileService.getTopicFilesTree("tree")).willReturn(root);

    mockMvc
        .perform(get("/topics/tree/files").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("file.name").value("tree"));
  }

  @Test
  @DisplayName("GET /topics/{topicName}/files gibt 404 für unbekanntes Topic")
  void getTopicFilesNotFound() throws Exception {
    given(fileService.getTopic(anyString())).willReturn(Optional.empty());
    mockMvc.perform(get("/topics/unknown/files")).andExpect(status().isNotFound());
  }
}
