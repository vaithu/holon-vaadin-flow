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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Action slot of an {@link Empty} component.
 *
 * <p>Holds buttons, links, or any interactive components displayed below the description
 * to give the user a primary call-to-action when a list or data set is empty.</p>
 *
 * @see Empty
 */
public class EmptyAction extends Div {

    /**
     * Creates an action slot pre-populated with the given components.
     *
     * @param components action components (buttons, anchors, etc.)
     */
    public EmptyAction(Component... components) {
        addClassName("empty__action");
        if (components != null) {
            add(components);
        }
    }
}

