package com.example.DBMS_project.controller;

import com.example.DBMS_project.model.FileDTO;
import com.example.DBMS_project.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @GetMapping("/files")
    public ResponseEntity<List<FileDTO>> getFilesRange(
            @RequestParam int start,
            @RequestParam int end) {

        List<FileDTO> files = fileRepository.findFilesBetween(start, end);
        return ResponseEntity.ok(files);
    }
}
