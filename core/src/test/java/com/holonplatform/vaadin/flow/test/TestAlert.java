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
import com.holonplatform.vaadin.flow.components.builders.AlertBuilder;
import com.holonplatform.vaadin.flow.components.builders.AlertConfigurator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertTitle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Alert} component and its builder / configurator infrastructure.
 *
 * <p>Tests are deliberately free of a running Vaadin UI / VaadinSession so they stay
 * lightweight and fast. DOM-level assertions use {@code component.getClassNames()} and
 * {@code component.getElement()} APIs that are available without a running servlet.</p>
 */
class TestAlert {

    // =========================================================================
    // Variant enum
    // =========================================================================

    @Test
    void variant_getCssClass_returnsCorrectModifierClass() {
        assertEquals("alert--default",     Alert.Variant.DEFAULT.getCssClass());
        assertEquals("alert--destructive", Alert.Variant.DESTRUCTIVE.getCssClass());
        assertEquals("alert--warning",     Alert.Variant.WARNING.getCssClass());
        assertEquals("alert--success",     Alert.Variant.SUCCESS.getCssClass());
        assertEquals("alert--info",        Alert.Variant.INFO.getCssClass());
    }

    // =========================================================================
    // Alert – constructor & initial state
    // =========================================================================

    @Test
    void constructor_default_appliesDefaultVariantAndBaseClass() {
        Alert alert = new Alert();

        assertTrue(alert.getClassNames().contains("alert"),           "base 'alert' class must be present");
        assertTrue(alert.getClassNames().contains("alert--default"),  "DEFAULT modifier must be present");
        assertEquals("alert", alert.getElement().getAttribute("role"), "role='alert' must be set");
        assertEquals(Alert.Variant.DEFAULT, alert.getVariant());
    }

    @Test
    void constructor_withVariant_appliesCorrectModifierClass() {
        Alert alert = new Alert(Alert.Variant.DESTRUCTIVE);

        assertTrue(alert.getClassNames().contains("alert--destructive"), "DESTRUCTIVE modifier must be present");
        assertFalse(alert.getClassNames().contains("alert--default"),    "DEFAULT modifier must not be present");
        assertEquals(Alert.Variant.DESTRUCTIVE, alert.getVariant());
    }

    // =========================================================================
    // Alert – setVariant
    // =========================================================================

    @Test
    void setVariant_swapsModifierClass() {
        Alert alert = new Alert(Alert.Variant.DEFAULT);

        alert.setVariant(Alert.Variant.SUCCESS);

        assertFalse(alert.getClassNames().contains("alert--default"), "old modifier must be removed");
        assertTrue(alert.getClassNames().contains("alert--success"),  "new modifier must be added");
        assertEquals(Alert.Variant.SUCCESS, alert.getVariant());
    }

    @Test
    void setVariant_null_removesCurrentModifier() {
        Alert alert = new Alert(Alert.Variant.INFO);
        alert.setVariant(null);

        assertFalse(alert.getClassNames().contains("alert--info"), "modifier must be removed for null variant");
        assertNull(alert.getVariant());
    }

    // =========================================================================
    // Alert – icon
    // =========================================================================

    @Test
    void setIcon_addsIconAndHasIconClass() {
        Alert alert = new Alert();

        assertFalse(alert.getClassNames().contains("alert--has-icon"), "has-icon class must be absent initially");

        alert.setIcon(new Icon(VaadinIcon.INFO_CIRCLE));

        assertTrue(alert.getClassNames().contains("alert--has-icon"), "has-icon class must be added after setIcon");
    }

    @Test
    void clearIcon_removesHasIconClass() {
        Alert alert = new Alert();
        alert.setIcon(new Icon(VaadinIcon.WARNING));

        assertTrue(alert.getClassNames().contains("alert--has-icon"));

        alert.clearIcon();

        assertFalse(alert.getClassNames().contains("alert--has-icon"), "has-icon class must be removed after clearIcon");
    }

    @Test
    void setIcon_null_clearsIcon() {
        Alert alert = new Alert();
        alert.setIcon(new Icon(VaadinIcon.WARNING));
        alert.setIcon(null);

        assertFalse(alert.getClassNames().contains("alert--has-icon"), "has-icon class must be absent after setIcon(null)");
    }

    // =========================================================================
    // Alert – title
    // =========================================================================

    @Test
    void setTitle_string_setsAlertTitle() {
        Alert alert = new Alert();

        assertNull(alert.getAlertTitle(), "title must be null initially");

        alert.setTitle("Something went wrong");

        AlertTitle title = alert.getAlertTitle();
        assertNotNull(title);
        assertEquals("Something went wrong", title.getText());
        assertTrue(title.getClassNames().contains("alert__title"));
    }

    @Test
    void setTitle_replacesExistingTitle() {
        Alert alert = new Alert();
        alert.setTitle("First");
        AlertTitle first = alert.getAlertTitle();

        alert.setTitle("Second");
        AlertTitle second = alert.getAlertTitle();

        assertNotSame(first, second, "setTitle must replace the previous title instance");
        assertEquals("Second", second.getText());
        // The old title must no longer be a child of the component
        assertTrue(first.getParent().isEmpty(), "replaced title must be detached");
    }

    @Test
    void setTitle_null_removesTitle() {
        Alert alert = new Alert();
        alert.setTitle("Remove me");
        AlertTitle old = alert.getAlertTitle();

        alert.setTitle((AlertTitle) null);

        assertNull(alert.getAlertTitle());
        assertTrue(old.getParent().isEmpty(), "removed title must be detached");
    }

    @Test
    void setTitle_localizable_resolvesText() {
        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        Alert[] alertHolder = new Alert[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            alertHolder[0] = new Alert();
            alertHolder[0].setTitle(localizable);
        });

        AlertTitle title = alertHolder[0].getAlertTitle();
        assertNotNull(title);
        // LocalizationProvider resolves "test.code" → "TestUS" in US locale context
        assertEquals("TestUS", title.getText());
    }

    // =========================================================================
    // Alert – description
    // =========================================================================

    @Test
    void setDescription_string_setsAlertDescription() {
        Alert alert = new Alert();

        assertNull(alert.getDescription(), "description must be null initially");

        alert.setDescription("Your session has expired.");

        AlertDescription desc = alert.getDescription();
        assertNotNull(desc);
        assertEquals("Your session has expired.", desc.getText());
        assertTrue(desc.getClassNames().contains("alert__description"));
    }

    @Test
    void setDescription_replacesExistingDescription() {
        Alert alert = new Alert();
        alert.setDescription("First");
        AlertDescription first = alert.getDescription();

        alert.setDescription("Second");
        AlertDescription second = alert.getDescription();

        assertNotSame(first, second, "setDescription must replace the previous description instance");
        assertEquals("Second", second.getText());
        assertTrue(first.getParent().isEmpty(), "replaced description must be detached");
    }

    @Test
    void setDescription_null_removesDescription() {
        Alert alert = new Alert();
        alert.setDescription("Remove me");
        AlertDescription old = alert.getDescription();

        alert.setDescription((AlertDescription) null);

        assertNull(alert.getDescription());
        assertTrue(old.getParent().isEmpty(), "removed description must be detached");
    }

    @Test
    void setDescription_localizable_resolvesText() {
        Localizable localizable = Localizable.builder()
                .message("Fallback desc")
                .messageCode("test.code")
                .build();

        Alert[] alertHolder = new Alert[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            alertHolder[0] = new Alert();
            alertHolder[0].setDescription(localizable);
        });

        AlertDescription desc = alertHolder[0].getDescription();
        assertNotNull(desc);
        assertEquals("TestUS", desc.getText());
    }

    // =========================================================================
    // Alert – action
    // =========================================================================

    @Test
    void setAction_components_wrapsInAlertAction() {
        Alert alert = new Alert();

        assertNull(alert.getAction(), "action must be null initially");

        Button btn = new Button("Sign in");
        alert.setAction(btn);

        AlertAction action = alert.getAction();
        assertNotNull(action);
        assertTrue(action.getClassNames().contains("alert__action"));
        // The button must be a child of the action container
        assertTrue(action.getChildren().anyMatch(c -> c == btn));
    }

    @Test
    void setAction_alertAction_setsDirectly() {
        Alert alert = new Alert();
        AlertAction action = new AlertAction(new Span("Retry"));

        alert.setAction(action);

        assertSame(action, alert.getAction());
    }

    @Test
    void setAction_replacesExistingAction() {
        Alert alert = new Alert();
        AlertAction first = new AlertAction(new Button("First"));
        AlertAction second = new AlertAction(new Button("Second"));

        alert.setAction(first);
        alert.setAction(second);

        assertSame(second, alert.getAction(), "second action must replace the first");
        assertTrue(first.getParent().isEmpty(), "replaced action must be detached");
    }

    @Test
    void setAction_null_removesAction() {
        Alert alert = new Alert();
        alert.setAction(new Button("Remove"));
        AlertAction old = alert.getAction();

        alert.setAction((AlertAction) null);

        assertNull(alert.getAction());
        assertTrue(old.getParent().isEmpty(), "removed action must be detached");
    }

    // =========================================================================
    // Alert – ordering: title is first, description second, action last
    // =========================================================================

    @Test
    void setTitleAndDescription_bothAreAttached() {
        Alert alert = new Alert();
        alert.setTitle("T");
        alert.setDescription("D");

        AlertTitle title = alert.getAlertTitle();
        AlertDescription desc = alert.getDescription();

        assertNotNull(title);
        assertNotNull(desc);

        // AlertTitle and AlertDescription are children of the internal contentSlot,
        // not direct children of Alert — verify via parent presence
        assertTrue(title.getParent().isPresent(), "title must be attached inside the alert");
        assertTrue(desc.getParent().isPresent(), "description must be attached inside the alert");
    }

    // =========================================================================
    // AlertBuilder – factories
    // =========================================================================

    @Test
    void alertBuilder_create_returnsDefaultVariantBuilder() {
        AlertBuilder builder = AlertBuilder.create();
        assertNotNull(builder);

        Alert alert = builder.build();
        assertNotNull(alert);
        assertEquals(Alert.Variant.DEFAULT, alert.getVariant());
    }

    @Test
    void alertBuilder_createWithVariant_appliesVariant() {
        Alert alert = AlertBuilder.create(Alert.Variant.DESTRUCTIVE).build();

        assertEquals(Alert.Variant.DESTRUCTIVE, alert.getVariant());
        assertTrue(alert.getClassNames().contains("alert--destructive"));
    }

    @Test
    void alert_staticBuilderFactory_delegatesToAlertBuilder() {
        Alert alert = Alert.builder().build();
        assertNotNull(alert);
        assertEquals(Alert.Variant.DEFAULT, alert.getVariant());

        Alert alertWithVariant = Alert.builder(Alert.Variant.WARNING).build();
        assertEquals(Alert.Variant.WARNING, alertWithVariant.getVariant());
    }

    // =========================================================================
    // AlertBuilder – fluent chaining
    // =========================================================================

    @Test
    void alertBuilder_fluentChain_setsAllSlots() {
        Button signIn = new Button("Sign in");

        Alert alert = AlertBuilder.create(Alert.Variant.DESTRUCTIVE)
                .icon(new Icon(VaadinIcon.WARNING))
                .title("Something went wrong")
                .description("Your session expired.")
                .action(signIn)
                .build();

        assertEquals(Alert.Variant.DESTRUCTIVE, alert.getVariant());
        assertTrue(alert.getClassNames().contains("alert--has-icon"),  "icon class must be set");
        assertNotNull(alert.getAlertTitle(),                           "title must be set");
        assertEquals("Something went wrong", alert.getAlertTitle().getText());
        assertNotNull(alert.getDescription(),                          "description must be set");
        assertEquals("Your session expired.", alert.getDescription().getText());
        assertNotNull(alert.getAction(),                               "action must be set");
    }

    @Test
    void alertBuilder_variant_changesVariantMidChain() {
        Alert alert = AlertBuilder.create()
                .variant(Alert.Variant.SUCCESS)
                .title("Done!")
                .build();

        assertEquals(Alert.Variant.SUCCESS, alert.getVariant());
        assertTrue(alert.getClassNames().contains("alert--success"));
        assertFalse(alert.getClassNames().contains("alert--default"), "DEFAULT modifier must be swapped out");
    }

    @Test
    void alertBuilder_clearIcon_removesIcon() {
        Alert alert = AlertBuilder.create()
                .icon(new Icon(VaadinIcon.INFO_CIRCLE))
                .clearIcon()
                .build();

        assertFalse(alert.getClassNames().contains("alert--has-icon"), "has-icon class must be absent after clearIcon()");
    }

    @Test
    void alertBuilder_localizableTitle_resolvesText() {
        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        Alert[] holder = new Alert[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = AlertBuilder.create().title(localizable).build()
        );

        assertNotNull(holder[0].getAlertTitle());
        assertEquals("TestUS", holder[0].getAlertTitle().getText());
    }

    @Test
    void alertBuilder_localizableDescription_resolvesText() {
        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        Alert[] holder = new Alert[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = AlertBuilder.create().description(localizable).build()
        );

        assertNotNull(holder[0].getDescription());
        assertEquals("TestUS", holder[0].getDescription().getText());
    }

    @Test
    void alertBuilder_commonConfigurator_idAndVisibility() {
        Alert alert = AlertBuilder.create()
                .id("my-alert")
                .visible(false)
                .build();

        assertTrue(alert.getId().isPresent());
        assertEquals("my-alert", alert.getId().get());
        assertFalse(alert.isVisible());
    }

    @Test
    void alertBuilder_styleName_addsCustomClass() {
        Alert alert = AlertBuilder.create()
                .styleName("my-custom-alert")
                .build();

        assertTrue(alert.getClassNames().contains("my-custom-alert"));
    }

    // =========================================================================
    // AlertConfigurator.configure – configure existing instance
    // =========================================================================

    @Test
    void alertConfigurator_configure_returnsBaseConfigurator() {
        Alert alert = new Alert();
        AlertConfigurator.BaseAlertConfigurator cfg = AlertConfigurator.configure(alert);
        assertNotNull(cfg);
    }

    @Test
    void alertConfigurator_configure_mutatesExistingComponent() {
        Alert alert = new Alert();
        AlertConfigurator.configure(alert)
                .variant(Alert.Variant.WARNING)
                .title("Heads up!")
                .description("Something needs attention.")
                .action(new Button("Dismiss"));

        assertEquals(Alert.Variant.WARNING, alert.getVariant());
        assertNotNull(alert.getAlertTitle());
        assertEquals("Heads up!", alert.getAlertTitle().getText());
        assertNotNull(alert.getDescription());
        assertEquals("Something needs attention.", alert.getDescription().getText());
        assertNotNull(alert.getAction());
    }

    // =========================================================================
    // AlertTitle – sub-component
    // =========================================================================

    @Test
    void alertTitle_string_hasCssClassAndText() {
        AlertTitle title = new AlertTitle("Error occurred");

        assertTrue(title.getClassNames().contains("alert__title"));
        assertEquals("Error occurred", title.getText());
    }

    @Test
    void alertTitle_localizable_hasCssClass() {
        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        AlertTitle[] holder = new AlertTitle[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = new AlertTitle(localizable)
        );

        assertTrue(holder[0].getClassNames().contains("alert__title"));
        assertEquals("TestUS", holder[0].getText());
    }

    @Test
    void alertTitle_setLocalizableText_updatesText() {
        AlertTitle title = new AlertTitle("Initial");

        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        LocalizationTestUtils.withTestLocalizationContext(() ->
                title.setLocalizableText(localizable)
        );

        assertEquals("TestUS", title.getText());
    }

    // =========================================================================
    // AlertDescription – sub-component
    // =========================================================================

    @Test
    void alertDescription_string_hasCssClassAndText() {
        AlertDescription desc = new AlertDescription("Session expired.");

        assertTrue(desc.getClassNames().contains("alert__description"));
        assertEquals("Session expired.", desc.getText());
    }

    @Test
    void alertDescription_localizable_hasCssClass() {
        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        AlertDescription[] holder = new AlertDescription[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = new AlertDescription(localizable)
        );

        assertTrue(holder[0].getClassNames().contains("alert__description"));
        assertEquals("TestUS", holder[0].getText());
    }

    @Test
    void alertDescription_setLocalizableText_updatesText() {
        AlertDescription desc = new AlertDescription("Initial");

        Localizable localizable = Localizable.builder()
                .message("Fallback")
                .messageCode("test.code")
                .build();

        LocalizationTestUtils.withTestLocalizationContext(() ->
                desc.setLocalizableText(localizable)
        );

        assertEquals("TestUS", desc.getText());
    }

    // =========================================================================
    // AlertAction – sub-component
    // =========================================================================

    @Test
    void alertAction_hasCssClass() {
        AlertAction action = new AlertAction(new Button("Retry"));
        assertTrue(action.getClassNames().contains("alert__action"));
    }

    @Test
    void alertAction_containsProvidedComponents() {
        Button btn1 = new Button("Retry");
        Button btn2 = new Button("Dismiss");

        AlertAction action = new AlertAction(btn1, btn2);

        assertEquals(2, action.getChildren().count());
        assertTrue(action.getChildren().anyMatch(c -> c == btn1));
        assertTrue(action.getChildren().anyMatch(c -> c == btn2));
    }

    @Test
    void alertAction_emptyConstructor_hasCssClassNoChildren() {
        AlertAction action = new AlertAction();
        assertTrue(action.getClassNames().contains("alert__action"));
        assertEquals(0, action.getChildren().count());
    }
}


