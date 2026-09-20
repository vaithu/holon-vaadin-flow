package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.MaterialHeader.Variant;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.holonplatform.vaadin.flow.components.support.ViewMode;

/** Fluent configuration for Material 3 content header slots and actions. */
public interface MaterialHeaderConfigurator<C extends MaterialHeaderConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C variant(Variant variant);

    C headline(Component headline);

    C headline(String headline);

    C subtitle(Component subtitle);

    C breadcrumb(Component breadcrumb);

    C breadcrumb(Breadcrumb breadcrumb);

    C breadcrumb(ListItem... items);

    C media(Component media);

    C details(Component... components);

    C tags(Component... components);

    C leading(Component... components);

    C actions(Component... components);

    C primaryAction(Component component);

    C secondaryAction(Component... components);

    C overflowAction(String label, Runnable action);

    C responsiveAction(Component desktopComponent, String label, Runnable action);

    C viewMode(ViewMode viewMode);

    C responsiveAction(ViewMode viewMode, Component desktopComponent, String label, Runnable action);
}
