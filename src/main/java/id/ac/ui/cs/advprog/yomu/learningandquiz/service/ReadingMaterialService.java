package id.ac.ui.cs.advprog.yomu.learningandquiz.service;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.Question;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.ReadingMaterialRepository;
import id.ac.ui.cs.advprog.yomu.achievement.event.QuizCompletedEvent;
import id.ac.ui.cs.advprog.yomu.achievement.event.ReadingCompletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReadingMaterialService {

    private final ReadingMaterialRepository materialRepo;
    private final QuizAttemptRepository attemptRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReadingMaterialService(ReadingMaterialRepository materialRepo,
                                  QuizAttemptRepository attemptRepo,
                                  ApplicationEventPublisher eventPublisher) {
        this.materialRepo = materialRepo;
        this.attemptRepo = attemptRepo;
        this.eventPublisher = eventPublisher;
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

    public void completeReading(String userId, String materialId) {
        ReadingMaterial material = materialRepo.findById(materialId);
        if (material != null) {
            material.setProgress(50);
            materialRepo.save(material);
        }
        eventPublisher.publishEvent(new ReadingCompletedEvent(userId));
    }

    public double submitQuiz(String userId, String materialId, List<Integer> studentAnswers, long duration) {
        if (attemptRepo.existsByUserIdAndMaterialId(userId, materialId)) {
            throw new IllegalStateException("User has already attempted this quiz.");
        }

        ReadingMaterial material = materialRepo.findById(materialId);
        if (material == null) throw new IllegalArgumentException("Material not found.");

        List<Question> questions = material.getQuestions();
        if (questions.isEmpty()) {
            throw new IllegalStateException("Material has no questions.");
        }

        if (studentAnswers.size() != questions.size()) {
            throw new IllegalArgumentException("Answer count does not match question count.");
        }

        int timeLimit = material.getTimeLimit();
        if (timeLimit <= 0) {
            throw new IllegalStateException("Invalid time limit.");
        }

        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectOptionIndex() == studentAnswers.get(i)) {
                correctCount++;
            }
        }
        double baseScore = ((double) correctCount / questions.size()) * 100;

        double remaining = Math.max(0, timeLimit - duration);
        double timeBonus = (remaining / timeLimit) * 10.0;

        double finalScore = baseScore + timeBonus;

        material.setProgress(100);
        materialRepo.save(material);
        attemptRepo.save(new QuizAttempt(userId, materialId, finalScore, duration, studentAnswers));

        eventPublisher.publishEvent(new QuizCompletedEvent(userId, finalScore));

        return finalScore;
    }
}