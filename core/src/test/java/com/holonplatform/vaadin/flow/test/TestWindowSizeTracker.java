package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.iyensoft.vaadin.flow.utils.responsive.WindowSizeTracker;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.local.ValueSignal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWindowSizeTracker {

    // ── helpers ──────────────────────────────────────────────────────────────

    private static UI mockUiWithWindowSize(int width, int height) {
        UI ui = mock(UI.class);
        Page page = mock(Page.class);
        when(ui.getPage()).thenReturn(page);
        when(page.windowSizeSignal()).thenReturn(new ValueSignal<>(new WindowSize(width, height)));
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
}
