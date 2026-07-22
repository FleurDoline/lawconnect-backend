package org.arited.lawconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LawconnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(LawconnectApplication.class, args);
    }

}