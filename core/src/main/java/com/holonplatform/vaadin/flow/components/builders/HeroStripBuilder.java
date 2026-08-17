package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHeroStripBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.HeroStrip;

/**
 * Builder to create and configure {@link HeroStrip} components.
 */
public interface HeroStripBuilder
        extends HeroStripConfigurator<HeroStripBuilder>, ComponentBuilder<HeroStrip, HeroStripBuilder> {

    static HeroStripBuilder create() {
        return new DefaultHeroStripBuilder();
    }

    static HeroStripBuilder create(HeroStrip.Variant variant) {
        return new DefaultHeroStripBuilder(variant);
    }
}
