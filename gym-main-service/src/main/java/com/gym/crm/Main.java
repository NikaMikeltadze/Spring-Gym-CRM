package com.gym.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJms
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
