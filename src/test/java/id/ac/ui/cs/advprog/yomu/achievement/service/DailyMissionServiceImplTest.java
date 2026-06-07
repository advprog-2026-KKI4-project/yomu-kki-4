package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.repository.DailyMissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyMissionServiceImplTest {

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @InjectMocks
    private DailyMissionServiceImpl service;

    private DailyMission mission;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        mission = DailyMission.builder()
                .id(id)
                .name("Daily Reader")
                .description("Read 1 article today")
                .type(MissionType.READING)
                .targetCount(1)
                .rewardPoints(10)
                .active(true)
                .build();
    }

    @Test
    void create_savesAndReturns() {
        when(dailyMissionRepository.save(mission)).thenReturn(mission);
        assertThat(service.create(mission)).isEqualTo(mission);
        verify(dailyMissionRepository).save(mission);
    }

    @Test
    void findAll_delegatesToRepository() {
        when(dailyMissionRepository.findAll()).thenReturn(List.of(mission));
        assertThat(service.findAll()).containsExactly(mission);
    }

    @Test
    void findById_returnsWhenFound() {
        when(dailyMissionRepository.findById(id)).thenReturn(Optional.of(mission));
        assertThat(service.findById(id)).isEqualTo(mission);
    }

    @Test
    void findById_throwsWhenNotFound() {
        UUID missing = UUID.randomUUID();
        when(dailyMissionRepository.findById(missing)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(missing))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Daily Mission not found");
    }

    @Test
    void findByType_delegatesToRepository() {
        when(dailyMissionRepository.findByType(MissionType.READING)).thenReturn(List.of(mission));
        assertThat(service.findByType(MissionType.READING)).containsExactly(mission);
    }

    @Test
    void findActiveMissions_passesCurrentTimeToRepository() {
        when(dailyMissionRepository.findCurrentlyActive(any(LocalDateTime.class))).thenReturn(List.of(mission));

        List<DailyMission> result = service.findActiveMissions();

        assertThat(result).containsExactly(mission);
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(dailyMissionRepository).findCurrentlyActive(captor.capture());
        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    void update_updatesAllFieldsAndSaves() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 12, 0);
        LocalDateTime to = from.plusDays(1);
        DailyMission updated = DailyMission.builder()
                .name("Daily Quiz Master").description("Complete 3 quizzes today")
                .type(MissionType.QUIZ).targetCount(3).rewardPoints(30)
                .active(false).activeFrom(from).activeTo(to)
                .build();
        when(dailyMissionRepository.findById(id)).thenReturn(Optional.of(mission));
        when(dailyMissionRepository.save(mission)).thenReturn(mission);

        service.update(id, updated);

        assertThat(mission.getName()).isEqualTo("Daily Quiz Master");
        assertThat(mission.getType()).isEqualTo(MissionType.QUIZ);
        assertThat(mission.getRewardPoints()).isEqualTo(30);
        assertThat(mission.isActive()).isFalse();
        assertThat(mission.getActiveFrom()).isEqualTo(from);
        assertThat(mission.getActiveTo()).isEqualTo(to);
        verify(dailyMissionRepository).save(mission);
    }

    @Test
    void update_throwsWhenNotFound() {
        UUID missing = UUID.randomUUID();
        when(dailyMissionRepository.findById(missing)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(missing, mission))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void delete_removesExisting() {
        when(dailyMissionRepository.findById(id)).thenReturn(Optional.of(mission));
        service.delete(id);
        verify(dailyMissionRepository).delete(mission);
    }

    @Test
    void delete_throwsWhenNotFound() {
        UUID missing = UUID.randomUUID();
        when(dailyMissionRepository.findById(missing)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(missing))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
