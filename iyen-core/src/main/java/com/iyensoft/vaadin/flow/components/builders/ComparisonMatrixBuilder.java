package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultComparisonMatrixBuilder;

public interface ComparisonMatrixBuilder extends ComparisonMatrixConfigurator<ComparisonMatrixBuilder>,
        ComponentBuilder<ComparisonMatrix, ComparisonMatrixBuilder> {

    static ComparisonMatrixBuilder create() {
        return create(new ComparisonMatrix());
    }

    static ComparisonMatrixBuilder create(ComparisonMatrix matrix) {
        return new DefaultComparisonMatrixBuilder(matrix);
    }
}
