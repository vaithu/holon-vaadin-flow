package com.iyensoft.vaadin.flow.internal.components.builders;

import java.util.Optional;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.NotFoundPage;
import com.iyensoft.vaadin.flow.components.builders.NotFoundPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

public abstract class AbstractNotFoundPageConfigurator<C extends NotFoundPageConfigurator<C>>

        extends AbstractComponentConfigurator<NotFoundPage, C>

        implements NotFoundPageConfigurator<C> {

    public AbstractNotFoundPageConfigurator(NotFoundPage component) {
        super(component);
    }

    @Override
    public C eyebrow(String text) {
        getComponent().setEyebrow(text);
        return getConfigurator();
    }

    @Override
    public C errorCode(String text) {
        getComponent().setErrorCode(text);
        return getConfigurator();
    }

    @Override
    public C message(String text) {
        getComponent().setMessage(text);
        return getConfigurator();
    }

    @Override
    public C buttonText(String text) {
        getComponent().setButtonText(text);
        return getConfigurator();
    }

    @Override
    public C branding(boolean visible) {
        getComponent().setBrandingVisible(visible);
        return getConfigurator();
    }

    @Override
    public C brandingLogo(Component logo) {
        getComponent().setBrandingLogo(logo);
        return getConfigurator();
    }

    @Override
    public C illustration(Component graphic) {
        getComponent().setIllustration(graphic);
        return getConfigurator();
    }

    @Override
    public C footer(boolean visible) {
        getComponent().setFooterVisible(visible);
        return getConfigurator();
    }

    @Override
    public C footerText(String text) {
        getComponent().setFooterText(text);
        return getConfigurator();
    }

    @Override
    public C homeTarget(String route) {
        getComponent().setHomeNavigationTarget(route);
        return getConfigurator();
    }

    @Override
    public C homeTarget(Class<? extends Component> target) {
        getComponent().setHomeNavigationTarget(target);
        return getConfigurator();
    }

    @Override
    public C withBackToHomeListener(ComponentEventListener<NotFoundPage.BackToHomeEvent> listener) {
        getComponent().addBackToHomeListener(listener);
        return getConfigurator();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
