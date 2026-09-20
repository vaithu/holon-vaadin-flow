package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultBaseCardConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;

public interface CardConfigurator<C extends CardConfigurator<C>> extends
        ComponentConfigurator<C>,
        HasComponentsConfigurator<C>,
        HasStyleConfigurator<C>, HasSizeConfigurator<C>,
        HasThemeVariantConfigurator<CardVariant, C> {

// ---------- Children content ----------

    C add(java.util.Collection<Component> components);

    // ---------- Footer ----------

    C withFooter(Component... footerComponents);

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

    // ── configure factory ────────────────────────────────────────────────────

    /**
     * Configure an existing {@link Card} instance using the fluent configurator API.
     *
     * @param card the Card instance to configure (not null)
     * @return a {@link BaseCardConfigurator}
     */
    static BaseCardConfigurator configure(Card card) {
        return new DefaultBaseCardConfigurator(card);
    }

    /**
     * Base (non-building) configurator for an existing {@link Card}.
     */
    interface BaseCardConfigurator extends CardConfigurator<BaseCardConfigurator> {
    }
}
