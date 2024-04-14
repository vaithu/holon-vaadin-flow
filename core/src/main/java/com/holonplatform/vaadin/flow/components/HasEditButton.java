package com.holonplatform.vaadin.flow.components;

public interface HasEditButton<C extends HasEditButton<C>> {
    C editButton(Runnable runnable);

    C visible(boolean visible);

}
