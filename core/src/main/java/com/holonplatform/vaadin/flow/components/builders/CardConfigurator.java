package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.util.Optional;

public interface CardConfigurator<C> {

    C title(String title);

    C content(Component... components);

    Optional<String> getTitle();

    C cardStyle(boolean enabled);

    default C withoutCardStyle() {
        return cardStyle(false);
    }

    default C cardStyle() {
        return cardStyle(true);
    }

    C gap();

    C withoutGap();

    Div build();

}
