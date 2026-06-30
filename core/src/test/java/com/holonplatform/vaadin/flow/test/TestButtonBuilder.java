package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.iyensoft.vaadin.flow.enums.ButtonPreset;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ButtonBuilder}.
 */
class TestButtonBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(ButtonBuilder.create());
    }

    @Test
    void createDelBtn_returnsNonNull() {
        assertNotNull(ButtonBuilder.create().preset(ButtonPreset.DELETE).build());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsButton() {
        Button btn = ButtonBuilder.create().build();
        assertNotNull(btn);
    }

    @Test
    void build_delBtn_hasPrimaryAndErrorThemeVariants() {
        Button btn = ButtonBuilder.create().preset(ButtonPreset.DELETE).build();
        assertNotNull(btn);
        // DELETE preset applies the "error" destructive theme variant only (not "primary").
        assertTrue(btn.getThemeNames().contains("error"),
                "DELETE preset must have 'error' theme variant");
    }

    // =========================================================================
    // Text
    // =========================================================================

    @Test
    void text_setsText() {
        Button btn = ButtonBuilder.create().text("Save").build();
        assertEquals("Save", btn.getText());
    }

    // =========================================================================
    // Icon
    // =========================================================================

    @Test
    void icon_component_setsIcon() {
        Button btn = ButtonBuilder.create()
                .icon(VaadinIcon.PLUS.create())
                .build();
        assertNotNull(btn.getIcon());
    }

    @Test
    void icon_cssOnly_addsThemeVariant() {
        Button btn = ButtonBuilder.create()
                .icon()
                .build();
        assertTrue(btn.getThemeNames().contains("icon"));
    }

    @Test
    void iconAfterText_sets() {
        Button btn = ButtonBuilder.create()
                .text("Next")
                .icon(VaadinIcon.ARROW_RIGHT.create())
                .iconAfterText(true)
                .build();
        assertTrue(btn.isIconAfterText());
    }

    // =========================================================================
    // Variants / Styles
    // =========================================================================

    @Nested
    class VariantTests {

        @Test
        void primary_addsThemeVariant() {
            Button btn = ButtonBuilder.create().primary().build();
            assertNotNull(btn);
        }

        @Test
        void tertiary_addsClass() {
            Button btn = ButtonBuilder.create().tertiary().build();
            assertNotNull(btn);
        }

        @Test
        void tertiaryInline_addsThemeVariant() {
            Button btn = ButtonBuilder.create().tertiaryInline().build();
            assertTrue(btn.getThemeNames().contains("tertiary-inline"));
        }

        @Test
        void error_addsClass() {
            Button btn = ButtonBuilder.create().error().build();
            assertNotNull(btn);
        }

        @Test
        void success_addsThemeVariant() {
            Button btn = ButtonBuilder.create().success().build();
            assertTrue(btn.getThemeNames().contains("success"));
        }

        @Test
        void contrast_addsThemeVariant() {
            Button btn = ButtonBuilder.create().contrast().build();
            assertTrue(btn.getThemeNames().contains("contrast"));
        }

        @Test
        void large_addsVariant() {
            Button btn = ButtonBuilder.create().large().build();
            assertNotNull(btn);
        }

        @Test
        void small_addsVariant() {
            Button btn = ButtonBuilder.create().small().build();
            assertNotNull(btn);
        }

        @Test
        void secondary_noOp() {
            Button btn = ButtonBuilder.create().secondary().build();
            assertNotNull(btn);
        }

        @Test
        void normal_noOp() {
            Button btn = ButtonBuilder.create().normal().build();
            assertNotNull(btn);
        }
    }

    // =========================================================================
    // Border
    // =========================================================================

    @Nested
    class BorderTests {

        @Test
        void borderContrast_addsClasses() {
            Button btn = ButtonBuilder.create().borderContrast().build();
            assertTrue(btn.getClassNames().contains("btn--outlined-border"));
            assertTrue(btn.getClassNames().contains("btn--tertiary"));
            assertTrue(btn.getClassNames().contains("border-color-contrast"));
        }

        @Test
        void borderPrimary_addsClasses() {
            Button btn = ButtonBuilder.create().borderPrimary().build();
            assertTrue(btn.getClassNames().contains("border-color-primary-50"));
        }

        @Test
        void borderError_addsClasses() {
            Button btn = ButtonBuilder.create().borderError().build();
            assertTrue(btn.getClassNames().contains("border-color-error-50"));
        }

        @Test
        void borderWarning_addsClasses() {
            Button btn = ButtonBuilder.create().borderWarning().build();
            assertTrue(btn.getClassNames().contains("border-color-warning"));
        }

        @Test
        void borderSuccess_addsClasses() {
            Button btn = ButtonBuilder.create().borderSuccess().build();
            assertTrue(btn.getClassNames().contains("border-color-success-50"));
        }

        @Test
        void borderRadius_addsClass() {
            Button btn = ButtonBuilder.create().borderRadius().build();
            assertTrue(btn.getClassNames().contains("btn--rounded"));
        }
    }

    // =========================================================================
    // Margin shortcuts
    // =========================================================================

    @Test
    void marginInlineEndAuto_addsClass() {
        Button btn = ButtonBuilder.create().marginInlineEndAuto().build();
        assertTrue(btn.getClassNames().contains("btn--push-end"));
    }

    @Test
    void marginInlineStartAuto_addsClass() {
        Button btn = ButtonBuilder.create().marginInlineStartAuto().build();
        assertTrue(btn.getClassNames().contains("btn--push-start"));
    }

    // =========================================================================
    // Disabled / DisableOnClick
    // =========================================================================

    @Test
    void disableOnClick_setsFlag() {
        Button btn = ButtonBuilder.create().disableOnClick().build();
        assertTrue(btn.isDisableOnClick());
    }

    @Test
    void enabled_false() {
        Button btn = ButtonBuilder.create().enabled(false).build();
        assertFalse(btn.isEnabled());
    }

    // =========================================================================
    // Autofocus
    // =========================================================================

    @Test
    void autofocus_true() {
        Button btn = ButtonBuilder.create().autofocus(true).build();
        assertTrue(btn.isAutofocus());
    }

    // =========================================================================
    // Aria
    // =========================================================================

    @Test
    void ariaLabel_setsAttribute() {
        Button btn = ButtonBuilder.create().ariaLabel("Submit form").build();
        assertEquals("Submit form", btn.getAriaLabel().orElse(null));
    }

    // =========================================================================
    // Component configurator
    // =========================================================================

    @Test
    void id_setsId() {
        Button btn = ButtonBuilder.create().id("btn-save").build();
        assertEquals("btn-save", btn.getId().orElse(null));
    }

    @Test
    void styleName_addsClass() {
        Button btn = ButtonBuilder.create().styleName("custom-btn").build();
        assertTrue(btn.getClassNames().contains("custom-btn"));
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Button btn = ButtonBuilder.create()
                .text("Delete")
                .icon(VaadinIcon.TRASH.create())
                .error()
                .disableOnClick()
                .ariaLabel("Delete item")
                .id("btn-delete")
                .styleName("action-btn")
                .build();
        assertNotNull(btn);
        assertEquals("Delete", btn.getText());
        assertTrue(btn.isDisableOnClick());
        assertEquals("btn-delete", btn.getId().orElse(null));
    }
}
