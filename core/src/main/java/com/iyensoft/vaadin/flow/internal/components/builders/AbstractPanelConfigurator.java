package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LayoutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.iyensoft.vaadin.flow.components.IyenPanel;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractPanelConfigurator<C extends LayoutConfigurator<C>>
        extends AbstractComponentConfigurator<IyenPanel,C>
        implements LayoutConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component The component instance (not getConfigurator())
     */
    public AbstractPanelConfigurator(IyenPanel component) {
        super(component);
    }

    @Override
    public C alignItems(AlignItems alignItems) {
        getComponent().setAlignItems(alignItems);
        return getConfigurator();
    }

    @Override
    public C alignSelf(AlignSelf alignSelf) {
        getComponent().setAlignSelf(alignSelf);
        return getConfigurator();
    }

    @Override
    public C boxSizing(BoxSizing boxSizing) {
        getComponent().setBoxSizing(boxSizing);
        return getConfigurator();
    }

    @Override
    public C display(Display display) {
        getComponent().setDisplay(display);
        return getConfigurator();
    }

    @Override
    public C flex() {
        return display(Display.FLEX);
    }

    @Override
    public C add(Component... components) {
        getComponent().add(components);
        return getConfigurator();
    }

    @Override
    public C add(String text) {
        getComponent().add(text);
        return getConfigurator();
    }

    @Override
    public C addComponentAsFirst(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C addComponentAtIndex(int index, Component component) {
        getComponent().addComponentAtIndex(index, component);
        return getConfigurator();
    }

    @Override
    public C display(Breakpoint breakpoint, Display display) {
        getComponent().setDisplay(breakpoint, display);
        return getConfigurator();
    }

    @Override
    public C flexDirection(FlexDirection flexDirection) {
        getComponent().setFlexDirection(flexDirection);
        return getConfigurator();
    }

    @Override
    public C flexDirection(Breakpoint breakpoint, FlexDirection flexDirection) {
        getComponent().setFlexDirection(breakpoint,flexDirection);
        return getConfigurator();
    }

    @Override
    public C display(ViewMode viewMode, Display display) {

        return display(viewMode.toBreakpoint(), display);
    }

    @Override
    public C flexDirection(ViewMode viewMode, FlexDirection flexDirection) {
        return flexDirection(viewMode.toBreakpoint(), flexDirection);
    }

    @Override
    public C flexBasis(String flexBasis, Component... components) {
        getComponent().setFlexBasis(flexBasis, components);
        return getConfigurator();
    }

    @Override
    public C flexGrow() {
        getComponent().setFlexGrow();
        return getConfigurator();
    }

    @Override
    public C flexGrow(Component... components) {
        getComponent().setFlexGrow(components);
        return getConfigurator();
    }

    @Override
    public C flexWrap(FlexWrap flexWrap) {
        getComponent().setFlexWrap(flexWrap);
        return getConfigurator();
    }

    @Override
    public C gap(Gap gap) {
        getComponent().setGap(gap);
        return getConfigurator();
    }

    @Override
    public C columnGap(Gap gap) {
        getComponent().setColumnGap(gap);
        return getConfigurator();
    }

    @Override
    public C rowGap(Gap gap) {
        getComponent().setRowGap(gap);
        return getConfigurator();
    }

    @Override
    public C columns(GridColumns gridColumns) {
        getComponent().setColumns(gridColumns);
        return getConfigurator();
    }

    @Override
    public C columns(Breakpoint breakpoint, GridColumns gridColumns) {
        getComponent().setColumns(breakpoint, gridColumns);
        return getConfigurator();
    }

    @Override
    public C columnSpan(ColumnSpan columnSpan, Component... components) {
        getComponent().setColumnSpan(columnSpan, components);
        return getConfigurator();
    }

    @Override
    public C justifyContent(JustifyContent justifyContent) {
        getComponent().setJustifyContent(justifyContent);
        return getConfigurator();
    }

    @Override
    public C lineClamp(LineClamp lineClamp) {
        getComponent().setLineClamp(lineClamp);
        return getConfigurator();
    }

    @Override
    public C overflow(Overflow overflow) {
        getComponent().setOverflow(overflow);
        return getConfigurator();
    }

    @Override
    public C position(Position position) {
        getComponent().setPosition(position);
        return getConfigurator();
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

}