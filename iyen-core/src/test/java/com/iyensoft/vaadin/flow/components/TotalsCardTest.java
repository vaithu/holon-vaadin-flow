package com.iyensoft.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TotalsCardTest {

    @Test
    void clearRows_addsConfiguredEmptyStateOnlyWhenRequired() {
        TotalsCard card = new TotalsCard();

        assertThat(findNestedChildren(card, Empty.class)).isEmpty();

        card.setEmptyState("No totals available");
        card.clearRows();

        List<Empty> emptyStates = findNestedChildren(card, Empty.class);
        assertThat(emptyStates).hasSize(1);
        assertThat(emptyStates.get(0).getEmptyTitle()).isNotNull();
        assertThat(emptyStates.get(0).getEmptyTitle().getText()).isEqualTo("No totals available");
    }

    @Test
    void addRow_removesPreviouslyRenderedEmptyState() {
        TotalsCard card = new TotalsCard();
        card.setEmptyState("No totals available");
        card.clearRows();

        card.addRow("Revenue", "$100");

        assertThat(findNestedChildren(card, Empty.class)).isEmpty();
    }

    @Test
    void clearRows_withoutConfiguredTitle_doesNotAddEmptyState() {
        TotalsCard card = new TotalsCard();

        card.clearRows();

        assertThat(findNestedChildren(card, Empty.class)).isEmpty();
    }

    private static <T extends Component> List<T> findNestedChildren(Component root, Class<T> type) {
        return root.getChildren()
                .flatMap(child -> java.util.stream.Stream.concat(
                        type.isInstance(child) ? java.util.stream.Stream.of(type.cast(child)) : java.util.stream.Stream.empty(),
                        findNestedChildren(child, type).stream()))
                .toList();
    }
}
