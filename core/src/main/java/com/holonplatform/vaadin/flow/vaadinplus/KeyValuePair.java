package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.vaadin.flow.internal.lumo.Breakpoint;
import com.holonplatform.vaadin.flow.internal.lumo.FlexRowBreakpoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.DescriptionList;

@StyleSheet("context://key-value-pair.css")
public class KeyValuePair extends Layout {

    private FlexRowBreakpoint breakpoint;
    private KeyPosition keyPosition;

    private final DescriptionList.Term key;
    private final DescriptionList.Description value1;

    public KeyValuePair(String key, String value) {
        this(new Text(key), new Text(value));
    }

    public KeyValuePair(String key, Component value) {
        this(new Text(key), value);
    }

    public KeyValuePair(Component key, Component value) {
        this.key = new DescriptionList.Term(key);
        this.key.addClassName("key-value-pair__key");
        this.value1 = new DescriptionList.Description(value);
        this.value1.addClassName("key-value-pair__value");
        add(this.key, this.value1);

        addClassName("key-value-pair");
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
        this.key.removeClassName("key-value-pair__key--grow");
    }

    public void setKeyWidthFull() {
        this.key.setMinWidth(null);
        this.key.addClassName("key-value-pair__key--grow");
    }

    public void removeHorizontalPadding() {
        removeClassName("key-value-pair--padded");
        addClassName("key-value-pair--no-h-padding");
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
