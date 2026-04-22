# LazyInitializationException: Kịch bản lỗi và cách khắc phục

## 1. LazyInitializationException là gì?

**LazyInitializationException** là một exception của Hibernate xảy ra khi cố gắng truy cập vào một collection hoặc entity được load với `FetchType.LAZY` sau khi Hibernate session đã đóng.

## 2. Kịch bản lỗi phổ biến trong hệ thống quản lý đơn thuốc

### Kịch bản 1: Truy cập prescriptionDetails ngoài transaction
```java
// Controller - VẤN ĐỀ
@GetMapping("/view/{id}")
public String viewPrescription(@PathVariable Long id, Model model) {
    Prescription prescription = prescriptionService.getPrescriptionById(id);
    
    // ❌ LỖI: Session đã đóng sau khi service method kết thúc
    // Khi Thymeleaf template cố gắng truy cập prescription.getPrescriptionDetails()
    // sẽ throw LazyInitializationException
    model.addAttribute("prescription", prescription);
    return "prescriptions/view";
}
```

**Lỗi xảy ra khi:**
- Thymeleaf template cố gắng truy cập `${prescription.prescriptionDetails}`
- Hibernate session đã đóng sau khi `getPrescriptionById()` kết thúc
- `prescriptionDetails` được config với `FetchType.LAZY`

### Kịch bản 2: Truy cập patient thông tin trong view
```java
// Template - VẤN ĐỀ
<tr th:each="prescription : ${prescriptions}">
    <td th:text="${prescription.patient.fullName}"></td>  <!-- ❌ Có thể gây lỗi -->
    <td th:text="${prescription.patient.patientCode}"></td>
</tr>
```

**Lỗi xảy ra khi:**
- `patient` relationship được config với `FetchType.LAZY`
- Session đã đóng khi template render
- Cố gắng truy cập `patient.fullName` hoặc các properties khác

### Kịch bản 3: Service method trả về entity với lazy collections
```java
// Service - VẤN ĐỀ
public List<Prescription> getAllPrescriptions() {
    return prescriptionDAO.findAll(); // ❌ Collections không được initialized
}
```

## 3. Cách khắc phục hiệu quả

### Giải pháp 1: Eager Loading (Không khuyến khích cho hệ thống lớn)
```java
// Entity - KHÔNG KHUYẾN KHÍCH
@OneToMany(mappedBy = "prescription", fetch = FetchType.EAGER) // ❌ Performance issue
private List<PrescriptionDetail> prescriptionDetails;
```

**Nhược điểm:**
- Load tất cả dữ liệu kể cả không cần thiết
- Performance kém với dữ liệu lớn
- Có thể gây N+1 query problem

### Giải pháp 2: Join Fetch trong DAO/Repository (KHUYẾN KHÍCH)
```java
// DAO - CÁCH TỐT NHẤT
public Prescription findByIdWithDetails(Long id) {
    Session session = getCurrentSession();
    String hql = "FROM Prescription p LEFT JOIN FETCH p.prescriptionDetails " +
                 "LEFT JOIN FETCH p.patient WHERE p.id = :id";
    try {
        return session.createQuery(hql, Prescription.class)
                .setParameter("id", id)
                .getSingleResult();
    } catch (Exception e) {
        return null;
    }
}

public List<Prescription> findAllWithPatient() {
    Session session = getCurrentSession();
    String hql = "FROM Prescription p LEFT JOIN FETCH p.patient";
    return session.createQuery(hql, Prescription.class).getResultList();
}
```

### Giải pháp 3: Initialize collections trong Service
```java
// Service - CÁCH TỐT
public Prescription getPrescriptionById(Long id) {
    Prescription prescription = prescriptionDAO.findById(id);
    if (prescription != null) {
        // Initialize lazy collections
        Hibernate.initialize(prescription.getPrescriptionDetails());
        Hibernate.initialize(prescription.getPatient());
    }
    return prescription;
}
```

### Giải pháp 4: DTO Pattern (KHUYẾN KHÍCH cho API)
```java
// DTO Class
public class PrescriptionDTO {
    private Long id;
    private String patientCode;
    private String patientName;
    private LocalDate prescriptionDate;
    private String doctorName;
    private List<PrescriptionDetailDTO> prescriptionDetails;
    
    // Constructor và getters
}

// Service - CÁCH TỐT CHO API
public PrescriptionDTO getPrescriptionDTO(Long id) {
    Prescription prescription = prescriptionDAO.findByIdWithDetails(id);
    return convertToDTO(prescription);
}
```

### Giải pháp 5: Open Session in View Pattern (CẢNH BÁO)
```java
// Configuration - KHUYẾN KHÍCH cho Web Application
@Configuration
public class HibernateConfig {
    
    @Bean
    public OpenSessionInViewFilter openSessionInViewFilter() {
        return new OpenSessionInViewFilter();
    }
}
```

**Nhược điểm:**
- Database connection được giữ lâu hơn
- Có thể gây connection pool exhaustion
- Khó debug performance issues

## 4. Implementation trong hệ thống hiện tại

### Cách đã implement trong PrescriptionController:
```java
// Controller - ĐÃ SỬA ĐỔI
@GetMapping("/view/{id}")
public String viewPrescription(@PathVariable Long id, Model model) {
    Prescription prescription = prescriptionService.getPrescriptionById(id);
    if (prescription == null) {
        return "redirect:/prescriptions";
    }
    
    // ✅ Load prescription details để tránh LazyInitializationException
    List<PrescriptionDetail> details = prescriptionService.getPrescriptionDetails(id);
    prescription.setPrescriptionDetails(details);
    
    model.addAttribute("prescription", prescription);
    return "prescriptions/view";
}
```

### DAO method đã được tạo:
```java
// PrescriptionDetailDAO
public List<PrescriptionDetail> findByPrescriptionId(Long prescriptionId) {
    Session session = getCurrentSession();
    String hql = "FROM PrescriptionDetail pd WHERE pd.prescription.id = :prescriptionId";
    return session.createQuery(hql, PrescriptionDetail.class)
            .setParameter("prescriptionId", prescriptionId)
            .getResultList();
}
```

## 5. Best Practices để tránh LazyInitializationException

### 5.1. Design Principles
1. **Never expose entities directly to view layer** - Sử dụng DTOs
2. **Initialize collections within transaction boundaries** 
3. **Use JOIN FETCH cho specific queries**
4. **Keep transactions short and focused**

### 5.2. Code Patterns
```java
// ✅ Tốt: Join Fetch
public Prescription getPrescriptionWithDetails(Long id) {
    String hql = "FROM Prescription p LEFT JOIN FETCH p.prescriptionDetails " +
                 "LEFT JOIN FETCH p.patient WHERE p.id = :id";
    return session.createQuery(hql, Prescription.class)
            .setParameter("id", id)
            .uniqueResult();
}

// ✅ Tốt: Explicit initialization
public Prescription getPrescriptionSafely(Long id) {
    Prescription prescription = findById(id);
    if (prescription != null) {
        Hibernate.initialize(prescription.getPrescriptionDetails());
    }
    return prescription;
}

// ❌ Xấu: Trả về entity với lazy collections
public List<Prescription> getAllPrescriptions() {
    return session.createQuery("FROM Prescription", Prescription.class).list();
}
```

### 5.3. Testing Strategy
```java
@Test
public void testLazyInitializationException() {
    // Test case để verify không có LazyInitializationException
    Prescription prescription = prescriptionService.getPrescriptionById(1L);
    assertNotNull(prescription);
    
    // Verify có thể truy cập collections mà không có exception
    assertDoesNotThrow(() -> {
        prescription.getPrescriptionDetails().size();
        prescription.getPatient().getFullName();
    });
}
```

## 6. Kết luận

**LazyInitializationException** là một trong những lỗi phổ biến nhất khi làm việc với Hibernate. Trong hệ thống quản lý đơn thuốc, lỗi này thường xảy ra khi:

1. Truy cập `prescriptionDetails` sau khi session đóng
2. Truy cập `patient` information trong view layer
3. Service methods trả về entities với lazy relationships

**Giải pháp được khuyến khích:**
- Sử dụng **JOIN FETCH** cho specific queries
- **Initialize collections** trong service layer
- Sử dụng **DTO pattern** cho API responses
- **Keep transactions short** và focused

Với việc implement đúng cách, hệ thống sẽ hoạt động ổn định và tránh được các lỗi liên quan đến lazy loading.
