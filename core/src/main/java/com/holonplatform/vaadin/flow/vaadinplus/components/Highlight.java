package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class Highlight extends Layout {

    // Style
    private Font.Size valueFontSize;

    // Components
    private final Layout prefix;
    private final Layout column;
    private Component heading;
    private final Component value;
    private final Layout details;
    private final Layout suffix;

    // ---------- Constructors ----------

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
        // Base layout styling
        addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL
        );
        setAlignItems(AlignItems.CENTER);
        setGap(Gap.MEDIUM);
        setPosition(Position.RELATIVE);

        // Prefix
        this.prefix = new Layout();
        this.prefix.setDisplay(Display.FLEX);
        setPrefix(prefix);

        // Heading
        this.heading = new H3(heading);
        this.heading.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.FontWeight.NORMAL,
                LumoUtility.TextColor.SECONDARY
        );

        // Value
        Span valueSpan = new Span(value);
        valueSpan.addClassNames(LumoUtility.FontWeight.MEDIUM);
        this.value = valueSpan;
        setValueFontSize(Font.Size.XLARGE);

        // Details
        this.details = new Layout();
        this.details.setFlexWrap(FlexWrap.WRAP);
        this.details.setGap(Gap.SMALL);
        this.details.setDisplay(Display.FLEX);
        setDetails((Component[]) null);

        // Column (heading + value + details)
        this.column = new Layout(this.heading, this.value, this.details);
        this.column.addClassNames(LumoUtility.Padding.Vertical.XSMALL);
        this.column.setDisplay(Display.FLEX);
        this.column.setFlexDirection(FlexDirection.COLUMN);
        this.column.setFlexGrow();

        // Suffix
        this.suffix = new Layout();
        this.suffix.setDisplay(Display.FLEX);
        setSuffix(suffix);

        // Compose the final layout
        add(this.prefix, this.column, this.suffix);
    }

    // ---------- Public API ----------

    /**
     * Sets the prefix.
     */
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

    /**
     * Sets the heading text.
     */
    public void setHeading(String heading) {
        this.heading.getElement().setText(heading);
    }

    /**
     * Changes the heading level (e.g. H1–H6) using HeadingLevel.
     */
    public void setHeadingLevel(HeadingLevel level) {
        String currentText = this.heading.getElement().getText();
        Component newHeading = level.getComponent(currentText);

        if (this.heading != null) {
            replace(this.heading, newHeading);
        }

        this.heading = newHeading;
        this.heading.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY
        );
    }

    /**
     * Sets the value text.
     */
    public void setValue(String value) {
        this.value.getElement().setText(value);
    }

    /**
     * Sets the value's font size.
     */
    public void setValueFontSize(Font.Size fontSize) {
        if (this.valueFontSize != null) {
            this.value.removeClassName(this.valueFontSize.getClassName());
        }
        this.value.addClassName(fontSize.getClassName());
        this.valueFontSize = fontSize;
    }

    /**
     * Sets the details components.
     */
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

    /**
     * Sets the suffix components.
     */
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

}