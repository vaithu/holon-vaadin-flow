package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.HeroStripBuilder;
import com.iyensoft.vaadin.flow.components.HeroStrip;

/**
 * Default {@link HeroStripBuilder} implementation.
 */
public class DefaultHeroStripBuilder
        extends AbstractHeroStripConfigurator<HeroStripBuilder>
        implements HeroStripBuilder {

    public DefaultHeroStripBuilder() {
        super(new HeroStrip(HeroStrip.Variant.DEFAULT));
    }

    public DefaultHeroStripBuilder(HeroStrip.Variant variant) {
        super(new HeroStrip(variant != null ? variant : HeroStrip.Variant.DEFAULT));
    }

    @Override
    protected HeroStripBuilder getConfigurator() {
        return this;
    }

    @Override
    public HeroStrip build() {
        applyPostProcessors();
        return getComponent();
    }
}
