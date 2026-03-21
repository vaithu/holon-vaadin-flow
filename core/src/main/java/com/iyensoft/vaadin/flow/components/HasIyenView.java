package com.iyensoft.vaadin.flow.components;

import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;

public interface HasIyenView {

    /**
     * Builds the master area (typically a list, grid, or summary).
     */
    IyenMasterBuilder master();

    /**
     * Builds the detail area (typically a form or inspector).
     */
    IyenDetailBuilder detail();

    /**
     * Selects the initial view mode (MOBILE or DESKTOP).
     * This can be a simple preference; actual mode can still
     * change based on screen size via IyenResponsiveLayout.
     */
    ViewMode selectInitialMode();
}
