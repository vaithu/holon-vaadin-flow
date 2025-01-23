/*
 * Copyright 2022 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.holonplatform.vaadin.flow.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.PendingJavaScriptResult;

/**
 * Utility class for web browser related functionality.
 */
public final class WebBrowserTools {

    public static final String BEFORE_UNLOAD_LISTENER = "jmixBeforeUnloadListener";

    private WebBrowserTools() {
    }

    /**
     * Subscribes on window's beforeunload event to prevent browser tab closing.
     *
     * @param ui ui object that calls JavaScript function
     * @return a pending result from a JavaScript snippet sent to the browser for evaluation.
     */
    public static PendingJavaScriptResult preventBrowserTabClosing(UI ui) {
        return ui.getElement().executeJs(
                "window.addEventListener('beforeunload', " + BEFORE_UNLOAD_LISTENER + ", {capture: true})"
        );
    }

    /**
     * Removes window's beforeunload event listener that prevents browser tab closing.
     *
     * @param ui ui object that calls JavaScript function
     * @return a pending result from a JavaScript snippet sent to the browser for evaluation.
     */
    public static PendingJavaScriptResult allowBrowserTabClosing(UI ui) {
        return ui.getElement().executeJs(
                "window.removeEventListener('beforeunload', " + BEFORE_UNLOAD_LISTENER + ", {capture: true})"
        );
    }
}