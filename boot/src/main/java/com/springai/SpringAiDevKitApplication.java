package com.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI Dev Kit 启动类
 */
@SpringBootApplication
public class SpringAiDevKitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDevKitApplication.class, args);
        System.out.println("""
                
                ╔═══════════════════════════════════════════════════════════╗
                ║                                                           ║
                ║          Spring AI Dev Kit 启动成功！                     ║
                ║                                                           ║
                ║          Swagger UI: http://localhost:8080/swagger-ui    ║
                ║          API Docs: http://localhost:8080/v3/api-docs     ║
                ║                                                           ║
                ╚═══════════════════════════════════════════════════════════╝
                """);
    }
}

