package com.holonplatform.vaadin.flow.components.utils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Small reusable holder for the current selection in master-detail style screens.
 *
 * <p>The controller intentionally stays UI-agnostic: it only tracks the current item
 * and exposes a callback hook for data-change notifications.</p>
 *
 * @param <T> selected item type
 */
public final class SelectionController<T> {

    private final AtomicReference<T> selected = new AtomicReference<>();
    private Runnable dataChangedListener = () -> { };

    private SelectionController() {
    }

    public static <T> SelectionController<T> create() {
        return new SelectionController<>();
    }

    public T getSelected() {
        return selected.get();
    }

    public void setSelected(T item) {
        selected.set(item);
    }

    public boolean hasSelection() {
        return selected.get() != null;
    }

    public void clear() {
        selected.set(null);
    }

    public void refreshItem(T item) {
        selected.set(item);
    }

    public SelectionController<T> onDataChanged(Runnable listener) {
        dataChangedListener = listener != null ? listener : () -> { };
        return this;
    }

    public void notifyDataChanged() {
        dataChangedListener.run();
    }
}