package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.iyensoft.vaadin.flow.components.builders.ComparisonMatrixBuilder;

public class DefaultComparisonMatrixBuilder
        extends AbstractComparisonMatrixConfigurator<ComparisonMatrixBuilder>
        implements ComparisonMatrixBuilder {

    public DefaultComparisonMatrixBuilder(ComparisonMatrix matrix) {
        super(matrix);
    }

    @Override
    public ComparisonMatrix build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected ComparisonMatrixBuilder getConfigurator() {
        return this;
    }
}
