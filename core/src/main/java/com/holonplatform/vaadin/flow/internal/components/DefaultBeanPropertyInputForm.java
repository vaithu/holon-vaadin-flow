/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.Registration;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.beans.BeanProperty;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.beans.Ignore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.builders.BeanPropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.vaadin.flow.component.Component;

import java.io.Serial;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Default {@link BeanPropertyInputForm} implementation.
 *
 * <p>
 * Delegates all {@link PropertyInputForm} operations to an inner
 * {@link PropertyInputForm} instance built at construction time from the
 * filtered bean property set.  Bean-aware operations ({@link #setBean},
 * {@link #getBean}) use the full {@link BeanPropertySet} for conversion.
 * </p>
 *
 * @param <T> Bean type
 * @since 10.0.0
 */
public class DefaultBeanPropertyInputForm<T> implements BeanPropertyInputForm<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Underlying Holon form (handles all rendering / validation). */
    private final PropertyInputForm delegate;

    /** Full bean property set – used for setBean / getBean conversions. */
    private final BeanPropertySet<T> beanPropertySet;

    /**
     * Constructor.
     *
     * @param delegate        the pre-built {@link PropertyInputForm} (not null)
     * @param beanPropertySet the full bean property set (not null)
     */
    public DefaultBeanPropertyInputForm(PropertyInputForm delegate, BeanPropertySet<T> beanPropertySet) {
        ObjectUtils.argumentNotNull(delegate, "PropertyInputForm delegate must be not null");
        ObjectUtils.argumentNotNull(beanPropertySet, "BeanPropertySet must be not null");
        this.delegate = delegate;
        this.beanPropertySet = beanPropertySet;
    }

    // -----------------------------------------------------------------------
    // Bean-aware API
    // -----------------------------------------------------------------------

    @Override
    public void setBean(T bean) {
        if (bean == null) {
            delegate.setValue(null, false);
            return;
        }
        PropertyBox box = PropertyBox.builder(beanPropertySet).invalidAllowed(true).build();
        beanPropertySet.read(box, bean);
        // Populate only the properties present in the delegate form
        delegate.setValue(box, false);
    }

    @Override
    public T getBean() {
        return getBean(true);
    }

    @Override
    public T getBean(boolean validate) {
        PropertyBox box = delegate.getValue(validate);
        try {
            T instance = beanPropertySet.getBeanClass()
                    .getDeclaredConstructor()
                    .newInstance();
            return beanPropertySet.write(box, instance, true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Failed to instantiate bean class [" + beanPropertySet.getBeanClass().getName()
                            + "]. Ensure a public no-arg constructor is present.", e);
        }
    }

    // -----------------------------------------------------------------------
    // PropertyInputForm delegation
    // -----------------------------------------------------------------------

    @Override
    public void refresh() {
        delegate.refresh();
    }

    @Override
    public <V> boolean refresh(Property<V> property) {
        return delegate.refresh(property);
    }

    @Override
    public PropertyBox getValue(boolean validate) {
        return delegate.getValue(validate);
    }

    @Override
    public PropertyBox getValue() {
        return delegate.getValue();
    }

    @Override
    public Optional<PropertyBox> getValueIfValid() {
        return delegate.getValueIfValid();
    }

    @Override
    public void setValue(PropertyBox value, boolean validate) {
        delegate.setValue(value, validate);
    }

    @Override
    public void setValue(PropertyBox value) {
        delegate.setValue(value);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        delegate.setReadOnly(readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        delegate.setEnabled(enabled);
    }

    @Override
    public void validate() throws ValidationException {
        delegate.validate();
    }

    @Override
    public void validateInput(Property<?> property) throws ValidationException {
        delegate.validateInput(property);
    }

    @Override
    public <V> Optional<Input<V>> getInput(Property<V> property) {
        return delegate.getInput(property);
    }

    @Override
    public Collection<Property<?>> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public Stream<Binding<Property<?>, Input<?>>> getBindings() {
        return delegate.getBindings();
    }

    @Override
    public Optional<Input<?>> getElement(Property<?> property) {
        return delegate.getElement(property);
    }

    @Override
    public Stream<Input<?>> getElements() {
        return delegate.getElements();
    }

    @Override
    public Component getComponent() {
        return delegate.getComponent();
    }

    @Override
    public void compose() {
        delegate.compose();
    }

    @Override
    public void setAutoRequiredIndicators(boolean autoRequiredIndicators) {
        delegate.setAutoRequiredIndicators(autoRequiredIndicators);
    }

    @Override
    public boolean isAutoRequiredIndicators() {
        return delegate.isAutoRequiredIndicators();
    }

    @Override
    public PropertyBox getEmptyValue() {
        return delegate.getEmptyValue();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, Input<?>, PropertyInputGroup>> listener) {
        return delegate.addValueChangeListener(listener);
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Default {@link BeanPropertyInputFormBuilder} implementation.
     *
     * @param <C> Vaadin layout component type
     * @param <T> Bean type
     */
    public static class DefaultBuilder<C extends Component, T>
            implements BeanPropertyInputFormBuilder<C, T> {

        private final C content;
        private final BeanPropertySet<T> beanPropertySet;

        /** Field names to exclude entirely from the form. */
        private final Set<String> excludedFields = new LinkedHashSet<>();

        /** Field names to mark as read-only. */
        private final Set<String> readOnlyFields = new LinkedHashSet<>();

        /** Whether to expose @Identifier fields instead of hiding them. */
        private boolean showIdentifiers = false;

        /** Whether to expose @Version fields instead of hiding them. */
        private boolean showVersions = false;

        /** Additional configuration applied to the PropertyInputFormBuilder at build time. */
        private final List<Consumer<PropertyInputFormBuilder<C>>> extraConfigs = new ArrayList<>();

        /** Explicit bean field order requested by the caller. */
        private final List<String> explicitFieldOrder = new ArrayList<>();

        /**
         * Constructor.
         *
         * @param content   the layout component (not null)
         * @param beanClass the bean class to introspect (not null)
         */
        public DefaultBuilder(C content, Class<T> beanClass) {
            ObjectUtils.argumentNotNull(content, "Form content must be not null");
            ObjectUtils.argumentNotNull(beanClass, "Bean class must be not null");
            this.content = content;
            this.beanPropertySet = BeanPropertySet.create(beanClass);
        }

        @Override
        public BeanPropertyInputFormBuilder<C, T> excludeFields(String... fieldNames) {
            ObjectUtils.argumentNotNull(fieldNames, "Field names must be not null");
            Collections.addAll(excludedFields, fieldNames);
            return this;
        }

        @Override
        public BeanPropertyInputFormBuilder<C, T> readOnlyFields(String... fieldNames) {
            ObjectUtils.argumentNotNull(fieldNames, "Field names must be not null");
            Collections.addAll(readOnlyFields, fieldNames);
            return this;
        }

        @Override
        public BeanPropertyInputFormBuilder<C, T> showIdentifiers() {
            this.showIdentifiers = true;
            return this;
        }

        @Override
        public BeanPropertyInputFormBuilder<C, T> showVersions() {
            this.showVersions = true;
            return this;
        }

        @Override
        public BeanPropertyInputFormBuilder<C, T> properties(String... fieldNames) {
            ObjectUtils.argumentNotNull(fieldNames, "Field names must be not null");
            explicitFieldOrder.clear();
            Collections.addAll(explicitFieldOrder, fieldNames);
            return this;
        }

        @Override
        public Optional<PathProperty<?>> property(String fieldName) {
            ObjectUtils.argumentNotNull(fieldName, "Field name must be not null");
            if (excludedFields.contains(fieldName)) {
                return Optional.empty();
            }
            return asPathProperty(fieldName);
        }

        @Override
        public BeanPropertyInputFormBuilder<C, T> configure(Consumer<PropertyInputFormBuilder<C>> config) {
            ObjectUtils.argumentNotNull(config, "Configuration consumer must be not null");
            extraConfigs.add(config);
            return this;
        }

        @Override
        public BeanPropertyInputForm<T> build() {

            // 1. Build the filtered, ordered list of PathProperties.
            //    If the caller supplied an explicit field order, honor it exactly.
            //    Otherwise preserve BeanPropertySet declaration order and apply @Sequence.
            List<PathProperty<?>> orderedProperties = new ArrayList<>();
            if (!explicitFieldOrder.isEmpty()) {
                Set<String> seen = new HashSet<>();
                for (String fieldName : explicitFieldOrder) {
                    if (!seen.add(fieldName)) {
                        throw new IllegalArgumentException("Duplicate bean field name in explicit order: " + fieldName);
                    }
                    PathProperty<?> property = property(fieldName)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown or excluded bean field in explicit order: " + fieldName));
                    orderedProperties.add(property);
                }
            } else {
                Map<String, Integer> declarationOrder = new HashMap<>();
                int idx = 0;
                for (PathProperty<?> property : beanPropertySet) {
                    declarationOrder.put(property.relativeName(), idx++);
                    if (isIncludedProperty(property)) {
                        orderedProperties.add(property);
                    }
                }

                orderedProperties.sort((left, right) -> {
                    int cmp = Integer.compare(sequenceOrder(left), sequenceOrder(right));
                    if (cmp != 0) {
                        return cmp;
                    }
                    return Integer.compare(
                            declarationOrder.getOrDefault(left.relativeName(), Integer.MAX_VALUE),
                            declarationOrder.getOrDefault(right.relativeName(), Integer.MAX_VALUE));
                });
            }

            // 3. Create the standard PropertyInputFormBuilder with the filtered set.
            //    The Composable.Composer raw cast is intentional – componentContainerComposer()
            //    is inferred as Composer<HasComponents,...> while C is only bounded by Component.
            //    All concrete layout types (FormLayout, VerticalLayout, HorizontalLayout) do
            //    implement HasComponents, so the cast is safe at runtime.
            Composable.Composer rawComposer = Composable.componentContainerComposer();
            PropertyInputFormBuilder<C> fb = PropertyInputForm.builder(content, orderedProperties)
                    .composer(rawComposer);

            // 4. Apply annotation-driven configuration.
            for (PathProperty<?> prop : orderedProperties) {
                if (isAutoHidden(prop)) {
                    fb.hidden(prop);
                }
            }

            // 5. Apply read-only fields.
            for (PathProperty<?> prop : orderedProperties) {
                if (readOnlyFields.contains(prop.relativeName())) {
                    fb.readOnly(prop);
                }
            }

            // 6. Apply extra consumer-based configurations.
            for (Consumer<PropertyInputFormBuilder<C>> cfg : extraConfigs) {
                cfg.accept(fb);
            }

            // 7. Build the delegate form and wrap it.
            PropertyInputForm form = fb.build();
            return new DefaultBeanPropertyInputForm<>(form, beanPropertySet);
        }

        // -----------------------------------------------------------------------
        // Helpers
        // -----------------------------------------------------------------------

        @SuppressWarnings("unchecked")
        private Optional<PathProperty<?>> asPathProperty(String fieldName) {
            return (Optional<PathProperty<?>>) (Optional<?>) beanPropertySet.getProperty(fieldName)
                    .filter(this::isIncludedProperty);
        }

        private Optional<BeanProperty<?>> asBeanProperty(PathProperty<?> property) {
            if (property instanceof BeanProperty<?> beanProperty) {
                return Optional.of(beanProperty);
            }
            return Optional.empty();
        }

        private boolean isIncludedProperty(PathProperty<?> property) {
            if (excludedFields.contains(property.relativeName())) {
                return false;
            }
            return asBeanProperty(property)
                    .map(bp -> !bp.hasAnnotation(Ignore.class))
                    .orElse(true);
        }

        private int sequenceOrder(PathProperty<?> property) {
            return asBeanProperty(property)
                    .flatMap(BeanProperty::getSequence)
                    .orElse(Integer.MAX_VALUE);
        }

        private boolean isAutoHidden(PathProperty<?> property) {
            return asBeanProperty(property)
                    .map(bp -> (!showIdentifiers && bp.isIdentifier()) || (!showVersions && bp.isVersion()))
                    .orElse(false);
        }
    }
}

