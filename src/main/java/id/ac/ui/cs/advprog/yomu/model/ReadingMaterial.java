package id.ac.ui.cs.advprog.yomu.model;

import java.util.UUID;

public class ReadingMaterial {
    private String id;
    private String title;
    private String content;
    private String category;

    public ReadingMaterial() {
        this.id = UUID.randomUUID().toString();
    }

    public ReadingMaterial(String title, String content, String category) {
        this();
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}