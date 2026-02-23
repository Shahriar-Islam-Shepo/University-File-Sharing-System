package com.example.DBMS_project.model;

public class FileDTO {
    private Integer id;
    private String title;
    private String cat;
    private String topic;
    private String date;
    private String user;

    // Constructors, Getters, and Setters
    public FileDTO(Integer id, String title, String cat, String topic, String date, String user) {
        this.id = id;
        this.title = title;
        this.cat = cat;
        this.topic = topic;
        this.date = date;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
