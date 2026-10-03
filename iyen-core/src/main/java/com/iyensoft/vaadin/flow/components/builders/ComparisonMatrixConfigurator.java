package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.TableRow;

public interface ComparisonMatrixConfigurator<C extends ComparisonMatrixConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C heading(String heading);

    C column(String title);

    C column(Component header);

    C row(String label, String... values);

    C row(String label, Component... values);

    C row(TableRow row);

    static BaseComparisonMatrixConfigurator configure(ComparisonMatrix matrix) {
        return new com.iyensoft.vaadin.flow.internal.components.builders.DefaultComparisonMatrixConfigurator(matrix);
    }

    interface BaseComparisonMatrixConfigurator
            extends ComparisonMatrixConfigurator<BaseComparisonMatrixConfigurator> {
    }
}
