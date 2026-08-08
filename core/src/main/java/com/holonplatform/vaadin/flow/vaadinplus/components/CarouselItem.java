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

import java.io.Serial;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * An individual slide inside a {@link CarouselContent}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;div class="carousel__item" role="group" aria-roledescription="slide"&gt;
 *   â€¦ user content â€¦
 * &lt;/div&gt;
 * </pre>
 *
 * <p>By default each item occupies 100 % of the viewport width (horizontal)
 * or height (vertical). Use {@link #setBasis(String)} to create multi-item
 * views â€” e.g. {@code setBasis("33.333%")} shows three slides at once.</p>
 *
 * @see Carousel
 * @see CarouselContent
 */
public class CarouselItem extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty slide.
     */
    public CarouselItem() {
        init();
    }

    /**
     * Creates a slide pre-populated with the given component.
     *
     * @param content the component to display inside the slide (not null)
     */
    public CarouselItem(Component content) {
        init();
        add(content);
    }

    // -----------------------------------------------------------------------
    // Basis API
    // -----------------------------------------------------------------------

    /**
     * Overrides the slide's {@code flex-basis} via the
     * {@code --carousel-item-basis} CSS custom property, enabling multi-item views.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code "100%"} â€“ one slide at a time (default)</li>
     *   <li>{@code "50%"} â€“ two slides at a time</li>
     *   <li>{@code "33.333%"} â€“ three slides at a time</li>
     * </ul>
     *
     * @param basis a valid CSS length or percentage string (not null)
     */
    public void setBasis(String basis) {
        getElement().getStyle().set("--carousel-item-basis", basis);
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    private void init() {
        addClassName("carousel__item");
        getElement().setAttribute("role", "group");
        getElement().setAttribute("aria-roledescription", "slide");
    }
}
