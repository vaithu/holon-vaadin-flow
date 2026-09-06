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

import com.holonplatform.vaadin.flow.components.builders.FabBuilder;
import com.holonplatform.vaadin.flow.components.builders.FabConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

/**
 * A Material Design 3 <a href="https://m3.material.io/components/floating-action-button/overview">
 * Floating Action Button (FAB)</a> — represents the primary, most important, or most common
 * action on a screen.
 *
 * <p>Per the M3 guidance a FAB:</p>
 * <ul>
 *   <li>should be used sparingly — there should be no more than one per screen, docked to a
 *       fixed {@link Position} (typically {@link Position#BOTTOM_END});</li>
 *   <li>comes in three sizes ({@link Size#SMALL}, {@link Size#DEFAULT}, {@link Size#LARGE});</li>
 *   <li>comes in four color schemes ({@link Color#SURFACE}, {@link Color#PRIMARY},
 *       {@link Color#SECONDARY}, {@link Color#TERTIARY});</li>
 *   <li>can be <em>extended</em> to show a text label next to the icon, and can later
 *       collapse back to icon-only (e.g. on scroll).</li>
 * </ul>
 *
 * <p>DOM structure (a plain {@link Button}, so it stays keyboard-accessible, focusable and
 * a {@code ClickNotifier} for free):</p>
 * <pre>
 * &lt;vaadin-button class="fab fab--{color} [fab--{size}] [fab--extended] [fab--lowered]
 *                        [fab--pos-{position}]"&gt;
 *   &lt;vaadin-icon .../&gt;
 *   [label text, only when extended]
 * &lt;/vaadin-button&gt;
 * </pre>
 *
 * <p>All visual styling lives in {@code fab.css}. No inline styles are used.</p>
 *
 * <p>Preferred usage — via the {@link com.holonplatform.vaadin.flow.components.Components}
 * factory or the builder:</p>
 * <pre>{@code
 * // Simplest — surface FAB, default size, docked bottom-end
 * Fab fab = Fab.builder(VaadinIcon.PLUS)
 *     .position(Fab.Position.BOTTOM_END)
 *     .onClick(e -> createNewItem())
 *     .build();
 *
 * // Primary, large, extended with a label
 * Fab compose = Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY)
 *     .size(Fab.Size.LARGE)
 *     .extended("Compose")
 *     .position(Fab.Position.BOTTOM_END)
 *     .build();
 * }</pre>
 *
 * @see FabBuilder
 * @see FabConfigurator
 */
@StyleSheet("context://fab.css")
public class Fab extends Button {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Size enum
    // -----------------------------------------------------------------------

    /**
     * FAB size preset, per the M3 specification.
     *
     * <ul>
     *   <li>{@link #SMALL}   — 40dp container / 24dp icon — for compact screens</li>
     *   <li>{@link #DEFAULT} — 56dp container / 24dp icon — the most common size</li>
     *   <li>{@link #LARGE}   — 96dp container / 36dp icon — hero / prominent actions</li>
     * </ul>
     */
    public enum Size {
        SMALL("fab--small"),
        DEFAULT(null),
        LARGE("fab--large");

        private final String cssClass;

        Size(String cssClass) {
            this.cssClass = cssClass;
        }

        /** Returns the CSS modifier class, or {@code null} for {@link #DEFAULT}. */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // Color enum
    // -----------------------------------------------------------------------

    /**
     * FAB color scheme, per the M3 specification.
     *
     * <ul>
     *   <li>{@link #SURFACE}   — low-emphasis surface-container color (the M3 default)</li>
     *   <li>{@link #PRIMARY}   — primary-container color, for the most prominent action</li>
     *   <li>{@link #SECONDARY} — secondary-container color</li>
     *   <li>{@link #TERTIARY}  — tertiary-container color, useful to add visual variety</li>
     * </ul>
     */
    public enum Color {
        SURFACE(null),
        PRIMARY("fab--primary"),
        SECONDARY("fab--secondary"),
        TERTIARY("fab--tertiary");

        private final String cssClass;

        Color(String cssClass) {
            this.cssClass = cssClass;
        }

        /** Returns the CSS modifier class, or {@code null} for {@link #SURFACE}. */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // Position enum
    // -----------------------------------------------------------------------

    /**
     * Fixed on-screen docking position. M3 recommends a single, consistently placed FAB per
     * screen — {@link #BOTTOM_END} is the most common placement (bottom-right in LTR locales).
     */
    public enum Position {
        /** No fixed positioning — the FAB flows normally in its container. */
        NONE(null),
        BOTTOM_END("fab--pos-bottom-end"),
        BOTTOM_START("fab--pos-bottom-start"),
        BOTTOM_CENTER("fab--pos-bottom-center"),
        TOP_END("fab--pos-top-end"),
        TOP_START("fab--pos-top-start");

        private final String cssClass;

        Position(String cssClass) {
            this.cssClass = cssClass;
        }

        /** Returns the CSS modifier class, or {@code null} for {@link #NONE}. */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private Size currentSize = Size.DEFAULT;
    private Color currentColor = Color.SURFACE;
    private Position currentPosition = Position.NONE;
    private boolean extended = false;
    private boolean lowered = false;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a default-size, surface-colored, icon-only FAB with a {@link VaadinIcon#PLUS} icon.
     */
    public Fab() {
        this(VaadinIcon.PLUS.create(), Color.SURFACE);
    }

    /**
     * Creates a default-size, surface-colored, icon-only FAB.
     *
     * @param icon the icon to display (not null)
     */
    public Fab(VaadinIcon icon) {
        this(icon.create(), Color.SURFACE);
    }

    /**
     * Creates a default-size, icon-only FAB with the given color.
     *
     * @param icon  the icon to display (not null)
     * @param color the color scheme ({@code null} = {@link Color#SURFACE})
     */
    public Fab(VaadinIcon icon, Color color) {
        this(icon.create(), color);
    }

    /**
     * Creates a default-size, icon-only FAB wrapping the given icon component.
     *
     * @param icon  the icon component to display (not null)
     * @param color the color scheme ({@code null} = {@link Color#SURFACE})
     */
    public Fab(Component icon, Color color) {
        super();
        addClassName("fab");
        setIcon(icon);
        getElement().setAttribute("aria-hidden", "false");
        setFabSize(Size.DEFAULT);
        setFabColor(color != null ? color : Color.SURFACE);
        setFabPosition(Position.NONE);
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Obtain a {@link FabBuilder} for a default surface FAB with no icon pre-set
     * ({@link VaadinIcon#PLUS} is used until changed).
     *
     * @return a new {@link FabBuilder}
     */
    public static FabBuilder builder() {
        return FabBuilder.create();
    }

    /**
     * Obtain a {@link FabBuilder} pre-configured with the given icon.
     *
     * @param icon the icon to display (not null)
     * @return a new {@link FabBuilder}
     */
    public static FabBuilder builder(VaadinIcon icon) {
        return FabBuilder.create(icon);
    }

    /**
     * Obtain a {@link FabBuilder} pre-configured with icon and color.
     *
     * <pre>{@code
     * Fab fab = Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY)
     *     .size(Fab.Size.LARGE)
     *     .build();
     * }</pre>
     *
     * @param icon  the icon to display (not null)
     * @param color the color scheme ({@code null} = {@link Color#SURFACE})
     * @return a new {@link FabBuilder}
     */
    public static FabBuilder builder(VaadinIcon icon, Color color) {
        return FabBuilder.create(icon, color);
    }

    /**
     * Obtain a {@link FabConfigurator.BaseFabConfigurator} to configure an existing {@link Fab}.
     *
     * @param fab the FAB to configure (not null)
     * @return a {@link FabConfigurator.BaseFabConfigurator}
     */
    public static FabConfigurator.BaseFabConfigurator configure(Fab fab) {
        return FabConfigurator.configure(fab);
    }

    // -----------------------------------------------------------------------
    // Size API
    // -----------------------------------------------------------------------

    /**
     * Returns the current size preset.
     *
     * @return the current {@link Size} (never null)
     */
    public Size getFabSize() {
        return currentSize;
    }

    /**
     * Sets the FAB size, swapping the CSS modifier class.
     *
     * @param size the new size (not null; {@link Size#DEFAULT} removes any size modifier)
     */
    public void setFabSize(Size size) {
        if (currentSize != null && currentSize.getCssClass() != null) {
            removeClassName(currentSize.getCssClass());
        }
        currentSize = (size != null) ? size : Size.DEFAULT;
        if (currentSize.getCssClass() != null) {
            addClassName(currentSize.getCssClass());
        }
    }

    // -----------------------------------------------------------------------
    // Color API
    // -----------------------------------------------------------------------

    /**
     * Returns the current color scheme.
     *
     * @return the current {@link Color} (never null)
     */
    public Color getFabColor() {
        return currentColor;
    }

    /**
     * Sets the FAB color scheme, swapping the CSS modifier class.
     *
     * @param color the new color ({@code null} / {@link Color#SURFACE} = default surface color)
     */
    public void setFabColor(Color color) {
        if (currentColor != null && currentColor.getCssClass() != null) {
            removeClassName(currentColor.getCssClass());
        }
        currentColor = (color != null) ? color : Color.SURFACE;
        if (currentColor.getCssClass() != null) {
            addClassName(currentColor.getCssClass());
        }
    }

    // -----------------------------------------------------------------------
    // Position API
    // -----------------------------------------------------------------------

    /**
     * Returns the current fixed docking position.
     *
     * @return the current {@link Position} (never null)
     */
    public Position getFabPosition() {
        return currentPosition;
    }

    /**
     * Docks the FAB at a fixed position on screen (relative to its nearest positioned
     * ancestor), swapping the CSS modifier class.
     *
     * @param position the new position (not null; {@link Position#NONE} removes fixed positioning)
     */
    public void setFabPosition(Position position) {
        if (currentPosition != null && currentPosition.getCssClass() != null) {
            removeClassName(currentPosition.getCssClass());
        }
        currentPosition = (position != null) ? position : Position.NONE;
        if (currentPosition.getCssClass() != null) {
            addClassName(currentPosition.getCssClass());
        }
    }

    // -----------------------------------------------------------------------
    // Extended API
    // -----------------------------------------------------------------------

    /**
     * Returns whether this FAB is currently extended (showing a text label).
     *
     * @return {@code true} if extended
     */
    public boolean isExtended() {
        return extended;
    }

    /**
     * Extends or collapses the FAB.
     * <p>
     * When extending, a non-blank {@code label} should generally be set via {@link #setText(String)}
     * beforehand (or right after) — the {@code fab--extended} CSS class switches the shape from
     * circular to a pill and reveals the button's text next to the icon.
     * </p>
     *
     * @param extended {@code true} to show the pill shape with the label, {@code false} to
     *                 collapse back to icon-only
     */
    public void setExtended(boolean extended) {
        this.extended = extended;
        if (extended) {
            addClassName("fab--extended");
        } else {
            removeClassName("fab--extended");
        }
    }

    /**
     * Convenience method: sets the label text and extends the FAB in one call.
     *
     * @param label the label to display next to the icon (not null / not blank)
     * @return this FAB instance
     */
    public Fab extended(String label) {
        setText(label);
        setExtended(true);
        return this;
    }

    /**
     * Collapses the FAB back to its icon-only circular shape, clearing the label text.
     *
     * @return this FAB instance
     */
    public Fab collapse() {
        setText("");
        setExtended(false);
        return this;
    }

    // -----------------------------------------------------------------------
    // Lowered API
    // -----------------------------------------------------------------------

    /**
     * Returns whether the FAB is currently in its "lowered" (reduced elevation) state.
     *
     * @return {@code true} if lowered
     */
    public boolean isLowered() {
        return lowered;
    }

    /**
     * Applies the M3 "lowered" elevation variant — a reduced-shadow style typically used when
     * the FAB overlaps other elevated surfaces (e.g. a bottom app bar).
     *
     * @param lowered {@code true} to reduce elevation
     */
    public void setLowered(boolean lowered) {
        this.lowered = lowered;
        if (lowered) {
            addClassName("fab--lowered");
        } else {
            removeClassName("fab--lowered");
        }
    }
}

