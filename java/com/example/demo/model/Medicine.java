
//Phần 1 - Phân tích: Tại sao và Thế nào?
//1. Tại sao Hibernate bắt buộc phải có @Id?
//Hibernate (và chuẩn JPA nói chung) coi mỗi đối tượng Java là một đại diện cho một dòng (row) trong Database. Để quản lý vòng đời của đối tượng đó (như cập nhật đúng dòng, xóa đúng người), Hibernate cần một định danh duy nhất để phân biệt các dòng với nhau.
//Nếu không có @Id, Hibernate sẽ không biết dòng nào là dòng nào khi bạn gọi lệnh save() hay delete().
//Lỗi "No identifier specified for entity" chính là lời phàn nàn của Hibernate: "Tôi thấy bảng này rồi, nhưng tôi không biết cột nào là khóa chính để tôi quản lý!".

//2. Cách cấu hình tên bảng khác với tên Class Java?
//Mặc định, Hibernate dùng chiến lược "Naming Strategy" để tự suy luận tên bảng từ tên Class (ví dụ: Class Medicine thành bảng Medicine hoặc medicine).
//Để ghi đè (override) quy tắc này và đặt tên bảng theo đúng quy định của bệnh viện (viết thường, số nhiều), chúng ta sử dụng Annotation @Table(name = "tên_bảng") ngay phía trên tên Class.
package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "unit")
    private String unit;

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;
}
