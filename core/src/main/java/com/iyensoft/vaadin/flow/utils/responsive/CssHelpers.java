package com.iyensoft.vaadin.flow.utils.responsive;

import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CssHelpers {

    private CssHelpers() {}

    public static void addResponsiveClasses(Component c, Map<ViewMode, List<String>> byMode) {
        if (c == null || byMode == null || byMode.isEmpty()) return;

        byMode.forEach((mode, list) -> {
            if (mode == null || list == null) return;
            list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(mode::toCssClass) // "sm:...", "md:...", etc.
                .forEach(c::addClassName);
        });
    }
}