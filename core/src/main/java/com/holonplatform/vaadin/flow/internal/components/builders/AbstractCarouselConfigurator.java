/*
 * Copyright 2016-2024 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.builders.CarouselConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Carousel;
import com.holonplatform.vaadin.flow.vaadinplus.components.CarouselItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.shared.Registration;

import java.util.Optional;

/**
 * Base {@link CarouselConfigurator} implementation.
 *
 * <p>Extends {@link AbstractComponentConfigurator} to inherit the standard Holon Platform
 * component lifecycle hooks ({@code id}, {@code visible}, {@code styleName}, {@code width}, etc.)
 * and provides Carousel-specific configuration logic.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractCarouselConfigurator<C extends CarouselConfigurator<C>>
        extends AbstractComponentConfigurator<Carousel, C>
        implements CarouselConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component the {@link Carousel} instance to configure (not null)
     */
    public AbstractCarouselConfigurator(Carousel component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // CarouselConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C orientation(Carousel.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C loop(boolean loop) {
        getComponent().setLoop(loop);
        return getConfigurator();
    }

    @Override
    public C addItem(Component... items) {
        getComponent().addItem(items);
        return getConfigurator();
    }

    @Override
    public C addItem(CarouselItem item) {
        getComponent().getContent().add(item);
        return getConfigurator();
    }

    @Override
    public Registration withSlideChangeListener(ComponentEventListener<Carousel.SlideChangeEvent> listener) {
        return getComponent().addSlideChangeListener(listener);
    }

    // -----------------------------------------------------------------------
    // AbstractComponentConfigurator hooks
    // -----------------------------------------------------------------------

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

