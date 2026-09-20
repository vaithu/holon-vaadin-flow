package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.PopoverConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/** Base {@link PopoverConfigurator} implementation. */
public abstract class AbstractPopoverConfigurator<C extends PopoverConfigurator<C>>
        extends AbstractComponentConfigurator<Popover, C>
        implements PopoverConfigurator<C> {

    protected AbstractPopoverConfigurator(Popover popover ) {
        super(popover );

    }

    @Override
    protected Optional<com.vaadin.flow.component.HasSize> hasSize() {
        return Optional.empty();
    }

    @Override
    protected Optional<com.vaadin.flow.component.HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<com.vaadin.flow.component.HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

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
        getComponent().add(text);
        return getConfigurator();
    }

    @Override
    public C autofocus(boolean autofocus) {
        getComponent().setAutofocus(autofocus);
        return getConfigurator();
    }

    @Override
    public C backdropVisible(boolean backdropVisible) {
        getComponent().setBackdropVisible(backdropVisible);
        return getConfigurator();
    }

    @Override
    public C closeOnEsc(boolean closeOnEsc) {
        getComponent().setCloseOnEsc(closeOnEsc);
        return getConfigurator();
    }

    @Override
    public C closeOnOutsideClick(boolean closeOnOutsideClick) {
        getComponent().setCloseOnOutsideClick(closeOnOutsideClick);
        return getConfigurator();
    }

    @Override
    public C defaultFocusDelay(int defaultFocusDelay) {
        Popover.setDefaultFocusDelay(defaultFocusDelay);
        return getConfigurator();
    }

    @Override
    public C defaultHideDelay(int defaultHideDelay) {
        Popover.setDefaultHideDelay(defaultHideDelay);
        return getConfigurator();
    }

    @Override
    public C defaultHoverDelay(int defaultHoverDelay) {
        Popover.setDefaultHoverDelay(defaultHoverDelay);
        return getConfigurator();
    }

    @Override
    public C focusDelay(int focusDelay) {
        getComponent().setFocusDelay(focusDelay);
        return getConfigurator();
    }

    @Override
    public C forId(String id) {
        getComponent().setFor(id);
        return getConfigurator();
    }

    @Override
    public C height(String height) {
        getComponent().setHeight(height);
        return getConfigurator();
    }

    @Override
    public C hideDelay(int hideDelay) {
        getComponent().setHideDelay(hideDelay);
        return getConfigurator();
    }

    @Override
    public C hoverDelay(int hoverDelay) {
        getComponent().setHoverDelay(hoverDelay);
        return getConfigurator();
    }

    @Override
    public C modal(boolean modal) {
        getComponent().setModal(modal);
        return getConfigurator();
    }

    @Override
    public C modal(boolean modal, boolean backdropVisible) {
        getComponent().setModal(modal, backdropVisible);
        return getConfigurator();
    }

    @Override
    public C opened(boolean opened) {
        getComponent().setOpened(opened);
        return getConfigurator();
    }

    @Override
    public C openOnClick(boolean openOnClick) {
        getComponent().setOpenOnClick(openOnClick);
        return getConfigurator();
    }

    @Override
    public C openOnFocus(boolean openOnFocus) {
        getComponent().setOpenOnFocus(openOnFocus);
        return getConfigurator();
    }

    @Override
    public C openOnHover(boolean openOnHover) {
        getComponent().setOpenOnHover(openOnHover);
        return getConfigurator();
    }

    @Override
    public C position(PopoverPosition position) {
        getComponent().setPosition(position);
        return getConfigurator();
    }

    @Override
    public C role(String role) {
        getComponent().setRole(role);
        return getConfigurator();
    }

    @Override
    public C tabFocusEnabled(boolean tabFocusEnabled) {
        getComponent().setTabFocusEnabled(tabFocusEnabled);
        return getConfigurator();
    }

    @Override
    public C target(Component target) {
        getComponent().setTarget(target);
        return getConfigurator();
    }

    @Override
    public C width(String width) {
        getComponent().setWidth(width);
        return getConfigurator();
    }

    @Override
    public void open() {
        getComponent().open();
    }

    @Override
    public C ariaLabel(String ariaLabel) {
        getComponent().setAriaLabel(ariaLabel);
        return getConfigurator();
    }

    @Override
    public C ariaLabelledBy(String ariaLabelledBy) {
        getComponent().setAriaLabelledBy(ariaLabelledBy);
        return getConfigurator();
    }

    @Override
    public C ariaLabel(Localizable ariaLabel) {
        if (ariaLabel == null) {
            return ariaLabel((String) null);
        }
        return ariaLabel(LocalizationProvider.localize(ariaLabel).orElse(ariaLabel.getMessage()));
    }

    @Override
    public C withThemeVariants(PopoverVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }
}
