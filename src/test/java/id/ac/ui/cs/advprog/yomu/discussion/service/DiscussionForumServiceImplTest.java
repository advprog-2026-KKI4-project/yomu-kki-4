package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.achievement.event.DiscussionPostEvent;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.model.CommentReaction;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import id.ac.ui.cs.advprog.yomu.discussion.repository.CommentReactionRepository;
import id.ac.ui.cs.advprog.yomu.discussion.repository.DiscussionForumRepository;
import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionForumServiceImplTest {

    @Mock private DiscussionForumRepository commentRepository;
    @Mock private CommentReactionRepository reactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReadingMaterialService readingMaterialService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DiscussionForumServiceImpl service;

    private static final String MATERIAL_ID = "mat-001";
    private static final Long AUTHOR_ID = 10L;
    private static final Long OTHER_USER_ID = 99L;

    private User author;
    private DiscussionForum savedComment;
    private CommentRequest validRequest;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(AUTHOR_ID);
        author.setUsername("alice");

        savedComment = DiscussionForum.builder()
                .id(1L)
                .content("Hello world")
                .materialId(MATERIAL_ID)
                .authorId(AUTHOR_ID)
                .parentCommentId(null)
                .createdAt(LocalDateTime.now())
                .build();

        validRequest = new CommentRequest("Hello world", MATERIAL_ID, null);
    }

    @Test
    void postComment_validRequest_savesAndReturnsResponse() {
        when(readingMaterialService.getById(MATERIAL_ID)).thenReturn(mock(ReadingMaterial.class));
        when(commentRepository.save(any())).thenReturn(savedComment);
        when(userRepository.findAllById(List.of(AUTHOR_ID))).thenReturn(List.of(author));

        CommentResponse response = service.postComment(validRequest, AUTHOR_ID);

        assertThat(response.getContent()).isEqualTo("Hello world");
        assertThat(response.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(response.getAuthorId()).isEqualTo(AUTHOR_ID);
        assertThat(response.getAuthorUsername()).isEqualTo("alice");
        assertThat(response.isOwnedByCurrentUser()).isTrue();
        verify(commentRepository).save(any(DiscussionForum.class));
    }

    @Test
    void postComment_publishesDiscussionPostEvent_withAuthorId() {
        when(readingMaterialService.getById(MATERIAL_ID)).thenReturn(mock(ReadingMaterial.class));
        when(commentRepository.save(any())).thenReturn(savedComment);
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        service.postComment(validRequest, AUTHOR_ID);

        ArgumentCaptor<DiscussionPostEvent> captor = ArgumentCaptor.forClass(DiscussionPostEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(AUTHOR_ID);
    }

    @Test
    void postComment_materialNotFound_throwsIllegalArgument() {
        when(readingMaterialService.getById(MATERIAL_ID)).thenReturn(null);

        assertThatThrownBy(() -> service.postComment(validRequest, AUTHOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MATERIAL_ID);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void postComment_parentCommentNotFound_throwsIllegalArgument() {
        CommentRequest replyReq = new CommentRequest("reply", MATERIAL_ID, 999L);
        when(readingMaterialService.getById(MATERIAL_ID)).thenReturn(mock(ReadingMaterial.class));
        when(commentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.postComment(replyReq, AUTHOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void postComment_withValidParent_savesReply() {
        CommentRequest replyReq = new CommentRequest("I agree!", MATERIAL_ID, 1L);
        DiscussionForum replyForum = DiscussionForum.builder()
                .id(2L).content("I agree!").materialId(MATERIAL_ID)
                .authorId(AUTHOR_ID).parentCommentId(1L)
                .createdAt(LocalDateTime.now()).build();

        when(readingMaterialService.getById(MATERIAL_ID)).thenReturn(mock(ReadingMaterial.class));
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(replyForum);
        when(userRepository.findAllById(List.of(AUTHOR_ID))).thenReturn(List.of(author));

        CommentResponse response = service.postComment(replyReq, AUTHOR_ID);

        assertThat(response.getParentCommentId()).isEqualTo(1L);
    }

    @Test
    void getCommentsByMaterial_noComments_returnsEmptyList() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(Collections.emptyList());

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, AUTHOR_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void getCommentsByMaterial_withComments_returnsResponses() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(List.of(1L)))
                .thenReturn(Collections.emptyList());
        when(userRepository.findAllById(List.of(AUTHOR_ID))).thenReturn(List.of(author));

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, AUTHOR_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hello world");
    }

    @Test
    void getCommentsByMaterial_setsOwnedByCurrentUserTrue_forAuthor() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(any())).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, AUTHOR_ID);

        assertThat(result.get(0).isOwnedByCurrentUser()).isTrue();
    }

    @Test
    void getCommentsByMaterial_setsOwnedByCurrentUserFalse_forOtherUser() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(any())).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, OTHER_USER_ID);

        assertThat(result.get(0).isOwnedByCurrentUser()).isFalse();
    }

    @Test
    void getCommentsByMaterial_nullCurrentUser_ownedByCurrentUserFalse() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(any())).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, null);

        assertThat(result.get(0).isOwnedByCurrentUser()).isFalse();
    }


    @Test
    void getAllComments_returnsAllSortedByDesc() {
        when(commentRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(any())).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        List<CommentResponse> result = service.getAllComments(AUTHOR_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void editComment_authorEditsOwn_updatesContent() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(commentRepository.save(savedComment)).thenReturn(savedComment);
        when(reactionRepository.findByCommentId(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        CommentResponse response = service.editComment(1L, "Updated content", AUTHOR_ID);

        assertThat(response.getContent()).isEqualTo("Updated content");
        verify(commentRepository).save(savedComment);
    }

    @Test
    void editComment_nonAuthor_throwsIllegalArgument() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));

        assertThatThrownBy(() -> service.editComment(1L, "Hack", OTHER_USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You can only edit");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void editComment_commentNotFound_throwsIllegalArgument() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.editComment(999L, "text", AUTHOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    void deleteComment_authorDeletesOwn_deletesSuccessfully() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));

        service.deleteComment(1L, AUTHOR_ID);

        verify(reactionRepository).deleteByCommentId(1L);
        verify(commentRepository).delete(savedComment);
    }

    @Test
    void deleteComment_nonAuthor_throwsIllegalArgument() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));

        assertThatThrownBy(() -> service.deleteComment(1L, OTHER_USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You can only delete");

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_commentNotFound_throwsIllegalArgument() {
        when(commentRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteComment(42L, AUTHOR_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteCommentAsAdmin_existingComment_deletesSuccessfully() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));

        service.deleteCommentAsAdmin(1L);

        verify(reactionRepository).deleteByCommentId(1L);
        verify(commentRepository).delete(savedComment);
    }

    @Test
    void deleteCommentAsAdmin_notFound_throwsIllegalArgument() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCommentAsAdmin(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }


    @Test
    void reactToComment_newReaction_savesReaction() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(reactionRepository.findByCommentIdAndUserId(1L, AUTHOR_ID)).thenReturn(Optional.empty());
        when(reactionRepository.findByCommentId(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        service.reactToComment(1L, ReactionType.LIKE, AUTHOR_ID);

        ArgumentCaptor<CommentReaction> captor = ArgumentCaptor.forClass(CommentReaction.class);
        verify(reactionRepository).save(captor.capture());
        assertThat(captor.getValue().getReactionType()).isEqualTo(ReactionType.LIKE);
        assertThat(captor.getValue().getUserId()).isEqualTo(AUTHOR_ID);
    }

    @Test
    void reactToComment_sameReactionExists_deletesReaction() {
        CommentReaction existing = CommentReaction.builder()
                .id(5L).commentId(1L).userId(AUTHOR_ID).reactionType(ReactionType.LIKE).build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(reactionRepository.findByCommentIdAndUserId(1L, AUTHOR_ID)).thenReturn(Optional.of(existing));
        when(reactionRepository.findByCommentId(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        service.reactToComment(1L, ReactionType.LIKE, AUTHOR_ID);

        verify(reactionRepository).delete(existing);
        verify(reactionRepository, never()).save(any());
    }

    @Test
    void reactToComment_differentReactionExists_updatesReaction() {
        CommentReaction existing = CommentReaction.builder()
                .id(5L).commentId(1L).userId(AUTHOR_ID).reactionType(ReactionType.LIKE).build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(reactionRepository.findByCommentIdAndUserId(1L, AUTHOR_ID)).thenReturn(Optional.of(existing));
        when(reactionRepository.findByCommentId(1L)).thenReturn(List.of(existing));
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        service.reactToComment(1L, ReactionType.ANGRY, AUTHOR_ID);

        assertThat(existing.getReactionType()).isEqualTo(ReactionType.ANGRY);
        verify(reactionRepository).save(existing);
        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void reactToComment_commentNotFound_throwsIllegalArgument() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reactToComment(1L, ReactionType.UPVOTE, AUTHOR_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reactToComment_responseHasCorrectCurrentUserReaction() {
        CommentReaction reaction = CommentReaction.builder()
                .id(7L).commentId(1L).userId(AUTHOR_ID).reactionType(ReactionType.WOW).build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(reactionRepository.findByCommentIdAndUserId(1L, AUTHOR_ID)).thenReturn(Optional.empty());
        when(reactionRepository.findByCommentId(1L)).thenReturn(List.of(reaction));
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        CommentResponse response = service.reactToComment(1L, ReactionType.WOW, AUTHOR_ID);

        assertThat(response.getCurrentUserReaction()).isEqualTo(ReactionType.WOW);
        assertThat(response.getReactionCounts().get(ReactionType.WOW)).isEqualTo(1L);
    }


    @Test
    void removeReaction_callsRepositoryDelete() {
        service.removeReaction(1L, AUTHOR_ID);
        verify(reactionRepository).deleteByCommentIdAndUserId(1L, AUTHOR_ID);
    }


    @Test
    void getCommentCountsByMaterial_groupsCorrectly() {
        DiscussionForum c1 = DiscussionForum.builder().id(1L).materialId("mat-1").authorId(1L).content("a").build();
        DiscussionForum c2 = DiscussionForum.builder().id(2L).materialId("mat-1").authorId(1L).content("b").build();
        DiscussionForum c3 = DiscussionForum.builder().id(3L).materialId("mat-2").authorId(1L).content("c").build();

        when(commentRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        Map<String, Long> counts = service.getCommentCountsByMaterial();

        assertThat(counts).containsEntry("mat-1", 2L);
        assertThat(counts).containsEntry("mat-2", 1L);
    }

    @Test
    void getCommentCountsByMaterial_noComments_returnsEmptyMap() {
        when(commentRepository.findAll()).thenReturn(Collections.emptyList());

        Map<String, Long> counts = service.getCommentCountsByMaterial();

        assertThat(counts).isEmpty();
    }


    @Test
    void getCommentsByMaterial_reactionCountsInitializedToZeroForAllTypes() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(any())).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(List.of(author));

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, AUTHOR_ID);

        Map<ReactionType, Long> counts = result.get(0).getReactionCounts();
        for (ReactionType type : ReactionType.values()) {
            assertThat(counts).containsEntry(type, 0L);
        }
    }

    @Test
    void getCommentsByMaterial_unknownAuthor_fallsBackToUnknownUser() {
        when(commentRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID))
                .thenReturn(List.of(savedComment));
        when(reactionRepository.findByCommentIdIn(any())).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any())).thenReturn(Collections.emptyList());

        List<CommentResponse> result = service.getCommentsByMaterial(MATERIAL_ID, AUTHOR_ID);

        assertThat(result.get(0).getAuthorUsername()).isEqualTo("Unknown user");
    }
}
