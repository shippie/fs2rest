package org.mifa.fs2rest.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mifa.fs2rest.model.FileNode;
import org.mifa.fs2rest.model.Topic;
import org.mifa.fs2rest.model.TopicFile;
import org.mifa.fs2rest.service.FileService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/topics")
@Tag(name = "Topics", description = "Themen-Explorer API für Topics und Dateien")
public class TopicController {

  private final FileService fileService;

  public TopicController(FileService fileService) {
    this.fileService = fileService;
  }

  @GetMapping
  //  @Operation(
  //      summary = "Liste aller Topics",
  //      description = "Gibt alle verfügbaren Themen (Ordner) zurück.")
  public ResponseEntity<CollectionModel<EntityModel<Topic>>> getAllTopics() {
    List<EntityModel<Topic>> topics =
        fileService.getAllTopics().stream()
            .map(
                topic ->
                    EntityModel.of(
                        topic,
                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(TopicController.class)
                                    .getTopic(topic.getName()))
                            .withSelfRel(),
                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(TopicController.class).getAllTopics())
                            .withRel("topics")))
            .collect(Collectors.toList());
    return ResponseEntity.ok(
        CollectionModel.of(
            topics,
            WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(TopicController.class).getAllTopics())
                .withSelfRel()));
  }

  @GetMapping("/{topicName}")
  //  @Operation(
  //      summary = "Details zu einem Topic",
  //      description =
  //          "Gibt Details und Dateien eines Themas zurück. Flach oder baumförmig je nach
  // Konfiguration.")
  public ResponseEntity<EntityModel<Topic>> getTopic(
      // @Parameter(description = "Name des Themas")
      @PathVariable String topicName) {

    Optional<Topic> topicOpt = fileService.getTopic(topicName);
    if (topicOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Topic topic = topicOpt.get();
    EntityModel<Topic> model =
        EntityModel.of(
            topic,
            WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(TopicController.class).getTopic(topicName))
                .withSelfRel(),
            WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(TopicController.class).getAllTopics())
                .withRel("topics"),
            linkToFiles(topicName));
    return ResponseEntity.ok(model);
  }

  private static Link linkToFiles(String topicName) {
    return WebMvcLinkBuilder.linkTo(TopicController.class)
        .slash(topicName)
        .slash("files")
        .withRel("files");
  }

  @GetMapping("/{topicName}/files")
  //  @Operation(
  //      summary = "Dateien eines Topics",
  //      description =
  //          "Gibt die Dateien eines Themas zurück. Flach oder baumförmig je nach Konfiguration.")
  public ResponseEntity<?> getTopicFiles(
      // @Parameter(description = "Name des Themas")
      @PathVariable String topicName) throws IOException {
    return getTopicFilesInternal(topicName);
  }

  // Hilfsmethode ohne checked Exception für Link-Erstellung
  private ResponseEntity<?> getTopicFilesInternal(String topicName) {
    try {
      Optional<Topic> topicOpt = fileService.getTopic(topicName);
      if (topicOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      Topic topic = topicOpt.get();
      if (topic.isFlat()) {
        List<TopicFile> files = fileService.getTopicFilesFlat(topicName);
        return ResponseEntity.ok(files);
      } else {
        FileNode tree = fileService.getTopicFilesTree(topicName);
        return ResponseEntity.ok(tree);
      }
    } catch (IOException e) {
      return ResponseEntity.internalServerError()
          .body("Fehler beim Lesen der Dateien: " + e.getMessage());
    }
  }
}
