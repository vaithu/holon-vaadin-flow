package com.iyensoft.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

import java.util.ArrayList;
import java.util.List;

@StyleSheet("context://panel.css")
public class Panel extends Div {

    private static final String CLASS_PANEL = "iyen-panel";
    private Component header;
    private Component footer;
    private final List<Component> contentComponents = new ArrayList<>();
    private String emptyStateTitle;
    private Layout panelContent;
    private PanelVariant.Background background;

    public Panel() {
        addClassName(CLASS_PANEL);
        getElement().setAttribute("role", "group");

    }

    public void setHeader(Component header) {
        if (this.header != null) {
            remove(this.header);
        }
        this.header = header;
        if (header == null) {
            return;
        }
        header.addClassName("panel-header");
        addComponentAsFirst(header);
    }

    public void setHeader(String title) {
        setHeader(
                Components.span().styleName("section-heading")
                        .text(title).build()
        );
    }

    public void setContent(Component... components) {

        if (components != null) {

            removeEmptyState();
            contentComponents.forEach(this::remove);
            contentComponents.clear();

            for (Component component : components) {
                if (component != null) {
                    contentComponents.add(component);
                }
            }
        }
        if (contentComponents.isEmpty()) {
            syncEmptyState();
            return;
        }
        int insertIndex = header != null ? 1 : 0;
        panelContent = Components.layout(components)
                .styleName("panel-content")
                .build();
        addComponentAtIndex(insertIndex, panelContent);
    }

    public void setFooter(Component footer) {
        if (this.footer != null) {
            remove(this.footer);
        }
        this.footer = footer;
        if (footer == null) {
            return;
        }
        add(footer);
    }

    public void setEmptyState(String title) {
        this.emptyStateTitle = title;
        syncEmptyState();
    }

    /** Replaces the current panel background variant. */
    public void setBackground(PanelVariant.Background background) {
        if (this.background != null) {
            removeClassName(this.background.getClassName());
        }
        this.background = background;
        if (background != null) {
            addClassName(background.getClassName());
        }
    }

    public PanelVariant.Background getBackground() {
        return background;
    }

    private void syncEmptyState() {
        removeEmptyState();
        if (contentComponents.isEmpty() && emptyStateTitle != null && !emptyStateTitle.isBlank()) {
            Empty emptyState = new Empty();
            emptyState.setTitle(emptyStateTitle);
            addComponentAtIndex(header != null ? 1 : 0, emptyState);
        }
    }

    private void removeEmptyState() {
        getChildren()
                .filter(component -> component instanceof Empty)
                .findFirst()
                .ifPresent(this::remove);
    }

}
