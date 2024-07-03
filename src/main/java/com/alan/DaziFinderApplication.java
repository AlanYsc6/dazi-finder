package com.alan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.alan.mapper")
@EnableScheduling
public class DaziFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaziFinderApplication.class, args);
    }

}
