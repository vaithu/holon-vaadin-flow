package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ArAgingBarConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar.Segment;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar.Variant;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Base {@link ArAgingBarConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractArAgingBarConfigurator<C extends ArAgingBarConfigurator<C>>
        extends AbstractComponentConfigurator<ArAgingBar, C>
        implements ArAgingBarConfigurator<C> {

    public AbstractArAgingBarConfigurator(ArAgingBar component) {
        super(component);
    }

    @Override
    public C icon(String icon) {
        getComponent().setIcon(icon);
        return getConfigurator();
    }

    @Override
    public C title(String title) {
        getComponent().setBarTitle(title);
        return getConfigurator();
    }

    @Override
    public C variant(Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C segment(Segment segment) {
        getComponent().addSegment(segment);
        return getConfigurator();
    }

    @Override
    public C segments(List<Segment> segments) {
        getComponent().setSegments(segments);
        return getConfigurator();
    }

    @Override
    public C segment(String key, String value, double percent, Variant color) {
        getComponent().addSegment(new Segment(key, value, percent, color));
        return getConfigurator();
    }

    @Override
    public C leftStat(String text) {
        getComponent().setLeftStat(text);
        return getConfigurator();
    }

    @Override
    public C centerStat(String text) {
        getComponent().setCenterStat(text);
        return getConfigurator();
    }

    @Override
    public C rightStat(String text) {
        getComponent().setRightStat(text);
        return getConfigurator();
    }

    @Override
    public C header(Consumer<ArAgingBarConfigurator.HeaderSection> configurator) {
        if (configurator != null) {
            configurator.accept(new ArAgingBarConfigurator.HeaderSection() {
                @Override
                public ArAgingBarConfigurator.HeaderSection icon(String icon) {
                    getComponent().setIcon(icon);
                    return this;
                }
                @Override
                public ArAgingBarConfigurator.HeaderSection title(String title) {
                    getComponent().setBarTitle(title);
                    return this;
                }
                @Override
                public ArAgingBarConfigurator.HeaderSection variant(Variant variant) {
                    getComponent().setVariant(variant);
                    return this;
                }
            });
        }
        return getConfigurator();
    }

    @Override
    public C content(Consumer<ArAgingBarConfigurator.ContentSection> configurator) {
        if (configurator != null) {
            configurator.accept(new ArAgingBarConfigurator.ContentSection() {
                @Override
                public ArAgingBarConfigurator.ContentSection segment(Segment seg) {
                    getComponent().addSegment(seg);
                    return this;
                }
                @Override
                public ArAgingBarConfigurator.ContentSection segment(String key, String value, double percent, Variant color) {
                    getComponent().addSegment(new Segment(key, value, percent, color));
                    return this;
                }
                @Override
                public ArAgingBarConfigurator.ContentSection segment(Consumer<ArAgingBarConfigurator.SegmentSection> segConfigurator) {
                    if (segConfigurator != null) {
                        final String[] key   = {null};
                        final String[] value = {null};
                        final double[] pct   = {0};
                        final Variant[] color = {Variant.DEFAULT};
                        segConfigurator.accept(new ArAgingBarConfigurator.SegmentSection() {
                            @Override public ArAgingBarConfigurator.SegmentSection key(String k)     { key[0]   = k; return this; }
                            @Override public ArAgingBarConfigurator.SegmentSection value(String v)   { value[0] = v; return this; }
                            @Override public ArAgingBarConfigurator.SegmentSection percent(double p) { pct[0]   = p; return this; }
                            @Override public ArAgingBarConfigurator.SegmentSection variant(Variant c) {
                                color[0] = c != null ? c : Variant.DEFAULT;
                                return this;
                            }
                        });
                        getComponent().addSegment(new Segment(key[0], value[0], pct[0], color[0]));
                    }
                    return this;
                }
            });
        }
        return getConfigurator();
    }

    @Override
    public C footer(Consumer<ArAgingBarConfigurator.FooterSection> configurator) {
        if (configurator != null) {
            configurator.accept(new ArAgingBarConfigurator.FooterSection() {
                @Override
                public ArAgingBarConfigurator.FooterSection left(String text) {
                    getComponent().setLeftStat(text);
                    return this;
                }
                @Override
                public ArAgingBarConfigurator.FooterSection center(String text) {
                    getComponent().setCenterStat(text);
                    return this;
                }
                @Override
                public ArAgingBarConfigurator.FooterSection right(String text) {
                    getComponent().setRightStat(text);
                    return this;
                }
            });
        }
        return getConfigurator();
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
