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

import com.holonplatform.vaadin.flow.components.builders.EmptyBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;

/**
 * Default {@link EmptyBuilder} implementation.
 *
 * <p>Instantiates an {@link Empty} component and delegates all configuration to
 * {@link AbstractEmptyConfigurator}. Returned by {@link EmptyBuilder#create()}.</p>
 */
public class DefaultEmptyBuilder
        extends AbstractEmptyConfigurator<EmptyBuilder>
        implements EmptyBuilder {

    /**
     * Constructor — creates a new {@link Empty} instance.
     */
    public DefaultEmptyBuilder() {
        super(new Empty());
    }

    @Override
    protected EmptyBuilder getConfigurator() {
        return this;
    }

    @Override
    public Empty build() {
        return getComponent();
    }
}

