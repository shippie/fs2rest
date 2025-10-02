package de.shippie.fs2rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Fs2restApplication {

    public static void main(String[] args) {
        SpringApplication.run(Fs2restApplication.class, args);
    }

}
