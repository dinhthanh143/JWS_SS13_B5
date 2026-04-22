package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    //1. Tại sao gọi là "Starter"?
    //Trong Spring Boot, Starter đóng vai trò như một "Thực đơn combo". Thay vì bạn phải tự chọn lẻ tẻ từng nguyên liệu (thư viện), Starter gom tất cả những gì cần thiết để chạy một tính năng cụ thể vào một gói duy nhất.
    //Cơ chế: Nó sử dụng kỹ thuật "Transitive Dependencies" (Phụ thuộc bắc cầu). Khi bạn khai báo Starter, Maven/Gradle sẽ tự động tải về tất cả các thư viện mà Starter đó cần.

    //2. Nó giúp chúng ta không phải khai báo những thư viện lẻ nào?
    //Khi bạn khai báo spring-boot-starter-web, nó âm thầm kéo về khoảng hơn 30 thư viện nhỏ. Những món chính bao gồm:
    //Spring Web MVC: Để xử lý Controller, Routing.
    //Embedded Tomcat: Server nhúng để chạy ứng dụng mà không cần cài Tomcat rời.
    //Jackson: Để tự động chuyển đổi dữ liệu (Object <-> JSON).
    //Spring Boot Starter Logging: Cấu hình sẵn Logback và SLF4J để in log.
    //Spring Boot Starter Validation: (Trong một số phiên bản) để dùng các @Valid, @NotNull.

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("Hospital Web Service is ready on Embedded Tomcat!");
    }
}
