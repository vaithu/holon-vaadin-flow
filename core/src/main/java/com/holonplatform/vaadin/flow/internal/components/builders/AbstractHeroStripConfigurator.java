package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HeroStripConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.HeroStrip;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Base {@link HeroStripConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractHeroStripConfigurator<C extends HeroStripConfigurator<C>>
        extends AbstractComponentConfigurator<HeroStrip, C>
        implements HeroStripConfigurator<C> {

    public AbstractHeroStripConfigurator(HeroStrip component) {
        super(component);
    }

    @Override
    public C variant(HeroStrip.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C responsive() {
        getComponent().setResponsive(true);
        return getConfigurator();
    }

    @Override
    public C responsive(boolean responsive) {
        getComponent().setResponsive(responsive);
        return getConfigurator();
    }

    @Override
    public C wideFirstColumn() {
        getComponent().setWideFirstCell(true);
        return getConfigurator();
    }

    @Override
    public C wideFirstColumn(boolean wideFirstColumn) {
        getComponent().setWideFirstCell(wideFirstColumn);
        return getConfigurator();
    }

    @Override
    public C header(HeroStrip.Header header) {
        getComponent().setHeader(header);
        return getConfigurator();
    }

    @Override
    public C header(Consumer<HeroStripConfigurator.HeaderSection> configurator) {
        if (configurator != null) {
            final Component[] thumbIcon = {null};
            final String[] ribbon = {null};
            final String[] name = {null};
            final boolean[] starred = {false};
            final String[] meta = {null};
            configurator.accept(new HeroStripConfigurator.HeaderSection() {
                @Override public HeroStripConfigurator.HeaderSection thumbIcon(Component icon) { thumbIcon[0] = icon; return this; }
                @Override public HeroStripConfigurator.HeaderSection ribbon(String r) { ribbon[0] = r; return this; }
                @Override public HeroStripConfigurator.HeaderSection name(String n) { name[0] = n; return this; }
                @Override public HeroStripConfigurator.HeaderSection starred(boolean s) { starred[0] = s; return this; }
                @Override public HeroStripConfigurator.HeaderSection meta(String m) { meta[0] = m; return this; }
            });
            getComponent().setHeader(new HeroStrip.Header(thumbIcon[0], ribbon[0], name[0], starred[0], meta[0]));
        }
        return getConfigurator();
    }

    @Override
    public C tag(HeroStrip.Tag tag) {
        getComponent().addTag(tag);
        return getConfigurator();
    }

    @Override
    public C tag(String text, HeroStrip.TagVariant variant) {
        getComponent().addTag(new HeroStrip.Tag(text, variant));
        return getConfigurator();
    }

    @Override
    public C tags(List<HeroStrip.Tag> tags) {
        getComponent().setTags(tags);
        return getConfigurator();
    }

    @Override
    public C cell(HeroStrip.Cell cell) {
        getComponent().addCell(cell);
        return getConfigurator();
    }

    @Override
    public C cells(List<HeroStrip.Cell> cells) {
        getComponent().setCells(cells);
        return getConfigurator();
    }

    @Override
    public C cell(Consumer<HeroStripConfigurator.CellSection> configurator) {
        if (configurator != null) {
            final String[] header  = {null};
            final String[] content = {null};
            final String[] footer  = {null};
            final boolean[] pulse  = {false};
            final HeroStrip.ValueVariant[] valueVariant = {HeroStrip.ValueVariant.DEFAULT};
            configurator.accept(new HeroStripConfigurator.CellSection() {
                @Override public HeroStripConfigurator.CellSection header(String h)  { header[0]  = h; return this; }
                @Override public HeroStripConfigurator.CellSection content(String c) { content[0] = c; return this; }
                @Override public HeroStripConfigurator.CellSection footer(String f)  { footer[0]  = f; return this; }
                @Override public HeroStripConfigurator.CellSection pulse(boolean p)  { pulse[0]   = p; return this; }
                @Override public HeroStripConfigurator.CellSection valueVariant(HeroStrip.ValueVariant vv) {
                    valueVariant[0] = vv != null ? vv : HeroStrip.ValueVariant.DEFAULT;
                    return this;
                }
            });
            getComponent().addCell(new HeroStrip.Cell(header[0], content[0], footer[0], pulse[0], valueVariant[0]));
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
