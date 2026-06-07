package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.AlertBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;

/**
 * Contextual alert component inspired by shadcn/ui Alert.
 *
 * <p>Composition:
 * <pre>
 * Alert
 *  ├── Icon            (optional — {@link #setIcon(Icon)})
 *  ├── AlertTitle      ({@link #setTitle(AlertTitle)} / {@link #setTitle(String)} / {@link #setTitle(Localizable)})
 *  ├── AlertDescription({@link #setDescription(AlertDescription)} / {@link #setDescription(String)})
 *  └── AlertAction     ({@link #setAction(AlertAction)} / {@link #setAction(Component...)})
 * </pre>
 *
 * <p>Preferred usage via builder:
 * <pre>{@code
 * Alert alert = Alert.builder(Alert.Variant.DESTRUCTIVE)
 *     .icon(new Icon(VaadinIcon.WARNING))
 *     .title(Localizable.builder()
 *             .message("Something went wrong")
 *             .messageCode("alert.title.error")
 *             .build())
 *     .description(Localizable.builder()
 *             .message("Your session expired. Please sign in again.")
 *             .messageCode("alert.desc.session-expired")
 *             .build())
 *     .action(new Button("Sign in"))
 *     .build();
 * }</pre>
 *
 * <p>All visual styling is handled by {@code alert.css}; no inline styles or Lumo tokens are used.
 * Variant colours are exposed as overridable CSS custom properties (e.g. {@code --alert-default-bg}).
 */
@StyleSheet("context://alert.css")
public class Alert extends Div {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Visual variant of the alert.
     */
    public enum Variant {

        DEFAULT("alert--default"),
        DESTRUCTIVE("alert--destructive"),
        WARNING("alert--warning"),
        SUCCESS("alert--success"),
        INFO("alert--info");

        private final String cssClass;

        Variant(String cssClass) {
            this.cssClass = cssClass;
        }

        /**
         * Returns the CSS BEM modifier class associated with this variant.
         *
         * @return the modifier class name (never null)
         */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final Div iconSlot;
    private final Div titleRow;
    private final Div contentSlot;

    private Variant currentVariant;
    private AlertTitle currentTitle;
    private AlertDescription currentDescription;
    private AlertAction currentAction;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a {@link Variant#DEFAULT} alert with no content.
     */
    public Alert() {
        this(Variant.DEFAULT);
    }

    /**
     * Creates an alert with the given variant and no content.
     *
     * @param variant the visual variant (not null)
     */
    public Alert(Variant variant) {
        addClassName("alert");
        getElement().setAttribute("role", "alert");

        this.iconSlot = Components.div().styleName("alert__icon").visible(false).build();

        // titleRow groups icon + title so they always appear side-by-side,
        // independent of outer layout or CSS alignment.
        this.titleRow = Components.div().styleName("alert__title-row").build();
        this.titleRow.add(iconSlot);

        this.contentSlot = Components.div().styleName("alert__content").build();
        this.contentSlot.add(titleRow);  // titleRow is always index 0

        add(contentSlot);
        setVariant(variant);
    }

    // -----------------------------------------------------------------------
    // Static factory
    // -----------------------------------------------------------------------

    /**
     * Obtain an {@link AlertBuilder} for a {@link Variant#DEFAULT} alert.
     *
     * @return a new {@link AlertBuilder}
     */
    public static AlertBuilder builder() {
        return AlertBuilder.create();
    }

    /**
     * Obtain an {@link AlertBuilder} for the given variant.
     *
     * @param variant the visual variant (not null)
     * @return a new {@link AlertBuilder}
     */
    public static AlertBuilder builder(Variant variant) {
        return AlertBuilder.create(variant);
    }

    // -----------------------------------------------------------------------
    // Variant API
    // -----------------------------------------------------------------------

    /**
     * Returns the current visual variant.
     *
     * @return the current {@link Variant} (never null after construction)
     */
    public Variant getVariant() {
        return currentVariant;
    }

    /**
     * Changes the visual variant, swapping the corresponding CSS modifier class.
     *
     * @param variant the new variant (not null)
     */
    public void setVariant(Variant variant) {
        if (this.currentVariant != null) {
            removeClassName(this.currentVariant.getCssClass());
        }
        this.currentVariant = variant;
        if (variant != null) {
            addClassName(variant.getCssClass());
        }
    }

    // -----------------------------------------------------------------------
    // Icon API
    // -----------------------------------------------------------------------

    /**
     * Sets the leading icon. The icon is placed inside the title row so it always
     * appears directly beside the title text. Adds the {@code alert--has-icon}
     * modifier class for any additional icon-specific CSS overrides.
     * Passing {@code null} clears the icon.
     *
     * @param icon the Vaadin icon to display, or {@code null} to clear
     */
    public void setIcon(Icon icon) {
        if (icon == null) {
            clearIcon();
            return;
        }
        iconSlot.removeAll();
        iconSlot.add(icon);
        iconSlot.setVisible(true);
        addClassName("alert--has-icon");
    }

    /**
     * Removes the leading icon and reverts to the single-column layout.
     */
    public void clearIcon() {
        iconSlot.removeAll();
        iconSlot.setVisible(false);
        removeClassName("alert--has-icon");
    }

    // -----------------------------------------------------------------------
    // Title API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link AlertTitle}, or {@code null} if not set.
     *
     * @return the current title component
     */
    public AlertTitle getAlertTitle() {
        return currentTitle;
    }

    /**
     * Sets (or replaces) the {@link AlertTitle}. Passing {@code null} removes it.
     *
     * @param title the title component, or {@code null} to remove
     */
    public void setTitle(AlertTitle title) {
        if (this.currentTitle != null) {
            titleRow.remove(this.currentTitle);
        }
        this.currentTitle = title;
        if (title != null) {
            titleRow.add(title);  // appended after iconSlot inside titleRow
        }
    }

    /**
     * Sets the title from a plain string.
     *
     * @param text the title text (not null)
     */
    public void setTitle(String text) {
        setTitle(new AlertTitle(text));
    }

    /**
     * Sets the title from a Holon {@link Localizable}.
     * The text is resolved on first attach; re-resolved on subsequent attaches.
     *
     * @param localizable the localizable message (not null)
     */
    public void setTitle(Localizable localizable) {
        setTitle(new AlertTitle(localizable));
    }

    // -----------------------------------------------------------------------
    // Description API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link AlertDescription}, or {@code null} if not set.
     *
     * @return the current description component
     */
    public AlertDescription getDescription() {
        return currentDescription;
    }

    /**
     * Sets (or replaces) the {@link AlertDescription}. Passing {@code null} removes it.
     *
     * @param description the description component, or {@code null} to remove
     */
    public void setDescription(AlertDescription description) {
        if (this.currentDescription != null) {
            contentSlot.remove(this.currentDescription);
        }
        this.currentDescription = description;
        if (description != null) {
            // titleRow is always at index 0; description always follows it at index 1
            contentSlot.addComponentAtIndex(1, description);
        }
    }

    /**
     * Sets the description from a plain string.
     *
     * @param text the description text (not null)
     */
    public void setDescription(String text) {
        setDescription(new AlertDescription(text));
    }

    /**
     * Sets the description from a Holon {@link Localizable}.
     * The text is resolved on first attach; re-resolved on subsequent attaches.
     *
     * @param localizable the localizable message (not null)
     */
    public void setDescription(Localizable localizable) {
        setDescription(new AlertDescription(localizable));
    }

    // -----------------------------------------------------------------------
    // Action API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link AlertAction}, or {@code null} if not set.
     *
     * @return the current action container
     */
    public AlertAction getAction() {
        return currentAction;
    }

    /**
     * Sets (or replaces) the {@link AlertAction}. Passing {@code null} removes it.
     *
     * @param action the action container, or {@code null} to remove
     */
    public void setAction(AlertAction action) {
        if (this.currentAction != null) {
            contentSlot.remove(this.currentAction);
        }
        this.currentAction = action;
        if (action != null) {
            contentSlot.add(action);
        }
    }

    /**
     * Wraps the given components in an {@link AlertAction} and sets it.
     *
     * @param actions action components (buttons, links, etc.)
     */
    public void setAction(Component... actions) {
        setAction(new AlertAction(actions));
    }
}



