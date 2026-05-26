package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.AbstractIyenView;
import com.iyensoft.vaadin.flow.utils.responsive.IyenResponsiveLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.signals.local.ValueSignal;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestAbstractIyenViewModeSynchronization {

    @Test
    void synchronizeCurrentModeUsesWindowSizeSignalWhenLayoutModeIsMissing() throws Exception {
        TestView view = new TestView();
        IyenResponsiveLayout layout = mock(IyenResponsiveLayout.class);
        when(layout.getCurrentMode()).thenReturn(null);
        setLayout(view, layout);

        AttachEvent attachEvent = mockAttachEventWithWindowSize(1280, 900);
        invokeSynchronizeCurrentMode(view, attachEvent);

        assertEquals(ViewMode.DESKTOP, view.currentViewMode());
    }

    @Test
    void synchronizeCurrentModePrefersLayoutModeOverWindowSizeFallback() throws Exception {
        TestView view = new TestView();
        IyenResponsiveLayout layout = mock(IyenResponsiveLayout.class);
        when(layout.getCurrentMode()).thenReturn(ViewMode.TABLET);
        setLayout(view, layout);

        AttachEvent attachEvent = mockAttachEventWithWindowSize(1600, 768);
        invokeSynchronizeCurrentMode(view, attachEvent);

        assertEquals(ViewMode.TABLET, view.currentViewMode());
    }

    @Test
    void abstractIyenViewReferencesExpectedStyleSheet() {
        StyleSheet styleSheet = AbstractIyenView.class.getAnnotation(StyleSheet.class);
        assertNotNull(styleSheet);
        assertEquals("context://master-details.css", styleSheet.value());
    }

    @Test
    void masterDetailsCssResourceIsAvailableOnClasspath() {
        URL css = AbstractIyenView.class.getClassLoader().getResource("META-INF/resources/master-details.css");
        assertNotNull(css, "Expected META-INF/resources/master-details.css to be present on classpath");
    }

    private static AttachEvent mockAttachEventWithWindowSize(int width, int height) {
        AttachEvent attachEvent = mock(AttachEvent.class);
        UI ui = mock(UI.class);
        Page page = mock(Page.class);

        when(attachEvent.getUI()).thenReturn(ui);
        when(ui.getPage()).thenReturn(page);
        when(page.windowSizeSignal()).thenReturn(new ValueSignal<>(new WindowSize(width, height)));

        return attachEvent;
    }

    private static void setLayout(AbstractIyenView view, IyenResponsiveLayout layout) throws Exception {
        Field field = AbstractIyenView.class.getDeclaredField("iyenResponsiveLayout");
        field.setAccessible(true);
        field.set(view, layout);
    }

    private static void invokeSynchronizeCurrentMode(AbstractIyenView view, AttachEvent attachEvent) throws Exception {
        Method method = AbstractIyenView.class.getDeclaredMethod("synchronizeCurrentMode", AttachEvent.class);
        method.setAccessible(true);
        method.invoke(view, attachEvent);
    }

    private static final class TestView extends AbstractIyenView {

        @Override
        public IyenMasterBuilder master() {
            return IyenMasterBuilder.create();
        }

        @Override
        public IyenDetailBuilder detail() {
            return IyenDetailBuilder.create();
        }

        ViewMode currentViewMode() {
            return getViewMode();
        }
    }
}



