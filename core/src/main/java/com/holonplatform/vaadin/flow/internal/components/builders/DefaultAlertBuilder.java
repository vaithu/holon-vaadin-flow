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

import com.holonplatform.vaadin.flow.components.builders.AlertBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;

/**
 * Default {@link AlertBuilder} implementation.
 *
 * <p>Instantiates an {@link Alert} with the specified {@link Alert.Variant} and delegates
 * all configuration to {@link AbstractAlertConfigurator}. Returned by
 * {@link AlertBuilder#create()} and {@link AlertBuilder#create(Alert.Variant)}.</p>
 */
public class DefaultAlertBuilder
        extends AbstractAlertConfigurator<AlertBuilder>
        implements AlertBuilder {

    /**
     * Constructor.
     *
     * @param variant the initial visual variant for the alert (not null)
     */
    public DefaultAlertBuilder(Alert.Variant variant) {
        super(new Alert(variant));
    }

    @Override
    protected AlertBuilder getConfigurator() {
        return this;
    }

    @Override
    public Alert build() {
        applyPostProcessors();
        return getComponent();
    }
}

