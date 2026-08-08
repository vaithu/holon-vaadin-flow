package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link EntityCreationForm} — a generic multi-step entity
 * creation layout with a page header, stacked {@link FormStepCard} steps, and a
 * {@link StickyActionBar}.
 *
 * <p>The builder configures the <em>initial</em> state of the form.  Everything
 * set here can be updated at runtime via the mutator methods on the returned
 * {@link EntityCreationForm}: {@link EntityCreationForm#setTitle setTitle},
 * {@link EntityCreationForm#setSubtitle setSubtitle},
 * {@link EntityCreationForm#setDraftBadge setDraftBadge},
 * {@link EntityCreationForm#setStatus setStatus},
 * {@link EntityCreationForm#setProgress setProgress},
 * {@link EntityCreationForm#addStep addStep}, and
 * {@link EntityCreationForm#addBarAction addBarAction}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * EntityCreationForm form = Components.entityCreationForm()
 *     .breadcrumb(
 *         new BreadcrumbItem(new Span("Products")),
 *         new BreadcrumbPage("New product")
 *     )
 *     .title("New product")
 *     .subtitle("All required fields are marked with *")
 *     .draftBadge("Unsaved draft")
 *     .headerAction(Components.button().text("Discard").build())
 *     .headerAction(Components.button().text("Save product").primary().build())
 *     .step(detailsStep)
 *     .step(pricingStep)
 *     .status("Draft — not saved yet", StickyActionBar.Variant.WARNING)
 *     .progress("Setup", 0)
 *     .barAction(Components.button().text("Discard").build())
 *     .barAction(Components.button().text("Save product").primary()
 *         .onClick(e -> {
 *             service.save(item);
 *             form.setStatus("Saved · just now", StickyActionBar.Variant.SUCCESS);
 *             form.setProgress("Setup", 100);
 *             form.setDraftBadge(null);
 *         }).build())
 *     .build();
 * }</pre>
 *
 * @see EntityCreationForm
 */
public class EntityCreationFormBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Builder state ─────────────────────────────────────────────────────

    private final List<ListItem>    breadcrumbItems = new ArrayList<>();
    private String                  title;
    private String                  subtitle;
    private Component               subtitleComponent;
    private String                  draftBadge;
    private final List<Component>   headerActions   = new ArrayList<>();
    private final List<FormStepCard> steps          = new ArrayList<>();
    private String                  statusText;
    private StickyActionBar.Variant statusVariant   = StickyActionBar.Variant.SUCCESS;
    private String                  progressLabel;
    private int                     progressPercent = 0;
    private final List<Component>   barActions      = new ArrayList<>();
    private Consumer<EntityCreationForm> postProcessor;

    // ── Constructor ───────────────────────────────────────────────────────

    private EntityCreationFormBuilder() {}

    /**
     * Creates a new {@code EntityCreationFormBuilder}.
     *
     * @return a new builder instance
     */
    public static EntityCreationFormBuilder create() {
        return new EntityCreationFormBuilder();
    }

    // ── Breadcrumb ────────────────────────────────────────────────────────

    /**
     * Appends breadcrumb list items (typically {@link BreadcrumbItem} /
     * {@link BreadcrumbPage} instances). Separators are inserted automatically.
     *
     * @param items breadcrumb items; {@code null} entries are silently ignored
     * @return this (fluent)
     */
    public EntityCreationFormBuilder breadcrumb(ListItem... items) {
        if (items != null) {
            for (ListItem item : items) {
                if (item != null) breadcrumbItems.add(item);
            }
        }
        return this;
    }

    // ── Header text ───────────────────────────────────────────────────────

    /**
     * Sets the initial main title (e.g. {@code "New customer"}).
     * Can be changed at runtime via {@link EntityCreationForm#setTitle}.
     *
     * @param title page title; {@code null} renders no title
     * @return this (fluent)
     */
    public EntityCreationFormBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the initial subtitle / hint text (e.g. {@code "All required fields are marked with *"}).
     * Can be changed at runtime via {@link EntityCreationForm#setSubtitle}.
     *
     * @param subtitle subtitle text; {@code null} omits it
     * @return this (fluent)
     */
    public EntityCreationFormBuilder subtitle(String subtitle) {
        this.subtitle = subtitle;
        this.subtitleComponent = null;
        return this;
    }

    /**
     * Sets the initial subtitle as an arbitrary component — e.g. a composed span
     * with a red required-field asterisk (see {@code FormViewUtils.requiredFieldsHint()}).
     * Mutually exclusive with {@link #subtitle(String)}: calling either clears the other.
     *
     * @param component subtitle component; {@code null} omits the subtitle
     * @return this (fluent)
     */
    public EntityCreationFormBuilder subtitle(Component component) {
        this.subtitleComponent = component;
        this.subtitle = null;
        return this;
    }

    /**
     * Sets the initial draft-status chip badge text (e.g. {@code "Unsaved draft"}).
     * Can be shown, hidden, or relabelled at runtime via {@link EntityCreationForm#setDraftBadge}.
     *
     * @param badgeText badge label; {@code null} omits the badge initially
     * @return this (fluent)
     */
    public EntityCreationFormBuilder draftBadge(String badgeText) {
        this.draftBadge = badgeText;
        return this;
    }

    // ── Header actions ────────────────────────────────────────────────────

    /**
     * Appends components (typically buttons) to the right side of the page header.
     *
     * @param components header action components; {@code null} entries are silently ignored
     * @return this (fluent)
     */
    public EntityCreationFormBuilder headerAction(Component... components) {
        if (components != null) {
            for (Component c : components) {
                if (c != null) headerActions.add(c);
            }
        }
        return this;
    }

    // ── Steps ─────────────────────────────────────────────────────────────

    /**
     * Appends a {@link FormStepCard} to the main content column.
     * More steps can be added after build via {@link EntityCreationForm#addStep}.
     *
     * @param card the step card to add; {@code null} is silently ignored
     * @return this (fluent)
     */
    public EntityCreationFormBuilder step(FormStepCard card) {
        if (card != null) steps.add(card);
        return this;
    }

    /**
     * Appends multiple {@link FormStepCard}s to the main content column.
     *
     * @param cards step cards; {@code null} entries are silently ignored
     * @return this (fluent)
     */
    public EntityCreationFormBuilder steps(FormStepCard... cards) {
        if (cards != null) {
            for (FormStepCard card : cards) {
                if (card != null) steps.add(card);
            }
        }
        return this;
    }

    // ── Sticky bar — initial status ───────────────────────────────────────

    /**
     * Sets the initial status message and colour variant in the sticky action bar.
     * Can be updated at runtime via {@link EntityCreationForm#setStatus}.
     *
     * @param text    status text (e.g. {@code "Draft — not saved yet"})
     * @param variant colour variant for the indicator dot
     * @return this (fluent)
     */
    public EntityCreationFormBuilder status(String text, StickyActionBar.Variant variant) {
        this.statusText    = text;
        this.statusVariant = variant != null ? variant : StickyActionBar.Variant.SUCCESS;
        return this;
    }

    // ── Sticky bar — initial progress ─────────────────────────────────────

    /**
     * Sets the initial progress indicator in the sticky action bar.
     * Can be updated at runtime via {@link EntityCreationForm#setProgress}.
     *
     * @param label   progress label (e.g. {@code "Profile"}); {@code null} hides the block initially
     * @param percent initial completion percentage 0–100
     * @return this (fluent)
     */
    public EntityCreationFormBuilder progress(String label, int percent) {
        this.progressLabel   = label;
        this.progressPercent = percent;
        return this;
    }

    // ── Sticky bar — actions ──────────────────────────────────────────────

    /**
     * Appends components (typically buttons) to the sticky action bar.
     * More actions can be appended after build via {@link EntityCreationForm#addBarAction}.
     *
     * @param components bar action components; {@code null} entries are silently ignored
     * @return this (fluent)
     */
    public EntityCreationFormBuilder barAction(Component... components) {
        if (components != null) {
            for (Component c : components) {
                if (c != null) barActions.add(c);
            }
        }
        return this;
    }

    // ── Post-processor ────────────────────────────────────────────────────

    /**
     * Registers a callback that is invoked with the fully assembled
     * {@link EntityCreationForm} at the very end of {@link #build()}, before the
     * form is returned to the caller.
     *
     * <p>Use this to wire reactive listeners, field-change handlers, and any
     * other post-build logic — all in the same fluent chain, without breaking
     * it to store an intermediate reference:</p>
     *
     * <pre>{@code
     * EntityCreationForm form = Components.entityCreationForm()
     *     .title("New order")
     *     .step(step1).step(step2)
     *     .status("Draft — not saved yet", StickyActionBar.Variant.WARNING)
     *     .progress("Setup", 0)
     *     .withPostProcessor(f -> {
     *         // reactive: keep title in sync with a field
     *         clientField.addValueChangeListener(e -> f.setTitle("New order — " + e.getValue()));
     *
     *         // reactive: update progress as fields are filled
     *         Stream.of(clientField, amountField, dateField).forEach(input ->
     *             input.addValueChangeListener(e -> {
     *                 int filled = countFilledFields();
     *                 f.setProgress("Setup", filled * 100 / totalFields);
     *                 f.setStatus(filled + "/" + totalFields + " fields complete",
     *                     filled == totalFields
     *                         ? StickyActionBar.Variant.SUCCESS
     *                         : StickyActionBar.Variant.DEFAULT);
     *             })
     *         );
     *     })
     *     .build();
     * }</pre>
     *
     * <p>Multiple calls to this method compose the processors: all registered
     * callbacks are applied in registration order.</p>
     *
     * @param processor consumer called with the built form; {@code null} is silently ignored
     * @return this (fluent)
     */
    public EntityCreationFormBuilder withPostProcessor(Consumer<EntityCreationForm> processor) {
        if (processor != null) {
            this.postProcessor = this.postProcessor == null
                    ? processor
                    : this.postProcessor.andThen(processor);
        }
        return this;
    }

    // ── Build ─────────────────────────────────────────────────────────────

    /**
     * Builds and returns the configured {@link EntityCreationForm}.
     *
     * <p>The builder wires live DOM references into the component so that all
     * mutator methods ({@code setTitle}, {@code setStatus}, {@code setProgress},
     * etc.) work correctly after the form is attached to the UI.</p>
     *
     * @return the configured {@link EntityCreationForm}
     */
    public EntityCreationForm build() {

        // ── Mutable header elements ───────────────────────────────────────
        Span titleSpan = new Span(title != null ? title : "");
        Div  titleRow  = new Div(titleSpan);
        titleRow.addClassName("ecf__head-title");

        Chip badgeChip = null;
        if (draftBadge != null && !draftBadge.isBlank()) {
            badgeChip = new Chip(draftBadge);
            badgeChip.getElement().setAttribute("tabindex", "-1");
            badgeChip.getElement().getStyle().set("cursor", "default");
            // Initial state is always WARNING — the form is unsaved when first shown
            badgeChip.getElement().setAttribute("data-badge-variant", "warning");
            titleRow.add(badgeChip);
        }

        Span subtitleSpan = new Span();
        subtitleSpan.addClassName("ecf__head-sub");
        if (subtitleComponent != null) {
            subtitleSpan.add(subtitleComponent);
            subtitleSpan.setVisible(true);
        } else if (subtitle != null && !subtitle.isBlank()) {
            subtitleSpan.setText(subtitle);
            subtitleSpan.setVisible(true);
        } else {
            subtitleSpan.setVisible(false);
        }

        // ── Page header — reuse the existing Header component ────────────
        Header pageHead = new Header("");
        pageHead.setHeadingFontSize(null);   // let ecf__head-title CSS control font sizing
        pageHead.setHeading(titleRow);
        pageHead.setDetails(subtitleSpan);
        pageHead.withoutSticky();
        if (!breadcrumbItems.isEmpty()) {
            Breadcrumb breadcrumb = new Breadcrumb();
            breadcrumb.addWithSeparators(breadcrumbItems.toArray(ListItem[]::new));
            pageHead.setBreadcrumb(breadcrumb);
        }
        if (!headerActions.isEmpty()) {
            pageHead.setActions(headerActions.toArray(Component[]::new));
        }

        // ── Main column ───────────────────────────────────────────────────
        Div mainCol = new Div();
        mainCol.addClassName("ecf__main");
        steps.forEach(mainCol::add);

        Div body = new Div(mainCol);
        body.addClassName("ecf__body");

        // ── Sticky action bar ─────────────────────────────────────────────
        StickyActionBar bar = new StickyActionBar();
        if (statusText != null) {
            bar.setStatus(statusText, statusVariant);
        }
        if (progressLabel != null) {
            bar.setProgress(progressLabel, progressPercent);
        }
        if (!barActions.isEmpty()) {
            bar.addAction(barActions.toArray(Component[]::new));
        }

        // ── Assemble form (wiring live refs into the component) ───────────
        EntityCreationForm form = new EntityCreationForm(
                titleSpan, subtitleSpan, titleRow, badgeChip, mainCol, bar);
        form.add(pageHead, body, bar);

        if (postProcessor != null) {
            postProcessor.accept(form);
        }

        return form;
    }
}
