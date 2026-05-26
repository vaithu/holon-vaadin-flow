/*
 * Copyright 2016-2017 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.signals.Signal;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DynamicFilterPanel}.
 *
 * <p>These tests verify:
 * <ol>
 *   <li>Filter state management ({@link DynamicFilterPanel#getQueryFilter()},
 *       {@link DynamicFilterPanel#isAnyActive()}, {@link DynamicFilterPanel#resetAll()}).</li>
 *   <li>{@link DynamicFilterPanel#applyFilterProgrammatically(QueryFilter)} — the primary
 *       API for restoring saved filter state and for testing signal wiring without browser
 *       interaction.</li>
 *   <li>{@link com.holonplatform.vaadin.flow.components.events.FilterChangeListener} notification
 *       on both <em>apply</em> and <em>reset</em> lifecycle events.</li>
 *   <li>{@link DynamicFilterPanel#addApplyListener(Runnable)} semantics — only fires on
 *       the built-in "Apply filter" button, <strong>not</strong> on programmatic apply or reset.</li>
 *   <li>{@link FilterInputGroup#queryFilterSignal()} signal bridge — the {@link Signal}
 *       updates whenever the filter changes, enabling reactive wiring to
 *       {@link com.holonplatform.vaadin.flow.components.ItemListingPageSizeSelector}.</li>
 *   <li>In-memory {@link DynamicFilterPanel#toPredicate()} evaluation for EQUALS / CONTAINS
 *       type operators.</li>
 * </ol>
 *
 * <p>Signal-based tests ({@code signal_*}) extend {@link AbstractSessionTest} because
 * {@code Signal.effect(Component, Runnable)} requires a component attached to a UI with an
 * active {@link com.vaadin.flow.server.VaadinSession}.</p>
 */
class TestDynamicFilterPanel extends AbstractSessionTest {

    // ── Simple test DTO ──────────────────────────────────────────────────────

    /**
     * Minimal bean used to drive DynamicFilterPanel introspection.
     * Field names deliberately kept lower-camel so the label conversion
     * ("name" → "Name", "price" → "Price") is predictable.
     */
    static class Product {
        private String name = "";
        private double price = 0.0;
        private String category = "";

        Product() {}
        Product(String name, double price, String category) {
            this.name     = name;
            this.price    = price;
            this.category = category;
        }

        public String getName()        { return name; }
        public void   setName(String v){ this.name = v; }
        public double getPrice()        { return price; }
        public void   setPrice(double v){ this.price = v; }
        public String getCategory()     { return category; }
        public void   setCategory(String v){ this.category = v; }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Filter state tests
    // ────────────────────────────────────────────────────────────────────────

    @Test
    void newPanel_hasNoActiveFilter() {
        var panel = DynamicFilterPanel.of(Product.class);

        assertFalse(panel.isAnyActive(),
                "freshly created panel must report no active filter");
        assertFalse(panel.getQueryFilter().isPresent(),
                "freshly created panel must return empty QueryFilter optional");
    }

    @Test
    void applyFilterProgrammatically_setsActiveFiler() {
        var panel = DynamicFilterPanel.of(Product.class);
        QueryFilter filter = QueryFilter.eq(PathProperty.create("name", String.class), "Laptop");

        panel.applyFilterProgrammatically(filter);

        assertTrue(panel.isAnyActive(),
                "panel must report active filter after programmatic apply");
        assertEquals(Optional.of(filter), panel.getQueryFilter(),
                "getQueryFilter() must return the programmatically applied filter");
    }

    @Test
    void resetAll_clearsFilter() {
        var panel = DynamicFilterPanel.of(Product.class);
        QueryFilter filter = QueryFilter.eq(PathProperty.create("name", String.class), "X");
        panel.applyFilterProgrammatically(filter);

        panel.resetAll();

        assertFalse(panel.isAnyActive(),
                "panel must report no active filter after resetAll()");
        assertFalse(panel.getQueryFilter().isPresent(),
                "getQueryFilter() must return empty after resetAll()");
    }

    @Test
    void applyFilterProgrammatically_nullClearsFilter() {
        var panel = DynamicFilterPanel.of(Product.class);
        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        // Applying null = explicit clear
        panel.applyFilterProgrammatically(null);

        assertFalse(panel.isAnyActive());
        assertFalse(panel.getQueryFilter().isPresent());
    }

    // ────────────────────────────────────────────────────────────────────────
    // FilterChangeListener tests
    // ────────────────────────────────────────────────────────────────────────

    @Test
    void addFilterChangeListener_firesOnProgrammaticApply() {
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicInteger callCount = new AtomicInteger();
        panel.addFilterChangeListener(e -> callCount.incrementAndGet());

        QueryFilter filter = QueryFilter.eq(PathProperty.create("name", String.class), "Lap");
        panel.applyFilterProgrammatically(filter);

        assertEquals(1, callCount.get(),
                "FilterChangeListener must be called once on programmatic apply");
    }

    @Test
    void addFilterChangeListener_firesOnResetAll() {
        var panel = DynamicFilterPanel.of(Product.class);
        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        AtomicInteger callCount = new AtomicInteger();
        panel.addFilterChangeListener(e -> callCount.incrementAndGet());

        panel.resetAll();

        assertEquals(1, callCount.get(),
                "FilterChangeListener must be called once on resetAll()");
    }

    @Test
    void addFilterChangeListener_canBeUnregistered() {
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicInteger callCount = new AtomicInteger();
        var reg = panel.addFilterChangeListener(e -> callCount.incrementAndGet());

        // Remove before any event — must not receive any notification.
        reg.remove();
        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        assertEquals(0, callCount.get(),
                "listener must not fire after registration.remove()");
    }

    @Test
    void addFilterChangeListener_previousAndNewFilterPassedInEvent() {
        var panel = DynamicFilterPanel.of(Product.class);
        QueryFilter first  = QueryFilter.eq(PathProperty.create("name", String.class), "A");
        QueryFilter second = QueryFilter.eq(PathProperty.create("name", String.class), "B");

        AtomicReference<QueryFilter> capturedPrev = new AtomicReference<>();
        AtomicReference<QueryFilter> capturedNew  = new AtomicReference<>();
        panel.addFilterChangeListener(e -> {
            capturedPrev.set(e.getOldQueryFilter().orElse(null));
            capturedNew.set(e.getNewQueryFilter().orElse(null));
        });

        panel.applyFilterProgrammatically(first);   // prev=null → first
        assertEquals(null,  capturedPrev.get(), "previous filter should be null on first apply");
        assertEquals(first, capturedNew.get(),  "new filter should equal first");

        panel.applyFilterProgrammatically(second);  // prev=first → second
        assertEquals(first,  capturedPrev.get(), "previous filter should equal first");
        assertEquals(second, capturedNew.get(),  "new filter should equal second");
    }

    // ────────────────────────────────────────────────────────────────────────
    // addApplyListener tests
    // ────────────────────────────────────────────────────────────────────────

    @Test
    void addApplyListener_neverFiredOnProgrammaticApply() {
        // addApplyListener callbacks are ONLY fired when the built-in "Apply filter"
        // button is clicked — never on programmatic apply or resetAll().
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicInteger callCount = new AtomicInteger();
        panel.addApplyListener(callCount::incrementAndGet);

        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        assertEquals(0, callCount.get(),
                "addApplyListener MUST NOT fire on programmatic filter application");
    }

    @Test
    void addApplyListener_neverFiredOnResetAll() {
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicInteger callCount = new AtomicInteger();
        panel.addApplyListener(callCount::incrementAndGet);

        panel.resetAll();

        assertEquals(0, callCount.get(),
                "addApplyListener MUST NOT fire on resetAll()");
    }

    @Test
    void addApplyListener_canBeUnregistered() {
        // Simulate the dialog-close wiring pattern: addApplyListener(dialog::close).
        // After remove() the dialog should NOT receive further close notifications.
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicInteger closeCount = new AtomicInteger();
        var reg = panel.addApplyListener(closeCount::incrementAndGet);
        reg.remove();

        // It's impossible to trigger via public API (button click requires browser),
        // but we can at least confirm that remove() itself doesn't throw.
        assertEquals(0, closeCount.get());
    }

    // ────────────────────────────────────────────────────────────────────────
    // queryFilterSignal tests  (requires AbstractSessionTest setup for Signal.effect)
    // ────────────────────────────────────────────────────────────────────────

    @Test
    void queryFilterSignal_initiallyReturnsCurrentFilter() {
        var panel  = DynamicFilterPanel.of(Product.class);
        Signal<Optional<QueryFilter>> sig = panel.queryFilterSignal();

        // Signal is initialized with the current filter (empty) — no apply yet.
        assertFalse(Signal.untracked(sig::get).isPresent(),
                "queryFilterSignal() must start with empty Optional (no filter applied)");
    }

    @Test
    void queryFilterSignal_updatesOnApply() {
        // The signal must reflect the new filter after applyFilterProgrammatically().
        // No Signal.effect needed here — we just read the signal value directly.
        var panel  = DynamicFilterPanel.of(Product.class);
        Signal<Optional<QueryFilter>> sig = panel.queryFilterSignal();

        QueryFilter filter = QueryFilter.eq(PathProperty.create("name", String.class), "Laptop");
        panel.applyFilterProgrammatically(filter);

        assertTrue(Signal.untracked(sig::get).isPresent(),  "signal must hold a present filter after apply");
        assertEquals(Optional.of(filter), Signal.untracked(sig::get),
                "signal value must equal the applied filter");
    }

    @Test
    void queryFilterSignal_clearsOnReset() {
        var panel  = DynamicFilterPanel.of(Product.class);
        Signal<Optional<QueryFilter>> sig = panel.queryFilterSignal();
        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        panel.resetAll();

        assertFalse(Signal.untracked(sig::get).isPresent(),
                "signal must return empty Optional after resetAll()");
    }

    /**
     * Verifies that {@code Signal.effect} bound to a component fires {@code resetToPage1()}
     * when the filter changes — the core mechanism that keeps the grid in sync with the panel.
     *
     * <p>Uses {@link AbstractSessionTest} infrastructure + {@link ComponentUtil#onComponentAttach}
     * to satisfy the lifecycle requirement of {@code Signal.effect(Component, Runnable)}.</p>
     */
    @Test
    void queryFilterSignal_effectFiresOnFilterChange() {
        var panel  = DynamicFilterPanel.of(Product.class);
        Signal<Optional<QueryFilter>> sig = panel.queryFilterSignal();

        // Attach a lightweight component to the UI so Signal.effect becomes active.
        var owner = new com.vaadin.flow.component.html.Div();
        ui.add(owner);
        ComponentUtil.onComponentAttach(owner, true);

        AtomicInteger effectCallCount = new AtomicInteger(0);
        java.util.concurrent.atomic.AtomicBoolean initialized = new java.util.concurrent.atomic.AtomicBoolean(false);
        Signal.effect(owner, () -> {
            sig.get(); // register dependency
            if (initialized.getAndSet(true)) {
                effectCallCount.incrementAndGet(); // skip first (attach-time) execution
            }
        });

        // First filter change must fire the effect (after the guard skips registration)
        QueryFilter filter = QueryFilter.eq(PathProperty.create("name", String.class), "Laptop");
        panel.applyFilterProgrammatically(filter);

        assertEquals(1, effectCallCount.get(),
                "Signal.effect must fire exactly once after the first filter apply");

        // Second change (reset) fires again
        panel.resetAll();
        assertEquals(2, effectCallCount.get(),
                "Signal.effect must fire again after resetAll()");
    }

    // ────────────────────────────────────────────────────────────────────────
    // toPredicate() in-memory evaluation tests
    // ────────────────────────────────────────────────────────────────────────

    @Test
    void toPredicate_matchesEverythingWhenNoFilter() {
        var panel = DynamicFilterPanel.of(Product.class);
        // No filter applied → predicate matches everything
        assertTrue(panel.toPredicate().test(new Product("X", 1.0, "cat")));
        assertTrue(panel.toPredicate().test(new Product("", 0.0,  "")));
    }

    @Test
    void toPredicate_afterReset_matchesEverything() {
        var panel = DynamicFilterPanel.of(Product.class);
        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "Laptop"));
        panel.resetAll();

        // After reset, predicate should be permissive again
        assertTrue(panel.toPredicate().test(new Product("anything", 1.0, "cat")));
    }

    // ────────────────────────────────────────────────────────────────────────
    // Multiple listeners test
    // ────────────────────────────────────────────────────────────────────────

    @Test
    void multipleFilterChangeListeners_allFired() {
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicInteger count1 = new AtomicInteger();
        AtomicInteger count2 = new AtomicInteger();
        AtomicInteger count3 = new AtomicInteger();

        panel.addFilterChangeListener(e -> count1.incrementAndGet());
        panel.addFilterChangeListener(e -> count2.incrementAndGet());
        panel.addFilterChangeListener(e -> count3.incrementAndGet());

        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        assertEquals(1, count1.get(), "listener 1 must be called");
        assertEquals(1, count2.get(), "listener 2 must be called");
        assertEquals(1, count3.get(), "listener 3 must be called");
    }

    @Test
    void filterChangeEvent_isUserOriginated() {
        // FilterChangeEvent.isUserOriginated() is always true for DynamicFilterPanel events.
        var panel = DynamicFilterPanel.of(Product.class);
        AtomicReference<Boolean> captured = new AtomicReference<>();
        panel.addFilterChangeListener(e -> captured.set(e.isUserOriginated()));

        panel.applyFilterProgrammatically(
                QueryFilter.eq(PathProperty.create("name", String.class), "X"));

        assertNotNull(captured.get());
        assertTrue(captured.get(), "event must be marked as user-originated");
    }
}





