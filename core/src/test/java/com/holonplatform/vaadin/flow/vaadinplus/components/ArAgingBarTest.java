package com.iyensoft.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArAgingBarTest {

    @Test
    void setSegments_empty_addsConfiguredEmptyStateOnlyWhenRequired() {
        ArAgingBar bar = new ArAgingBar(ArAgingBar.Variant.DEFAULT);
        assertThat(findNestedChildren(bar, Empty.class)).isEmpty();

        bar.setEmptyState("No aging data available");

        bar.setSegments(List.of());

        List<Empty> emptyStates = findNestedChildren(bar, Empty.class);
        assertThat(emptyStates).hasSize(1);
        assertThat(emptyStates.get(0).getEmptyTitle()).isNotNull();
        assertThat(emptyStates.get(0).getEmptyTitle().getText()).isEqualTo("No aging data available");
    }

    @Test
    void setSegments_nonEmpty_removesPreviouslyAddedEmptyState() {
        ArAgingBar bar = new ArAgingBar(ArAgingBar.Variant.DEFAULT);
        bar.setEmptyState("No aging data available");
        bar.setSegments(List.of());

        bar.setSegments(List.of(new ArAgingBar.Segment("Current", "Current - 10", 100, ArAgingBar.Variant.SUCCESS)));

        assertThat(findNestedChildren(bar, Empty.class)).isEmpty();
    }

    @Test
    void setSegments_emptyWithoutConfiguredTitle_doesNotAddEmptyState() {
        ArAgingBar bar = new ArAgingBar(ArAgingBar.Variant.DEFAULT);

        bar.setSegments(List.of());

        assertThat(findNestedChildren(bar, Empty.class)).isEmpty();
    }

    private static <T extends Component> List<T> findNestedChildren(Component root, Class<T> type) {
        return root.getChildren()
                .flatMap(child -> java.util.stream.Stream.concat(
                        type.isInstance(child) ? java.util.stream.Stream.of(type.cast(child)) : java.util.stream.Stream.empty(),
                        findNestedChildren(child, type).stream()))
                .toList();
    }
}
