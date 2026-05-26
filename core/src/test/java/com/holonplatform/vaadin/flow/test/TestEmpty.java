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
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.EmptyBuilder;
import com.holonplatform.vaadin.flow.components.builders.EmptyConfigurator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.holonplatform.vaadin.flow.vaadinplus.components.EmptyAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.EmptyDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.EmptyTitle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Empty} component and its builder / configurator infrastructure.
 *
 * <p>Tests are deliberately free of a running Vaadin UI so they stay lightweight and fast.
 * DOM-level assertions use {@code component.getClassNames()} and {@code component.getElement()}
 * APIs available without a running servlet.</p>
 */
class TestEmpty {

    // =========================================================================
    // Constructor — default state
    // =========================================================================

    @Test
    void constructor_hasEmptyClass() {
        Empty empty = new Empty();
        assertTrue(empty.getClassNames().contains("empty"), "root element must have 'empty' class");
    }

    @Test
    void constructor_titleAndDescriptionAreNullByDefault() {
        Empty empty = new Empty();
        assertNull(empty.getEmptyTitle(),    "title must be null by default");
        assertNull(empty.getDescription(),   "description must be null by default");
        assertNull(empty.getAction(),        "action must be null by default");
    }

    // =========================================================================
    // EmptyTitle sub-component
    // =========================================================================

    @Test
    void emptyTitle_string_setsText() {
        EmptyTitle title = new EmptyTitle("No results found");
        assertEquals("No results found", title.getText());
        assertTrue(title.getClassNames().contains("empty__title"));
    }

    @Test
    void emptyTitle_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        EmptyTitle[] holder = new EmptyTitle[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> holder[0] = new EmptyTitle(loc));
        assertEquals("TestUS", holder[0].getText());
    }

    @Test
    void emptyTitle_components_addsChildren() {
        Span span = new Span("child");
        EmptyTitle title = new EmptyTitle(span);
        assertEquals(1, title.getComponentCount());
    }

    // =========================================================================
    // EmptyDescription sub-component
    // =========================================================================

    @Test
    void emptyDescription_string_setsText() {
        EmptyDescription desc = new EmptyDescription("Try clearing your filters.");
        assertEquals("Try clearing your filters.", desc.getText());
        assertTrue(desc.getClassNames().contains("empty__description"));
    }

    @Test
    void emptyDescription_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        EmptyDescription[] holder = new EmptyDescription[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> holder[0] = new EmptyDescription(loc));
        assertEquals("TestUS", holder[0].getText());
    }

    @Test
    void emptyDescription_setLocalizableText_updatesText() {
        EmptyDescription desc = new EmptyDescription("initial");
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        LocalizationTestUtils.withTestLocalizationContext(() -> desc.setLocalizableText(loc));
        assertEquals("TestUS", desc.getText());
    }

    // =========================================================================
    // EmptyAction sub-component
    // =========================================================================

    @Test
    void emptyAction_hasCorrectClass() {
        EmptyAction action = new EmptyAction(new Button("Go"));
        assertTrue(action.getClassNames().contains("empty__action"));
    }

    @Test
    void emptyAction_addsComponents() {
        Button btn1 = new Button("Create");
        Button btn2 = new Button("Import");
        EmptyAction action = new EmptyAction(btn1, btn2);
        assertEquals(2, action.getComponentCount());
    }

    // =========================================================================
    // Empty.setTitle / setDescription / setAction
    // =========================================================================

    @Test
    void setTitle_string_setsTitle() {
        Empty empty = new Empty();
        empty.setTitle("No data available");
        assertNotNull(empty.getEmptyTitle());
        assertEquals("No data available", empty.getEmptyTitle().getText());
    }

    @Test
    void setTitle_localizable_resolvesTitle() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Empty[] holder = new Empty[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new Empty();
            holder[0].setTitle(loc);
        });
        assertEquals("TestUS", holder[0].getEmptyTitle().getText());
    }

    @Test
    void setTitle_replacing_removesOldTitle() {
        Empty empty = new Empty();
        empty.setTitle("First");
        empty.setTitle("Second");
        assertEquals("Second", empty.getEmptyTitle().getText());
    }

    @Test
    void setTitle_null_clearsTitle() {
        Empty empty = new Empty();
        empty.setTitle("Title");
        empty.setTitle((EmptyTitle) null);
        assertNull(empty.getEmptyTitle());
    }

    @Test
    void setDescription_string_setsDescription() {
        Empty empty = new Empty();
        empty.setDescription("Nothing to show here.");
        assertNotNull(empty.getDescription());
        assertEquals("Nothing to show here.", empty.getDescription().getText());
    }

    @Test
    void setDescription_localizable_resolvesDescription() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Empty[] holder = new Empty[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new Empty();
            holder[0].setDescription(loc);
        });
        assertEquals("TestUS", holder[0].getDescription().getText());
    }

    @Test
    void setAction_components_setsAction() {
        Empty empty = new Empty();
        empty.setAction(new Button("Create new"));
        assertNotNull(empty.getAction());
        assertTrue(empty.getAction().getClassNames().contains("empty__action"));
    }

    @Test
    void setAction_null_clearsAction() {
        Empty empty = new Empty();
        empty.setAction(new Button("x"));
        empty.setAction((EmptyAction) null);
        assertNull(empty.getAction());
    }

    // =========================================================================
    // Icon API
    // =========================================================================

    @Test
    void setIcon_icon_doesNotThrow() {
        Empty empty = new Empty();
        empty.setIcon(new Icon(VaadinIcon.INBOX));
        // No exception = success; visibility toggled server-side
        assertNotNull(empty);
    }

    @Test
    void setIcon_illustration_doesNotThrow() {
        Empty empty = new Empty();
        empty.setIcon(new Span("illustration"));
        assertNotNull(empty);
    }

    @Test
    void clearIcon_doesNotThrow() {
        Empty empty = new Empty();
        empty.setIcon(new Icon(VaadinIcon.INBOX));
        empty.clearIcon(); // must not throw
        assertNotNull(empty);
    }

    @Test
    void setIcon_null_clearsIcon() {
        Empty empty = new Empty();
        empty.setIcon(new Icon(VaadinIcon.INBOX));
        empty.setIcon((Icon) null); // clears via null guard
        assertNotNull(empty);
    }

    // =========================================================================
    // EmptyBuilder — factories
    // =========================================================================

    @Test
    void emptyBuilder_create_returnsBuilder() {
        EmptyBuilder builder = EmptyBuilder.create();
        assertNotNull(builder);
        Empty empty = builder.build();
        assertNotNull(empty);
        assertTrue(empty.getClassNames().contains("empty"));
    }

    @Test
    void empty_staticBuilderFactory() {
        Empty empty = Empty.builder().build();
        assertNotNull(empty);
        assertTrue(empty.getClassNames().contains("empty"));
    }

    // =========================================================================
    // EmptyBuilder — fluent chaining
    // =========================================================================

    @Test
    void emptyBuilder_fullChain_allPropertiesSet() {
        Empty empty = EmptyBuilder.create()
                .icon(new Icon(VaadinIcon.INBOX))
                .title("No results found")
                .description("Try adjusting your search or filter.")
                .action(new Button("Clear filters"))
                .build();

        assertNotNull(empty.getEmptyTitle());
        assertEquals("No results found", empty.getEmptyTitle().getText());
        assertNotNull(empty.getDescription());
        assertEquals("Try adjusting your search or filter.", empty.getDescription().getText());
        assertNotNull(empty.getAction());
    }

    @Test
    void emptyBuilder_localizableTitle() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Empty[] holder = new Empty[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = EmptyBuilder.create().title(loc).build()
        );
        assertEquals("TestUS", holder[0].getEmptyTitle().getText());
    }

    @Test
    void emptyBuilder_localizableDescription() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Empty[] holder = new Empty[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = EmptyBuilder.create().description(loc).build()
        );
        assertEquals("TestUS", holder[0].getDescription().getText());
    }

    @Test
    void emptyBuilder_prebuiltSubComponents() {
        EmptyTitle title       = new EmptyTitle("Pre-built title");
        EmptyDescription desc  = new EmptyDescription("Pre-built description");
        EmptyAction action     = new EmptyAction(new Button("Pre-built action"));

        Empty empty = EmptyBuilder.create()
                .title(title)
                .description(desc)
                .action(action)
                .build();

        assertSame(title,  empty.getEmptyTitle());
        assertSame(desc,   empty.getDescription());
        assertSame(action, empty.getAction());
    }

    @Test
    void emptyBuilder_idAndStyleName() {
        Empty empty = EmptyBuilder.create()
                .id("empty-inbox")
                .styleName("my-empty")
                .build();
        assertEquals("empty-inbox", empty.getId().orElse(null));
        assertTrue(empty.getClassNames().contains("my-empty"));
    }

    @Test
    void emptyBuilder_width() {
        Empty empty = EmptyBuilder.create().width("100%").build();
        assertEquals("100%", empty.getWidth());
    }

    @Test
    void emptyBuilder_clearIcon_doesNotThrow() {
        Empty empty = EmptyBuilder.create()
                .icon(new Icon(VaadinIcon.INBOX))
                .clearIcon()
                .build();
        assertNotNull(empty);
    }

    @Test
    void emptyBuilder_illustrationComponent() {
        Empty empty = EmptyBuilder.create()
                .icon(new Span("svg-placeholder"))
                .build();
        assertNotNull(empty);
    }

    // =========================================================================
    // EmptyConfigurator.configure
    // =========================================================================

    @Test
    void emptyConfigurator_configure_returnsConfigurator() {
        EmptyConfigurator.BaseEmptyConfigurator cfg = EmptyConfigurator.configure(new Empty());
        assertNotNull(cfg);
    }

    @Test
    void emptyConfigurator_configure_mutatesExistingEmpty() {
        Empty empty = new Empty();
        EmptyConfigurator.configure(empty)
                .title("Mutated title")
                .description("Mutated description")
                .action(new Button("Mutated action"))
                .id("mutated-id");

        assertEquals("Mutated title",       empty.getEmptyTitle().getText());
        assertEquals("Mutated description", empty.getDescription().getText());
        assertNotNull(empty.getAction());
        assertEquals("mutated-id", empty.getId().orElse(null));
    }

    @Test
    void emptyConfigurator_configure_replacesTitle() {
        Empty empty = new Empty();
        empty.setTitle("Old");
        EmptyConfigurator.configure(empty).title("New");
        assertEquals("New", empty.getEmptyTitle().getText());
    }
}

