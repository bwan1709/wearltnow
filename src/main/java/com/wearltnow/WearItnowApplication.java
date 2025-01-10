package com.wearltnow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@Slf4j
@EnableCaching
public class WearItnowApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WearItnowApplication.class, args);
    }
}
