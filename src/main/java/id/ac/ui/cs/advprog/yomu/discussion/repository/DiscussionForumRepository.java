package id.ac.ui.cs.advprog.yomu.discussion.repository;

import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class DiscussionForumRepository {

    private final Map<Long, DiscussionForum> store = new LinkedHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public DiscussionForum save(DiscussionForum comment) {
        if (comment.getId() == null) {
            comment.setId(idCounter.getAndIncrement());
        }
        store.put(comment.getId(), comment);
        return comment;
    }

    public Optional<DiscussionForum> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public void delete(DiscussionForum comment) {
        store.remove(comment.getId());
    }

    public List<DiscussionForum> findByMaterialIdOrderByCreatedAtAsc(String materialId) {
        return store.values().stream()
                .filter(c -> materialId.equals(c.getMaterialId()))
                .sorted(Comparator.comparing(DiscussionForum::getCreatedAt))
                .collect(Collectors.toList());
    }
}