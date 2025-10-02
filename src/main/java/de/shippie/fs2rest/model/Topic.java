package de.shippie.fs2rest.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a topic (folder) in the file system.
 * Immutable value object following Clean Code principles.
 */
public final class Topic {
    
    private final String name;
    private final String path;
    private final Instant lastModified;
    private final boolean hasFlat;
    
    public Topic(String name, String path, Instant lastModified, boolean hasFlat) {
        this.name = Objects.requireNonNull(name, "Topic name must not be null");
        this.path = Objects.requireNonNull(path, "Topic path must not be null");
        this.lastModified = Objects.requireNonNull(lastModified, "Last modified must not be null");
        this.hasFlat = hasFlat;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public Instant getLastModified() {
        return lastModified;
    }
    
    public boolean isFlat() {
        return hasFlat;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(name, topic.name) && 
               Objects.equals(path, topic.path);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
    
    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", lastModified=" + lastModified +
                ", hasFlat=" + hasFlat +
                '}';
    }
}
