package com.example.DBMS_project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JavaMailSender mailSender;

    // Use ConcurrentHashMap for thread safety
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    // Scheduler to handle code expiration
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        try {
            // 1. Generate 4-digit code
            String code = String.valueOf((int)(Math.random() * 9000) + 1000);
            verificationCodes.put(email, code);

            // 2. Set expiration: Remove the code from the map after 5 minutes
            scheduler.schedule(() -> verificationCodes.remove(email), 5, TimeUnit.MINUTES);

            // 3. Prepare Email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("shahriarshepo@gmail.com"); // Using your config email
            message.setTo(email);
            message.setSubject("UniVault Registration Code");
            message.setText("Your UniVault verification code is: " + code + "\n\nThis code will expire in 5 minutes.");

            // 4. Send Email
            mailSender.send(message);

            return ResponseEntity.ok("Verification code sent to " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: Unable to send email. " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("code") String code,
            @RequestParam("image") MultipartFile image) throws IOException {

        // 1. Check if code exists and is correct
        String storedCode = verificationCodes.get(email);
        if (storedCode == null) {
            return ResponseEntity.status(400).body("Code expired. Please request a new one.");
        }
        if (!storedCode.equals(code)) {
            return ResponseEntity.status(400).body("Invalid verification code.");
        }

        // 2. File handling
        String folderPath = "F:\\DBMS_project\\profile\\";
        File directory = new File(folderPath);
        if (!directory.exists()) directory.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path path = Paths.get(folderPath + fileName);
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // 3. Database operation
        try {
            String sql = "INSERT INTO users (username, email, password_hash, profile_pic_path) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, username, email, password, "profile/" + fileName);

            // Success! Remove code immediately
            verificationCodes.remove(email);

            return ResponseEntity.ok("Registration successful!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }


    public String fun(){
        System.out.println("ok this is me");
        return "hello world";
    }


    @PostMapping("/uploads")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("username") String username,
            @RequestParam("categoryId") int categoryId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {

        // Define storage location (Make sure this path exists on your F: drive)
        String uploadDir = "F:/DBMS_project/uploads/";

        System.out.println("ok no problem");
        try {
            // 1. Find User ID from username
            Integer userId;
            try {
                userId = jdbcTemplate.queryForObject(
                        "SELECT user_id FROM users WHERE username = ?", Integer.class, username);
            } catch (Exception e) {
                return ResponseEntity.status(404).body("User not found: " + username);
            }

            if (file.isEmpty()) {
                return ResponseEntity.status(400).body("Please select a file to upload.");
            }

            // 2. Prepare File Storage
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path path = Paths.get(uploadDir + uniqueFilename);

            // 3. Save file to physical disk
            Files.write(path, file.getBytes());

            // 4. Save metadata to Database
            String sql = "INSERT INTO files (user_id, category_id, title, description, file_path, file_size_kb, mime_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    userId,
                    categoryId,
                    title,
                    description,
                    "uploads/" + uniqueFilename, // Matches your DB storage style
                    (file.getSize() / 1024),
                    file.getContentType()
            );

            // Return a JSON response instead of a redirect
            return ResponseEntity.ok(Map.of(
                    "message", "File uploaded successfully!",
                    "fileName", uniqueFilename,
                    "status", "success"
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500).body("File system error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database error: " + e.getMessage());
        }
    }


}
