package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.holonplatform.vaadin.flow.components.support.ViewMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Material 3 content header with explicit leading, headline, subtitle, and
 * trailing action slots.
 */
@StyleSheet("context://material-header.css")
public class MaterialHeader extends Div implements HasTheme {

    /** Material header sizes. */
    public enum Variant {
        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large");

        private final String className;

        Variant(String className) {
            this.className = className;
        }

        String getClassName() {
            return className;
        }
    }

    private final Div leadingSlot = new Div();
    private final Div mediaSlot = new Div();
    private final Div contentSlot = new Div();
    private final Div trailingSlot = new Div();
    private final Div headlineSlot = new Div();
    private final Div bodySlot = new Div();
    private final Div breadcrumbSlot = new Div();
    private final Div detailsSlot = new Div();
    private final Div tagsSlot = new Div();
    private final Div secondaryActionsSlot = new Div();
    private final Div primaryActionSlot = new Div();
    private final List<ResponsiveAction> responsiveActions = new ArrayList<>();
    private Variant variant = Variant.SMALL;
    private Button overflowButton;
    private ContextMenu overflowMenu;
    private ViewMode viewMode = ViewMode.DESKTOP;

    public MaterialHeader() {
        leadingSlot.addClassName("material-header__leading");
        mediaSlot.addClassName("material-header__media");
        contentSlot.addClassName("material-header__content");
        trailingSlot.addClassName("material-header__trailing");
        headlineSlot.addClassName("material-header__headline-slot");
        bodySlot.addClassName("material-header__body");
        breadcrumbSlot.addClassName("material-header__breadcrumb");
        detailsSlot.addClassName("material-header__details");
        tagsSlot.addClassName("material-header__tags");
        secondaryActionsSlot.addClassName("material-header__secondary-actions");
        primaryActionSlot.addClassName("material-header__primary-action");

        addClassNames("material-header", "material-header--small");
        setWidthFull();
        getElement().setAttribute("role", "region");
        contentSlot.add(headlineSlot, bodySlot);
        bodySlot.add(detailsSlot, tagsSlot);
    }

    public void setVariant(Variant variant) {
        this.variant = Objects.requireNonNull(variant, "variant must not be null");
        getClassNames().remove("material-header--small");
        getClassNames().remove("material-header--medium");
        getClassNames().remove("material-header--large");
        addClassName("material-header--" + variant.getClassName());
    }

    public Variant getVariant() {
        return variant;
    }

    /**
     * Sets the responsive projection used for responsive actions.
     * The component does not inspect browser dimensions; applications can
     * update this value from their existing responsive layout infrastructure.
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode must not be null");
        renderResponsiveActions();
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setHeadline(Component headline) {
        headlineSlot.removeAll();
        if (headline != null) {
            headline.addClassName("material-header__headline");
            headlineSlot.add(headline);
            ensureContentMounted();
        } else {
            ensureTopLevelSlotsMounted();
        }
    }

    public void setHeadline(String headline) {
        headlineSlot.removeAll();
        if (headline != null) {
            H3 h3 = Components.h3()
                    .text(headline)
                    .build();
            headlineSlot.add(h3);
            ensureContentMounted();
        } else {
            ensureTopLevelSlotsMounted();
        }

    }

    public void setSubtitle(Component subtitle) {
        bodySlot.getChildren()
                .filter(component -> component.getClassNames().contains("material-header__subtitle"))
                .findFirst()
            .ifPresent(bodySlot::remove);
        if (subtitle != null) {
            subtitle.addClassName("material-header__subtitle");
            bodySlot.addComponentAtIndex(0, subtitle);
            ensureContentMounted();
        } else {
            ensureTopLevelSlotsMounted();
        }
    }

    public void setBreadcrumb(Component breadcrumb) {
        breadcrumbSlot.removeAll();
        if (breadcrumb != null) {
            breadcrumb.addClassName("material-header__breadcrumb-content");
            breadcrumbSlot.add(breadcrumb);
            ensureBreadcrumbMounted();
        } else {
            ensureTopLevelSlotsMounted();
        }
    }

    public void setBreadcrumb(Breadcrumb breadcrumb) {
        setBreadcrumb((Component) breadcrumb);
    }

    public void setBreadcrumb(ListItem... items) {
        if (items == null || items.length == 0) {
            setBreadcrumb((Component) null);
            return;
        }
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.addWithSeparators(items);
        setBreadcrumb(breadcrumb);
    }

    /** Replaces the optional visual media shown before the header content. */
    public void setMedia(Component media) {
        mediaSlot.removeAll();
        if (media != null) {
            mediaSlot.add(media);
            if (mediaSlot.getParent().isEmpty()) {
                leadingSlot.addComponentAtIndex(0, mediaSlot);
            }
            ensureLeadingMounted();
        } else {
            mediaSlot.getParent().ifPresent(parent -> {
                if (parent == leadingSlot) {
                    leadingSlot.remove(mediaSlot);
                }
            });
            ensureTopLevelSlotsMounted();
        }
    }

    public void setDetails(Component... components) {
        detailsSlot.removeAll();
        if (components != null) {
            detailsSlot.add(components);
            ensureContentMounted();
        } else {
            ensureTopLevelSlotsMounted();
        }
    }

    public void setTags(Component... components) {
        tagsSlot.removeAll();
        if (components != null) {
            tagsSlot.add(components);
            ensureContentMounted();
        } else {
            ensureTopLevelSlotsMounted();
        }
    }

    public void setPrimaryAction(Component component) {
        primaryActionSlot.removeAll();
        if (component != null) {
            attachActionSlot(primaryActionSlot);
            primaryActionSlot.add(component);
            ensureTrailingMounted();
        } else {
            detachActionSlot(primaryActionSlot);
            ensureTopLevelSlotsMounted();
        }
    }

    public void addSecondaryAction(Component... components) {
        if (components != null) {
            attachActionSlot(secondaryActionsSlot);
            secondaryActionsSlot.add(components);
            ensureTrailingMounted();
        }
    }

    private void attachActionSlot(Div slot) {
        if (slot.getParent().isEmpty()) {
            trailingSlot.add(slot);
        }
    }

    private void detachActionSlot(Div slot) {
        slot.getParent().ifPresent(parent -> {
            if (parent == trailingSlot) {
                trailingSlot.remove(slot);
            }
        });
    }

    public void addToLeading(Component... components) {
        leadingSlot.add(components);
        ensureLeadingMounted();
    }

    public void addToTrailing(Component... components) {
        trailingSlot.add(components);
        ensureTrailingMounted();
    }

    /** Adds a command that is always available from the More actions menu. */
    public void addOverflowAction(String label, Runnable action) {
        ensureOverflowMenu();
        addMenuAction(label, action);
    }

    /**
     * Adds an action rendered inline on desktop and in More actions below 640 px.
     */
    public void addResponsiveAction(Component desktopComponent, String label, Runnable action) {
        responsiveActions.add(new ResponsiveAction(
                Objects.requireNonNull(desktopComponent, "desktopComponent must not be null"),
                Objects.requireNonNull(label, "label must not be null"),
                Objects.requireNonNull(action, "action must not be null")));
        renderResponsiveActions();
    }

    /** Adds an action and applies the supplied initial responsive projection. */
    public void addResponsiveAction(ViewMode viewMode, Component desktopComponent, String label, Runnable action) {
        setViewMode(viewMode);
        addResponsiveAction(desktopComponent, label, action);
    }

    private void renderResponsiveActions() {
        if (responsiveActions.isEmpty()) {
            return;
        }
        responsiveActions.forEach(action -> trailingSlot.remove(action.desktopComponent()));
        if (viewMode.isMobile()) {
            ensureOverflowMenu();
            responsiveActions.forEach(action -> addMenuAction(action.label(), action.action()));
        } else {
            responsiveActions.forEach(action -> trailingSlot.add(action.desktopComponent()));
        }
    }

    private void ensureOverflowMenu() {
        if (overflowMenu != null) {
            return;
        }
        overflowButton = new Button(VaadinIcon.ELLIPSIS_DOTS_V.create());
        overflowButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        overflowButton.addClassName("material-header__overflow");
        overflowButton.setAriaLabel(LocalizationProvider.localize("More actions", "material_header.more_actions_aria"));
        trailingSlot.add(overflowButton);
        overflowMenu = new ContextMenu(overflowButton);
        overflowMenu.setOpenOnClick(true);
    }

    private void addMenuAction(String label, Runnable action) {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        overflowMenu.addItem(label, event -> action.run());
    }

    private void ensureBreadcrumbMounted() {
        if (breadcrumbSlot.getComponentCount() > 0 && breadcrumbSlot.getParent().isEmpty()) {
            addComponentAtIndex(0, breadcrumbSlot);
        }
    }

    private void ensureLeadingMounted() {
        if (leadingSlot.getComponentCount() > 0 && leadingSlot.getParent().isEmpty()) {
            int index = breadcrumbSlot.getParent().isPresent() ? 1 : 0;
            addComponentAtIndex(index, leadingSlot);
        }
    }

    private void ensureContentMounted() {
        if ((headlineSlot.getComponentCount() > 0 || bodySlot.getComponentCount() > 0) 
                && contentSlot.getParent().isEmpty()) {
            int index = 0;
            if (breadcrumbSlot.getParent().isPresent()) index++;
            if (leadingSlot.getParent().isPresent()) index++;
            addComponentAtIndex(index, contentSlot);
        }
    }

    private void ensureTrailingMounted() {
        if (trailingSlot.getComponentCount() > 0 && trailingSlot.getParent().isEmpty()) {
            addComponentAtIndex(getComponentCount(), trailingSlot);
        }
    }

    private void ensureTopLevelSlotsMounted() {
        ensureBreadcrumbMounted();
        ensureLeadingMounted();
        ensureContentMounted();
        ensureTrailingMounted();
    }

    public Div getLeadingSlot() {
        return leadingSlot;
    }

    public Div getMediaSlot() {
        return mediaSlot;
    }

    public Div getBreadcrumbSlot() {
        return breadcrumbSlot;
    }

    public Div getContentSlot() {
        return contentSlot;
    }

    public Div getDetailsSlot() {
        return detailsSlot;
    }

    public Div getTagsSlot() {
        return tagsSlot;
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

    private record ResponsiveAction(Component desktopComponent, String label, Runnable action) {
    }
}
