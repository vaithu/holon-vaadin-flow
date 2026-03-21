package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLayoutConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.theme.lumo.LumoUtility;

public interface LayoutConfigurator<C extends LayoutConfigurator<C>> extends ComponentConfigurator<C>,
        HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C>, HasComponentsConfigurator<C>  {

    C alignItems(AlignItems alignItems);

    C alignSelf(AlignSelf alignSelf);

    C boxSizing(BoxSizing boxSizing);

    C display(Display display);

    C flex();

    C display(Breakpoint breakpoint, Display display);

    C display(ViewMode viewMode, Display display);

    C flexDirection(FlexDirection flexDirection);

    C flexDirection(Breakpoint breakpoint, FlexDirection flexDirection);

    C flexDirection(ViewMode viewMode, FlexDirection flexDirection);
    C flexBasis(String flexBasis, Component... components);

    C flexGrow();

    C flexGrow(Component... components);

    C flexWrap(FlexWrap flexWrap);
    /**
     * Sets both the column (horizontal) and row (vertical) gap between components.
     */
    C gap(Gap gap);
    /**
     * Sets the column (horizontal) gap between components.
     */
    C columnGap(Gap gap);
    /**
     * Sets the row (vertical) gap between components.
     */
    C rowGap(Gap gap);

    /**
     * Sets the default number of grid columns.
     */
    C columns(GridColumns gridColumns);

    /**
     * Sets the number of grid columns for a given breakpoint.
     */
    C columns(Breakpoint breakpoint, GridColumns gridColumns);
 /**
     * Sets the number of grid columns for a given breakpoint.
     */
    default C columns(ViewMode viewMode, GridColumns gridColumns) {
        return columns(viewMode.toBreakpoint(),gridColumns);
    }

    C columnSpan(ColumnSpan columnSpan, Component... components);

    /**
     * Sets the justify content property.
     */
    C justifyContent(JustifyContent justifyContent);

    /**
     * Sets the line clamp property.
     */
    C lineClamp(LineClamp lineClamp);

    /**
     * Sets the overflow property.
     */
    C overflow(Overflow overflow);

    C position(Position position);

    default C spacing() {
        return gap(Gap.MEDIUM);
    }

    default C padding() {
        return styleName(LumoUtility.Padding.MEDIUM);
    }

    default C addAndExpand(Component component) {
        return add(component).flexGrow(component);
    }

    default C horizontal() {
        return flexDirection(FlexDirection.ROW);
    }

    default C vertical() {
        return flexDirection(FlexDirection.COLUMN);
    }


    /**
     * Get a new {@link LayoutConfigurator} for given component.
     *
     * @param component The component to create (not null)
     * @return A new {@link LayoutConfigurator}
     */
    static LayoutConfigurator.BaseLayoutConfigurator configure(Layout component) {
        return new DefaultLayoutConfigurator(component);
    }

    /**
     * Base configurator.
     */
    public interface BaseLayoutConfigurator extends LayoutConfigurator<LayoutConfigurator.BaseLayoutConfigurator> {

    }
}
