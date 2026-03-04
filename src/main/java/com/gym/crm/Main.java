package com.gym.crm;

import com.gym.crm.config.AppConfig;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.getBean(EntityManagerFactory.class);
        Thread.sleep(300000); // Sleep for 5 minutes
    }
}

