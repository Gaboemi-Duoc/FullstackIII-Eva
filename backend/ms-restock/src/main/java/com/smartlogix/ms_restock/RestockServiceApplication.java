package com.smartlogix.ms_restock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RestockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestockServiceApplication.class, args);
    }
}