package com.iyensoft.vaadin.flow.internal.components.builders;

import java.util.Optional;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.ResetPasswordPage;
import com.iyensoft.vaadin.flow.components.builders.ResetPasswordPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

public abstract class AbstractResetPasswordPageConfigurator<C extends ResetPasswordPageConfigurator<C>>

        extends AbstractComponentConfigurator<ResetPasswordPage, C>

        implements ResetPasswordPageConfigurator<C> {

    public AbstractResetPasswordPageConfigurator(ResetPasswordPage component) {
        super(component);
    }

    @Override
    public C heading(String text) {
        getComponent().setHeading(text);
        return getConfigurator();
    }

    @Override
    public C subtitle(String text) {
        getComponent().setSubtitle(text);
        return getConfigurator();
    }

    @Override
    public C signIn(boolean visible) {
        getComponent().setSignInVisible(visible);
        return getConfigurator();
    }

    @Override
    public C branding(boolean visible) {
        getComponent().setBrandingVisible(visible);
        return getConfigurator();
    }

    @Override
    public C brandingTitle(String text) {
        getComponent().setBrandingTitle(text);
        return getConfigurator();
    }

    @Override
    public C brandingText(String text) {
        getComponent().setBrandingText(text);
        return getConfigurator();
    }

    @Override
    public C brandingLogo(Component logo) {
        getComponent().setBrandingLogo(logo);
        return getConfigurator();
    }

    @Override
    public C errorMessage(String message) {
        getComponent().setErrorMessage(message);
        return getConfigurator();
    }

    @Override
    public C withResetPasswordListener(ComponentEventListener<ResetPasswordPage.ResetPasswordEvent> listener) {
        getComponent().addResetPasswordListener(listener);
        return getConfigurator();
    }

    @Override
    public C withBackToSignInListener(ComponentEventListener<ResetPasswordPage.BackToSignInEvent> listener) {
        getComponent().addBackToSignInListener(listener);
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
