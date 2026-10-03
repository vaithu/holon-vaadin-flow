package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.iyensoft.vaadin.flow.components.builders.ComparisonMatrixConfigurator;

public class DefaultComparisonMatrixConfigurator
        extends AbstractComparisonMatrixConfigurator<ComparisonMatrixConfigurator.BaseComparisonMatrixConfigurator>
        implements ComparisonMatrixConfigurator.BaseComparisonMatrixConfigurator {

    public DefaultComparisonMatrixConfigurator(ComparisonMatrix matrix) {
        super(matrix);
    }

    @Override
    protected ComparisonMatrixConfigurator.BaseComparisonMatrixConfigurator getConfigurator() {
        return this;
    }
}
