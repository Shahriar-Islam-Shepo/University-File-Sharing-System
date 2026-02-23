package com.example.DBMS_project.repository;

import com.example.DBMS_project.model.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<FileDTO> findFilesBetween(int start, int end) {
        String sql = "SELECT f.file_id, f.title, c.category_name, f.description, " +
                "f.upload_date, u.username " +
                "FROM files f " +
                "JOIN categories c ON f.category_id = c.category_id " +
                "JOIN users u ON f.user_id = u.user_id " +
                "WHERE f.file_id BETWEEN ? AND ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FileDTO(
                rs.getInt("file_id"),
                rs.getString("title"),
                rs.getString("category_name"),
                rs.getString("description"), // Mapped to 'topic'
                rs.getTimestamp("upload_date").toLocalDateTime().toLocalDate().toString(),
                rs.getString("username")
        ), start, end);
    }
}
