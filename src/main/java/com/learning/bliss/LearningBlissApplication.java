package com.learning.bliss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LearningBlissApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningBlissApplication.class, args);
    }

}
