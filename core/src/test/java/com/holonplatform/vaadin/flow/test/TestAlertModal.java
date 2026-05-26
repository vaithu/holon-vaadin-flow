package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.AlertModalBuilder;
import com.holonplatform.vaadin.flow.components.builders.AlertModalConfigurator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for {@link AlertModal} — the dismissible modal notification wrapper. */
class TestAlertModal {

    @Test
    void constructor_default_isDefaultVariantAndClosable() {
        AlertModal modal = new AlertModal();
        assertNotNull(modal.getAlert());
        assertEquals(Alert.Variant.DEFAULT, modal.getVariant());
        assertEquals("alertdialog", modal.getElement().getAttribute("role"));
        assertTrue(modal.isCloseOnEsc());
        assertTrue(modal.isCloseOnOutsideClick());
    }

    @Test
    void constructor_withVariant_appliesVariantToInnerAlert() {
        AlertModal modal = new AlertModal(Alert.Variant.DESTRUCTIVE);
        assertEquals(Alert.Variant.DESTRUCTIVE, modal.getVariant());
        assertTrue(modal.getAlert().getClassNames().contains("alert--destructive"));
    }

    @Test
    void setVariant_delegatesToInnerAlert() {
        AlertModal modal = new AlertModal();
        modal.setVariant(Alert.Variant.SUCCESS);
        assertEquals(Alert.Variant.SUCCESS, modal.getVariant());
        assertTrue(modal.getAlert().getClassNames().contains("alert--success"));
    }

    @Test
    void setIcon_delegatesToInnerAlert() {
        AlertModal modal = new AlertModal();
        modal.setIcon(new Icon(VaadinIcon.INFO_CIRCLE));
        assertTrue(modal.getAlert().getClassNames().contains("alert--has-icon"));
    }

    @Test
    void clearIcon_delegatesToInnerAlert() {
        AlertModal modal = new AlertModal();
        modal.setIcon(new Icon(VaadinIcon.WARNING));
        modal.clearIcon();
        assertFalse(modal.getAlert().getClassNames().contains("alert--has-icon"));
    }

    @Test
    void setTitle_string_delegatesToInnerAlert() {
        AlertModal modal = new AlertModal();
        modal.setTitle("Session expired");
        assertNotNull(modal.getAlertTitle());
        assertEquals("Session expired", modal.getAlertTitle().getText());
    }

    @Test
    void setTitle_localizable_resolvesViaLocalizationProvider() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertModal[] holder = new AlertModal[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new AlertModal();
            holder[0].setTitle(loc);
        });
        assertEquals("TestUS", holder[0].getAlertTitle().getText());
    }

    @Test
    void setDescription_string_delegatesToInnerAlert() {
        AlertModal modal = new AlertModal();
        modal.setDescription("Please sign in again.");
        assertNotNull(modal.getAlertDescription());
        assertEquals("Please sign in again.", modal.getAlertDescription().getText());
    }

    @Test
    void setAction_components_delegatesToInnerAlert() {
        AlertModal modal = new AlertModal();
        Button btn = new Button("Sign in");
        modal.setAction(btn);
        assertNotNull(modal.getAlertAction());
        assertTrue(modal.getAlertAction().getChildren().anyMatch(c -> c == btn));
    }

    @Test
    void setAction_alertAction_setsDirectly() {
        AlertModal modal = new AlertModal();
        AlertAction action = new AlertAction(new Span("Retry"));
        modal.setAction(action);
        assertSame(action, modal.getAlertAction());
    }

    @Test
    void setCloseOnEsc_false_disables() {
        AlertModal modal = new AlertModal();
        modal.setCloseOnEsc(false);
        assertFalse(modal.isCloseOnEsc());
    }

    @Test
    void setCloseOnOutsideClick_false_disables() {
        AlertModal modal = new AlertModal();
        modal.setCloseOnOutsideClick(false);
        assertFalse(modal.isCloseOnOutsideClick());
    }

    @Test
    void alertModalBuilder_create_returnsDefaultVariant() {
        AlertModal modal = AlertModalBuilder.create().build();
        assertEquals(Alert.Variant.DEFAULT, modal.getVariant());
    }

    @Test
    void alertModalBuilder_createWithVariant_appliesVariant() {
        AlertModal modal = AlertModalBuilder.create(Alert.Variant.WARNING).build();
        assertEquals(Alert.Variant.WARNING, modal.getVariant());
    }

    @Test
    void alertModal_staticBuilderFactory() {
        assertEquals(Alert.Variant.DEFAULT, AlertModal.builder().build().getVariant());
        assertEquals(Alert.Variant.INFO, AlertModal.builder(Alert.Variant.INFO).build().getVariant());
    }

    @Test
    void alertModalBuilder_fluentChain() {
        Button signIn = new Button("Sign in");
        AlertModal modal = AlertModalBuilder.create(Alert.Variant.DESTRUCTIVE)
                .icon(new Icon(VaadinIcon.WARNING))
                .title("Session expired")
                .description("Please sign in again.")
                .action(signIn)
                .closeOnEsc(true)
                .closeOnOutsideClick(false)
                .build();

        assertEquals(Alert.Variant.DESTRUCTIVE, modal.getVariant());
        assertTrue(modal.getAlert().getClassNames().contains("alert--has-icon"));
        assertEquals("Session expired", modal.getAlertTitle().getText());
        assertEquals("Please sign in again.", modal.getAlertDescription().getText());
        assertNotNull(modal.getAlertAction());
        assertTrue(modal.isCloseOnEsc());
        assertFalse(modal.isCloseOnOutsideClick());
    }

    @Test
    void alertModalBuilder_localizableTitle() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertModal[] holder = new AlertModal[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = AlertModalBuilder.create().title(loc).build()
        );
        assertEquals("TestUS", holder[0].getAlertTitle().getText());
    }

    @Test
    void alertModalBuilder_idAndStyleName() {
        AlertModal modal = AlertModalBuilder.create()
                .id("my-modal").styleName("custom-class").build();
        assertEquals("my-modal", modal.getId().orElse(null));
        assertTrue(modal.getClassNames().contains("custom-class"));
    }

    @Test
    void alertModalConfigurator_configure_mutatesExisting() {
        AlertModal modal = new AlertModal();
        AlertModalConfigurator.configure(modal)
                .variant(Alert.Variant.WARNING)
                .title("Heads up!")
                .description("Attention needed.")
                .action(new Button("Dismiss"));
        assertEquals(Alert.Variant.WARNING, modal.getVariant());
        assertEquals("Heads up!", modal.getAlertTitle().getText());
        assertEquals("Attention needed.", modal.getAlertDescription().getText());
        assertNotNull(modal.getAlertAction());
    }

    @Test
    void getAlert_returnsInnerAlertWithExpectedContent() {
        AlertModal modal = AlertModalBuilder.create(Alert.Variant.SUCCESS).title("Saved!").build();
        Alert innerAlert = modal.getAlert();
        assertNotNull(innerAlert);
        assertEquals(Alert.Variant.SUCCESS, innerAlert.getVariant());
        assertEquals("Saved!", innerAlert.getAlertTitle().getText());
    }
}

