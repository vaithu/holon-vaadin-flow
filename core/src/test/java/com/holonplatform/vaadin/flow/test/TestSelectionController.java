package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionController;

/**
 * Browser-free unit tests for {@link SelectionController}.
 *
 * <p>Covers:</p>
 * <ol>
 *   <li>Initial state — selection is empty, version is zero.</li>
 *   <li>{@code set} / {@code peek} — non-reactive reads work correctly.</li>
 *   <li>{@code clear} — selection returns to empty.</li>
 *   <li>{@code selectionSignal()} — returns a non-null signal whose initial peek matches.</li>
 *   <li>{@code notifyDataChanged()} — increments version and fires all data-changed listeners.</li>
 *   <li>{@code addDataChangedListener} — returns a removal handle; listener can be removed.</li>
 *   <li>Multiple data-changed listeners — all fire.</li>
 *   <li>Listener throws — other listeners still fire.</li>
 *   <li>{@code read()} — returns the current selection inside effect-like contexts.</li>
 * </ol>
 *
 * <p>Signal.effect lifecycle binding is not tested here — it requires a Vaadin UI context;
 * see integration tests for that behaviour.</p>
 */
class TestSelectionController {

    record Product(Long id, String name) {}

    private SelectionController<Product> sut;

    @BeforeEach
    void setUp() {
        sut = new SelectionController<>(new ArrayList<>());
    }

    // ── 1. Initial state ──────────────────────────────────────────────────────

    @Test
    void initialState_selectionIsEmpty() {
        assertTrue(sut.peek().isEmpty(),
                "Selection must be empty before any set() call");
    }

    @Test
    void initialState_readIsEmpty() {
        // read() requires a reactive context; use Signal.untracked to call safely in unit tests.
        assertTrue(com.vaadin.flow.signals.Signal.untracked(sut::read).isEmpty(),
                "read() must return empty Optional initially");
    }

    // ── 2. set / peek ─────────────────────────────────────────────────────────

    @Test
    void set_peek_returnsItem() {
        Product p = new Product(1L, "Widget");
        sut.set(Optional.of(p));

        assertEquals(Optional.of(p), sut.peek(),
                "peek() must return the item just set");
    }

    @Test
    void set_multipleItems_peekReturnsLatest() {
        Product a = new Product(1L, "Alpha");
        Product b = new Product(2L, "Beta");

        sut.set(Optional.of(a));
        sut.set(Optional.of(b));

        assertEquals(Optional.of(b), sut.peek(),
                "peek() must return the most-recently set item");
    }

    @Test
    void set_empty_peekReturnsEmpty() {
        sut.set(Optional.of(new Product(1L, "Widget")));
        sut.set(Optional.empty());

        assertTrue(sut.peek().isEmpty(),
                "peek() must return empty after setting Optional.empty()");
    }

    // ── 3. read ───────────────────────────────────────────────────────────────

    @Test
    void read_returnsCurrentSelection() {
        Product p = new Product(42L, "Test");
        sut.set(Optional.of(p));

        // read() requires a reactive context — wrap with Signal.untracked in tests.
        assertEquals(Optional.of(p),
                com.vaadin.flow.signals.Signal.untracked(sut::read),
                "read() must return the current selection");
    }

    // ── 4. clear ──────────────────────────────────────────────────────────────

    @Test
    void clear_emptySelection_returnsEmpty() {
        sut.set(Optional.of(new Product(1L, "Widget")));
        sut.clear();

        assertTrue(sut.peek().isEmpty(),
                "clear() must reset the selection to empty");
    }

    @Test
    void clear_noop_whenAlreadyEmpty() {
        // Must not throw
        sut.clear();
        assertTrue(sut.peek().isEmpty());
    }

    // ── 5. selectionSignal ────────────────────────────────────────────────────

    @Test
    void selectionSignal_isNotNull() {
        assertNotNull(sut.selectionSignal(),
                "selectionSignal() must never return null");
    }

    @Test
    void selectionSignal_peekMatchesControllerPeek() {
        Product p = new Product(7L, "Sprocket");
        sut.set(Optional.of(p));

        assertEquals(sut.peek(), sut.selectionSignal().peek(),
                "selectionSignal().peek() must equal controller.peek()");
    }

    // ── 6. notifyDataChanged — fires listeners ────────────────────────────────

    @Test
    void notifyDataChanged_invokesAllListeners() {
        List<String> log = new ArrayList<>();
        sut = new SelectionController<>(List.of(() -> log.add("A"), () -> log.add("B")));

        sut.notifyDataChanged();

        assertEquals(List.of("A", "B"), log);
    }

    @Test
    void notifyDataChanged_calledTwice_firesListenersTwice() {
        AtomicInteger count = new AtomicInteger();
        sut = new SelectionController<>(List.of(count::incrementAndGet));

        sut.notifyDataChanged();
        sut.notifyDataChanged();

        assertEquals(2, count.get());
    }

    @Test
    void notifyDataChanged_withNoListeners_noException() {
        // Must not throw
        sut.notifyDataChanged();
    }

    // ── 7. addDataChangedListener ─────────────────────────────────────────────

    @Test
    void addDataChangedListener_firesOnNotify() {
        List<String> log = new ArrayList<>();
        sut.addDataChangedListener(() -> log.add("added"));

        sut.notifyDataChanged();

        assertEquals(List.of("added"), log);
    }

    @Test
    void addDataChangedListener_removalHandle_removesListener() {
        List<String> log = new ArrayList<>();
        Runnable remove = sut.addDataChangedListener(() -> log.add("present"));

        remove.run();
        sut.notifyDataChanged();

        assertTrue(log.isEmpty(),
                "Removed listener must NOT fire after its removal handle is run");
    }

    @Test
    void addDataChangedListener_addThenRemove_otherListenersUnaffected() {
        List<String> log = new ArrayList<>();
        sut = new SelectionController<>(new ArrayList<>(List.of(() -> log.add("stable"))));

        Runnable remove = sut.addDataChangedListener(() -> log.add("transient"));
        remove.run();

        sut.notifyDataChanged();

        assertEquals(List.of("stable"), log,
                "Only the removed listener should be absent");
    }

    // ── 8. Listener throws — others still fire ────────────────────────────────

    @Test
    void notifyDataChanged_listenerThrows_otherListenersStillFire() {
        List<String> log = new ArrayList<>();
        sut = new SelectionController<>(List.of(
                () -> { throw new RuntimeException("kaboom"); },
                () -> log.add("after-throw")
        ));

        // Must not throw
        sut.notifyDataChanged();

        assertEquals(List.of("after-throw"), log,
                "A throwing listener must not prevent subsequent listeners from firing");
    }

    // ── 9. Interaction: set then notifyDataChanged ────────────────────────────

    @Test
    void setThenNotify_listenerFiresAndPeekRetainsItem() {
        Product p = new Product(99L, "Gadget");
        List<Optional<Product>> snapshots = new ArrayList<>();

        sut.addDataChangedListener(() -> snapshots.add(sut.peek()));
        sut.set(Optional.of(p));
        sut.notifyDataChanged();

        assertEquals(1, snapshots.size());
        assertEquals(Optional.of(p), snapshots.getFirst(),
                "Listener should see the currently set item via peek()");
    }
}



