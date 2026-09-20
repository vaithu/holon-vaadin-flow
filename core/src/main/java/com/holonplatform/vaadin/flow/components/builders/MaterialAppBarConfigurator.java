package com.holonplatform.vaadin.flow.components.builders;

import com.iyensoft.vaadin.flow.components.MaterialAppBar.Variant;
import com.vaadin.flow.component.Component;
import com.holonplatform.vaadin.flow.components.support.ViewMode;

/** Fluent configuration for Material 3 app bar variants and content slots. */
public interface MaterialAppBarConfigurator<C extends MaterialAppBarConfigurator<C>>
    extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C variant(Variant variant);

    C headline(Component headline);

    default C headline(String headline) {
        return headline(headline == null ? null : new com.vaadin.flow.component.html.Span(headline));
    }

    C subtitle(Component subtitle);

    C leading(Component... components);

    C actions(Component... components);

    C overflowAction(String label, Runnable action);

    C responsiveAction(Component desktopComponent, String label, Runnable action);

    C viewMode(ViewMode viewMode);

    C responsiveAction(ViewMode viewMode, Component desktopComponent, String label, Runnable action);

    C centered(boolean centered);

    default C centered() {
        return centered(true);
    }

    C search(boolean search);

    default C search() {
        return search(true);
    }

    C scrolled(boolean scrolled);

    default C scrolled() {
        return scrolled(true);
    }
}