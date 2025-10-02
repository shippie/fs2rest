package de.shippie.fs2rest.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a file within a topic.
 * Immutable value object following Clean Code principles.
 */
public final class TopicFile {
    
    private final String name;
    private final String path;
    private final long size;
    private final Instant lastModified;
    private final boolean isDirectory;
    
    public TopicFile(String name, String path, long size, Instant lastModified, boolean isDirectory) {
        this.name = Objects.requireNonNull(name, "File name must not be null");
        this.path = Objects.requireNonNull(path, "File path must not be null");
        this.size = size;
        this.lastModified = Objects.requireNonNull(lastModified, "Last modified must not be null");
        this.isDirectory = isDirectory;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public long getSize() {
        return size;
    }
    
    public Instant getLastModified() {
        return lastModified;
    }
    
    public boolean isDirectory() {
        return isDirectory;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicFile that = (TopicFile) o;
        return Objects.equals(path, that.path);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
    
    @Override
    public String toString() {
        return "TopicFile{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", lastModified=" + lastModified +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
