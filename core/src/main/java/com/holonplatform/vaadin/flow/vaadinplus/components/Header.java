package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Getter;

import java.util.Optional;

public class Header extends Layout implements HasTheme {

    // Layout structure
    private final Layout row;
    private final Layout prefix;
    private final Layout column;
    private final Breadcrumb breadcrumb;
    private final Layout details;
    private final Layout actions;

    // Optional tabs row
    private Tabs tabs;

    // Heading component
    private Component heading;

    // Style state for heading
    private Color.Text headingTextColor;
    private Font.Size headingFontSize;
    private Font.Weight headingFontWeight;
    private Font.LineHeight headingLineHeight;

    @Getter
    private Component[] prefixComponents;

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public Header(String title) {
        this(title, HeadingLevel.H2);
    }

    public Header(LabelBuilder<?> labelBuilder) {
        this("Not set", HeadingLevel.H1);
        setHeading(labelBuilder);
    }

    public Header(String title, HeadingLevel level) {

        addClassName("iyen-header");
        // Create all "slots" but don't attach them yet
        this.prefix = new Layout();
        this.prefix.setDisplay(Display.FLEX);
        this.breadcrumb = new Breadcrumb();
        this.breadcrumb.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        this.details = new Layout();
        this.details.setDisplay(Display.FLEX);
        this.details.addClassNames(LumoUtility.Margin.Top.XSMALL);
        this.details.setFlexWrap(FlexWrap.WRAP);
        this.details.setColumnGap(Gap.MEDIUM);

        this.actions = new Layout();
        this.actions.setDisplay(Display.FLEX);
        this.actions.setGap(Gap.SMALL);

        // Heading
        this.heading = level.getComponent(title);
        setHeadingFontSize(Font.Size.XLARGE);

        // Column starts only with mandatory heading
        this.column = new Layout(this.heading);
        this.column.addClassName("heading-column");

        // Row starts with column only
        this.row = new Layout(this.column);
        this.row.setDisplay(Display.FLEX);
        this.row.addClassNames(LumoUtility.Padding.MEDIUM);
        this.row.setFlexWrap(FlexWrap.WRAP);
        this.row.setGap(Gap.MEDIUM);

        add(this.row);
    }

    // ---------------------------------------------------------------------
    // Public API – Layout slots
    // ---------------------------------------------------------------------

    /**
     * Sets the prefix components.
     */
    public void setPrefix(Component... components) {
        this.prefixComponents = components;
        updateSlotInRow(prefix, components);
    }

    /**
     * Sets the content of the breadcrumb.
     */
    public void setBreadcrumb(BreadcrumbItem... items) {
        breadcrumb.removeAll();

        int count = 0;
        if (items != null) {
            for (BreadcrumbItem item : items) {
                if (item != null) {
                    breadcrumb.add(item);
                    count++;
                }
            }
        }

        boolean hasItems = count > 0;

        if (hasItems) {
            // ensure it's in the column at index 0 (above heading)
            if (breadcrumb.getParent().isEmpty()) {
                column.getElement().insertChild(0, breadcrumb.getElement());
            }
            breadcrumb.setVisible(true);
        } else {
            if (breadcrumb.getParent().isPresent()) {
                column.remove(breadcrumb);
            }
            breadcrumb.setVisible(false);
        }
    }

    /**
     * Sets the details components.
     */
    public void setDetails(Component... components) {
        updateSlotInColumn(details, components);

        if (details.getComponentCount() > 0) {
            this.row.setAlignItems(AlignItems.CENTER);
        }
    }

    /**
     * Adds the specified action components.
     */
    public void addActions(Component... components) {
        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    this.actions.add(component);
                }
            }
        }
        updateActionsVisibilityAndAttachment();
    }

    /**
     * Sets the action components.
     */
    public void setActions(Component... components) {
        this.actions.removeAll();

        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    this.actions.add(component);
                }
            }
        }

        updateActionsVisibilityAndAttachment();
    }

    /**
     * Returns the row layout (prefix, column, actions).
     */
    public Layout getRowLayout() {
        return this.row;
    }

    /**
     * Returns the column layout (breadcrumb, heading, details).
     */
    public Layout getColumnLayout() {
        return this.column;
    }

    // ---------------------------------------------------------------------
    // Public API – Heading
    // ---------------------------------------------------------------------

    /**
     * Sets the heading text & level.
     */
    public void setHeading(String title, HeadingLevel level) {
        Component newHeading = level.getComponent(title);
        applyHeadingStyles(newHeading);
        setHeading(newHeading);
    }

    private void setHeading(Component newHeading) {
        if (this.heading != null && this.heading.getParent().isPresent()) {
            this.column.replace(this.heading, newHeading);
        }
        this.heading = newHeading;
    }

    /**
     * Updates only the heading text.
     */
    public void setHeading(String title) {
        if (this.heading != null) {
            this.heading.getElement().setText(title);
        }
    }

    /**
     * Sets the heading using a {@link LabelBuilder}.
     */
    public void setHeading(LabelBuilder<?> labelBuilder) {
        Component newHeading = labelBuilder.build();
        applyHeadingStyles(newHeading);
        setHeading(newHeading);
    }

    /**
     * Sets the heading's font size.
     */
    public void setHeadingFontSize(Font.Size fontSize) {
        if (fontSize == headingFontSize) {
            return; // no-op
        }

        updateHeadingStyleClass(
                headingFontSize != null ? headingFontSize.getClassName() : null,
                fontSize != null ? fontSize.getClassName() : null
        );
        this.headingFontSize = fontSize;
    }

    /**
     * Sets the heading's font weight.
     */
    public void setHeadingFontWeight(Font.Weight fontWeight) {
        if (fontWeight == headingFontWeight) {
            return; // no-op
        }

        updateHeadingStyleClass(
                headingFontWeight != null ? headingFontWeight.getClassName() : null,
                fontWeight != null ? fontWeight.getClassName() : null
        );
        this.headingFontWeight = fontWeight;
    }

    /**
     * Sets the heading's line height.
     */
    public void setHeadingLineHeight(Font.LineHeight lineHeight) {
        if (lineHeight == headingLineHeight) {
            return; // no-op
        }

        updateHeadingStyleClass(
                headingLineHeight != null ? headingLineHeight.getClassName() : null,
                lineHeight != null ? lineHeight.getClassName() : null
        );
        this.headingLineHeight = lineHeight;
    }

    /**
     * Sets the heading's text color.
     */
    public void setHeadingTextColor(Color.Text textColor) {
        if (textColor == headingTextColor) {
            return; // no-op
        }

        updateHeadingStyleClass(
                headingTextColor != null ? headingTextColor.getClassName() : null,
                textColor != null ? textColor.getClassName() : null
        );
        this.headingTextColor = textColor;
    }

    /**
     * Sets the heading id.
     */
    public void setHeadingId(String id) {
        if (this.heading != null) {
            this.heading.setId(id);
        }
    }

    // ---------------------------------------------------------------------
    // Public API – Tabs
    // ---------------------------------------------------------------------

    /**
     * Returns the tabs, if present.
     */
    public Optional<Tabs> getTabs() {
        return Optional.ofNullable(tabs);
    }

    /**
     * Sets the tabs using a list of {@link Tab} components.
     */
    public void setTabs(Tab... tabs) {
        Tabs newTabs = new Tabs();
        if (tabs != null) {
            for (Tab tab : tabs) {
                if (tab != null) {
                    newTabs.add(tab);
                }
            }
        }
        setTabs(newTabs);
    }

    /**
     * Sets the tabs component.
     */
    public void setTabs(Tabs tabs) {
        // Remove previous tabs, if they were added
        if (this.tabs != null && this.tabs.getParent().isPresent()) {
            remove(this.tabs);
        }

        this.tabs = tabs;
        configTabs();
    }

    // ---------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------

    /**
     * Shared slot-update method for layouts inside the column (details).
     * Adds/removes the slot container from the column depending on content.
     */
    private void updateSlotInColumn(Layout slot, Component[] components) {
        slot.removeAll();

        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    slot.add(component);
                }
            }
        }

        boolean hasContent = slot.getComponentCount() > 0;

        if (hasContent) {
            if (slot.getParent().isEmpty()) {
                // Append at the end for DETAILS. Could be extended if needed.
                column.add(slot);
            }
            slot.setVisible(true);
        } else {
            if (slot.getParent().isPresent()) {
                column.remove(slot);
            }
            slot.setVisible(false);
        }
    }

    /**
     * Shared slot-update method for layouts inside the row (prefix).
     */
    private void updateSlotInRow(Layout slot, Component[] components) {
        slot.removeAll();

        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    slot.add(component);
                }
            }
        }

        boolean hasContent = slot.getComponentCount() > 0;

        if (hasContent) {
            if (slot.getParent().isEmpty()) {
                // prefix | column | actions
                row.getElement().insertChild(0, slot.getElement());
            }
            slot.setVisible(true);
        } else {
            if (slot.getParent().isPresent()) {
                row.remove(slot);
            }
            slot.setVisible(false);
        }
    }

    private void updateActionsVisibilityAndAttachment() {
        boolean hasActions = this.actions.getComponentCount() > 0;
        if (hasActions) {
            if (this.actions.getParent().isEmpty()) {
                // actions are on the right side
                row.add(this.actions);
            }
            this.actions.setVisible(true);
        } else {
            if (this.actions.getParent().isPresent()) {
                row.remove(this.actions);
            }
            this.actions.setVisible(false);
        }
    }

    private void configTabs() {
        if (this.tabs == null) {
            return;
        }

        boolean hasTabs = this.tabs.getTabCount() > 0;

        if (hasTabs) {
            removeClassNames(LumoUtility.Border.BOTTOM);
            this.tabs.setVisible(true);

            if (this.tabs.getParent().isEmpty()) {
                add(this.tabs);
            }
        } else {
            // If no tabs, don't show them and restore border
            this.tabs.setVisible(false);
            if (this.tabs.getParent().isPresent()) {
                remove(this.tabs);
            }
            addClassNames(LumoUtility.Border.BOTTOM);
        }
    }

    /**
     * Applies the current heading style state to a new heading component.
     */
    private void applyHeadingStyles(Component target) {
        if (target == null) {
            return;
        }
        if (headingFontSize != null) {
            target.addClassNames(headingFontSize.getClassName());
        }
        if (headingFontWeight != null) {
            target.addClassNames(headingFontWeight.getClassName());
        }
        if (headingLineHeight != null) {
            target.addClassNames(headingLineHeight.getClassName());
        }
        if (headingTextColor != null) {
            target.addClassNames(headingTextColor.getClassName());
        }
    }

    /**
     * Replaces an old heading style class with a new one.
     */
    private void updateHeadingStyleClass(String oldClassName, String newClassName) {
        if (this.heading == null) {
            return;
        }
        if (oldClassName != null) {
            this.heading.removeClassName(oldClassName);
        }
        if (newClassName != null) {
            this.heading.addClassNames(newClassName);
        }
    }


}