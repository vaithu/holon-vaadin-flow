package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;

@StyleSheet("context://footer.css")
public class Footer extends Layout {

    private static final String CLASS_FOOTER = "iyen-footer";
    private static final String CLASS_FOOTER_BORDERED = "iyen-footer--bordered";
    private static final String CLASS_FOOTER_NO_BORDER = "footer--no-border";
    private static final String CLASS_MAIN = "iyen-footer__main";
    private static final String CLASS_BOTTOM = "iyen-footer__bottom";
    private static final String CLASS_BRAND = "iyen-footer__brand";
    private static final String CLASS_NAVIGATION = "iyen-footer__navigation";
    private static final String CLASS_ACTIONS = "iyen-footer__actions";
    private static final String CLASS_ACTIONS_COMPACT = "iyen-footer__actions--compact";
    private static final String CLASS_META = "iyen-footer__meta";
    private static final String CLASS_LEGAL = "iyen-footer__legal";

    private final Layout mainRow = new Layout();
    private final Layout bottomRow = new Layout();
    private Layout brand;
    private Layout navigation;
    private Layout actions;
    private Layout meta;
    private Layout legal;
    private String backgroundClassName;
    private Component[] brandComponents = new Component[0];
    private Component[] navigationComponents = new Component[0];
    private Component[] actionsComponents = new Component[0];
    private Component[] metaComponents = new Component[0];
    private Component[] legalComponents = new Component[0];

    public Footer() {
        addClassName(CLASS_FOOTER);
        addClassName(CLASS_FOOTER_NO_BORDER);
        getElement().setAttribute("role", "contentinfo");

        mainRow.addClassName(CLASS_MAIN);
        bottomRow.addClassName(CLASS_BOTTOM);
    }

    public void setBrand(Component... components) {
        this.brandComponents = components != null ? components.clone() : new Component[0];
        if (isEmpty(components)) {
            detach(mainRow, brand);
            brand = null;
        } else {
            brand = ensureSlot(brand, CLASS_BRAND);
            replaceContent(brand, components);
        }
        refreshMainRow();
    }

    public void setPrefix(Component... components) {
        setBrand(components);
    }

    public Component[] getBrandComponents() {
        return brandComponents.clone();
    }

    public Component[] getPrefixComponents() {
        return getBrandComponents();
    }

    public void setNavigation(Component... components) {
        this.navigationComponents = components != null ? components.clone() : new Component[0];
        if (isEmpty(components)) {
            detach(mainRow, navigation);
            navigation = null;
        } else {
            navigation = ensureSlot(navigation, CLASS_NAVIGATION);
            replaceContent(navigation, components);
        }
        refreshMainRow();
    }

    public void setDetails(Component... components) {
        setNavigation(components);
    }

    public Component[] getNavigationComponents() {
        return navigationComponents.clone();
    }

    public Component[] getDetailsComponents() {
        return getNavigationComponents();
    }

    public void setActions(Component... components) {
        this.actionsComponents = components != null ? components.clone() : new Component[0];
        if (isEmpty(components)) {
            detach(mainRow, actions);
            actions = null;
        } else {
            actions = ensureSlot(actions, CLASS_ACTIONS);
            replaceContent(actions, components);
            updateActionsVariant(components);
        }
        if (actions == null) {
            updateActionsVariant();
        }
        refreshMainRow();
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
        actionsComponents = actions.getChildren().filter(component -> component != null).toArray(Component[]::new);
        updateActionsVariant(actionsComponents);
        refreshMainRow();
    }

    public Component[] getActionsComponents() {
        return actionsComponents.clone();
    }

    public void setMeta(Component... components) {
        this.metaComponents = components != null ? components.clone() : new Component[0];
        if (isEmpty(components)) {
            detach(bottomRow, meta);
            meta = null;
        } else {
            meta = ensureSlot(meta, CLASS_META);
            replaceContent(meta, components);
        }
        refreshBottomRow();
    }

    public Component[] getMetaComponents() {
        return metaComponents.clone();
    }

    public void setLegal(Component... components) {
        this.legalComponents = components != null ? components.clone() : new Component[0];
        if (isEmpty(components)) {
            detach(bottomRow, legal);
            legal = null;
        } else {
            legal = ensureSlot(legal, CLASS_LEGAL);
            replaceContent(legal, components);
        }
        refreshBottomRow();
    }

    public Component[] getLegalComponents() {
        return legalComponents.clone();
    }

    public void setBordered(boolean bordered) {
        if (bordered) {
            removeClassName(CLASS_FOOTER_NO_BORDER);
            addClassName(CLASS_FOOTER_BORDERED);
        } else {
            removeClassName(CLASS_FOOTER_BORDERED);
            addClassName(CLASS_FOOTER_NO_BORDER);
        }
    }

    public void withoutBorder() {
        setBordered(false);
    }

    public void background(Color.Background background) {
        if (this.backgroundClassName != null) {
            removeClassName(this.backgroundClassName);
        }
        this.backgroundClassName = background != null ? background.getClassName() : null;
        if (this.backgroundClassName != null) {
            addClassName(this.backgroundClassName);
        }
    }

    public Layout getMainRowLayout() {
        return mainRow;
    }

    public Layout getBottomRowLayout() {
        return bottomRow;
    }

    private Layout ensureSlot(Layout slot, String className) {
        if (slot == null) {
            slot = new Layout();
            slot.addClassName(className);
        }
        return slot;
    }

    private void refreshMainRow() {
        mainRow.removeAll();
        if (brand != null) {
            mainRow.add(brand);
        }
        if (navigation != null) {
            mainRow.add(navigation);
        }
        if (actions != null) {
            mainRow.add(actions);
        }
        updateRowPresence(mainRow, true);
    }

    private void refreshBottomRow() {
        bottomRow.removeAll();
        if (meta != null) {
            bottomRow.add(meta);
        }
        if (legal != null) {
            bottomRow.add(legal);
        }
        updateRowPresence(bottomRow, false);
    }

    private void updateRowPresence(Layout row, boolean firstChild) {
        if (row.getChildren().findAny().isPresent()) {
            if (row.getParent().isEmpty()) {
                if (firstChild) {
                    addComponentAsFirst(row);
                } else {
                    add(row);
                }
            }
        } else {
            detach(this, row);
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

    private void detach(Layout parent, Layout slot) {
        if (slot != null && slot.getParent().isPresent()) {
            parent.remove(slot);
        }
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
}