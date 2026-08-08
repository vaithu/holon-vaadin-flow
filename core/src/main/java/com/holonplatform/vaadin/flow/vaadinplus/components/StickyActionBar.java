package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * A fixed bottom action bar providing persistent action controls with an
 * optional status indicator and an optional completion-progress display.
 *
 * <p>Attach to the page view by adding it as the last child of the view's root layout.
 * The component uses {@code position: fixed} so it floats above the page content regardless
 * of scroll. It automatically respects the Vaadin AppLayout drawer width via the
 * {@code --vaadin-app-layout-drawer-offset-left} CSS variable, so it will never overlap
 * a {@code SideNav} / drawer sidebar.</p>
 *
 * <p>For an inline (non-sticky) action footer embedded in a form or dialog, use
 * {@link com.holonplatform.vaadin.flow.components.FormFooter} instead.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │ ● Status text · 8/13 fields  │ Profile 62% ▐░░│ [Btn] [Btn] [Btn] │
 * └──────────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * StickyActionBar bar = Components.stickyActionBar();
 * bar.setStatus("Auto-saved · 2 sec ago · 8/13 fields complete", StickyActionBar.Variant.SUCCESS);
 * bar.setProgress("Profile", 62);
 *
 * bar.addAction(
 *     Components.button().text("Discard").onClick(e -> confirmDiscard()).build(),
 *     Components.button().text("Save customer").primary().onClick(e -> handleSave()).build()
 * );
 *
 * // Optionally override the left offset (default auto-detects AppLayout drawer):
 * bar.setLeftOffset("240px");
 *
 * // Optionally override the bottom offset (default auto-detects AppLayout touch-optimized navbar):
 * bar.setBottomOffset("56px");
 * }</pre>
 *
 * <h3>CSS file</h3>
 * {@code META-INF/resources/sticky-action-bar.css} — BEM root: {@code .sab}
 */
@StyleSheet("context://sticky-action-bar.css")
public class StickyActionBar extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── BEM class constants ───────────────────────────────────────────────

    private static final String CSS_ROOT         = "sab";
    private static final String CSS_LEFT         = "sab__left";
    private static final String CSS_STATUS       = "sab__status";
    private static final String CSS_DOT          = "sab__dot";
    private static final String CSS_STATUS_TEXT  = "sab__status-text";
    private static final String CSS_PROGRESS     = "sab__progress";
    private static final String CSS_PROG_LABEL   = "sab__progress-label";
    private static final String CSS_PROG_TRACK   = "sab__progress-track";
    private static final String CSS_PROG_FILL    = "sab__progress-fill";
    private static final String CSS_ACTIONS      = "sab__actions";

    // ── DOM structure ─────────────────────────────────────────────────────

    private final Span statusDot      = new Span();
    private final Span statusText     = new Span();
    private final Span progressLabel  = new Span();
    private final Div  progressFill   = new Div();
    private final Div  progressBlock;
    private final Div  actionsDiv     = new Div();

    private Variant currentVariant;

    // ── Constructor ───────────────────────────────────────────────────────

    /**
     * Creates an empty {@code StickyActionBar} with no status, no progress, and no actions.
     */
    public StickyActionBar() {
        addClassName(CSS_ROOT);

        // Status block
        statusDot.addClassName(CSS_DOT);
        statusText.addClassName(CSS_STATUS_TEXT);
        Div statusBlock = new Div(statusDot, statusText);
        statusBlock.addClassName(CSS_STATUS);

        // Progress block (hidden by default)
        Div progressTrack = new Div(progressFill);
        progressTrack.addClassName(CSS_PROG_TRACK);
        progressFill.addClassName(CSS_PROG_FILL);
        progressLabel.addClassName(CSS_PROG_LABEL);
        progressBlock = new Div(progressLabel, progressTrack);
        progressBlock.addClassName(CSS_PROGRESS);
        progressBlock.setVisible(false);

        // Left side: status + progress
        Div left = new Div(statusBlock, progressBlock);
        left.addClassName(CSS_LEFT);

        // Right side: action buttons
        actionsDiv.addClassName(CSS_ACTIONS);

        add(left, actionsDiv);
    }

    // ── Status ────────────────────────────────────────────────────────────

    /**
     * Updates the status indicator text and dot colour.
     *
     * @param text    status message (e.g. {@code "Auto-saved · 2 sec ago"})
     * @param variant colour variant for the status dot
     * @return this (fluent)
     */
    public StickyActionBar setStatus(String text, Variant variant) {
        statusText.setText(text != null ? text : "");
        if (currentVariant != null) {
            statusDot.removeClassName(currentVariant.cssClass());
        }
        currentVariant = variant != null ? variant : Variant.SUCCESS;
        statusDot.addClassName(currentVariant.cssClass());
        // Reflect error state on the bar root so the background turns red
        if (currentVariant == Variant.ERROR) {
            addClassName("sab--error");
        } else {
            removeClassName("sab--error");
        }
        return this;
    }

    /**
     * Convenience overload using a {@link Localizable} status text.
     *
     * @param text    localizable status message
     * @param variant colour variant for the status dot
     * @return this (fluent)
     */
    public StickyActionBar setStatus(Localizable text, Variant variant) {
        return setStatus(
            LocalizationProvider.localize(text.getMessage(), text.getMessageCode()),
            variant
        );
    }

    // ── Progress ──────────────────────────────────────────────────────────

    /**
     * Shows (or updates) the inline progress indicator.
     *
     * <p>Pass {@code null} label to hide the indicator.</p>
     *
     * @param label   label shown before the bar (e.g. {@code "Profile"}); {@code null} hides the block
     * @param percent completion percentage 0–100 (clamped automatically)
     * @return this (fluent)
     */
    public StickyActionBar setProgress(String label, int percent) {
        if (label == null) {
            progressBlock.setVisible(false);
            return this;
        }
        int clamped = Math.max(0, Math.min(100, percent));
        progressLabel.setText(label + " " + clamped + "%");
        progressFill.getElement().setAttribute("style", "width:" + clamped + "%");
        progressBlock.setVisible(true);
        return this;
    }

    /**
     * Hides the progress block.
     *
     * @return this (fluent)
     */
    public StickyActionBar hideProgress() {
        progressBlock.setVisible(false);
        return this;
    }

    // ── Actions ───────────────────────────────────────────────────────────

    /**
     * Appends one or more components (typically {@link com.vaadin.flow.component.button.Button}s)
     * to the action area on the right.
     *
     * <p>Components are displayed in insertion order. Typically add secondary buttons first
     * and the primary action button last.</p>
     *
     * @param components the components to add
     * @return this (fluent)
     */
    public StickyActionBar addAction(Component... components) {
        actionsDiv.add(components);
        return this;
    }

    /**
     * Removes all action components.
     *
     * @return this (fluent)
     */
    public StickyActionBar clearActions() {
        actionsDiv.removeAll();
        return this;
    }

    // ── Left offset ───────────────────────────────────────────────────────

    /**
     * Overrides the {@code left} CSS offset used to avoid overlapping a fixed sidebar.
     *
     * <p>By default the bar automatically uses
     * {@code --vaadin-app-layout-drawer-offset-left} (set by Vaadin's {@code AppLayout})
     * so you usually do <em>not</em> need to call this.  Only use it when the sidebar is
     * not an AppLayout drawer (e.g. a custom fixed panel).</p>
     *
     * @param cssValue any valid CSS length value (e.g. {@code "64px"}, {@code "240px"}, {@code "0"})
     * @return this (fluent)
     */
    public StickyActionBar setLeftOffset(String cssValue) {
        getElement().getStyle().set("--sab-left", cssValue != null ? cssValue : "0");
        return this;
    }

    /**
     * Overrides the {@code bottom} CSS offset used to sit above a fixed bottom navbar.
     *
     * <p>By default the bar automatically uses
     * {@code --vaadin-app-layout-navbar-offset-bottom} (set by Vaadin's {@code AppLayout}
     * when {@code touchOptimized} is {@code true} and the navbar is placed at the bottom).
     * Only call this when using a custom bottom bar that is not an AppLayout navbar.</p>
     *
     * @param cssValue any valid CSS length value (e.g. {@code "56px"}, {@code "3rem"}, {@code "0"})
     * @return this (fluent)
     */
    public StickyActionBar setBottomOffset(String cssValue) {
        getElement().getStyle().set("--sab-bottom", cssValue != null ? cssValue : "0");
        return this;
    }

    // ── Variant ───────────────────────────────────────────────────────────

    /**
     * Semantic colour variant for the status indicator dot.
     */
    public enum Variant {

        /** Green pulsing dot — operation succeeded / data is current. */
        SUCCESS("sab__dot--success"),

        /** Amber pulsing dot — operation is in progress. */
        PENDING("sab__dot--pending"),

        /** Orange static dot — there are unsaved / unsubmitted changes. */
        WARNING("sab__dot--warning"),

        /** Red static dot — an error occurred. */
        ERROR("sab__dot--error");

        private final String cssClass;

        Variant(String cssClass) {
            this.cssClass = cssClass;
        }

        String cssClass() {
            return cssClass;
        }
    }
}
