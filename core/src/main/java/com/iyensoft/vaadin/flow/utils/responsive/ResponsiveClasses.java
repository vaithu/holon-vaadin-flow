package com.iyensoft.vaadin.flow.utils.responsive;

import java.util.*;

/**
 * Fluent builder to configure responsive base classes per ViewMode.
 * Produces an immutable Map<ViewMode, List<String>> suitable for Responsive.apply(...).
 */
public final class ResponsiveClasses {

    private final Map<ViewMode, List<String>> classesByMode;

    private ResponsiveClasses(Map<ViewMode, List<String>> classesByMode) {
        // Defensive copy + immutable
        Map<ViewMode, List<String>> copy = new EnumMap<>(ViewMode.class);
        classesByMode.forEach((k, v) -> copy.put(k, List.copyOf(v)));
        this.classesByMode = Collections.unmodifiableMap(copy);
    }

    /** Returns an immutable map of configured classes per mode. */
    public Map<ViewMode, List<String>> toMap() {
        return classesByMode;
    }

    /** Start a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    /** Convenience to start and set first mode in one call. */
    public static Builder forMode(ViewMode mode) {
        return new Builder().forMode(mode);
    }

    public static final class Builder {
        private final EnumMap<ViewMode, LinkedHashSet<String>> map = new EnumMap<>(ViewMode.class);
        private ViewMode current;

        /** Select/activate the mode you want to add classes for. */
        public Builder forMode(ViewMode mode) {
            Objects.requireNonNull(mode, "mode");
            this.current = mode;
            map.computeIfAbsent(mode, k -> new LinkedHashSet<>());
            return this;
        }

        /** Alias of forMode(mode) for fluent “and(nextMode)” chaining. */
        public Builder and(ViewMode mode) {
            return forMode(mode);
        }

        /** Add one or more base classes to the current mode (e.g., "flex-row", "gap-x-m"). */
        public Builder add(String... baseClasses) {
            if (current == null) {
                throw new IllegalStateException("No mode selected. Call forMode(...) or and(...) first.");
            }
            if (baseClasses == null) return this;
            LinkedHashSet<String> set = map.get(current);
            for (String s : baseClasses) {
                if (s == null) continue;
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) set.add(trimmed);
            }
            return this;
        }

        /** Add a collection of base classes to the current mode. */
        public Builder addAll(Collection<String> baseClasses) {
            if (baseClasses == null || baseClasses.isEmpty()) return this;
            return add(baseClasses.toArray(String[]::new));
        }

        /** Remove specific classes from a given mode. */
        public Builder remove(ViewMode mode, String... baseClasses) {
            if (mode == null || baseClasses == null) return this;
            LinkedHashSet<String> set = map.get(mode);
            if (set == null) return this;
            for (String s : baseClasses) {
                if (s == null) continue;
                set.remove(s.trim());
            }
            return this;
        }

        /** Clear all classes registered for a given mode. */
        public Builder clear(ViewMode mode) {
            if (mode != null) map.remove(mode);
            return this;
        }

        /** Build an immutable ResponsiveClasses config. */
        public ResponsiveClasses build() {
            Map<ViewMode, List<String>> out = new EnumMap<>(ViewMode.class);
            for (Map.Entry<ViewMode, LinkedHashSet<String>> e : map.entrySet()) {
                if (e.getValue() == null || e.getValue().isEmpty()) continue;
                // Preserve insertion order, remove blanks, distinct is implicit via Set
                List<String> list = e.getValue().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                if (!list.isEmpty()) {
                    out.put(e.getKey(), list);
                }
            }
            return new ResponsiveClasses(out);
        }
    }
}