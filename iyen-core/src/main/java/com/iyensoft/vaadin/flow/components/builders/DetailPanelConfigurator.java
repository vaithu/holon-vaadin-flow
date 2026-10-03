package com.iyensoft.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;

/**
 * Configures a {@link com.iyensoft.vaadin.flow.components.Panel} that uses the compact
 * detail-view header presentation.
 *
 * @param <C> concrete configurator type
 */
public interface DetailPanelConfigurator<C extends DetailPanelConfigurator<C>> extends PanelConfigurator<C> {

    /**
     * Sets supporting content in the detail panel header.
     *
     * @param components detail components
     * @return this configurator
     */
    C details(Component... components);

    /**
     * Sets the actions displayed in the detail panel header.
     *
     * @param components action components
     * @return this configurator
     */
    C actions(Component... components);
}
