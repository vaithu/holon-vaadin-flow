package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Unit tests for the {@link com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator}
 * PropertySet path — exercises {@code Components.masterDetail(PropertySet)},
 * {@code master().listing()}, and the {@code detail()} consumer when the item type is
 * {@link PropertyBox}.
 *
 * <p>These tests exercise {@link com.iyensoft.vaadin.flow.internal.components.builders.AbstractPropertyBoxListingAdapter}
 * indirectly through the public builder API, ensuring the refactored adapter delegates correctly.
 */
class TestMasterDetailConfiguratorPropertySet {

    // ── PropertySet fixture ───────────────────────────────────────────────────

    static final PathProperty<Long>   ID       = PathProperty.create("id",       Long.class);
    static final PathProperty<String> NAME     = PathProperty.create("name",     String.class);
    static final PathProperty<Double> PRICE    = PathProperty.create("price",    Double.class);
    static final PropertySet<?>       ITEM_SET = PropertySet.of(ID, NAME, PRICE);

    static PropertyBox item(long id, String name, double price) {
        return PropertyBox.builder(ITEM_SET).set(ID, id).set(NAME, name).set(PRICE, price).build();
    }

    // ── builder basics ────────────────────────────────────────────────────────

    @Test
    void propertySet_builderIsNotNull() {
        assertNotNull(Components.masterDetail(ITEM_SET));
    }

    @Test
    void propertySet_buildReturnsLayout() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET).build();
        assertNotNull(layout);
    }

    @Test
    void propertySet_emptyBuild_layoutHasNoChildren() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET).build();
        assertEquals(0, layout.getChildren().count());
    }

    // ── master() with PropertySet listing ────────────────────────────────────

    @Test
    void propertySet_master_appendsMasterDivToLayout() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .build();

        assertEquals(1, layout.getChildren().count());
    }

    @Test
    void propertySet_master_fetchCallback_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .fetch((q, text, sort) -> Stream.of(item(1, "Widget", 9.99)))))
                .build());
    }

    @Test
    void propertySet_master_filteredFetchCallback_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .fetch((q, text, filter, sort) -> Stream.empty())))
                .build());
    }

    @Test
    void propertySet_master_columnAwareFetchCallback_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .fetch((q, text, filter, sort, cols) -> Stream.empty())))
                .build());
    }

    @Test
    void propertySet_master_searchPlaceholder_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .search("Search items…")
                        .fetch((q, text, sort) -> Stream.empty())))
                .build());
    }

    @Test
    void propertySet_master_columns_doesNotThrow() {
        // gridHeader(String) is unsupported for PropertyListing (pre-existing cast issue in ListingBundle)
        // this test verifies the columns() path instead
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .columns("id", "name", "price")
                        .fetch((q, text, sort) -> Stream.empty())))
                .build());
    }

    @Test
    void propertySet_master_pageSizes_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .pageSizes(10, 25, 50)
                        .defaultPageSize(10)
                        .paginated()
                        .fetch((q, text, sort) -> Stream.empty())))
                .build());
    }

    // ── withFilterPanel — advancedMode is a no-op for PropertyBox adapter ─────

    /**
     * {@code withFilterPanel()} should activate the filter panel without throwing.
     * The fetch callback must be the filtered variant so the bundle's misconfiguration
     * guard does not log a warning.
     */
    @Test
    void propertySet_withFilterPanel_noArg_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .withFilterPanel()
                        .fetch((q, text, filter, sort) -> Stream.empty())))
                .build());
    }

    /**
     * {@code withFilterPanel(true)} must behave identically to {@code withFilterPanel()}.
     * The {@code advancedMode} flag is documented as not supported by
     * {@code PropertyListingBundleBuilder} and is intentionally ignored.
     */
    @Test
    void propertySet_withFilterPanel_trueFlag_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .withFilterPanel(true)
                        .fetch((q, text, filter, sort) -> Stream.empty())))
                .build());
    }

    /** {@code withFilterPanel(false)} must also be accepted without error. */
    @Test
    void propertySet_withFilterPanel_falseFlag_doesNotThrow() {
        assertDoesNotThrow(() -> Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l
                        .withFilterPanel(false)
                        .fetch((q, text, filter, sort) -> Stream.empty())))
                .build());
    }

    // ── detail() sync — PropertyBox item type ─────────────────────────────────

    @Test
    void propertySet_detail_appendsDetailDivToLayout() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .detail(d -> {})
                .build();

        assertEquals(1, layout.getChildren().count());
    }

    @Test
    void propertySet_detail_withDetailSync_firesOnDispatch() {
        List<PropertyBox> received = new ArrayList<>();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .detail(d -> d.withDetailSync((PropertyBox pb) -> received.add(pb)))
                .build();

        PropertyBox pb = item(42, "Gadget", 99.0);
        layout.dispatchSync(pb);

        assertEquals(1, received.size());
        assertSame(pb, received.getFirst());
    }

    @Test
    void propertySet_detail_withDetailSync_firesOnEveryDispatch() {
        List<String> names = new ArrayList<>();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .detail(d -> d.withDetailSync((PropertyBox pb) -> names.add(pb.getValue(NAME))))
                .build();

        layout.dispatchSync(item(1, "Alpha", 1.0));
        layout.dispatchSync(item(2, "Beta",  2.0));

        assertEquals(List.of("Alpha", "Beta"), names);
    }

    @Test
    void propertySet_detail_detailSyncAware_isAutoDiscoveredAndFired() {
        TrackingSync tracker = new TrackingSync();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .detail(d -> d.content(tracker))
                .build();

        layout.dispatchSync(item(7, "Zeta", 7.0));

        assertEquals(List.of("Zeta"), tracker.names);
    }

    @Test
    void propertySet_detail_detailSyncAware_nestedInsideDiv_isScannedAndFired() {
        TrackingSync tracker = new TrackingSync();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .detail(d -> d.content(new Div(tracker)))
                .build();

        layout.dispatchSync(item(8, "Eta", 8.0));

        assertEquals(List.of("Eta"), tracker.names);
    }

    @Test
    void propertySet_topLevel_withDetailSync_fires() {
        List<PropertyBox> received = new ArrayList<>();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .withDetailSync(received::add)
                .build();

        PropertyBox pb = item(99, "Omega", 0.5);
        layout.dispatchSync(pb);

        assertEquals(1, received.size());
        assertSame(pb, received.getFirst());
    }

    // ── master + detail combined ──────────────────────────────────────────────

    @Test
    void propertySet_masterAndDetail_produceTwoChildDivs() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .detail(d -> d.content(new Span("detail")))
                .build();

        assertEquals(2, layout.getChildren().count());
    }

    @Test
    void propertySet_masterAndDetail_syncFires() {
        List<PropertyBox> received = new ArrayList<>();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .detail(d -> d.withDetailSync((PropertyBox pb) -> received.add(pb)))
                .build();

        PropertyBox pb = item(3, "Gamma", 3.0);
        layout.dispatchSync(pb);

        assertEquals(1, received.size());
        assertSame(pb, received.getFirst());
    }

    // ── mobile + Sheet ────────────────────────────────────────────────────────

    @Test
    void propertySet_mobile_withSheet_detailDivNotAddedDirectly() {
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .detail(d -> d.content(new Span("mobile detail")))
                .build();

        // master div (1) + sheet (1) = 2; raw detail Div must NOT be added
        List<com.vaadin.flow.component.Component> children = layout.getChildren().toList();
        assertEquals(2, children.size());
        assertInstanceOf(Sheet.class, children.get(1),
                "Second child should be Sheet, not a raw detail Div");
    }

    @Test
    void propertySet_mobile_withSheet_opensOnDispatch() {
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .detail(d -> {})
                .build();

        assertFalse(sheet.isOpen(), "Sheet should be closed before any dispatch");
        layout.dispatchSync(item(1, "Alpha", 1.0));
        assertTrue(sheet.isOpen(), "Sheet should open after dispatchSync");
    }

    @Test
    void propertySet_mobile_withSheet_syncHandlerFires() {
        List<PropertyBox> received = new ArrayList<>();
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .detail(d -> d.withDetailSync((PropertyBox pb) -> received.add(pb)))
                .build();

        PropertyBox pb = item(5, "Delta", 5.0);
        layout.dispatchSync(pb);

        assertEquals(1, received.size());
        assertSame(pb, received.getFirst());
    }

    @Test
    void propertySet_mobile_withSheet_detailSyncAware_firesOnEachDispatch() {
        TrackingSync tracker = new TrackingSync();
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> m.listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                .detail(d -> d.content(tracker))
                .build();

        layout.dispatchSync(item(1, "First",  1.0));
        layout.dispatchSync(item(2, "Second", 2.0));

        assertEquals(List.of("First", "Second"), tracker.names);
    }

    @Test
    void propertySet_mobile_noSheet_explicitSyncHandlerStillFires() {
        List<PropertyBox> received = new ArrayList<>();
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .viewMode(ViewMode.MOBILE)
                .detail(d -> d.withDetailSync((PropertyBox pb) -> received.add(pb)))
                .build();

        PropertyBox pb = item(10, "Kappa", 10.0);
        layout.dispatchSync(pb);

        assertEquals(List.of(pb), received);
    }

    // ── header / footer configuration ─────────────────────────────────────────

    @Test
    void propertySet_detail_header_isFirstChild() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .detail(d -> d.header(h -> h.heading("Items")))
                .build();

        Div detailDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertNotNull(detailDiv.getChildren().findFirst().orElse(null),
                "Detail header should be attached as the first child");
    }

    @Test
    void propertySet_master_header_attached() {
        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ITEM_SET)
                .master(m -> m.header(h -> h.heading("All Items")))
                .build();

        Div masterDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertTrue(masterDiv.getChildren().findAny().isPresent(),
                "Master header should be attached to the master div");
    }

    // ── Helper: TrackingSync ──────────────────────────────────────────────────

    private static class TrackingSync extends Div implements DetailSyncAware<PropertyBox> {
        final List<String> names = new ArrayList<>();

        @Override
        public void onItemSelected(PropertyBox item) {
            names.add(item.getValue(NAME));
        }
    }
}
