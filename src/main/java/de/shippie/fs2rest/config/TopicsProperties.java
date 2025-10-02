package de.shippie.fs2rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration properties for file system topics.
 * Maps application properties with prefix "fs2rest.topics".
 */
@Configuration
@ConfigurationProperties(prefix = "fs2rest.topics")
public class TopicsProperties {
    
    /**
     * Path to the network drive where topics (folders) are located.
     */
    private String path = "./topics";
    
    /**
     * Keyword for flat file listing.
     * When a folder contains this keyword, files are listed flat.
     * Otherwise, tree structure is preserved.
     */
    private String flatKeyword = "ausgabe";
    
    /**
     * Interval for refreshing the file structure cache.
     */
    private Duration refreshInterval = Duration.ofMinutes(10);
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = Objects.requireNonNull(path, "Path must not be null");
    }
    
    public String getFlatKeyword() {
        return flatKeyword;
    }
    
    public void setFlatKeyword(String flatKeyword) {
        this.flatKeyword = Objects.requireNonNull(flatKeyword, "Flat keyword must not be null");
    }
    
    public Duration getRefreshInterval() {
        return refreshInterval;
    }
    
    public void setRefreshInterval(Duration refreshInterval) {
        this.refreshInterval = Objects.requireNonNull(refreshInterval, "Refresh interval must not be null");
    }
}
