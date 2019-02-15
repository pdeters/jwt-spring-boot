package com.pdeters

import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@Slf4j
@SpringBootApplication
class Application {

    static void main(String... args) {
        log.info('Application starting ...')
        SpringApplication.run(Application.class, args)
    }
}