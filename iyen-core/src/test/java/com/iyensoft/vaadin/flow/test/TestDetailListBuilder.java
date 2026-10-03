package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.DetailList;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestDetailListBuilder {

    record Customer(String id) {}

    record Order(String number, String status) {}

    private static final Map<String, List<Order>> ORDERS = Map.of(
            "c1", List.of(new Order("SO-1", "Open"), new Order("SO-2", "Shipped")),
            "c2", List.of(new Order("SO-3", "Open")));

    private static DetailList<Customer, Order> ordersList() {
        return Components.detailList("Orders", Order.class, (Customer c) -> ORDERS.get(c.id()))
                .column(Order::number, "Order")
                .column(Order::status, "Status")
                .build();
    }

    @Test
    void build_createsCompactDetailPanelWithTypedColumns() {
        Button action = new Button("New");
        DetailList<Customer, Order> list = Components
                .detailList("Orders", Order.class, (Customer c) -> ORDERS.get(c.id()))
                .column(Order::number, "Order")
                .actions(action)
                .build();

        Component header = list.getChildren().findFirst().orElseThrow();
        Grid<Order> grid = list.getListing().getGrid();

        assertTrue(header.getElement().getClassList().contains("panel-header"));
        assertSame(action, header.getChildren().toList().get(1));
        assertEquals(1, grid.getColumns().size());
        assertEquals("Order", grid.getColumns().get(0).getHeaderText());
    }

    @Test
    void selection_reloadsRowsForEachItem() {
        DetailList<Customer, Order> list = ordersList();

        list.onItemSelected(new Customer("c1"));
        assertEquals(2, list.getListing().getGrid().getListDataView().getItemCount());

        list.onItemSelected(new Customer("c2"));
        assertEquals(1, list.getListing().getGrid().getListDataView().getItemCount());
    }

    @Test
    void selection_withoutRows_showsEmptyListing() {
        DetailList<Customer, Order> list = ordersList();

        list.onItemSelected(new Customer("unknown"));

        assertEquals(0, list.getListing().getGrid().getListDataView().getItemCount());
    }

    @Test
    void lazyTab_receivesSelectionMadeBeforeItWasOpened() {
        LazyTabsBuilder tabs = LazyTabsBuilder.create()
                .withEagerTab("Overview", new Span("Overview"))
                .withLazyTab("Orders", (Customer c) -> ORDERS.get(c.id()).size(),
                        TestDetailListBuilder::ordersList);
        @SuppressWarnings("unchecked")
        DetailSyncAware<Object> relay = (DetailSyncAware<Object>) tabs.getContentContainer();

        relay.onItemSelected(new Customer("c1"));
        tabs.getTabs().setSelectedIndex(1);

        DetailList<?, ?> list = (DetailList<?, ?>) tabs.getContentContainer()
                .getChildren().findFirst().orElseThrow();
        assertEquals(2, list.getListing().getGrid().getListDataView().getItemCount());
    }

    @Test
    void create_rejectsMissingArguments() {
        assertThrows(NullPointerException.class,
                () -> Components.detailList(null, Order.class, (Customer c) -> List.of()));
        assertThrows(NullPointerException.class,
                () -> Components.<Customer, Order>detailList("Orders", Order.class, null));
    }
}
