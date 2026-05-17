package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.Question;
import id.ac.ui.cs.advprog.yomu.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.ui.ConcurrentModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingPageControllerTest {

    private ReadingPageController controller;
    private ReadingMaterialService service;
    private QuizAttemptRepository attemptRepo;

    @BeforeEach
    void setUp() {
        service = mock(ReadingMaterialService.class);
        attemptRepo = mock(QuizAttemptRepository.class);
        controller = new ReadingPageController(service, attemptRepo);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "student@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- dashboard() ---

    @Test
    void testDashboardReturnsCorrectView() {
        when(service.getAll()).thenReturn(Collections.emptyList());

        String view = controller.dashboard(new ConcurrentModel());

        assertEquals("reading/dashboard", view);
    }

    @Test
    void testDashboardPopulatesModelAttributes() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setTitle("Test Material");
        when(service.getAll()).thenReturn(List.of(mat));

        Model model = new ConcurrentModel();
        controller.dashboard(model);

        assertEquals("/reading", model.getAttribute("currentUri"));
        assertEquals("student@test.com", model.getAttribute("username"));
        assertEquals("STUDENT", model.getAttribute("role"));
        assertNotNull(model.getAttribute("materials"));
        assertEquals(1, ((List<?>) model.getAttribute("materials")).size());
    }

    // --- myLearning() ---

    @Test
    void testMyLearningReturnsCorrectView() {
        when(service.getAll()).thenReturn(Collections.emptyList());

        String view = controller.myLearning(new ConcurrentModel());

        assertEquals("reading/dashboard", view);
    }

    @Test
    void testMyLearningFiltersInProgressOnly() {
        ReadingMaterial inProgress = new ReadingMaterial();
        inProgress.setTitle("In Progress");
        inProgress.setProgress(50);

        ReadingMaterial notStarted = new ReadingMaterial();
        notStarted.setTitle("Not Started");
        notStarted.setProgress(0);

        ReadingMaterial completed = new ReadingMaterial();
        completed.setTitle("Completed");
        completed.setProgress(100);

        when(service.getAll()).thenReturn(List.of(inProgress, notStarted, completed));

        Model model = new ConcurrentModel();
        controller.myLearning(model);

        assertEquals("/my-learning", model.getAttribute("currentUri"));
        List<?> materials = (List<?>) model.getAttribute("materials");
        assertEquals(2, materials.size()); // inProgress + completed, not notStarted
    }

    // --- readingPage() ---

    @Test
    void testReadingPageReturnsMaterial() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setTitle("Test");
        when(service.getById("test-id")).thenReturn(mat);

        Model model = new ConcurrentModel();
        String view = controller.readingPage("test-id", model);

        assertEquals("reading/reader", view);
        assertEquals(mat, model.getAttribute("material"));
        assertEquals(false, model.getAttribute("isReview"));
        assertEquals("STUDENT", model.getAttribute("role"));
    }

    @Test
    void testReadingPageNullMaterialStillRenders() {
        when(service.getById("nonexistent")).thenReturn(null);

        Model model = new ConcurrentModel();
        String view = controller.readingPage("nonexistent", model);

        assertEquals("reading/reader", view);
        assertNull(model.getAttribute("material"));
        assertEquals(false, model.getAttribute("isReview"));
    }

    // --- quizPage() ---

    @Test
    void testQuizPageReturnsSessionView() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setTitle("Quiz Material");
        when(service.getById("quiz-id")).thenReturn(mat);

        Model model = new ConcurrentModel();
        String view = controller.quizPage("quiz-id", model);

        assertEquals("quiz/session", view);
        assertEquals(mat, model.getAttribute("material"));
        assertEquals("STUDENT", model.getAttribute("role"));
    }

    // --- resultPage() ---

    @Test
    void testResultPageAddsAllParamsToModel() {
        Model model = new ConcurrentModel();
        String view = controller.resultPage(95.5, 120, 90.0, 5.5, 60, model);

        assertEquals("quiz/result", view);
        assertEquals(95.5, model.getAttribute("score"));
        assertEquals(120L, model.getAttribute("duration"));
        assertEquals(90.0, model.getAttribute("baseScore"));
        assertEquals(5.5, model.getAttribute("bonus"));
        assertEquals(60L, model.getAttribute("remaining"));
        assertEquals("STUDENT", model.getAttribute("role"));
    }

    // --- reviewPage() ---

    @Test
    void testReviewPageFindsAttemptForCurrentUser() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setTitle("Review Material");
        when(service.getById("rev-id")).thenReturn(mat);

        QuizAttempt attempt = new QuizAttempt("student@test.com", "rev-id", 85.0, 90, List.of(0, 1));
        when(attemptRepo.findByUserIdAndMaterialId("student@test.com", "rev-id"))
                .thenReturn(attempt);

        Model model = new ConcurrentModel();
        String view = controller.reviewPage("rev-id", model);

        assertEquals("reading/reader", view);
        assertEquals(mat, model.getAttribute("material"));
        assertEquals(attempt, model.getAttribute("attempt"));
        assertEquals(85.0, model.getAttribute("score"));
        assertEquals(true, model.getAttribute("isReview"));
        verify(attemptRepo).findByUserIdAndMaterialId("student@test.com", "rev-id");
    }

    @Test
    void testReviewPageHandlesNullAttempt() {
        ReadingMaterial mat = new ReadingMaterial();
        when(service.getById("rev-id")).thenReturn(mat);
        when(attemptRepo.findByUserIdAndMaterialId(anyString(), anyString()))
                .thenReturn(null);

        Model model = new ConcurrentModel();
        String view = controller.reviewPage("rev-id", model);

        assertEquals("reading/reader", view);
        assertEquals(0, model.getAttribute("score"));
        assertNull(model.getAttribute("attempt"));
        assertEquals(true, model.getAttribute("isReview"));
    }

    // --- SecurityContext edge cases ---

    @Test
    void testGetCurrentUserIdReturnsAnonymousWhenNoAuth() {
        SecurityContextHolder.clearContext();

        // Call dashboard which uses getCurrentUserId()
        when(service.getAll()).thenReturn(Collections.emptyList());
        Model model = new ConcurrentModel();
        controller.dashboard(model);

        assertEquals("anonymous", model.getAttribute("username"));
    }

    @Test
    void testGetCurrentUserRoleReturnsStudentWhenNoAuth() {
        SecurityContextHolder.clearContext();

        when(service.getAll()).thenReturn(Collections.emptyList());
        Model model = new ConcurrentModel();
        controller.dashboard(model);

        assertEquals("STUDENT", model.getAttribute("role"));
    }
}
