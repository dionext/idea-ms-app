package com.dionext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import java.util.Locale;


@SpringBootApplication
@ComponentScan
@ComponentScan(basePackages = "com.dionext.utils")
@ComponentScan(basePackages = "com.dionext.site")
@Slf4j
@EnableCaching
//@EnableMethodSecurity
public class IdeaMsAppApplication implements ApplicationRunner {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(IdeaMsAppApplication.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.debug("ShutdownHook...")));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Application running");
    }

}
