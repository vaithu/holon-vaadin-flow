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

import com.holonplatform.vaadin.flow.components.builders.AlertDialogBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;

/**
 * Default {@link AlertDialogBuilder} implementation.
 *
 * <p>Instantiates an {@link AlertDialog} with the specified {@link Alert.Variant} and
 * delegates all configuration to {@link AbstractAlertDialogConfigurator}.</p>
 */
public class DefaultAlertDialogBuilder
        extends AbstractAlertDialogConfigurator<AlertDialogBuilder>
        implements AlertDialogBuilder {

    public DefaultAlertDialogBuilder() {
        super(new AlertDialog());
    }

    @Override
    protected AlertDialogBuilder getConfigurator() {
        return this;
    }

    @Override
    public AlertDialog build() {
        applyPostProcessors();
        return getComponent();
    }
}

