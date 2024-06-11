package com.alan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.alan.mapper")
public class DaziFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaziFinderApplication.class, args);
    }

}
