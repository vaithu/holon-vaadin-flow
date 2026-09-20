package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.property.PropertySet;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;

/**
 * Default implementation of {@link MasterDetailBuilder}.
 */
public class DefaultMasterDetailBuilder<T>
        extends AbstractMasterDetailConfigurator<T, MasterDetailBuilder<T>>
        implements MasterDetailBuilder<T> {

    /** Bean-typed entry point. */
    public DefaultMasterDetailBuilder(Class<T> beanType) {
        super(new MasterDetailLayout<>(), beanType);
    }

    /** PropertySet-typed entry point (item type is {@code PropertyBox}). */
    public DefaultMasterDetailBuilder(PropertySet<?> propertySet) {
        super(new MasterDetailLayout<>(), propertySet);
    }

    @Override
    protected MasterDetailBuilder<T> getConfigurator() {
        return this;
    }

    @Override
    public MasterDetailLayout<T> build() {
        applyPostProcessors();
        return getComponent();
    }
}
