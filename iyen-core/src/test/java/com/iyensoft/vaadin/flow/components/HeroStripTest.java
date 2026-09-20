package com.iyensoft.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HeroStripTest {

    @Test
    void setCells_empty_addsConfiguredEmptyStateOnlyWhenRequired() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);

        assertThat(findNestedChildren(strip, Empty.class)).isEmpty();

        strip.setEmptyState("No hero metrics available");
        strip.setCells(List.of());

        List<Empty> emptyStates = findNestedChildren(strip, Empty.class);
        assertThat(emptyStates).hasSize(1);
        assertThat(emptyStates.get(0).getEmptyTitle()).isNotNull();
        assertThat(emptyStates.get(0).getEmptyTitle().getText()).isEqualTo("No hero metrics available");
    }

    @Test
    void setCells_nonEmpty_removesPreviouslyRenderedEmptyState() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);
        strip.setEmptyState("No hero metrics available");
        strip.setCells(List.of());

        strip.setCells(List.of(new HeroStrip.Cell("Open AR", "10", "1 overdue", false, HeroStrip.ValueVariant.OK)));

        assertThat(findNestedChildren(strip, Empty.class)).isEmpty();
    }

    @Test
    void setCells_emptyWithoutConfiguredTitle_doesNotAddEmptyState() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);

        strip.setCells(List.of());

        assertThat(findNestedChildren(strip, Empty.class)).isEmpty();
    }

    private static <T extends Component> List<T> findNestedChildren(Component root, Class<T> type) {
        return root.getChildren()
                .flatMap(child -> java.util.stream.Stream.concat(
                        type.isInstance(child) ? java.util.stream.Stream.of(type.cast(child)) : java.util.stream.Stream.empty(),
                        findNestedChildren(child, type).stream()))
                .toList();
    }
}
