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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCarouselConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Carousel;
import com.holonplatform.vaadin.flow.vaadinplus.components.CarouselItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;

/**
 * Configurator for {@link Carousel} components.
 *
 * <p>Extends the standard Holon Platform {@link ComponentConfigurator},
 * {@link HasSizeConfigurator} and {@link HasStyleConfigurator} contracts,
 * adding Carousel-specific configuration methods.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see CarouselBuilder
 */
public interface CarouselConfigurator<C extends CarouselConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Sets the scroll orientation.
     *
     * @param orientation the orientation (not null)
     * @return this configurator (for chaining)
     */
    C orientation(Carousel.Orientation orientation);

    // -----------------------------------------------------------------------
    // Loop
    // -----------------------------------------------------------------------

    /**
     * Enables or disables wrap-around looping.
     * When disabled the Previous/Next buttons are hidden at the ends.
     *
     * @param loop {@code true} to enable looping
     * @return this configurator (for chaining)
     */
    C loop(boolean loop);

    // -----------------------------------------------------------------------
    // Items
    // -----------------------------------------------------------------------

    /**
     * Appends one or more slide content components, each wrapped in a {@link CarouselItem}.
     *
     * @param items slide content components (not null)
     * @return this configurator (for chaining)
     */
    C addItem(Component... items);

    /**
     * Appends a pre-built {@link CarouselItem} directly to the content viewport.
     * Use this overload when you need to control item-level properties such as
     * {@link CarouselItem#setBasis(String)}.
     *
     * @param item the pre-built carousel item (not null)
     * @return this configurator (for chaining)
     */
    C addItem(CarouselItem item);

    // -----------------------------------------------------------------------
    // Events
    // -----------------------------------------------------------------------

    /**
     * Registers a listener that is notified whenever the active slide changes.
     *
     * @param listener the listener (not null)
     * @return registration handle to remove the listener
     */
    Registration withSlideChangeListener(ComponentEventListener<Carousel.SlideChangeEvent> listener);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a new {@link BaseCarouselConfigurator} to configure an existing {@link Carousel}.
     *
     * @param carousel the carousel component to configure (not null)
     * @return a new {@link BaseCarouselConfigurator}
     */
    static BaseCarouselConfigurator configure(Carousel carousel) {
        return new DefaultCarouselConfigurator(carousel);
    }

    // -----------------------------------------------------------------------
    // Base configurator
    // -----------------------------------------------------------------------

    /**
     * Base (non-generic) {@link CarouselConfigurator}.
     */
    interface BaseCarouselConfigurator extends CarouselConfigurator<BaseCarouselConfigurator> {
    }
}

