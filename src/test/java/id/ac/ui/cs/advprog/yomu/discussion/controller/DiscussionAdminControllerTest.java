package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionAdminControllerTest {

    @Mock private ReadingMaterialService readingMaterialService;
    @Mock private Model model;

    @InjectMocks
    private DiscussionAdminController controller;

    @Test
    void discussionIndex_addsMaterialsToModelAndReturnsView() {
        ReadingMaterial m1 = new ReadingMaterial();
        m1.setTitle("Material A");
        ReadingMaterial m2 = new ReadingMaterial();
        m2.setTitle("Material B");
        when(readingMaterialService.getAll()).thenReturn(List.of(m1, m2));

        String viewName = controller.discussionIndex(model);

        assertThat(viewName).isEqualTo("discussion/discussionAdmin");
        verify(model).addAttribute("materials", List.of(m1, m2));
    }

    @Test
    void discussionIndex_emptyMaterials_addsEmptyListToModel() {
        when(readingMaterialService.getAll()).thenReturn(List.of());

        String viewName = controller.discussionIndex(model);

        assertThat(viewName).isEqualTo("discussion/discussionAdmin");
        verify(model).addAttribute("materials", List.of());
    }
}
