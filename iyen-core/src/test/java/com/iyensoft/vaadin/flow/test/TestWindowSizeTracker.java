package com.iyensoft.vaadin.flow.test;

import com.holonplatform.vaadin.flow.test.AbstractSessionTest;

import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.iyensoft.vaadin.flow.utils.responsive.WindowSizeTracker;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.UIInternals;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.component.screenorientation.ScreenOrientationData;
import com.vaadin.flow.component.screenorientation.ScreenOrientationType;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.local.ValueSignal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link WindowSizeTracker}.
 *
 * <p>Extends {@link AbstractSessionTest} because {@code track} subscribes via
 * {@code Signal.effect(owner, …)}, and component-scoped signal effects only run inside a real
 * Vaadin session context. Without that fixture the effect is registered but never executed, so
 * every "consumer is called" assertion would fail regardless of the tracker being correct.</p>
 */
class TestWindowSizeTracker extends AbstractSessionTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Builds a UI mock that reports the given viewport size.
     *
     * <p>{@code WindowSizeTracker.track} also reads the Screen Orientation signal, which
     * {@code ScreenOrientation.orientationSignal(ui)} obtains via {@code ui.getInternals()}. A bare
     * {@code mock(UI.class)} returns {@code null} there, so {@link UIInternals} has to be stubbed
     * too. It is seeded with {@link ScreenOrientationType#UNKNOWN} — the same value Vaadin itself
     * uses before the client reports a real orientation — which makes {@code getViewMode} fall back
     * to the width/height heuristic these tests assert on.</p>
     */
    private static UI mockUiWithWindowSize(int width, int height) {
        return mockUiWithWindowSize(width, height, ScreenOrientationType.UNKNOWN);
    }

    private static UI mockUiWithWindowSize(int width, int height, ScreenOrientationType orientation) {
        UI ui = mock(UI.class);
        Page page = mock(Page.class);
        when(ui.getPage()).thenReturn(page);
        when(page.windowSizeSignal()).thenReturn(new ValueSignal<>(new WindowSize(width, height)));

        UIInternals internals = mock(UIInternals.class);
        when(ui.getInternals()).thenReturn(internals);
        when(internals.getScreenOrientationSignalReadOnly())
                .thenReturn(new ValueSignal<>(new ScreenOrientationData(orientation, 0)));
        return ui;
    }

    // ── positive tests ────────────────────────────────────────────────────────

    @Test
    void trackCallsConsumerImmediatelyWithDesktopMode() {
        UI ui = mockUiWithWindowSize(1280, 900);
        List<ViewMode> received = new ArrayList<>();

        WindowSizeTracker.track(ui, new Div(), received::add);

        assertEquals(1, received.size());
        assertEquals(ViewMode.DESKTOP, received.getFirst());
    }

    @Test
    void trackCallsConsumerImmediatelyWithTabletMode() {
        UI ui = mockUiWithWindowSize(768, 1024);
        List<ViewMode> received = new ArrayList<>();

        WindowSizeTracker.track(ui, new Div(), received::add);

        assertEquals(1, received.size());
        assertEquals(ViewMode.TABLET, received.getFirst());
    }

    @Test
    void trackCallsConsumerImmediatelyWithMobilePortraitMode() {
        UI ui = mockUiWithWindowSize(375, 812);
        List<ViewMode> received = new ArrayList<>();

        WindowSizeTracker.track(ui, new Div(), received::add);

        assertEquals(1, received.size());
        assertEquals(ViewMode.MOBILE_PORTRAIT, received.getFirst());
    }

    @Test
    void trackCallsConsumerImmediatelyWithMobileLandscapeMode() {
        // width=600 falls exactly on the boundary (<= 600) and width > height → landscape
        UI ui = mockUiWithWindowSize(600, 375);
        List<ViewMode> received = new ArrayList<>();

        WindowSizeTracker.track(ui, new Div(), received::add);

        assertEquals(1, received.size());
        assertEquals(ViewMode.MOBILE_LANDSCAPE, received.getFirst());
    }

    @Test
    void trackReturnsNonNullRegistration() {
        UI ui = mockUiWithWindowSize(1280, 900);

        Registration reg = WindowSizeTracker.track(ui, new Div(), mode -> {});

        assertNotNull(reg);
    }

    @Test
    void trackRegistrationCanBeRemovedWithoutError() {
        UI ui = mockUiWithWindowSize(1280, 900);

        Registration reg = WindowSizeTracker.track(ui, new Div(), mode -> {});

        assertDoesNotThrow(reg::remove);
    }

    // ── negative tests ────────────────────────────────────────────────────────

    @Test
    void trackWithZeroDimensionsDoesNotThrow() {
        UI ui = mockUiWithWindowSize(0, 0);
        List<ViewMode> received = new ArrayList<>();

        assertDoesNotThrow(() -> WindowSizeTracker.track(ui, new Div(), received::add));
        assertFalse(received.isEmpty(), "Consumer should still be called with zero-size viewport");
    }

    // ── orientation ───────────────────────────────────────────────────────────

    @Test
    void realOrientationOverridesTheWidthHeightHeuristic() {
        // 375x812 looks like portrait by the width/height heuristic, but the browser reports
        // landscape — the reported orientation must win.
        UI ui = mockUiWithWindowSize(375, 812, ScreenOrientationType.LANDSCAPE_PRIMARY);
        List<ViewMode> received = new ArrayList<>();

        WindowSizeTracker.track(ui, new Div(), received::add);

        assertEquals(1, received.size());
        assertEquals(ViewMode.MOBILE_LANDSCAPE, received.getFirst());
    }

    @Test
    void unsupportedOrientationFallsBackToTheWidthHeightHeuristic() {
        UI ui = mockUiWithWindowSize(375, 812, ScreenOrientationType.UNSUPPORTED);
        List<ViewMode> received = new ArrayList<>();

        WindowSizeTracker.track(ui, new Div(), received::add);

        assertEquals(1, received.size());
        assertEquals(ViewMode.MOBILE_PORTRAIT, received.getFirst());
    }
}
