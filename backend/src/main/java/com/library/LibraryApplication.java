package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.library.mapper")
@EnableTransactionManagement
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
        System.out.println("========================================");
        System.out.println("  图书管理系统后端启动成功  端口:8888");
        System.out.println("  API文档: http://localhost:8888/swagger-ui.html");
        System.out.println("========================================");
    }
}
