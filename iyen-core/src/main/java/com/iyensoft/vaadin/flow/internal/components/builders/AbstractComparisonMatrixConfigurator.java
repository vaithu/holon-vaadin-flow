package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.iyensoft.vaadin.flow.components.builders.ComparisonMatrixConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.TableRow;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractComparisonMatrixConfigurator<C extends ComparisonMatrixConfigurator<C>>
        extends AbstractComponentConfigurator<ComparisonMatrix, C>
        implements ComparisonMatrixConfigurator<C> {

    protected AbstractComparisonMatrixConfigurator(ComparisonMatrix matrix) {
        super(matrix);
    }

    @Override
    public C heading(String heading) {
        getComponent().setHeading(heading);
        return getConfigurator();
    }

    @Override
    public C column(String title) {
        getComponent().addColumn(title);
        return getConfigurator();
    }

    @Override
    public C column(Component header) {
        getComponent().addColumn(header);
        return getConfigurator();
    }

    @Override
    public C row(String label, String... values) {
        getComponent().addRow(label, values);
        return getConfigurator();
    }

    @Override
    public C row(String label, Component... values) {
        getComponent().addRow(label, values);
        return getConfigurator();
    }

    @Override
    public C row(TableRow row) {
        getComponent().addRow(row);
        return getConfigurator();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
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
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
