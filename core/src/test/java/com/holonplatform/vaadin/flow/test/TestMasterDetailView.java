package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

/**
 * Browser-free UI unit tests for views built with {@link MasterDetailConfigurator}.
 *
 * <p>Each scenario declares a minimal {@code @Route} inner view so that
 * {@link UIUnitTest#navigate} can set up the full Vaadin session context.
 * Interactions are exercised through {@link #test}/{@link #$} — the TestBench
 * component-tester API — which validates visibility and enabled state before
 * firing events, mirroring actual browser behaviour.</p>
 *
 * <p>Complements {@link TestMasterDetailConfigurator}, which covers the
 * configurator API via direct {@code dispatchSync} calls without a running UI.</p>
 */
@ViewPackages
class TestMasterDetailView extends UIUnitTest {

    // ── Reusable test double ───────────────────────────────────────────────────

    /** A {@link Span} that records every {@code onItemSelected} call. */
    static class TrackingSpan extends Span implements DetailSyncAware<Object> {
        final List<Object> calls = new ArrayList<>();

        @Override
        public void onItemSelected(Object item) {
            calls.add(item);
        }
    }

    // ── Inner route views ──────────────────────────────────────────────────────

    /**
     * Minimal desktop master-detail view.
     * Fields are package-private so this test class can access them directly.
     */
    @Route("test-md-desktop")
    static class DesktopView extends Div {
        final MasterDetailLayout<Object> layout = new MasterDetailLayout<>();
        final Button selectButton = new Button("Select");
        final Span detailLabel = new Span("none");

        DesktopView() {
            MasterDetailConfigurator.configure(layout, Object.class)
                    .master(m -> m.content(selectButton))
                    .detail(d -> d
                            .content(detailLabel)
                            .withDetailSync(item -> detailLabel.setText("selected:" + item)));
            selectButton.addClickListener(e -> layout.dispatchSync("order-1"));
            add(layout);
        }
    }

    /**
     * Desktop view whose detail panel contains a {@link TrackingSpan} that implements
     * {@link DetailSyncAware}. No explicit {@code withDetailSync} — relies on auto-scan.
     */
    @Route("test-md-sync-aware")
    static class SyncAwareView extends Div {
        final MasterDetailLayout<Object> layout = new MasterDetailLayout<>();
        final Button selectButton = new Button("Pick");
        final TrackingSpan tracker = new TrackingSpan();

        SyncAwareView() {
            MasterDetailConfigurator.configure(layout, Object.class)
                    .master(m -> m.content(selectButton))
                    .detail(d -> d.content(tracker));
            selectButton.addClickListener(e -> layout.dispatchSync("event-A"));
            add(layout);
        }
    }

    /**
     * Desktop view where the {@link DetailSyncAware} component is nested inside an
     * intermediate wrapper {@link Div} — verifies recursive tree scan.
     */
    @Route("test-md-nested-sync-aware")
    static class NestedSyncAwareView extends Div {
        final MasterDetailLayout<Object> layout = new MasterDetailLayout<>();
        final Button selectButton = new Button("Tap");
        final TrackingSpan tracker = new TrackingSpan();

        NestedSyncAwareView() {
            Div wrapper = new Div(new Div(tracker)); // two levels of nesting
            MasterDetailConfigurator.configure(layout, Object.class)
                    .master(m -> m.content(selectButton))
                    .detail(d -> d.content(wrapper));
            selectButton.addClickListener(e -> layout.dispatchSync("nested-item"));
            add(layout);
        }
    }

    /**
     * Mobile view with an explicit {@link Sheet}: the detail panel is wrapped inside
     * the sheet rather than added to the DOM directly.
     */
    @Route("test-md-mobile-sheet")
    static class MobileSheetView extends Div {
        final MasterDetailLayout<Object> layout = new MasterDetailLayout<>();
        final Button selectButton = new Button("Tap");
        final Span detailLabel = new Span("none");
        final Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();

        MobileSheetView() {
            MasterDetailConfigurator.configure(layout, Object.class)
                    .viewMode(ViewMode.MOBILE)
                    .withMobileSheet(sheet)
                    .master(m -> m.content(selectButton))
                    .detail(d -> d
                            .content(detailLabel)
                            .withDetailSync(item -> detailLabel.setText("selected:" + item)));
            selectButton.addClickListener(e -> layout.dispatchSync("mobile-item"));
            add(layout);
        }
    }

    /**
     * Mobile view with a lazily-built detail panel: detail components are only
     * constructed on the first tap.
     */
    @Route("test-md-lazy")
    static class LazyDetailView extends Div {
        final MasterDetailLayout<Object> layout = new MasterDetailLayout<>();
        final Button selectButton = new Button("Open");
        final int[] buildCount = {0};
        final List<Object> syncLog = new ArrayList<>();
        final Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM).build();

        LazyDetailView() {
            MasterDetailConfigurator.configure(layout, Object.class)
                    .viewMode(ViewMode.MOBILE)
                    .withMobileSheet(sheet)
                    .master(m -> m.content(selectButton))
                    .lazyDetail(detail -> {
                        buildCount[0]++;
                        detail.content(new Span("lazy-content"))
                                .withDetailSync(syncLog::add);
                    });
            selectButton.addClickListener(e -> layout.dispatchSync("lazy-item"));
            add(layout);
        }
    }

    // ── Desktop tests ──────────────────────────────────────────────────────────

    @Test
    void navigate_desktopView_masterButtonIsPresent() {
        navigate(DesktopView.class);
        assertTrue($(Button.class).exists(), "Master button should be in the rendered view");
    }

    @Test
    void desktop_masterAndDetailDivsBothPresentInLayout_afterNavigate() {
        DesktopView view = navigate(DesktopView.class);
        assertEquals(2, view.layout.getChildren().count(),
                "Desktop: master div + detail div should both be in the layout");
    }

    @Test
    void desktop_detailLabel_startsWithDefaultText() {
        DesktopView view = navigate(DesktopView.class);
        assertEquals("none", view.detailLabel.getText(),
                "Detail label should show placeholder before any selection");
    }

    @Test
    void desktop_clickMasterButton_detailLabelUpdates() {
        DesktopView view = navigate(DesktopView.class);
        test(view.selectButton).click();
        assertEquals("selected:order-1", view.detailLabel.getText());
    }

    @Test
    void desktop_clickMasterButton_multipleClicks_detailReflectsLatestDispatch() {
        DesktopView view = navigate(DesktopView.class);
        test(view.selectButton).click();
        test(view.selectButton).click();
        assertEquals("selected:order-1", view.detailLabel.getText(),
                "Each click should dispatch sync; detail shows latest");
    }

    // ── DetailSyncAware auto-scan tests ────────────────────────────────────────

    @Test
    void syncAware_beforeClick_noCallsReceived() {
        SyncAwareView view = navigate(SyncAwareView.class);
        assertTrue(view.tracker.calls.isEmpty());
    }

    @Test
    void syncAware_clickButton_componentReceivesDispatchedItem() {
        SyncAwareView view = navigate(SyncAwareView.class);
        test(view.selectButton).click();
        assertEquals(List.of("event-A"), view.tracker.calls);
    }

    @Test
    void syncAware_multipleClicks_componentReceivesAllDispatches() {
        SyncAwareView view = navigate(SyncAwareView.class);
        test(view.selectButton).click();
        test(view.selectButton).click();
        assertEquals(List.of("event-A", "event-A"), view.tracker.calls,
                "Each click should trigger onItemSelected");
    }

    @Test
    void syncAware_nestedInsideWrapper_autoScannedAndReceivesItem() {
        NestedSyncAwareView view = navigate(NestedSyncAwareView.class);
        test(view.selectButton).click();
        assertEquals(List.of("nested-item"), view.tracker.calls,
                "collectSyncAware should walk the full subtree");
    }

    // ── Mobile sheet tests ─────────────────────────────────────────────────────

    @Test
    void mobile_sheet_closedOnNavigate() {
        MobileSheetView view = navigate(MobileSheetView.class);
        assertFalse(view.sheet.isOpen(), "Sheet must be closed before any interaction");
    }

    @Test
    void mobile_sheet_andMasterDivPresentInLayout_afterNavigate() {
        MobileSheetView view = navigate(MobileSheetView.class);
        // mobile + sheet + detail().add(): layout gets [masterDiv, sheet]
        assertEquals(2, view.layout.getChildren().count(),
                "Mobile: master div + sheet (not raw detail div) should be in layout root");
        assertTrue(view.layout.getChildren().anyMatch(c -> c instanceof Sheet),
                "Sheet should be a direct child of the layout");
    }

    @Test
    void mobile_tapButton_opensSheet() {
        MobileSheetView view = navigate(MobileSheetView.class);
        test(view.selectButton).click();
        assertTrue(view.sheet.isOpen(), "Sheet should open after tap");
    }

    @Test
    void mobile_tapButton_detailLabelUpdatesInsideSheet() {
        MobileSheetView view = navigate(MobileSheetView.class);
        test(view.selectButton).click();
        assertEquals("selected:mobile-item", view.detailLabel.getText(),
                "withDetailSync handler fires before Sheet becomes visible");
    }

    @Test
    void mobile_secondTap_sheetRemainsOpen_andDetailUpdates() {
        MobileSheetView view = navigate(MobileSheetView.class);
        test(view.selectButton).click();
        test(view.selectButton).click();
        assertTrue(view.sheet.isOpen());
        assertEquals("selected:mobile-item", view.detailLabel.getText());
    }

    // ── Lazy detail tests ──────────────────────────────────────────────────────

    @Test
    void lazy_consumerNotCalledOnNavigate() {
        LazyDetailView view = navigate(LazyDetailView.class);
        assertEquals(0, view.buildCount[0], "Lazy consumer must not run at build/navigate time");
    }

    @Test
    void lazy_sheetNotYetInLayout_beforeFirstTap() {
        LazyDetailView view = navigate(LazyDetailView.class);
        // Only master div should be in layout before any tap
        assertEquals(1, view.layout.getChildren().count(),
                "Lazy: only master div present before first tap");
    }

    @Test
    void lazy_firstTap_buildsDetailAndOpensSheet() {
        LazyDetailView view = navigate(LazyDetailView.class);
        test(view.selectButton).click();
        assertEquals(1, view.buildCount[0], "Consumer should run exactly once on first tap");
        assertTrue(view.sheet.isOpen(), "Sheet should open after first tap");
    }

    @Test
    void lazy_sheetAddedToLayout_afterFirstTap() {
        LazyDetailView view = navigate(LazyDetailView.class);
        test(view.selectButton).click();
        assertEquals(2, view.layout.getChildren().count(),
                "Master div + sheet should be in layout after first tap");
        assertTrue(view.layout.getChildren().anyMatch(c -> c instanceof Sheet));
    }

    @Test
    void lazy_consumerCalledExactlyOnce_acrossMultipleTaps() {
        LazyDetailView view = navigate(LazyDetailView.class);
        test(view.selectButton).click();
        test(view.selectButton).click();
        test(view.selectButton).click();
        assertEquals(1, view.buildCount[0],
                "Detail components should be built exactly once, never per subsequent tap");
    }

    @Test
    void lazy_syncHandlerFiresOnEveryTap() {
        LazyDetailView view = navigate(LazyDetailView.class);
        test(view.selectButton).click();
        test(view.selectButton).click();
        assertEquals(List.of("lazy-item", "lazy-item"), view.syncLog,
                "withDetailSync fires on every tap, not only the first");
    }
}
