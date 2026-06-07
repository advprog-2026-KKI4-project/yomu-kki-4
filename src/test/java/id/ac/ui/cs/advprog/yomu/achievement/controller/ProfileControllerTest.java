package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanMemberRepository;
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

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
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
class ProfileControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementTrackingService trackingService;

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @InjectMocks
    private ProfileController controller;

    private User me;
    private User other;
    private Authentication authentication;
    private UserAchievementProgress unlockedProgress;

    @BeforeEach
    void setUp() {
        me = User.builder().id(1L).email("alice@test.com").username("alice")
                .firstName("Alice").lastName("Smith").password("pass123").role("STUDENT").build();
        other = User.builder().id(2L).email("bob@test.com").username("bob")
                .firstName("Bob").lastName("Jones").password("pass123").role("STUDENT").build();

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("alice@test.com");
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(me));

        Achievement achievement = Achievement.builder()
                .id(UUID.randomUUID()).name("Bookworm").description("Read 10 articles")
                .type(AchievementType.READING).targetCount(10).points(50).build();
        unlockedProgress = UserAchievementProgress.builder()
                .user(me).achievement(achievement).currentCount(10).unlocked(true).showOnProfile(true).build();
    }

    // ===== myProfile =====

    @Test
    void myProfile_returnsProfileView() {
        when(trackingService.getUnlockedAchievements(me)).thenReturn(List.of(unlockedProgress));
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.myProfile(model, authentication);

        assertThat(view).isEqualTo("achievement/profile");
        assertThat(model.asMap().get("profileUser")).isEqualTo(me);
        assertThat(model.asMap().get("isOwnProfile")).isEqualTo(true);
        assertThat(model.asMap().get("unlockedAchievements")).isEqualTo(List.of(unlockedProgress));
    }

    @Test
    void myProfile_setsDisplayNameFromFirstAndLastName() {
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.myProfile(model, authentication);

        assertThat(model.asMap().get("displayName")).isEqualTo("Alice Smith");
    }

    @Test
    void myProfile_fallsBackToUsernameWhenNoFirstName() {
        me.setFirstName(null);
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.myProfile(model, authentication);

        assertThat(model.asMap().get("displayName")).isEqualTo("alice");
    }

    @Test
    void myProfile_fallsBackToUsernameWhenFirstNameIsBlank() {
        me.setFirstName("   ");
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.myProfile(model, authentication);

        assertThat(model.asMap().get("displayName")).isEqualTo("alice");
    }

    // ===== viewProfile =====

    @Test
    void viewProfile_returnsProfileViewForAnotherUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(other));
        when(trackingService.getPublicAchievements(2L)).thenReturn(List.of(unlockedProgress));
        when(clanMemberRepository.findByStudentId(2L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.viewProfile(2L, model, authentication);

        assertThat(view).isEqualTo("achievement/profile");
        assertThat(model.asMap().get("profileUser")).isEqualTo(other);
        assertThat(model.asMap().get("isOwnProfile")).isEqualTo(false);
    }

    @Test
    void viewProfile_redirectsToMyProfileWhenViewingOwnId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(me));
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.viewProfile(1L, model, authentication);

        assertThat(view).isEqualTo("redirect:/profile");
    }

    @Test
    void viewProfile_throwsWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> controller.viewProfile(99L, new ExtendedModelMap(), authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ===== studentDirectory =====

    @Test
    void studentDirectory_returnsStudentsView() {
        when(userRepository.findAll()).thenReturn(List.of(me, other));

        Model model = new ExtendedModelMap();
        String view = controller.studentDirectory(model, authentication);

        assertThat(view).isEqualTo("achievement/students");
    }

    @Test
    void studentDirectory_excludesCurrentUser() {
        when(userRepository.findAll()).thenReturn(List.of(me, other));

        Model model = new ExtendedModelMap();
        controller.studentDirectory(model, authentication);

        @SuppressWarnings("unchecked")
        List<User> students = (List<User>) model.asMap().get("students");
        assertThat(students).containsExactly(other);
        assertThat(students).doesNotContain(me);
    }

    @Test
    void studentDirectory_excludesAdminUsers() {
        User admin = User.builder().id(3L).email("admin@test.com").username("admin")
                .password("pass123").role("ADMIN").build();
        when(userRepository.findAll()).thenReturn(List.of(me, other, admin));

        Model model = new ExtendedModelMap();
        controller.studentDirectory(model, authentication);

        @SuppressWarnings("unchecked")
        List<User> students = (List<User>) model.asMap().get("students");
        assertThat(students).doesNotContain(admin);
    }

    @Test
    void studentDirectory_returnsEmptyList_whenNoOtherStudents() {
        when(userRepository.findAll()).thenReturn(List.of(me));

        Model model = new ExtendedModelMap();
        controller.studentDirectory(model, authentication);

        @SuppressWarnings("unchecked")
        List<User> students = (List<User>) model.asMap().get("students");
        assertThat(students).isEmpty();
    }

    @Test
    void myProfile_usesFirstNameOnlyWhenLastNameIsNull() {
        me.setLastName(null);
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.myProfile(model, authentication);

        assertThat(model.asMap().get("displayName")).isEqualTo("Alice");
    }

    @Test
    void myProfile_setsClanMembership_whenAcceptedMemberExists() {
        ClanMember member = new ClanMember();
        member.setStatus("ACCEPTED");
        member.setClan(new Clan());
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(List.of(member));

        Model model = new ExtendedModelMap();
        controller.myProfile(model, authentication);

        assertThat(model.asMap().get("clanMembership")).isEqualTo(member);
    }

    @Test
    void myProfile_setsIsAdminTrue_whenUserHasAdminRole() {
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        when(authentication.getAuthorities()).thenAnswer(inv -> List.of(adminRole));
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.myProfile(model, authentication);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(true);
    }

    @Test
    void myProfile_findsUserByPhone_whenEmailNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.of(me));
        when(trackingService.getUnlockedAchievements(me)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.myProfile(model, authentication);

        assertThat(view).isEqualTo("achievement/profile");
    }

    @Test
    void myProfile_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.myProfile(new ExtendedModelMap(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void viewProfile_setsIsAdminTrue_whenUserHasAdminRole() {
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        when(authentication.getAuthorities()).thenAnswer(inv -> List.of(adminRole));
        when(userRepository.findById(2L)).thenReturn(Optional.of(other));
        when(trackingService.getPublicAchievements(2L)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(2L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.viewProfile(2L, model, authentication);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(true);
    }

    @Test
    void viewProfile_setsClanMembership_whenAcceptedMemberExists() {
        ClanMember member = new ClanMember();
        member.setStatus("ACCEPTED");
        member.setClan(new Clan());
        when(userRepository.findById(2L)).thenReturn(Optional.of(other));
        when(trackingService.getPublicAchievements(2L)).thenReturn(Collections.emptyList());
        when(clanMemberRepository.findByStudentId(2L)).thenReturn(List.of(member));

        Model model = new ExtendedModelMap();
        controller.viewProfile(2L, model, authentication);

        assertThat(model.asMap().get("clanMembership")).isEqualTo(member);
    }
}
