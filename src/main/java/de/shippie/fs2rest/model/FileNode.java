package de.shippie.fs2rest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a hierarchical node in the file system tree.
 * Used for tree-based representation of topics.
 * Immutable value object following Clean Code principles.
 */
public final class FileNode {
    
    private final TopicFile file;
    private final List<FileNode> children;
    
    public FileNode(TopicFile file, List<FileNode> children) {
        this.file = Objects.requireNonNull(file, "File must not be null");
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
    }
    
    public FileNode(TopicFile file) {
        this(file, Collections.emptyList());
    }
    
    public TopicFile getFile() {
        return file;
    }
    
    public List<FileNode> getChildren() {
        return children;
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileNode fileNode = (FileNode) o;
        return Objects.equals(file, fileNode.file);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
    
    @Override
    public String toString() {
        return "FileNode{" +
                "file=" + file +
                ", childrenCount=" + children.size() +
                '}';
    }
}
