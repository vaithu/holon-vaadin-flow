package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.property.PropertySet;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;

public class DefaultMasterDetailConfigurator<T>
        extends AbstractMasterDetailConfigurator<T, MasterDetailConfigurator.BaseMasterDetailConfigurator<T>>
        implements MasterDetailConfigurator.BaseMasterDetailConfigurator<T> {

    public DefaultMasterDetailConfigurator(MasterDetailLayout<T> component, Class<T> beanType) {
        super(component, beanType);
    }

    public DefaultMasterDetailConfigurator(MasterDetailLayout<T> component, PropertySet<?> propertySet) {
        super(component, propertySet);
    }

    @Override
    protected BaseMasterDetailConfigurator<T> getConfigurator() {
        return this;
    }
}
