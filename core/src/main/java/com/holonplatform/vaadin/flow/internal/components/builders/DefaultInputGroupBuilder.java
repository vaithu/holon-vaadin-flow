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

import com.holonplatform.vaadin.flow.components.builders.InputGroupBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;

/**
 * Default {@link InputGroupBuilder} implementation.
 */
public class DefaultInputGroupBuilder
        extends AbstractInputGroupLayoutConfigurator<InputGroupBuilder>
        implements InputGroupBuilder {

    public DefaultInputGroupBuilder() {
        super(new InputGroup());
    }

    @Override
    public InputGroup build() {
        return getComponent();
    }

    @Override
    protected InputGroupBuilder getConfigurator() {
        return this;
    }
}

