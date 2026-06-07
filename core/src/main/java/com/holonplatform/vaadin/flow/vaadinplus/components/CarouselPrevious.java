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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * The "Previous slide" navigation button inside a {@link Carousel}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;div class="carousel__previous [carousel__button--disabled]"
 *      aria-label="Previous slide" role="button" tabindex="0"&gt;
 *   &lt;vaadin-icon class="carousel__button-icon" icon="vaadin:chevron-left"&gt;&lt;/vaadin-icon&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * @see Carousel
 * @see CarouselNext
 */
public class CarouselPrevious extends Div {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates the previous-slide button with a chevron-left icon.
     */
    public CarouselPrevious() {
        addClassName("carousel__previous");
        getElement().setAttribute("role", "button");
        getElement().setAttribute("aria-label", "Previous slide");
        getElement().setAttribute("tabindex", "0");

        Icon icon = new Icon(VaadinIcon.CHEVRON_LEFT);
        icon.addClassName("carousel__button-icon");
        add(icon);
    }

    // -----------------------------------------------------------------------
    // Disabled state
    // -----------------------------------------------------------------------

    /**
     * Toggles the disabled appearance and keyboard accessibility of this button.
     *
     * @param disabled {@code true} to disable
     */
    public void setDisabled(boolean disabled) {
        if (disabled) {
            addClassName("carousel__button--disabled");
            getElement().setAttribute("aria-disabled", "true");
            getElement().setAttribute("tabindex", "-1");
        } else {
            removeClassName("carousel__button--disabled");
            getElement().removeAttribute("aria-disabled");
            getElement().setAttribute("tabindex", "0");
        }
    }
}

