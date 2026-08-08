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
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.operation.TriConsumer;
import com.holonplatform.core.property.PathProperty;
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
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Consumer;

import static com.holonplatform.core.internal.utils.FormatUtils.toSentenceCase;

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
    // Input width-tier system — driven by @Column(length) / @Size(max)
    // -----------------------------------------------------------------------

    /**
     * Resolved input-width tier driven by {@code @Column(length)} or {@code @Size(max)}.
     * Applied in bean-mode only when {@code columnLengthAwareWidth(true)} is set.
     *
     * <p>Instead of constraining the input with {@code max-width} (which leaves blank space
     * inside the grid cell), the tier controls the <strong>column span</strong> allocated to
     * the field so the input always fills 100% of its (tier-sized) cell.</p>
     *
     * <ul>
     *   <li>{@code XS} / {@code SM} — smallest useful unit: 1 column</li>
     *   <li>{@code MD}              — medium: 2 columns (or full row when max ≤ 2)</li>
     *   <li>{@code UNCONSTRAINED}   — full row: all available columns</li>
     * </ul>
     */
    private enum FieldWidthTier {
        UNCONSTRAINED,
        XS,
        SM,
        MD;

        static FieldWidthTier of(int columnLength) {
            if (columnLength <= 0 || columnLength > 100) return UNCONSTRAINED;
            if (columnLength <= 10) return XS;
            if (columnLength <= 30) return SM;
            return MD;
        }

        /**
         * Returns the number of form-layout columns this tier should occupy,
         * capped by the supplied {@code maxColumns}.
         *
         * <pre>
         * maxColumns=3 : XS→1  SM→1  MD→2  UNCONSTRAINED→3
         * maxColumns=2 : XS→1  SM→1  MD→2  UNCONSTRAINED→2
         * maxColumns=1 : everything→1
         * </pre>
         */
        int formColspan(int maxColumns) {
            return switch (this) {
                case XS, SM      -> 1;
                case MD          -> Math.clamp((int) Math.ceil(maxColumns * 2.0 / 3), 1, maxColumns);
                case UNCONSTRAINED -> maxColumns;
            };
        }

        /**
         * Returns the 12-column CSS-grid span for this tier at a given column count.
         *
         * <pre>
         * maxColumns=3 : XS→4  SM→4  MD→8  UNCONSTRAINED→12
         * maxColumns=2 : XS→6  SM→6  MD→12 UNCONSTRAINED→12
         * maxColumns=1 : everything→12
         * </pre>
         */
        int gridSpan(int maxColumns) {
            int tileColumns = switch (this) {
                case XS, SM      -> 1;
                case MD          -> Math.clamp(maxColumns, 1, (int) Math.ceil(maxColumns * 2.0 / 3));
                case UNCONSTRAINED -> maxColumns;
            };
            return Math.max(1, Math.round(12.0f * tileColumns / maxColumns));
        }
    }

    /**
     * Resolves the effective column length for a bean field via annotation reflection.
     *
     * <p><strong>Resolution order (first match wins at each tier):</strong></p>
     * <ol>
     *   <li>{@code @Column(length=N)} where N &gt; 0 and N ≠ 255 — authoritative schema width;
     *       255 is the JPA spec default and is skipped to avoid false positives.</li>
     *   <li>{@code @Size(max=N)} where N ≠ {@link Integer#MAX_VALUE} — Bean Validation string length.</li>
     *   <li>{@code @Max(value=N)} / {@code @Min(value=N)} — numeric bounds; the digit count of the
     *       boundary value is used as the effective length (e.g. {@code @Max(9999)} → 4 chars → XS).
     *       Negative {@code @Min} values add one extra char for the minus sign.</li>
     * </ol>
     *
     * <p>Uses annotation reflection (no compile-time dependency on JPA or Bean Validation APIs).
     * Walks the class hierarchy for inherited fields. Returns {@code 0} if undeterminable.</p>
     *
     * @param beanClass the entity / bean class to inspect
     * @param fieldName the simple field name (e.g. {@code "firstName"})
     * @return resolved length, or {@code 0} if not determinable
     */
    private static int resolveColumnLength(Class<?> beanClass, String fieldName) {
        Class<?> cls = beanClass;
        while (cls != null && cls != Object.class) {
            try {
                java.lang.reflect.Field f = cls.getDeclaredField(fieldName);
                int sizeMax   = 0;  // from @Size(max)
                int numDigits = 0;  // from @Max / @Min digit count

                for (java.lang.annotation.Annotation ann : f.getAnnotations()) {
                    String name = ann.annotationType().getSimpleName();
                    try {
                        switch (name) {
                            case "Column" -> {
                                int len = (int) ann.annotationType().getMethod("length").invoke(ann);
                                if (len > 0 && len != 255) return len; // authoritative; skip JPA default
                            }
                            case "Size" -> {
                                int max = (int) ann.annotationType().getMethod("max").invoke(ann);
                                if (max != Integer.MAX_VALUE) sizeMax = max;
                            }
                            case "Max" -> {
                                long val = (long) ann.annotationType().getMethod("value").invoke(ann);
                                if (val < Long.MAX_VALUE) {
                                    numDigits = Math.max(numDigits, Long.toString(val).length());
                                }
                            }
                            case "Min" -> {
                                long val = (long) ann.annotationType().getMethod("value").invoke(ann);
                                int digits = Long.toString(Math.abs(val)).length() + (val < 0 ? 1 : 0);
                                numDigits = Math.max(numDigits, digits);
                            }
                            default -> { /* other annotations ignored */ }
                        }
                    } catch (Exception ignored) {}
                }

                if (sizeMax > 0) return sizeMax;       // @Size beats @Min/@Max
                if (numDigits > 0) return numDigits;   // @Min/@Max fallback
                break; // field found — stop climbing
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            } catch (Exception e) {
                break;
            }
        }
        return 0;
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final PropertyInputForm form;
    private final Button saveButton;
    private final Button saveAndNewButton;
    private final Button clearButton;
    private final Button cancelButton;
    private final List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors;
    /** Stored so that {@link #setReadOnly} can toggle its visibility at runtime. */
    private final Div footer;
    /** Bean class for {@link FieldWidthTier} resolution; {@code null} in PropertySet-mode. */
    private final Class<?> beanClass;

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
            List<TriConsumer<Component, Property<?>, Input<?>>> postProcessors,
            String title,
            boolean bordered,
            boolean showFooter,
            Class<?> beanClass,
            boolean autoLabels) {

        this.form = form;
        this.saveButton = saveBtn;
        this.saveAndNewButton = saveAndNewBtn;
        this.clearButton = clearBtn;
        this.cancelButton = cancelBtn;
        this.postProcessors = postProcessors;
        this.beanClass = beanClass;

        addClassName("entity-form-panel");
        if (bordered) {
            addClassName("entity-form-panel--bordered");
        }

        if (title != null && !title.isBlank()) {
            Div titleDiv = new Div(new Span(title));
            titleDiv.addClassName("entity-form-panel__title");
            add(titleDiv);
        }

        Div body = Components.div().add(form.getComponent()).styleName("entity-form-panel__body").build();

        form.getComponent().addAttachListener(e ->
                form.getElements()
                        .findFirst()
                        .ifPresent(input -> {
                            if (input.getComponent() instanceof Focusable<?> f) {
                                f.focus();
                            }
                        }));

        this.footer = Components.div().styleName("entity-form-panel__footer").build();

        if (showFooter && saveBtn != null) {
            saveBtn.addClassName("entity-form-panel__btn-save");
            saveBtn.addClickListener(e -> {
                try {
                    T value = valueSupplier.get();
                    saveAction.accept(value);
                } catch (Validator.ValidationException ignored) {
                }
            });
            footer.add(saveBtn);

            if (saveAndNewBtn != null) {
                saveAndNewBtn.addClassName("entity-form-panel__btn-save-new");
                saveAndNewBtn.addClickListener(e -> {
                    try {
                        T value = valueSupplier.get();
                        saveAndNewAction.accept(value);
                        form.clear();
                    } catch (Validator.ValidationException ignored) {
                    }
                });
                footer.add(saveAndNewBtn);
            }

            clearBtn.addClassName("entity-form-panel__btn-clear");
            clearBtn.getElement().removeAttribute("theme");
            clearBtn.addClickListener(e -> form.clear());
            footer.add(clearBtn);

            if (cancelBtn != null) {
                cancelBtn.addClassName("entity-form-panel__btn-cancel");
                cancelBtn.getElement().removeAttribute("theme");
                cancelBtn.addClickListener(e -> cancelAction.run());
                footer.add(cancelBtn);
            }
        } else if (!showFooter && saveAndNewBtn != null) {
            saveAndNewBtn.addClassName("entity-form-panel__btn-save-new");
        }

        if (showFooter) {
            add(body, footer);
        } else {
            add(body);
        }

        form.compose();
        if (autoLabels) {
            applyDefaultLabels(form);
        }

        if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
            form.getBindings().forEach(binding ->
                    this.postProcessors.forEach(postProcessor ->
                            postProcessor.accept(form.getComponent(), binding.getProperty(), binding.getElement())));
        }

        // ── Width-tier column-span (bean-mode only) ───────────────────────
        // When columnLengthAwareWidth is on (beanClass != null), each field is
        // assigned a proportional column span so the input still fills 100% of
        // its (tier-sized) cell — no max-width gaps.
        //
        //   FORM mode → setColspan() on the FormLayout
        //   GRID mode → tier-based col-span-N CSS classes via applyGridDivLayout
        if (layoutMode == LayoutMode.GRID) {
            // Build tier map once; empty when columnLengthAwareWidth is off (beanClass == null).
            java.util.Map<Component, FieldWidthTier> tierMap = beanClass != null
                    ? buildTierMap(form)
                    : java.util.Collections.emptyMap();
            applyGridDivLayout((Div) form.getComponent(), form, responsiveSteps, stretchLastRow, tierMap);
        } else {
            // FORM mode
            if (this.beanClass != null) {
                // Build component → tier map and apply FormLayout column spans.
                java.util.Map<Component, FieldWidthTier> tierMap = buildTierMap(form);
                FormLayout fl = (FormLayout) form.getComponent();
                int maxColumns = fl.getResponsiveSteps().stream()
                        .mapToInt(s -> {
                            var j = s.toJson();
                            return j != null && j.has("columns") ? Math.max(1, j.get("columns").asInt(1)) : 1;
                        })
                        .max().orElse(1);

                if (maxColumns > 1) {
                    tierMap.forEach((component, tier) -> {
                        int span = tier.formColspan(maxColumns);
                        if (span > 1) fl.setColspan(component, span);
                    });
                }
            }

            if (stretchLastRow) {
                applyStretchLastRow((FormLayout) form.getComponent(), form);
            }
        }
    }

    /** Builds component → FieldWidthTier map from form bindings. */
    private java.util.Map<Component, FieldWidthTier> buildTierMap(PropertyInputForm form) {
        java.util.Map<Component, FieldWidthTier> map = new java.util.IdentityHashMap<>();
        form.getBindings().forEach(binding -> {
            Property<?> prop = binding.getProperty();
            if (prop instanceof PathProperty<?> pp && beanClass != null) {
                int colLen = resolveColumnLength(beanClass, pp.relativeName());
                map.put(binding.getElement().getComponent(), FieldWidthTier.of(colLen));
            }
        });
        return map;
    }

    private static void applyDefaultLabels(PropertyInputForm form) {
        form.getBindings().forEach(binding -> {
            Property<?> property = binding.getProperty();
            binding.getElement().hasLabel().ifPresent(hasLabel -> {
                String current = hasLabel.getLabel();
                String rawName = property.getName();
                if (current == null || current.isBlank() || current.equals(rawName)) {
                    hasLabel.setLabel(toSentenceCase(rawName));
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



    private static void applyGridDivLayout(Div layout,
                                           PropertyInputForm form,
                                           List<FormLayout.ResponsiveStep> responsiveSteps,
                                           boolean stretchLastRow,
                                           java.util.Map<Component, FieldWidthTier> tierMap) {

        layout.addClassName("entity-form-panel__grid");
        layout.addClassNames("grid", "grid-cols-12", "gap-m");

        final List<Component> children = form.getBindings()
                .map(binding -> binding.getElement().getComponent())
                .toList();

        if (children.isEmpty()) {
            return;
        }

        final boolean hasTiers = tierMap != null && !tierMap.isEmpty();
        final List<GridStep> steps = normalizeGridSteps(responsiveSteps);

        for (GridStep step : steps) {
            final String prefix = step.prefix();
            final int columns = Math.max(1, step.columns());
            final int baseSpan = spanFor(columns);

            for (int index = 0; index < children.size(); index++) {
                Component child = children.get(index);

                if (hasTiers) {
                    // Tier-based span: short fields get fewer columns, long fields get more.
                    // All inputs still fill 100% of their cell.
                    FieldWidthTier tier = tierMap.getOrDefault(child, FieldWidthTier.UNCONSTRAINED);
                    addGridSpanClass(child, prefix, tier.gridSpan(columns));
                } else {
                    // Equal spans (default): every field occupies the same number of columns.
                    final int rowStart = index / columns * columns;
                    final int rowSize = Math.min(columns, children.size() - rowStart);
                    final int span = stretchLastRow && rowSize < columns ? spanFor(rowSize) : baseSpan;
                    addGridSpanClass(child, prefix, span);
                }
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

    /** Internal holder for a field-name bind call. */
    private record FieldBinding(String fieldName, Input<?> input) {}

    /** Internal holder for a typed {@link PathProperty} bind call. */
    private record PathPropertyBinding<V>(PathProperty<V> property, Input<V> input) {}

    /** Internal holder for a {@code required(fieldName, message)} call. */
    private record RequiredBinding(String fieldName, String message) {}

    /**
     * Applies stored {@link FieldBinding}s to the given form builder.
     * Each binding looks up the property by field name and, if found, replaces
     * the default auto-generated input with the supplied one.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T, C extends Component> void applyFieldBindings(
            BeanPropertyInputFormBuilder<C, T> beanFormBuilder,
            List<FieldBinding> fieldBindings) {
        if (fieldBindings == null || fieldBindings.isEmpty()) return;
        for (FieldBinding binding : fieldBindings) {
            beanFormBuilder.property(binding.fieldName()).ifPresent(p -> {
                final Input input = binding.input();
                beanFormBuilder.configure(f -> f.bind((Property) p, prop -> input));
            });
        }
    }

    /**
     * Applies stored {@link PathPropertyBinding}s to the given form builder.
     * Binds each typed {@link PathProperty} directly without a name lookup.
     */
    private static <T, C extends Component> void applyPathPropertyBindings(
            BeanPropertyInputFormBuilder<C, T> beanFormBuilder,
            List<PathPropertyBinding<?>> pathPropertyBindings) {
        if (pathPropertyBindings == null || pathPropertyBindings.isEmpty()) return;
        for (PathPropertyBinding<?> binding : pathPropertyBindings) {
            applyPathPropertyBinding(beanFormBuilder, binding);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T, V, C extends Component> void applyPathPropertyBinding(
            BeanPropertyInputFormBuilder<C, T> beanFormBuilder,
            PathPropertyBinding<V> binding) {
        final Input input = binding.input();
        beanFormBuilder.configure(f -> f.bind((Property) binding.property(), prop -> input));
    }

    /**
     * Applies stored {@link RequiredBinding}s to the given form builder.
     * Each binding marks the field as required with the supplied literal message,
     * bypassing the generic {@code holon.common.validation.message.required} key.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T, C extends Component> void applyRequiredBindings(
            BeanPropertyInputFormBuilder<C, T> beanFormBuilder,
            List<RequiredBinding> requiredBindings) {
        if (requiredBindings == null || requiredBindings.isEmpty()) return;
        for (RequiredBinding binding : requiredBindings) {
            beanFormBuilder.property(binding.fieldName()).ifPresent(p ->
                beanFormBuilder.configure(f -> f.required((Property) p, binding.message())));
        }
    }

    /**
     * Scans {@code beanClass} for {@code @NotBlank} / {@code @NotNull} / {@code @NotEmpty}
     * annotations and wires each field through the builder-level {@code required()} API so that:
     * <ul>
     *   <li>Custom messages (e.g. {@code @NotBlank(message = "Email is required")}) are forwarded
     *       verbatim — no {@code holon.common.validation.message.required} key lookup occurs.</li>
     *   <li>Fields using the default Jakarta message key fall back to Holon's default required message.</li>
     *   <li>Fields already covered by an explicit {@code .required(fieldName, message)} call are skipped.</li>
     * </ul>
     * Called instead of {@code panel.setAutoRequiredIndicators(true)} for bean-mode builders.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T, C extends Component> void applyAutoRequiredFromAnnotations(
            BeanPropertyInputFormBuilder<C, T> beanFormBuilder,
            Class<T> beanClass,
            List<RequiredBinding> explicitRequiredBindings) {

        final java.util.Set<String> explicitFields =
                explicitRequiredBindings == null || explicitRequiredBindings.isEmpty()
                ? java.util.Set.of()
                : explicitRequiredBindings.stream()
                        .map(RequiredBinding::fieldName)
                        .collect(java.util.stream.Collectors.toSet());

        Class<?> cls = beanClass;
        while (cls != null && cls != Object.class) {
            for (java.lang.reflect.Field field : cls.getDeclaredFields()) {
                final String fieldName = field.getName();
                if (explicitFields.contains(fieldName)) {
                    continue; // explicit required() call already handles this field
                }
                Optional<java.lang.annotation.Annotation> requiredAnnotation = java.util.Arrays
                        .stream(field.getAnnotations())
                        .filter(ann -> {
                            final String annSimpleName = ann.annotationType().getSimpleName();
                            return annSimpleName.equals("NotBlank") || annSimpleName.equals("NotNull")
                                    || annSimpleName.equals("NotEmpty");
                        })
                        .findFirst();
                if (requiredAnnotation.isPresent()) {
                    java.lang.annotation.Annotation ann = requiredAnnotation.get();
                    String rawMessage = null;
                    try {
                        rawMessage = (String) ann.annotationType().getMethod("message").invoke(ann);
                    } catch (Exception ignored) {
                    }

                    final boolean isDefaultKey = rawMessage == null
                            || rawMessage.startsWith("{jakarta.")
                            || rawMessage.startsWith("{javax.");

                    final String customMessage = isDefaultKey ? null : rawMessage;

                    beanFormBuilder.property(fieldName).ifPresent(p -> {
                        if (customMessage != null) {
                            beanFormBuilder.configure(f -> f.required((Property) p, Localizable.of(customMessage)));
                        } else {
                            beanFormBuilder.configure(f -> f.required((Property) p));
                        }
                    });
                }
            }
            cls = cls.getSuperclass();
        }
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

    /**
     * Triggers form validation and returns {@code true} if the form is valid.
     * <p>
     * Use this in wizard / multi-step contexts when the wizard's Next button
     * drives navigation instead of the form's own Save button.
     * Validation errors are shown inline — the same behaviour as pressing Save.
     * </p>
     *
     * @return {@code true} if the form passed validation, {@code false} otherwise
     */
    public boolean validate() {
        try {
            form.getValue();
            return true;
        } catch (Validator.ValidationException e) {
            return false;
        }
    }

    /**
     * Returns the current form values as a new bean instance, running validation.
     * <p>
     * Use in wizard / multi-step contexts to read values from a {@code noFooter()}
     * panel (which has no Save button to trigger the write-back). Builds a fresh
     * instance of the bean class and writes all field values into it.
     * </p>
     *
     * @return current bean value
     * @throws com.holonplatform.core.Validator.ValidationException if the form is invalid
     * @throws IllegalStateException if the underlying form is not bean-based
     */
    @SuppressWarnings("unchecked")
    public T getBean() {
        return getBean(true);
    }

    /**
     * Returns the current form values as a new bean instance.
     * <p>
     * Set {@code validate = false} to skip validation (e.g. on a review/summary step
     * where showing partial data is acceptable).
     * </p>
     *
     * @param validate {@code true} to run validation, {@code false} to skip it
     * @return current bean value
     * @throws com.holonplatform.core.Validator.ValidationException if {@code validate} is
     *         {@code true} and the form is invalid
     * @throws IllegalStateException if the underlying form is not bean-based
     */
    @SuppressWarnings("unchecked")
    public T getBean(boolean validate) {
        if (form instanceof BeanPropertyInputForm<?> bpif) {
            return ((BeanPropertyInputForm<T>) bpif).getBean(validate);
        }
        throw new IllegalStateException(
                "EntityFormPanel#getBean is only supported when the underlying form is bean-based");
    }

    /**
     * Switch the panel into or out of <em>read-only</em> mode.
     *
     * <p>When {@code true}:
     * <ul>
     *   <li>Every input bound to the form is set read-only.</li>
     *   <li>The button footer (Save / Clear / …) is hidden.</li>
     *   <li>The CSS modifier class {@code entity-form-panel--readonly} is applied to the
     *       root element so the host page can tune the visual appearance.</li>
     * </ul>
     *
     * <p>When {@code false} all three effects are reversed.  Note that if the panel was
     * originally built with {@link BeanBuilder#readOnly()} / {@link BeanBuilder#noFooter()},
     * the footer was never added to the DOM; toggling back to {@code false} will make the
     * inputs editable but the footer will remain absent.</p>
     *
     * @param readOnly {@code true} to enter read-only mode, {@code false} to leave it
     */
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            addClassName("entity-form-panel--readonly");
        } else {
            removeClassName("entity-form-panel--readonly");
        }
        form.getElements().forEach(input -> input.setReadOnly(readOnly));
        footer.setVisible(!readOnly);
    }

    /**
     * Returns {@code true} when the panel is currently in read-only mode.
     *
     * @return whether read-only mode is active
     * @see #setReadOnly(boolean)
     */
    public boolean isReadOnly() {
        return hasClassName("entity-form-panel--readonly");
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
         * Mark a specific bean field as required with a <strong>literal</strong> validation
         * error message, bypassing Holon's generic {@code holon.common.validation.message.required}
         * message-key lookup.
         *
         * <p>Use this instead of {@code autoRequiredIndicators(true)} whenever you need
         * per-field required messages. The field gains the required asterisk indicator
         * and the supplied message is shown verbatim when the field is left empty.</p>
         *
         * <pre>{@code
         * EntityFormPanel.bean(ContactBean.class)
         *     .required("firstName", "First name is required")
         *     .required("lastName",  "Last name is required")
         *     .required("email",     "Email is required")
         *     .noFooter()
         *     .build();
         * }</pre>
         *
         * @param fieldName bean field name (not null)
         * @param message   literal error message shown when the field is empty (not null)
         * @return this
         */
        BeanBuilder<T> required(String fieldName, String message);

        /**
         * Bind a specific bean field to a pre-built {@link Input}, replacing the default
         * auto-generated input for that field.
         *
         * <p>This is the short form of the nested
         * {@code configure(fb -> fb.configure(f -> { fb.property(fieldName).ifPresent(...) }))}
         * pattern. If {@code fieldName} does not match any field in the bean class the call
         * is silently ignored.</p>
         *
         * <pre>{@code
         * EntityFormPanel.<CompanyBean>bean(CompanyBean.class)
         *     .bind("industry",  Input.singleSelect(String.class).items("Technology", "Finance").build())
         *     .bind("companySize", Input.singleSelect(String.class).items("1–10", "11–50", "51+").build())
         *     .noFooter()
         *     .build();
         * }</pre>
         *
         * @param fieldName bean field name (not null)
         * @param input     the input to use for that field (not null)
         * @return this
         */
        BeanBuilder<T> bind(String fieldName, Input<?> input);

        /**
         * Bind a specific bean field to a pre-built {@link Input} using a typed
         * {@link PathProperty} reference, replacing the default auto-generated input.
         *
         * <p>Use this overload when you have a static property constant (e.g.
         * {@code Customer.NAME}, {@code CompanyBean.INDUSTRY}) and want compile-time
         * type safety:</p>
         *
         * <pre>{@code
         * EntityFormPanel.<CompanyBean>bean(CompanyBean.class)
         *     .bind(CompanyBean.INDUSTRY,
         *           Input.singleSelect(String.class).items("Technology", "Finance").build())
         *     .noFooter()
         *     .build();
         * }</pre>
         *
         * @param <V>      property value type
         * @param property the typed {@link PathProperty} (not null)
         * @param input    the input to use for that property (not null)
         * @return this
         */
        <V> BeanBuilder<T> bind(PathProperty<V> property, Input<V> input);

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
         * Suppress the button footer entirely.
         * <p>
         * Use this in wizard / multi-step contexts where navigation is driven by
         * an external component (e.g. {@code WizardFrame}). Validation is still
         * triggered externally via {@code getForm().getValue()}.
         * When this is set, {@code saveButton()} and {@code clearButton()} become
         * optional and calling them has no effect.
         *
         * @return this
         */
        BeanBuilder<T> noFooter();

        /**
         * Set an optional title displayed above the form body.
         *
         * @param title the title text (null or blank = no title)
         * @return this
         */
        BeanBuilder<T> title(String title);

        /**
         * Wrap the panel in a card-like border with padding and rounded corners.
         *
         * @param bordered whether to show the border
         * @return this
         */
        BeanBuilder<T> bordered(boolean bordered);

        /**
         * Enable annotation-driven input-width sizing ({@code @Column(length)}, {@code @Size(max)},
         * {@code @Min}/{@code @Max}).
         *
         * <p>When {@code true}, each input receives a CSS modifier class ({@code --xs}/{@code --sm}/{@code --md})
         * that constrains its {@code max-width} proportionally to the declared column length.
         *
         * <p>Default is {@code false} — all inputs fill their full form-cell width, producing a
         * consistent grid layout. Set to {@code true} only when you intentionally want narrow inputs
         * for short fields (e.g. a 2-char country code next to a long address in a compact form).</p>
         *
         * @param enable {@code true} to activate annotation-driven width tiers
         * @return this
         */
        BeanBuilder<T> columnLengthAwareWidth(boolean enable);

        /**
         * Automatically derive sentence-case labels from field names for inputs
         * that have no explicit label set.
         * <p>Default: {@code false} — labels are left exactly as provided.</p>
         *
         * @param autoLabels {@code true} to enable automatic label generation
         * @return this
         */
        BeanBuilder<T> autoLabels(boolean autoLabels);

        /**
         * Build the panel in <em>read-only</em> mode.
         *
         * <p>Calling this method is equivalent to calling {@link #noFooter()} and then
         * invoking {@link EntityFormPanel#setReadOnly(boolean) setReadOnly(true)} on the
         * resulting panel.  The mandatory {@code saveButton} / {@code clearButton} calls
         * are therefore not required when this flag is set.</p>
         *
         * <pre>{@code
         * EntityFormPanel<Customer> detail = EntityFormPanel.<Customer>bean(Customer.class)
         *     .autoLabels(true)
         *     .readOnly()
         *     .build();
         * detail.setBean(customer);   // populate fields for display
         * }</pre>
         *
         * @return this
         */
        BeanBuilder<T> readOnly();

        /**
         * Pre-populate the form with an initial bean value immediately after build.
         *
         * <p>Equivalent to calling {@link EntityFormPanel#setBean(Object)} right after
         * {@link #build()}, but keeps the intent in the builder chain:</p>
         *
         * <pre>{@code
         * EntityFormPanel<Product> form = EntityFormPanel.<Product>bean(Product.class)
         *     .autoLabels(true)
         *     .withBean(product)          // pre-populate on creation
         *     .saveButton(b -> b.text("Save"), this::onSave)
         *     .clearButton(b -> b.text("Clear"))
         *     .build();
         * }</pre>
         *
         * @param bean the bean instance to load into the form (may be {@code null} — no-op)
         * @return this
         */
        BeanBuilder<T> withBean(T bean);

        /**
         * Build the {@link EntityFormPanel}.
         *
         * @return a fully configured {@link EntityFormPanel}
         * @throws IllegalStateException if the mandatory Save or Clear button has not
         *                               been configured (and {@link #noFooter()} was not called)
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

        /** Binds a field by name to a pre-built input; see {@link BeanBuilder#bind}. */
        DivBeanBuilder<T> bind(String fieldName, Input<?> input);

        /** Marks a field as required with a literal message; see {@link BeanBuilder#required}. */
        DivBeanBuilder<T> required(String fieldName, String message);

        /** Binds a typed property reference to a pre-built input; see {@link BeanBuilder#bind}. */
        <V> DivBeanBuilder<T> bind(PathProperty<V> property, Input<V> input);

        DivBeanBuilder<T> saveButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                     Consumer<T> onSave);

        DivBeanBuilder<T> saveAndNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                         Consumer<T> onSaveAndNew);

        DivBeanBuilder<T> clearButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config);

        DivBeanBuilder<T> cancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> config,
                                       Runnable onCancel);

        /**
         * Suppress the button footer entirely.
         * @return this
         * @see BeanBuilder#noFooter()
         */
        DivBeanBuilder<T> noFooter();

        /** @see BeanBuilder#title(String) */
        DivBeanBuilder<T> title(String title);

        /** @see BeanBuilder#bordered(boolean) */
        DivBeanBuilder<T> bordered(boolean bordered);

        /** @see BeanBuilder#columnLengthAwareWidth(boolean) */
        DivBeanBuilder<T> columnLengthAwareWidth(boolean enable);

        /** @see BeanBuilder#autoLabels(boolean) */
        DivBeanBuilder<T> autoLabels(boolean autoLabels);

        /** @see BeanBuilder#readOnly() */
        DivBeanBuilder<T> readOnly();

        /**
         * Pre-populate the form with an initial bean value immediately after build.
         * @see BeanBuilder#withBean(Object)
         */
        DivBeanBuilder<T> withBean(T bean);

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
         * Suppress the button footer entirely.
         * @return this
         * @see BeanBuilder#noFooter()
         */
        PropertyBuilder noFooter();

        /** @see BeanBuilder#title(String) */
        PropertyBuilder title(String title);

        /** @see BeanBuilder#bordered(boolean) */
        PropertyBuilder bordered(boolean bordered);

        /**
         * Automatically derive sentence-case labels from property names for inputs
         * that have no explicit label set.
         * <p>Default: {@code false} — labels are left exactly as provided.</p>
         *
         * @param autoLabels {@code true} to enable automatic label generation
         * @return this
         */
        PropertyBuilder autoLabels(boolean autoLabels);

        /** @see BeanBuilder#readOnly() */
        PropertyBuilder readOnly();

        /**
         * Build the {@link EntityFormPanel}.
         *
         * @return a fully configured {@link EntityFormPanel}
         * @throws IllegalStateException if the mandatory Save or Clear button has not
         *                               been configured (and {@link #noFooter()} was not called)
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

        /**
         * Suppress the button footer entirely.
         * @return this
         * @see BeanBuilder#noFooter()
         */
        DivPropertyBuilder noFooter();

        /** @see BeanBuilder#title(String) */
        DivPropertyBuilder title(String title);

        /** @see BeanBuilder#bordered(boolean) */
        DivPropertyBuilder bordered(boolean bordered);

        /** @see BeanBuilder#autoLabels(boolean) */
        DivPropertyBuilder autoLabels(boolean autoLabels);

        /** @see BeanBuilder#readOnly() */
        DivPropertyBuilder readOnly();

        EntityFormPanel<PropertyBox> build();
    }

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
        /** Field name → Input overrides registered via {@link #bind(String, Input)}. */
        private final List<FieldBinding> fieldBindings = new ArrayList<>();
        /** PathProperty → Input overrides registered via {@link #bind(PathProperty, Input)}. */
        private final List<PathPropertyBinding<?>> pathPropertyBindings = new ArrayList<>();
        /** Field name → required message overrides registered via {@link #required(String, String)}. */
        private final List<RequiredBinding> requiredBindings = new ArrayList<>();

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveBtnConfig;
        private Consumer<T> saveAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveAndNewBtnConfig;
        private Consumer<T> saveAndNewAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> clearBtnConfig;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelBtnConfig;
        private Runnable cancelAction;
        private boolean noFooter;
        private boolean readOnly;
        private String title;
        private boolean bordered;
        /** Default false — inputs fill their full cell width for a consistent grid layout. */
        private boolean columnLengthAwareWidth = false;
        private boolean autoLabels = false;
        private T initialBean;

        DefaultBeanBuilder(Class<T> beanClass) {
            this.beanClass = beanClass;
        }

        @Override
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
        public BeanBuilder<T> bind(String fieldName, Input<?> input) {
            this.fieldBindings.add(new FieldBinding(fieldName, input));
            return this;
        }

        @Override
        public <V> BeanBuilder<T> bind(PathProperty<V> property, Input<V> input) {
            this.pathPropertyBindings.add(new PathPropertyBinding<>(property, input));
            return this;
        }

        @Override
        public BeanBuilder<T> required(String fieldName, String message) {
            this.requiredBindings.add(new RequiredBinding(fieldName, message));
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
        public BeanBuilder<T> noFooter() {
            this.noFooter = true;
            return this;
        }

        @Override
        public BeanBuilder<T> title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public BeanBuilder<T> bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        @Override
        public BeanBuilder<T> columnLengthAwareWidth(boolean enable) {
            this.columnLengthAwareWidth = enable;
            return this;
        }

        @Override
        public BeanBuilder<T> autoLabels(boolean autoLabels) {
            this.autoLabels = autoLabels;
            return this;
        }

        @Override
        public BeanBuilder<T> readOnly() {
            this.readOnly = true;
            this.noFooter = true;
            return this;
        }

        @Override
        public BeanBuilder<T> withBean(T bean) {
            this.initialBean = bean;
            return this;
        }

        @Override
        public EntityFormPanel<T> build() {
            if (!noFooter) {
                if (saveBtnConfig == null || saveAction == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...) or noFooter()");
                }
                if (clearBtnConfig == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...) or noFooter()");
                }
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
                    formConfig.accept(beanFormBuilder);
                }

                applyFieldBindings(beanFormBuilder, fieldBindings);
                applyPathPropertyBindings(beanFormBuilder, pathPropertyBindings);
                applyRequiredBindings(beanFormBuilder, requiredBindings);
                if (autoRequiredIndicators) {
                    applyAutoRequiredFromAnnotations(beanFormBuilder, beanClass, requiredBindings);
                }

                if (initializer != null) {
                    initializer.accept(content);
                }

                BeanPropertyInputForm<T> beanForm = beanFormBuilder.build();

                EntityFormPanel<T> panel = new EntityFormPanel<>(
                    beanForm,
                    beanForm::getBean,
                    noFooter ? null : makeButton(saveBtnConfig), noFooter ? null : saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    noFooter ? null : makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors,
                    title,
                    bordered,
                    !noFooter,
                    columnLengthAwareWidth ? beanClass : null,
                    autoLabels
                );
                if (readOnly) panel.setReadOnly(true);
                if (initialBean != null) panel.setBean(initialBean);
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

            applyFieldBindings(beanFormBuilder, fieldBindings);
            applyPathPropertyBindings(beanFormBuilder, pathPropertyBindings);
            applyRequiredBindings(beanFormBuilder, requiredBindings);
            if (autoRequiredIndicators) {
                applyAutoRequiredFromAnnotations(beanFormBuilder, beanClass, requiredBindings);
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
                    noFooter ? null : makeButton(saveBtnConfig), noFooter ? null : saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    noFooter ? null : makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.FORM,
                    stretchLastRow,
                    List.of(),
                    postProcessors,
                    title,
                    bordered,
                    !noFooter,
                    null,  // PropertySet-mode: no bean class for width-tier resolution
                    autoLabels
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            if (readOnly) panel.setReadOnly(true);
            if (initialBean != null) panel.setBean(initialBean);
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
        /** Field name → Input overrides registered via {@link #bind(String, Input)}. */
        private final List<FieldBinding> fieldBindings = new ArrayList<>();
        /** PathProperty → Input overrides registered via {@link #bind(PathProperty, Input)}. */
        private final List<PathPropertyBinding<?>> pathPropertyBindings = new ArrayList<>();
        /** Field name → required message overrides registered via {@link #required(String, String)}. */
        private final List<RequiredBinding> requiredBindings = new ArrayList<>();

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveBtnConfig;
        private Consumer<T> saveAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> saveAndNewBtnConfig;
        private Consumer<T> saveAndNewAction;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> clearBtnConfig;

        private Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelBtnConfig;
        private Runnable cancelAction;
        private boolean noFooter;
        private boolean readOnly;
        private String title;
        private boolean bordered;
        /** Default false — inputs fill their full cell width for a consistent grid layout. */
        private boolean columnLengthAwareWidth = false;
        private boolean autoLabels = false;
        private T initialBean;

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
        public DivBeanBuilder<T> bind(String fieldName, Input<?> input) {
            this.fieldBindings.add(new FieldBinding(fieldName, input));
            return this;
        }

        @Override
        public <V> DivBeanBuilder<T> bind(PathProperty<V> property, Input<V> input) {
            this.pathPropertyBindings.add(new PathPropertyBinding<>(property, input));
            return this;
        }

        @Override
        public DivBeanBuilder<T> required(String fieldName, String message) {
            this.requiredBindings.add(new RequiredBinding(fieldName, message));
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
        public DivBeanBuilder<T> noFooter() {
            this.noFooter = true;
            return this;
        }

        @Override
        public DivBeanBuilder<T> title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public DivBeanBuilder<T> bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        @Override
        public DivBeanBuilder<T> columnLengthAwareWidth(boolean enable) {
            this.columnLengthAwareWidth = enable;
            return this;
        }

        @Override
        public DivBeanBuilder<T> autoLabels(boolean autoLabels) {
            this.autoLabels = autoLabels;
            return this;
        }

        @Override
        public DivBeanBuilder<T> readOnly() {
            this.readOnly = true;
            this.noFooter = true;
            return this;
        }

        @Override
        public DivBeanBuilder<T> withBean(T bean) {
            this.initialBean = bean;
            return this;
        }

        @Override
        public EntityFormPanel<T> build() {
            if (!noFooter) {
                if (saveBtnConfig == null || saveAction == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...) or noFooter()");
                }
                if (clearBtnConfig == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...) or noFooter()");
                }
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

            applyFieldBindings(beanFormBuilder, fieldBindings);
            applyPathPropertyBindings(beanFormBuilder, pathPropertyBindings);
            applyRequiredBindings(beanFormBuilder, requiredBindings);
            if (autoRequiredIndicators) {
                applyAutoRequiredFromAnnotations(beanFormBuilder, beanClass, requiredBindings);
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
                    noFooter ? null : makeButton(saveBtnConfig), noFooter ? null : saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    noFooter ? null : makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors,
                    title,
                    bordered,
                    !noFooter,
                    columnLengthAwareWidth ? beanClass : null,
                    autoLabels
            );
            if (readOnly) panel.setReadOnly(true);
            if (initialBean != null) panel.setBean(initialBean);
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
        private boolean noFooter;
        private boolean readOnly;
        private String title;
        private boolean bordered;
        private boolean autoLabels = false;

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
        public DivPropertyBuilder noFooter() {
            this.noFooter = true;
            return this;
        }

        @Override
        public DivPropertyBuilder title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public DivPropertyBuilder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        @Override
        public DivPropertyBuilder autoLabels(boolean autoLabels) {
            this.autoLabels = autoLabels;
            return this;
        }

        @Override
        public DivPropertyBuilder readOnly() {
            this.readOnly = true;
            this.noFooter = true;
            return this;
        }

        @Override
        public EntityFormPanel<PropertyBox> build() {
            if (!noFooter) {
                if (saveBtnConfig == null || saveAction == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...) or noFooter()");
                }
                if (clearBtnConfig == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...) or noFooter()");
                }
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
                    noFooter ? null : makeButton(saveBtnConfig), noFooter ? null : saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    noFooter ? null : makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors,
                    title,
                    bordered,
                    !noFooter,
                    null,  // PropertySet-mode: no bean class for width-tier resolution
                    autoLabels
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            if (readOnly) panel.setReadOnly(true);
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
        private boolean noFooter;
        private boolean readOnly;
        private String title;
        private boolean bordered;
        private boolean autoLabels = false;

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
        public PropertyBuilder noFooter() {
            this.noFooter = true;
            return this;
        }

        @Override
        public PropertyBuilder title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public PropertyBuilder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        @Override
        public PropertyBuilder autoLabels(boolean autoLabels) {
            this.autoLabels = autoLabels;
            return this;
        }

        @Override
        public PropertyBuilder readOnly() {
            this.readOnly = true;
            this.noFooter = true;
            return this;
        }

        @Override
        public EntityFormPanel<PropertyBox> build() {
            if (!noFooter) {
                if (saveBtnConfig == null || saveAction == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: saveButton(config, action) is mandatory — call saveButton(...) or noFooter()");
                }
                if (clearBtnConfig == null) {
                    throw new IllegalStateException(
                            "EntityFormPanel: clearButton(config) is mandatory — call clearButton(...) or noFooter()");
                }
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
                    noFooter ? null : makeButton(saveBtnConfig), noFooter ? null : saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    noFooter ? null : makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.GRID,
                    stretchLastRow,
                    responsiveSteps,
                    postProcessors,
                    title,
                    bordered,
                    !noFooter,
                    null,  // PropertySet-mode: no bean class for width-tier resolution
                    autoLabels
                );
                panel.setAutoRequiredIndicators(autoRequiredIndicators);
                if (readOnly) panel.setReadOnly(true);
                return panel;
            }
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
                    noFooter ? null : makeButton(saveBtnConfig), noFooter ? null : saveAction,
                    saveAndNewBtnConfig != null ? makeButton(saveAndNewBtnConfig) : null, saveAndNewAction,
                    noFooter ? null : makeButton(clearBtnConfig),
                    cancelBtnConfig != null ? makeButton(cancelBtnConfig) : null, cancelAction,
                    LayoutMode.FORM,
                    stretchLastRow,
                    List.of(),
                    postProcessors,
                    title,
                    bordered,
                    !noFooter,
                    null,  // PropertySet-mode: no bean class for width-tier resolution
                    autoLabels
            );
            panel.setAutoRequiredIndicators(autoRequiredIndicators);
            if (readOnly) panel.setReadOnly(true);
            return panel;
        }
    }
}
