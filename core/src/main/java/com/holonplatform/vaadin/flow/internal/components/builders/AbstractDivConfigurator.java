/*
 * Copyright 2016-2018 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.DivConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.LayoutBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.lumo.ColumnSpan;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.internal.lumo.Gap;
import com.holonplatform.vaadin.flow.internal.lumo.RowSpan;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Consumer;


/**
 * Base {@link com.holonplatform.vaadin.flow.components.builders.DivConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 * @since 5.5.4
 */
public abstract class AbstractDivConfigurator<C extends DivConfigurator<C>>
        extends AbstractComponentConfigurator<Div, C> implements DivConfigurator<C> {

    private final String backgroundColor = com.holonplatform.vaadin.flow.internal.lumo.Background.CONTRAST_10.getClassName();
    private boolean isGrid;

    public AbstractDivConfigurator(Div component) {
        super(component);
    }

    /**
     * Creates a default horizontal separator for use inside a Div layout.
     */
    private Separator buildHorizontalSeparator(String... extraClasses) {
        Separator sep = new Separator();
        sep.setDecorative(true);
        if (extraClasses != null) {
            sep.addClassNames(extraClasses);
        }
        return sep;
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

    @Override
    public C responsive() {
        getComponent().addClassName("div--responsive");
        return getConfigurator();
    }

    @Override
    public C gridLayout(int columns) {
        makeItGrid();
        getComponent().addClassName("grid-cols-" + columns);
        return getConfigurator();
    }

    @Override
    public C gridLayout(String... styles) {
        makeItGrid();
        getComponent().addClassNames(styles);
        return getConfigurator();
    }

    @Override
    public C add(Component... components) {
        getComponent().add(components);
        return getConfigurator();
    }


    @Override
    public C addComponentAsFirst(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C addComponentAtIndex(int index, Component component) {
        getComponent().addComponentAtIndex(index, component);
        return getConfigurator();
    }

    @Override
    public C add(String text) {
        getComponent().add(text);
        return getConfigurator();
    }

    @Override
    public C title(LabelBuilder<H4> title) {
        getComponent().add(
                title
                        .build()
        );
        return getConfigurator();
    }

    @Override
    public C title(Component prefix, String title) {
        getComponent().add(
                LayoutBuilder.create().add(prefix).add(title).flexDirection(FlexDirection.ROW)
                        .gap(Gap.SMALL)
                        .build()
        );
        return getConfigurator();
    }

    @Override
    public C add(int columnSpan, Component... components) {
        makeItGrid();
        for (Component component : components) {
            component.addClassName("col-span-" + columnSpan);
        }
        add(components);
        return getConfigurator();
    }

    @Override
    public C add(ColumnSpan columnSpan, Component... components) {
        makeItGrid();
        for (Component component : components) {

            component.addClassName(columnSpan.getClassName());

        }
        add(components);
        return getConfigurator();
    }

    @Override
    public C add(RowSpan rowSpan, Component... components) {
        makeItGrid();
        for (Component component : components) {
            component.addClassName(rowSpan.getValue());
        }

        add(components);
        return getConfigurator();
    }


    @Override
    public C add(ColumnSpan columnSpan, RowSpan rowSpan, Component... components) {
        makeItGrid();
        for (Component component : components) {
            component.addClassNames(columnSpan.getClassName(), rowSpan.getValue());
        }
        add(components);
        return getConfigurator();
    }

    @Override
    public C add(int columnSpan, HasComponent... components) {
        makeItGrid();
        for (HasComponent component : components) {
            component.hasStyle().ifPresent(hasStyle -> hasStyle.addClassName("col-span-" + columnSpan));
            getComponent().add(component.getComponent());
            getComponent().addClassName("div--grid");
        }
        return getConfigurator();
    }

    private void makeItGrid() {
        if (!isGrid) {
            getComponent().addClassName("div--grid");
            isGrid = true;
        }
    }

    @Override
    public C title(Localizable title) {
        return title(LabelBuilder.h4().title(title));
    }

    @Override
    public C title(String title) {
        return title(UIUtils.createH4(title));
    }

    @Override
    public C add(String title, Component... components) {
        return title(UIUtils.createH4(title)).add(components);
    }

    @Override
    public C add(String title, HasComponent... components) {
        return title(UIUtils.createH4(title)).add(components);
    }

    @Override
    public C horizontalRule() {
        Separator sep = buildHorizontalSeparator();
        sep.addClassName(backgroundColor);
        getComponent().add(sep);
        return getConfigurator();
    }

    @Override
    public C horizontalRule(String... styles) {
        getComponent().add(buildHorizontalSeparator(styles));
        return getConfigurator();
    }

    @Override
    public C horizontalRule(int size, String... styles) {
        Separator sep = buildHorizontalSeparator(styles);
        // Honour the legacy size hint via a CSS custom property on the element attribute
        // so no inline style is set directly in Java (the property is picked up by separator.css)
        if (size > 0) {
            sep.getElement().setAttribute("style", "--separator-thickness:" + size + "px");
        }
        getComponent().add(sep);
        return getConfigurator();
    }

    @Override
    public C configure(Consumer<DivConfigurator<C>> configurator) {
        configurator.accept(this);
        return getConfigurator();
    }
}
