package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;

import java.io.Serial;

/**
 * A clickable assignment row displaying an avatar, primary name, secondary role/description,
 * and an optional action link (e.g. "Change").
 *
 * <p>Typically used in forms to show the currently assigned person (account owner,
 * customer success manager, approver, etc.) with a quick-change affordance.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌──────────────────────────────────────────┐
 * │  [MS]  Maria Schmidt          [Change]   │
 * │        Account Executive                 │
 * └──────────────────────────────────────────┘
 * </pre>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * AssignmentPickerRow row = new AssignmentPickerRow("Maria Schmidt", "Account Executive · me");
 * row.setColorIndex(2);          // sets the avatar gradient colour (0–7)
 * row.addActionClickListener(e -> openPersonPickerDialog());
 *
 * // Update after user picks someone:
 * row.setName("Tom Bergmann");
 * row.setDescription("Senior CSM");
 * row.setColorIndex(5);
 * }</pre>
 *
 * <h3>Avatar initials</h3>
 * <p>Initials are automatically derived from the name: up to two words, first letter each.
 * Override with {@link #setInitials(String)} for custom display.</p>
 *
 * <h3>CSS file</h3>
 * {@code META-INF/resources/assignment-picker-row.css} — BEM root: {@code .apr}
 */
@StyleSheet("context://assignment-picker-row.css")
public class AssignmentPickerRow extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── BEM class constants ───────────────────────────────────────────────

    private static final String CSS_ROOT       = "apr";
    private static final String CSS_AVATAR     = "apr__avatar";
    private static final String CSS_INFO       = "apr__info";
    private static final String CSS_NAME       = "apr__name";
    private static final String CSS_DESC       = "apr__desc";
    private static final String CSS_ACTION     = "apr__action";
    private static final String CSS_COLOR_PFX  = "apr__avatar--color-";

    /** Number of distinct avatar colour presets defined in the CSS. */
    private static final int COLOR_PRESETS = 8;

    // ── DOM structure ─────────────────────────────────────────────────────

    private final Span initialsSpan = new Span();
    private final Span nameSpan     = new Span();
    private final Span descSpan     = new Span();
    private final Span actionSpan   = new Span();

    private int    colorIndex     = 0;
    private String overrideInitials;

    // ── Constructor ───────────────────────────────────────────────────────

    /**
     * Creates an {@code AssignmentPickerRow}.
     *
     * @param name        display name of the assigned person (initials derived automatically)
     * @param description secondary description, e.g. role or department (may be {@code null})
     */
    public AssignmentPickerRow(String name, String description) {
        addClassName(CSS_ROOT);
        getElement().setAttribute("role",    "button");
        getElement().setAttribute("tabindex", "0");

        // Avatar circle
        Div avatar = new Div(initialsSpan);
        avatar.addClassName(CSS_AVATAR);
        avatar.addClassName(CSS_COLOR_PFX + colorIndex);

        // Info block
        nameSpan.addClassName(CSS_NAME);
        descSpan.addClassName(CSS_DESC);
        Div info = new Div(nameSpan, descSpan);
        info.addClassName(CSS_INFO);

        // Action link
        actionSpan.addClassName(CSS_ACTION);
        actionSpan.setText(LocalizationProvider.localize("Change", "apr.action_label"));
        // Prevent the row's click event from also firing when the action span is clicked
        actionSpan.getElement().addEventListener("click", event -> {}).addEventData("event.stopPropagation()");

        add(avatar, info, actionSpan);
        setName(name);
        setDescription(description);
    }

    /**
     * Creates an {@code AssignmentPickerRow} with a {@link Localizable} name and description.
     *
     * @param name        localizable name
     * @param description localizable description (may be {@code null})
     */
    public AssignmentPickerRow(Localizable name, Localizable description) {
        this(
            LocalizationProvider.localize(name.getMessage(), name.getMessageCode()),
            description != null
                ? LocalizationProvider.localize(description.getMessage(), description.getMessageCode())
                : null
        );
    }

    // ── Name ──────────────────────────────────────────────────────────────

    /**
     * Updates the displayed name and re-derives initials (unless overridden via {@link #setInitials}).
     *
     * @param name new name; {@code null} clears the field
     * @return this (fluent)
     */
    public AssignmentPickerRow setName(String name) {
        nameSpan.setText(name != null ? name : "");
        if (overrideInitials == null) {
            initialsSpan.setText(deriveInitials(name));
        }
        return this;
    }

    // ── Description ───────────────────────────────────────────────────────

    /**
     * Updates the secondary description line (role, department, etc.).
     *
     * @param description new description; {@code null} or empty hides the line
     * @return this (fluent)
     */
    public AssignmentPickerRow setDescription(String description) {
        descSpan.setText(description != null ? description : "");
        descSpan.setVisible(description != null && !description.isEmpty());
        return this;
    }

    // ── Initials ──────────────────────────────────────────────────────────

    /**
     * Overrides the auto-derived initials shown in the avatar circle.
     *
     * @param initials custom initials (1–3 characters recommended); {@code null} restores auto-derivation
     * @return this (fluent)
     */
    public AssignmentPickerRow setInitials(String initials) {
        this.overrideInitials = initials;
        initialsSpan.setText(initials != null ? initials : deriveInitials(nameSpan.getText()));
        return this;
    }

    // ── Avatar colour ─────────────────────────────────────────────────────

    /**
     * Sets the avatar background colour using one of the pre-defined CSS presets (0–7).
     *
     * <p>Each index maps to a distinct gradient in the CSS.  The default is {@code 0} (blue).
     * A consistent index can be derived from a person's ID via {@code id % 8}.</p>
     *
     * @param index colour index in range [0, {@value #COLOR_PRESETS})
     * @return this (fluent)
     */
    public AssignmentPickerRow setColorIndex(int index) {
        Div avatar = (Div) getChildren()
                .filter(c -> c.getElement().getClassList().contains(CSS_AVATAR))
                .findFirst()
                .orElse(null);
        if (avatar != null) {
            avatar.removeClassName(CSS_COLOR_PFX + colorIndex);
            this.colorIndex = Math.abs(index) % COLOR_PRESETS;
            avatar.addClassName(CSS_COLOR_PFX + colorIndex);
        }
        return this;
    }

    // ── Action label ──────────────────────────────────────────────────────

    /**
     * Overrides the action link label (default: "Change").
     *
     * @param label new action label; {@code null} hides the action link
     * @return this (fluent)
     */
    public AssignmentPickerRow setActionLabel(String label) {
        actionSpan.setText(label != null ? label : "");
        actionSpan.setVisible(label != null && !label.isEmpty());
        return this;
    }

    /**
     * Hides the action link entirely.
     *
     * @return this (fluent)
     */
    public AssignmentPickerRow hideAction() {
        actionSpan.setVisible(false);
        return this;
    }

    // ── Action click listener ─────────────────────────────────────────────

    /**
     * Registers a listener called when the action link (e.g. "Change") is clicked.
     *
     * <p>The action click does <em>not</em> propagate to the row's own click listeners.</p>
     *
     * @param listener the listener to register
     * @return a {@link Registration} to remove the listener later
     */
    public Registration addActionClickListener(ComponentEventListener<ClickEvent<Span>> listener) {
        return actionSpan.addClickListener(listener);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Derives up to two initials from a full name string.
     *
     * <p>Examples: {@code "Maria Schmidt"} → {@code "MS"}, {@code "Tom"} → {@code "T"},
     * {@code null} or blank → {@code "?"}.</p>
     *
     * @param name the full name to derive initials from
     * @return the derived initials string
     */
    public static String deriveInitials(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
