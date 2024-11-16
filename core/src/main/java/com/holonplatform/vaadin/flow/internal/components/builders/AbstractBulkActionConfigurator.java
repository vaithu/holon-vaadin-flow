package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractBulkActionConfigurator<C extends BulkActionConfigurator<C>>
        extends AbstractComponentConfigurator<HorizontalLayout, C>
        implements BulkActionConfigurator<C> {

//    private final Div contentDiv;
    private  DefaultCloseButtonBuilder closeButtonBuilder;

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractBulkActionConfigurator(HorizontalLayout component) {
        super(component);
        getComponent().setWidthFull();
        getComponent().setAlignItems(FlexComponent.Alignment.BASELINE);

      /*  contentDiv = Components.div()
                .styleNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW)
                .styleName(LumoUtility.Margin.Left.AUTO)
                .build();

        getComponent().add(contentDiv);*/


    }

    @Override
    public C optionsMenuBar(MenuBar menuBar) {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE, MenuBarVariant.LUMO_END_ALIGNED);
        getComponent().add(menuBar);
        return getConfigurator();
    }

    @Override
    public C closeButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        closeButtonBuilder = new DefaultCloseButtonBuilder();
        configurator.accept(ButtonConfigurator.configure(closeButtonBuilder.getComponent()));
        getComponent().addComponentAtIndex(getComponent().getComponentCount(),closeButtonBuilder.getComponent());
        return getConfigurator();
    }

    @Override
    public C selected(LabelBuilder<?> label) {

        getComponent().add(label.build());
        return getConfigurator();
    }

    @Override
    public C selected(Span span) {
        getComponent().add(span);
        return getConfigurator();
    }

    private Span createSpan(String text) {
        return LabelBuilder.span().text(text).title(text).build();
    }

    @Override
    public C selected(int selectedCount) {
        getComponent().add(createSpan(String.format("%d selected", selectedCount)));
        return getConfigurator();
    }

    @Override
    public C selected(String selectedText) {
        getComponent().add(createSpan(selectedText));
        return getConfigurator();
    }

    @Override
    public C bulkAction(MenuBar menuBar) {

        getComponent().add(menuBar);
        return getConfigurator();
    }

    @Override
    public C bulkAction(ContextMenuBuilder contextMenuBuilder, Component target) {
        contextMenuBuilder.build(target);
        getComponent().add(target);
        return getConfigurator();
    }

    @Override
    public C selectAll(BooleanInputBuilder builder) {
        builder.styleName(LumoUtility.Margin.Right.AUTO);
        getComponent().addComponentAsFirst(builder.build().getComponent());
        return getConfigurator();
    }

    @Override
    public C selectAll(Checkbox checkbox) {
        getComponent().addComponentAsFirst(checkbox);
        return getConfigurator();
    }

    @Override
    public C withPostProcessor(Consumer<BulkActionConfigurator<C>> postProcessor) {
        postProcessor.accept(this);
        return getConfigurator();
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
     *
     * @return Optional component as {@link HasStyle}, if supported
     */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
     *
     * @return Optional component as {@link HasEnabled}, if supported
     */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
     *
     * @return Optional component as {@link HasTooltip}, if supported
     */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
