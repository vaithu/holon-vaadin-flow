package com.iyensoft.vaadin.flow.test;

import com.holonplatform.vaadin.flow.test.AbstractSessionTest;

import com.iyensoft.vaadin.flow.components.AppBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.PageTestSupport;
import com.vaadin.flow.component.popover.Popover;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link AppBar}.
 *
 * <p>{@code AppBar} is responsive: only the start slot is in the DOM at construction time. The
 * middle and end slots are added on first attach, and only when the viewport is a desktop one — on
 * mobile the end slot is moved into a popover behind a "more actions" button instead. These tests
 * therefore drive a real attach with an explicit viewport rather than asserting on a freshly
 * constructed, never-attached instance.</p>
 */
class TestAppBar extends AbstractSessionTest {

    private static final int DESKTOP_WIDTH = 1280;
    private static final int DESKTOP_HEIGHT = 900;
    private static final int MOBILE_WIDTH = 375;
    private static final int MOBILE_HEIGHT = 812;

    /** Attaches {@code appBar} to the test UI with the given viewport reported by the client. */
    private AppBar attachWithViewport(AppBar appBar, int width, int height) {
        PageTestSupport.setWindowSize(ui.getPage(), width, height);
        ui.add(appBar);
        return appBar;
    }

    private static List<String> slotClassNames(AppBar appBar) {
        return appBar.getChildren()
                .map(c -> c.getClassNames().stream()
                        .filter(n -> n.startsWith("app-bar__"))
                        .findFirst()
                        .orElse(""))
                .filter(n -> !n.isEmpty())
                .toList();
    }

    // ── structure ─────────────────────────────────────────────────────────────

    @Test
    void testDefaultConstructor() {
        var appBar = new AppBar();
        assertTrue(appBar.getClassNames().contains("app-bar"));
        assertEquals("banner", appBar.getElement().getAttribute("role"));
    }

    @Test
    void onlyTheStartSlotIsPresentBeforeAttach() {
        var appBar = new AppBar();
        // The middle/end slots are attach-time, viewport-dependent decisions.
        assertEquals(List.of("app-bar__start"), slotClassNames(appBar));
    }

    @Test
    void testThreeSlotStructureOnDesktop() {
        var appBar = attachWithViewport(new AppBar(), DESKTOP_WIDTH, DESKTOP_HEIGHT);

        assertEquals(List.of("app-bar__start", "app-bar__middle", "app-bar__end"),
                slotClassNames(appBar));
    }

    @Test
    void testConstructorWithComponents() {
        var logo = new Span("Logo");
        var appBar = attachWithViewport(new AppBar(logo), DESKTOP_WIDTH, DESKTOP_HEIGHT);

        assertTrue(appBar.getClassNames().contains("app-bar"));
        assertEquals(3, appBar.getComponentCount(), "start, middle, end");

        Component startSlot = appBar.getChildren().findFirst().orElseThrow();
        assertEquals(List.of(logo), startSlot.getChildren().toList(), "logo goes into the start slot");
    }

    @Test
    void mobileCollapsesTheEndSlotIntoAPopover() {
        var action = new Span("Action");
        var appBar = new AppBar();
        appBar.addToEnd(action);
        attachWithViewport(appBar, MOBILE_WIDTH, MOBILE_HEIGHT);

        assertTrue(appBar.getChildren().anyMatch(Button.class::isInstance),
                "a 'more actions' button should be rendered on mobile");

        Popover popover = appBar.getChildren()
                .filter(Popover.class::isInstance)
                .map(Popover.class::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("the end slot should be hosted in a popover"));

        assertTrue(popover.getChildren()
                        .anyMatch(slot -> slot.getChildren().anyMatch(action::equals)),
                "the end-slot content should have moved into the popover");
        assertFalse(slotClassNames(appBar).contains("app-bar__middle"),
                "the middle slot is not rendered on mobile");
    }

    // ── slot content ──────────────────────────────────────────────────────────

    @Test
    void testAddToStart() {
        var appBar = new AppBar();
        var comp = new Span("Start");
        appBar.addToStart(comp);
        attachWithViewport(appBar, DESKTOP_WIDTH, DESKTOP_HEIGHT);

        assertEquals(List.of(comp), appBar.getChildren().findFirst().orElseThrow().getChildren().toList());
    }

    @Test
    void testAddToMiddle() {
        var appBar = new AppBar();
        var comp = new Span("Middle");
        appBar.addToMiddle(comp);
        attachWithViewport(appBar, DESKTOP_WIDTH, DESKTOP_HEIGHT);

        assertEquals(List.of(comp), appBar.getChildren().toList().get(1).getChildren().toList());
    }

    @Test
    void testAddToEnd() {
        var appBar = new AppBar();
        var comp = new Span("End");
        appBar.addToEnd(comp);
        attachWithViewport(appBar, DESKTOP_WIDTH, DESKTOP_HEIGHT);

        assertEquals(List.of(comp), appBar.getChildren().toList().get(2).getChildren().toList());
    }

    @Test
    void testAddToEndAtIndex() {
        var appBar = new AppBar();
        var first = new Span("First");
        var second = new Span("Second");
        appBar.addToEnd(first);
        appBar.addToEnd(0, second);
        attachWithViewport(appBar, DESKTOP_WIDTH, DESKTOP_HEIGHT);

        assertEquals(List.of(second, first), appBar.getChildren().toList().get(2).getChildren().toList(),
                "index 0 inserts at the leftmost position of the end slot");
    }

    @Test
    void testAddToBottom() {
        var appBar = new AppBar();
        var comp = new Span("Bottom");
        appBar.addToBottom(comp);

        assertTrue(appBar.getChildren()
                        .anyMatch(c -> c.getClassNames().contains("app-bar__bottom")),
                "the bottom slot is created on demand");
    }

    @Test
    void testWidthFull() {
        var appBar = new AppBar();
        assertEquals("100%", appBar.getWidth());
    }
}
