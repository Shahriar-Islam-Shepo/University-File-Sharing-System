package com.example.DBMS_project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class viewFile {

    // Inject the actual JdbcTemplate bean
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/files/view/{id}")
    public ResponseEntity<Resource> viewFile(@PathVariable int id) {
        // 1. Fetch the file path and type from the database
        String sql = "SELECT file_path, mime_type FROM files WHERE file_id = ?";


        return jdbcTemplate.query(sql, (rs) -> {
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                System.out.println(filePath);
                String mimeType = rs.getString("mime_type");
                File file = new File("F:\\DBMS_project"+File.separator+filePath);

                // 2. Check if the physical file exists on the disk
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }

                Resource resource = new FileSystemResource(file);

                // 3. Return the file stream
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        // "inline" tells the browser to try and show it (PDF/Images)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        }, id);
    }


    @GetMapping("/files/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int id) {
        // 1. Fetch file path from the database
        String sql = "SELECT file_path, mime_type FROM files WHERE file_id = ?";

        return jdbcTemplate.query(sql, (rs) -> {
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                String mimeType = rs.getString("mime_type");

                System.out.println("it is come to download");
                // 2. Locate the file on your F: drive
                File file = new File("F:\\DBMS_project" + File.separator + filePath);

                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }

                Resource resource = new FileSystemResource(file);

                // 3. Return the response with "attachment" header
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        // "attachment" forces the browser to download instead of opening
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        }, id);
    }







}
