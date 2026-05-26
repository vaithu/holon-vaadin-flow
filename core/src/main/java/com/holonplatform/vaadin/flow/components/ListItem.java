package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.css.Right;
import com.holonplatform.vaadin.flow.components.css.WhiteSpace;
import com.holonplatform.vaadin.flow.components.css.Wide;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;

public class ListItem extends FlexBoxLayout {

    private static final String CLASS_NAME = "list-item";

    private Div prefix;
    private Div suffix;

    private FlexBoxLayout content;

    private NativeLabel primary;
    private NativeLabel secondary;

    public ListItem(String primary, String secondary) {
        addClassName(CLASS_NAME);

        setAlignItems(Alignment.CENTER);
        setPadding(Wide.RESPONSIVE_L);
        setSpacing(Right.L);

        this.primary = new NativeLabel(primary);
        this.secondary = new NativeLabel(secondary);
        this.secondary.addClassNames("font-size-small", "color-text-secondary");

        content = new FlexBoxLayout(this.primary, this.secondary);
        content.setClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.COLUMN);
        add(content);
    }

    public ListItem(String primary) {
        this(primary, "");
    }

    /* === PREFIX === */

    public ListItem(Component prefix, String primary, String secondary) {
        this(primary, secondary);
        setPrefix(prefix);
    }

    public ListItem(Component prefix, String primary) {
        this(prefix, primary, "");
    }

    /* === SUFFIX === */

    public ListItem(String primary, String secondary, Component suffix) {
        this(primary, secondary);
        setSuffix(suffix);
    }

    public ListItem(String primary, Component suffix) {
        this(primary, null, suffix);
    }

    /* === PREFIX & SUFFIX === */

    public ListItem(Component prefix, String primary, String secondary,
                    Component suffix) {
        this(primary, secondary);
        setPrefix(prefix);
        setSuffix(suffix);
    }

    public ListItem(Component prefix, String primary, Component suffix) {
        this(prefix, primary, "", suffix);
    }

    /* === MISC === */

    public FlexBoxLayout getContent() {
        return content;
    }

    public void setWhiteSpace(WhiteSpace whiteSpace) {
        UIUtils.setWhiteSpace(whiteSpace, this);
    }

    public void setReverse(boolean reverse) {
        if (reverse) {
            content.setFlexDirection(FlexDirection.COLUMN_REVERSE);
        } else {
            content.setFlexDirection(FlexDirection.COLUMN);
        }
    }

    public void setHorizontalPadding(boolean horizontalPadding) {
        if (horizontalPadding) {
            addClassName("list-item--no-h-padding");
        } else {
            removeClassName("list-item--no-h-padding");
        }
    }

    public void setPrimaryText(String text) {
        primary.setText(text);
    }

    /**
     * Sets the primary text from a {@link Localizable} descriptor.
     *
     * @param text localizable primary label (not null)
     */
    public void setPrimaryText(Localizable text) {
        primary.setText(resolve(text));
    }

    public NativeLabel getPrimary() {
        return primary;
    }

    public void setSecondaryText(String text) {
        secondary.setText(text);
    }

    /**
     * Sets the secondary text from a {@link Localizable} descriptor.
     *
     * @param text localizable secondary label (not null)
     */
    public void setSecondaryText(Localizable text) {
        secondary.setText(resolve(text));
    }

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    public void setPrefix(Component... components) {
        if (prefix == null) {
            prefix = Components.div().build();
            prefix.setClassName(CLASS_NAME + "__prefix");
            getElement().insertChild(0, prefix.getElement());
            getElement().setAttribute("with-prefix", true);
        }
        prefix.removeAll();
        prefix.add(components);
    }

    public void setSuffix(Component... components) {
        if (suffix == null) {
            suffix = Components.div().build();
            suffix.setClassName(CLASS_NAME + "__suffix");
            getElement().insertChild(getElement().getChildCount(),
                    suffix.getElement());
            getElement().setAttribute("with-suffix", true);
        }
        suffix.removeAll();
        suffix.add(components);
    }

    public void setDividerVisible(boolean visible) {
        if (visible) {
            getElement().setAttribute("with-divider", true);
        } else {
            getElement().removeAttribute("with-divider");
        }
    }

}
