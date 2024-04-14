package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.util.Arrays;

public interface CardGridConfigurator<C> extends GridSizeConfigurator<C> {

//    CardBuilder addCard();

    C addCard(CardBuilder card);

    C add();

    C add(String title, Component component);

    default C add(String title, HasComponent component) {
        return add(title, Arrays.stream(UIUtils.toComponents(new HasComponent[]{component})).findFirst().get());
    }



    String title(String titleText);

    Component content(Component component);

    C column(int column);

    C row(int row);

    C spanColumn(int column);

    C spanRow(int row);

    C gap();

    C withoutGap();



    Div build();
}
