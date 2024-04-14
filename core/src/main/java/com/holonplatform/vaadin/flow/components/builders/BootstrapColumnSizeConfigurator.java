package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.ColumnSize;

public interface BootstrapColumnSizeConfigurator<C> {

    C small(ColumnSize column);
    C medium(ColumnSize column);
    C large(ColumnSize column);
    C xLarge(ColumnSize column);
    C xxLarge(ColumnSize column);
}
