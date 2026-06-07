package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementServiceImpl service;

    private Achievement achievement;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        achievement = Achievement.builder()
                .id(id)
                .name("Bookworm")
                .description("Read 10 articles")
                .type(AchievementType.READING)
                .targetCount(10)
                .points(50)
                .badgeIcon("book.svg")
                .build();
    }

    @Test
    void create_savesAndReturns() {
        when(achievementRepository.save(achievement)).thenReturn(achievement);
        assertThat(service.create(achievement)).isEqualTo(achievement);
        verify(achievementRepository).save(achievement);
    }

    @Test
    void findAll_delegatesToRepository() {
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        assertThat(service.findAll()).containsExactly(achievement);
    }

    @Test
    void findById_returnsWhenFound() {
        when(achievementRepository.findById(id)).thenReturn(Optional.of(achievement));
        assertThat(service.findById(id)).isEqualTo(achievement);
    }

    @Test
    void findById_throwsWhenNotFound() {
        UUID missing = UUID.randomUUID();
        when(achievementRepository.findById(missing)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(missing))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(missing.toString());
    }

    @Test
    void findByType_delegatesToRepository() {
        when(achievementRepository.findByType(AchievementType.READING)).thenReturn(List.of(achievement));
        assertThat(service.findByType(AchievementType.READING)).containsExactly(achievement);
    }

    @Test
    void update_updatesAllFieldsAndSaves() {
        Achievement updated = Achievement.builder()
                .name("Super Reader").description("Read 20 articles")
                .type(AchievementType.READING).targetCount(20).points(100).badgeIcon("star.svg")
                .build();
        when(achievementRepository.findById(id)).thenReturn(Optional.of(achievement));
        when(achievementRepository.save(achievement)).thenReturn(achievement);

        Achievement result = service.update(id, updated);

        assertThat(result.getName()).isEqualTo("Super Reader");
        assertThat(result.getPoints()).isEqualTo(100);
        assertThat(result.getTargetCount()).isEqualTo(20);
        assertThat(result.getBadgeIcon()).isEqualTo("star.svg");
        verify(achievementRepository).save(achievement);
    }

    @Test
    void update_throwsWhenNotFound() {
        UUID missing = UUID.randomUUID();
        when(achievementRepository.findById(missing)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(missing, achievement))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void delete_removesExisting() {
        when(achievementRepository.findById(id)).thenReturn(Optional.of(achievement));
        service.delete(id);
        verify(achievementRepository).delete(achievement);
    }

    @Test
    void delete_throwsWhenNotFound() {
        UUID missing = UUID.randomUUID();
        when(achievementRepository.findById(missing)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(missing))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
