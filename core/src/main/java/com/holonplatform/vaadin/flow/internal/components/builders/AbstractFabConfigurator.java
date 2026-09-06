/*
 * Copyright 2016-2026 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.builders.FabConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;

/**
 * Base {@link FabConfigurator} implementation.
 * <p>
 * Extends {@link AbstractButtonConfigurator} to reuse all the standard button configuration
 * behavior (icon, text, click listeners, theme variants, focus, tooltip, ...), keeping a typed
 * {@link Fab} reference alongside for the FAB-specific size / color / extended / lowered /
 * position API, since {@link AbstractButtonConfigurator} is hard-typed to {@link com.vaadin.flow.component.button.Button}.
 * </p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractFabConfigurator<C extends FabConfigurator<C>>
        extends AbstractButtonConfigurator<C> implements FabConfigurator<C> {

    private final Fab fab;

    public AbstractFabConfigurator(Fab component) {
        super(component);
        this.fab = component;
    }

    /**
     * Get the concrete {@link Fab} component instance.
     *
     * @return the FAB instance
     */
    protected Fab getFab() {
        return fab;
    }

    @Override
    public C size(Fab.Size size) {
        fab.setFabSize(size);
        return getConfigurator();
    }

    @Override
    public C color(Fab.Color color) {
        fab.setFabColor(color);
        return getConfigurator();
    }

    @Override
    public C extended(String label) {
        fab.extended(label);
        return getConfigurator();
    }

    @Override
    public C extended(boolean extended) {
        fab.setExtended(extended);
        return getConfigurator();
    }

    @Override
    public C lowered(boolean lowered) {
        fab.setLowered(lowered);
        return getConfigurator();
    }

    @Override
    public C position(Fab.Position position) {
        fab.setFabPosition(position);
        return getConfigurator();
    }
}

