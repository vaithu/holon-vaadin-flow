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
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ButtonGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonGroupConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

/**
 * A visually unified group of {@link Button} instances â€” borders between
 * adjacent buttons are merged and corner radius is applied only to the
 * outermost edges, creating a single cohesive control.
 *
 * <p>Inspired by the shadcn/ui
 * <a href="https://ui.shadcn.com/docs/components/radix/button-group">ButtonGroup</a>
 * component.
 *
 * <p>Renders as:
 * <pre>
 * &lt;div class="btn-group"&gt;
 *   &lt;vaadin-button&gt;Day&lt;/vaadin-button&gt;
 *   &lt;vaadin-button&gt;Week&lt;/vaadin-button&gt;
 *   &lt;vaadin-button&gt;Month&lt;/vaadin-button&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>All styling is handled by {@code button-group.css}. No inline styles or
 * Lumo tokens are used.
 *
 * <p><strong>Orientation:</strong>
 * <pre>{@code
 * // Horizontal (default)
 * ButtonGroup group = new ButtonGroup(new Button("A"), new Button("B"));
 *
 * // Vertical
 * ButtonGroup group = new ButtonGroup(Orientation.VERTICAL, new Button("Top"), new Button("Bottom"));
 * }</pre>
 *
 * <p><strong>Holon Fluent builder:</strong>
 * <pre>{@code
 * ButtonGroup group = ButtonGroup.builder()
 *     .content(new Button("Left"), new Button("Center"), new Button("Right"))
 *     .vertical()
 *     .build();
 * }</pre>
 *
 * <p><strong>Disabled state:</strong> disable individual buttons as usual â€”
 * the group has no global disable API because groups often mix enabled/disabled
 * buttons (e.g. a selected segment in a toggle bar).
 *
 * @see Button
 * @see ButtonGroupBuilder
 * @see ButtonGroupConfigurator
 */
@StyleSheet("context://button-group.css")
public class ButtonGroup extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Stored localizable aria-label; re-resolved on locale change. */
    private Localizable ariaLabelLocalizable;

    /**
     * Layout orientation of the button group.
     */
    public enum Orientation {
        /** Buttons are laid out left-to-right (default). */
        HORIZONTAL,
        /** Buttons are stacked top-to-bottom. */
        VERTICAL
    }

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty horizontal button group.
     */
    public ButtonGroup() {
        addClassName("btn-group");
        getElement().setAttribute("role", "group");
    }

    /**
     * Creates a horizontal button group pre-populated with the given buttons.
     *
     * @param buttons the buttons to content; individual {@code null} entries are skipped
     */
    public ButtonGroup(Button... buttons) {
        this();
        add(buttons);
    }

    /**
     * Creates a button group with the given orientation and buttons.
     *
     * @param orientation the layout direction (not null)
     * @param buttons     the buttons to content; individual {@code null} entries are skipped
     */
    public ButtonGroup(Orientation orientation, Button... buttons) {
        this();
        setOrientation(orientation);
        add(buttons);
    }

    // -----------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------

    /**
     * Appends one or more {@link Button} instances to this group.
     *
     * @param buttons the buttons to content; individual {@code null} entries are skipped
     */
    public void add(Button... buttons) {
        if (buttons == null) return;
        for (Button b : buttons) {
            if (b != null) {
                super.add(b);
            }
        }
    }

    /**
     * Removes specific buttons from this group.
     *
     * @param buttons the buttons to remove
     */
    public void remove(Button... buttons) {
        if (buttons == null) return;
        for (Button b : buttons) {
            if (b != null) {
                super.remove(b);
            }
        }
    }

    /**
     * Sets the orientation of this group.
     *
     * <p>Applying {@link Orientation#VERTICAL} adds the {@code btn-group--vertical}
     * CSS modifier; removing it (switching back to {@link Orientation#HORIZONTAL})
     * removes the modifier.
     *
     * @param orientation the desired orientation (not null)
     */
    public void setOrientation(Orientation orientation) {
        if (orientation == Orientation.VERTICAL) {
            addClassName("btn-group--vertical");
        } else {
            removeClassName("btn-group--vertical");
        }
    }

    /**
     * Sets the ARIA label that describes the purpose of this group to assistive technology.
     *
     * <p>Example: {@code group.setAriaLabel("View period")} for a Day/Week/Month toggle.
     *
     * @param label the accessible name for the group (not null)
     */
    public void setAriaLabel(String label) {
        this.ariaLabelLocalizable = null;
        if (label != null && !label.isBlank()) {
            getElement().setAttribute("aria-label", label);
        } else {
            getElement().removeAttribute("aria-label");
        }
    }

    /**
     * Sets the ARIA label from a {@link Localizable} descriptor.
     * The label is resolved immediately and re-resolved on locale change.
     *
     * @param label the localizable accessible name (not null)
     */
    public void setAriaLabel(Localizable label) {
        this.ariaLabelLocalizable = label;
        applyAriaLabel();
    }
    private void applyAriaLabel() {
        if (ariaLabelLocalizable == null) return;
        String resolved = LocalizationProvider.localize(ariaLabelLocalizable)
                .orElseGet(() -> ariaLabelLocalizable.getMessage() != null
                        ? ariaLabelLocalizable.getMessage() : "");
        if (!resolved.isBlank()) {
            getElement().setAttribute("aria-label", resolved);
        }
    }

    // -----------------------------------------------------------------------
    // Holon Fluent Builder / Configurator factories
    // -----------------------------------------------------------------------

    /**
     * Returns a new Holon fluent {@link ButtonGroupBuilder} to create a {@link ButtonGroup}.
     *
     * <pre>{@code
     * ButtonGroup group = ButtonGroup.builder()
     *     .content(new Button("A"), new Button("B"), new Button("C"))
     *     .fullWidth()
     *     .build();
     * }</pre>
     *
     * @return a new {@link ButtonGroupBuilder}
     */
    public static ButtonGroupBuilder builder() {
        return ButtonGroupBuilder.create();
    }

    /**
     * Returns a Holon fluent configurator for an <strong>existing</strong> {@link ButtonGroup}.
     *
     * <pre>{@code
     * ButtonGroup.configure(myGroup)
     *     .vertical()
     *     .width("20rem");
     * }</pre>
     *
     * @param buttonGroup the group to configure (not null)
     * @return a {@link ButtonGroupConfigurator.BaseButtonGroupConfigurator}
     */
    public static ButtonGroupConfigurator.BaseButtonGroupConfigurator configure(ButtonGroup buttonGroup) {
        return ButtonGroupConfigurator.configure(buttonGroup);
    }
}