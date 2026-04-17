package id.ac.ui.cs.advprog.yomu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingMaterial {
    private String id;
    private String title;
    private String content;
    private String category;
    private List<Question> questions;

    public ReadingMaterial() {
        this.id = UUID.randomUUID().toString();
        this.questions = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}