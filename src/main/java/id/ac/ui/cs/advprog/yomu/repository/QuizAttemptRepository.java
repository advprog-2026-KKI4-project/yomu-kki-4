package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.QuizAttempt;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuizAttemptRepository {
    private final List<QuizAttempt> attemptData = new ArrayList<>();

    public QuizAttempt save(QuizAttempt attempt) {
        attemptData.removeIf(a -> a.getId().equals(attempt.getId()));
        attemptData.add(attempt);
        return attempt;
    }

    public QuizAttempt findByUserIdAndMaterialId(String userId, String materialId) {
        return attemptData.stream()
                .filter(a -> a.getUserId().equals(userId) && a.getMaterialId().equals(materialId))
                .findFirst()
                .orElse(null);
    }

    public boolean existsByUserIdAndMaterialId(String userId, String materialId) {
        return findByUserIdAndMaterialId(userId, materialId) != null;
    }

    public List<QuizAttempt> findAll() {
        return new ArrayList<>(attemptData);
    }

    public void deleteByMaterialId(String materialId) {
        attemptData.removeIf(attempt -> attempt.getMaterialId().equals(materialId));
    }
}