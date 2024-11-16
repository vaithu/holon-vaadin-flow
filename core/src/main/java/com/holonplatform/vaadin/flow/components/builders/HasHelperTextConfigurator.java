package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.vaadin.flow.component.Component;

/**
 * Configurator for {@link com.vaadin.flow.component.HasHelper} type components.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.5.8
 */
public interface HasHelperTextConfigurator<C extends HasHelperTextConfigurator<C>> {

    /**
     * String used for the helper text. It shows a text adjacent to the field that can be used, e.g., to inform to the
     * users which values it expects. Example: a text "The password must contain numbers" for the PasswordField.
     * In case both setHelperText(String) and setHelperComponent(Component) are used, only the element defined by
     * setHelperComponent(Component) will be visible, regardless of the order on which they are defined.
     * @param helperText
     * @return
     */
    C helperText(String helperText);

    C helperText(Localizable helperText);



    /**
     * Adds the given component into helper slot of component, replacing any existing helper component.
     * It adds the component adjacent to the field that can be used,
     * e.g., to inform to the users which values it expects.
     * Example: a component that shows the password strength for the PasswordField.
     * @param component  the component to set, can be null to remove existing helper component
     * @return
     */
    C helperComponent(Component component);

}
