package de.shippie.fs2rest.service;

import de.shippie.fs2rest.config.TopicsProperties;
import de.shippie.fs2rest.event.TopicsCacheRefreshedEvent;
import de.shippie.fs2rest.model.FileNode;
import de.shippie.fs2rest.model.Topic;
import de.shippie.fs2rest.model.TopicFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Service for accessing file system and directory structures.
 * This service handles reading topics (folders) from the network drive
 * and provides both flat and tree-based representations based on configuration.
 * 
 * Key responsibilities:
 * - Read directory structures from local drive / mount on linux or windows
 * - Support flat listing for folders containing the configured keyword (default: "ausgabe")
 * - Support tree-based listing for other structures
 * - Cache topics with scheduled refresh (default: every 10 minutes)
 * - Publish domain events following Domain-Driven Design principles
 */
@Service
public class FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    
    private final TopicsProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<String, Topic> topicsCache;
    
    public FileService(TopicsProperties properties, ApplicationEventPublisher eventPublisher) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
        this.topicsCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Initializes the service by loading topics on startup.
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing FileService with path: {}, flat keyword: {}, refresh interval: {}",
                properties.getPath(), properties.getFlatKeyword(), properties.getRefreshInterval());
        refreshTopicsCache();
    }
    
    /**
     * Scheduled method to refresh topics cache periodically.
     * Fixed delay is configured via TopicsProperties.
     */
    @Scheduled(fixedDelayString = "#{@topicsProperties.refreshInterval.toMillis()}")
    public void refreshTopicsCache() {
        logger.debug("Refreshing topics cache...");
        
        Path basePath = Paths.get(properties.getPath());
        
        if (!Files.exists(basePath)) {
            logger.warn("Base path does not exist: {}", basePath);
            return;
        }
        
        if (!Files.isDirectory(basePath)) {
            logger.warn("Base path is not a directory: {}", basePath);
            return;
        }
        
        try {
            List<Topic> topics = loadTopicsFromFileSystem(basePath);
            
            topicsCache.clear();
            topics.forEach(topic -> topicsCache.put(topic.getName(), topic));
            
            logger.info("Topics cache refreshed. Found {} topics", topics.size());
            
            eventPublisher.publishEvent(
                new TopicsCacheRefreshedEvent(topics.size(), Instant.now())
            );
            
        } catch (IOException e) {
            logger.error("Error refreshing topics cache", e);
        }
    }
    
    /**
     * Gets all available topics.
     * 
     * @return List of all topics
     */
    public List<Topic> getAllTopics() {
        return new ArrayList<>(topicsCache.values());
    }
    
    /**
     * Gets a specific topic by name.
     * 
     * @param name The topic name
     * @return Optional containing the topic if found
     */
    public Optional<Topic> getTopic(String name) {
        return Optional.ofNullable(topicsCache.get(name));
    }
    
    /**
     * Gets files for a topic in flat representation.
     * Used when the topic contains a folder with the configured flat keyword.
     * 
     * @param topicName The topic name
     * @return List of files in flat structure
     * @throws IOException If an I/O error occurs
     */
    public List<TopicFile> getTopicFilesFlat(String topicName) throws IOException {
        Topic topic = topicsCache.get(topicName);
        if (topic == null) {
            return Collections.emptyList();
        }
        
        if (!topic.isFlat()) {
            logger.warn("Topic {} does not have flat structure, but flat listing requested", topicName);
        }
        
        Path topicPath = Paths.get(topic.getPath());
        Path flatPath = topicPath.resolve(properties.getFlatKeyword());
        
        if (!Files.exists(flatPath) || !Files.isDirectory(flatPath)) {
            logger.debug("Flat folder not found for topic {}, falling back to topic root", topicName);
            flatPath = topicPath;
        }
        
        return loadFilesFlat(flatPath);
    }
    
    /**
     * Gets files for a topic in tree representation.
     * Used for topics without the configured flat keyword folder.
     * 
     * @param topicName The topic name
     * @return Root node of the file tree
     * @throws IOException If an I/O error occurs
     */
    public FileNode getTopicFilesTree(String topicName) throws IOException {
        Topic topic = topicsCache.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicName);
        }
        
        Path topicPath = Paths.get(topic.getPath());
        return buildFileTree(topicPath);
    }
    
    // Private helper methods
    
    private List<Topic> loadTopicsFromFileSystem(Path basePath) throws IOException {
        List<Topic> topics = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath, Files::isDirectory)) {
            for (Path path : stream) {
                String name = path.getFileName().toString();
                Instant lastModified = Files.getLastModifiedTime(path).toInstant();
                boolean hasFlat = hasFlatKeywordFolder(path);
                
                topics.add(new Topic(name, path.toString(), lastModified, hasFlat));
            }
        }
        
        return topics;
    }
    
    private boolean hasFlatKeywordFolder(Path topicPath) {
        Path flatPath = topicPath.resolve(properties.getFlatKeyword());
        return Files.exists(flatPath) && Files.isDirectory(flatPath);
    }
    
    private List<TopicFile> loadFilesFlat(Path directory) throws IOException {
        List<TopicFile> files = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     try {
                         files.add(createTopicFile(path));
                     } catch (IOException e) {
                         logger.warn("Error reading file: {}", path, e);
                     }
                 });
        }
        
        return files;
    }
    
    private FileNode buildFileTree(Path directory) throws IOException {
        TopicFile rootFile = createTopicFile(directory);
        List<FileNode> children = new ArrayList<>();
        
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path path : stream) {
                    children.add(buildFileTree(path));
                }
            }
        }
        
        return new FileNode(rootFile, children);
    }
    
    private TopicFile createTopicFile(Path path) throws IOException {
        String name = path.getFileName().toString();
        long size = Files.isRegularFile(path) ? Files.size(path) : 0;
        Instant lastModified = Files.getLastModifiedTime(path).toInstant();
        boolean isDirectory = Files.isDirectory(path);
        
        return new TopicFile(name, path.toString(), size, lastModified, isDirectory);
    }
}
