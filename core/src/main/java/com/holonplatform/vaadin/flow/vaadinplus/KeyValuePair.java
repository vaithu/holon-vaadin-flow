package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.vaadin.flow.internal.lumo.Breakpoint;
import com.holonplatform.vaadin.flow.internal.lumo.FlexRowBreakpoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.DescriptionList;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

public class KeyValuePair extends Layout {

    private FlexRowBreakpoint breakpoint;
    private KeyPosition keyPosition;

    private final DescriptionList.Term key;

    public KeyValuePair(String key, String value) {
        this(new Text(key), new Text(value));
    }

    public KeyValuePair(String key, Component value) {
        this(new Text(key), value);
    }

    public KeyValuePair(Component key, Component value) {
        this.key = new DescriptionList.Term(key);
        this.key.addClassNames(FontSize.SMALL, FontWeight.MEDIUM, TextColor.SECONDARY);

        DescriptionList.Description value1 = new DescriptionList.Description(value);
        value1.addClassNames(Margin.Start.NONE);

        add(this.key, value1);

        addClassNames(Padding.Horizontal.MEDIUM, Padding.Vertical.SMALL);
        setAlignItems(com.holonplatform.vaadin.flow.internal.lumo.AlignItems.BASELINE);
        setBreakpoint(Breakpoint.MEDIUM);
        setColumnGap(com.holonplatform.vaadin.flow.internal.lumo.Gap.MEDIUM);
        setKeyPosition(KeyPosition.SIDE);
        setKeyWidth(25, Unit.PERCENTAGE);
        setPosition(com.holonplatform.vaadin.flow.internal.lumo.Position.RELATIVE);
    }

    /**
     * Determines when the key is positioned on top.
     * Only works with KeyPosition.SIDE. Otherwise, the key is always on top.
     */
    public void setBreakpoint(Breakpoint breakpoint) {
        if (this.breakpoint != null) {
            removeClassNames(this.breakpoint.getClassName());
        }
        this.breakpoint = breakpoint != null ? breakpoint.getFlexRowBreakpoint() : null;
        updateClassNames();
    }

    public void removeBreakpoint() {
        setBreakpoint(null);
    }

    public void setKeyPosition(KeyPosition keyPosition) {
        this.keyPosition = keyPosition;
        updateClassNames();
    }

    public void setKeyWidth(float width, Unit unit) {
        this.key.setMinWidth(width, unit);
        this.key.removeClassNames(Flex.GROW);
    }

    public void setKeyWidthFull() {
        this.key.setMinWidth(null);
        this.key.addClassNames(Flex.GROW);
    }

    public void removeHorizontalPadding() {
        removeClassName(Padding.Horizontal.MEDIUM);
    }

    private void updateClassNames() {
        if (this.keyPosition != null) {
            if (this.keyPosition.equals(KeyPosition.SIDE)) {
                // If there's a breakpoint, we set the flex direction to column
                // because our responsive styles are mobile-first.
                if (this.breakpoint != null) {
                    setFlexDirection(com.holonplatform.vaadin.flow.internal.lumo.FlexDirection.COLUMN);
                    addClassNames(this.breakpoint.getClassName());
                } else {
                    setFlexDirection(com.holonplatform.vaadin.flow.internal.lumo.FlexDirection.ROW);
                }
            } else {
                setFlexDirection(com.holonplatform.vaadin.flow.internal.lumo.FlexDirection.COLUMN);
                if (this.breakpoint != null) {
                    removeClassNames(this.breakpoint.getClassName());
                }
            }
        }
    }

    public enum KeyPosition {
        SIDE, TOP
    }

}
