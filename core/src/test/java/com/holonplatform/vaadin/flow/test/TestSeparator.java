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

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.SeparatorBuilder;
import com.holonplatform.vaadin.flow.components.builders.SeparatorConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Separator} component family:
 * {@link Separator}, {@link SeparatorBuilder} and {@link SeparatorConfigurator}.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService.</p>
 */
class TestSeparator {

    // =========================================================================
    // Separator — construction defaults
    // =========================================================================

    @Test
    void separator_default_hasBaseClass() {
        Separator sep = new Separator();
        assertTrue(sep.getClassNames().contains("separator"));
    }

    @Test
    void separator_default_hasHorizontalClass() {
        Separator sep = new Separator();
        assertTrue(sep.getClassNames().contains("separator--horizontal"));
    }

    @Test
    void separator_default_orientation_isHorizontal() {
        Separator sep = new Separator();
        assertEquals(Separator.Orientation.HORIZONTAL, sep.getOrientation());
    }

    @Test
    void separator_default_isNotDecorative() {
        Separator sep = new Separator();
        assertFalse(sep.isDecorative());
    }

    // =========================================================================
    // Separator — role and aria attributes (meaningful / non-decorative)
    // =========================================================================

    @Test
    void separator_meaningful_hasRoleSeparator() {
        Separator sep = new Separator();
        assertEquals("separator", sep.getElement().getAttribute("role"));
    }

    @Test
    void separator_meaningful_hasAriaOrientationHorizontal() {
        Separator sep = new Separator();
        assertEquals("horizontal", sep.getElement().getAttribute("aria-orientation"));
    }

    @Test
    void separator_meaningful_noAriaHidden() {
        Separator sep = new Separator();
        assertNull(sep.getElement().getAttribute("aria-hidden"));
    }

    // =========================================================================
    // Separator — orientation
    // =========================================================================

    @Test
    void separator_setOrientation_vertical_updatesClass() {
        Separator sep = new Separator();
        sep.setOrientation(Separator.Orientation.VERTICAL);

        assertTrue(sep.getClassNames().contains("separator--vertical"));
        assertFalse(sep.getClassNames().contains("separator--horizontal"));
        assertEquals(Separator.Orientation.VERTICAL, sep.getOrientation());
    }

    @Test
    void separator_setOrientation_vertical_updatesAriaOrientation() {
        Separator sep = new Separator();
        sep.setOrientation(Separator.Orientation.VERTICAL);
        assertEquals("vertical", sep.getElement().getAttribute("aria-orientation"));
    }

    @Test
    void separator_setOrientation_backToHorizontal_updatesClass() {
        Separator sep = new Separator();
        sep.setOrientation(Separator.Orientation.VERTICAL);
        sep.setOrientation(Separator.Orientation.HORIZONTAL);

        assertTrue(sep.getClassNames().contains("separator--horizontal"));
        assertFalse(sep.getClassNames().contains("separator--vertical"));
    }

    @Test
    void separator_setOrientation_null_defaultsToHorizontal() {
        Separator sep = new Separator();
        sep.setOrientation(null);
        assertEquals(Separator.Orientation.HORIZONTAL, sep.getOrientation());
    }

    // =========================================================================
    // Separator — decorative flag
    // =========================================================================

    @Test
    void separator_setDecorative_true_hasRoleNone() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        assertEquals("none", sep.getElement().getAttribute("role"));
    }

    @Test
    void separator_setDecorative_true_hasAriaHidden() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        assertEquals("true", sep.getElement().getAttribute("aria-hidden"));
    }

    @Test
    void separator_setDecorative_true_hasDecorativeClass() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        assertTrue(sep.getClassNames().contains("separator--decorative"));
    }

    @Test
    void separator_setDecorative_true_noAriaOrientation() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        assertNull(sep.getElement().getAttribute("aria-orientation"));
    }

    @Test
    void separator_setDecorative_false_restoresRoleSeparator() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        sep.setDecorative(false);
        assertEquals("separator", sep.getElement().getAttribute("role"));
    }

    @Test
    void separator_setDecorative_false_removesAriaHidden() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        sep.setDecorative(false);
        assertNull(sep.getElement().getAttribute("aria-hidden"));
    }

    @Test
    void separator_setDecorative_false_removesDecorativeClass() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        sep.setDecorative(false);
        assertFalse(sep.getClassNames().contains("separator--decorative"));
    }

    @Test
    void separator_setDecorative_false_restoresAriaOrientation() {
        Separator sep = new Separator();
        sep.setDecorative(true);
        sep.setDecorative(false);
        assertEquals("horizontal", sep.getElement().getAttribute("aria-orientation"));
    }

    @Test
    void separator_isDecorative_returnsCorrectState() {
        Separator sep = new Separator();
        assertFalse(sep.isDecorative());
        sep.setDecorative(true);
        assertTrue(sep.isDecorative());
        sep.setDecorative(false);
        assertFalse(sep.isDecorative());
    }

    // =========================================================================
    // Separator — decorative + vertical combination
    // =========================================================================

    @Test
    void separator_decorative_vertical_hasCorrectClasses() {
        Separator sep = new Separator();
        sep.setOrientation(Separator.Orientation.VERTICAL);
        sep.setDecorative(true);

        assertTrue(sep.getClassNames().contains("separator--vertical"));
        assertTrue(sep.getClassNames().contains("separator--decorative"));
        assertEquals("none", sep.getElement().getAttribute("role"));
        assertNull(sep.getElement().getAttribute("aria-orientation"));
    }

    // =========================================================================
    // Static factories
    // =========================================================================

    @Test
    void separator_builder_staticFactory_returnsNonNull() {
        assertNotNull(Separator.builder());
    }

    @Test
    void separatorBuilder_create_returnsNonNull() {
        assertNotNull(SeparatorBuilder.create());
    }

    @Test
    void separatorConfigurator_configure_returnsNonNull() {
        assertNotNull(SeparatorConfigurator.configure(new Separator()));
    }

    @Test
    void separator_configure_staticFactory_returnsNonNull() {
        assertNotNull(Separator.configure(new Separator()));
    }

    // =========================================================================
    // SeparatorBuilder — defaults
    // =========================================================================

    @Test
    void builder_default_buildsHorizontalMeaningfulSeparator() {
        Separator sep = Separator.builder().build();

        assertEquals(Separator.Orientation.HORIZONTAL, sep.getOrientation());
        assertFalse(sep.isDecorative());
        assertTrue(sep.getClassNames().contains("separator"));
        assertTrue(sep.getClassNames().contains("separator--horizontal"));
        assertEquals("separator", sep.getElement().getAttribute("role"));
        assertEquals("horizontal", sep.getElement().getAttribute("aria-orientation"));
    }

    @Test
    void builder_orientation_vertical() {
        Separator sep = Separator.builder()
                .orientation(Separator.Orientation.VERTICAL)
                .build();

        assertEquals(Separator.Orientation.VERTICAL, sep.getOrientation());
        assertTrue(sep.getClassNames().contains("separator--vertical"));
        assertEquals("vertical", sep.getElement().getAttribute("aria-orientation"));
    }

    @Test
    void builder_decorative_true() {
        Separator sep = Separator.builder()
                .decorative(true)
                .build();

        assertTrue(sep.isDecorative());
        assertEquals("none", sep.getElement().getAttribute("role"));
        assertEquals("true", sep.getElement().getAttribute("aria-hidden"));
        assertTrue(sep.getClassNames().contains("separator--decorative"));
    }

    @Test
    void builder_decorative_false_isDefault() {
        Separator sep = Separator.builder()
                .decorative(false)
                .build();

        assertFalse(sep.isDecorative());
        assertEquals("separator", sep.getElement().getAttribute("role"));
    }

    // =========================================================================
    // SeparatorBuilder — ComponentConfigurator methods
    // =========================================================================

    @Test
    void builder_id_setsId() {
        Separator sep = Separator.builder().id("my-sep").build();
        assertEquals("my-sep", sep.getId().orElse(null));
    }

    @Test
    void builder_visible_false_componentIsHidden() {
        Separator sep = Separator.builder().visible(false).build();
        assertFalse(sep.isVisible());
    }

    @Test
    void builder_styleName_addsClass() {
        Separator sep = Separator.builder().styleName("section-rule").build();
        assertTrue(sep.getClassNames().contains("section-rule"));
        assertTrue(sep.getClassNames().contains("separator"), "base class must remain");
    }

    @Test
    void builder_width_setsWidth() {
        Separator sep = Separator.builder().width("50%").build();
        assertEquals("50%", sep.getWidth());
    }

    @Test
    void builder_enabled_false_isDisabled() {
        Separator sep = Separator.builder().enabled(false).build();
        assertFalse(sep.isEnabled());
    }

    // =========================================================================
    // SeparatorBuilder — fluent chain returns same builder
    // =========================================================================

    @Test
    void builder_fluentChain_allCallsReturnSameBuilder() {
        SeparatorBuilder builder = Separator.builder();
        SeparatorBuilder b1 = builder.orientation(Separator.Orientation.HORIZONTAL);
        SeparatorBuilder b2 = b1.decorative(false);
        SeparatorBuilder b3 = b2.id("chained");

        assertSame(builder, b1);
        assertSame(builder, b2);
        assertSame(builder, b3);
    }

    // =========================================================================
    // SeparatorConfigurator.configure() — wraps existing instance
    // =========================================================================

    @Test
    void configure_existingSeparator_setsOrientation() {
        Separator sep = new Separator();
        SeparatorConfigurator.configure(sep).orientation(Separator.Orientation.VERTICAL);

        assertEquals(Separator.Orientation.VERTICAL, sep.getOrientation());
    }

    @Test
    void configure_existingSeparator_setsDecorative() {
        Separator sep = new Separator();
        SeparatorConfigurator.configure(sep).decorative(true);

        assertTrue(sep.isDecorative());
    }

    @Test
    void configure_existingSeparator_setsWidth() {
        Separator sep = new Separator();
        SeparatorConfigurator.configure(sep).width("fit-content");

        assertEquals("fit-content", sep.getWidth());
    }

    // =========================================================================
    // Components façade integration
    // =========================================================================

    @Test
    void components_separator_returnsBuilder() {
        assertNotNull(Components.separator());
    }

    @Test
    void components_separator_buildsCorrectly() {
        Separator sep = Components.separator()
                .orientation(Separator.Orientation.VERTICAL)
                .decorative(true)
                .id("facade-sep")
                .build();

        assertEquals(Separator.Orientation.VERTICAL, sep.getOrientation());
        assertTrue(sep.isDecorative());
        assertEquals("facade-sep", sep.getId().orElse(null));
    }

    @Test
    void components_configure_wrapsExistingInstance() {
        Separator sep = new Separator();
        Components.configure(sep).orientation(Separator.Orientation.VERTICAL);

        assertEquals(Separator.Orientation.VERTICAL, sep.getOrientation());
    }

    // =========================================================================
    // Orientation enum
    // =========================================================================

    @Test
    void orientation_horizontal_ariaValue() {
        assertEquals("horizontal", Separator.Orientation.HORIZONTAL.getAriaValue());
    }

    @Test
    void orientation_vertical_ariaValue() {
        assertEquals("vertical", Separator.Orientation.VERTICAL.getAriaValue());
    }

    @Test
    void orientation_horizontal_cssClass() {
        assertEquals("separator--horizontal", Separator.Orientation.HORIZONTAL.getCssClass());
    }

    @Test
    void orientation_vertical_cssClass() {
        assertEquals("separator--vertical", Separator.Orientation.VERTICAL.getCssClass());
    }
}

