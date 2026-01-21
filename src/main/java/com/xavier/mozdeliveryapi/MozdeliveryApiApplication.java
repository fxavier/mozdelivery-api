package com.xavier.mozdeliveryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith
@SpringBootApplication
public class MozdeliveryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MozdeliveryApiApplication.class, args);
    }
}
