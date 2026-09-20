package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.HeroStripConfigurator;
import com.iyensoft.vaadin.flow.components.HeroStrip;

/**
 * Default {@link HeroStripConfigurator.BaseHeroStripConfigurator} implementation.
 */
public class DefaultHeroStripConfigurator
        extends AbstractHeroStripConfigurator<HeroStripConfigurator.BaseHeroStripConfigurator>
        implements HeroStripConfigurator.BaseHeroStripConfigurator {

    public DefaultHeroStripConfigurator(HeroStrip strip) {
        super(strip);
    }

    @Override
    protected HeroStripConfigurator.BaseHeroStripConfigurator getConfigurator() {
        return this;
    }
}
