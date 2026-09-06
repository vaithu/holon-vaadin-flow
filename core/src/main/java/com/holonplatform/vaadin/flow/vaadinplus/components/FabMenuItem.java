/*
 * Copyright 2016-2026 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;
import java.io.Serializable;

/**
 * Immutable descriptor for a single action item of a {@link FabMenu} — the Material Design 3
 * <a href="https://m3.material.io/components/floating-action-button/overview">FAB Menu</a>
 * pattern (a "speed-dial" of related actions revealed from the main FAB).
 *
 * <pre>{@code
 * FabMenuItem.of(VaadinIcon.EDIT, "Compose", e -> compose());
 * FabMenuItem.of(VaadinIcon.CAMERA, "Photo", Fab.Color.TERTIARY, e -> takePhoto());
 * }</pre>
 *
 * @see FabMenu
 */
public class FabMenuItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final VaadinIcon icon;
    private final String label;
    private final Fab.Color color;
    private final ClickEventListener<Button, ClickEvent<Button>> clickListener;
    private boolean enabled = true;

    /**
     * Creates a menu item with the default (surface) color.
     *
     * @param icon          the item icon (not null)
     * @param label         the item label, shown next to the icon
     * @param clickListener the action to run when the item is selected (may be null)
     */
    public FabMenuItem(VaadinIcon icon, String label, ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        this(icon, label, null, clickListener);
    }

    /**
     * Creates a fully-specified menu item.
     *
     * @param icon          the item icon (not null)
     * @param label         the item label, shown next to the icon
     * @param color         the item color ({@code null} = {@link Fab.Color#SURFACE})
     * @param clickListener the action to run when the item is selected (may be null)
     */
    public FabMenuItem(VaadinIcon icon, String label, Fab.Color color,
            ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        this.icon = icon;
        this.label = label;
        this.color = color;
        this.clickListener = clickListener;
    }

    /**
     * Creates a menu item with the default (surface) color.
     *
     * @param icon          the item icon (not null)
     * @param label         the item label, shown next to the icon
     * @param clickListener the action to run when the item is selected (may be null)
     * @return a new {@link FabMenuItem}
     */
    public static FabMenuItem of(VaadinIcon icon, String label,
            ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        return new FabMenuItem(icon, label, clickListener);
    }

    /**
     * Creates a fully-specified menu item.
     *
     * @param icon          the item icon (not null)
     * @param label         the item label, shown next to the icon
     * @param color         the item color ({@code null} = {@link Fab.Color#SURFACE})
     * @param clickListener the action to run when the item is selected (may be null)
     * @return a new {@link FabMenuItem}
     */
    public static FabMenuItem of(VaadinIcon icon, String label, Fab.Color color,
            ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        return new FabMenuItem(icon, label, color, clickListener);
    }

    /**
     * Returns the item icon.
     *
     * @return the icon
     */
    public VaadinIcon getIcon() {
        return icon;
    }

    /**
     * Returns the item label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the item color, or {@code null} to use the {@link FabMenu} default.
     *
     * @return the color, may be {@code null}
     */
    public Fab.Color getColor() {
        return color;
    }

    /**
     * Returns the action to run when this item is selected.
     *
     * @return the click listener, may be {@code null}
     */
    public ClickEventListener<Button, ClickEvent<Button>> getClickListener() {
        return clickListener;
    }

    /**
     * Returns whether this item is enabled.
     *
     * @return {@code true} if enabled (the default)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether this item is enabled.
     *
     * @param enabled {@code false} to render the item disabled
     * @return this item
     */
    public FabMenuItem enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}

