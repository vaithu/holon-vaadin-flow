package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.CardVariant;

public interface CardConfigurator<C extends CardConfigurator<C>> extends
        ComponentConfigurator<C>,
        HasComponentsConfigurator<C>,
        HasElementConfigurator<C>, HasEnabledConfigurator<C>, HasStyleConfigurator<C>, HasSizeConfigurator<C>,
        HasThemeVariantConfigurator<CardVariant, C> {

// ---------- Children content ----------

    C add(java.util.Collection<Component> components);

    // ---------- Footer ----------

    C addToFooter(Component... footerComponent);

    // ---------- Read-only accessors ----------

    java.util.Optional<String> getAriaRole();

    java.util.stream.Stream<Component> getChildren();

    Component[] getFooterComponents();

    Component getHeader();

    Component getHeaderPrefix();

    Component getHeaderSuffix();

    Component getMedia();

    Component getSubtitle();

    Component getTitle();

    String getTitleAsText();

    // ---------- Fluent mutation methods (no 'set', return C) ----------

    C ariaRole(String role);

    C header(Component header);

    C headerPrefix(Component headerPrefix);

    C headerSuffix(Component headerSuffix);

    C media(Component media);

    C subtitle(Component subtitle);

    C title(LabelBuilder<?> title);

    void remove(java.util.Collection<Component> components);

    void removeAll();
}
