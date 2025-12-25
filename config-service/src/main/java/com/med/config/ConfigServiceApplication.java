package com.med.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {
    public static void main(String[] args) {
        // Принудительно включаем профиль "native" для работы с локальными файлами
        System.setProperty("spring.profiles.active", "native");
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}