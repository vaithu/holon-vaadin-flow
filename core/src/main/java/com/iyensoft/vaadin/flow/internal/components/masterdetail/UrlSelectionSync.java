package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.vaadin.flow.dom.Element;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Synchronises the master-detail selection with the {@code ?id=} URL query parameter
 * via {@code history.replaceState} (no Vaadin navigation round-trip).
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

    public UrlSelectionSync(Function<T, String> idExtractor,
                            Function<String, Optional<T>> itemLoader) {
        this.idExtractor = idExtractor;
        this.itemLoader  = itemLoader;
    }

    /** {@code true} if URL push/restore is configured. */
    public boolean isEnabled() {
        return idExtractor != null;
    }

    /** Writes {@code ?id=<encoded-id>} to the current URL. No-op if disabled. */
    public void pushId(Element host, T item) {
        if (idExtractor == null || item == null) return;
        String idStr = idExtractor.apply(item);
        if (idStr == null || idStr.isBlank()) return;
        host.executeJs("history.replaceState(null, '', location.pathname + '?id=' + $0)", idStr);
    }

    /** Removes the {@code ?id=} parameter from the current URL. */
    public void clearId(Element host) {
        host.executeJs("history.replaceState(null, '', location.pathname)");
    }

    /**
     * Looks up an item by ID and forwards it to {@code selector} if found.
     * No-op if {@code itemLoader} was not configured or {@code idStr} is blank.
     */
    public void restore(String idStr, Consumer<T> selector) {
        if (itemLoader == null || idStr == null || idStr.isBlank()) return;
        itemLoader.apply(idStr).ifPresent(selector);
    }
}

