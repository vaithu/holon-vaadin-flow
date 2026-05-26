package com.holonplatform.vaadin.flow.internal.lumo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Breakpoint {
    SMALL("sm"),
    MEDIUM("md"),
    LARGE("lg"),
    XLARGE("xl"),
    XXLARGE("2xl");

    private final String prefix;

    Breakpoint(String prefix) {
        this.prefix = prefix;
    }

    /** Returns the short prefix: "sm", "md", "lg", "xl", "2xl". */
    public String getPrefix() {
        return prefix;
    }

    public FlexRowBreakpoint getFlexRowBreakpoint() {
        return FlexRowBreakpoint.valueOf(this.name());
    }

    // ── O(1) reverse lookup map ────────────────────────────────────────────────
    // Avoids values() array allocation on every fromPrefixOrNull call.
    private static final Map<String, Breakpoint> BY_PREFIX;
    static {
        Map<String, Breakpoint> map = new HashMap<>(5);
        for (Breakpoint bp : values()) {
            map.put(bp.prefix, bp);
        }
        BY_PREFIX = Collections.unmodifiableMap(map);
    }

    /**
     * O(1) lenient reverse lookup by prefix.
     * @param prefix "sm", "md", "lg", "xl", or "2xl" (case-insensitive; trims whitespace)
     * @return the matching Breakpoint, or null if no match is found
     */
    public static Breakpoint fromPrefixOrNull(String prefix) {
        if (prefix == null) return null;
        return BY_PREFIX.get(prefix.trim().toLowerCase());
    }

    /**
     * Strict reverse lookup by prefix.
     * @param prefix "sm", "md", "lg", "xl", or "2xl"
     * @return the matching Breakpoint
     * @throws IllegalArgumentException if no match is found
     */
    public static Breakpoint fromPrefix(String prefix) {
        Breakpoint bp = fromPrefixOrNull(prefix);
        if (bp == null) {
            throw new IllegalArgumentException("Unknown Breakpoint prefix: " + prefix);
        }
        return bp;
    }

    /**
     * Optional-returning helper if you prefer not to throw.
     */
    public static Optional<Breakpoint> fromPrefixOptional(String prefix) {
        return Optional.ofNullable(fromPrefixOrNull(prefix));
    }
}
