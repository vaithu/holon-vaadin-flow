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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAlertBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;

/**
 * Builder to create and configure {@link Alert} components.
 *
 * <p>Extends {@link AlertConfigurator} (for all Alert-specific methods) and
 * {@link ComponentBuilder} (for the terminal {@link #build()} method).</p>
 *
 * <p>Usage:
 * <pre>{@code
 * Alert alert = Alert.builder(Alert.Variant.DESTRUCTIVE)
 *     .icon(new Icon(VaadinIcon.WARNING))
 *     .title(Localizable.builder()
 *             .message("Something went wrong")
 *             .messageCode("alert.title.error")
 *             .build())
 *     .description(Localizable.builder()
 *             .message("Your session expired. Please sign in again.")
 *             .messageCode("alert.desc.session-expired")
 *             .build())
 *     .action(new Button("Sign in"))
 *     .build();
 * }</pre>
 *
 * <p>Instances are obtained via the static factory methods {@link #create()} and
 * {@link #create(Alert.Variant)}, or via the convenience shortcut {@link Alert#builder()}.
 *
 * @see AlertConfigurator
 * @see Alert
 */
public interface AlertBuilder extends AlertConfigurator<AlertBuilder>, ComponentBuilder<Alert, AlertBuilder> {

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a new {@link AlertBuilder} for a {@link Alert.Variant#DEFAULT} alert.
     *
     * @return a new {@link AlertBuilder}
     */
    static AlertBuilder create() {
        return new DefaultAlertBuilder(Alert.Variant.DEFAULT);
    }

    /**
     * Create a new {@link AlertBuilder} for the given {@link Alert.Variant}.
     *
     * @param variant the visual variant (not null)
     * @return a new {@link AlertBuilder}
     */
    static AlertBuilder create(Alert.Variant variant) {
        return new DefaultAlertBuilder(variant);
    }
}

