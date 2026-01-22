package com.xavier.mozdeliveryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableScheduling;

@Modulith
@SpringBootApplication
@EnableScheduling
public class MozdeliveryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MozdeliveryApiApplication.class, args);
    }
}
