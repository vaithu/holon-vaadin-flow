package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;

/**
 * A full-page multi-step entity creation form — a generic, reusable layout that
 * composes a page header (breadcrumb + title + badge + actions), a vertically stacked
 * list of {@link FormStepCard} steps, and a {@link StickyActionBar} at the bottom.
 *
 * <p>All mutable parts of the form (title, subtitle, draft badge, sticky bar status
 * and progress) can be updated at any time after construction, making it straightforward
 * to build reactive creation flows where the UI reflects save state, field completion,
 * or step advancement in real time.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │  breadcrumb                                                          │
 * │  Title ● Draft                 [Action …] [Action …] [Primary Btn]  │  ← .ecf__page-head
 * │  subtitle / hint text                                                │
 * ├──────────────────────────────────────────────────────────────────────┤
 * │  ┌──── FormStepCard 1 (DONE)  ───────────────────────────────────┐  │
 * │  └────────────────────────────────────────────────────────────────┘  │  ← .ecf__main
 * │  ┌──── FormStepCard 2 (CURRENT) ─────────────────────────────────┐  │
 * │  └────────────────────────────────────────────────────────────────┘  │
 * ├──────────────────────────────────────────────────────────────────────┤
 * │  ● Status text · progress bar          [Discard] [Save & New] [Save] │  ← StickyActionBar
 * └──────────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>Build-time configuration — via the builder</h3>
 * <pre>{@code
 * EntityCreationForm form = Components.entityCreationForm()
 *     .breadcrumb(
 *         new BreadcrumbItem(new Span("Contacts")),
 *         new BreadcrumbPage("New contact")
 *     )
 *     .title("New contact")
 *     .subtitle("All required fields are marked with *")
 *     .draftBadge("Unsaved draft")
 *     .headerAction(Components.button().text("Discard").build())
 *     .headerAction(Components.button().text("Save contact").primary().build())
 *     .step(step1)
 *     .step(step2)
 *     .status("Draft — not saved yet", StickyActionBar.Variant.WARNING)
 *     .progress("Profile", 0)
 *     .barAction(Components.button().text("Discard").build())
 *     .barAction(Components.button().text("Save contact").primary()
 *         .onClick(e -> {
 *             service.save(data);
 *             form.setStatus("Saved", StickyActionBar.Variant.SUCCESS);
 *             form.setProgress("Profile", 100);
 *             form.setDraftBadge(null);   // clear the draft badge
 *         }).build())
 *     .build();
 * }</pre>
 *
 * <h3>Run-time mutation</h3>
 * <pre>{@code
 * // Update sticky bar after save:
 * form.setStatus("Saved · just now", StickyActionBar.Variant.SUCCESS);
 * form.setProgress("Profile", 100);
 *
 * // React to field changes:
 * nameField.addValueChangeListener(e -> form.setTitle(e.getValue()));
 *
 * // Advance a step:
 * step1.setState(FormStepCard.StepState.DONE);
 * step2.setState(FormStepCard.StepState.CURRENT);
 *
 * // Add a step dynamically (e.g. conditional step):
 * form.addStep(optionalStep);
 *
 * // Add more bar actions at runtime:
 * form.addBarAction(Components.button().text("Preview").build());
 * }</pre>
 *
 * <h3>CSS file</h3>
 * {@code META-INF/resources/entity-creation-form.css} — BEM root: {@code .ecf}
 *
 * @see EntityCreationFormBuilder
 * @see FormStepCard
 * @see StickyActionBar
 */
@StyleSheet("context://entity-creation-form.css")
public class EntityCreationForm extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    static final String CSS_ROOT = "ecf";

    // ── Draft badge colour variants ───────────────────────────────────────

    /**
     * Colour variant for the draft-status chip badge rendered next to the page title.
     *
     * <p>Communicated to CSS via a {@code data-badge-variant} attribute on the chip element,
     * so the visual appearance is fully controlled by {@code entity-creation-form.css}.</p>
     */
    public enum DraftBadgeVariant implements Serializable {
        /** Amber/yellow — unsaved or has unsaved changes. */
        WARNING,
        /** Red — save attempt failed. */
        ERROR,
        /** Green — successfully saved. */
        SUCCESS
    }

    // ── Mutable DOM references ────────────────────────────────────────────

    private final Span  titleSpan;
    private final Span  subtitleSpan;
    private final Div   titleRow;
    private       Chip  draftBadgeChip;
    private final Div   mainCol;
    private final StickyActionBar actionBar;

    // ── Constructor (package-private) ─────────────────────────────────────

    EntityCreationForm(Span titleSpan, Span subtitleSpan, Div titleRow,
                       Chip draftBadgeChip, Div mainCol, StickyActionBar actionBar) {
        addClassName(CSS_ROOT);
        this.titleSpan      = titleSpan;
        this.subtitleSpan   = subtitleSpan;
        this.titleRow       = titleRow;
        this.draftBadgeChip = draftBadgeChip;
        this.mainCol        = mainCol;
        this.actionBar      = actionBar;
    }

    // ── Header — title ────────────────────────────────────────────────────

    /**
     * Updates the main title text shown in the page header.
     * @param title new title; {@code null} or blank clears the title span
     * @return this (fluent)
     */
    public EntityCreationForm setHeader(String title) {
        titleSpan.setText(title != null ? title : "");
        return this;
    }

    // ── Header — subtitle ─────────────────────────────────────────────────

    /**
     * Updates the subtitle / hint text shown below the title.
     *
     * @param subtitle new subtitle; {@code null} or blank clears the text
     * @return this (fluent)
     */
    public EntityCreationForm setSubtitle(String subtitle) {
        subtitleSpan.getElement().removeAllChildren();
        if (subtitle != null && !subtitle.isBlank()) {
            subtitleSpan.setText(subtitle);
            subtitleSpan.setVisible(true);
        } else {
            subtitleSpan.setVisible(false);
        }
        return this;
    }

    /**
     * Updates the subtitle slot with an arbitrary component — e.g. a composed
     * span with a red required-field asterisk.
     *
     * @param component component to display as the subtitle; {@code null} hides the slot
     * @return this (fluent)
     */
    public EntityCreationForm setSubtitle(Component component) {
        subtitleSpan.getElement().removeAllChildren();
        if (component != null) {
            subtitleSpan.add(component);
            subtitleSpan.setVisible(true);
        } else {
            subtitleSpan.setVisible(false);
        }
        return this;
    }

    // ── Header — draft badge ──────────────────────────────────────────────

    /**
     * Updates or hides the draft-status chip badge next to the title,
     * with an explicit colour variant.
     *
     * <p>Pass {@code null} (or a blank string) to hide the badge entirely.</p>
     *
     * @param badgeText badge label; {@code null} or blank hides the badge
     * @param variant   colour variant; {@code null} defaults to {@link DraftBadgeVariant#WARNING}
     * @return this (fluent)
     */
    public EntityCreationForm setDraftBadge(String badgeText, DraftBadgeVariant variant) {
        if (badgeText == null || badgeText.isBlank()) {
            if (draftBadgeChip != null) {
                draftBadgeChip.setVisible(false);
            }
        } else {
            if (draftBadgeChip == null) {
                draftBadgeChip = new Chip(badgeText);
                draftBadgeChip.getElement().setAttribute("tabindex", "-1");
                draftBadgeChip.getElement().getStyle().set("cursor", "default");
                titleRow.add(draftBadgeChip);
            } else {
                draftBadgeChip.setLabel(badgeText);
                draftBadgeChip.setVisible(true);
            }
            draftBadgeChip.getElement().setAttribute(
                    "data-badge-variant",
                    (variant != null ? variant : DraftBadgeVariant.WARNING).name().toLowerCase());
        }
        return this;
    }

    /**
     * Updates or hides the draft-status chip badge next to the title.
     * When showing, defaults to {@link DraftBadgeVariant#WARNING}.
     *
     * @param badgeText badge label; {@code null} or blank hides the badge
     * @return this (fluent)
     */
    public EntityCreationForm setDraftBadge(String badgeText) {
        return setDraftBadge(badgeText,
                badgeText != null && !badgeText.isBlank() ? DraftBadgeVariant.WARNING : null);
    }

    // ── Steps ─────────────────────────────────────────────────────────────

    /**
     * Appends a {@link FormStepCard} to the main content column at runtime.
     *
     * <p>Use this for conditionally shown steps that are determined after the
     * form is built.</p>
     *
     * @param card the step card to add; {@code null} is silently ignored
     * @return this (fluent)
     */
    public EntityCreationForm addStep(FormStepCard card) {
        if (card != null) {
            mainCol.add(card);
        }
        return this;
    }

    // ── Sticky bar — status ───────────────────────────────────────────────

    /**
     * Updates the status indicator text and colour variant in the sticky action bar.
     *
     * <p>Call this after every auto-save, validation pass, or field-completion
     * check to keep the bar in sync with form state.</p>
     *
     * @param text    status message (e.g. {@code "Saved · just now · 12/13 fields complete"})
     * @param variant colour variant for the status indicator dot
     * @return this (fluent)
     */
    public EntityCreationForm setStatus(String text, StickyActionBar.Variant variant) {
        actionBar.setStatus(text, variant);
        return this;
    }

    // ── Sticky bar — progress ─────────────────────────────────────────────

    /**
     * Updates the completion-progress indicator in the sticky action bar.
     *
     * <p>Recalculate and call this whenever a field is filled or a step is
     * completed.</p>
     *
     * @param label   progress label (e.g. {@code "Profile"}); {@code null} hides the block
     * @param percent completion percentage 0–100
     * @return this (fluent)
     */
    public EntityCreationForm setProgress(String label, int percent) {
        actionBar.setProgress(label, percent);
        return this;
    }

    /**
     * Hides the progress block in the sticky action bar.
     *
     * @return this (fluent)
     */
    public EntityCreationForm hideProgress() {
        actionBar.hideProgress();
        return this;
    }

    // ── Sticky bar — actions ──────────────────────────────────────────────

    /**
     * Appends one or more components to the sticky action bar at runtime.
     *
     * @param components components to add; {@code null} entries are silently ignored
     * @return this (fluent)
     */
    public EntityCreationForm addBarAction(Component... components) {
        actionBar.addAction(components);
        return this;
    }

    /**
     * Returns the underlying {@link StickyActionBar} for direct fine-grained control.
     *
     * @return the action bar (never {@code null})
     */
    public StickyActionBar getActionBar() {
        return actionBar;
    }
}
