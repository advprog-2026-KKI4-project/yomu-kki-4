package id.ac.ui.cs.advprog.yomu.discussion.repository;

import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DiscussionForumRepositoryTest {

    @Autowired
    private DiscussionForumRepository repository;

    @Test
    void findByMaterialId_returnsMatchingComments() {
        repository.save(DiscussionForum.builder().content("first").materialId("mat-1").authorId(1L).build());
        repository.save(DiscussionForum.builder().content("second").materialId("mat-1").authorId(2L).build());
        repository.save(DiscussionForum.builder().content("other mat").materialId("mat-2").authorId(1L).build());

        List<DiscussionForum> result = repository.findByMaterialIdOrderByCreatedAtAsc("mat-1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DiscussionForum::getContent).containsExactly("first", "second");
    }

    @Test
    void findByMaterialId_unknownMaterial_returnsEmpty() {
        assertThat(repository.findByMaterialIdOrderByCreatedAtAsc("no-such-mat")).isEmpty();
    }

    @Test
    void findAllByOrderByCreatedAtDesc_returnsAll() {
        repository.save(DiscussionForum.builder().content("alpha").materialId("mat-1").authorId(1L).build());
        repository.save(DiscussionForum.builder().content("beta").materialId("mat-2").authorId(2L).build());
        assertThat(repository.findAllByOrderByCreatedAtDesc().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void save_comment() {
        DiscussionForum c = repository.save(DiscussionForum.builder().content("hello").materialId("mat-1").authorId(99L).build());
        assertThat(c.getId()).isNotNull();
        assertThat(repository.findById(c.getId())).isPresent();
    }

    @Test
    void delete_removesComment() {
        DiscussionForum c = repository.save(DiscussionForum.builder().content("bye").materialId("mat-1").authorId(1L).build());
        Long id = c.getId();
        repository.delete(c);
        assertThat(repository.findById(id)).isEmpty();
    }
}
