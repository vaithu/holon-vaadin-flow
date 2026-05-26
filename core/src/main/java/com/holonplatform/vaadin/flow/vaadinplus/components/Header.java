package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Optional;

@StyleSheet("context://header.css")
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

    private Component[] prefixComponents;

    public Component[] getPrefixComponents() {
        return prefixComponents;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Header(String title) {
        this(title, HeadingLevel.H2);
    }

    public Header(LabelBuilder<?> labelBuilder) {
        this("Not set", HeadingLevel.H1);
        setHeading(labelBuilder);
    }

    public Header(String title, HeadingLevel level) {
        addClassName("iyen-header");
        getElement().setAttribute("role", "region");

        this.prefix = new Layout();

        this.breadcrumb = new Breadcrumb();
        this.breadcrumb.addClassName("iyen-header__breadcrumb");

        this.details = new Layout();
        this.details.addClassName("iyen-header__details");

        this.actions = new Layout();
        this.actions.addClassName("iyen-header__actions");

        this.heading = level.getComponent(title);
        setHeadingFontSize(Font.Size.XLARGE);

        this.column = new Layout(this.heading);

        this.row = new Layout(this.column);
        this.row.addClassName("iyen-header__row");

        add(this.row);
    }

    // -------------------------------------------------------------------------
    // Public API – Layout slots
    // -------------------------------------------------------------------------

    public void setPrefix(Component... components) {
        this.prefixComponents = components;
        updateSlotInRow(prefix, components);
    }

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

    public void setDetails(Component... components) {
        updateSlotInColumn(details, components);

        if (details.getComponentCount() > 0) {
            this.row.addClassName("iyen-header__row--with-details");
        } else {
            this.row.removeClassName("iyen-header__row--with-details");
        }
    }

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

    public Layout getRowLayout() {
        return this.row;
    }

    public Layout getColumnLayout() {
        return this.column;
    }

    // -------------------------------------------------------------------------
    // Public API – Heading
    // -------------------------------------------------------------------------

    public void setHeading(String title, HeadingLevel level) {
        Component newHeading = level.getComponent(title);
        applyHeadingStyles(newHeading);
        setHeading(newHeading);
    }

    public void setHeading(Component newHeading) {
        if (this.heading != null && this.heading.getParent().isPresent()) {
            this.column.replace(this.heading, newHeading);
        }
        this.heading = newHeading;
    }

    public void setHeading(String title) {
        if (this.heading != null) {
            this.heading.getElement().setText(title);
        }
    }

    /**
     * Sets the heading text from a {@link Localizable} descriptor,
     * resolved using the current {@link LocalizationProvider}.
     *
     * @param title localizable heading text (not null)
     */
    public void setHeading(Localizable title) {
        String resolved = LocalizationProvider.localize(title)
                .orElseGet(() -> title.getMessage() != null ? title.getMessage() : "");
        setHeading(resolved);
    }

    /**
     * Replaces the heading element with a new one at the given level,
     * using a {@link Localizable} for the title text.
     *
     * @param title localizable heading text (not null)
     * @param level heading level (not null)
     */
    public void setHeading(Localizable title, HeadingLevel level) {
        String resolved = LocalizationProvider.localize(title)
                .orElseGet(() -> title.getMessage() != null ? title.getMessage() : "");
        setHeading(resolved, level);
    }

    public void setHeading(LabelBuilder<?> labelBuilder) {
        Component newHeading = labelBuilder.build();
        applyHeadingStyles(newHeading);
        setHeading(newHeading);
    }

    public void setHeadingFontSize(Font.Size fontSize) {
        if (fontSize == headingFontSize) {
            return;
        }
        updateHeadingStyleClass(
                headingFontSize != null ? headingFontSize.getClassName() : null,
                fontSize != null ? fontSize.getClassName() : null
        );
        this.headingFontSize = fontSize;
    }

    public void setHeadingFontWeight(Font.Weight fontWeight) {
        if (fontWeight == headingFontWeight) {
            return;
        }
        updateHeadingStyleClass(
                headingFontWeight != null ? headingFontWeight.getClassName() : null,
                fontWeight != null ? fontWeight.getClassName() : null
        );
        this.headingFontWeight = fontWeight;
    }

    public void setHeadingLineHeight(Font.LineHeight lineHeight) {
        if (lineHeight == headingLineHeight) {
            return;
        }
        updateHeadingStyleClass(
                headingLineHeight != null ? headingLineHeight.getClassName() : null,
                lineHeight != null ? lineHeight.getClassName() : null
        );
        this.headingLineHeight = lineHeight;
    }

    public void setHeadingTextColor(Color.Text textColor) {
        if (textColor == headingTextColor) {
            return;
        }
        updateHeadingStyleClass(
                headingTextColor != null ? headingTextColor.getClassName() : null,
                textColor != null ? textColor.getClassName() : null
        );
        this.headingTextColor = textColor;
    }

    public void setHeadingId(String id) {
        if (this.heading != null) {
            this.heading.setId(id);
        }
    }

    // -------------------------------------------------------------------------
    // Public API – Tabs
    // -------------------------------------------------------------------------

    public Optional<Tabs> getTabs() {
        return Optional.ofNullable(tabs);
    }

    public void setTabs(Tab... tabs) {
        Tabs newTabs = Components.tabs().build();
        if (tabs != null) {
            for (Tab tab : tabs) {
                if (tab != null) {
                    newTabs.add(tab);
                }
            }
        }
        setTabs(newTabs);
    }

    public void setTabs(Tabs tabs) {
        if (this.tabs != null && this.tabs.getParent().isPresent()) {
            remove(this.tabs);
        }
        this.tabs = tabs;
        configTabs();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

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
            removeClassName("iyen-header--bordered");
            this.tabs.setVisible(true);

            if (this.tabs.getParent().isEmpty()) {
                add(this.tabs);
            }
            addClassName("iyen-header--tabbed");
        } else {
            this.tabs.setVisible(false);
            if (this.tabs.getParent().isPresent()) {
                remove(this.tabs);
            }
            addClassName("iyen-header--bordered");
        }
    }

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