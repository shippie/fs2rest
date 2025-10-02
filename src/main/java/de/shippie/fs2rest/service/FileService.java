package de.shippie.fs2rest.service;

import org.springframework.stereotype.Service;

/**
 * Service for accessing file system and directory structures.
 * This service will handle reading topics (folders) from the network drive
 * and provide both flat and tree-based representations based on configuration.
 */
@Service
public class FileService {
    
    // TODO: Implement file system access
    // - Read directory structures from local drive / mount on linux or windows
    // - Support flat listing for "ausgabe" folders 
    // - Support tree-based listing for other structures
    // - Configuration for keyword (default: "ausgabe")
    // refresh file structure every 10 minutes (Configuration possible)
    // Application architecure is with internal events (Domain Driven Design)
    
}
