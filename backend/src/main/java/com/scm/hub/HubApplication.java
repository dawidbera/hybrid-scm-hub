package com.scm.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Hybrid-Cloud SCM Hub application.
 * This class initializes the Spring Boot context and starts the application.
 */
@SpringBootApplication
public class HubApplication {

    /**
     * Main method that launches the Spring Boot application.
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(HubApplication.class, args);
    }
}
