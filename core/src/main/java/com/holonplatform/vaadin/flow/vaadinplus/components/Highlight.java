package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.HighlightBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;


/**
 * KPI / metric card with five composable slots:
 * <ul>
 *   <li><b>cardHeader</b>  – optional top row: left icon + right action (Image-2 project cards)</li>
 *   <li><b>prefix</b>      – left icon / avatar</li>
 *   <li><b>heading</b>     – small label (H1–H6 or Span)</li>
 *   <li><b>value</b>       – prominent number / text, with optional inline metric beside it</li>
 *   <li><b>details</b>     – flex row below the value (trend chips, sub-labels)</li>
 *   <li><b>suffix</b>      – right icon / badge</li>
 *   <li><b>footer</b>      – optional progress label + progress bar (Image-2 project cards)</li>
 * </ul>
 *
 * <p>Layout variants:
 * <ul>
 *   <li>{@link #setValueFirst(boolean) setValueFirst(true)} – value above heading (KPI numbers)</li>
 *   <li>{@link #setAccentColor(AccentColor)} – coloured left-border stripe</li>
 * </ul>
 */
@StyleSheet("context://highlight.css")
public class Highlight extends Layout implements HasSize, HasStyle {

    /**
     * Ready-made Chart.js {@code options} JSON that strips all chrome from a line chart,
     * leaving only the bare area/line — perfect for the {@link #setSparkline} slot.
     *
     * <p>Pass directly to {@code ChartJsBuilder.options(Highlight.SPARKLINE_OPTIONS)}.
     */
    public static final String SPARKLINE_OPTIONS =
            "{\"responsive\":true,\"maintainAspectRatio\":false," +
            "\"animation\":false," +
            "\"plugins\":{\"legend\":{\"display\":false},\"tooltip\":{\"enabled\":false}}," +
            "\"scales\":{\"x\":{\"display\":false},\"y\":{\"display\":false}}," +
            "\"elements\":{\"point\":{\"radius\":0,\"hoverRadius\":0}}," +
            "\"layout\":{\"padding\":0}}";

    // ── AccentColor ───────────────────────────────────────────────────────────

    /**
     * Coloured left-border accent for the card.
     * Each value maps to a predefined BEM modifier class in {@code highlight.css}.
     */
    public enum AccentColor {
        PURPLE("highlight--accent-purple"),
        ORANGE("highlight--accent-orange"),
        TEAL("highlight--accent-teal"),
        GREEN("highlight--accent-green"),
        RED("highlight--accent-red"),
        BLUE("highlight--accent-blue");

        private final String className;

        AccentColor(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    // ── State ─────────────────────────────────────────────────────────────────

    private Font.Size valueFontSize;
    private AccentColor accentColor;

    // I18N localizables — re-resolved on each attach
    private Localizable headingLocalizable;
    private Localizable subheadingLocalizable;
    private Localizable valueLocalizable;
    private Localizable progressLabelLocalizable;
    private Localizable ariaLabelLocalizable;

    // ── Components ────────────────────────────────────────────────────────────

    private final Layout cardHeader;
    private final Layout cardHeaderIcon;
    private final Layout cardHeaderAction;
    private final Layout prefix;
    private final Layout column;
    private Component heading;
    private final Span subheading;
    private final Layout valueRow;
    private final Component value;
    private final Layout details;
    private final Layout suffix;
    private final Layout footer;
    private final Span progressLabel;
    private final ProgressBar progressBar;
    private final Layout sparkline;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Highlight(String heading, String value) {
        this(null, heading, value, null);
    }

    public Highlight(Component prefix, String heading, String value) {
        this(prefix, heading, value, null);
    }

    public Highlight(String heading, String value, Component suffix) {
        this(null, heading, value, suffix);
    }

    public Highlight(Component prefix, String heading, String value, Component suffix) {
        addClassName("highlight");
        // Accessibility: region landmark + labelled-by the heading
        getElement().setAttribute("role", "region");

        // ── Card header (initially hidden) ────────────────────────────────────
        this.cardHeaderIcon = new Layout();
        this.cardHeaderIcon.addClassName("highlight__card-icon");
        this.cardHeaderIcon.setDisplay(Display.FLEX);

        this.cardHeaderAction = new Layout();
        this.cardHeaderAction.addClassName("highlight__card-action");
        this.cardHeaderAction.setDisplay(Display.FLEX);

        this.cardHeader = new Layout(this.cardHeaderIcon, this.cardHeaderAction);
        this.cardHeader.addClassName("highlight__card-header");
        this.cardHeader.setDisplay(Display.FLEX);
        this.cardHeader.setAlignItems(AlignItems.CENTER);
        this.cardHeader.setJustifyContent(JustifyContent.BETWEEN);
        this.cardHeader.setVisible(false);

        // ── Prefix ────────────────────────────────────────────────────────────
        this.prefix = new Layout();
        this.prefix.setDisplay(Display.FLEX);
        this.prefix.addClassName("highlight__prefix");

        // ── Heading ───────────────────────────────────────────────────────────
        this.heading = Components.h3().text(heading).styleName("highlight__heading").build();

        // ── Sub-heading (optional muted secondary line, initially hidden) ─────
        this.subheading = Components.span().styleName("highlight__subheading").visible(false).build();

        // ── Value + inline metric row ─────────────────────────────────────────
        Span valueSpan = Components.span().text(value).styleName("highlight__value").build();
        this.value = valueSpan;

        this.valueRow = new Layout(this.value);
        this.valueRow.addClassName("highlight__value-row");
        this.valueRow.setDisplay(Display.FLEX);
        this.valueRow.setAlignItems(AlignItems.BASELINE);

        // ── Details ───────────────────────────────────────────────────────────
        this.details = new Layout();
        this.details.setFlexWrap(FlexWrap.WRAP);
        this.details.setGap(Gap.SMALL);
        this.details.setDisplay(Display.FLEX);
        this.details.addClassName("highlight__details");
        setDetails((Component[]) null);

        // ── Column: heading → subheading → value-row → details ───────────────
        this.column = new Layout(this.heading, this.subheading, this.valueRow, this.details);
        this.column.addClassName("highlight__column");
        this.column.setDisplay(Display.FLEX);
        this.column.setFlexDirection(FlexDirection.COLUMN);
        this.column.setFlexGrow();

        // ── Suffix ────────────────────────────────────────────────────────────
        this.suffix = new Layout();
        this.suffix.setDisplay(Display.FLEX);
        this.suffix.addClassName("highlight__suffix");

        // ── Footer: progress label + bar (initially hidden) ───────────────────
        this.progressLabel = Components.span().styleName("highlight__progress-label").visible(false).build();

        this.progressBar = new ProgressBar();
        this.progressBar.addClassName("highlight__progress");
        this.progressBar.setVisible(false);

        this.footer = new Layout(this.progressLabel, this.progressBar);
        this.footer.addClassName("highlight__footer");
        this.footer.setDisplay(Display.FLEX);
        this.footer.setFlexDirection(FlexDirection.COLUMN);
        this.footer.setVisible(false);

        // ── Body: prefix + column + suffix (the original flex row) ────────────
        Layout body = new Layout(this.prefix, this.column, this.suffix);
        body.addClassName("highlight__body");
        body.setDisplay(Display.FLEX);
        body.setAlignItems(AlignItems.CENTER);

        // ── Sparkline: full-bleed chart at card bottom (initially hidden) ────────
        this.sparkline = new Layout();
        this.sparkline.addClassName("highlight__chart");
        this.sparkline.setVisible(false);

        setPrefix(prefix);
        setSuffix(suffix);

        add(this.cardHeader, body, this.footer, this.sparkline);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Sets a coloured left-border accent stripe on the card.
     * Pass {@code null} to remove the current accent.
     */
    public void setAccentColor(AccentColor color) {
        if (this.accentColor != null) {
            removeClassName(this.accentColor.getClassName());
        }
        if (color != null) {
            addClassName(color.getClassName());
        }
        this.accentColor = color;
    }

    /**
     * When {@code true}, the value is rendered visually <em>above</em> the heading.
     * Useful for large KPI numbers where the number is the focal point.
     */
    public void setValueFirst(boolean valueFirst) {
        if (valueFirst) {
            addClassName("highlight--value-first");
        } else {
            removeClassName("highlight--value-first");
        }
    }

    /**
     * Adds components displayed inline to the right of the value (e.g. a trend badge,
     * delta percentage). Each component receives the {@code highlight__value-metric}
     * class for consistent sizing. Pass no arguments or {@code null} to clear.
     */
    public void setInlineMetric(Component... components) {
        this.valueRow.removeAll();
        this.valueRow.add(this.value);
        if (components != null) {
            for (Component c : components) {
                if (c != null) {
                    c.addClassName("highlight__value-metric");
                    this.valueRow.add(c);
                }
            }
        }
    }

    /**
     * Populates the card-header row (top area, above the body).
     * {@code icon} goes to the left slot, {@code action} to the right slot.
     * The header is hidden when both are {@code null}.
     */
    public void setCardHeader(Component icon, Component action) {
        this.cardHeaderIcon.removeAll();
        if (icon != null) {
            this.cardHeaderIcon.add(icon);
        }
        this.cardHeaderAction.removeAll();
        if (action != null) {
            this.cardHeaderAction.add(action);
        }
        this.cardHeader.setVisible(icon != null || action != null);
    }

    /**
     * Sets the progress bar value in the range {@code [0.0, 1.0]}.
     * Shows the footer automatically.
     */
    public void setProgress(double percent) {
        this.progressBar.setValue(Math.clamp(percent, 0.0, 1.0));
        this.progressBar.setVisible(true);
        this.footer.setVisible(true);
    }

    /**
     * Switches the progress bar to indeterminate (animated) mode.
     * Shows the footer automatically.
     */
    public void setProgressIndeterminate(boolean indeterminate) {
        this.progressBar.setIndeterminate(indeterminate);
        this.progressBar.setVisible(true);
        this.footer.setVisible(true);
    }

    /**
     * Sets the text label shown above the progress bar (e.g. "40% complete").
     * Shows the footer automatically when a non-blank label is provided.
     */
    public void setProgressLabel(String label) {
        this.progressLabel.setText(label != null ? label : "");
        boolean visible = label != null && !label.isBlank();
        this.progressLabel.setVisible(visible);
        if (visible) {
            this.footer.setVisible(true);
        }
    }

    /**
     * Places a component (typically a sparkline {@code ChartJsComponent}) in the
     * full-bleed chart slot at the very bottom of the card.
     *
     * <p>The slot uses negative margins equal to the card's padding so the chart
     * visually bleeds to the left, right, and bottom edges — identical to the
     * dashboard KPI card pattern (see {@code highlight.css .highlight__chart}).
     *
     * <p>Build a sparkline with the {@code holon-vaadin-flow-chartjs} module:
     * <pre>{@code
     * var sparkline = ChartJs.builder()
     *     .type(ChartType.LINE)
     *     .categories("1","2","3","4","5","6","7","8","9","10","11","12")
     *     .series(ChartJsDataset.builder()
     *         .data(42, 47, 45, 52, 56, 51, 58, 62, 60, 67, 71, 74)
     *         .borderColor("rgb(249,115,22)")
     *         .backgroundColor("rgba(249,115,22,0.1)")
     *         .borderWidth(2)
     *         .tension(0.4)
     *         .fill(true)
     *         .property("pointRadius", 0)
     *         .build())
     *     .options(Highlight.SPARKLINE_OPTIONS)
     *     .height("70px").width("100%")
     *     .build();
     * highlight.setSparkline(sparkline);
     * }</pre>
     *
     * Pass {@code null} to clear and hide the slot.
     */
    public void setSparkline(Component chart) {
        this.sparkline.removeAll();
        if (chart != null) {
            this.sparkline.add(chart);
        }
        this.sparkline.setVisible(chart != null);
    }

    // ── Existing API (unchanged contracts) ───────────────────────────────────

    /** Sets the prefix slot (left side of the body row). */
    public void setPrefix(Component... components) {
        this.prefix.removeAll();
        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    this.prefix.add(component);
                }
            }
        }
        this.prefix.setVisible(this.prefix.getComponentCount() > 0);
    }

    /** Sets the heading text. */
    public void setHeading(String heading) {
        this.heading.getElement().setText(heading);
    }

    /**
     * Sets a muted secondary line rendered immediately below the heading
     * (e.g. a ticker symbol, subtitle, or company category).
     * Pass {@code null} or blank to hide.
     */
    public void setSubheading(String text) {
        this.subheading.setText(text != null ? text : "");
        this.subheading.setVisible(text != null && !text.isBlank());
    }

    /** Swaps the heading element to the given semantic level. */
    public void setHeadingLevel(HeadingLevel level) {
        String currentText = this.heading.getElement().getText();
        Component newHeading = level.getComponent(currentText);
        if (this.heading != null) {
            this.column.replace(this.heading, newHeading);
        }
        this.heading = newHeading;
        this.heading.addClassName("highlight__heading");
    }

    /** Sets the value text. */
    public void setValue(String value) {
        this.value.getElement().setText(value);
    }

    /** Applies a Font.Size class to the value span. */
    public void setValueFontSize(Font.Size fontSize) {
        if (this.valueFontSize != null) {
            this.value.removeClassName(this.valueFontSize.getClassName());
        }
        this.value.addClassName(fontSize.getClassName());
        this.valueFontSize = fontSize;
    }

    /** Sets the details row components (shown below the value). */
    public void setDetails(Component... components) {
        this.details.removeAll();
        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    this.details.add(component);
                }
            }
        }
        this.details.setVisible(this.details.getComponentCount() > 0);
    }

    /** Sets the suffix slot (right side of the body row). */
    public void setSuffix(Component... components) {
        this.suffix.removeAll();
        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    this.suffix.add(component);
                }
            }
        }
        this.suffix.setVisible(this.suffix.getComponentCount() > 0);
    }

    // ── I18N overloads ────────────────────────────────────────────────────────

    /** Sets the heading from a Holon {@link Localizable}. Re-resolved on each attach. */
    public void setHeading(Localizable localizable) {
        this.headingLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::setHeading);
    }

    /** Sets the subheading from a Holon {@link Localizable}. Re-resolved on each attach. */
    public void setSubheading(Localizable localizable) {
        this.subheadingLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::setSubheading);
    }

    /** Sets the value from a Holon {@link Localizable}. Re-resolved on each attach. */
    public void setValue(Localizable localizable) {
        this.valueLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::setValue);
    }

    /** Sets the progress label from a Holon {@link Localizable}. Re-resolved on each attach. */
    public void setProgressLabel(Localizable localizable) {
        this.progressLabelLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::setProgressLabel);
    }

    // ── Accessibility ─────────────────────────────────────────────────────────

    /**
     * Sets the {@code aria-label} attribute on the card root element.
     *
     * <p>Use when the visual heading alone is insufficient for screen-reader context
     * (e.g. multiple cards with similar headings on one page). Passing {@code null}
     * or blank clears the attribute.
     *
     * @param label the accessible label text
     */
    public void setAriaLabel(String label) {
        if (label != null && !label.isBlank()) {
            getElement().setAttribute("aria-label", label);
        } else {
            getElement().removeAttribute("aria-label");
        }
    }

    /**
     * Sets the {@code aria-label} from a Holon {@link Localizable}. Re-resolved on each attach.
     *
     * @param localizable the localizable label (not null)
     */
    public void setAriaLabel(Localizable localizable) {
        this.ariaLabelLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(label -> setAriaLabel(label));
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (this.headingLocalizable != null) {
            LocalizationProvider.localize(this.headingLocalizable).ifPresent(this::setHeading);
        }
        if (this.subheadingLocalizable != null) {
            LocalizationProvider.localize(this.subheadingLocalizable).ifPresent(this::setSubheading);
        }
        if (this.valueLocalizable != null) {
            LocalizationProvider.localize(this.valueLocalizable).ifPresent(this::setValue);
        }
        if (this.progressLabelLocalizable != null) {
            LocalizationProvider.localize(this.progressLabelLocalizable).ifPresent(this::setProgressLabel);
        }
        if (this.ariaLabelLocalizable != null) {
            LocalizationProvider.localize(this.ariaLabelLocalizable).ifPresent(label -> setAriaLabel(label));
        }
    }

    // ── Static builder factories ──────────────────────────────────────────────

    /**
     * Returns a fluent {@link HighlightBuilder} seeded with the given heading and value.
     *
     * <pre>{@code
     * Highlight card = Highlight.builder("Total Revenue", "$128,430")
     *     .valueFirst()
     *     .accentColor(AccentColor.PURPLE)
     *     .ariaLabel("Total Revenue KPI")
     *     .build();
     * }</pre>
     *
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     * @return a new {@link HighlightBuilder}
     */
    public static HighlightBuilder builder(String heading, String value) {
        return HighlightBuilder.create(heading, value);
    }

    /**
     * Returns a fluent {@link HighlightBuilder} seeded with a prefix + heading + value.
     *
     * @param prefix  the prefix component (left slot)
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     * @return a new {@link HighlightBuilder}
     */
    public static HighlightBuilder builder(Component prefix, String heading, String value) {
        return HighlightBuilder.create(prefix, heading, value);
    }
}









