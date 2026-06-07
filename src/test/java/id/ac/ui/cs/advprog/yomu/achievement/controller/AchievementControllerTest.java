package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AchievementControllerTest {

    @Mock
    private AchievementService achievementService;

    @Mock
    private AchievementTrackingService achievementTrackingService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AchievementController controller;

    private User user;
    private Achievement achievement;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("alice@test.com").username("alice").password("pass123").build();
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("alice@test.com");
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        achievement = Achievement.builder()
                .id(UUID.randomUUID()).name("Bookworm").description("Read 10 articles")
                .type(AchievementType.READING).targetCount(10).points(50).build();
    }

    @Test
    void myProgress_returnsMyProgressView() {
        when(achievementTrackingService.getUserAchievements(user)).thenReturn(Collections.emptyList());
        when(achievementService.findAll()).thenReturn(List.of(achievement));

        Model model = new ExtendedModelMap();
        String view = controller.myProgress(model, authentication);

        assertThat(view).isEqualTo("achievement/myProgress");
        assertThat(model.asMap().get("achievements")).isNotNull();
        assertThat(model.asMap().get("progressMap")).isNotNull();
    }

    @Test
    void myProgress_sortsUnlockedAchievementsLast() {
        Achievement lockedAchievement = Achievement.builder()
                .id(UUID.randomUUID()).name("Quiz Novice").description("Complete 3 quizzes")
                .type(AchievementType.QUIZ).targetCount(3).points(30).build();
        UserAchievementProgress unlockedProgress = UserAchievementProgress.builder()
                .user(user).achievement(achievement).currentCount(10).unlocked(true).build();

        when(achievementTrackingService.getUserAchievements(user)).thenReturn(List.of(unlockedProgress));
        when(achievementService.findAll()).thenReturn(List.of(achievement, lockedAchievement));

        Model model = new ExtendedModelMap();
        controller.myProgress(model, authentication);

        @SuppressWarnings("unchecked")
        List<Achievement> sorted = (List<Achievement>) model.asMap().get("achievements");
        assertThat(sorted.get(0)).isEqualTo(lockedAchievement);
        assertThat(sorted.get(1)).isEqualTo(achievement);
    }

    @Test
    void listAchievements_returnsAchievementListView() {
        when(achievementService.findAll()).thenReturn(List.of(achievement));

        Model model = new ExtendedModelMap();
        String view = controller.listAchievements(model, authentication);

        assertThat(view).isEqualTo("achievement/achievementList");
        assertThat(model.asMap().get("achievements")).isEqualTo(List.of(achievement));
    }

    @Test
    void listAchievements_setsIsAdminFalseForStudent() {
        when(achievementService.findAll()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.listAchievements(model, authentication);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(false);
    }

    @Test
    void showCreateForm_returnsCreateView() {
        Model model = new ExtendedModelMap();
        String view = controller.showCreateForm(model);

        assertThat(view).isEqualTo("achievement/achievementCreate");
        assertThat(model.asMap().get("achievement")).isInstanceOf(Achievement.class);
        assertThat(model.asMap().get("types")).isEqualTo(AchievementType.values());
    }

    @Test
    void createAchievement_callsServiceAndRedirects() {
        String view = controller.createAchievement(achievement);

        verify(achievementService).create(achievement);
        assertThat(view).isEqualTo("redirect:/achievements/progress");
    }

    @Test
    void showEditForm_returnsEditViewWithAchievement() {
        UUID id = achievement.getId();
        when(achievementService.findById(id)).thenReturn(achievement);

        Model model = new ExtendedModelMap();
        String view = controller.showEditForm(id, model);

        assertThat(view).isEqualTo("achievement/achievementEdit");
        assertThat(model.asMap().get("achievement")).isEqualTo(achievement);
        assertThat(model.asMap().get("types")).isEqualTo(AchievementType.values());
    }

    @Test
    void updateAchievement_callsServiceAndRedirects() {
        UUID id = achievement.getId();
        String view = controller.updateAchievement(id, achievement);

        verify(achievementService).update(id, achievement);
        assertThat(view).isEqualTo("redirect:/achievements/progress");
    }

    @Test
    void deleteAchievement_callsServiceAndRedirects() {
        UUID id = achievement.getId();
        String view = controller.deleteAchievement(id);

        verify(achievementService).delete(id);
        assertThat(view).isEqualTo("redirect:/achievements/progress");
    }

    @Test
    void listAchievements_setsIsAdminTrue_whenUserHasAdminRole() {
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        when(authentication.getAuthorities()).thenAnswer(inv -> List.of(adminRole));
        when(achievementService.findAll()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.listAchievements(model, authentication);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(true);
    }

    @Test
    void listAchievements_setsIsAdminFalse_whenAuthenticationIsNull() {
        when(achievementService.findAll()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.listAchievements(model, null);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(false);
    }

    @Test
    void myProgress_setsIsAdminTrue_whenUserHasAdminRole() {
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        when(authentication.getAuthorities()).thenAnswer(inv -> List.of(adminRole));
        when(achievementTrackingService.getUserAchievements(user)).thenReturn(Collections.emptyList());
        when(achievementService.findAll()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.myProgress(model, authentication);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(true);
    }

    @Test
    void myProgress_findsUserByPhone_whenEmailNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.of(user));
        when(achievementTrackingService.getUserAchievements(user)).thenReturn(Collections.emptyList());
        when(achievementService.findAll()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.myProgress(model, authentication);

        assertThat(view).isEqualTo("achievement/myProgress");
    }

    @Test
    void myProgress_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.myProgress(new ExtendedModelMap(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void myProgress_comparator_aInMapNotUnlocked_bNotInMap() {
        // findAll [notInMap, inMapLocked] → sort calls compare(inMapLocked, notInMap)
        // covers: aUnlocked(containsKey=true, isUnlocked=false), bUnlocked(containsKey=false)
        Achievement inMapLocked = Achievement.builder()
                .id(UUID.randomUUID()).name("Half Done").description("In progress")
                .type(AchievementType.READING).targetCount(10).points(30).build();
        Achievement notInMap = Achievement.builder()
                .id(UUID.randomUUID()).name("Not Started").description("Untouched")
                .type(AchievementType.QUIZ).targetCount(5).points(20).build();
        UserAchievementProgress notUnlocked = UserAchievementProgress.builder()
                .user(user).achievement(inMapLocked).currentCount(5).unlocked(false).build();

        when(achievementTrackingService.getUserAchievements(user)).thenReturn(List.of(notUnlocked));
        when(achievementService.findAll()).thenReturn(List.of(notInMap, inMapLocked));

        Model model = new ExtendedModelMap();
        controller.myProgress(model, authentication);

        @SuppressWarnings("unchecked")
        List<Achievement> sorted = (List<Achievement>) model.asMap().get("achievements");
        assertThat(sorted).containsExactlyInAnyOrder(inMapLocked, notInMap);
    }

    @Test
    void myProgress_comparator_aUnlocked_bInMapNotUnlocked() {
        // findAll [inMapLocked, unlocked] → sort calls compare(unlocked, inMapLocked)
        // covers: aUnlocked(containsKey=true, isUnlocked=true), bUnlocked(containsKey=true, isUnlocked=false)
        Achievement inMapLocked = Achievement.builder()
                .id(UUID.randomUUID()).name("Half Done").description("In progress")
                .type(AchievementType.READING).targetCount(10).points(30).build();
        Achievement unlocked = Achievement.builder()
                .id(UUID.randomUUID()).name("Done").description("Finished")
                .type(AchievementType.QUIZ).targetCount(3).points(50).build();
        UserAchievementProgress lockedProgress = UserAchievementProgress.builder()
                .user(user).achievement(inMapLocked).currentCount(5).unlocked(false).build();
        UserAchievementProgress unlockedProgress = UserAchievementProgress.builder()
                .user(user).achievement(unlocked).currentCount(3).unlocked(true).build();

        when(achievementTrackingService.getUserAchievements(user)).thenReturn(List.of(lockedProgress, unlockedProgress));
        when(achievementService.findAll()).thenReturn(List.of(inMapLocked, unlocked));

        Model model = new ExtendedModelMap();
        controller.myProgress(model, authentication);

        @SuppressWarnings("unchecked")
        List<Achievement> sorted = (List<Achievement>) model.asMap().get("achievements");
        assertThat(sorted.get(sorted.size() - 1)).isEqualTo(unlocked);
    }
}
