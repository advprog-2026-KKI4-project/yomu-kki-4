package id.ac.ui.cs.advprog.yomu.model;

import java.util.List;
import java.util.UUID;

public class Question {
    private String id;
    private String questionText;
    private List<String> options;
    private int correctOptionIndex;

    public Question() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
}