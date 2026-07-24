package com.rentnest;

import com.rentnest.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class RentNestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentNestApplication.class, args);
    }
}
