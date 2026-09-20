package com.iyensoft.vaadin.flow.internal.components.builders;

import java.util.Optional;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.SignInPage;
import com.iyensoft.vaadin.flow.components.builders.SignInPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

public abstract class AbstractSignInPageConfigurator<C extends SignInPageConfigurator<C>>

        extends AbstractComponentConfigurator<SignInPage, C>

        implements SignInPageConfigurator<C> {

    public AbstractSignInPageConfigurator(SignInPage component) {
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
    public C socialLogin(boolean visible) {
        getComponent().setSocialLoginVisible(visible);
        return getConfigurator();
    }

    @Override
    public C keepLoggedIn(boolean visible) {
        getComponent().setKeepLoggedInVisible(visible);
        return getConfigurator();
    }

    @Override
    public C forgotPassword(boolean visible) {
        getComponent().setForgotPasswordVisible(visible);
        return getConfigurator();
    }

    @Override
    public C signUp(boolean visible) {
        getComponent().setSignUpVisible(visible);
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
    public C brandingContent(Component content) {
        getComponent().setBrandingContent(content);
        return getConfigurator();
    }

    @Override
    public C errorMessage(String message) {
        getComponent().setErrorMessage(message);
        return getConfigurator();
    }

    @Override
    public C withSignInListener(ComponentEventListener<SignInPage.SignInEvent> listener) {
        getComponent().addSignInListener(listener);
        return getConfigurator();
    }

    @Override
    public C withSocialSignInListener(ComponentEventListener<SignInPage.SocialSignInEvent> listener) {
        getComponent().addSocialSignInListener(listener);
        return getConfigurator();
    }

    @Override
    public C withForgotPasswordListener(ComponentEventListener<SignInPage.ForgotPasswordEvent> listener) {
        getComponent().addForgotPasswordListener(listener);
        return getConfigurator();
    }

    @Override
    public C withSignUpListener(ComponentEventListener<SignInPage.SignUpEvent> listener) {
        getComponent().addSignUpListener(listener);
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
