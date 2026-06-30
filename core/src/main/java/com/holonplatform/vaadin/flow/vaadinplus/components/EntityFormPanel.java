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

import com.holonplatform.core.Validator;
import com.holonplatform.core.operation.TriConsumer;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.builders.BeanPropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FormResponsiveStepBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A full-featured form panel that wraps a {@link BeanPropertyInputForm} or a
 * {@link PropertyInputForm} and provides a standard button footer.
 *
 * <p>
 * The footer always shows two mandatory buttons:
 * <ol>
 *   <li><strong>Save</strong> – validates the form and calls the supplied save action.</li>
 *   <li><strong>Clear</strong> – clears all form inputs without validation.</li>
 * </ol>
 * Two optional buttons are rendered only when explicitly configured:
 * <ol>
 *   <li><strong>Save &amp; New</strong> – validates, calls the supplied action, then clears the form.</li>
 *   <li><strong>Cancel</strong> – calls the supplied runnable without validation.</li>
 * </ol>
 *
 * <p>Button layout (footer):
 * <pre>
 * ┌──────────────────────────────────────────┐
 * │  [Clear]  [Cancel?]   [Save&amp;New?]  [Save] │
 * └──────────────────────────────────────────┘
 * </pre>
 *
 * <p>By default the form enables ENTER-key navigation between inputs
 * ({@code enterMovesFocusToNext=true}, {@code validateOnEnterFocusMove=true}).
 * Override these defaults via {@link BeanBuilder#configure} /
 * {@link PropertyBuilder#configure}.
 *
 * <h3>Bean-mode usage</h3>
 * <pre>{@code
 * EntityFormPanel<Customer> panel = EntityFormPanel.<Customer>bean(Customer.class)
 *     .configure(fb -> fb.excludeFields("id", "createdAt"))
 *     .properties("firstName", "lastName", "email")  // optional custom order / subset
 *     .saveButton(
 *         btn -> btn.primary().withText("Save"),
 *         customer -> service.save(customer))
 *     .clearButton(btn -> btn.withText("Reset"))          // no theme needed — styled by panel
 *     .saveAndNewButton(
 *         btn -> btn.withText("Save & New"),
 *         customer -> service.save(customer))             // optional
 *     .cancelButton(
 *         btn -> btn.withText("Cancel"),
 *         () -> dialog.close())                          // optional
 *     .build();
 * }</pre>
 *
 * <h3>PropertySet-mode usage</h3>
 * <pre>{@code
 * EntityFormPanel<PropertyBox> panel = EntityFormPanel
 *     .properties(NAME, EMAIL, PHONE)
 *     .saveButton(btn -> btn.primary().withText("Save"), pb -> service.save(pb))
 *     .clearButton(btn -> btn.withText("Reset"))
 *     .build();
 * }</pre>
 *
 * <p><strong>Note on Clear / Cancel styling:</strong> the panel automatically strips the
 * {@code theme} attribute from the Clear and Cancel buttons so they always render as
 * neutral outlined buttons (the Holon shell "default outlined" style).  Pass only the
 * button label, icon, or disabled state via the config consumer; do not call
 * {@code .tertiary()}, {@code .primary()}, or similar theme methods on these two buttons.</p>
 *
 * <p>All styling is handled by {@code entity-form-panel.css}; no inline styles
 * or Lumo tokens are used in Java code.
 *
 * <h3>Layout-aware example</h3>
 * <pre>{@code
 * EntityFormPanel<Customer> panel = EntityFormPanel.<Customer>bean(Customer.class)
 *     .properties("firstName", "lastName", "email", "notes")
 *     .initializer(layout -> layout.setResponsiveSteps(
 *         new FormLayout.ResponsiveStep("0", 1),
 *         new FormLayout.ResponsiveStep("480px", 2)))
 *     .responsiveSteps(steps -> steps
 *         .mobile(1)
 *         .tablet(2)
 *         .desktop(3))
 *     .withPostProcessor((layout, property, input) -> {
 *         if ("email".equals(property.relativeName())) {
 *             layout.setColspan(input.getComponent(), 2);
 *         }
 *     })
 *     .saveButton(btn -> btn.save("Save"), customer -> service.save(customer))
 *     .clearButton(btn -> btn.withText("Clear"))
 *     .build();
 * }</pre>
 *
 * <p>This keeps layout configuration and per-field adjustments close together
 * without needing any external state holder.</p>
 *
 * @param <T> The value type: the bean class for bean-mode, {@link PropertyBox} for PropertySet-mode.
 *
 * @see BeanPropertyInputForm
 * @see PropertyInputForm
 * @see BeanBuilder
 * @see PropertyBuilder
 */
@StyleSheet("context://entity-form-panel.css")
public class EntityFormPanel<T> extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final PropertyInputForm form;
    private final Button saveButton;
    private final Button saveAndNewButton;  // null when not configured
    private final Button clearButton;
    private final Button cancelButton;      // null when not configured
    private final boolean stretchLastRow;
    private final LayoutMode layoutMode;
    private final List<FormLayout.ResponsiveStep> responsiveSteps;
    private final List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors;

    // -----------------------------------------------------------------------
    // Private constructor — use factory methods to create instances
    // -----------------------------------------------------------------------

    private EntityFormPanel(
            PropertyInputForm form,
            FormValueSupplier<T> valueSupplier,
            Button saveBtn, Consumer<T> saveAction,
            Button saveAndNewBtn, Consumer<T> saveAndNewAction,
            Button clearBtn,
            Button cancelBtn, Runnable cancelAction,
            LayoutMode layoutMode,
            boolean stretchLastRow,
            List<FormLayout.ResponsiveStep> responsiveSteps,
            List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors) {

        this.form = form;
        this.saveButton = saveBtn;
        this.saveAndNewButton = saveAndNewBtn;
        this.clearButton = clearBtn;
        this.cancelButton = cancelBtn;
        this.stretchLastRow = stretchLastRow;
        this.layoutMode = layoutMode;
        this.responsiveSteps = responsiveSteps;
        this.postProcessors = postProcessors;

        addClassName("entity-form-panel");

        // ── Form body ──────────────────────────────────────────────────────
        Div body = Components.div().add(form.getComponent()).styleName("entity-form-panel__body").build();

        // ── Auto-focus first field on attach so users can type immediately ─
        // and so that Holon's focusNextDocumentElement() traversal naturally
        // leads back to this component's Save button on the last field.
        form.getComponent().addAttachListener(e ->
                form.getElements()
                        .findFirst()
                        .ifPresent(input -> {
                            if (input.getComponent() instanceof Focusable<?> f) {
                                f.focus();
                            }
                        }));

        // ── Footer ────────────────────────────────────────────────────────
        // DOM ORDER IS INTENTIONAL:
        //   Save → SaveAndNew → Clear → Cancel
        //
        // When the user presses Enter on the LAST form field, Holon's
        // focusNextDocumentElement() walks forward in the DOM from that field
        // and lands on the first focusable element — the Save button.
        // Pressing Enter on the focused Save button then triggers it natively.
        //
        // VISUAL ORDER (via CSS `order` property) is the opposite:
        //   [Clear] [Cancel]        [SaveAndNew] [Save]
        // so secondary actions are on the left and primary on the right.
        Div footer = Components.div().styleName("entity-form-panel__footer").build();

        // 1. Save — DOM first → keyboard Enter on last field reaches it first
        saveBtn.addClassName("entity-form-panel__btn-save");
        saveBtn.addClickListener(e -> {
            try {
                T value = valueSupplier.get();   // validates; throws ValidationException if invalid
                saveAction.accept(value);
            } catch (Validator.ValidationException ignored) {
                // Form already shows inline errors; no extra action needed.
            }
        });
        footer.add(saveBtn);

        // 2. SaveAndNew — DOM second (optional)
        if (saveAndNewBtn != null) {
            saveAndNewBtn.addClassName("entity-form-panel__btn-save-new");
            saveAndNewBtn.addClickListener(e -> {
                try {
                    T value = valueSupplier.get();      // Step 1: validate
                    saveAndNewAction.accept(value);     // Step 2: persist
                    form.clear();                       // Step 3: clear ONLY on full success
                } catch (Validator.ValidationException ignored) {
                    // Validation failed — inline errors shown; form NOT cleared.
                }
            });
            footer.add(saveAndNewBtn);
        }

        // 3. Clear — CSS `margin-inline-end: auto` pushes it visually to the far left.
        //    Strip the theme attribute: EntityFormPanel owns the visual style of secondary
        //    actions.  Leaving theme="tertiary" (or any variant) from the developer's config
        //    would make the button invisible as plain text.  Without a theme attribute the
        //    shell-theme "default outlined" style applies, and entity-form-panel.css adds the
        //    surface tint and hover states on top.
        clearBtn.addClassName("entity-form-panel__btn-clear");
        clearBtn.getElement().removeAttribute("theme");
        clearBtn.addClickListener(e -> form.clear());
        footer.add(clearBtn);

        // 4. Cancel — DOM last (optional). Same theme-strip rationale as Clear above.
        if (cancelBtn != null) {
            cancelBtn.addClassName("entity-form-panel__btn-cancel");
            cancelBtn.getElement().removeAttribute("theme");
            cancelBtn.addClickListener(e -> cancelAction.run());
            footer.add(cancelBtn);
        }

        add(body, footer);

        form.compose();
        applyDefaultLabels(form);

        if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
            form.getBindings().forEach(binding ->
                    this.postProcessors.forEach(postProcessor ->
                            postProcessor.accept(form.getComponent(), binding.getProperty(), binding.getElement())));
        }

        if (this.layoutMode == LayoutMode.GRID) {
            applyGridDivLayout((Div) form.getComponent(), form, this.responsiveSteps, this.stretchLastRow);
        } else if (this.stretchLastRow) {
            applyStretchLastRow((FormLayout) form.getComponent(), form);
        }
    }

    private static void applyDefaultLabels(PropertyInputForm form) {
        form.getBindings().forEach(binding -> {
            Property<?> property = binding.getProperty();
            binding.getElement().hasLabel().ifPresent(hasLabel -> {
                String current = hasLabel.getLabel();
                String rawName = property.getName();
                if (current == null || current.isBlank() || current.equals(rawName)) {
                    hasLabel.setLabel(toPascalCase(rawName));
                }
            });
        });
    }

    private static void applyStretchLastRow(FormLayout layout, PropertyInputForm form) {
        final var inputs = form.getBindings()
                .map(binding -> binding.getElement())
                .toList();
        final var children = layout.getChildren().toList();

        if (inputs.isEmpty() || children.size() < inputs.size()) {
            return;
        }

        final int columns = layout.getResponsiveSteps().stream()
                    .mapToInt(step -> step.toJson().path("columns").asInt(1))
                .max()
                .orElse(1);

        if (columns <= 1) {
            return;
        }

        final int remainder = inputs.size() % columns;
        if (remainder == 0) {
            return;
        }

        final int startIndex = inputs.size() - remainder;
        final int baseSpan = columns / remainder;
        final int extraColumns = columns % remainder;

        for (int i = 0; i < remainder; i++) {
            final int span = baseSpan + (i < extraColumns ? 1 : 0);
            layout.setColspan(children.get(startIndex + i), span);
        }
    }

    private static String toPascalCase(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        StringBuilder sb = new StringBuilder(name.length());
        boolean capitalizeNext = true;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch == '_' || ch == '-' || ch == ' ') {
                capitalizeNext = true;
                continue;
            }
            if (capitalizeNext) {
                sb.append(Character.toUpperCase(ch));
                capitalizeNext = false;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static void applyGridDivLayout(Div layout,
                                           PropertyInputForm form,
                                           List<FormLayout.ResponsiveStep> responsiveSteps,
                                           boolean stretchLastRow) {

        layout.addClassName("entity-form-panel__grid");
        layout.addClassNames("grid", "grid-cols-12", "gap-m");

        final List<Component> children = form.getBindings()
                .map(binding -> binding.getElement().getComponent())
                .toList();

        if (children.isEmpty()) {
            return;
        }

        final List<GridStep> steps = normalizeGridSteps(responsiveSteps);
        for (GridStep step : steps) {
            final String prefix = step.prefix();
            final int columns = Math.max(1, step.columns());
            final int baseSpan = spanFor(columns);

            for (int index = 0; index < children.size(); index++) {
                final int rowStart = (index / columns) * columns;
                final int rowSize = Math.min(columns, children.size() - rowStart);
                final int span = stretchLastRow && rowSize < columns ? spanFor(rowSize) : baseSpan;
                addGridSpanClass(children.get(index), prefix, span);
            }
        }
    }

    private static List<GridStep> normalizeGridSteps(List<FormLayout.ResponsiveStep> responsiveSteps) {
        if (responsiveSteps == null || responsiveSteps.isEmpty()) {
            return List.of(new GridStep(null, 1));
        }

        return responsiveSteps.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(UIUtils::parseMinWidth))
                .map(step -> new GridStep(prefixForResponsiveStep(step), responsiveStepColumns(step)))
                .toList();
    }

    private static int responsiveStepColumns(FormLayout.ResponsiveStep step) {
        final var json = step.toJson();
        return json != null && json.has("columns") ? Math.max(1, json.get("columns").asInt(1)) : 1;
    }

    private static String prefixForResponsiveStep(FormLayout.ResponsiveStep step) {
        final int minWidth = UIUtils.parseMinWidth(step);
        return switch (minWidth) {
            case 0 -> null;
            case 576 -> ViewMode.MOBILE.getPrefix();
            case 768 -> ViewMode.TABLET.getPrefix();
            case 992 -> ViewMode.DESKTOP.getPrefix();
            case 1200 -> ViewMode.LARGE_DESKTOP.getPrefix();
            case 1400 -> ViewMode.ULTRA_WIDE.getPrefix();
            default -> minWidth < 768 ? ViewMode.MOBILE.getPrefix()
                    : minWidth < 992 ? ViewMode.TABLET.getPrefix()
                    : minWidth < 1200 ? ViewMode.DESKTOP.getPrefix()
                    : minWidth < 1400 ? ViewMode.LARGE_DESKTOP.getPrefix()
                    : ViewMode.ULTRA_WIDE.getPrefix();
        };
    }

    private static int spanFor(int columns) {
        if (columns <= 1) {
            return 12;
        }
        return Math.max(1, 12 / columns);
    }

    private static void addGridSpanClass(Component component, String prefix, int span) {
        final String className = "col-span-" + span;
        if (prefix == null || prefix.isBlank()) {
            component.addClassName(className);
        } else {
            component.addClassName(prefix + ":" + className);
        }
    }

    private static <L extends Component> TriConsumer<Component, Property<?>, Input<?>> adaptPostProcessor(
            TriConsumer<L, Property<?>, Input<?>> postProcessor) {
        return (layout, property, input) -> postProcessor.accept((L) layout, property, input);
    }

    private static List<FormLayout.ResponsiveStep> buildResponsiveSteps(Consumer<FormResponsiveStepBuilder> config) {
        if (config == null) {
            return List.of();
        }
        FormResponsiveStepBuilder responsiveStepsBuilder = FormResponsiveStepBuilder.create();
        config.accept(responsiveStepsBuilder);
        return responsiveStepsBuilder.build();
    }

    private record GridStep(String prefix, int columns) {
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Returns the underlying {@link PropertyInputForm}.
     * <p>
     * In bean mode, cast to {@link BeanPropertyInputForm} to use bean-specific API
     * (e.g. {@code setBean()}, {@code getBean()}).
     *
     * @return the form (never null)
     */
    public PropertyInputForm getForm() {
        return form;
    }

    /**
     * Returns the Save button.
     *
     * @return the Save {@link Button} (never null)
     */
    public Button getSaveButton() {
        return saveButton;
    }

    /**
     * Returns the Save &amp; New button if one was configured.
     *
     * @return the Save &amp; New {@link Button} wrapped in an {@link Optional}
     */
    public Optional<Button> getSaveAndNewButton() {
        return Optional.ofNullable(saveAndNewButton);
    }

    /**
     * Returns the Clear button.
     *
     * @return the Clear {@link Button} (never null)
     */
    public Button getClearButton() {
        return clearButton;
    }

    /**
     * Returns the Cancel button if one was configured.
     *
     * @return the Cancel {@link Button} wrapped in an {@link Optional}
     */
    public Optional<Button> getCancelButton() {
        return Optional.ofNullable(cancelButton);
    }

    /**
     * Enable or disable automatic required indicators for the wrapped form.
     * <p>
     * When enabled, bean-backed fields annotated with required validation
     * constraints are marked required automatically, while explicit required
     * settings are preserved.
     * </p>
     *
     * @param autoRequiredIndicators whether automatic required indicators are enabled
     */
    public void setAutoRequiredIndicators(boolean autoRequiredIndicators) {
        form.setAutoRequiredIndicators(autoRequiredIndicators);
    }

    /**
     * Get whether automatic required indicators are enabled.
     *
     * @return whether automatic required indicators are enabled
     */
    public boolean isAutoRequiredIndicators() {
        return form.isAutoRequiredIndicators();
    }

    /**
     * Populates the underlying form from the given bean instance.
     * <p>
     * When this panel wraps a {@link BeanPropertyInputForm}, a {@code null}
     * value clears all inputs. When the panel is backed by a plain
     * {@link PropertyInputForm}, a {@code null} value clears the current
     * property box and a non-null value must be a {@link PropertyBox}.
     * </p>
     *
     * @param bean bean instance to load, or {@code null} to clear the form
     */
    @SuppressWarnings("unchecked")
    public void setBean(T bean) {
        if (form instanceof BeanPropertyInputForm<?>) {
            ((BeanPropertyInputForm<T>) form).setBean(bean);
            return;
        }
        if (bean == null) {
            form.setValue(null);
            return;
        }
        if (bean instanceof PropertyBox propertyBox) {
            form.setValue(propertyBox);
            return;
        }
        throw new IllegalStateException(
                "EntityFormPanel#setBean is only supported when the underlying form is bean-based");
    }

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Entry point for building an {@link EntityFormPanel} in <em>bean mode</em>.
     * <p>
     * The form's property set is derived automatically from the bean class via
     * {@code BeanPropertySet}. Fields annotated with {@code @Identifier} or
     * {@code @Version} are hidden by default.
     *
     * @param <T>       bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link BeanBuilder}
     */
    public static <T> BeanBuilder<T> bean(Class<T> beanClass) {
        return new DefaultBeanBuilder<>(beanClass);
    }

    /**
     * Entry point for building an {@link EntityFormPanel} in <em>bean mode</em>
     * using a CSS grid {@link Div} as the layout container.
     *
     * @param <T> bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link DivBeanBuilder}
     * @since 10.0.0
     */
    public static <T> DivBeanBuilder<T> beanDiv(Class<T> beanClass) {
        return new DefaultDivBeanBuilder<>(beanClass);
    }

    /**
     * Entry point for building an {@link EntityFormPanel} in <em>PropertySet mode</em>.
     *
     * @param propertySet the property set that defines the form fields (not null)
     * @return a new {@link PropertyBuilder}
     */
    public static PropertyBuilder properties(PropertySet<?> propertySet) {
        return new DefaultPropertyBuilder(propertySet);
    }

    /**
     * Entry point for building an {@link EntityFormPanel} in <em>PropertySet mode</em>
     * using a CSS grid {@link Div} as the layout container.
     *
     * @param propertySet the property set that defines the form fields (not null)
     * @return a new {@link DivPropertyBuilder}
     * @since 10.0.0
     */
    public static DivPropertyBuilder propertiesDiv(PropertySet<?> propertySet) {
        return new DefaultDivPropertyBuilder(propertySet);
    }

    /**
     * Entry point for building an {@link EntityFormPanel} in <em>PropertySet mode</em>
     * using a varargs property list.
     *
     * @param properties the properties that define the form fields (not null)
     * @return a new {@link PropertyBuilder}
     */
    public static PropertyBuilder properties(Property<?>... properties) {
        return new DefaultPropertyBuilder(PropertySet.of(properties));
    }

    /**
     * Available form layout modes.
     */
    public enum LayoutMode {
        FORM,
        GRID
    }

    /**
     * Entry point for building an {@link EntityFormPanel} in <em>PropertySet mode</em>
     * using a CSS grid {@link Div} as the layout container.
     *
     * @param properties the properties that define the form fields (not null)
     * @return a new {@link DivPropertyBuilder}
     * @since 10.0.0
     */
    public static DivPropertyBuilder propertiesDiv(Property<?>... properties) {
        return new DefaultDivPropertyBuilder(PropertySet.of(properties));
    }

    // -----------------------------------------------------------------------
    // Builder interfaces
    // -----------------------------------------------------------------------

    /**
     * Fluent builder for an {@link EntityFormPanel} in <em>bean mode</em>.
     *
     * @param <T> bean type
     */
    public interface BeanBuilder<T> {

        /**
         * Apply additional configuration to the underlying
         * {@link BeanPropertyInputFormBuilder}.
         * <p>
         * Use this to exclude/read-only specific fields, register custom renderers,
         * validators, or override the default ENTER-navigation flags.
         *
         * <pre>{@code
         * .configure(fb -> {
         *     fb.excludeFields("id", "createdAt");
         *     fb.configure(inner -> inner.validateOnValueChange(true));
         * })
         * }</pre>
         *
         * @param config consumer that receives the form builder (not null)
         * @return this
         */
        <C extends Component> BeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<C, T>> config);

        /**
         * Configure the underlying {@link FormLayout} used by the form.
         * <p>
         * The layout is provided automatically by the form builder, so this is the
         * direct EntityFormPanel equivalent of {@code BeanPropertyInputForm}'s
         * initializer hook.
         * </p>
         *
         * @param initializer callback receiving the generated {@link FormLayout} (not null)
         * @return this
         */
        <C extends Component> BeanBuilder<T> initializer(Consumer<C> initializer);

        /**
         * Select the form layout mode.
         *
         * @param layoutMode the layout mode to use (not null)
         * @return this
         */
        BeanBuilder<T> layout(LayoutMode layoutMode);

        /**
         * Configure the form columns using responsive step shortcuts.
         * <p>
         * Use the provided builder to define the number of columns for each
         * {@link ViewMode}.
         * </p>
         *
         * @param config responsive step configurator (not null)
         * @return this
         */
        BeanBuilder<T> responsiveSteps(Consumer<FormResponsiveStepBuilder> config);

        /**
         * Stretch the final incomplete row to fill the available width.
         * <p>
         * When enabled, the remaining fields in the last row are distributed across
         * the full responsive-step column count instead of leaving empty space on the
         * right.
         * </p>
         *
         * @param stretchLastRow whether to stretch the last row
         * @return this
         */
        BeanBuilder<T> stretchLastRow(boolean stretchLastRow);

        /**
         * Enable or disable automatic required indicators for the generated form.
         *
         * @param autoRequiredIndicators whether automatic required indicators are enabled
         * @return this
         */
        BeanBuilder<T> autoRequiredIndicators(boolean autoRequiredIndicators);

        /**
         * Select the bean fields to render and define their order.
         * <p>
         * Only the named fields are rendered, in the exact order provided.
         * Fields not listed here are omitted from the form.
         * </p>
         *
         * @param fieldNames bean field names to render, in order (not null)
         * @return this
         */
        BeanBuilder<T> properties(String... fieldNames);

        /**
         * Add a post-processor that runs after each input is created.
         * <p>
         * The current {@link FormLayout} is provided automatically, so this is the
         * right place to adjust component-specific details such as colspan for
         * individual properties.
         * </p>
         *
         * @param postProcessor post-processor receiving the layout, property and its input (not null)
         * @return this
         */
        <C extends Component> BeanBuilder<T> withPostProcessor(TriConsumer<C, Property<?>, Input<?>> postProcessor);

        /**
         * Configure the <strong>mandatory</strong> Save button and its action.
         * <p>
         * The action receives the validated bean after the form passes validation.
         * If validation fails, inline errors are shown and the action is not called.
         *
         * @param config   button appearance configurator (not null)
         * @param onSave   callback invoked with the validated bean on successful validation
         *                 (not null)
         * @return this
         */
        BeanBuilder<T> saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                  Consumer<T> onSave);

        /**
         * Configure the optional Save &amp; New button and its action.
         * <p>
         * After calling the action the form is automatically cleared, ready for a
         * new entry. The action is only called when validation succeeds.
         *
         * @param config        button appearance configurator (not null)
         * @param onSaveAndNew  callback invoked with the validated bean (not null)
         * @return this
         */
        BeanBuilder<T> saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                        Consumer<T> onSaveAndNew);

        /**
         * Configure the <strong>mandatory</strong> Clear button.
         * <p>
         * The button click handler clears all form inputs without validation.
         *
         * @param config button appearance configurator (not null)
         * @return this
         */
        BeanBuilder<T> clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config);

        /**
         * Configure the optional Cancel button and its action.
         * <p>
         * No validation is performed when Cancel is clicked.
         *
         * @param config   button appearance configurator (not null)
         * @param onCancel runnable invoked when the button is clicked (not null)
         * @return this
         */
        BeanBuilder<T> cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                    Runnable onCancel);

        /**
         * Build the {@link EntityFormPanel}.
         *
         * @return a fully configured {@link EntityFormPanel}
         * @throws IllegalStateException if the mandatory Save or Clear button has not
         *                               been configured
         */
        EntityFormPanel<T> build();
    }

    /**
     * Fluent builder for an {@link EntityFormPanel} in <em>bean mode</em> using a
     * {@link Div} grid layout container.
     *
     * @param <T> bean type
     */
    public interface DivBeanBuilder<T> {

        DivBeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<Div, T>> config);

        DivBeanBuilder<T> initializer(Consumer<Div> initializer);

        /**
         * Enable or disable automatic required indicators for the generated form.
         *
         * @param autoRequiredIndicators whether automatic required indicators are enabled
         * @return this
         */
        DivBeanBuilder<T> autoRequiredIndicators(boolean autoRequiredIndicators);

        DivBeanBuilder<T> responsiveSteps(Consumer<FormResponsiveStepBuilder> config);

        DivBeanBuilder<T> stretchLastRow(boolean stretchLastRow);

        DivBeanBuilder<T> properties(String... fieldNames);

        DivBeanBuilder<T> withPostProcessor(TriConsumer<Div, Property<?>, Input<?>> postProcessor);

        DivBeanBuilder<T> saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                     Consumer<T> onSave);

        DivBeanBuilder<T> saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                         Consumer<T> onSaveAndNew);

        DivBeanBuilder<T> clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config);

        DivBeanBuilder<T> cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                       Runnable onCancel);

        EntityFormPanel<T> build();
    }

    /**
     * Fluent builder for an {@link EntityFormPanel} in <em>PropertySet mode</em>.
     */
    public interface PropertyBuilder {

        /**
         * Apply additional configuration to the underlying
         * {@link PropertyInputFormBuilder}.
         *
         * @param config consumer that receives the form builder (not null)
         * @return this
         */
        <C extends Component> PropertyBuilder configure(Consumer<PropertyInputFormBuilder<C>> config);

        /**
         * Configure the underlying {@link FormLayout} used by the form.
         *
         * @param initializer callback receiving the generated {@link FormLayout} (not null)
         * @return this
         */
        <C extends Component> PropertyBuilder initializer(Consumer<C> initializer);

        /**
         * Select the form layout mode.
         *
         * @param layoutMode the layout mode to use (not null)
         * @return this
         */
        PropertyBuilder layout(LayoutMode layoutMode);

        /**
         * Configure the form columns using responsive step shortcuts.
         *
         * @param config responsive step configurator (not null)
         * @return this
         */
        PropertyBuilder responsiveSteps(Consumer<FormResponsiveStepBuilder> config);

        /**
         * Stretch the final incomplete row to fill the available width.
         *
         * @param stretchLastRow whether to stretch the last row
         * @return this
         */
        PropertyBuilder stretchLastRow(boolean stretchLastRow);

        /**
         * Enable or disable automatic required indicators for the generated form.
         *
         * @param autoRequiredIndicators whether automatic required indicators are enabled
         * @return this
         */
        PropertyBuilder autoRequiredIndicators(boolean autoRequiredIndicators);

        /**
         * Add a post-processor that runs after each input is created.
         * <p>
         * The current {@link FormLayout} is provided automatically, so this is the
         * right place to adjust component-specific details such as colspan for
         * individual properties.
         * </p>
         *
         * @param postProcessor post-processor receiving the layout, property and its input (not null)
         * @return this
         */
        <C extends Component> PropertyBuilder withPostProcessor(TriConsumer<C, Property<?>, Input<?>> postProcessor);

        /**
         * Configure the <strong>mandatory</strong> Save button and its action.
         *
         * @param config  button appearance configurator (not null)
         * @param onSave  callback invoked with the validated {@link PropertyBox} (not null)
         * @return this
         */
        PropertyBuilder saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                   Consumer<PropertyBox> onSave);

        /**
         * Configure the optional Save &amp; New button and its action.
         *
         * @param config       button appearance configurator (not null)
         * @param onSaveAndNew callback invoked with the validated {@link PropertyBox} (not null)
         * @return this
         */
        PropertyBuilder saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                         Consumer<PropertyBox> onSaveAndNew);

        /**
         * Configure the <strong>mandatory</strong> Clear button.
         *
         * @param config button appearance configurator (not null)
         * @return this
         */
        PropertyBuilder clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config);

        /**
         * Configure the optional Cancel button and its action.
         *
         * @param config   button appearance configurator (not null)
         * @param onCancel runnable invoked when the button is clicked (not null)
         * @return this
         */
        PropertyBuilder cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                     Runnable onCancel);

        /**
         * Build the {@link EntityFormPanel}.
         *
         * @return a fully configured {@link EntityFormPanel}
         * @throws IllegalStateException if the mandatory Save or Clear button has not
         *                               been configured
         */
        EntityFormPanel<PropertyBox> build();
    }

    /**
     * Fluent builder for an {@link EntityFormPanel} in <em>PropertySet mode</em>
     * using a {@link Div} grid layout container.
     */
    public interface DivPropertyBuilder {

        DivPropertyBuilder configure(Consumer<PropertyInputFormBuilder<Div>> config);

        DivPropertyBuilder initializer(Consumer<Div> initializer);

        /**
         * Enable or disable automatic required indicators for the generated form.
         *
         * @param autoRequiredIndicators whether automatic required indicators are enabled
         * @return this
         */
        DivPropertyBuilder autoRequiredIndicators(boolean autoRequiredIndicators);

        DivPropertyBuilder responsiveSteps(Consumer<FormResponsiveStepBuilder> config);

        DivPropertyBuilder stretchLastRow(boolean stretchLastRow);

        DivPropertyBuilder withPostProcessor(TriConsumer<Div, Property<?>, Input<?>> postProcessor);

        DivPropertyBuilder saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                      Consumer<PropertyBox> onSave);

        DivPropertyBuilder saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                            Consumer<PropertyBox> onSaveAndNew);

        DivPropertyBuilder clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config);

        DivPropertyBuilder cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                        Runnable onCancel);

        EntityFormPanel<PropertyBox> build();
    }

    // -----------------------------------------------------------------------
    // Internal: FormValueSupplier
    // -----------------------------------------------------------------------

    /**
     * Internal strategy for retrieving the validated form value.
     * ValidationException (RuntimeException in Holon) propagates to the click handler.
     */
    @FunctionalInterface
    private interface FormValueSupplier<T> {
        T get();
    }

    // -----------------------------------------------------------------------
    // Internal: button factory helper
    // -----------------------------------------------------------------------

    private static Button makeButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config) {
        Button btn = Components.button().build();
        config.accept(ButtonConfigurator.configure(btn));
        return btn;
    }

    // -----------------------------------------------------------------------
    // Internal: DefaultBeanBuilder
    // -----------------------------------------------------------------------

    private static final class DefaultBeanBuilder<T> implements BeanBuilder<T> {

        private final Class<T> beanClass;

        private String[] propertyNames;

        @SuppressWarnings("rawtypes")
        private Consumer<BeanPropertyInputFormBuilder<?, T>> formConfig;
        private Consumer<Component> initializer;
        private Consumer<FormResponsiveStepBuilder> responsiveStepsConfig;
        private boolean stretchLastRow;
        private boolean autoRequiredIndicators;
        private LayoutMode layoutMode = LayoutMode.FORM;

        private final List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors = new ArrayList<>();

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveBtnConfig;
        private Consumer<T> saveAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveAndNewBtnConfig;
        private Consumer<T> saveAndNewAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> clearBtnConfig;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelBtnConfig;
        private Runnable cancelAction;

        DefaultBeanBuilder(Class<T> beanClass) {
            this.beanClass = beanClass;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <C extends Component> BeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<C, T>> config) {
            this.formConfig = (Consumer) config;
            return this;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <C extends Component> BeanBuilder<T> initializer(Consumer<C> initializer) {
            this.initializer = (Consumer) initializer;
            return this;
        }

        @Override
        public BeanBuilder<T> layout(LayoutMode layoutMode) {
            this.layoutMode = Objects.requireNonNull(layoutMode, "layoutMode must not be null");
            return this;
        }

        @Override
        public BeanBuilder<T> responsiveSteps(Consumer<FormResponsiveStepBuilder> config) {
            this.responsiveStepsConfig = config;
            return this;
        }

        @Override
        public BeanBuilder<T> stretchLastRow(boolean stretchLastRow) {
            this.stretchLastRow = stretchLastRow;
            return this;
        }

        @Override
        public BeanBuilder<T> autoRequiredIndicators(boolean autoRequiredIndicators) {
            this.autoRequiredIndicators = autoRequiredIndicators;
            return this;
        }

        @Override
        public BeanBuilder<T> properties(String... fieldNames) {
            this.propertyNames = fieldNames;
            return this;
        }

        @Override
        public <C extends Component> BeanBuilder<T> withPostProcessor(TriConsumer<C, Property<?>, Input<?>> postProcessor) {
            this.postProcessors.add(adaptPostProcessor(postProcessor));
            return this;
        }

        @Override
        public BeanBuilder<T> saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                         Consumer<T> onSave) {
            this.saveBtnConfig = config;
            this.saveAction = onSave;
            return this;
        }

        @Override
        public BeanBuilder<T> saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                               Consumer<T> onSaveAndNew) {
            this.saveAndNewBtnConfig = config;
            this.saveAndNewAction = onSaveAndNew;
            return this;
        }

        @Override
        public BeanBuilder<T> clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config) {
            this.clearBtnConfig = config;
            return this;
        }

        @Override
        public BeanBuilder<T> cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                           Runnable onCancel) {
            this.cancelBtnConfig = config;
            this.cancelAction = onCancel;
            return this;
        }

        @Override
        public EntityFormPanel<T> build() {
            if (saveBtnConfig == null || saveAction == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...)");
            }
            if (clearBtnConfig == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...)");
            }

                if (layoutMode == LayoutMode.GRID) {
                Div content = new Div();
                content.addClassNames("entity-form-panel__grid", "grid", "grid-cols-12", "gap-m");

                BeanPropertyInputFormBuilder<Div, T> beanFormBuilder =
                    new com.holonplatform.vaadin.flow.internal.components.DefaultBeanPropertyInputForm.DefaultBuilder<>(content, beanClass)
                        .configure(fb -> fb
                            .enterMovesFocusToNext(true)
                            .validateOnEnterFocusMove(true)
                            .validateOnValueChange(true));

                if (propertyNames != null) {
                    beanFormBuilder.properties(propertyNames);
                }

                final List<FormLayout.ResponsiveStep> responsiveSteps = buildResponsiveSteps(responsiveStepsConfig);

                if (formConfig != null) {
                    (formConfig).accept(beanFormBuilder);
                }

                if (initializer != null) {
                    initializer.accept(content);
                }

                BeanPropertyInputForm<T> beanForm = beanFormBuilder.build();

                EntityFormPanel<T> panel = new EntityFormPanel<>(
                    beanForm,
                    beanForm::getBean,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors
                );
                panel.setAutoRequiredIndicators(autoRequiredIndicators);
                return panel;
                }

                // Build the BeanPropertyInputForm with default ENTER navigation enabled.
            BeanPropertyInputFormBuilder<FormLayout, T> beanFormBuilder =
                    BeanPropertyInputForm.formLayout(beanClass)
                            .configure(fb -> fb
                                    .enterMovesFocusToNext(true)     // Enter → next field
                                    .validateOnEnterFocusMove(true)  // stay on invalid field
                                    .validateOnValueChange(true));   // inline errors while typing

            if (propertyNames != null) {
                beanFormBuilder.properties(propertyNames);
            }

            // Developer config is applied after defaults so it can selectively override.
            if (formConfig != null) {
                formConfig.accept(beanFormBuilder);
            }

            Consumer<FormLayout> layoutInitializer = initializer != null ? layout -> initializer.accept(layout) : null;
            if (responsiveStepsConfig != null) {
                FormResponsiveStepBuilder responsiveStepsBuilder = FormResponsiveStepBuilder.create();
                responsiveStepsConfig.accept(responsiveStepsBuilder);
                Consumer<FormLayout> responsiveInitializer = layout -> layout.setResponsiveSteps(responsiveStepsBuilder.build());
                layoutInitializer = (layoutInitializer == null) ? responsiveInitializer : layoutInitializer.andThen(responsiveInitializer);
            }

            Consumer<FormLayout> finalLayoutInitializer = layoutInitializer;
            if (finalLayoutInitializer != null) {
                beanFormBuilder.configure(fb -> fb.initializer(finalLayoutInitializer));
            }

            BeanPropertyInputForm<T> beanForm = beanFormBuilder.build();

            // Value supplier: validate and return bean.
            FormValueSupplier<T> valueSupplier = beanForm::getBean;

            EntityFormPanel<T> panel = new EntityFormPanel<>(
                    beanForm,
                    valueSupplier,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.FORM,
                    stretchLastRow,
                    List.of(),
                    postProcessors
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            return panel;
        }
    }

    // -----------------------------------------------------------------------
    // Internal: DefaultDivBeanBuilder
    // -----------------------------------------------------------------------

    private static final class DefaultDivBeanBuilder<T> implements DivBeanBuilder<T> {

        private final Class<T> beanClass;

        private String[] propertyNames;

        private Consumer<BeanPropertyInputFormBuilder<Div, T>> formConfig;
        private Consumer<Div> initializer;
        private Consumer<FormResponsiveStepBuilder> responsiveStepsConfig;
        private boolean stretchLastRow;
        private boolean autoRequiredIndicators;

        private final List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors = new ArrayList<>();

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveBtnConfig;
        private Consumer<T> saveAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveAndNewBtnConfig;
        private Consumer<T> saveAndNewAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> clearBtnConfig;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelBtnConfig;
        private Runnable cancelAction;

        DefaultDivBeanBuilder(Class<T> beanClass) {
            this.beanClass = beanClass;
        }

        @Override
        public DivBeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<Div, T>> config) {
            this.formConfig = config;
            return this;
        }

        @Override
        public DivBeanBuilder<T> initializer(Consumer<Div> initializer) {
            this.initializer = initializer;
            return this;
        }

        @Override
        public DivBeanBuilder<T> responsiveSteps(Consumer<FormResponsiveStepBuilder> config) {
            this.responsiveStepsConfig = config;
            return this;
        }

        @Override
        public DivBeanBuilder<T> stretchLastRow(boolean stretchLastRow) {
            this.stretchLastRow = stretchLastRow;
            return this;
        }

        @Override
        public DivBeanBuilder<T> autoRequiredIndicators(boolean autoRequiredIndicators) {
            this.autoRequiredIndicators = autoRequiredIndicators;
            return this;
        }

        @Override
        public DivBeanBuilder<T> properties(String... fieldNames) {
            this.propertyNames = fieldNames;
            return this;
        }

        @Override
        public DivBeanBuilder<T> withPostProcessor(TriConsumer<Div, Property<?>, Input<?>> postProcessor) {
            this.postProcessors.add(adaptPostProcessor(postProcessor));
            return this;
        }

        @Override
        public DivBeanBuilder<T> saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                            Consumer<T> onSave) {
            this.saveBtnConfig = config;
            this.saveAction = onSave;
            return this;
        }

        @Override
        public DivBeanBuilder<T> saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                                  Consumer<T> onSaveAndNew) {
            this.saveAndNewBtnConfig = config;
            this.saveAndNewAction = onSaveAndNew;
            return this;
        }

        @Override
        public DivBeanBuilder<T> clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config) {
            this.clearBtnConfig = config;
            return this;
        }

        @Override
        public DivBeanBuilder<T> cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                              Runnable onCancel) {
            this.cancelBtnConfig = config;
            this.cancelAction = onCancel;
            return this;
        }

        @Override
        public EntityFormPanel<T> build() {
            if (saveBtnConfig == null || saveAction == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...)");
            }
            if (clearBtnConfig == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...)");
            }

            Div content = new Div();
            content.addClassNames("entity-form-panel__grid", "grid", "grid-cols-12", "gap-m");

            BeanPropertyInputFormBuilder<Div, T> beanFormBuilder =
                    new com.holonplatform.vaadin.flow.internal.components.DefaultBeanPropertyInputForm.DefaultBuilder<>(content, beanClass)
                            .configure(fb -> fb
                                    .enterMovesFocusToNext(true)
                                    .validateOnEnterFocusMove(true)
                                    .validateOnValueChange(true));

            if (propertyNames != null) {
                beanFormBuilder.properties(propertyNames);
            }

            if (formConfig != null) {
                formConfig.accept(beanFormBuilder);
            }

            if (initializer != null) {
                beanFormBuilder.configure(fb -> fb.initializer(layout -> {
                    layout.addClassNames("entity-form-panel__grid", "grid", "grid-cols-12", "gap-m");
                    initializer.accept(layout);
                }));
            }

            final List<FormLayout.ResponsiveStep> responsiveSteps = buildResponsiveSteps(responsiveStepsConfig);

            BeanPropertyInputForm<T> beanForm = beanFormBuilder.build();

            EntityFormPanel<T> panel = new EntityFormPanel<>(
                    beanForm,
                    beanForm::getBean,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                        LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            return panel;
        }
    }

    // -----------------------------------------------------------------------
    // Internal: DefaultDivPropertyBuilder
    // -----------------------------------------------------------------------

    private static final class DefaultDivPropertyBuilder implements DivPropertyBuilder {

        private final PropertySet<?> propertySet;

        private Consumer<PropertyInputFormBuilder<Div>> formConfig;
        private Consumer<Div> initializer;
        private Consumer<FormResponsiveStepBuilder> responsiveStepsConfig;
        private boolean stretchLastRow;
        private boolean autoRequiredIndicators;

        private final List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors = new ArrayList<>();

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveBtnConfig;
        private Consumer<PropertyBox> saveAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveAndNewBtnConfig;
        private Consumer<PropertyBox> saveAndNewAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> clearBtnConfig;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelBtnConfig;
        private Runnable cancelAction;

        DefaultDivPropertyBuilder(PropertySet<?> propertySet) {
            this.propertySet = propertySet;
        }

        @Override
        public DivPropertyBuilder configure(Consumer<PropertyInputFormBuilder<Div>> config) {
            this.formConfig = config;
            return this;
        }

        @Override
        public DivPropertyBuilder initializer(Consumer<Div> initializer) {
            this.initializer = initializer;
            return this;
        }

        @Override
        public DivPropertyBuilder responsiveSteps(Consumer<FormResponsiveStepBuilder> config) {
            this.responsiveStepsConfig = config;
            return this;
        }

        @Override
        public DivPropertyBuilder stretchLastRow(boolean stretchLastRow) {
            this.stretchLastRow = stretchLastRow;
            return this;
        }

        @Override
        public DivPropertyBuilder autoRequiredIndicators(boolean autoRequiredIndicators) {
            this.autoRequiredIndicators = autoRequiredIndicators;
            return this;
        }

        @Override
        public DivPropertyBuilder withPostProcessor(TriConsumer<Div, Property<?>, Input<?>> postProcessor) {
            this.postProcessors.add(adaptPostProcessor(postProcessor));
            return this;
        }

        @Override
        public DivPropertyBuilder saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                             Consumer<PropertyBox> onSave) {
            this.saveBtnConfig = config;
            this.saveAction = onSave;
            return this;
        }

        @Override
        public DivPropertyBuilder saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                                 Consumer<PropertyBox> onSaveAndNew) {
            this.saveAndNewBtnConfig = config;
            this.saveAndNewAction = onSaveAndNew;
            return this;
        }

        @Override
        public DivPropertyBuilder clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config) {
            this.clearBtnConfig = config;
            return this;
        }

        @Override
        public DivPropertyBuilder cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                               Runnable onCancel) {
            this.cancelBtnConfig = config;
            this.cancelAction = onCancel;
            return this;
        }

        @Override
        public EntityFormPanel<PropertyBox> build() {
            if (saveBtnConfig == null || saveAction == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...)");
            }
            if (clearBtnConfig == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...)");
            }

            final Property<?>[] properties = propertySet.stream().toArray(Property[]::new);

            Div content = new Div();
            content.addClassNames("entity-form-panel__grid", "grid", "grid-cols-12", "gap-m");

            PropertyInputFormBuilder<Div> formBuilder =
                    PropertyInputForm.builder(content, properties)
                            .composer(com.holonplatform.vaadin.flow.components.Composable.componentContainerComposer())
                            .enterMovesFocusToNext(true)
                            .validateOnEnterFocusMove(true)
                            .validateOnValueChange(true);

            if (formConfig != null) {
                formConfig.accept(formBuilder);
            }

            if (initializer != null) {
                formBuilder.initializer(layout -> {
                    layout.addClassNames("entity-form-panel__grid", "grid", "grid-cols-12", "gap-m");
                    initializer.accept(layout);
                });
            }

            final List<FormLayout.ResponsiveStep> responsiveSteps = buildResponsiveSteps(responsiveStepsConfig);

            PropertyInputForm propertyForm = formBuilder.build();

            EntityFormPanel<PropertyBox> panel = new EntityFormPanel<>(
                    propertyForm,
                    propertyForm::getValue,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                        LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            return panel;
        }
    }

    // -----------------------------------------------------------------------
    // Internal: DefaultPropertyBuilder
    // -----------------------------------------------------------------------

    private static final class DefaultPropertyBuilder implements PropertyBuilder {

        private final PropertySet<?> propertySet;

        @SuppressWarnings("rawtypes")
        private Consumer formConfig;
        private Consumer<Component> initializer;
        private Consumer<FormResponsiveStepBuilder> responsiveStepsConfig;
        private boolean stretchLastRow;
        private boolean autoRequiredIndicators;
        private LayoutMode layoutMode = LayoutMode.FORM;

        private final List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors = new ArrayList<>();

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveBtnConfig;
        private Consumer<PropertyBox> saveAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveAndNewBtnConfig;
        private Consumer<PropertyBox> saveAndNewAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> clearBtnConfig;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelBtnConfig;
        private Runnable cancelAction;

        DefaultPropertyBuilder(PropertySet<?> propertySet) {
            this.propertySet = propertySet;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <C extends Component> PropertyBuilder configure(Consumer<PropertyInputFormBuilder<C>> config) {
            this.formConfig = (Consumer) config;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends Component> PropertyBuilder initializer(Consumer<C> initializer) {
            this.initializer = (Consumer<Component>) initializer;
            return this;
        }

        @Override
        public PropertyBuilder layout(LayoutMode layoutMode) {
            this.layoutMode = Objects.requireNonNull(layoutMode, "layoutMode must not be null");
            return this;
        }

        @Override
        public PropertyBuilder responsiveSteps(Consumer<FormResponsiveStepBuilder> config) {
            this.responsiveStepsConfig = config;
            return this;
        }

        @Override
        public PropertyBuilder stretchLastRow(boolean stretchLastRow) {
            this.stretchLastRow = stretchLastRow;
            return this;
        }

        @Override
        public PropertyBuilder autoRequiredIndicators(boolean autoRequiredIndicators) {
            this.autoRequiredIndicators = autoRequiredIndicators;
            return this;
        }

        @Override
        public <C extends Component> PropertyBuilder withPostProcessor(TriConsumer<C, Property<?>, Input<?>> postProcessor) {
            this.postProcessors.add(adaptPostProcessor(postProcessor));
            return this;
        }

        @Override
        public PropertyBuilder saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                          Consumer<PropertyBox> onSave) {
            this.saveBtnConfig = config;
            this.saveAction = onSave;
            return this;
        }

        @Override
        public PropertyBuilder saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                                Consumer<PropertyBox> onSaveAndNew) {
            this.saveAndNewBtnConfig = config;
            this.saveAndNewAction = onSaveAndNew;
            return this;
        }

        @Override
        public PropertyBuilder clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config) {
            this.clearBtnConfig = config;
            return this;
        }

        @Override
        public PropertyBuilder cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                            Runnable onCancel) {
            this.cancelBtnConfig = config;
            this.cancelAction = onCancel;
            return this;
        }

        @Override
        public EntityFormPanel<PropertyBox> build() {
            if (saveBtnConfig == null || saveAction == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...)");
            }
            if (clearBtnConfig == null) {
                throw new IllegalStateException(
                        "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...)");
            }

            if (layoutMode == LayoutMode.GRID) {
                Div content = new Div();
                content.addClassNames("entity-form-panel__grid", "grid", "grid-cols-12", "gap-m");

                PropertyInputFormBuilder<Div> formBuilder =
                    PropertyInputForm.builder(content, propertySet.stream().toArray(Property[]::new))
                        .composer(com.holonplatform.vaadin.flow.components.Composable.componentContainerComposer())
                        .enterMovesFocusToNext(true)
                        .validateOnEnterFocusMove(true)
                        .validateOnValueChange(true);

                if (formConfig != null) {
                    formConfig.accept(formBuilder);
                }

                if (initializer != null) {
                    initializer.accept(content);
                }

                final List<FormLayout.ResponsiveStep> responsiveSteps = buildResponsiveSteps(responsiveStepsConfig);

                PropertyInputForm propertyForm = formBuilder.build();

                EntityFormPanel<PropertyBox> panel = new EntityFormPanel<>(
                    propertyForm,
                    propertyForm::getValue,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors
                );
                panel.setAutoRequiredIndicators(autoRequiredIndicators);
                return panel;
            }

            // Build the PropertyInputForm with default ENTER navigation enabled.
            PropertyInputFormBuilder<FormLayout> formBuilder =
                    PropertyInputForm.formLayout(propertySet)
                            .enterMovesFocusToNext(true)     // Enter → next field
                            .validateOnEnterFocusMove(true)  // stay on invalid field
                            .validateOnValueChange(true);    // inline errors while typing

            if (formConfig != null) {
                formConfig.accept(formBuilder);
            }

            Consumer<FormLayout> layoutInitializer = initializer != null ? layout -> initializer.accept(layout) : null;
            if (responsiveStepsConfig != null) {
                FormResponsiveStepBuilder responsiveStepsBuilder = FormResponsiveStepBuilder.create();
                responsiveStepsConfig.accept(responsiveStepsBuilder);
                Consumer<FormLayout> responsiveInitializer = layout -> layout.setResponsiveSteps(responsiveStepsBuilder.build());
                layoutInitializer = (layoutInitializer == null) ? responsiveInitializer : layoutInitializer.andThen(responsiveInitializer);
            }

            Consumer<FormLayout> finalLayoutInitializer = layoutInitializer;
            if (finalLayoutInitializer != null) {
                formBuilder.initializer(finalLayoutInitializer);
            }

            PropertyInputForm propertyForm = formBuilder.build();

            // Value supplier: inherit validation from the underlying PropertyInputForm.
            FormValueSupplier<PropertyBox> valueSupplier = propertyForm::getValue;

            EntityFormPanel<PropertyBox> panel = new EntityFormPanel<>(
                    propertyForm,
                    valueSupplier,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.FORM,
                    stretchLastRow,
                    List.of(),
                    postProcessors
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            return panel;
        }
    }
}






