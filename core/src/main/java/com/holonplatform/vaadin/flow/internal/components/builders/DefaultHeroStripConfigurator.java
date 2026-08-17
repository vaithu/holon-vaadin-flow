package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HeroStripConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.HeroStrip;

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
