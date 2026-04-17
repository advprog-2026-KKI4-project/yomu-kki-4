package id.ac.ui.cs.advprog.yomu.service;

import id.ac.ui.cs.advprog.yomu.model.*;
import id.ac.ui.cs.advprog.yomu.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReadingMaterialService {

    private final ReadingMaterialRepository materialRepo;
    private final QuizAttemptRepository attemptRepo;

    @Autowired
    public ReadingMaterialService(ReadingMaterialRepository materialRepo, QuizAttemptRepository attemptRepo) {
        this.materialRepo = materialRepo;
        this.attemptRepo = attemptRepo;
    }

    public ReadingMaterial add(ReadingMaterial material) {
        return materialRepo.save(material);
    }

    public List<ReadingMaterial> getAll() {
        return materialRepo.findAll();
    }

    public ReadingMaterial getById(String id) {
        return materialRepo.findById(id);
    }

    public void delete(String id) {
        materialRepo.deleteById(id);
    }

    public double submitQuiz(String userId, String materialId, List<Integer> studentAnswers, long duration) {
        if (attemptRepo.existsByUserIdAndMaterialId(userId, materialId)) {
            throw new IllegalStateException("Quiz already completed.");
        }

        ReadingMaterial material = materialRepo.findById(materialId);
        if (material == null) throw new IllegalArgumentException("Material not found.");

        List<Question> questions = material.getQuestions();
        if (studentAnswers.size() != questions.size()) {
            throw new IllegalArgumentException("Invalid answer count.");
        }

        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectOptionIndex() == studentAnswers.get(i)) {
                correctCount++;
            }
        }

        double score = ((double) correctCount / questions.size()) * 100;
        attemptRepo.save(new QuizAttempt(userId, materialId, score, duration));

        return score;
    }
}