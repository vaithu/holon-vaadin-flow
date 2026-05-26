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

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.InputGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.InputGroupLayoutConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroupText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the {@link InputGroup} fluent builder layer:
 * {@link InputGroupBuilder} and {@link InputGroupLayoutConfigurator}.
 *
 * <p>These tests focus on the <em>builder behaviour</em>; the underlying
 * component behaviour (add / remove / class names) is already covered by
 * {@link TestInputGroup}.</p>
 */
class TestInputGroupBuilder {

    // =========================================================================
    // Static factory methods
    // =========================================================================

    @Test
    void inputGroupBuilder_create_returnsNonNull() {
        assertNotNull(InputGroupBuilder.create());
    }

    @Test
    void inputGroup_builder_staticFactory_returnsNonNull() {
        assertNotNull(InputGroup.builder());
    }

    @Test
    void inputGroupLayoutConfigurator_configure_returnsNonNull() {
        assertNotNull(InputGroupLayoutConfigurator.configure(new InputGroup()));
    }

    @Test
    void inputGroup_configure_staticFactory_returnsNonNull() {
        assertNotNull(InputGroup.configure(new InputGroup()));
    }

    // =========================================================================
    // build() — produces an InputGroup
    // =========================================================================

    @Test
    void build_emptyGroup_isInputGroup() {
        InputGroup result = InputGroup.builder().build();
        assertInstanceOf(InputGroup.class, result);
    }

    @Test
    void build_emptyGroup_hasBaseClass() {
        InputGroup result = InputGroup.builder().build();
        assertTrue(result.getClassNames().contains("input-group"),
                "built group must have 'input-group' class");
    }

    @Test
    void build_emptyGroup_hasNoChildren() {
        InputGroup result = InputGroup.builder().build();
        assertEquals(0, result.getElement().getChildCount());
    }

    // =========================================================================
    // add(Component...) — fluent add
    // =========================================================================

    @Test
    void add_singleComponent_childCountIsOne() {
        InputGroup result = InputGroup.builder()
                .add(new TextField())
                .build();
        assertEquals(1, result.getElement().getChildCount());
    }

    @Test
    void add_multipleComponents_allPresent() {
        InputGroup result = InputGroup.builder()
                .add(new InputGroupText("@"), new TextField(), new Button("Go"))
                .build();
        assertEquals(3, result.getElement().getChildCount());
    }

    @Test
    void add_chainedCalls_allComponentsAdded() {
        InputGroup result = InputGroup.builder()
                .add(new InputGroupText("$"))
                .add(new NumberField())
                .add(new InputGroupText(".00"))
                .build();
        assertEquals(3, result.getElement().getChildCount());
    }

    @Test
    void add_nullElement_isSkipped() {
        InputGroup result = InputGroup.builder()
                .add(new TextField(), null, new Button("Go"))
                .build();
        assertEquals(2, result.getElement().getChildCount());
    }

    @Test
    void add_nullArray_doesNotThrow() {
        assertDoesNotThrow(() -> InputGroup.builder()
                .add((com.vaadin.flow.component.Component[]) null)
                .build());
    }

    // =========================================================================
    // add(HasComponent...) — Holon wrapper unwrapping
    // =========================================================================

    @Test
    void add_hasComponent_unwrapsUnderlyingComponent() {
        TextField tf = new TextField();
        HasComponent wrapper = mock(HasComponent.class);
        doReturn(tf).when(wrapper).getComponent();

        InputGroup result = InputGroup.builder()
                .add(wrapper)
                .build();

        assertEquals(1, result.getElement().getChildCount());
    }

    @Test
    void add_hasComponent_nullArray_doesNotThrow() {
        assertDoesNotThrow(() -> InputGroup.builder()
                .add((HasComponent[]) null)
                .build());
    }

    @Test
    void add_hasComponent_nullElement_isSkipped() {
        HasComponent wrapper = mock(HasComponent.class);
        doReturn(new TextField()).when(wrapper).getComponent();

        InputGroup result = InputGroup.builder()
                .add(wrapper, null)
                .build();

        assertEquals(1, result.getElement().getChildCount());
    }

    // =========================================================================
    // add(Input<?>...) — Holon Input unwrapping
    // =========================================================================

    @Test
    @SuppressWarnings("unchecked")
    void add_input_unwrapsUnderlyingComponent() {
        TextField tf = new TextField();
        Input<String> input = mock(Input.class);
        doReturn(tf).when(input).getComponent();

        InputGroup result = InputGroup.builder()
                .add(input)
                .build();

        assertEquals(1, result.getElement().getChildCount());
    }

    @Test
    void add_input_nullArray_doesNotThrow() {
        assertDoesNotThrow(() -> InputGroup.builder()
                .add((Input<?>[]) null)
                .build());
    }

    @Test
    @SuppressWarnings("unchecked")
    void add_mixedVaadinAndHolonInput_allPresent() {
        Input<String> input = mock(Input.class);
        doReturn(new TextField()).when(input).getComponent();

        InputGroup result = InputGroup.builder()
                .add(new InputGroupText("@"))
                .add(new TextField())
                .add(input)
                .build();

        assertEquals(3, result.getElement().getChildCount());
    }

    // =========================================================================
    // responsive() / responsive(boolean)
    // =========================================================================

    @Test
    void responsive_noArg_addsModifierClass() {
        InputGroup result = InputGroup.builder()
                .responsive()
                .build();

        assertTrue(result.getClassNames().contains("input-group--responsive"),
                "responsive() must add 'input-group--responsive' class");
    }

    @Test
    void responsive_true_addsModifierClass() {
        InputGroup result = InputGroup.builder()
                .responsive(true)
                .build();

        assertTrue(result.getClassNames().contains("input-group--responsive"));
    }

    @Test
    void responsive_false_doesNotAddModifierClass() {
        InputGroup result = InputGroup.builder()
                .responsive(false)
                .build();

        assertFalse(result.getClassNames().contains("input-group--responsive"),
                "responsive(false) must not add the modifier class");
    }

    @Test
    void responsive_trueFollowedByFalse_classIsAbsent() {
        InputGroup result = InputGroup.builder()
                .responsive(true)
                .responsive(false)
                .build();

        assertFalse(result.getClassNames().contains("input-group--responsive"),
                "calling responsive(false) after responsive(true) must remove the class");
    }

    @Test
    void responsive_baseClassStillPresent() {
        InputGroup result = InputGroup.builder()
                .responsive()
                .build();

        assertTrue(result.getClassNames().contains("input-group"),
                "base 'input-group' class must remain after responsive()");
    }

    // =========================================================================
    // ComponentConfigurator — id, visible, styleName
    // =========================================================================

    @Test
    void id_setsIdOnBuiltComponent() {
        InputGroup result = InputGroup.builder()
                .id("my-group")
                .build();

        assertEquals("my-group", result.getId().orElse(null));
    }

    @Test
    void visible_false_componentIsHidden() {
        InputGroup result = InputGroup.builder()
                .visible(false)
                .build();

        assertFalse(result.isVisible());
    }

    @Test
    void visible_true_componentIsVisible() {
        InputGroup result = InputGroup.builder()
                .visible(true)
                .build();

        assertTrue(result.isVisible());
    }

    @Test
    void styleName_addsCustomClass() {
        InputGroup result = InputGroup.builder()
                .styleName("my-custom-group")
                .build();

        assertTrue(result.getClassNames().contains("my-custom-group"));
    }

    @Test
    void styleName_doesNotRemoveBaseClass() {
        InputGroup result = InputGroup.builder()
                .styleName("extra")
                .build();

        assertTrue(result.getClassNames().contains("input-group"),
                "adding a custom style name must not remove the base class");
    }

    // =========================================================================
    // HasSizeConfigurator — width, height
    // =========================================================================

    @Test
    void width_setsWidthOnBuiltComponent() {
        InputGroup result = InputGroup.builder()
                .width("50%")
                .build();

        assertEquals("50%", result.getWidth());
    }

    @Test
    void height_setsHeightOnBuiltComponent() {
        InputGroup result = InputGroup.builder()
                .height("40px")
                .build();

        assertEquals("40px", result.getHeight());
    }

    // =========================================================================
    // HasEnabledConfigurator — enabled
    // =========================================================================

    @Test
    void enabled_false_componentIsDisabled() {
        InputGroup result = InputGroup.builder()
                .enabled(false)
                .build();

        assertFalse(result.isEnabled());
    }

    @Test
    void enabled_true_componentIsEnabled() {
        InputGroup result = InputGroup.builder()
                .enabled(true)
                .build();

        assertTrue(result.isEnabled());
    }

    // =========================================================================
    // InputGroupLayoutConfigurator.configure() — wraps existing instance
    // =========================================================================

    @Test
    void configure_existingGroup_addsComponents() {
        InputGroup existing = new InputGroup();
        InputGroupLayoutConfigurator.configure(existing)
                .add(new TextField(), new Button("Go"));

        assertEquals(2, existing.getElement().getChildCount());
    }

    @Test
    void configure_existingGroup_setsResponsive() {
        InputGroup existing = new InputGroup();
        InputGroupLayoutConfigurator.configure(existing).responsive(true);

        assertTrue(existing.getClassNames().contains("input-group--responsive"));
    }

    @Test
    void configure_existingGroup_setsWidth() {
        InputGroup existing = new InputGroup();
        InputGroupLayoutConfigurator.configure(existing).width("300px");

        assertEquals("300px", existing.getWidth());
    }

    @Test
    void configure_existingGroup_doesNotDuplicateBaseClass() {
        InputGroup existing = new InputGroup();  // already has "input-group"
        InputGroupLayoutConfigurator.configure(existing)
                .styleName("extra");

        assertTrue(existing.getClassNames().contains("input-group"),
                "configure must not remove base class");
        assertTrue(existing.getClassNames().contains("extra"));
    }

    // =========================================================================
    // InputGroup.configure() static shortcut
    // =========================================================================

    @Test
    void inputGroup_configure_shortcut_setsId() {
        InputGroup existing = new InputGroup();
        InputGroup.configure(existing).id("cfg-group");

        assertEquals("cfg-group", existing.getId().orElse(null));
    }

    // =========================================================================
    // Fluent chaining — builder returns itself
    // =========================================================================

    @Test
    void builder_fluentChain_allCallsReturnSameBuilder() {
        InputGroupBuilder builder = InputGroup.builder();
        InputGroupBuilder b1 = builder.add(new Span("x"));
        InputGroupBuilder b2 = b1.responsive(false);
        InputGroupBuilder b3 = b2.id("chain-test");

        assertSame(builder, b1, "add() must return the same builder");
        assertSame(builder, b2, "responsive() must return the same builder");
        assertSame(builder, b3, "id() must return the same builder");
    }

    // =========================================================================
    // Full composition via builder
    // =========================================================================

    @Test
    void fullComposition_prefixFieldButton() {
        InputGroup result = InputGroup.builder()
                .add(new InputGroupText("@"))
                .add(new TextField())
                .add(new Button("Go"))
                .id("email-group")
                .width("100%")
                .build();

        assertEquals(3, result.getElement().getChildCount());
        assertEquals("email-group", result.getId().orElse(null));
        assertEquals("100%", result.getWidth());
        assertTrue(result.getClassNames().contains("input-group"));
    }

    @Test
    void fullComposition_priceInputResponsive() {
        InputGroup result = InputGroup.builder()
                .add(new InputGroupText("$"))
                .add(new NumberField())
                .add(new InputGroupText(".00"))
                .responsive()
                .build();

        assertEquals(3, result.getElement().getChildCount());
        assertTrue(result.getClassNames().contains("input-group--responsive"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void fullComposition_holonInputWithPrefix() {
        Input<String> holonInput = mock(Input.class);
        doReturn(new TextField()).when(holonInput).getComponent();

        InputGroup result = InputGroup.builder()
                .add(new InputGroupText("Username:"))
                .add(holonInput)
                .width("400px")
                .build();

        assertEquals(2, result.getElement().getChildCount());
        assertEquals("400px", result.getWidth());
    }
}

