package de.shippie.fs2rest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTests {

    @Autowired
    private FileService fileService;

    @Test
    void contextLoads() {
        assertNotNull(fileService);
    }
}
