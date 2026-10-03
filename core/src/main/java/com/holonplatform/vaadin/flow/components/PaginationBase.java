package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.UnorderedList;

import java.util.Objects;

/**
 * Semantic pagination landmark shared by core and higher-level pagination controls.
 */
public class PaginationBase extends Nav {

    private final UnorderedList content;

    public PaginationBase() {
        this(new UnorderedList());
    }

    protected PaginationBase(UnorderedList content) {
        this.content = Objects.requireNonNull(content, "content");
        addClassName("pagination");
        getElement().setAttribute("role", "navigation");
        getElement().setAttribute("aria-label",
                LocalizationProvider.localize("Page navigation", "pagination.aria_label"));
        add(content);
    }

    public UnorderedList getContent() {
        return content;
    }
}
