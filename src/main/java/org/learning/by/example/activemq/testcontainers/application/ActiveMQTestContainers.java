package org.learning.by.example.activemq.testcontainers.application;

import org.learning.by.example.activemq.testcontainers.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApplicationContext.class})
public class ActiveMQTestContainers {
    public static void main(String[] args) {
        SpringApplication.run(ActiveMQTestContainers.class, args);
    }
}
