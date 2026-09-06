package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHeroStripConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.HeroStrip;
import com.vaadin.flow.component.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * Configurator for {@link HeroStrip} components.
 *
 * @param <C> Concrete configurator type
 */
public interface HeroStripConfigurator<C extends HeroStripConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C variant(HeroStrip.Variant variant);
    C responsive();
    C responsive(boolean responsive);
    C wideFirstColumn();
    C wideFirstColumn(boolean wideFirstColumn);

    C header(HeroStrip.Header header);
    C header(Consumer<HeaderSection> configurator);

    C tag(HeroStrip.Tag tag);
    C tag(String text, HeroStrip.TagVariant variant);
    C tags(List<HeroStrip.Tag> tags);

    C cell(HeroStrip.Cell cell);
    C cells(List<HeroStrip.Cell> cells);
    C cell(Consumer<CellSection> configurator);

    static BaseHeroStripConfigurator configure(HeroStrip strip) {
        return new DefaultHeroStripConfigurator(strip);
    }

    interface BaseHeroStripConfigurator extends HeroStripConfigurator<BaseHeroStripConfigurator> {}

    interface CellSection {
        CellSection header(String header);
        CellSection content(String content);
        CellSection footer(String footer);
        CellSection pulse(boolean pulse);
        CellSection valueVariant(HeroStrip.ValueVariant valueVariant);
        CellSection icon(Component icon);
    }

    interface HeaderSection {
        HeaderSection thumbIcon(Component icon);
        HeaderSection ribbon(String ribbon);
        HeaderSection name(String name);
        HeaderSection starred(boolean starred);
        HeaderSection meta(String meta);
    }
}

