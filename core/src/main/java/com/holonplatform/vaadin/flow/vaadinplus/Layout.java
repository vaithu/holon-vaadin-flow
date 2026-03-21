package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Getter;

import java.util.HashMap;

public class Layout extends Div {

    private AlignItems alignItems;
    private AlignSelf alignSelf;
    private BoxSizing boxSizing;
    private Display display;
    private final HashMap<Breakpoint, Display> responsiveDisplay;
    @Getter
    private FlexDirection flexDirection;
    private FlexWrap flexWrap;
    private final HashMap<Breakpoint, FlexDirection> responsiveFlexDirection;
    private ColumnGap colGap;
    private RowGap rowGap;
    private GridColumns gridColumns;
    private final HashMap<Breakpoint, GridColumns> responsiveColumns;
    private final HashMap<Component, ColumnSpan> columnSpans;
    private JustifyContent justifyContent;
    private LineClamp lineClamp;
    private Overflow overflow;
    private Position position;

    public Layout() {
//        setDisplay(Display.FLEX);
        this.responsiveDisplay = new HashMap<>();
        this.responsiveFlexDirection = new HashMap<>();
        this.responsiveColumns = new HashMap<>();
        this.columnSpans = new HashMap<>();
    }

    public Layout(Component... components) {
        this();
        add(components);
    }

    public void setAlignItems(AlignItems alignItems) {
        if (this.alignItems != null) {
            removeClassNames(this.alignItems.getClassName());
        }
        addClassNames(alignItems.getClassName());
        this.alignItems = alignItems;
    }

    public void setAlignSelf(AlignSelf alignSelf) {
        if (this.alignSelf != null) {
            removeClassNames(this.alignSelf.getClassName());
        }
        addClassNames(alignSelf.getClassName());
        this.alignSelf = alignSelf;
    }

    public void setBoxSizing(BoxSizing boxSizing) {
        if (this.boxSizing != null) {
            removeClassNames(this.boxSizing.getClassName());
        }
        addClassNames(boxSizing.getClassName());
        this.boxSizing = boxSizing;
    }

    public void setDisplay(Display display) {
        if (this.display != null) {
            removeClassNames(this.display.getClassName());
        }
        addClassNames(display.getClassName());
        this.display = display;
    }

    public void setDisplay(Breakpoint breakpoint, Display display) {
        if (this.responsiveDisplay.get(breakpoint) != null) {
            removeClassName(breakpoint.getPrefix() + ":" + this.responsiveDisplay.get(breakpoint).getClassName());
        }
        addClassNames(breakpoint.getPrefix() + ":" + display.getClassName());
        this.responsiveDisplay.put(breakpoint, display);
    }

    public void setFlexDirection(FlexDirection flexDirection) {
        if (this.flexDirection != null) {
            removeClassNames(this.flexDirection.getClassName());
        }
        addClassNames(flexDirection.getClassName());
        this.flexDirection = flexDirection;
    }

    public void setFlexDirection(Breakpoint breakpoint, FlexDirection flexDirection) {
        if (this.responsiveFlexDirection.get(breakpoint) != null) {
            removeClassName(breakpoint.getPrefix() + ":" + this.responsiveFlexDirection.get(breakpoint).getClassName());
        }
        addClassNames(breakpoint.getPrefix() + ":" + flexDirection.getClassName());
        this.responsiveFlexDirection.put(breakpoint, flexDirection);
    }

    public void setFlexBasis(String flexBasis, Component... components) {
        for (Component component : components) {
            component.getStyle().setFlexBasis(flexBasis);
        }
    }

    public void setFlexGrow() {
        addClassNames(LumoUtility.Flex.GROW);
    }

    public void setFlexGrow(Component... components) {
        for (Component component : components) {
            component.addClassNames(LumoUtility.Flex.GROW);
        }
    }

    public void setFlexWrap(FlexWrap flexWrap) {
        if (this.flexWrap != null) {
            removeClassNames(this.flexWrap.getClassName());
        }
        addClassNames(flexWrap.getClassName());
        this.flexWrap = flexWrap;
    }

    /**
     * Sets both the column (horizontal) and row (vertical) gap between components.
     */
    public void setGap(Gap gap) {
        setColumnGap(gap);
        setRowGap(gap);
    }

    /**
     * Sets the column (horizontal) gap between components.
     */
    public void setColumnGap(Gap gap) {
        removeColumnGap();
        this.addClassNames(gap.getColumnGap().getClassName());
        this.colGap = gap.getColumnGap();
    }

    /**
     * Sets the row (vertical) gap between components.
     */
    public void setRowGap(Gap gap) {
        removeRowGap();
        this.addClassNames(gap.getRowGap().getClassName());
        this.rowGap = gap.getRowGap();
    }

    /**
     * Removes both the column (horizontal) and row (vertical) gap between components.
     */
    public void removeGap() {
        removeColumnGap();
        removeRowGap();
    }

    /**
     * Removes the column (horizontal) gap between components.
     */
    public void removeColumnGap() {
        if (this.colGap != null) {
            this.removeClassName(this.colGap.getClassName());
        }
        this.colGap = null;
    }

    /**
     * Removes the row (vertical) gap between components.
     */
    public void removeRowGap() {
        if (this.rowGap != null) {
            this.removeClassName(this.rowGap.getClassName());
        }
        this.rowGap = null;
    }

    /**
     * Sets the default number of grid columns.
     */
    public void setColumns(GridColumns gridColumns) {
        if (this.gridColumns != null) {
            removeClassNames(this.gridColumns.getClassName());
        }
        addClassNames(gridColumns.getClassName());
        this.gridColumns = gridColumns;
    }

    /**
     * Sets the number of grid columns for a given breakpoint.
     */
    public void setColumns(Breakpoint breakpoint, GridColumns gridColumns) {
        if (this.responsiveColumns.get(breakpoint) != null) {
            removeClassName(breakpoint.getPrefix() + ":" + this.responsiveColumns.get(breakpoint).getClassName());
        }
        addClassNames(breakpoint.getPrefix() + ":" + gridColumns.getClassName());
        this.responsiveColumns.put(breakpoint, gridColumns);
    }

    public void setColumnSpan(ColumnSpan columnSpan, Component... components) {
        for (Component component : components) {
            if (this.columnSpans.get(component) != null) {
                component.removeClassName(this.columnSpans.get(component).getClassName());
            }
            component.addClassNames(columnSpan.getClassName());
            this.columnSpans.put(component, columnSpan);
        }
    }

    /**
     * Sets the justify content property.
     */
    public void setJustifyContent(JustifyContent justifyContent) {
        if (this.justifyContent != null) {
            removeClassName(this.justifyContent.getClassName());
        }
        addClassNames(justifyContent.getClassName());
        this.justifyContent = justifyContent;
    }

    /**
     * Sets the line clamp property.
     */
    public void setLineClamp(LineClamp lineClamp) {
        if (this.lineClamp != null) {
            removeClassName(this.lineClamp.getClassName());
        }
        addClassNames(lineClamp.getClassName());
        this.lineClamp = lineClamp;
    }

    /**
     * Sets the overflow property.
     */
    public void setOverflow(Overflow overflow) {
        if (this.overflow != null) {
            removeClassNames(this.overflow.getClassName());
        }
        addClassNames(overflow.getClassName());
        this.overflow = overflow;
    }

    public void setPosition(Position position) {
        if (this.position != null) {
            this.removeClassName(this.position.getClassName());
        }
        addClassNames(position.getClassName());
        this.position = position;
    }


}
