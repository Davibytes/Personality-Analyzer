package com.deadline.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.deadline.analyzer")
public class DeadlineAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeadlineAnalyzerApplication.class, args);
    }
}