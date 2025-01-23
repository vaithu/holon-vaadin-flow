package com.holonplatform.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.shared.Registration;
/**
 * Mock of a ResizeObservable interface that could e.g. be merged with {@link com.vaadin.flow.component.HasSize} in the
 * future (and bring in ResizeObserver as an internal helper to Flow).
 */
public interface ResizeObservable extends HasSize {
    default Registration addResizeListener(ComponentEventListener<ResizeObserver.SizeChangeEvent> listener) {
        return ResizeObserver.get().addResizeListener((Component) this, listener);
    };
}