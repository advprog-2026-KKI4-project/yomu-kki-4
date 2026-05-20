package id.ac.ui.cs.advprog.yomu.learningandquiz.model;

import java.util.ArrayList;
import java.util.List;

public class ReadingMaterial {
    private String id;
    private String title;
    private String category;
    private String content;
    private int timeLimit;
    private int progress;
    private List<Question> questions = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public void addQuestion(Question question) { this.questions.add(question); }
}