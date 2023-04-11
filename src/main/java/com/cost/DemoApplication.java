package com.cost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot启动器
 */

// 定义该类为SpringBoot启动器
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // 启动Spring容器，并且加载该类上的@SpringBootApplication注解
        SpringApplication.run(DemoApplication.class, args);
    }
}
