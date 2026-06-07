package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.lumo.Gap;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Optional;

@StyleSheet("context://header.css")
public class Header extends Layout implements HasTheme {

    private static final String CLASS_HEADER = "iyen-header";
    private static final String CLASS_HEADER_BORDERED = "iyen-header--bordered";
    private static final String CLASS_HEADER_NO_BORDER = "header--no-border";
    private static final String CLASS_ROW = "iyen-header__row";
    private static final String CLASS_TOP_ROW = "iyen-header__top-row";
    private static final String CLASS_TOP_ROW_AVATAR = "iyen-header__top-row--avatar";
    private static final String CLASS_COLUMN = "iyen-header__column";
    private static final String CLASS_COLUMN_LINE = "iyen-header__column-line";
    private static final String CLASS_PREFIX = "iyen-header__prefix";
    private static final String CLASS_PREFIX_AVATAR = "iyen-header__prefix--avatar";
    private static final String CLASS_BREADCRUMB = "iyen-header__breadcrumb";
    private static final String CLASS_DETAILS = "iyen-header__details";
    private static final String CLASS_ACTIONS = "iyen-header__actions";
    private static final String CLASS_ACTIONS_COMPACT = "iyen-header__actions--compact";
    private static final String CLASS_TABS = "iyen-header__tabs";

    private final Layout row = new Layout();
    private final Layout topRow = new Layout();
    private final Layout column = new Layout();
    private final Layout columnLine = new Layout();
    private Layout prefix;
    private Breadcrumb breadcrumb;
    private Layout details;
    private Layout actions;
    private Tabs tabs;
    private Component heading;
    private Avatar avatar;
    private Color.Text headingTextColor;
    private Font.Size headingFontSize;
    private Font.Weight headingFontWeight;
    private Font.LineHeight headingLineHeight;
    private Component[] prefixComponents = new Component[0];

    public Header(String title) {
        this(title, HeadingLevel.H2);
    }

    public Header(LabelBuilder<?> labelBuilder) {
        this("Not set", HeadingLevel.H1);
        if (labelBuilder != null) {
            setHeading(labelBuilder.build());
        }
    }

    public Header(String title, HeadingLevel level) {
        addClassName(CLASS_HEADER);
        addClassName(CLASS_HEADER_BORDERED);
        getElement().setAttribute("role", "region");

        getRowLayout().addClassName(CLASS_ROW);
        getTopRowLayout().addClassName(CLASS_TOP_ROW);
        getColumnLayout().addClassName(CLASS_COLUMN);
        columnLine.addClassName(CLASS_COLUMN_LINE);
        getColumnLayout().add(columnLine);
        getTopRowLayout().add(getColumnLayout());
        getRowLayout().add(getTopRowLayout());
        add(getRowLayout());

        setHeading(title, level);
        setHeadingFontSize(Font.Size.XLARGE);
    }

    public void setPrefix(Component... components) {
        this.prefixComponents = components != null ? components.clone() : new Component[0];
        if (isEmpty(components)) {
            detachFromTopRow(prefix);
            prefix = null;
        } else {
            prefix = ensureSlot(prefix, CLASS_PREFIX);
            updatePrefixVariant(components);
            replaceContent(prefix, components);
        }
        refreshRow();
    }

    public Component[] getPrefixComponents() {
        return prefixComponents.clone();
    }

    public void setAvatar(LabelBuilder<?> labelBuilder, AvatarVariant size) {
        var builder = Components.avatar().profile();
        if (labelBuilder != null) {
            builder.name(labelBuilder);
        }
        if (size != null) {
            builder.withThemeVariants(size);
        }
        avatar = builder.build();
        setPrefix(avatar);
    }

    public void setAvatarName(String name) {
        if (avatar != null) {
            avatar.setName(name);
        } else if (name != null) {
            setAvatar(name, AvatarVariant.LUMO_XLARGE);
        }
    }

    public void setAvatar(String name, AvatarVariant size) {
        var builder = Components.avatar().profile().name(name);
        if (size != null) {
            builder.withThemeVariants(size);
        }
        avatar = builder.build();
        setPrefix(avatar);
    }

    public void setBreadcrumb(BreadcrumbItem... items) {
        if (isEmpty(items)) {
            clearBreadcrumb();
            return;
        }
        Breadcrumb newBreadcrumb = new Breadcrumb();
        for (BreadcrumbItem item : items) {
            if (item != null) {
                newBreadcrumb.add(item);
            }
        }
        setBreadcrumb(newBreadcrumb);
    }

    public void setBreadcrumb(Breadcrumb breadcrumb) {
        if (this.breadcrumb != null && this.breadcrumb != breadcrumb) {
            remove(this.breadcrumb);
            this.breadcrumb.setVisible(false);
        }
        this.breadcrumb = breadcrumb;
        if (this.breadcrumb != null) {
            this.breadcrumb.addClassName(CLASS_BREADCRUMB);
            this.breadcrumb.setVisible(true);
            Components.configure(this).addComponentAsFirst(this.breadcrumb);
        }
    }

    public void setDetails(Component... components) {
        if (isEmpty(components)) {
            detachFromColumn(details);
            details = null;
        } else {
            details = ensureSlot(details, CLASS_DETAILS);
            replaceContent(details, components);
        }
        refreshColumn();
    }

    public void setActions(Component... components) {
        if (isEmpty(components)) {
            detachFromColumnLine(actions);
            actions = null;
        } else {
            actions = ensureSlot(actions, CLASS_ACTIONS);
            replaceContent(actions, components);
            updateActionsVariant(components);
        }
        if (actions == null) {
            updateActionsVariant();
        }
        refreshColumn();
    }

    public void addActions(Component... components) {
        if (isEmpty(components)) {
            return;
        }
        actions = ensureSlot(actions, CLASS_ACTIONS);
        for (Component component : components) {
            if (component != null) {
                actions.add(component);
            }
        }
        updateActionsVariant(actions.getChildren().toArray(Component[]::new));
        refreshColumn();
    }

    public void setTabs(Tabs tabs) {
        if (this.tabs != null && this.tabs != tabs) {
            remove(this.tabs);
            this.tabs.setVisible(false);
        }
        this.tabs = tabs;
        if (this.tabs != null) {
            this.tabs.addClassName(CLASS_TABS);
            refreshTabs();
        } else {
            addClassName(CLASS_HEADER_BORDERED);
        }
    }

    public void setTabs(Tab... tabs) {
        if (isEmpty(tabs)) {
            ensureTabs();
            this.tabs.removeAll();
            refreshTabs();
            return;
        }

        ensureTabs();
        this.tabs.removeAll();
        for (Tab tab : tabs) {
            if (tab != null) {
                this.tabs.add(tab);
            }
        }
        refreshTabs();
    }

    public Optional<Tabs> getTabs() {
        return Optional.ofNullable(tabs);
    }

    public void setHeading(Component component) {
        if (component == null) {
            return;
        }
        heading = component;
        heading.setVisible(true);
        applyHeadingStyles(heading);
        refreshColumn();
    }

    public void setHeading(LabelBuilder<?> labelBuilder) {
        if (labelBuilder != null) {
            setHeading(labelBuilder.build());
        }
    }

    public void setHeading(String title) {
        if (heading != null) {
            heading.setVisible(true);
            heading.getElement().setText(title != null ? title : "");
        } else {
            setHeading(title, HeadingLevel.H2);
        }
    }

    public void setHeading(String newTitle, HeadingLevel headingLevel) {
        HeadingLevel level = headingLevel != null ? headingLevel : HeadingLevel.H2;
        heading = level.getComponent(newTitle != null ? newTitle : "");
        heading.setVisible(true);
        applyHeadingStyles(heading);
        refreshColumn();
    }

    public void setHeadingFontSize(Font.Size fontSize) {
        if (fontSize == headingFontSize) {
            return;
        }
        updateStyleClass(heading, headingFontSize, fontSize);
        headingFontSize = fontSize;
    }

    public void setHeadingFontWeight(Font.Weight fontWeight) {
        if (fontWeight == headingFontWeight) {
            return;
        }
        updateStyleClass(heading, headingFontWeight, fontWeight);
        headingFontWeight = fontWeight;
    }

    public void setHeadingLineHeight(Font.LineHeight lineHeight) {
        if (lineHeight == headingLineHeight) {
            return;
        }
        updateStyleClass(heading, headingLineHeight, lineHeight);
        headingLineHeight = lineHeight;
    }

    public void setHeadingTextColor(Color.Text textColor) {
        if (textColor == headingTextColor) {
            return;
        }
        updateStyleClass(heading, headingTextColor, textColor);
        headingTextColor = textColor;
    }

    public void setHeadingId(String id) {
        if (heading != null) {
            heading.setId(id);
        }
    }

    public void setHeadingVisible(boolean visible) {
        if (heading != null) {
            heading.setVisible(visible);
        }
    }

    public void setBordered(boolean bordered) {
        if (bordered) {
            removeClassName(CLASS_HEADER_NO_BORDER);
            addClassName(CLASS_HEADER_BORDERED);
        } else {
            removeClassName(CLASS_HEADER_BORDERED);
            addClassName(CLASS_HEADER_NO_BORDER);
        }
    }

    public void withoutBorder() {
        setBordered(false);
    }

    // noinspection UnusedDeclaration
    public Layout getRowLayout() {
        return row;
    }

    // noinspection UnusedDeclaration
    public Layout getTopRowLayout() {
        return topRow;
    }

    // noinspection UnusedDeclaration
    public Layout getColumnLayout() {
        return column;
    }

    private Layout ensureSlot(Layout slot, String className) {
        if (slot == null) {
            slot = new Layout();
            slot.addClassName(className);
        }
        return slot;
    }

    private void ensureTabs() {
        if (tabs == null) {
            tabs = new Tabs();
            tabs.addClassName(CLASS_TABS);
        }
    }

    private void refreshRow() {
        getTopRowLayout().removeAll();
        if (prefix != null) {
            getTopRowLayout().add(prefix);
        }
        getTopRowLayout().add(getColumnLayout());
        getRowLayout().removeAll();
        getRowLayout().add(getTopRowLayout());
    }

    private void refreshColumn() {
        columnLine.removeAll();
        if (heading != null) {
            columnLine.add(heading);
        }
        if (actions != null) {
            columnLine.add(actions);
        }
        getColumnLayout().removeAll();
        getColumnLayout().add(columnLine);
        if (details != null) {
            getColumnLayout().add(details);
        }
    }

    private void refreshTabs() {
        if (tabs == null) {
            addClassName(CLASS_HEADER_BORDERED);
            return;
        }
        if (tabs.getChildren().findFirst().isPresent()) {
            tabs.setVisible(true);
            removeClassName(CLASS_HEADER_BORDERED);
            if (tabs.getParent().isEmpty()) {
                add(tabs);
            }
        } else {
            tabs.setVisible(false);
            addClassName(CLASS_HEADER_BORDERED);
            if (tabs.getParent().isPresent()) {
                remove(tabs);
            }
        }
    }

    private void clearBreadcrumb() {
        if (breadcrumb != null) {
            remove(breadcrumb);
            breadcrumb.setVisible(false);
            breadcrumb = null;
        }
    }

    private void detachFromTopRow(Layout slot) {
        if (slot != null && slot.getParent().isPresent()) {
            getTopRowLayout().remove(slot);
        }
    }

    private void updatePrefixVariant(Component... components) {
        if (prefix == null) {
            return;
        }
        prefix.removeClassName(CLASS_PREFIX_AVATAR);
        getTopRowLayout().removeClassName(CLASS_TOP_ROW_AVATAR);
        if (components != null && components.length == 1 && components[0] instanceof Avatar) {
            prefix.addClassName(CLASS_PREFIX_AVATAR);
            getTopRowLayout().addClassName(CLASS_TOP_ROW_AVATAR);
        }
    }

    private void detachFromColumn(Layout slot) {
        if (slot != null && slot.getParent().isPresent()) {
            getColumnLayout().remove(slot);
        }
    }

    private void detachFromColumnLine(Layout slot) {
        if (slot != null && slot.getParent().isPresent()) {
            columnLine.remove(slot);
        }
    }

    private void updateActionsVariant(Component... components) {
        if (actions == null) {
            return;
        }
        actions.removeClassName(CLASS_ACTIONS_COMPACT);
        if (isCompactActions(components)) {
            actions.addClassName(CLASS_ACTIONS_COMPACT);
        }
    }

    private boolean isCompactActions(Component... components) {
        if (isEmpty(components)) {
            return false;
        }
        for (Component component : components) {
            if (!(component instanceof com.vaadin.flow.component.button.Button button)) {
                return false;
            }
            if (button.getText() != null && !button.getText().isBlank()) {
                return false;
            }
        }
        return true;
    }

    private void replaceContent(Layout layout, Component[] components) {
        layout.removeAll();
        for (Component component : components) {
            if (component != null) {
                layout.add(component);
            }
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

    private void updateStyleClass(Component target, Enum<?> oldValue, Enum<?> newValue) {
        if (target == null) {
            return;
        }
        if (oldValue != null) {
            target.removeClassName(classNameOf(oldValue));
        }
        if (newValue != null) {
            target.addClassNames(classNameOf(newValue));
        }
    }

    private String classNameOf(Enum<?> value) {
        if (value instanceof Font.Size size) {
            return size.getClassName();
        }
        if (value instanceof Font.Weight weight) {
            return weight.getClassName();
        }
        if (value instanceof Font.LineHeight lineHeight) {
            return lineHeight.getClassName();
        }
        if (value instanceof Color.Text textColor) {
            return textColor.getClassName();
        }
        return value != null ? value.name().toLowerCase() : "";
    }

    private boolean isEmpty(Component... components) {
        if (components == null) {
            return true;
        }
        for (Component component : components) {
            if (component != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty(Tab... tabs) {
        if (tabs == null) {
            return true;
        }
        for (Tab tab : tabs) {
            if (tab != null) {
                return false;
            }
        }
        return true;
    }

    public void setGap(Gap gap) {

    }
}
