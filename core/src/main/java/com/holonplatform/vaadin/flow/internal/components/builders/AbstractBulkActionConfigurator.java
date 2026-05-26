package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractBulkActionConfigurator<C extends BulkActionConfigurator<C>>
        extends AbstractComponentConfigurator<HorizontalLayout, C>
        implements BulkActionConfigurator<C> {

    private DefaultCloseButtonBuilder closeButtonBuilder;

    public AbstractBulkActionConfigurator(HorizontalLayout component) {
        super(component);
        getComponent().setWidthFull();
        getComponent().setAlignItems(FlexComponent.Alignment.BASELINE);
    }

    @Override
    public C optionsMenuBar(MenuBar menuBar) {
        menuBar.addClassName("bulk-action__options-menu");
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
    public C selected(Localizable selectedText) {
        String resolved = LocalizationProvider.localize(selectedText)
                .orElseGet(() -> selectedText.getMessage() != null ? selectedText.getMessage() : "");
        return selected(resolved);
    }

    @Override
    public C bulkAction(MenuBar menuBar) {
        menuBar.addClassName("btn--push-end");
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

    @Override
    public C add(Component... components) {
        getComponent().add(components);
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
    public C add(String text) {
        getComponent().add(new com.vaadin.flow.component.Text(text));
        return getConfigurator();
    }

}
