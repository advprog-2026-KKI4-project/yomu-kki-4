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

        attemptRepo.deleteByMaterialId(id);
    }

    public double submitQuiz(String userId, String materialId, List<Integer> studentAnswers, long duration) {
        ReadingMaterial material = materialRepo.findById(materialId);
        if (material == null) throw new IllegalArgumentException("Material not found.");

        List<Question> questions = material.getQuestions();
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectOptionIndex() == studentAnswers.get(i)) {
                correctCount++;
            }
        }
        double baseScore = ((double) correctCount / questions.size()) * 100;

        double timeLimit = material.getTimeLimit();
        double remaining = Math.max(0, timeLimit - duration);
        double timeBonus = (remaining / timeLimit) * 10.0;

        double finalScore = baseScore + timeBonus;

        material.setProgress(100);
        materialRepo.save(material);
        attemptRepo.save(new QuizAttempt(userId, materialId, finalScore, duration, studentAnswers));

        return finalScore;
    }
}