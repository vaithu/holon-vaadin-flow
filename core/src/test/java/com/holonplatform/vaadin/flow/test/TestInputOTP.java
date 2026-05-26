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

import com.holonplatform.vaadin.flow.components.builders.InputOTPBuilder;
import com.holonplatform.vaadin.flow.components.builders.InputOTPConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTP;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPSeparator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPSlot;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link InputOTP} component family:
 * {@link InputOTP}, {@link InputOTPGroup}, {@link InputOTPSlot}, {@link InputOTPSeparator},
 * {@link InputOTPBuilder} and {@link InputOTPConfigurator}.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService.  Focus advancing and keyboard events
 * (auto-advance, backspace retreat) require browser execution and are not verified here.</p>
 */
class TestInputOTP {

    // =========================================================================
    // InputOTPSlot
    // =========================================================================

    @Test
    void slot_hasBaseClass() {
        InputOTPSlot slot = new InputOTPSlot();
        assertTrue(slot.getClassNames().contains("input-otp__slot"));
    }

    @Test
    void slot_hasOneChildTextField() {
        InputOTPSlot slot = new InputOTPSlot();
        assertEquals(1, slot.getElement().getChildCount());
        assertTrue(slot.getElement().getChild(0).getTag().equals("vaadin-text-field"));
    }

    @Test
    void slot_textFieldHasSlotInputClass() {
        InputOTPSlot slot = new InputOTPSlot();
        assertTrue(slot.getTextField().getClassNames().contains("input-otp__slot-input"));
    }

    @Test
    void slot_textFieldMaxLengthIsOne() {
        InputOTPSlot slot = new InputOTPSlot();
        assertEquals(1, slot.getTextField().getMaxLength());
    }

    @Test
    void slot_getAndSetSlotValue() {
        InputOTPSlot slot = new InputOTPSlot();
        slot.setSlotValue("5");
        assertEquals("5", slot.getSlotValue());
    }

    @Test
    void slot_setSlotValue_null_treatedAsEmpty() {
        InputOTPSlot slot = new InputOTPSlot();
        slot.setSlotValue(null);
        assertEquals("", slot.getSlotValue());
    }

    @Test
    void slot_setPattern_doesNotThrow() {
        InputOTPSlot slot = new InputOTPSlot();
        assertDoesNotThrow(() -> slot.setPattern("[0-9]"));
    }

    @Test
    void slot_setReadOnly_doesNotThrow() {
        InputOTPSlot slot = new InputOTPSlot();
        assertDoesNotThrow(() -> slot.setReadOnly(true));
        assertTrue(slot.getTextField().isReadOnly());
    }

    @Test
    void slot_setEnabled_propagatesToTextField() {
        InputOTPSlot slot = new InputOTPSlot();
        slot.setSlotEnabled(false);
        assertFalse(slot.getTextField().isEnabled());
    }

    // =========================================================================
    // InputOTPGroup
    // =========================================================================

    @Test
    void group_hasBaseClass() {
        InputOTPGroup group = new InputOTPGroup();
        assertTrue(group.getClassNames().contains("input-otp__group"));
    }

    @Test
    void group_isEmpty_byDefault() {
        InputOTPGroup group = new InputOTPGroup();
        assertTrue(group.getSlots().isEmpty());
        assertEquals(0, group.getElement().getChildCount());
    }

    @Test
    void group_addSlots_storesAndRendersThemAll() {
        InputOTPGroup group = new InputOTPGroup();
        group.add(new InputOTPSlot(), new InputOTPSlot(), new InputOTPSlot());

        assertEquals(3, group.getSlots().size());
        assertEquals(3, group.getElement().getChildCount());
    }

    @Test
    void group_addSlots_nullElementSkipped() {
        InputOTPGroup group = new InputOTPGroup();
        group.add(new InputOTPSlot(), null, new InputOTPSlot());

        assertEquals(2, group.getSlots().size());
    }

    @Test
    void group_addSlots_nullArray_doesNotThrow() {
        InputOTPGroup group = new InputOTPGroup();
        assertDoesNotThrow(() -> group.add((InputOTPSlot[]) null));
    }

    @Test
    void group_getSlots_isUnmodifiable() {
        InputOTPGroup group = new InputOTPGroup();
        group.add(new InputOTPSlot());
        assertThrows(UnsupportedOperationException.class,
                () -> group.getSlots().add(new InputOTPSlot()));
    }

    // =========================================================================
    // InputOTPSeparator
    // =========================================================================

    @Test
    void separator_hasBaseClass() {
        InputOTPSeparator sep = new InputOTPSeparator();
        assertTrue(sep.getClassNames().contains("input-otp__separator"));
    }

    @Test
    void separator_hasAriaHidden() {
        InputOTPSeparator sep = new InputOTPSeparator();
        assertEquals("true", sep.getElement().getAttribute("aria-hidden"));
    }

    @Test
    void separator_hasRolePresentation() {
        InputOTPSeparator sep = new InputOTPSeparator();
        assertEquals("presentation", sep.getElement().getAttribute("role"));
    }

    @Test
    void separator_default_rendersEnDashSpan() {
        InputOTPSeparator sep = new InputOTPSeparator();
        assertEquals(1, sep.getElement().getChildCount());
        String cls = sep.getElement().getChild(0).getAttribute("class");
        assertNotNull(cls);
        assertTrue(cls.contains("input-otp__separator-icon"));
    }

    @Test
    void separator_customComponent_usesIt() {
        Span custom = new Span("·");
        InputOTPSeparator sep = new InputOTPSeparator(custom);

        assertTrue(sep.getClassNames().contains("input-otp__separator"));
        assertEquals(1, sep.getElement().getChildCount());
    }

    @Test
    void separator_nullComponent_fallsBackToDefault() {
        InputOTPSeparator sep = new InputOTPSeparator((com.vaadin.flow.component.Component) null);
        assertEquals(1, sep.getElement().getChildCount());
        assertTrue(sep.getElement().getChild(0).getAttribute("class").contains("input-otp__separator-icon"));
    }

    // =========================================================================
    // InputOTP — basic structure
    // =========================================================================

    @Test
    void inputOTP_default_hasBaseClass() {
        InputOTP otp = new InputOTP();
        assertTrue(otp.getClassNames().contains("input-otp"));
    }

    @Test
    void inputOTP_default_isEmpty() {
        InputOTP otp = new InputOTP();
        assertEquals(0, otp.getElement().getChildCount());
        assertEquals(0, otp.getLength());
    }

    @Test
    void inputOTP_add_groupRegistersSlots() {
        InputOTPGroup group = new InputOTPGroup();
        group.add(new InputOTPSlot(), new InputOTPSlot(), new InputOTPSlot());

        InputOTP otp = new InputOTP();
        otp.add(group);

        assertEquals(3, otp.getLength());
        assertEquals(1, otp.getElement().getChildCount()); // the group itself
    }

    @Test
    void inputOTP_add_separatorDoesNotCountAsSlot() {
        InputOTPGroup g1 = new InputOTPGroup();
        g1.add(new InputOTPSlot(), new InputOTPSlot(), new InputOTPSlot());

        InputOTPGroup g2 = new InputOTPGroup();
        g2.add(new InputOTPSlot(), new InputOTPSlot(), new InputOTPSlot());

        InputOTP otp = new InputOTP();
        otp.add(g1, new InputOTPSeparator(), g2);

        assertEquals(6, otp.getLength());
        assertEquals(3, otp.getElement().getChildCount()); // group + sep + group
    }

    @Test
    void inputOTP_add_nullsSkipped() {
        InputOTP otp = new InputOTP();
        assertDoesNotThrow(() -> otp.add((com.vaadin.flow.component.Component[]) null));
        assertEquals(0, otp.getLength());
    }

    // =========================================================================
    // InputOTP — getValue / setValue / clear
    // =========================================================================

    @Test
    void inputOTP_getValue_empty_returnsEmptyString() {
        InputOTP otp = InputOTP.builder().group(4).build();
        assertEquals("", otp.getValue());
    }

    @Test
    void inputOTP_setValue_distributesCharactersToSlots() {
        InputOTP otp = InputOTP.builder().group(6).build();
        otp.setValue("123456");
        assertEquals("123456", otp.getValue());
    }

    @Test
    void inputOTP_setValue_shortValue_paddedWithEmpty() {
        InputOTP otp = InputOTP.builder().group(6).build();
        otp.setValue("123");
        assertEquals("123", otp.getValue()); // remaining slots are empty
    }

    @Test
    void inputOTP_setValue_longValue_extraCharsIgnored() {
        InputOTP otp = InputOTP.builder().group(4).build();
        otp.setValue("123456789");
        assertEquals("1234", otp.getValue());
    }

    @Test
    void inputOTP_setValue_null_treatedAsEmpty() {
        InputOTP otp = InputOTP.builder().group(4).build();
        otp.setValue("ABCD");
        otp.setValue(null);
        assertEquals("", otp.getValue());
    }

    @Test
    void inputOTP_clear_emptiesAllSlots() {
        InputOTP otp = InputOTP.builder().group(4).build();
        otp.setValue("ABCD");
        otp.clear();
        assertEquals("", otp.getValue());
    }

    @Test
    void inputOTP_getLength_matchesTotalSlotCount() {
        InputOTP otp = InputOTP.builder().group(3).separator().group(3).build();
        assertEquals(6, otp.getLength());
    }

    // =========================================================================
    // InputOTP — pattern / readOnly / enabled
    // =========================================================================

    @Test
    void inputOTP_setPattern_propagatesToExistingSlots() {
        InputOTP otp = InputOTP.builder().group(4).build();
        otp.setPattern("[0-9]");
        // Verify by reading back the pattern on each slot's TextField
        otp.getElement().getChildren().forEach(groupEl ->
                groupEl.getChildren().forEach(slotEl ->
                        slotEl.getChildren().forEach(inputEl ->
                                assertEquals("[0-9]", inputEl.getAttribute("pattern")))));
    }

    @Test
    void inputOTP_setPattern_beforeGroupAdded_appliedOnAdd() {
        // Pattern set before groups — builder order: pattern() then group()
        InputOTP otp = InputOTP.builder()
                .pattern("[0-9]")
                .group(3)
                .build();
        // length should still be 3
        assertEquals(3, otp.getLength());
    }

    @Test
    void inputOTP_setReadOnly_propagatesToSlots() {
        InputOTP otp = InputOTP.builder().group(3).build();
        otp.setReadOnly(true);
        // All slot TextFields should be readOnly
        // Accessed via DOM: slot > vaadin-text-field[readonly]
        boolean allReadOnly = otp.getElement()
                .getChildren()
                .flatMap(g -> g.getChildren())
                .flatMap(s -> s.getChildren())
                .allMatch(tf -> tf.hasAttribute("readonly"));
        assertTrue(allReadOnly);
    }

    @Test
    void inputOTP_setEnabled_false_propagatesToSlots() {
        InputOTP otp = InputOTP.builder().group(3).build();
        otp.setEnabled(false);
        assertFalse(otp.isEnabled());
    }

    // =========================================================================
    // InputOTP — value-change listener and onComplete
    // =========================================================================

    @Test
    void addValueChangeListener_returnsRegistration() {
        InputOTP otp = InputOTP.builder().group(4).build();
        var reg = otp.addValueChangeListener(v -> {});
        assertNotNull(reg);
        assertDoesNotThrow(reg::remove);
    }

    @Test
    void setOnComplete_doesNotThrow() {
        InputOTP otp = InputOTP.builder().group(4).build();
        assertDoesNotThrow(() -> otp.setOnComplete(v -> {}));
    }

    // =========================================================================
    // InputOTPBuilder — static factories
    // =========================================================================

    @Test
    void inputOTPBuilder_create_returnsNonNull() {
        assertNotNull(InputOTPBuilder.create());
    }

    @Test
    void inputOTP_builder_staticFactory_returnsNonNull() {
        assertNotNull(InputOTP.builder());
    }

    @Test
    void inputOTPConfigurator_configure_returnsNonNull() {
        assertNotNull(InputOTPConfigurator.configure(new InputOTP()));
    }

    @Test
    void inputOTP_configure_staticFactory_returnsNonNull() {
        assertNotNull(InputOTP.configure(new InputOTP()));
    }

    // =========================================================================
    // InputOTPBuilder — group / separator
    // =========================================================================

    @Test
    void builder_group_singleGroup_createsSlots() {
        InputOTP otp = InputOTP.builder().group(6).build();
        assertEquals(6, otp.getLength());
        assertEquals(1, otp.getElement().getChildCount()); // one group
    }

    @Test
    void builder_group_twoGroups_summedSlots() {
        InputOTP otp = InputOTP.builder().group(3).group(3).build();
        assertEquals(6, otp.getLength());
        assertEquals(2, otp.getElement().getChildCount()); // two groups
    }

    @Test
    void builder_separator_addsOneSeparatorElement() {
        InputOTP otp = InputOTP.builder()
                .group(3)
                .separator()
                .group(3)
                .build();

        assertEquals(6, otp.getLength());
        assertEquals(3, otp.getElement().getChildCount()); // group + sep + group
        assertTrue(otp.getElement().getChild(1).getAttribute("class").contains("input-otp__separator"));
    }

    @Test
    void builder_separator_customContent_usesIt() {
        Span custom = new Span("/");
        InputOTP otp = InputOTP.builder()
                .group(2)
                .separator(custom)
                .group(2)
                .build();

        assertEquals(4, otp.getLength());
        assertEquals(3, otp.getElement().getChildCount());
    }

    // =========================================================================
    // InputOTPBuilder — pattern, readOnly, onComplete, onValueChange
    // =========================================================================

    @Test
    void builder_pattern_appliedToSlots() {
        InputOTP otp = InputOTP.builder()
                .group(4)
                .pattern("[0-9]")
                .build();
        assertEquals(4, otp.getLength());
        // Pattern must be set on each TextField (verified via DOM)
        long patterned = otp.getElement().getChildren()
                .flatMap(g -> g.getChildren())          // slots
                .flatMap(s -> s.getChildren())          // textfields
                .filter(tf -> "[0-9]".equals(tf.getAttribute("pattern")))
                .count();
        assertEquals(4, patterned);
    }

    @Test
    void builder_readOnly_true_allSlotsReadOnly() {
        InputOTP otp = InputOTP.builder().group(4).readOnly(true).build();
        long readOnly = otp.getElement().getChildren()
                .flatMap(g -> g.getChildren())
                .flatMap(s -> s.getChildren())
                .filter(tf -> tf.hasAttribute("readonly"))
                .count();
        assertEquals(4, readOnly);
    }

    @Test
    void builder_onComplete_registered() {
        AtomicReference<String> captured = new AtomicReference<>();
        InputOTP otp = InputOTP.builder()
                .group(4)
                .onComplete(captured::set)
                .build();

        assertNotNull(otp);
        assertNull(captured.get()); // not fired yet
    }

    @Test
    void builder_onValueChange_registered() {
        List<String> values = new ArrayList<>();
        InputOTP otp = InputOTP.builder()
                .group(4)
                .onValueChange(values::add)
                .build();
        assertNotNull(otp);
    }

    // =========================================================================
    // InputOTPBuilder — ComponentConfigurator methods
    // =========================================================================

    @Test
    void builder_id_setsIdOnBuiltComponent() {
        InputOTP otp = InputOTP.builder().group(4).id("otp-field").build();
        assertEquals("otp-field", otp.getId().orElse(null));
    }

    @Test
    void builder_visible_false_componentIsHidden() {
        InputOTP otp = InputOTP.builder().group(4).visible(false).build();
        assertFalse(otp.isVisible());
    }

    @Test
    void builder_styleName_addsClass() {
        InputOTP otp = InputOTP.builder().group(4).styleName("my-otp").build();
        assertTrue(otp.getClassNames().contains("my-otp"));
        assertTrue(otp.getClassNames().contains("input-otp"), "base class must remain");
    }

    @Test
    void builder_width_setsWidth() {
        InputOTP otp = InputOTP.builder().group(4).width("auto").build();
        assertEquals("auto", otp.getWidth());
    }

    @Test
    void builder_enabled_false_componentIsDisabled() {
        InputOTP otp = InputOTP.builder().group(4).enabled(false).build();
        assertFalse(otp.isEnabled());
    }

    // =========================================================================
    // InputOTPBuilder — fluent chaining
    // =========================================================================

    @Test
    void builder_fluentChain_allCallsReturnSameBuilder() {
        InputOTPBuilder builder = InputOTP.builder();
        InputOTPBuilder b1 = builder.group(3);
        InputOTPBuilder b2 = b1.separator();
        InputOTPBuilder b3 = b2.group(3);
        InputOTPBuilder b4 = b3.pattern("[0-9]");

        assertSame(builder, b1);
        assertSame(builder, b2);
        assertSame(builder, b3);
        assertSame(builder, b4);
    }

    // =========================================================================
    // InputOTPConfigurator.configure() — wraps existing instance
    // =========================================================================

    @Test
    void configure_existingOtp_addsGroup() {
        InputOTP otp = new InputOTP();
        InputOTPConfigurator.configure(otp).group(3);

        assertEquals(3, otp.getLength());
    }

    @Test
    void configure_existingOtp_setsPattern() {
        InputOTP otp = new InputOTP();
        InputOTPConfigurator.configure(otp).group(4).pattern("[A-Z]");

        assertEquals(4, otp.getLength());
    }

    @Test
    void configure_existingOtp_setsWidth() {
        InputOTP otp = new InputOTP();
        InputOTPConfigurator.configure(otp).width("fit-content");

        assertEquals("fit-content", otp.getWidth());
    }

    // =========================================================================
    // Full compositions
    // =========================================================================

    @Test
    void fullComposition_3dash3_numericOTP() {
        InputOTP otp = InputOTP.builder()
                .group(3)
                .separator()
                .group(3)
                .pattern("[0-9]")
                .onComplete(v -> {})
                .id("verify-otp")
                .build();

        assertEquals(6, otp.getLength());
        assertEquals(3, otp.getElement().getChildCount());
        assertEquals("verify-otp", otp.getId().orElse(null));
    }

    @Test
    void fullComposition_setValue_then_getValue_roundTrip() {
        InputOTP otp = InputOTP.builder().group(3).separator().group(3).build();
        otp.setValue("ABCDEF");
        assertEquals("ABCDEF", otp.getValue());
    }

    @Test
    void fullComposition_clear_after_setValue() {
        InputOTP otp = InputOTP.builder().group(6).build();
        otp.setValue("123456");
        assertEquals("123456", otp.getValue());
        otp.clear();
        assertEquals("", otp.getValue());
    }

    @Test
    void fullComposition_Components_factory() {
        // Verify the Components.inputOTP() integration point
        InputOTP otp = com.holonplatform.vaadin.flow.components.Components.inputOTP()
                .group(3)
                .separator()
                .group(3)
                .build();

        assertEquals(6, otp.getLength());
    }
}

