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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCarouselBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Carousel;

/**
 * Builder to create and configure {@link Carousel} components.
 *
 * <p>Extends {@link CarouselConfigurator} (for all Carousel-specific methods) and
 * {@link ComponentBuilder} (for the terminal {@link #build()} method).</p>
 *
 * <p>Usage:
 * <pre>{@code
 * Carousel carousel = Carousel.builder()
 *     .orientation(Carousel.Orientation.HORIZONTAL)
 *     .loop(true)
 *     .addItem(card1, card2, card3)
 *     .build();
 *
 * // Multi-item with custom basis
 * Carousel carousel = Carousel.builder()
 *     .addItem(item1)          // CarouselItem with setBasis already set
 *     .addItem(item2)
 *     .build();
 * }</pre>
 *
 * <p>Instances are obtained via the static factory method {@link #create()} and
 * {@link #create(Carousel.Orientation)}, or via the convenience shortcut
 * {@link Carousel#builder()}.</p>
 *
 * @see CarouselConfigurator
 * @see Carousel
 */
public interface CarouselBuilder
        extends CarouselConfigurator<CarouselBuilder>, ComponentBuilder<Carousel, CarouselBuilder> {

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a new {@link CarouselBuilder} for a horizontal {@link Carousel}.
     *
     * @return a new {@link CarouselBuilder}
     */
    static CarouselBuilder create() {
        return new DefaultCarouselBuilder(Carousel.Orientation.HORIZONTAL);
    }

    /**
     * Create a new {@link CarouselBuilder} with the given orientation.
     *
     * @param orientation the scroll axis (not null)
     * @return a new {@link CarouselBuilder}
     */
    static CarouselBuilder create(Carousel.Orientation orientation) {
        return new DefaultCarouselBuilder(orientation);
    }
}

