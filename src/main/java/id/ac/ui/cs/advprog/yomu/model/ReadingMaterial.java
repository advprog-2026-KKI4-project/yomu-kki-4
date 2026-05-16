package id.ac.ui.cs.advprog.yomu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingMaterial {
    private String id;
    private String title;
    private String category;
    private String content;
    private List<Question> questions;
    private int progress;
    private int timeLimit;

    public ReadingMaterial() {
        this.id = UUID.randomUUID().toString();
        this.questions = new ArrayList<>();
        this.progress = 0;
        this.timeLimit = 300;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = (timeLimit <= 0) ? 300 : timeLimit;
    }
}