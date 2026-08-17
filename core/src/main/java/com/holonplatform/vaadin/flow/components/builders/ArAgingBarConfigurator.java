package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultArAgingBarConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar.Segment;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar.Variant;

import java.util.List;
import java.util.function.Consumer;

/**
 * Configurator for {@link ArAgingBar} components.
 *
 * @param <C> Concrete configurator type
 */
public interface ArAgingBarConfigurator<C extends ArAgingBarConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C icon(String icon);
    C title(String title);
    C variant(Variant variant);
    C segment(Segment segment);
    C segments(List<Segment> segments);
    C segment(String key, String value, double percent, Variant color);
    C leftStat(String text);
    C centerStat(String text);
    C rightStat(String text);
    C header(Consumer<HeaderSection> configurator);
    C content(Consumer<ContentSection> configurator);
    C footer(Consumer<FooterSection> configurator);

    static BaseArAgingBarConfigurator configure(ArAgingBar bar) {
        return new DefaultArAgingBarConfigurator(bar);
    }

    interface BaseArAgingBarConfigurator extends ArAgingBarConfigurator<BaseArAgingBarConfigurator> {}

    interface HeaderSection {
        HeaderSection icon(String icon);
        HeaderSection title(String title);
        HeaderSection variant(Variant variant);
    }

    interface ContentSection {
        ContentSection segment(Segment segment);
        ContentSection segment(String key, String value, double percent, Variant color);
        ContentSection segment(Consumer<SegmentSection> configurator);
    }

    interface FooterSection {
        FooterSection left(String text);
        FooterSection center(String text);
        FooterSection right(String text);
    }

    interface SegmentSection {
        SegmentSection key(String key);
        SegmentSection value(String value);
        SegmentSection percent(double percent);
        SegmentSection variant(Variant color);
    }
}
