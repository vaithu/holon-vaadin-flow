package com.iyensoft.vaadin.flow.internal.components.builders;
/**
 * Concrete Holon-style master-detail configurator.
 *
 * @param <T> item type
 */
public final class DefaultMasterDetailConfigurator<T>
        extends AbstractMasterDetailConfigurator<T, DefaultMasterDetailConfigurator<T>> {
    public DefaultMasterDetailConfigurator(Class<T> beanType) {
        super(beanType);
    }
    @Override
    protected DefaultMasterDetailConfigurator<T> getConfigurator() {
        return this;
    }
}
