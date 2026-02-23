package com.example.wms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WmsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsProjectApplication.class, args);
    }

}
