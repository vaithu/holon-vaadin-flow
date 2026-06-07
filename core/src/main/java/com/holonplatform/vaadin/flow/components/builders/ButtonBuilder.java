/*
 * Copyright 2016-2017 Axioma srl.
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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultButtonBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultDeleteButtonBuilder;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * Builder to create {@link Button} components.
 *
 * @since 5.2.0
 */
public interface ButtonBuilder extends ButtonConfigurator<ButtonBuilder>, ComponentBuilder<Button, ButtonBuilder> {

    /**
     * Create a new {@link ButtonBuilder}.
     *
     * @return A new {@link ButtonBuilder} instance
     */
    static ButtonBuilder create() {
        return new DefaultButtonBuilder();
    }

    static ButtonBuilder createDelBtn() {
        return new DefaultDeleteButtonBuilder();
    }

    default ButtonBuilder newButton() {
        return icon(LumoIcon.PLUS.create())
                .text("New", "new.code")
                .withFocusShortcutKey(Key.KEY_N, KeyModifier.CONTROL)
                .tooltip("Create New Record", "content.new.code")
                .primary();
    }

    default ButtonBuilder newButton(String text) {
        return newButton().text(text);
    }

    default ButtonBuilder edit() {
        return icon(LumoIcon.EDIT.create())
                .text("Edit", "edit.code")
                .iconAfterText(false)
                .tooltip("Edit Record", "content.edit.code")
                .tertiaryInline();
    }

    default ButtonBuilder edit(String text) {
        return edit().text(text);
    }

    default ButtonBuilder duplicate() {
        return icon(VaadinIcon.COPY)
                .text("Duplicate", "duplicate.code")
                .tooltip("Create Duplicate Record", "content.duplicate.code")
                .tertiaryInline();
    }

    default ButtonBuilder duplicate(String text) {
        return duplicate().text(text);
    }

    default ButtonBuilder export() {
        return icon(LumoIcon.DOWNLOAD.create())
                .text("Export", "export.code")
                .tooltip("Export Records", "content.export.code")
                .tertiaryInline();
    }

    default ButtonBuilder export(String text) {
        return export().text(text);
    }

    default ButtonBuilder refresh() {
        return icon(LumoIcon.RELOAD.create())
                .text("Refresh", "refresh.code")
                .tooltip("Refresh Records", "content.refresh.code")
                .tertiaryInline();
    }

    default ButtonBuilder refresh(String text) {
        return refresh().text(text);
    }

    default ButtonBuilder delete() {
        return createDelBtn();
    }

}
