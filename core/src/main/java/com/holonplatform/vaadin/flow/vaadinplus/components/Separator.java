/*
 * Copyright 2016-2024 Axioma srl.
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

import java.io.Serial;
import com.holonplatform.vaadin.flow.components.builders.SeparatorBuilder;
import com.holonplatform.vaadin.flow.components.builders.SeparatorConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

/**
 * A visual separator that divides content horizontally or vertically.
 *
 * <p>Inspired by the shadcn/ui {@code Separator} component
 * (<a href="https://ui.shadcn.com/docs/components/radix/separator">docs</a>),
 * built on the Radix UI {@code Separator} primitive.</p>
 *
 * <p>Two usage modes:</p>
 * <ul>
 *   <li><strong>Meaningful</strong> (default)  renders with {@code role="separator"} and
 *       {@code aria-orientation} so assistive technologies announce it as a thematic break.</li>
 *   <li><strong>Decorative</strong>  renders with {@code role="none"} and
 *       {@code aria-hidden="true"} when the separator is purely visual and should be
 *       invisible to screen readers.</li>
 * </ul>
 *
 * <p>HTML output examples:</p>
 * <pre>
 * &lt;!-- horizontal (default) --&gt;
 * &lt;div class="separator separator--horizontal"
 *      role="separator" aria-orientation="horizontal"&gt;&lt;/div&gt;
 *
 * &lt;!-- vertical --&gt;
 * &lt;div class="separator separator--vertical"
 *      role="separator" aria-orientation="vertical"&gt;&lt;/div&gt;
 *
 * &lt;!-- decorative --&gt;
 * &lt;div class="separator separator--horizontal separator--decorative"
 *      role="none" aria-hidden="true"&gt;&lt;/div&gt;
 * </pre>
 *
 * <p><strong>Fluent builder (recommended):</strong></p>
 * <pre>{@code
 * // Horizontal rule (default)
 * Separator sep = Separator.builder().build();
 *
 * // Vertical rule
 * Separator sep = Separator.builder()
 *     .orientation(Separator.Orientation.VERTICAL)
 *     .build();
 *
 * // Decorative (hidden from screen readers)
 * Separator sep = Separator.builder()
 *     .decorative(true)
 *     .build();
 * }</pre>
 *
 * <p>All visual styling is defined in {@code separator.css}. No inline styles or Lumo tokens
 * are used.</p>
 *
 * @see SeparatorBuilder
 * @see SeparatorConfigurator
 * @see Orientation
 */
@StyleSheet("context://separator.css")
public class Separator extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Separator thickness  maps to a CSS modifier class that overrides the
     * {@code --separator-thickness} custom property used by both orientation rules.
     *
     * <pre>{@code
     * Separator.builder().thickness(Separator.Thickness.THICK).build();
     * }</pre>
     */
    public enum Thickness {
        /** 1 px  the default. */
        THIN("separator--thickness-thin"),
        /** 2 px. */
        MEDIUM("separator--thickness-medium"),
        /** 4 px. */
        THICK("separator--thickness-thick");

        private final String cssClass;

        Thickness(String cssClass) {
            this.cssClass = cssClass;
        }

        /** The CSS modifier class that sets {@code --separator-thickness}. */
        public String getCssClass() {
            return cssClass;
        }
    }

    /**
     * Separator orientation  maps directly to the WAI-ARIA {@code aria-orientation} values.
     */
    public enum Orientation {
        /** A horizontal rule spanning the full width of its container. */
        HORIZONTAL("horizontal", "separator--horizontal"),
        /** A vertical rule spanning the full height of its container. */
        VERTICAL("vertical", "separator--vertical");

        private final String ariaValue;
        private final String cssClass;

        Orientation(String ariaValue, String cssClass) {
            this.ariaValue = ariaValue;
            this.cssClass = cssClass;
        }

        /** The {@code aria-orientation} attribute value. */
        public String getAriaValue() {
            return ariaValue;
        }

        /** The CSS modifier class applied to the separator element. */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private Orientation orientation = Orientation.HORIZONTAL;
    private boolean decorative = false;
    private Color.Background color = null;
    private Thickness thickness = null;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a meaningful horizontal separator with default settings.
     */
    public Separator() {
        addClassName("separator");
        applyOrientation(Orientation.HORIZONTAL);
        applyDecorativeFlag(false);
    }

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Returns the current orientation.
     *
     * @return the orientation (never null)
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the separator orientation.
     *
     * @param orientation the desired orientation (not null; defaults to {@link Orientation#HORIZONTAL})
     */
    public void setOrientation(Orientation orientation) {
        if (orientation == null) orientation = Orientation.HORIZONTAL;
        // Remove old orientation class
        removeClassName(this.orientation.getCssClass());
        this.orientation = orientation;
        applyOrientation(orientation);
    }

    // -----------------------------------------------------------------------
    // Decorative flag
    // -----------------------------------------------------------------------

    /**
     * Returns whether this separator is decorative (invisible to screen readers).
     *
     * @return {@code true} if decorative
     */
    public boolean isDecorative() {
        return decorative;
    }

    /**
     * Sets whether this separator is purely decorative.
     *
     * <p>When {@code true}: {@code role="none"} and {@code aria-hidden="true"} are applied
     * and the CSS class {@code separator--decorative} is added.</p>
     * <p>When {@code false}: {@code role="separator"} and {@code aria-orientation} are applied.</p>
     *
     * @param decorative {@code true} to make the separator decorative
     */
    public void setDecorative(boolean decorative) {
        this.decorative = decorative;
        applyDecorativeFlag(decorative);
        // Re-apply orientation ARIA attributes since they depend on the decorative flag
        applyOrientation(this.orientation);
    }

    // -----------------------------------------------------------------------
    // Color
    // -----------------------------------------------------------------------

    /**
     * Returns the current background color, or {@code null} if none has been set
     * (the CSS default colour is used in that case).
     *
     * @return the current color, may be {@code null}
     */
    public Color.Background getColor() {
        return color;
    }

    /**
     * Sets the separator colour by applying a predefined {@link Color.Background} CSS class.
     *
     * <p>Replaces any previously applied colour class. Pass {@code null} to revert to the
     * default CSS colour ({@code var(--separator-color, â€¦)}).</p>
     *
     * <pre>{@code
     * separator.setColor(Color.Background.PRIMARY);
     * }</pre>
     *
     * @param color the colour to apply, or {@code null} to clear
     */
    public void setColor(Color.Background color) {
        if (this.color != null) {
            removeClassName(this.color.getClassName());
        }
        this.color = color;
        if (color != null) {
            addClassName(color.getClassName());
        }
    }

    // -----------------------------------------------------------------------
    // Thickness
    // -----------------------------------------------------------------------

    /**
     * Returns the current thickness, or {@code null} if none has been set
     * (the CSS default of 1 px is used in that case).
     *
     * @return the current thickness, may be {@code null}
     */
    public Thickness getThickness() {
        return thickness;
    }

    /**
     * Sets the separator thickness by applying a predefined {@link Thickness} CSS modifier class.
     *
     * <p>The class overrides the {@code --separator-thickness} custom property for both
     * horizontal (height) and vertical (width) orientations. Pass {@code null} to revert to
     * the CSS default (1 px).</p>
     *
     * <pre>{@code
     * separator.setThickness(Separator.Thickness.THICK);
     * }</pre>
     *
     * @param thickness the thickness to apply, or {@code null} to clear
     */
    public void setThickness(Thickness thickness) {
        if (this.thickness != null) {
            removeClassName(this.thickness.getCssClass());
        }
        this.thickness = thickness;
        if (thickness != null) {
            addClassName(thickness.getCssClass());
        }
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private void applyOrientation(Orientation o) {
        addClassName(o.getCssClass());
        if (!decorative) {
            getElement().setAttribute("aria-orientation", o.getAriaValue());
        } else {
            getElement().removeAttribute("aria-orientation");
        }
    }

    private void applyDecorativeFlag(boolean decorative) {
        if (decorative) {
            getElement().setAttribute("role", "none");
            getElement().setAttribute("aria-hidden", "true");
            addClassName("separator--decorative");
        } else {
            getElement().setAttribute("role", "separator");
            getElement().removeAttribute("aria-hidden");
            removeClassName("separator--decorative");
        }
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Returns a new fluent {@link SeparatorBuilder}.
     *
     * <pre>{@code
     * Separator sep = Separator.builder()
     *     .orientation(Separator.Orientation.VERTICAL)
     *     .id("my-sep")
     *     .build();
     * }</pre>
     *
     * @return a new {@link SeparatorBuilder}
     */
    public static SeparatorBuilder builder() {
        return SeparatorBuilder.create();
    }

    /**
     * Returns a fluent configurator for an <strong>existing</strong> {@link Separator} instance.
     *
     * @param separator the separator to configure (not null)
     * @return a {@link SeparatorConfigurator.BaseSeparatorConfigurator}
     */
    public static SeparatorConfigurator.BaseSeparatorConfigurator configure(Separator separator) {
        return SeparatorConfigurator.configure(separator);
    }
}
