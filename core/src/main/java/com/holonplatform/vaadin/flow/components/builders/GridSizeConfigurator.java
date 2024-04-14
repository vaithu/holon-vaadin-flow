package com.holonplatform.vaadin.flow.components.builders;

public interface GridSizeConfigurator<C> {


    C small(int column);
    C medium(int column);
    C large(int column);
    C xLarge(int column);
    C xxLarge(int column);

    default C mobile() {
        small(1)
        return medium(2);
    }
}
