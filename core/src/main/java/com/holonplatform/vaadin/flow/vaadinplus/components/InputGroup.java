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
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.InputGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.InputGroupLayoutConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

/**
 * A horizontal flex container that groups input fields, buttons, and text
 * addons into a single, visually unified control.
 *
 * <p>Inspired by shadcn/ui {@code InputGroup}
 * (<a href="https://ui.shadcn.com/docs/components/radix/input-group">docs</a>).
 *
 * <p>Renders as:
 * <pre>
 * &lt;div class="input-group"&gt;
 *   &lt;span class="input-group__text"&gt;@&lt;/span&gt;
 *   &lt;vaadin-text-field ...&gt;&lt;/vaadin-text-field&gt;
 *   &lt;vaadin-button ...&gt;Search&lt;/vaadin-button&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Children receive automatic border-radius and border-merging treatment
 * from {@code input-group.css}. Vaadin field variants ({@code vaadin-text-field},
 * {@code vaadin-date-picker}, {@code vaadin-button}, â€¦) are all supported.
 * Holon {@link Input} and any {@link HasComponent} wrapper are unwrapped to
 * their underlying {@link Component} on content.
 *
 * <p><strong>Examples:</strong>
 *
 * <p>Text prefix + text field:
 * <pre>{@code
 * InputGroup group = new InputGroup(
 *     new InputGroupText("@"),
 *     Components.input.string().placeholder("Username").build()
 * );
 * }</pre>
 *
 * <p>Search field + button:
 * <pre>{@code
 * TextField search = new TextField();
 * Button     go    = new Button("Search");
 *
 * InputGroup group = new InputGroup(search, go);
 * }</pre>
 *
 * <p>Price field with both addons:
 * <pre>{@code
 * InputGroup group = new InputGroup(
 *     new InputGroupText("$"),
 *     new NumberField(),
 *     new InputGroupText(".00")
 * );
 * }</pre>
 *
 * <p>Responsive variant (stacks vertically on very small screens):
 * <pre>{@code
 * InputGroup group = new InputGroup(searchField, searchButton);
 * group.addClassName("input-group--responsive");
 * }</pre>
 *
 * <p>All visual styling is defined in {@code input-group.css}. No inline
 * styles or Lumo tokens are used.
 *
 * @see InputGroupText
 */
@StyleSheet("context://input-group.css")
public class InputGroup extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Stored localizable aria-label; re-resolved on locale change. */
    private Localizable ariaLabelLocalizable;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty input group.
     */
    public InputGroup() {
        addClassName("input-group");
        getElement().setAttribute("role", "group");
    }

    /**
     * Creates an input group pre-populated with the given Vaadin components.
     *
     * @param components the child components (any combination of Vaadin input
     *                   fields, buttons, and {@link InputGroupText} addons); may be empty
     */
    public InputGroup(Component... components) {
        this();
        if (components != null) {
            add(components);
        }
    }

    // -----------------------------------------------------------------------
    // Add  Vaadin components
    // -----------------------------------------------------------------------

    /**
     * Appends one or more Vaadin {@link Component} instances to this group.
     *
     * <p>Accepts any Vaadin component: {@code TextField}, {@code DatePicker},
     * {@code Button}, {@link InputGroupText}, etc.
     *
     * @param components the components to content (not null; individual elements may be null and are skipped)
     */
    @Override
    public void add(Component... components) {
        if (components == null) return;
        for (Component c : components) {
            if (c != null) {
                super.add(c);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Add  Holon HasComponent / Input<T>
    // -----------------------------------------------------------------------

    /**
     * Appends one or more {@link HasComponent} wrappers (e.g. Holon {@link Input}
     * instances) to this group by extracting the underlying {@link Component}
     * via {@link HasComponent#getComponent()}.
     *
     * <pre>{@code
     * Input<String> nameInput = Components.input.string().placeholder("Name").build();
     * group.content(nameInput);
     * }</pre>
     *
     * @param inputs the wrappers to content (not null; individual elements may be null and are skipped)
     */
    public void add(HasComponent... inputs) {
        if (inputs == null) return;
        for (HasComponent input : inputs) {
            if (input != null) {
                Component c = input.getComponent();
                if (c != null) {
                    super.add(c);
                }
            }
        }
    }

    /**
     * Appends one or more Holon {@link Input} instances to this group.
     *
     * <p>This is a convenience overload equivalent to {@link #add(HasComponent...)}
     * for the common case where callers already hold a typed {@code Input<T>} reference.
     *
     * <pre>{@code
     * Input<String> username = Components.input.string().placeholder("Username").build();
     * Input<LocalDate> dob   = Components.input.localDate().build();
     * group.content(username, dob);
     * }</pre>
     *
     * @param inputs the Holon inputs to content (not null; individual elements may be null and are skipped)
     */
    @SuppressWarnings("varargs")
    public void add(Input<?>... inputs) {
        if (inputs == null) return;
        for (Input<?> input : inputs) {
            if (input != null) {
                Component c = input.getComponent();
                if (c != null) {
                    super.add(c);
                }
            }
        }
    }

    /**
     * Sets the accessible name for this input group so assistive technology
     * can announce what the combined control represents (e.g. "Search", "Price range").
     *
     * @param label the ARIA label (not null); pass {@code null} or blank to remove
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
     * Sets the accessible name from a {@link Localizable} descriptor.
     * Resolved immediately and re-resolved on locale change.
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
    // Remove
    // -----------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // Builder / configurator factories
    // -----------------------------------------------------------------------

    /**
     * Returns a new fluent {@link InputGroupBuilder} to create an {@link InputGroup}.
     *
     * <pre>{@code
     * InputGroup group = InputGroup.builder()
     *     .content(new InputGroupText("@"))
     *     .content(new TextField())
     *     .build();
     * }</pre>
     *
     * @return a new {@link InputGroupBuilder}
     */
    public static InputGroupBuilder builder() {
        return InputGroupBuilder.create();
    }

    /**
     * Returns a fluent configurator for an <strong>existing</strong> {@link InputGroup}.
     *
     * <pre>{@code
     * InputGroup.configure(myGroup)
     *     .responsive(true)
     *     .width("50%");
     * }</pre>
     *
     * @param inputGroup the group to configure (not null)
     * @return a {@link InputGroupLayoutConfigurator.BaseInputGroupLayoutConfigurator}
     */
    public static InputGroupLayoutConfigurator.BaseInputGroupLayoutConfigurator configure(InputGroup inputGroup) {
        return InputGroupLayoutConfigurator.configure(inputGroup);
    }
}


