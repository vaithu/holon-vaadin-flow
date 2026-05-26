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
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;

/**
 * The scroll viewport inside a {@link Carousel}.
 *
 * <p>Renders as a {@code <div class="carousel__content">} with CSS
 * {@code scroll-snap-type} applied by the stylesheet. Each direct child
 * must be a {@link CarouselItem}.</p>
 *
 * <p>HTML output:</p>
 * <pre>
 * &lt;div class="carousel__content"&gt;
 *   &lt;div class="carousel__item"&gt;…&lt;/div&gt;
 *   &lt;div class="carousel__item"&gt;…&lt;/div&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * @see Carousel
 * @see CarouselItem
 */
public class CarouselContent extends Div implements HasStyle {

    private static final long serialVersionUID = 1L;

    private int itemCount = 0;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an empty carousel content viewport.
     */
    public CarouselContent() {
        addClassName("carousel__content");
        getElement().setAttribute("aria-live", "polite");
    }

    // -----------------------------------------------------------------------
    // Item API
    // -----------------------------------------------------------------------

    /**
     * Appends one or more {@link CarouselItem}s to this viewport.
     *
     * @param items items to add (not null)
     */
    public void add(CarouselItem... items) {
        for (CarouselItem item : items) {
            super.add(item);
            itemCount++;
        }
    }

    /**
     * Returns the number of {@link CarouselItem}s currently inside this viewport.
     *
     * @return item count ≥ 0
     */
    public int getItemCount() {
        return itemCount;
    }

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Applies the orientation-specific CSS modifier class.
     * Called internally by {@link Carousel#setOrientation}.
     *
     * @param orientation the new orientation (not null)
     */
    void setOrientation(Carousel.Orientation orientation) {
        if (orientation == Carousel.Orientation.VERTICAL) {
            addClassName("carousel__content--vertical");
        } else {
            removeClassName("carousel__content--vertical");
        }
    }
}

