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

/**
 * Default {@link CarouselConfigurator.BaseCarouselConfigurator} implementation.
 *
 * <p>Used by {@link CarouselConfigurator#configure(Carousel)} to configure an
 * already-existing {@link Carousel} instance without building a new one.</p>
 */
public class DefaultCarouselConfigurator
        extends AbstractCarouselConfigurator<CarouselConfigurator.BaseCarouselConfigurator>
        implements CarouselConfigurator.BaseCarouselConfigurator {

    /**
     * Constructor.
     *
     * @param carousel the existing {@link Carousel} component to configure (not null)
     */
    public DefaultCarouselConfigurator(Carousel carousel) {
        super(carousel);
    }

    @Override
    protected CarouselConfigurator.BaseCarouselConfigurator getConfigurator() {
        return this;
    }
}

