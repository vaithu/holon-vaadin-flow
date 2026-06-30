package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.dom.Element;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Synchronises the master-detail selection with the {@code ?id=} URL query parameter
 * via {@code history.replaceState} (no Vaadin navigation round-trip).
 *
 * <p><strong>Desktop only.</strong> {@link #pushId} and {@link #clearId} are no-ops
 * when the current {@link ViewMode} is mobile — the URL is left untouched so the
 * bottom-sheet interaction does not pollute browser history on small screens.</p>
 *
 * <p>Stateless aside from the two configured functions — safe to share across UIs.
 * If {@code idExtractor} or {@code itemLoader} is {@code null}, the corresponding
 * push/restore call is a no-op, so callers can wire it unconditionally.</p>
 *
 * @param <T> the item type
 */
public final class UrlSelectionSync<T> {

    private final Function<T, String> idExtractor;
    private final Function<String, Optional<T>> itemLoader;

    /** Use {@link #builder()} instead. */
    UrlSelectionSync(Function<T, String> idExtractor,
                     Function<String, Optional<T>> itemLoader) {
        this.idExtractor = idExtractor;
        this.itemLoader  = itemLoader;
    }

    /** Returns a new {@link Builder} for this type. */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Fluent builder for {@link UrlSelectionSync}.
     *
     * @param <T> the item type
     */
    public static final class Builder<T> {

        private Function<T, String> idExtractor;
        private Function<String, Optional<T>> itemLoader;

        private Builder() {}

        /** Converts an item to its URL-safe string ID. */
        public Builder<T> idExtractor(Function<T, String> idExtractor) {
            this.idExtractor = idExtractor;
            return this;
        }

        /** Loads an item by its string ID, returning {@link Optional#empty()} if not found. */
        public Builder<T> itemLoader(Function<String, Optional<T>> itemLoader) {
            this.itemLoader = itemLoader;
            return this;
        }

        public UrlSelectionSync<T> build() {
            return new UrlSelectionSync<>(idExtractor, itemLoader);
        }
    }

    /** {@code true} if URL push/restore is configured. */
    public boolean isEnabled() {
        return idExtractor != null;
    }

    /**
     * Writes {@code ?id=<encoded-id>} to the current URL.
     * No-op if disabled or if {@code mode} is a mobile viewport.
     */
    public void pushId(Element host, T item, ViewMode mode) {
        Objects.requireNonNull(host, "host must not be null");
        if (isMobile(mode) || idExtractor == null || item == null) return;
        String idStr = idExtractor.apply(item);
        if (idStr == null || idStr.isBlank()) return;
        host.executeJs("history.replaceState(null, '', location.pathname + '?id=' + $0)", idStr);
    }

    /**
     * Removes the {@code ?id=} parameter from the current URL.
     * No-op if {@code mode} is a mobile viewport.
     */
    public void clearId(Element host, ViewMode mode) {
        Objects.requireNonNull(host, "host must not be null");
        if (isMobile(mode)) return;
        host.executeJs("history.replaceState(null, '', location.pathname)");
    }

    /**
     * Looks up an item by ID and forwards it to {@code selector} if found.
     * No-op if {@code itemLoader} was not configured or {@code idStr} is blank.
     * Always runs regardless of viewport — restoring deep-link state is device-agnostic.
     */
    public void restore(String idStr, Consumer<T> selector) {
        if (itemLoader == null || idStr == null || idStr.isBlank()) return;
        itemLoader.apply(idStr).ifPresent(selector);
    }

    private static boolean isMobile(ViewMode mode) {
        return mode != null && mode.isMobile();
    }
}

