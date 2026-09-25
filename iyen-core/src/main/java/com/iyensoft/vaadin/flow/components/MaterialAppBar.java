package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.enums.MaterialAppBarColor;
import com.iyensoft.vaadin.flow.enums.MaterialAppBarVariant;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.holonplatform.vaadin.flow.components.support.ViewMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Material 3 app bar with explicit leading, headline, subtitle and trailing
 * content slots.
 */
@StyleSheet("context://material-app-bar.css")
public class MaterialAppBar extends Div implements HasTheme {

    private final Div leadingSlot = new Div();
    private final Div contentSlot = new Div();
    private final Div trailingSlot = new Div();
    private MaterialAppBarVariant variant = MaterialAppBarVariant.SMALL;
    private MaterialAppBarColor color = MaterialAppBarColor.NEUTRAL;
    private Button overflowButton;
    private ContextMenu overflowMenu;
    private final List<ResponsiveAction> responsiveActions = new ArrayList<>();
    private final List<OverflowAction> overflowActions = new ArrayList<>();
    private ViewMode viewMode = ViewMode.DESKTOP;

    public MaterialAppBar() {
        leadingSlot.addClassName("material-app-bar__leading");
        contentSlot.addClassName("material-app-bar__content");
        trailingSlot.addClassName("material-app-bar__trailing");

        addClassName("material-app-bar");
        addClassName("material-app-bar--small");
        setWidthFull();
        getElement().setAttribute("role", "banner");
        add(leadingSlot, contentSlot, trailingSlot);
    }

    public void setVariant(MaterialAppBarVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("variant must not be null");
        }
        removeClassName("material-app-bar--" + this.variant.getClassName());
        this.variant = variant;
        addClassName("material-app-bar--" + variant.getClassName());
    }

    public MaterialAppBarVariant getVariant() {
        return variant;
    }

    /**
     * Applies a predefined branded color variant. Pass {@link MaterialAppBarColor#NEUTRAL}
     * to reset to the default surface.
     *
     * @param color the color variant to apply (not null)
     */
    public void setColor(MaterialAppBarColor color) {
        if (color == null) {
            throw new IllegalArgumentException("color must not be null");
        }
        removeClassName("material-app-bar--color-" + this.color.getClassName());
        this.color = color;
        if (color != MaterialAppBarColor.NEUTRAL) {
            addClassName("material-app-bar--color-" + color.getClassName());
        }
    }

    public MaterialAppBarColor getColor() {
        return color;
    }

    /** Sets the responsive projection used by responsive actions. */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode must not be null");
        renderResponsiveActions();
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setHeadline(Component headline) {
        contentSlot.removeAll();
        if (headline != null) {
            headline.addClassName("material-app-bar__headline");
            contentSlot.add(headline);
        }
    }

    public void setHeadline(String headline) {
        setHeadline(headline == null ? null : new Span(headline));
    }

    public void setSubtitle(Component subtitle) {
        contentSlot.getChildren()
                .filter(component -> component.getClassNames().contains("material-app-bar__subtitle"))
                .findFirst()
                .ifPresent(contentSlot::remove);
        if (subtitle != null) {
            subtitle.addClassName("material-app-bar__subtitle");
            contentSlot.add(subtitle);
        }
    }

    public void addToLeading(Component... components) {
        leadingSlot.add(components);
    }

    public void addToTrailing(Component... components) {
        trailingSlot.add(components);
    }

    /**
     * Adds a secondary command to the app bar's overflow menu.
     * Use direct trailing actions only for the commands that must remain visible.
     *
     * @param label accessible, visible menu label
     * @param action command to run when the menu item is chosen
     */
    public void addOverflowAction(String label, Runnable action) {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");

        overflowActions.add(new OverflowAction(label, action));
        ensureOverflowMenu();
        overflowMenu.addItem(label, event -> action.run());
    }

    /**
     * Adds an action that is rendered as the supplied component on desktop and
     * as a labeled item in the More actions menu below 640 px.
     *
     * @param desktopComponent component to show inline on desktop
     * @param label accessible, visible mobile menu label
     * @param action command to run when the mobile menu item is chosen
     */
    public void addResponsiveAction(Component desktopComponent, String label, Runnable action) {
        responsiveActions.add(new ResponsiveAction(
                Objects.requireNonNull(desktopComponent, "desktopComponent must not be null"),
                Objects.requireNonNull(label, "label must not be null"),
                Objects.requireNonNull(action, "action must not be null")));
        renderResponsiveActions();
    }

    /** Adds a responsive action and applies the supplied initial projection. */
    public void addResponsiveAction(ViewMode viewMode, Component desktopComponent, String label, Runnable action) {
        setViewMode(viewMode);
        addResponsiveAction(desktopComponent, label, action);
    }

    private void renderResponsiveActions() {
        if (responsiveActions.isEmpty()) {
            return;
        }

        responsiveActions.forEach(action -> trailingSlot.remove(action.desktopComponent()));
        if (overflowButton != null) {
            trailingSlot.remove(overflowButton);
            overflowButton = null;
            overflowMenu = null;
        }

        if (viewMode.isMobile()) {
            ensureOverflowMenu();
            responsiveActions.forEach(action ->
                    overflowMenu.addItem(action.label(), event -> action.action().run()));
        } else {
            responsiveActions.forEach(action -> trailingSlot.add(action.desktopComponent()));
        }
    }

    private record ResponsiveAction(Component desktopComponent, String label, Runnable action) {
    }

    private void ensureOverflowMenu() {
        if (overflowMenu != null) {
            return;
        }

        overflowButton = new Button(VaadinIcon.ELLIPSIS_V.create());
        overflowButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        overflowButton.addClassName("material-app-bar__overflow");
        overflowButton.setAriaLabel(LocalizationProvider.localize("More actions", "material_app_bar.more_actions_aria"));
        trailingSlot.add(overflowButton);


        overflowMenu = new ContextMenu(overflowButton);
        overflowMenu.setOpenOnClick(true);
        overflowActions.forEach(action ->
            overflowMenu.addItem(action.label(), event -> action.action().run()));

    }

    public void setCentered(boolean centered) {
        getElement().getClassList().set("material-app-bar--centered", centered);
    }

    public void setSearch(boolean search) {
        getElement().getClassList().set("material-app-bar--search-active", search);
    }

    public void setScrolled(boolean scrolled) {
        getElement().getClassList().set("material-app-bar--scrolled", scrolled);
    }

    /**
     * Controls whether the app bar remains visible while its scroll container is scrolled.
     *
     * @param sticky {@code true} to make the app bar sticky, {@code false} to use normal flow
     */
    public void setSticky(boolean sticky) {
        getElement().getClassList().set("material-app-bar--sticky", sticky);
    }

    public Div getLeadingSlot() {
        return leadingSlot;
    }

    public Div getContentSlot() {
        return contentSlot;
    }

    public Div getTrailingSlot() {
        return trailingSlot;
    }

    /**
     * Returns the overflow ("⋯") {@link Button}, visible when overflow actions have been added
     * (e.g. for per-user visibility/authorization control).
     *
     * @return the overflow button, or {@code null} if no overflow actions have been configured
     */
    public Button getOverflowButton() {
        return overflowButton;
    }

    /**
     * Returns the {@link ContextMenu} attached to the overflow button.
     *
     * @return the overflow menu, or {@code null} if no overflow actions have been configured
     */
    public ContextMenu getOverflowMenu() {
        return overflowMenu;
    }

    private record OverflowAction(String label, Runnable action) {
    }
}
