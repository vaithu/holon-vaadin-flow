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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.builders.BeanPropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputFormBuilder;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;

import java.util.Optional;
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
 * @param <T> The value type: the bean class for bean-mode, {@link PropertyBox} for PropertySet-mode.
 *
 * @see BeanPropertyInputForm
 * @see PropertyInputForm
 * @see BeanBuilder
 * @see PropertyBuilder
 */
@StyleSheet("context://entity-form-panel.css")
public class EntityFormPanel<T> extends Div implements HasSize, HasStyle {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final PropertyInputForm form;
    private final Button saveButton;
    private final Button saveAndNewButton;  // null when not configured
    private final Button clearButton;
    private final Button cancelButton;      // null when not configured

    // -----------------------------------------------------------------------
    // Private constructor — use factory methods to create instances
    // -----------------------------------------------------------------------

    private EntityFormPanel(
            PropertyInputForm form,
            FormValueSupplier<T> valueSupplier,
            Button saveBtn, Consumer<T> saveAction,
            Button saveAndNewBtn, Consumer<T> saveAndNewAction,
            Button clearBtn,
            Button cancelBtn, Runnable cancelAction) {

        this.form = form;
        this.saveButton = saveBtn;
        this.saveAndNewButton = saveAndNewBtn;
        this.clearButton = clearBtn;
        this.cancelButton = cancelBtn;

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
     * using a varargs property list.
     *
     * @param properties the properties that define the form fields (not null)
     * @return a new {@link PropertyBuilder}
     */
    public static PropertyBuilder properties(Property<?>... properties) {
        return new DefaultPropertyBuilder(PropertySet.of(properties));
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
        BeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<FormLayout, T>> config);

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
        PropertyBuilder configure(Consumer<PropertyInputFormBuilder<FormLayout>> config);

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

        private Consumer<BeanPropertyInputFormBuilder<FormLayout, T>> formConfig;

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
        public BeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<FormLayout, T>> config) {
            this.formConfig = config;
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

            // Build the BeanPropertyInputForm with default ENTER navigation enabled.
            BeanPropertyInputFormBuilder<FormLayout, T> beanFormBuilder =
                    BeanPropertyInputForm.formLayout(beanClass)
                            .configure(fb -> fb
                                    .enterMovesFocusToNext(true)     // Enter → next field
                                    .validateOnEnterFocusMove(true)  // stay on invalid field
                                    .validateOnValueChange(true));   // inline errors while typing

            // Developer config is applied after defaults so it can selectively override.
            if (formConfig != null) {
                formConfig.accept(beanFormBuilder);
            }

            BeanPropertyInputForm<T> beanForm = beanFormBuilder.build();

            // Value supplier: validate and return bean.
            FormValueSupplier<T> valueSupplier = beanForm::getBean;

            return new EntityFormPanel<>(
                    beanForm,
                    valueSupplier,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction
            );
        }
    }

    // -----------------------------------------------------------------------
    // Internal: DefaultPropertyBuilder
    // -----------------------------------------------------------------------

    private static final class DefaultPropertyBuilder implements PropertyBuilder {

        private final PropertySet<?> propertySet;

        private Consumer<PropertyInputFormBuilder<FormLayout>> formConfig;

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
        public PropertyBuilder configure(Consumer<PropertyInputFormBuilder<FormLayout>> config) {
            this.formConfig = config;
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

            // Build the PropertyInputForm with default ENTER navigation enabled.
            PropertyInputFormBuilder<FormLayout> formBuilder =
                    PropertyInputForm.formLayout(propertySet)
                            .enterMovesFocusToNext(true)     // Enter → next field
                            .validateOnEnterFocusMove(true)  // stay on invalid field
                            .validateOnValueChange(true);    // inline errors while typing

            if (formConfig != null) {
                formConfig.accept(formBuilder);
            }

            PropertyInputForm propertyForm = formBuilder.build();

            // Value supplier: validate all inputs, then return the PropertyBox.
            FormValueSupplier<PropertyBox> valueSupplier = () -> {
                propertyForm.validate();           // throws ValidationException if invalid
                return propertyForm.getValue(false); // no re-validation
            };

            return new EntityFormPanel<>(
                    propertyForm,
                    valueSupplier,
                    makeButton(saveBtnConfig), saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction
            );
        }
    }
}






