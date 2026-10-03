package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultDetailPanelBuilder;

/**
 * Builds a {@link Panel} with the compact header presentation used by detail views.
 *
 * <p>The builder specializes only the panel header defaults. Content, background,
 * sizing, and styling remain available through the standard {@link PanelConfigurator}
 * contract.</p>
 */
public interface DetailPanelBuilder extends DetailPanelConfigurator<DetailPanelBuilder>,
        ComponentBuilder<Panel, DetailPanelBuilder> {

    /**
     * Creates a detail panel with the given required title.
     *
     * @param title panel title
     * @return a new detail panel builder
     */
    static DetailPanelBuilder create(String title) {
        return new DefaultDetailPanelBuilder(title);
    }
}
