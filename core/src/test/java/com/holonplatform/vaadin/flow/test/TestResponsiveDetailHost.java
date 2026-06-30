package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.ResponsiveDetailHost;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Browser-free unit tests for {@link ResponsiveDetailHost}.
 *
 * <p>Covers:</p>
 * <ol>
 *   <li>Initial state — desktop slot has {@code mdl-detail--no-selection}; sheet is closed.</li>
 *   <li>{@code place(desktop)} — builds content once, adds to desktop slot, opens desktop.</li>
 *   <li>{@code place(mobile)} — builds content once, places in sheet, opens sheet.</li>
 *   <li>Content provider called only ONCE regardless of how many times {@code place} is called.</li>
 *   <li>{@link DetailSyncAware} components are notified on every {@code place} call.</li>
 *   <li>{@code hide()} — adds back no-selection class, closes sheet.</li>
 *   <li>{@code onModeChanged} — transitions desktop → mobile with open item.</li>
 *   <li>{@code onModeChanged} — transitions mobile → desktop with open item.</li>
 *   <li>{@code onModeChanged} with {@code null} item — no sheet open, no desktop reveal.</li>
 * </ol>
 *
 * <p>No browser or Vaadin UI session is required.</p>
 */
class TestResponsiveDetailHost {

    record Product(Long id, String name) {}

    private Layout slot;
    private Sheet sheet;
    private ResponsiveDetailHost host;

    @BeforeEach
    void setUp() {
        slot  = new Layout();
        sheet = Sheet.builder(Sheet.Side.RIGHT).build();
        host  = new ResponsiveDetailHost(slot, sheet);
    }

    // ── 1. Initial state ──────────────────────────────────────────────────────

    @Test
    void initialState_slotHasNoSelectionClass() {
        assertTrue(slot.getClassNames().contains("mdl-detail--no-selection"),
                "Desktop slot must have 'mdl-detail--no-selection' class initially");
    }

    @Test
    void initialState_sheetIsClosed() {
        assertFalse(sheet.isOpen(), "Sheet must be closed initially");
    }

    // ── 2. place — desktop ────────────────────────────────────────────────────

    @Test
    void place_desktop_removesNoSelectionClass() {
        Product p = new Product(1L, "Widget");

        host.place(p, ViewMode.DESKTOP, item -> new Component[]{ new Span(item.name()) });

        assertFalse(slot.getClassNames().contains("mdl-detail--no-selection"),
                "'mdl-detail--no-selection' must be removed on desktop placement");
    }

    @Test
    void place_desktop_addsContentToSlot() {
        Product p = new Product(1L, "Widget");
        Span content = new Span(p.name());

        host.place(p, ViewMode.DESKTOP, item -> new Component[]{ content });

        assertTrue(slot.getChildren().anyMatch(c -> c == content),
                "Content component must be added to the desktop slot");
    }

    @Test
    void place_desktop_sheetRemainsClosedl() {
        host.place(new Product(1L, "Widget"), ViewMode.DESKTOP,
                item -> new Component[]{ new Span(item.name()) });

        assertFalse(sheet.isOpen(), "Sheet must NOT open for desktop placement");
    }

    // ── 3. place — mobile ─────────────────────────────────────────────────────

    @Test
    void place_mobile_opensSheet() {
        host.place(new Product(1L, "Mobile"), ViewMode.MOBILE,
                item -> new Component[]{ new Span(item.name()) });

        assertTrue(sheet.isOpen(), "Sheet must open for mobile placement");
    }

    @Test
    void place_mobile_slotRetainsNoSelectionClass() {
        host.place(new Product(1L, "Mobile"), ViewMode.MOBILE,
                item -> new Component[]{ new Span(item.name()) });

        assertTrue(slot.getClassNames().contains("mdl-detail--no-selection"),
                "Desktop slot must still have 'mdl-detail--no-selection' on mobile placement");
    }

    // ── 4. Content provider called only once ──────────────────────────────────

    @Test
    void place_calledMultipleTimes_contentProviderCalledOnlyOnce() {
        int[] buildCount = { 0 };

        host.place(new Product(1L, "First"), ViewMode.DESKTOP, item -> {
            buildCount[0]++;
            return new Component[]{ new Span(item.name()) };
        });
        host.place(new Product(2L, "Second"), ViewMode.DESKTOP, item -> {
            buildCount[0]++;
            return new Component[]{ new Span(item.name()) };
        });
        host.place(new Product(3L, "Third"), ViewMode.MOBILE, item -> {
            buildCount[0]++;
            return new Component[]{ new Span(item.name()) };
        });

        assertEquals(1, buildCount[0],
                "Content provider must be invoked exactly once (build-once strategy)");
    }

    // ── 5. DetailSyncAware notification ───────────────────────────────────────

    @Test
    void place_notifiesDetailSyncAware_onEveryCall() {
        TrackingPanel panel = new TrackingPanel();

        host.place(new Product(1L, "Alpha"), ViewMode.DESKTOP,
                item -> new Component[]{ panel });
        host.place(new Product(2L, "Beta"),  ViewMode.DESKTOP,
                item -> new Component[]{ panel });   // provider ignored on 2nd call

        assertEquals(List.of("Alpha", "Beta"), panel.log,
                "DetailSyncAware must be called on every place(), not only the first");
    }

    @Test
    void place_notifiesDetailSyncAware_onMobile() {
        TrackingPanel panel = new TrackingPanel();

        host.place(new Product(1L, "Mobile"), ViewMode.MOBILE,
                item -> new Component[]{ panel });

        assertEquals(List.of("Mobile"), panel.log);
    }

    // ── 6. hide ───────────────────────────────────────────────────────────────

    @Test
    void hide_addsBackNoSelectionClass_afterDesktopPlacement() {
        host.place(new Product(1L, "W"), ViewMode.DESKTOP,
                item -> new Component[]{ new Span() });

        assertFalse(slot.getClassNames().contains("mdl-detail--no-selection"));

        host.hide();

        assertTrue(slot.getClassNames().contains("mdl-detail--no-selection"),
                "'mdl-detail--no-selection' must be re-added after hide()");
    }

    @Test
    void hide_closesSheet_whenOpen() {
        host.place(new Product(1L, "M"), ViewMode.MOBILE,
                item -> new Component[]{ new Span() });

        assertTrue(sheet.isOpen());

        host.hide();

        assertFalse(sheet.isOpen(), "Sheet must be closed by hide()");
    }

    @Test
    void hide_noop_whenNothingPlaced() {
        // Must not throw
        host.hide();
        assertTrue(slot.getClassNames().contains("mdl-detail--no-selection"));
        assertFalse(sheet.isOpen());
    }

    // ── 7. onModeChanged — desktop → mobile with item ────────────────────────

    @Test
    void onModeChanged_desktopToMobile_opensSheet() {
        Product p = new Product(1L, "Flip");

        // Start with desktop placement
        host.place(p, ViewMode.DESKTOP, item -> new Component[]{ new Span(item.name()) });

        host.onModeChanged(ViewMode.MOBILE, p,
                item -> new Component[]{ new Span(item.name()) }); // ignored (already built)

        assertTrue(sheet.isOpen(), "Sheet must open after transition to mobile");
    }

    @Test
    void onModeChanged_desktopToMobile_slotGetNoSelectionClass() {
        Product p = new Product(1L, "Flip");

        host.place(p, ViewMode.DESKTOP, item -> new Component[]{ new Span(item.name()) });

        host.onModeChanged(ViewMode.MOBILE, p,
                item -> new Component[]{ new Span(item.name()) });

        assertTrue(slot.getClassNames().contains("mdl-detail--no-selection"),
                "Desktop slot must get 'mdl-detail--no-selection' after transition to mobile");
    }

    // ── 8. onModeChanged — mobile → desktop with item ────────────────────────

    @Test
    void onModeChanged_mobileToDesktop_closesSheet() {
        Product p = new Product(1L, "Flip");

        host.place(p, ViewMode.MOBILE, item -> new Component[]{ new Span(item.name()) });

        host.onModeChanged(ViewMode.DESKTOP, p,
                item -> new Component[]{ new Span(item.name()) });

        assertFalse(sheet.isOpen(), "Sheet must close after transition to desktop");
    }

    @Test
    void onModeChanged_mobileToDesktop_removesNoSelectionClass() {
        Product p = new Product(1L, "Flip");

        host.place(p, ViewMode.MOBILE, item -> new Component[]{ new Span(item.name()) });

        host.onModeChanged(ViewMode.DESKTOP, p,
                item -> new Component[]{ new Span(item.name()) });

        assertFalse(slot.getClassNames().contains("mdl-detail--no-selection"),
                "'mdl-detail--no-selection' must be removed after transition to desktop");
    }

    // ── 9. onModeChanged — null item ─────────────────────────────────────────

    @Test
    void onModeChanged_nullItem_mobileMode_sheetRemainsClosedAndNotBuilt() {
        int[] buildCount = { 0 };

        host.onModeChanged(ViewMode.MOBILE, null, item -> {
            buildCount[0]++;
            return new Component[]{ new Span() };
        });

        assertFalse(sheet.isOpen(), "Sheet must remain closed when no item is provided");
        assertEquals(0, buildCount[0], "Content provider must not be called with null item");
    }

    @Test
    void onModeChanged_nullItem_desktopMode_slotHasNoSelectionClass() {
        host.onModeChanged(ViewMode.DESKTOP, null, item -> new Component[]{ new Span() });

        assertTrue(slot.getClassNames().contains("mdl-detail--no-selection"),
                "Desktop slot must retain no-selection class when switching to desktop with no item");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /** Test double: a Div that records every {@link DetailSyncAware#onItemSelected} call. */
    private static class TrackingPanel extends Div implements DetailSyncAware<Product> {
        final List<String> log = new ArrayList<>();

        @Override
        public void onItemSelected(Product item) {
            log.add(item.name());
        }
    }
}

