package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Unit tests for {@link com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator}
 * via {@link Components#masterDetail(Class)} — Consumer-based API.
 */
class TestMasterDetailConfigurator {

    record Product(String name) {}

    private MasterDetailLayout<Product> layout;
    private MasterDetailBuilder<Product> sut;

    @BeforeEach
    void setUp() {
        sut = Components.masterDetail(Product.class);
        layout = sut.build();
    }

    // ── configure() factory ────────────────────────────────────────────────────

    @Test
    void configure_returnsNonNullConfigurator() {
        assertNotNull(sut);
    }

    @Test
    void configure_wrapsProvidedLayout_initiallyEmpty() {
        // Layout starts with only the master-detail-container class, no child components
        assertEquals(0, layout.getChildren().count());
    }

    // ── mobile(Div) ────────────────────────────────────────────────────────────

    @Test
    void mobile_addsMasterDivToRoot() {
        Div masterDiv = new Div();

        sut.mobile(masterDiv);

        List<Component> children = layout.getChildren().toList();
        assertEquals(1, children.size());
        assertSame(masterDiv, children.getFirst());
    }

    @Test
    void mobile_returnsSameConfigurator_forChaining() {
        Div masterDiv = new Div();
        MasterDetailBuilder<Product> result = sut.mobile(masterDiv);
        assertSame(sut, result);
    }

    // ── desktop(Div, Div) ──────────────────────────────────────────────────────

    @Test
    void desktop_addsBothDivsToRoot() {
        Div masterDiv = new Div();
        Div detailDiv = new Div();

        sut.desktop(masterDiv, detailDiv);

        List<Component> children = layout.getChildren().toList();
        assertEquals(2, children.size());
        assertSame(masterDiv, children.getFirst());
        assertSame(detailDiv, children.get(1));
    }

    @Test
    void desktop_returnsSameConfigurator_forChaining() {
        MasterDetailBuilder<Product> result = sut.desktop(new Div(), new Div());
        assertSame(sut, result);
    }

    // ── master(Consumer) ───────────────────────────────────────────────────────

    @Test
    void master_appendsMasterDivToRoot() {
        sut.master(m -> {});

        assertEquals(1, layout.getChildren().count());
    }

    @Test
    void master_returnsSameConfigurator_forChaining() {
        MasterDetailBuilder<Product> returned = sut.master(m -> {});
        assertSame(sut, returned);
    }

    @Test
    void masterContent_appearsInsideMasterDiv() {
        Span span = new Span("hello");

        sut.master(m -> m.content(span));

        Div masterDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertTrue(masterDiv.getChildren().anyMatch(c -> c == span));
    }

    @Test
    void masterHeader_isFirstChildOfMasterDiv() {
        sut.master(m -> m.header(h -> h.heading("Customers")));

        Div masterDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertTrue(masterDiv.getChildren().findAny().isPresent(), "Master div should have at least the header");

        // Header is added as first child
        Component firstChild = masterDiv.getChildren().findFirst().orElseThrow();
        assertInstanceOf(Header.class, firstChild,
                "First child of master div should be the Header component");
    }

    @Test
    void listingBundleHeader_isAddedToMasterDiv() {
        sut.master(m -> m.listing(l -> l
                .gridHeader("Products")
                .fetch((q, text, sort) -> java.util.stream.Stream.empty())));

        Div masterDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        // With the Consumer API the listing bundle is added as a single component;
        // the GridHeader lives inside it — verify by scanning the full subtree.
        boolean hasGridHeader = masterDiv.getChildren()
                .anyMatch(c -> c instanceof GridHeader
                        || c.getChildren().anyMatch(gc -> gc instanceof GridHeader));
        assertTrue(hasGridHeader,
                "A GridHeader should be present inside the master div (directly or within the ListingBundle)");
    }

    @Test
    void masterFooter_isAttachedToMasterDiv() {
        sut.master(m -> m.footer(f -> {}));

        Div masterDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertTrue(masterDiv.getChildren().findAny().isPresent(), "Footer should be attached to master div");
    }

    @Test
    void multipleMasterCalls_eachCreatingSeparateDiv() {
        Div div1 = new Div();
        Div div2 = new Div();

        // Two independent master consumers — each should produce its own Div in the root
        sut.master(m -> m.content(div1));
        sut.master(m -> m.content(div2));

        List<Component> children = layout.getChildren().toList();
        assertEquals(2, children.size(), "Each master() call should append a distinct Div");
        assertNotSame(children.get(0), children.get(1));
    }

    // ── detail(Consumer) ───────────────────────────────────────────────────────

    @Test
    void detail_appendsDetailDivToRoot() {
        sut.detail(d -> {});

        assertEquals(1, layout.getChildren().count());
    }

    @Test
    void detail_returnsSameConfigurator_forChaining() {
        MasterDetailBuilder<Product> returned = sut.detail(d -> {});
        assertSame(sut, returned);
    }

    @Test
    void detailContent_appearsInsideDetailDiv() {
        Span span = new Span("detail content");

        sut.detail(d -> d.content(span));

        Div detailDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertTrue(detailDiv.getChildren().anyMatch(c -> c == span));
    }

    @Test
    void detailHeader_isFirstChildOfDetailDiv() {
        sut.detail(d -> d.header(h -> h.heading("Details")));

        Div detailDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        Component firstChild = detailDiv.getChildren().findFirst().orElseThrow();
        assertInstanceOf(Header.class, firstChild);
    }

    // ── chaining master + detail ───────────────────────────────────────────────

    @Test
    void masterThenDetail_producesTwoChildDivs() {
        Span masterContent = new Span("master");
        Span detailContent = new Span("detail");

        sut.master(m -> m.content(masterContent))
           .detail(d -> d.content(detailContent));

        List<Component> children = layout.getChildren().toList();
        assertEquals(2, children.size());

        Div masterDiv = (Div) children.getFirst();
        Div detailDiv = (Div) children.get(1);

        assertTrue(masterDiv.getChildren().anyMatch(c -> c == masterContent));
        assertTrue(detailDiv.getChildren().anyMatch(c -> c == detailContent));
    }

    @Test
    void masterAndDetailDivs_areDistinctInstances() {
        sut.master(m -> {});
        sut.detail(d -> {});

        List<Component> children = layout.getChildren().toList();
        assertNotSame(children.get(0), children.get(1));
    }

    // ── master / detail Divs are fresh per call ────────────────────────────────

    @Test
    void masterDiv_isNotTheSameAsLayout() {
        sut.master(m -> {});

        Div masterDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertNotSame(layout, masterDiv);
    }

    @Test
    void detailDiv_isNotTheSameAsLayout() {
        sut.detail(d -> {});

        Div detailDiv = (Div) layout.getChildren().findFirst().orElseThrow();
        assertNotSame(layout, detailDiv);
    }

    // ── withDetailSync() (top-level — no detail node needed) ──────────────────

    @Test
    void withDetailSync_handlerFiresOnDispatchSync() {
        List<Product> received = new ArrayList<>();
        sut.withDetailSync(received::add);

        Product item = new Product("item1");
        layout.dispatchSync(item);

        assertEquals(1, received.size());
        assertSame(item, received.getFirst());
    }

    @Test
    void withDetailSync_multipleHandlers_allFire() {
        List<String> log = new ArrayList<>();
        sut.withDetailSync(item -> log.add("A:" + item.name()));
        sut.withDetailSync(item -> log.add("B:" + item.name()));

        layout.dispatchSync(new Product("ping"));

        assertEquals(List.of("A:ping", "B:ping"), log);
    }

    @Test
    void withDetailSync_notFiredBeforeAnyDispatch() {
        List<Product> received = new ArrayList<>();
        sut.withDetailSync(received::add);

        assertTrue(received.isEmpty());
    }

    // ── detail(Consumer).withDetailSync() ─────────────────────────────────────

    @Test
    void detailConsumer_withDetailSync_firesOnDispatchSync() {
        List<Product> received = new ArrayList<>();
        sut.detail(d -> d.withDetailSync((Product item) -> received.add(item)));

        Product item = new Product("order42");
        layout.dispatchSync(item);

        assertEquals(1, received.size());
        assertSame(item, received.getFirst());
    }

    @Test
    void detailConsumer_withDetailSync_firesOnEverySubsequentDispatch() {
        List<Product> log = new ArrayList<>();
        sut.detail(d -> d.withDetailSync((Product item) -> log.add(item)));

        Product first = new Product("first");
        Product second = new Product("second");
        layout.dispatchSync(first);
        layout.dispatchSync(second);

        assertEquals(List.of(first, second), log);
    }

    // ── DetailSyncAware auto-scan ─────────────────────────────────────────────

    @Test
    void detail_detailSyncAware_isAutoDiscoveredAndFiredOnDispatchSync() {
        TrackingSync tracker = new TrackingSync();
        sut.detail(d -> d.content(tracker));

        layout.dispatchSync(new Product("payload"));

        assertEquals(List.of("payload"), tracker.calls);
    }

    @Test
    void detail_detailSyncAware_nestedInsideDiv_isScannedAndFired() {
        TrackingSync tracker = new TrackingSync();
        Div wrapper = new Div(tracker);
        sut.detail(d -> d.content(wrapper));

        layout.dispatchSync(new Product("nested"));

        assertEquals(List.of("nested"), tracker.calls);
    }

    @Test
    void detail_detailSyncAware_firesOnEveryDispatch() {
        TrackingSync tracker = new TrackingSync();
        sut.detail(d -> d.content(tracker));

        layout.dispatchSync(new Product("a"));
        layout.dispatchSync(new Product("b"));

        assertEquals(List.of("a", "b"), tracker.calls);
    }

    // ── viewMode() ────────────────────────────────────────────────────────────

    @Test
    void viewMode_mobile_detailNotAddedToLayoutRoot() {
        sut.viewMode(ViewMode.MOBILE).master(m -> {});
        sut.detail(d -> {});

        // master Div is present; detail Div is suppressed (mobile — no sheet configured)
        assertEquals(1, layout.getChildren().count());
    }

    @Test
    void viewMode_desktop_detailIsAddedNormally() {
        sut.viewMode(ViewMode.DESKTOP).master(m -> {});
        sut.detail(d -> {});

        assertEquals(2, layout.getChildren().count());
    }

    @Test
    void viewMode_mobile_noSheet_explicitSyncHandlerStillFires() {
        List<Product> received = new ArrayList<>();
        sut.viewMode(ViewMode.MOBILE);
        sut.detail(d -> d.withDetailSync((Product item) -> received.add(item)));

        Product item = new Product("tap");
        layout.dispatchSync(item);

        assertEquals(List.of(item), received);
    }

    // ── withMobileSheet(Sheet) ────────────────────────────────────────────────

    @Test
    void withMobileSheet_sheetAddedToLayoutRoot_notDetailDiv() {
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.detail(d -> d.content(new Span("detail content")));

        List<Component> children = layout.getChildren().toList();
        assertEquals(2, children.size());
        assertInstanceOf(Sheet.class, children.get(1),
                "Second child should be Sheet, not raw detail Div");
    }

    @Test
    void withMobileSheet_dispatchSync_opensSheet() {
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.detail(d -> {});

        assertFalse(sheet.isOpen(), "Sheet should be closed before any dispatchSync");
        layout.dispatchSync(new Product("item"));
        assertTrue(sheet.isOpen(), "Sheet should be open after dispatchSync");
    }

    @Test
    void withMobileSheet_detailSyncAware_firesOnEachDispatch() {
        TrackingSync tracker = new TrackingSync();
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.detail(d -> d.content(tracker));

        layout.dispatchSync(new Product("first"));
        layout.dispatchSync(new Product("second"));

        assertEquals(List.of("first", "second"), tracker.calls);
    }

    @Test
    void withMobileSheet_sideConvenienceOverload_sheetPresentAndOpensOnDispatch() {
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(Sheet.Side.BOTTOM)
                .master(m -> {});
        sut.detail(d -> {});

        layout.dispatchSync(new Product("item"));

        boolean sheetPresent = layout.getChildren().anyMatch(c -> c instanceof Sheet);
        assertTrue(sheetPresent, "Auto-created Sheet should be in layout after dispatchSync");

        Sheet sheet = (Sheet) layout.getChildren()
                .filter(c -> c instanceof Sheet)
                .findFirst().orElseThrow();
        assertTrue(sheet.isOpen());
    }

    // ── lazyDetail() ──────────────────────────────────────────────────────────

    @Test
    void lazyDetail_desktop_buildsEagerlyAndAddsToRoot() {
        boolean[] built = {false};
        sut.lazyDetail(detail -> {
            built[0] = true;
            detail.content(new Span("eager"));
        });

        assertTrue(built[0], "Consumer should run at build time on desktop");
        assertEquals(1, layout.getChildren().count(), "Detail Div should be added to root");
    }

    @Test
    void lazyDetail_desktop_syncHandlersFire() {
        List<Product> log = new ArrayList<>();
        sut.lazyDetail(detail -> detail.withDetailSync((Product item) -> log.add(item)));

        Product item = new Product("desktop-item");
        layout.dispatchSync(item);

        assertEquals(List.of(item), log);
    }

    @Test
    void lazyDetail_mobile_consumerNotCalledAtBuildTime() {
        boolean[] built = {false};
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.lazyDetail(detail -> {
            built[0] = true;
            detail.content(new Span("lazy"));
        });

        assertFalse(built[0], "Consumer must NOT run at build time on mobile");
    }

    @Test
    void lazyDetail_mobile_consumerCalledOnFirstDispatch() {
        boolean[] built = {false};
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.lazyDetail(detail -> {
            built[0] = true;
            detail.content(new Span("lazy"));
        });

        layout.dispatchSync(new Product("tap"));

        assertTrue(built[0], "Consumer must run on first dispatchSync");
        assertTrue(sheet.isOpen(), "Sheet must open after first tap");
    }

    @Test
    void lazyDetail_mobile_consumerCalledExactlyOnce() {
        int[] buildCount = {0};
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.lazyDetail(detail -> {
            buildCount[0]++;
            detail.content(new Span("lazy"));
        });

        layout.dispatchSync(new Product("tap1"));
        layout.dispatchSync(new Product("tap2"));
        layout.dispatchSync(new Product("tap3"));

        assertEquals(1, buildCount[0], "Components should be built exactly once");
    }

    @Test
    void lazyDetail_mobile_withDetailSync_firesOnEveryDispatch() {
        List<Product> log = new ArrayList<>();
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.lazyDetail(detail -> detail.withDetailSync((Product item) -> log.add(item)));

        Product first = new Product("first");
        Product second = new Product("second");
        layout.dispatchSync(first);
        layout.dispatchSync(second);

        assertEquals(List.of(first, second), log);
    }

    @Test
    void lazyDetail_mobile_detailSyncAware_firesOnEveryDispatch() {
        TrackingSync tracker = new TrackingSync();
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.lazyDetail(detail -> detail.content(tracker));

        layout.dispatchSync(new Product("a"));
        layout.dispatchSync(new Product("b"));

        assertEquals(List.of("a", "b"), tracker.calls);
    }

    @Test
    void lazyDetail_mobile_sheetAddedToLayoutAfterFirstDispatch() {
        Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();
        sut.viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> {});
        sut.lazyDetail(detail -> detail.content(new Span("x")));

        assertEquals(1, layout.getChildren().count(), "Only master before first tap");

        layout.dispatchSync(new Product("tap"));

        assertEquals(2, layout.getChildren().count(), "Master + Sheet after first tap");
        assertTrue(layout.getChildren().anyMatch(c -> c instanceof Sheet));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /** Test double: a {@link Div} that records every {@link DetailSyncAware#onItemSelected} call. */
    private static class TrackingSync extends Div implements DetailSyncAware<Product> {
        final List<String> calls = new ArrayList<>();

        @Override
        public void onItemSelected(Product item) {
            calls.add(item.name());
        }
    }
}
