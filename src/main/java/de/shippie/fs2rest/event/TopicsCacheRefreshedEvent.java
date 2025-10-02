package de.shippie.fs2rest.event;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event published when topics cache is refreshed.
 * Follows Domain-Driven Design principles for internal event handling.
 */
public class TopicsCacheRefreshedEvent {
    
    private final int topicCount;
    private final Instant timestamp;
    
    public TopicsCacheRefreshedEvent(int topicCount, Instant timestamp) {
        this.topicCount = topicCount;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp must not be null");
    }
    
    public int getTopicCount() {
        return topicCount;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "TopicsCacheRefreshedEvent{" +
                "topicCount=" + topicCount +
                ", timestamp=" + timestamp +
                '}';
    }
}
