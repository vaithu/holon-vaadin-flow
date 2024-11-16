package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.NotificationConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.data.binder.ValidationException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractNotificationConfigurator<C extends NotificationConfigurator<C>>
        extends AbstractComponentConfigurator<Notification, C> implements NotificationConfigurator<C> {


//    private HorizontalLayout layout = new HorizontalLayout();

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractNotificationConfigurator(Notification component) {
        super(component);
    }

    /**
     * Adds the given components as children of this component.
     *
     * @param components The components to add
     * @return this
     */
    @Override
    public C add(Component... components) {
        getComponent().add(components);
        return getConfigurator();
    }

    @Override
    public C addComponentAsFirst(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C addComponentAtIndex(int index, Component component) {
        getComponent().addComponentAtIndex(index, component);
        return getConfigurator();
    }

    @Override
    public C add(String text) {
        return text(text);
    }

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
    @Override
    public C withThemeVariants(NotificationVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    @Override
    public void close() {
        getComponent().close();
    }

    @Override
    public void open() {
        getComponent().open();
    }

    @Override
    public C duration(int duration) {
        getComponent().setDuration(duration);
        return getConfigurator();
    }

    @Override
    public C opened(boolean opened) {
        getComponent().setOpened(opened);
        return getConfigurator();
    }

    @Override
    public C position(Notification.Position position) {
        getComponent().setPosition(position);
        return getConfigurator();
    }

    @Override
    public C text(String text) {
        getComponent().add(new Text(text));
        return getConfigurator();
    }

    @Override
    public C error(ValidationException validationException) {
        validationException.getFieldValidationErrors().forEach(err -> err.getMessage().ifPresent(msg2 -> {
            String label = ((HasLabel) err.getBinding().getField()).getLabel();

           String text = label != null ? label + " -> " + msg2 : msg2;
            text(text);
            error();

        }));
        return getConfigurator();
    }

    @Override
    public C autoClose(boolean autoClose) {
        if (autoClose) {
            autoClose(duration);
        } else {
            autoClose(0);
        }
        return getConfigurator();
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
    }

    /**
     * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
     *
     * @return Optional component as {@link HasStyle}, if supported
     */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
     *
     * @return Optional component as {@link HasEnabled}, if supported
     */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
     *
     * @return Optional component as {@link HasTooltip}, if supported
     */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    @Override
    public C closeButton(boolean closeBtnEnabled) {
        if (closeBtnEnabled) {
            getComponent().add(createCloseBtn());
        }
        return getConfigurator();
    }

    @Override
    public C closeButton(Consumer<ButtonConfigurator> buttonConfigurator) {
        Button closeButton = new Button();
        buttonConfigurator.accept(ButtonConfigurator.configure(closeButton));
        add(closeButton);
        return getConfigurator();
    }

    @Override
    public C error(Exception e) {
        text(ExceptionUtils.getRootCauseMessage(e));
        error();
        return getConfigurator();
    }

    @Override
    public C div(Div div) {
        getComponent().add(div);
        return getConfigurator();
    }

    @Override
    public C icon(VaadinIcon icon) {
        getComponent().addComponentAsFirst(icon.create());
        return getConfigurator();
    }

    @Override
    public C icon(Icon icon) {
        getComponent().addComponentAsFirst(icon);
        return getConfigurator();
    }

    private Button createCloseBtn() {
        final Button closeButton = new Button(new Icon("lumo", "cross"), event -> close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.getStyle().setMargin("0 0 0 var(--lumo-space-l)");

        return closeButton;
    }
}