package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    // 1. Lay duong dan tu file application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 2. Danh sach cac duoi file duoc phep (tranh nguoi dung up file .exe hoac .sh)
    private final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    public String saveFile(MultipartFile file) throws IOException {
        // 3. Kiem tra file co ton tai khong
        if (file == null || file.isEmpty()) return null;

        // 4. Xu ly duong dan thu muc (Fix loi ky hieu ${user.home})
        Path rootPath = Paths.get(uploadDir.replace("${user.home}", System.getProperty("user.home")));

        // Neu thu muc chua co thi tu tao moi
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
        }

        // 5. Lay extension va kiem tra dinh dang
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase() : "";

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Dinh dang file khong hop le (chi nhan jpg, png, webp).");
        }

        // 6. Dat ten file moi bang UUID de tranh trung lap
        String fileName = UUID.randomUUID().toString() + "." + ext;

        // 7. Copy file vao thu muc dich
        Files.copy(file.getInputStream(), rootPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        return fileName; // Tra ve ten file de luu vao Database (Object)
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Path rootPath = Paths.get(uploadDir.replace("${user.home}", System.getProperty("user.home")));
            Files.deleteIfExists(rootPath.resolve(fileName));
        } catch (IOException e) {
            System.err.println("Loi khi xoa file: " + fileName);
        }
    }
}