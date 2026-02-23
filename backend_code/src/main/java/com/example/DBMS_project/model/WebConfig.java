package com.example.DBMS_project.model;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/profile/**")   // URL prefix
                .addResourceLocations(
                        "file:///F:/DBMS_project/profile/"
                )
                .setCachePeriod(0);
    }

}



