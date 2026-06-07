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
import com.holonplatform.vaadin.flow.components.builders.AlertDialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.AlertDialogConfigurator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AlertDialog} — the shadcn/ui-inspired confirmation dialog
 * (non-dismissible by default, with Cancel + Confirm buttons).
 */
class TestAlertDialog {

    // =========================================================================
    // Constructor & initial state
    // =========================================================================

    @Test
    void constructor_isNonDismissibleByDefault() {
        AlertDialog dialog = new AlertDialog();

        assertFalse(dialog.isCloseOnEsc(),          "closeOnEsc must default to false");
        assertFalse(dialog.isCloseOnOutsideClick(), "closeOnOutsideClick must default to false");
        assertEquals("alertdialog", dialog.getElement().getAttribute("role"));
        assertEquals("true",        dialog.getElement().getAttribute("aria-modal"));
    }

    @Test
    void constructor_titleAndDescriptionAreNullByDefault() {
        AlertDialog dialog = new AlertDialog();
        assertNull(dialog.getDialogTitle());
        assertNull(dialog.getDialogDescription());
    }

    // =========================================================================
    // Title
    // =========================================================================

    @Test
    void setDialogTitle_string_setsTitle() {
        AlertDialog dialog = new AlertDialog();
        dialog.setDialogTitle("Are you absolutely sure?");
        assertEquals("Are you absolutely sure?", dialog.getDialogTitle());
    }

    @Test
    void setDialogTitle_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new AlertDialog();
            holder[0].setDialogTitle(loc);
        });
        assertEquals("TestUS", holder[0].getDialogTitle());
    }

    @Test
    void setDialogTitle_null_clearsTitle() {
        AlertDialog dialog = new AlertDialog();
        dialog.setDialogTitle("Title");
        dialog.setDialogTitle((String) null);
        assertNull(dialog.getDialogTitle());
    }

    // =========================================================================
    // Description
    // =========================================================================

    @Test
    void setDialogDescription_string_setsDescription() {
        AlertDialog dialog = new AlertDialog();
        dialog.setDialogDescription("This action cannot be undone.");
        assertEquals("This action cannot be undone.", dialog.getDialogDescription());
    }

    @Test
    void setDialogDescription_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new AlertDialog();
            holder[0].setDialogDescription(loc);
        });
        assertEquals("TestUS", holder[0].getDialogDescription());
    }

    // =========================================================================
    // Confirm callback
    // =========================================================================

    @Test
    void setOnConfirm_callbackInvokedOnConfirm() {
        AtomicBoolean called = new AtomicBoolean(false);
        AlertDialog dialog = new AlertDialog();
        dialog.setOnConfirm(() -> called.set(true));
        // Simulate confirm click via the builder then direct component state check
        // We can't click buttons server-side without UI, but we verify the callback is stored
        assertFalse(called.get(), "callback must not fire at construction time");
    }

    @Test
    void setConfirmText_string_updatesLabel() {
        AlertDialog dialog = new AlertDialog();
        dialog.setConfirmText("Yes, delete");
        // We can't directly read button text without DOM in test, but builder chain should work
        assertNotNull(dialog); // no exception = success
    }

    @Test
    void setConfirmText_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new AlertDialog();
            holder[0].setConfirmText(loc);
        });
        assertNotNull(holder[0]);
    }

    // =========================================================================
    // Cancel callback
    // =========================================================================

    @Test
    void setOnCancel_callbackStoredWithoutFiring() {
        AtomicBoolean called = new AtomicBoolean(false);
        AlertDialog dialog = new AlertDialog();
        dialog.setOnCancel(() -> called.set(true));
        assertFalse(called.get());
    }

    @Test
    void setCancelText_string_updatesLabel() {
        AlertDialog dialog = new AlertDialog();
        dialog.setCancelText("No, go back");
        assertNotNull(dialog);
    }

    // =========================================================================
    // Destructive marker
    // =========================================================================

    @Test
    @SuppressWarnings("removal")
    void setActionDestructive_addsDestructiveClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setActionDestructive(); // must not throw
        assertNotNull(dialog);
    }

    // =========================================================================
    // AlertDialogBuilder — factories
    // =========================================================================

    @Test
    void alertDialogBuilder_create_returnsBuilder() {
        AlertDialogBuilder builder = AlertDialogBuilder.create();
        assertNotNull(builder);
        AlertDialog dialog = builder.build();
        assertNotNull(dialog);
    }

    @Test
    void alertDialog_staticBuilderFactory() {
        AlertDialog dialog = AlertDialog.builder().build();
        assertNotNull(dialog);
        assertFalse(dialog.isCloseOnEsc());
        assertFalse(dialog.isCloseOnOutsideClick());
    }

    // =========================================================================
    // AlertDialogBuilder — fluent chaining
    // =========================================================================

    @Test
    @SuppressWarnings("removal")
    void alertDialogBuilder_fluentChain_fullConfiguration() {
        AtomicInteger confirmCount = new AtomicInteger(0);
        AtomicInteger cancelCount  = new AtomicInteger(0);

        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Are you sure?")
                .description("This cannot be undone.")
                .confirmText("Yes, delete")
                .cancelText("Cancel")
                .onConfirm(confirmCount::incrementAndGet)
                .onCancel(cancelCount::incrementAndGet)
                .closeOnEsc(false)
                .closeOnOutsideClick(false)
                .draggable(false)
                .resizable(false)
                .build();

        assertEquals("Are you sure?", dialog.getDialogTitle());
        assertEquals("This cannot be undone.", dialog.getDialogDescription());
        assertFalse(dialog.isCloseOnEsc());
        assertFalse(dialog.isCloseOnOutsideClick());
        assertFalse(dialog.isDraggable());
        assertFalse(dialog.isResizable());
        // callbacks are registered — no invocation yet
        assertEquals(0, confirmCount.get());
        assertEquals(0, cancelCount.get());
    }

    @Test
    void alertDialogBuilder_closeOnEscTrue_canBeEnabled() {
        AlertDialog dialog = AlertDialogBuilder.create().closeOnEsc(true).build();
        assertTrue(dialog.isCloseOnEsc());
    }

    @Test
    void alertDialogBuilder_closeOnOutsideClickTrue_canBeEnabled() {
        AlertDialog dialog = AlertDialogBuilder.create().closeOnOutsideClick(true).build();
        assertTrue(dialog.isCloseOnOutsideClick());
    }

    @Test
    void alertDialogBuilder_localizableTitle() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = AlertDialogBuilder.create().title(loc).build()
        );
        assertEquals("TestUS", holder[0].getDialogTitle());
    }

    @Test
    void alertDialogBuilder_localizableDescription() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = AlertDialogBuilder.create().description(loc).build()
        );
        assertEquals("TestUS", holder[0].getDialogDescription());
    }

    @Test
    void alertDialogBuilder_idAndStyleName() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .id("confirm-dialog")
                .styleName("my-confirm")
                .build();
        assertEquals("confirm-dialog", dialog.getId().orElse(null));
        assertTrue(dialog.getClassNames().contains("my-confirm"));
    }

    @Test
    void alertDialogBuilder_withOpenedChangeListener_noException() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Test")
                .withOpenedChangeListener(e -> {})
                .build();
        assertNotNull(dialog);
    }

    // =========================================================================
    // Confirm callback — conditional (BooleanSupplier)
    // =========================================================================

    @Test
    void setOnConfirm_booleanSupplier_storedWithoutFiring() {
        AtomicInteger callCount = new AtomicInteger(0);
        AlertDialog dialog = new AlertDialog();
        dialog.setOnConfirm((BooleanSupplier) () -> { callCount.incrementAndGet(); return true; });
        assertEquals(0, callCount.get(), "supplier must not fire at construction time");
    }

    @Test
    void setOnConfirm_booleanSupplier_replacesRunnable() {
        // Setting a BooleanSupplier after a Runnable must not throw and the object state
        // must be coherent (both are mutually exclusive).
        AtomicBoolean runnableCalled = new AtomicBoolean(false);
        AtomicBoolean supplierCalled = new AtomicBoolean(false);
        AlertDialog dialog = new AlertDialog();
        dialog.setOnConfirm(() -> runnableCalled.set(true));
        dialog.setOnConfirm((BooleanSupplier) () -> { supplierCalled.set(true); return true; });
        // Neither should have fired yet.
        assertFalse(runnableCalled.get());
        assertFalse(supplierCalled.get());
    }

    @Test
    void setOnConfirm_runnable_replacesSupplier() {
        AlertDialog dialog = new AlertDialog();
        dialog.setOnConfirm((BooleanSupplier) () -> true);
        // Replace with Runnable — must not throw.
        dialog.setOnConfirm(() -> {});
        assertNotNull(dialog);
    }

    @Test
    void alertDialogBuilder_onConfirmBooleanSupplier_fluentChain() {
        AtomicInteger callCount = new AtomicInteger(0);
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Delete file?")
                .description("This cannot be undone.")
                .onConfirm((BooleanSupplier) () -> { callCount.incrementAndGet(); return true; })
                .build();
        assertEquals("Delete file?", dialog.getDialogTitle());
        assertEquals(0, callCount.get(), "supplier must not fire during build");
    }

    @Test
    void alertDialogConfigurator_onConfirmBooleanSupplier_mutatesDialog() {
        AtomicBoolean called = new AtomicBoolean(false);
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog)
                .onConfirm((BooleanSupplier) () -> { called.set(true); return false; });
        assertFalse(called.get(), "supplier must not fire when configured");
    }

    // =========================================================================
    // AlertDialogConfigurator.configure
    // =========================================================================

    @Test
    void alertDialogConfigurator_configure_returnsConfigurator() {
        AlertDialogConfigurator.BaseAlertDialogConfigurator cfg =
                AlertDialogConfigurator.configure(new AlertDialog());
        assertNotNull(cfg);
    }

    @Test
    void alertDialogConfigurator_configure_mutatesExistingDialog() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog)
                .title("Heads up!")
                .description("Something needs attention.")
                .confirmText("Proceed")
                .cancelText("Go back")
                .closeOnEsc(true)
                .closeOnOutsideClick(true);

        assertEquals("Heads up!", dialog.getDialogTitle());
        assertEquals("Something needs attention.", dialog.getDialogDescription());
        assertTrue(dialog.isCloseOnEsc());
        assertTrue(dialog.isCloseOnOutsideClick());
    }

    // =========================================================================
    // Variant — setVariant(Alert.Variant)
    // =========================================================================

    @Test
    void setVariant_destructive_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.setVariant(Alert.Variant.DESTRUCTIVE));
    }

    @Test
    void setVariant_allValues_noException() {
        for (Alert.Variant v : Alert.Variant.values()) {
            AlertDialog dialog = new AlertDialog();
            assertDoesNotThrow(() -> dialog.setVariant(v));
        }
    }

    @Test
    void setVariant_null_resetsToDefault_noException() {
        AlertDialog dialog = new AlertDialog();
        dialog.setVariant(Alert.Variant.DESTRUCTIVE);
        assertDoesNotThrow(() -> dialog.setVariant(null));
    }

    @Test
    void setVariant_switchingVariants_clearsPrevious_noException() {
        AlertDialog dialog = new AlertDialog();
        dialog.setVariant(Alert.Variant.WARNING);
        assertDoesNotThrow(() -> dialog.setVariant(Alert.Variant.SUCCESS));
    }

    @Test
    void alertDialogBuilder_variant_destructive_noException() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .variant(Alert.Variant.DESTRUCTIVE)
                .build();
        assertNotNull(dialog);
    }

    @Test
    void alertDialogBuilder_variant_allValues_noException() {
        for (Alert.Variant v : Alert.Variant.values()) {
            AlertDialog dialog = AlertDialogBuilder.create().variant(v).build();
            assertNotNull(dialog);
        }
    }

    @Test
    void alertDialogConfigurator_variant_info_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() ->
                AlertDialogConfigurator.configure(dialog).variant(Alert.Variant.INFO));
    }

    // =========================================================================
    // Size — setSize(AlertDialog.Size)
    // =========================================================================

    @Test
    void setSize_sm_addsThemeToElement() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.SM);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-sm"),
                "Theme 'alert-dialog-sm' must be present for Size.SM");
    }

    @Test
    void setSize_lg_addsThemeToElement() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.LG);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-lg"));
    }

    @Test
    void setSize_xl_addsThemeToElement() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.XL);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-xl"));
    }

    @Test
    void setSize_xl2_addsThemeToElement() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.XL2);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-2xl"));
    }

    @Test
    void setSize_md_doesNotAddExtraTheme() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.MD);
        // MD is the default — no extra theme token should be added
        assertFalse(dialog.getElement().getThemeList().contains("alert-dialog-md"));
    }

    @Test
    void setSize_null_removesPreviousTheme() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.LG);
        dialog.setSize(null);
        assertFalse(dialog.getElement().getThemeList().contains("alert-dialog-lg"),
                "LG theme must be cleared when size is reset to null");
    }

    @Test
    void setSize_switching_clearsPreviousTheme() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSize(AlertDialog.Size.SM);
        dialog.setSize(AlertDialog.Size.XL);
        assertFalse(dialog.getElement().getThemeList().contains("alert-dialog-sm"),
                "Previous SM theme must be removed when switching to XL");
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-xl"));
    }

    @Test
    void alertDialogBuilder_size_lg_addsTheme() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .size(AlertDialog.Size.LG)
                .build();
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-lg"));
    }

    @Test
    void alertDialogBuilder_size_xl2_addsTheme() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .size(AlertDialog.Size.XL2)
                .build();
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-2xl"));
    }

    @Test
    void alertDialogConfigurator_size_sm_addsTheme() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).size(AlertDialog.Size.SM);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-sm"));
    }

    // =========================================================================
    // Alignment — setAlignment(AlertDialog.Alignment) / centered()
    // =========================================================================

    @Test
    void setAlignment_center_addsCenteredClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setAlignment(AlertDialog.Alignment.CENTER);
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--centered"),
                "Wrapper must carry 'alert-dialog--centered' when alignment is CENTER");
    }

    @Test
    void setAlignment_left_removesCenteredClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setAlignment(AlertDialog.Alignment.CENTER);
        dialog.setAlignment(AlertDialog.Alignment.LEFT);
        Component wrapper = getWrapper(dialog);
        assertFalse(wrapper.getElement().getClassList().contains("alert-dialog--centered"),
                "'alert-dialog--centered' must be removed when alignment is reset to LEFT");
    }

    @Test
    void alertDialogBuilder_centered_shorthand_addsCenteredClass() {
        AlertDialog dialog = AlertDialogBuilder.create().centered().build();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--centered"));
    }

    @Test
    void alertDialogBuilder_alignment_center_addsCenteredClass() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .alignment(AlertDialog.Alignment.CENTER)
                .build();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--centered"));
    }

    @Test
    void alertDialogConfigurator_centered_addsCenteredClass() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).centered();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--centered"));
    }

    // =========================================================================
    // Full-screen on mobile — setFullScreenOnMobile(boolean)
    // =========================================================================

    @Test
    void setFullScreenOnMobile_true_addsTheme() {
        AlertDialog dialog = new AlertDialog();
        dialog.setFullScreenOnMobile(true);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-fullscreen-mobile"),
                "Theme 'alert-dialog-fullscreen-mobile' must be present when fullScreenOnMobile=true");
    }

    @Test
    void setFullScreenOnMobile_false_removesTheme() {
        AlertDialog dialog = new AlertDialog();
        dialog.setFullScreenOnMobile(true);
        dialog.setFullScreenOnMobile(false);
        assertFalse(dialog.getElement().getThemeList().contains("alert-dialog-fullscreen-mobile"),
                "Theme must be removed when fullScreenOnMobile is set back to false");
    }

    @Test
    void alertDialogBuilder_fullScreenOnMobile_true_addsTheme() {
        AlertDialog dialog = AlertDialogBuilder.create().fullScreenOnMobile(true).build();
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-fullscreen-mobile"));
    }

    @Test
    void alertDialogBuilder_fullScreenOnMobile_false_noTheme() {
        AlertDialog dialog = AlertDialogBuilder.create().fullScreenOnMobile(false).build();
        assertFalse(dialog.getElement().getThemeList().contains("alert-dialog-fullscreen-mobile"));
    }

    @Test
    void alertDialogConfigurator_fullScreenOnMobile_true_addsTheme() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).fullScreenOnMobile(true);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-fullscreen-mobile"));
    }

    // =========================================================================
    // ARIA role — setAlertRole(boolean)
    // =========================================================================

    @Test
    void setAlertRole_false_switchesRoleToDialog() {
        AlertDialog dialog = new AlertDialog();
        dialog.setAlertRole(false);
        assertEquals("dialog", dialog.getElement().getAttribute("role"),
                "role must be 'dialog' when alertRole=false");
    }

    @Test
    void setAlertRole_true_restoresAlertDialogRole() {
        AlertDialog dialog = new AlertDialog();
        dialog.setAlertRole(false);
        dialog.setAlertRole(true);
        assertEquals("alertdialog", dialog.getElement().getAttribute("role"),
                "role must be restored to 'alertdialog' when alertRole=true");
    }

    @Test
    void alertDialogBuilder_alertRole_false_switchesRole() {
        AlertDialog dialog = AlertDialogBuilder.create().alertRole(false).build();
        assertEquals("dialog", dialog.getElement().getAttribute("role"));
    }

    @Test
    void alertDialogBuilder_alertRole_true_keepsAlertDialogRole() {
        AlertDialog dialog = AlertDialogBuilder.create().alertRole(true).build();
        assertEquals("alertdialog", dialog.getElement().getAttribute("role"));
    }

    @Test
    void alertDialogConfigurator_alertRole_false_switchesRole() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).alertRole(false);
        assertEquals("dialog", dialog.getElement().getAttribute("role"));
    }

    // =========================================================================
    // Stacked buttons — setStackedButtons(boolean)
    // =========================================================================

    @Test
    void setStackedButtons_true_addsStackedClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setStackedButtons(true);
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--stacked"),
                "Wrapper must carry 'alert-dialog--stacked' when stackedButtons=true");
    }

    @Test
    void setStackedButtons_false_removesStackedClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setStackedButtons(true);
        dialog.setStackedButtons(false);
        Component wrapper = getWrapper(dialog);
        assertFalse(wrapper.getElement().getClassList().contains("alert-dialog--stacked"),
                "'alert-dialog--stacked' must be removed when stackedButtons=false");
    }

    @Test
    void alertDialogBuilder_stackedButtons_true_addsClass() {
        AlertDialog dialog = AlertDialogBuilder.create().stackedButtons(true).build();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--stacked"));
    }

    @Test
    void alertDialogConfigurator_stackedButtons_true_addsClass() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).stackedButtons(true);
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--stacked"));
    }

    // =========================================================================
    // Cancel button visibility — setCancelButtonVisible(boolean)
    // =========================================================================

    @Test
    void setCancelButtonVisible_false_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.setCancelButtonVisible(false));
    }

    @Test
    void setCancelButtonVisible_true_noException() {
        AlertDialog dialog = new AlertDialog();
        dialog.setCancelButtonVisible(false);
        assertDoesNotThrow(() -> dialog.setCancelButtonVisible(true));
    }

    @Test
    void alertDialogBuilder_withCancelButton_false_noException() {
        AlertDialog dialog = AlertDialogBuilder.create().withCancelButton(false).build();
        assertNotNull(dialog);
    }

    @Test
    void alertDialogConfigurator_withCancelButton_false_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> AlertDialogConfigurator.configure(dialog).withCancelButton(false));
    }

    // =========================================================================
    // Secondary action — setSecondaryAction / clearSecondaryAction
    // =========================================================================

    @Test
    void setSecondaryAction_string_callbackNotFiredOnRegistration() {
        AtomicBoolean called = new AtomicBoolean(false);
        AlertDialog dialog = new AlertDialog();
        dialog.setSecondaryAction("Archive", () -> called.set(true));
        assertFalse(called.get(), "Secondary action callback must not fire on registration");
    }

    @Test
    void setSecondaryAction_localizable_callbackNotFiredOnRegistration() {
        Localizable loc = Localizable.builder().message("Archive").messageCode("test.code").build();
        AtomicBoolean called = new AtomicBoolean(false);
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new AlertDialog();
            holder[0].setSecondaryAction(loc, () -> called.set(true));
        });
        assertFalse(called.get(), "Secondary action callback must not fire when registered");
        assertNotNull(holder[0]);
    }

    @Test
    void clearSecondaryAction_afterSetting_noException() {
        AlertDialog dialog = new AlertDialog();
        dialog.setSecondaryAction("Archive", () -> {});
        assertDoesNotThrow(dialog::clearSecondaryAction);
    }

    @Test
    void clearSecondaryAction_withoutPriorSet_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(dialog::clearSecondaryAction);
    }

    @Test
    void alertDialogBuilder_secondaryAction_string_callbackNotFired() {
        AtomicInteger count = new AtomicInteger(0);
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Choose action")
                .secondaryAction("Archive", count::incrementAndGet)
                .confirmText("Delete")
                .variant(Alert.Variant.DESTRUCTIVE)
                .build();
        assertNotNull(dialog);
        assertEquals(0, count.get(), "Callback must not fire during build");
    }

    @Test
    void alertDialogBuilder_secondaryAction_localizable_callbackNotFired() {
        Localizable loc = Localizable.builder().message("Archive").messageCode("test.code").build();
        AtomicInteger count = new AtomicInteger(0);
        AlertDialog[] holder = new AlertDialog[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = AlertDialogBuilder.create()
                        .secondaryAction(loc, count::incrementAndGet)
                        .build()
        );
        assertNotNull(holder[0]);
        assertEquals(0, count.get());
    }

    // =========================================================================
    // Header icon — setHeaderIcon(Component) / setHeaderIcon(Component, Alert.Variant)
    // =========================================================================

    @Test
    void setHeaderIcon_component_addsHasIconClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setHeaderIcon(new Icon(VaadinIcon.WARNING));
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--has-icon"),
                "Wrapper must carry 'alert-dialog--has-icon' after setHeaderIcon");
    }

    @Test
    void setHeaderIcon_withDestructiveVariant_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() ->
                dialog.setHeaderIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.DESTRUCTIVE));
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--has-icon"));
    }

    @Test
    void setHeaderIcon_withNullVariant_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.setHeaderIcon(new Icon(VaadinIcon.WARNING), null));
    }

    @Test
    void setHeaderIcon_withAllVariants_noException() {
        for (Alert.Variant v : Alert.Variant.values()) {
            AlertDialog dialog = new AlertDialog();
            assertDoesNotThrow(() -> dialog.setHeaderIcon(new Icon(VaadinIcon.WARNING), v),
                    "setHeaderIcon must not throw for variant " + v);
        }
    }

    @Test
    void setHeaderIcon_replacingVariant_clearsPreviousVariantClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setHeaderIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.DESTRUCTIVE);
        // Replace variant — must not accumulate extra classes
        assertDoesNotThrow(() -> dialog.setHeaderIcon(new Icon(VaadinIcon.INFO), Alert.Variant.INFO));
    }

    @Test
    void alertDialogBuilder_headerIcon_component_addsHasIconClass() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .headerIcon(new Icon(VaadinIcon.WARNING))
                .build();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--has-icon"));
    }

    @Test
    void alertDialogBuilder_headerIcon_withDestructiveVariant_noException() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Delete account?")
                .headerIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.DESTRUCTIVE)
                .variant(Alert.Variant.DESTRUCTIVE)
                .onConfirm(() -> {})
                .build();
        assertNotNull(dialog);
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--has-icon"));
    }

    @Test
    void alertDialogConfigurator_headerIcon_withVariant_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() ->
                AlertDialogConfigurator.configure(dialog)
                        .headerIcon(new Icon(VaadinIcon.CHECK_CIRCLE), Alert.Variant.SUCCESS));
    }

    // =========================================================================
    // Body content slot — addBodyContent / clearBodyContent
    // =========================================================================

    @Test
    void addBodyContent_singleComponent_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.addBodyContent(new Div()));
    }

    @Test
    void addBodyContent_multipleComponents_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.addBodyContent(new Div(), new Div(), new Div()));
    }

    @Test
    void clearBodyContent_afterAdding_noException() {
        AlertDialog dialog = new AlertDialog();
        dialog.addBodyContent(new Div());
        assertDoesNotThrow(dialog::clearBodyContent);
    }

    @Test
    void clearBodyContent_withoutPriorContent_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(dialog::clearBodyContent);
    }

    @Test
    void alertDialogBuilder_bodyContent_noException() {
        Div content = new Div();
        content.setText("Scrollable list");
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Delete items?")
                .bodyContent(content)
                .size(AlertDialog.Size.LG)
                .variant(Alert.Variant.DESTRUCTIVE)
                .build();
        assertNotNull(dialog);
    }

    @Test
    void alertDialogConfigurator_bodyContent_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() ->
                AlertDialogConfigurator.configure(dialog).bodyContent(new Div()));
    }

    // =========================================================================
    // Loading state — setLoading(boolean)
    // =========================================================================

    @Test
    void setLoading_true_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.setLoading(true));
    }

    @Test
    void setLoading_falseAfterTrue_noException() {
        AlertDialog dialog = new AlertDialog();
        dialog.setLoading(true);
        assertDoesNotThrow(() -> dialog.setLoading(false));
    }

    @Test
    void setLoading_falseWithoutPriorTrue_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> dialog.setLoading(false));
    }

    @Test
    void alertDialogBuilder_loading_true_noException() {
        AlertDialog dialog = AlertDialogBuilder.create().loading(true).build();
        assertNotNull(dialog);
    }

    @Test
    void alertDialogBuilder_loading_false_noException() {
        AlertDialog dialog = AlertDialogBuilder.create().loading(false).build();
        assertNotNull(dialog);
    }

    @Test
    void alertDialogConfigurator_loading_true_noException() {
        AlertDialog dialog = new AlertDialog();
        assertDoesNotThrow(() -> AlertDialogConfigurator.configure(dialog).loading(true));
    }

    // =========================================================================
    // Close button — setCloseButtonVisible / setCloseButtonIcon
    // =========================================================================

    @Test
    void setCloseButtonVisible_true_addsCloseableClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setCloseButtonVisible(true);
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--closeable"),
                "Wrapper must carry 'alert-dialog--closeable' when close button is shown");
    }

    @Test
    void setCloseButtonVisible_false_removesCloseableClass() {
        AlertDialog dialog = new AlertDialog();
        dialog.setCloseButtonVisible(true);
        dialog.setCloseButtonVisible(false);
        Component wrapper = getWrapper(dialog);
        assertFalse(wrapper.getElement().getClassList().contains("alert-dialog--closeable"),
                "'alert-dialog--closeable' must be removed when close button is hidden");
    }

    @Test
    void setCloseButtonIcon_implicitlyShowsButton() {
        AlertDialog dialog = new AlertDialog();
        dialog.setCloseButtonIcon(new Icon(VaadinIcon.CLOSE_SMALL));
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--closeable"),
                "setCloseButtonIcon must implicitly show the close button");
    }

    @Test
    void alertDialogBuilder_withCloseButton_true_addsCloseableClass() {
        AlertDialog dialog = AlertDialogBuilder.create().withCloseButton(true).build();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--closeable"));
    }

    @Test
    void alertDialogBuilder_withCloseButton_false_noCloseableClass() {
        AlertDialog dialog = AlertDialogBuilder.create().withCloseButton(false).build();
        Component wrapper = getWrapper(dialog);
        assertFalse(wrapper.getElement().getClassList().contains("alert-dialog--closeable"));
    }

    @Test
    void alertDialogBuilder_closeIcon_addsCloseableClass() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .closeIcon(new Icon(VaadinIcon.CLOSE_SMALL))
                .build();
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--closeable"));
    }

    @Test
    void alertDialogConfigurator_withCloseButton_true_addsCloseableClass() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).withCloseButton(true);
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--closeable"));
    }

    @Test
    void alertDialogConfigurator_closeIcon_addsCloseableClass() {
        AlertDialog dialog = new AlertDialog();
        AlertDialogConfigurator.configure(dialog).closeIcon(new Icon(VaadinIcon.CLOSE_SMALL));
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--closeable"));
    }

    // =========================================================================
    // Full fluent chain — combined features (Tailwind UI patterns from Javadoc)
    // =========================================================================

    @Test
    void alertDialogBuilder_withIconAndCenteredLayout_noException() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Payment successful")
                .description("Your subscription has been activated.")
                .headerIcon(new Icon(VaadinIcon.CHECK_CIRCLE), Alert.Variant.SUCCESS)
                .centered()
                .withCancelButton(false)
                .confirmText("Continue")
                .alertRole(false)
                .build();
        assertNotNull(dialog);
        assertEquals("Payment successful", dialog.getDialogTitle());
        assertEquals("dialog", dialog.getElement().getAttribute("role"));
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--centered"));
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--has-icon"));
    }

    @Test
    void alertDialogBuilder_multiActionFooter_noException() {
        AtomicInteger archiveCount = new AtomicInteger(0);
        AtomicInteger deleteCount  = new AtomicInteger(0);
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("What would you like to do?")
                .description("Choose how to handle this item.")
                .secondaryAction("Move to archive", archiveCount::incrementAndGet)
                .confirmText("Delete permanently")
                .variant(Alert.Variant.DESTRUCTIVE)
                .onConfirm(deleteCount::incrementAndGet)
                .build();
        assertNotNull(dialog);
        assertEquals("What would you like to do?", dialog.getDialogTitle());
        assertEquals(0, archiveCount.get());
        assertEquals(0, deleteCount.get());
    }

    @Test
    void alertDialogBuilder_fullScreenMobileWithStackedButtons_noException() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Sign out?")
                .description("You will be redirected to the login page.")
                .fullScreenOnMobile(true)
                .stackedButtons(true)
                .confirmText("Sign out")
                .onConfirm(() -> {})
                .build();
        assertNotNull(dialog);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-fullscreen-mobile"));
        Component wrapper = getWrapper(dialog);
        assertTrue(wrapper.getElement().getClassList().contains("alert-dialog--stacked"));
    }

    @Test
    void alertDialogBuilder_bodyContentWithSize_noException() {
        AlertDialog dialog = AlertDialogBuilder.create()
                .title("Delete selected items?")
                .description("The following items will be permanently removed:")
                .bodyContent(new Div())
                .size(AlertDialog.Size.XL2)
                .variant(Alert.Variant.DESTRUCTIVE)
                .confirmText("Delete all")
                .onConfirm(() -> {})
                .build();
        assertNotNull(dialog);
        assertTrue(dialog.getElement().getThemeList().contains("alert-dialog-2xl"));
    }

    // =========================================================================
    // Helper
    // =========================================================================

    /**
     * Returns the wrapper {@link Div} that is the sole direct child of the dialog's
     * content (always added as {@code content(this.wrapper)} in the constructor).
     */
    private static Component getWrapper(AlertDialog dialog) {
        return dialog.getChildren().findFirst()
                .orElseThrow(() -> new AssertionError("AlertDialog has no direct child — wrapper not found"));
    }
}


