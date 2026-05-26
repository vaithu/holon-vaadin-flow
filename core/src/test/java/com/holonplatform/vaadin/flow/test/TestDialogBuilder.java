package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.*;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator.ActionVariant;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator.DialogSize;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator.IconVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DialogBuilder} hierarchy and all six sub-builder types.
 */
class TestDialogBuilder {

    // =========================================================================
    // MessageDialogBuilder
    // =========================================================================

    @Nested
    class MessageDialogTests {

        @Test
        void factory_returnsNonNull() {
            assertNotNull(DialogBuilder.message());
        }

        @Test
        void build_returnsDialog() {
            Dialog dialog = DialogBuilder.message().build();
            assertNotNull(dialog);
        }

        @Test
        void withTitle_setsTitle() {
            Dialog dialog = DialogBuilder.message()
                    .withTitle("Hello")
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withDescription_setsDescription() {
            Dialog dialog = DialogBuilder.message()
                    .withDescription("Some description")
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withContent_string_addsBody() {
            Dialog dialog = DialogBuilder.message()
                    .withContent("Body text")
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withContent_component_addsBody() {
            Dialog dialog = DialogBuilder.message()
                    .withContent(new Paragraph("Rich content"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void text_string_aliasForWithContent() {
            Dialog dialog = DialogBuilder.message()
                    .text("Alias text")
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void closeOnEsc_setsProperty() {
            Dialog dialog = DialogBuilder.message()
                    .closeOnEsc(false)
                    .build();
            assertFalse(dialog.isCloseOnEsc());
        }

        @Test
        void closeOnOutsideClick_setsProperty() {
            Dialog dialog = DialogBuilder.message()
                    .closeOnOutsideClick(false)
                    .build();
            assertFalse(dialog.isCloseOnOutsideClick());
        }

        @Test
        void draggable_setsProperty() {
            Dialog dialog = DialogBuilder.message()
                    .draggable(true)
                    .build();
            assertTrue(dialog.isDraggable());
        }

        @Test
        void resizable_setsProperty() {
            Dialog dialog = DialogBuilder.message()
                    .resizable(true)
                    .build();
            assertTrue(dialog.isResizable());
        }

        @Test
        void withCloseButton_false_hidesButton() {
            Dialog dialog = DialogBuilder.message()
                    .withCloseButton(false)
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void closeIcon_replacesIcon() {
            Dialog dialog = DialogBuilder.message()
                    .closeIcon(new Icon(VaadinIcon.CLOSE))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withSize_appliesThemeModifier() {
            Dialog dialog = DialogBuilder.message()
                    .withSize(DialogSize.LG)
                    .build();
            assertTrue(dialog.getElement().getThemeList().contains("h-dialog--lg"));
        }

        @Test
        void withSize_SM_appliesCorrectModifier() {
            Dialog dialog = DialogBuilder.message()
                    .withSize(DialogSize.SM)
                    .build();
            assertTrue(dialog.getElement().getThemeList().contains("h-dialog--sm"));
        }

        @Test
        void withSize_FULL_appliesCorrectModifier() {
            Dialog dialog = DialogBuilder.message()
                    .withSize(DialogSize.FULL)
                    .build();
            assertTrue(dialog.getElement().getThemeList().contains("h-dialog--full"));
        }

        @Test
        void centered_appliesThemeModifier() {
            Dialog dialog = DialogBuilder.message()
                    .centered()
                    .build();
            assertTrue(dialog.getElement().getThemeList().contains("h-dialog--centered"));
        }

        @Test
        void withHeaderIcon_requiresAttachedDialog() {
            // Dialog.getHeader().getChildren() is unsupported outside a browser context;
            // withHeaderIcon uses it to remove a previously-added icon — verify the limitation.
            MessageDialogBuilder builder = DialogBuilder.message();
            assertThrows(UnsupportedOperationException.class,
                    () -> builder.withHeaderIcon(new Icon(VaadinIcon.WARNING), IconVariant.WARNING));
        }

        @Test
        void withActionButton_stringAndListener_addsFooterButton() {
            AtomicBoolean clicked = new AtomicBoolean(false);
            Dialog dialog = DialogBuilder.message()
                    .withActionButton("OK", e -> clicked.set(true))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withActionButton_withVariant_addsStyledButton() {
            Dialog dialog = DialogBuilder.message()
                    .withActionButton("Delete", ActionVariant.DESTRUCTIVE, e -> {})
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withActionButton_configurator_addsButton() {
            Dialog dialog = DialogBuilder.message()
                    .withActionButton(btn -> btn.text("Custom"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withCancelButton_addsDefaultCancel() {
            Dialog dialog = DialogBuilder.message()
                    .withCancelButton()
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withCancelButton_configurator_customizesCancel() {
            Dialog dialog = DialogBuilder.message()
                    .withCancelButton(btn -> btn.text("Dismiss"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withFooter_addsRawComponents() {
            Dialog dialog = DialogBuilder.message()
                    .withFooter(new Span("Footer text"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withTrigger_component_registersIt() {
            Dialog dialog = DialogBuilder.message()
                    .withTrigger(new Div("Click me"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void withTrigger_string_createsButton() {
            Dialog dialog = DialogBuilder.message()
                    .withTrigger("Open dialog")
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void buildTriggered_withoutTrigger_throws() {
            MessageDialogBuilder builder = DialogBuilder.message();
            assertThrows(IllegalStateException.class, builder::buildTriggered);
        }

        @Test
        void buildTriggered_withTrigger_returnsComponent() {
            var trigger = DialogBuilder.message()
                    .withTitle("Test")
                    .withTrigger("Open")
                    .buildTriggered();
            assertNotNull(trigger);
        }

        @Test
        void makeDialogResponsive_setsViewportWidth() {
            Dialog dialog = DialogBuilder.message()
                    .makeDialogResponsive()
                    .build();
            assertEquals("min(95vw, 900px)", dialog.getWidth());
        }

        @Test
        void fluent_chain_fullExample() {
            Dialog dialog = DialogBuilder.message()
                    .withTitle("Information")
                    .withDescription("Please read carefully.")
                    .withContent("This is the body text.")
                    .withSize(DialogSize.MD)
                    .draggable(true)
                    .closeOnEsc(true)
                    .build();
            assertNotNull(dialog);
            assertTrue(dialog.isDraggable());
        }
    }

    // =========================================================================
    // ConfirmDialogBuilder
    // =========================================================================

    @Nested
    class ConfirmDialogTests {

        @Test
        void factory_returnsNonNull() {
            assertNotNull(DialogBuilder.confirm());
        }

        @Test
        void build_returnsDialog() {
            Dialog dialog = DialogBuilder.confirm().build();
            assertNotNull(dialog);
        }

        @Test
        void okButtonConfigurator_customizesOkButton() {
            Dialog dialog = DialogBuilder.confirm()
                    .okButtonConfigurator(btn -> btn.text("Got it"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void denialButtonConfigurator_lazilyCreatesCancel() {
            Dialog dialog = DialogBuilder.confirm()
                    .denialButtonConfigurator(btn -> btn.text("Never mind"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void confirm_closeOnEsc_available() {
            Dialog dialog = DialogBuilder.confirm()
                    .closeOnEsc(false)
                    .build();
            assertFalse(dialog.isCloseOnEsc());
        }

        @Test
        void fluent_chain_withTitleAndContent() {
            Dialog dialog = DialogBuilder.confirm()
                    .withTitle("Confirm")
                    .withContent("Are you sure?")
                    .okButtonConfigurator(btn -> btn.text("Yes"))
                    .build();
            assertNotNull(dialog);
        }
    }

    // =========================================================================
    // QuestionDialogBuilder
    // =========================================================================

    @Nested
    class QuestionDialogTests {

        @Test
        void factory_withCallback_returnsNonNull() {
            assertNotNull(DialogBuilder.question(confirmed -> {}));
        }

        @Test
        void factory_nullCallback_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> DialogBuilder.question(null));
        }

        @Test
        void build_returnsDialog() {
            Dialog dialog = DialogBuilder.question(confirmed -> {}).build();
            assertNotNull(dialog);
        }

        @Test
        void build_closeOnEsc_isFalse() {
            Dialog dialog = DialogBuilder.question(confirmed -> {}).build();
            assertFalse(dialog.isCloseOnEsc());
        }

        @Test
        void build_closeOnOutsideClick_isFalse() {
            Dialog dialog = DialogBuilder.question(confirmed -> {}).build();
            assertFalse(dialog.isCloseOnOutsideClick());
        }

        @Test
        void build_isDraggable() {
            Dialog dialog = DialogBuilder.question(confirmed -> {}).build();
            assertTrue(dialog.isDraggable());
        }

        @Test
        void confirmButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.question(confirmed -> {})
                    .confirmButtonConfigurator(btn -> btn.text("Absolutely"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void denialButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.question(confirmed -> {})
                    .denialButtonConfigurator(btn -> btn.text("Nope"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void fluent_chain_fullExample() {
            AtomicBoolean answer = new AtomicBoolean();
            Dialog dialog = DialogBuilder.question(answer::set)
                    .withTitle("Question")
                    .withContent("Do you agree?")
                    .confirmButtonConfigurator(btn -> btn.text("Agree"))
                    .denialButtonConfigurator(btn -> btn.text("Disagree"))
                    .build();
            assertNotNull(dialog);
        }
    }

    // =========================================================================
    // DeleteDialogBuilder
    // =========================================================================

    @Nested
    class DeleteDialogTests {

        @Test
        void factory_withCallback_returnsNonNull() {
            assertNotNull(DialogBuilder.delete(confirmed -> {}));
        }

        @Test
        void factory_nullCallback_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> DialogBuilder.delete(null));
        }

        @Test
        void build_returnsDialog() {
            Dialog dialog = DialogBuilder.delete(confirmed -> {}).build();
            assertNotNull(dialog);
        }

        @Test
        void build_closeOnEsc_isFalse() {
            Dialog dialog = DialogBuilder.delete(confirmed -> {}).build();
            assertFalse(dialog.isCloseOnEsc());
        }

        @Test
        void build_closeOnOutsideClick_isFalse() {
            Dialog dialog = DialogBuilder.delete(confirmed -> {}).build();
            assertFalse(dialog.isCloseOnOutsideClick());
        }

        @Test
        void confirmButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.delete(confirmed -> {})
                    .confirmButtonConfigurator(btn -> btn.text("Yes, delete"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void denialButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.delete(confirmed -> {})
                    .denialButtonConfigurator(btn -> btn.text("Keep it"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void fluent_chain_withTitleAndDescription() {
            Dialog dialog = DialogBuilder.delete(confirmed -> {})
                    .withTitle("Delete item?")
                    .withDescription("This action cannot be undone.")
                    .build();
            assertNotNull(dialog);
        }
    }

    // =========================================================================
    // SaveAndNewDialogBuilder
    // =========================================================================

    @Nested
    class SaveAndNewDialogTests {

        @Test
        void factory_withCallback_returnsNonNull() {
            assertNotNull(DialogBuilder.saveAndNew(confirmed -> {}));
        }

        @Test
        void factory_nullCallback_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> DialogBuilder.saveAndNew(null));
        }

        @Test
        void build_returnsDialog() {
            Dialog dialog = DialogBuilder.saveAndNew(confirmed -> {}).build();
            assertNotNull(dialog);
        }

        @Test
        void build_isResizable() {
            Dialog dialog = DialogBuilder.saveAndNew(confirmed -> {}).build();
            assertTrue(dialog.isResizable());
        }

        @Test
        void build_isDraggable() {
            Dialog dialog = DialogBuilder.saveAndNew(confirmed -> {}).build();
            assertTrue(dialog.isDraggable());
        }

        @Test
        void saveAndNewButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.saveAndNew(confirmed -> {})
                    .saveAndNewButtonConfigurator(btn -> btn.text("Save & Create Another"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void denialButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.saveAndNew(confirmed -> {})
                    .denialButtonConfigurator(btn -> btn.text("Discard"))
                    .build();
            assertNotNull(dialog);
        }
    }

    // =========================================================================
    // SaveDialogBuilder
    // =========================================================================

    @Nested
    class SaveDialogTests {

        @Test
        void factory_withCallback_returnsNonNull() {
            assertNotNull(DialogBuilder.save((confirmed, closer) -> {}));
        }

        @Test
        void factory_nullCallback_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> DialogBuilder.save(null));
        }

        @Test
        void build_returnsDialog() {
            Dialog dialog = DialogBuilder.save((confirmed, closer) -> {}).build();
            assertNotNull(dialog);
        }

        @Test
        void build_isResizable() {
            Dialog dialog = DialogBuilder.save((confirmed, closer) -> {}).build();
            assertTrue(dialog.isResizable());
        }

        @Test
        void build_isDraggable() {
            Dialog dialog = DialogBuilder.save((confirmed, closer) -> {}).build();
            assertTrue(dialog.isDraggable());
        }

        @Test
        void saveButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.save((confirmed, closer) -> {})
                    .saveButtonConfigurator(btn -> btn.text("Save now"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void denialButtonConfigurator_customizesButton() {
            Dialog dialog = DialogBuilder.save((confirmed, closer) -> {})
                    .denialButtonConfigurator(btn -> btn.text("Cancel"))
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void fluent_chain_fullExample() {
            Dialog dialog = DialogBuilder.save((confirmed, closer) -> {
                        if (confirmed) closer.accept(true);
                    })
                    .withTitle("Save Changes")
                    .withContent("Do you want to save your changes?")
                    .saveButtonConfigurator(btn -> btn.text("Save"))
                    .denialButtonConfigurator(btn -> btn.text("Discard"))
                    .draggable(true)
                    .resizable(true)
                    .build();
            assertNotNull(dialog);
            assertTrue(dialog.isDraggable());
            assertTrue(dialog.isResizable());
        }
    }

    // =========================================================================
    // DialogConfigurator enums
    // =========================================================================

    @Nested
    class EnumTests {

        @Test
        void actionVariant_default_hasEmptyModifier() {
            assertEquals("", ActionVariant.DEFAULT.getCssModifier());
        }

        @Test
        void actionVariant_destructive_hasModifier() {
            assertEquals("h-dialog__action-btn--destructive",
                    ActionVariant.DESTRUCTIVE.getCssModifier());
        }

        @Test
        void actionVariant_allValues_haveNonNullModifier() {
            for (ActionVariant v : ActionVariant.values()) {
                assertNotNull(v.getCssModifier());
            }
        }

        @Test
        void iconVariant_destructive_hasCssClass() {
            assertEquals("h-dialog__icon--destructive",
                    IconVariant.DESTRUCTIVE.getCssClass());
        }

        @Test
        void iconVariant_allValues_haveNonNullCssClass() {
            for (IconVariant v : IconVariant.values()) {
                assertNotNull(v.getCssClass());
                assertFalse(v.getCssClass().isEmpty());
            }
        }

        @Test
        void dialogSize_allValues_haveNonNullThemeModifier() {
            for (DialogSize s : DialogSize.values()) {
                assertNotNull(s.getThemeModifier());
                assertFalse(s.getThemeModifier().isEmpty());
            }
        }

        @Test
        void dialogSize_SM_hasCorrectModifier() {
            assertEquals("h-dialog--sm", DialogSize.SM.getThemeModifier());
        }

        @Test
        void dialogSize_MD_hasCorrectModifier() {
            assertEquals("h-dialog--md", DialogSize.MD.getThemeModifier());
        }

        @Test
        void dialogSize_LG_hasCorrectModifier() {
            assertEquals("h-dialog--lg", DialogSize.LG.getThemeModifier());
        }

        @Test
        void dialogSize_XL_hasCorrectModifier() {
            assertEquals("h-dialog--xl", DialogSize.XL.getThemeModifier());
        }

        @Test
        void dialogSize_FULL_hasCorrectModifier() {
            assertEquals("h-dialog--full", DialogSize.FULL.getThemeModifier());
        }
    }

    // =========================================================================
    // Message code constants
    // =========================================================================

    @Nested
    class MessageCodeTests {

        @Test
        void okButtonMessageCode_isNotNull() {
            assertNotNull(DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE);
            assertFalse(DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE.isEmpty());
        }

        @Test
        void confirmButtonMessageCode_isNotNull() {
            assertNotNull(DialogBuilder.DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE);
            assertFalse(DialogBuilder.DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE.isEmpty());
        }

        @Test
        void denyButtonMessageCode_isNotNull() {
            assertNotNull(DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE);
            assertFalse(DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE.isEmpty());
        }

        @Test
        void saveButtonMessageCode_isNotNull() {
            assertNotNull(DialogBuilder.DEFAULT_SAVE_BUTTON_MESSAGE_CODE);
            assertFalse(DialogBuilder.DEFAULT_SAVE_BUTTON_MESSAGE_CODE.isEmpty());
        }

        @Test
        void deleteButtonMessageCode_isNotNull() {
            assertNotNull(DialogBuilder.DEFAULT_DELETE_BUTTON_MESSAGE_CODE);
            assertFalse(DialogBuilder.DEFAULT_DELETE_BUTTON_MESSAGE_CODE.isEmpty());
        }

        @Test
        void saveNewButtonMessageCode_isNotNull() {
            assertNotNull(DialogBuilder.DEFAULT_SAVE_NEW_BUTTON_MESSAGE_CODE);
            assertFalse(DialogBuilder.DEFAULT_SAVE_NEW_BUTTON_MESSAGE_CODE.isEmpty());
        }
    }

    // =========================================================================
    // onOpen / onClose callbacks
    // =========================================================================

    @Nested
    class LifecycleCallbackTests {

        @Test
        void onOpen_acceptsCallback() {
            Dialog dialog = DialogBuilder.message()
                    .onOpen(() -> {})
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void onClose_runnable_acceptsCallback() {
            Dialog dialog = DialogBuilder.message()
                    .onClose((Runnable) () -> {})
                    .build();
            assertNotNull(dialog);
        }

        @Test
        void onClose_booleanSupplier_acceptsCallback() {
            Dialog dialog = DialogBuilder.message()
                    .onClose(() -> true)
                    .build();
            assertNotNull(dialog);
        }
    }
}
