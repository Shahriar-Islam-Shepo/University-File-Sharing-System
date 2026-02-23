package com.example.DBMS_project.controller;


import com.example.DBMS_project.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;

@CrossOrigin(origins = "*")
@Controller
public class LoginController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/upi/authenticate")
    public String authenticate(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {

        String sql = "SELECT u.username, u.profile_pic_path, r.role_name " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.role_id " +
                "WHERE u.username = ? AND u.password_hash = ?";

        try {
            // Fetch data using Spring JDBC
            UserProfile user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                UserProfile up = new UserProfile();
                up.setUsername(rs.getString("username"));
                up.setRoleName(rs.getString("role_name"));
                up.setProfilePicPath(rs.getString("profile_pic_path"));
                return up;
            }, username, password);

            assert user != null;
            System.out.println(user.getProfilePicPath());
            System.out.println(user.getUsername());
            System.out.println(user.getRoleName());

            // Pass the user object to the Thymeleaf template
            model.addAttribute("user", user);
            return "profile"; // Returns src/main/resources/templates/profile.html

        } catch (EmptyResultDataAccessException e) {
            // Redirect back to the plain HTML login page if user is not found
            return "redirect:/profile.html";
        }
    }
}