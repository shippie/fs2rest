package de.shippie.fs2rest.service;

import de.shippie.fs2rest.config.TopicsProperties;
import de.shippie.fs2rest.event.TopicsCacheRefreshedEvent;
import de.shippie.fs2rest.model.FileNode;
import de.shippie.fs2rest.model.Topic;
import de.shippie.fs2rest.model.TopicFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTests {

    @TempDir
    Path tempDir;

    private FileService fileService;
    private TopicsProperties properties;
    private AtomicInteger eventCount;

    @BeforeEach
    void setUp() {
        properties = new TopicsProperties();
        properties.setPath(tempDir.toString());
        properties.setFlatKeyword("ausgabe");
        
        eventCount = new AtomicInteger(0);
        ApplicationEventPublisher eventPublisher = event -> {
            if (event instanceof TopicsCacheRefreshedEvent) {
                eventCount.incrementAndGet();
            }
        };
        
        fileService = new FileService(properties, eventPublisher);
    }

    @Test
    void contextLoads() {
        assertNotNull(fileService);
    }

    @Test
    void shouldLoadTopicsFromFileSystem() throws IOException {
        // Given
        createTestDirectory("topic1");
        createTestDirectory("topic2");
        
        // When
        fileService.refreshTopicsCache();
        List<Topic> topics = fileService.getAllTopics();
        
        // Then
        assertEquals(2, topics.size());
        assertTrue(topics.stream().anyMatch(t -> t.getName().equals("topic1")));
        assertTrue(topics.stream().anyMatch(t -> t.getName().equals("topic2")));
    }

    @Test
    void shouldDetectFlatTopics() throws IOException {
        // Given
        Path topic1 = createTestDirectory("topic1");
        createTestDirectory("topic2");
        Files.createDirectory(topic1.resolve("ausgabe"));
        
        // When
        fileService.refreshTopicsCache();
        Optional<Topic> flatTopic = fileService.getTopic("topic1");
        Optional<Topic> treeTopic = fileService.getTopic("topic2");
        
        // Then
        assertTrue(flatTopic.isPresent());
        assertTrue(flatTopic.get().isFlat());
        
        assertTrue(treeTopic.isPresent());
        assertFalse(treeTopic.get().isFlat());
    }

    @Test
    void shouldGetTopicByName() throws IOException {
        // Given
        createTestDirectory("test-topic");
        fileService.refreshTopicsCache();
        
        // When
        Optional<Topic> topic = fileService.getTopic("test-topic");
        
        // Then
        assertTrue(topic.isPresent());
        assertEquals("test-topic", topic.get().getName());
    }

    @Test
    void shouldReturnEmptyForNonExistentTopic() {
        // When
        Optional<Topic> topic = fileService.getTopic("non-existent");
        
        // Then
        assertFalse(topic.isPresent());
    }

    @Test
    void shouldLoadFilesFlat() throws IOException {
        // Given
        Path topic = createTestDirectory("flat-topic");
        Path ausgabe = Files.createDirectory(topic.resolve("ausgabe"));
        createTestFile(ausgabe, "file1.txt", "content1");
        createTestFile(ausgabe, "file2.txt", "content2");
        
        Path subDir = Files.createDirectory(ausgabe.resolve("subdir"));
        createTestFile(subDir, "file3.txt", "content3");
        
        fileService.refreshTopicsCache();
        
        // When
        List<TopicFile> files = fileService.getTopicFilesFlat("flat-topic");
        
        // Then
        assertEquals(3, files.size());
        assertTrue(files.stream().anyMatch(f -> f.getName().equals("file1.txt")));
        assertTrue(files.stream().anyMatch(f -> f.getName().equals("file2.txt")));
        assertTrue(files.stream().anyMatch(f -> f.getName().equals("file3.txt")));
    }

    @Test
    void shouldLoadFilesTree() throws IOException {
        // Given
        Path topic = createTestDirectory("tree-topic");
        createTestFile(topic, "root.txt", "root content");
        
        Path subDir = Files.createDirectory(topic.resolve("subdir"));
        createTestFile(subDir, "nested.txt", "nested content");
        
        fileService.refreshTopicsCache();
        
        // When
        FileNode tree = fileService.getTopicFilesTree("tree-topic");
        
        // Then
        assertNotNull(tree);
        assertEquals("tree-topic", tree.getFile().getName());
        assertTrue(tree.getFile().isDirectory());
        
        // Should have children (root.txt and subdir)
        assertEquals(2, tree.getChildren().size());
        
        // Check subdir has nested file
        FileNode subDirNode = tree.getChildren().stream()
            .filter(n -> n.getFile().getName().equals("subdir"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(subDirNode);
        assertTrue(subDirNode.getFile().isDirectory());
        assertEquals(1, subDirNode.getChildren().size());
        assertEquals("nested.txt", subDirNode.getChildren().get(0).getFile().getName());
    }

    @Test
    void shouldPublishCacheRefreshEvent() throws IOException {
        // Given
        createTestDirectory("topic1");
        int initialCount = eventCount.get();
        
        // When
        fileService.refreshTopicsCache();
        
        // Then
        assertEquals(initialCount + 1, eventCount.get());
    }

    @Test
    void shouldHandleNonExistentBasePath() {
        // Given
        properties.setPath("/non/existent/path");
        FileService service = new FileService(properties, event -> {});
        
        // When
        service.refreshTopicsCache();
        List<Topic> topics = service.getAllTopics();
        
        // Then
        assertTrue(topics.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForNonExistentTopicFiles() throws IOException {
        // When
        List<TopicFile> files = fileService.getTopicFilesFlat("non-existent-topic");
        
        // Then
        assertTrue(files.isEmpty());
    }

    @Test
    void shouldThrowExceptionForNonExistentTopicTree() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            fileService.getTopicFilesTree("non-existent-topic");
        });
    }

    @Test
    void shouldHandleEmptyTopic() throws IOException {
        // Given
        createTestDirectory("empty-topic");
        fileService.refreshTopicsCache();
        
        // When
        FileNode tree = fileService.getTopicFilesTree("empty-topic");
        
        // Then
        assertNotNull(tree);
        assertTrue(tree.getChildren().isEmpty());
    }

    @Test
    void shouldUseCustomFlatKeyword() throws IOException {
        // Given
        properties.setFlatKeyword("output");
        FileService service = new FileService(properties, event -> {});
        
        Path topic = createTestDirectory("custom-topic");
        Files.createDirectory(topic.resolve("output"));
        
        // When
        service.refreshTopicsCache();
        Optional<Topic> topicOpt = service.getTopic("custom-topic");
        
        // Then
        assertTrue(topicOpt.isPresent());
        assertTrue(topicOpt.get().isFlat());
    }

    @Test
    void shouldIncludeFileMetadata() throws IOException {
        // Given
        Path topic = createTestDirectory("metadata-topic");
        Path ausgabe = Files.createDirectory(topic.resolve("ausgabe"));
        Path file = createTestFile(ausgabe, "test.txt", "test content");
        
        fileService.refreshTopicsCache();
        
        // When
        List<TopicFile> files = fileService.getTopicFilesFlat("metadata-topic");
        
        // Then
        assertEquals(1, files.size());
        TopicFile topicFile = files.get(0);
        
        assertEquals("test.txt", topicFile.getName());
        assertTrue(topicFile.getSize() > 0);
        assertNotNull(topicFile.getLastModified());
        assertFalse(topicFile.isDirectory());
    }

    // Helper methods

    private Path createTestDirectory(String name) throws IOException {
        return Files.createDirectory(tempDir.resolve(name));
    }

    private Path createTestFile(Path parent, String name, String content) throws IOException {
        Path file = parent.resolve(name);
        Files.writeString(file, content);
        return file;
    }
}
