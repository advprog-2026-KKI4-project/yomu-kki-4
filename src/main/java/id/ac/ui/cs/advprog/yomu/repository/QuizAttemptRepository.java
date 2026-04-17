package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.QuizAttempt;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuizAttemptRepository {
    private final List<QuizAttempt> attempts = new ArrayList<>();

    public void save(QuizAttempt attempt) {
        attempts.add(attempt);
    }

    public boolean existsByUserIdAndMaterialId(String userId, String materialId) {
        return attempts.stream()
                .anyMatch(a -> a.getUserId().equals(userId) && a.getMaterialId().equals(materialId));
    }
}