package com.mobilelearning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mobilelearning.mapper")
public class MobileLearningApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileLearningApiApplication.class, args);
    }
}
