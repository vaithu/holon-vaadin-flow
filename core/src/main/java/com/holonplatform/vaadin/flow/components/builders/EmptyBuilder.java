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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultEmptyBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;

/**
 * Builder to create and configure {@link Empty} empty-state components.
 *
 * <p>Extends {@link EmptyConfigurator} for all Empty-specific configuration methods and
 * {@link ComponentBuilder} for the terminal {@link #build()} method.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * Empty empty = Empty.builder()
 *     .icon(new Icon(VaadinIcon.INBOX))
 *     .title("No results found")
 *     .description("Try adjusting your search or filter to find what you're looking for.")
 *     .action(new Button("Clear filters"))
 *     .build();
 * }</pre>
 *
 * <p>Instances are obtained via {@link #create()} or the convenience shortcut {@link Empty#builder()}.</p>
 *
 * @see EmptyConfigurator
 * @see Empty
 */
public interface EmptyBuilder extends EmptyConfigurator<EmptyBuilder>, ComponentBuilder<Empty, EmptyBuilder> {

    /**
     * Create a new {@link EmptyBuilder}.
     *
     * @return a new {@link EmptyBuilder}
     */
    static EmptyBuilder create() {
        return new DefaultEmptyBuilder();
    }
}

