package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.dom.Element;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.vaadin.flow.function.SerializableFunction;

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
public final class UrlSelectionSync<T> implements java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /** Query-parameter name used when none is configured explicitly. */
    public static final String DEFAULT_PARAM_NAME = "id";

    private final SerializableFunction<T, String> idExtractor;
    private final SerializableFunction<String, Optional<T>> itemLoader;
    private final String paramName;

    /** Use {@link #builder()} instead. */
    UrlSelectionSync(SerializableFunction<T, String> idExtractor,
                     SerializableFunction<String, Optional<T>> itemLoader,
                     String paramName) {
        this.idExtractor = idExtractor;
        this.itemLoader  = itemLoader;
        this.paramName   = paramName == null || paramName.isBlank() ? DEFAULT_PARAM_NAME : paramName;
    }

    /** @return the query-parameter name carrying the selected id (defaults to {@value #DEFAULT_PARAM_NAME}). */
    public String getParamName() {
        return paramName;
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

        private SerializableFunction<T, String> idExtractor;
        private SerializableFunction<String, Optional<T>> itemLoader;
        private String paramName = DEFAULT_PARAM_NAME;

        private Builder() {}

        /** Converts an item to its URL-safe string ID. */
        public Builder<T> idExtractor(SerializableFunction<T, String> idExtractor) {
            this.idExtractor = idExtractor;
            return this;
        }

        /** Loads an item by its string ID, returning {@link Optional#empty()} if not found. */
        public Builder<T> itemLoader(SerializableFunction<String, Optional<T>> itemLoader) {
            this.itemLoader = itemLoader;
            return this;
        }

        /** Overrides the query-parameter name (defaults to {@value #DEFAULT_PARAM_NAME}). */
        public Builder<T> paramName(String paramName) {
            this.paramName = paramName;
            return this;
        }

        public UrlSelectionSync<T> build() {
            return new UrlSelectionSync<>(idExtractor, itemLoader, paramName);
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
        host.executeJs("history.replaceState(null, '', location.pathname + '?' + $1 + '=' + $0)", idStr, paramName);
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
     * Looks up an item by ID.
     * Returns {@link Optional#empty()} if no loader was configured, the id is blank,
     * or the loader found nothing.
     */
    public Optional<T> load(String idStr) {
        if (itemLoader == null || idStr == null || idStr.isBlank()) return Optional.empty();
        return itemLoader.apply(idStr);
    }

    /**
     * Looks up an item by ID and forwards it to {@code selector} if found.
     * No-op if {@code itemLoader} was not configured or {@code idStr} is blank.
     * Always runs regardless of viewport — restoring deep-link state is device-agnostic.
     */
    public void restore(String idStr, Consumer<T> selector) {
        load(idStr).ifPresent(selector);
    }

    private static boolean isMobile(ViewMode mode) {
        return mode != null && mode.isMobile();
    }
}

