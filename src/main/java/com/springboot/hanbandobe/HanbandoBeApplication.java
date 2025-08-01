package com.springboot.hanbandobe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HanbandoBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanbandoBeApplication.class, args);
    }

}
