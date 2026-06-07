package com.holonplatform.vaadin.flow.components.builders;

/**
 * Compatibility bridge for the recovered V2 master-detail configurator implementation.
 *
 * <p>This interface simply re-exports the real Holon/iyensoft configurator contract so
 * internal implementation files that still reference the legacy package continue to compile.
 * New code should use {@link com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator}.
 * </p>
 *
 * @param <T> item type
 * @param <C> parent configurator type
 */
public interface MasterDetailConfigurator<T, C extends MasterDetailConfigurator<T, C>>
        extends com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator<T, C> {
}

