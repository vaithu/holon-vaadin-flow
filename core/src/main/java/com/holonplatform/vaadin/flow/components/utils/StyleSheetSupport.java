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
package com.holonplatform.vaadin.flow.components.utils;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;

/**
 * Runtime equivalent of the {@link StyleSheet} annotation, for code that cannot carry one.
 *
 * <p>{@code @StyleSheet} is only honoured on {@link Component} classes. Builders, configurators
 * and static utility classes apply CSS class names to components they do not own, so the
 * stylesheet that defines those class names would never be requested — the markup would render
 * with the right class names but without any styling. Calling
 * {@link #require(Component, String...)} from such code registers the stylesheet against the
 * {@link UI} the target component is attached to, giving the same end result as the annotation.</p>
 *
 * <p>Registrations are de-duplicated per {@link UI}, so it is safe (and expected) to call this for
 * every component instance that needs a given stylesheet.</p>
 *
 * @since 12.0.1
 */
public final class StyleSheetSupport {

    private static final String LOADED_KEY = StyleSheetSupport.class.getName() + ".loaded";

    private StyleSheetSupport() {
    }

    /**
     * Ensures the given context stylesheets are loaded in the {@link UI} the target component
     * belongs to, both now (if already attached) and on every subsequent attach.
     *
     * @param target        the component whose UI must load the stylesheets, ignored if {@code null}
     * @param cssFileNames  context-relative file names, e.g. {@code "buttons.css"}
     */
    public static void require(Component target, String... cssFileNames) {
        if (target == null || cssFileNames == null || cssFileNames.length == 0) {
            return;
        }
        target.getUI().ifPresent(ui -> load(ui, cssFileNames));
        // AttachEvent#getUI() unwraps an Optional and throws when the component tree the
        // listener fired for is not attached to a UI (notably in unit tests), so resolve the
        // UI defensively from the event source instead.
        target.addAttachListener(event -> event.getSource().getUI()
                .ifPresent(ui -> load(ui, cssFileNames)));
    }

    /**
     * Ensures the given context stylesheets are loaded in the current {@link UI}.
     * <p>Prefer {@link #require(Component, String...)} whenever a component is available: this
     * variant is a no-op when invoked outside a UI context.</p>
     *
     * @param cssFileNames context-relative file names, e.g. {@code "utilities.css"}
     */
    public static void require(String... cssFileNames) {
        final UI ui = UI.getCurrent();
        if (ui != null) {
            load(ui, cssFileNames);
        }
    }

    @SuppressWarnings("unchecked")
    private static void load(UI ui, String[] cssFileNames) {
        if (ui == null) {
            return;
        }
        Set<String> loaded = (Set<String>) ComponentUtil.getData(ui, LOADED_KEY);
        if (loaded == null) {
            loaded = new HashSet<>();
            ComponentUtil.setData(ui, LOADED_KEY, loaded);
        }
        for (String css : cssFileNames) {
            if (css != null && !css.isEmpty() && loaded.add(css)) {
                ui.getPage().addStyleSheet("context://" + css);
            }
        }
    }

}
