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
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroupText;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link InputGroup} and {@link InputGroupText}.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService. Vaadin components
 * are instantiated directly. Holon {@link Input} and {@link HasComponent}
 * wrappers are simulated with Mockito mocks.</p>
 */
class TestInputGroup {

    // =========================================================================
    // InputGroup — constructor and base state
    // =========================================================================

    @Test
    void inputGroup_default_hasBaseClass() {
        InputGroup group = new InputGroup();
        assertTrue(group.getClassNames().contains("input-group"),
                "base 'input-group' class must be present");
    }

    @Test
    void inputGroup_default_isEmpty() {
        InputGroup group = new InputGroup();
        assertEquals(0, group.getElement().getChildCount(),
                "freshly created group must be empty");
    }

    @Test
    void inputGroup_componentConstructor_populatesChildren() {
        InputGroup group = new InputGroup(new Span("prefix"), new TextField(), new Button("Go"));
        assertEquals(3, group.getElement().getChildCount(),
                "all three components must be present");
    }

    @Test
    void inputGroup_componentConstructor_nullArray_doesNotThrow() {
        assertDoesNotThrow(() -> new InputGroup((Component[]) null),
                "null varargs array must not throw");
    }

    @Test
    void inputGroup_componentConstructor_emptyArray_isEmpty() {
        InputGroup group = new InputGroup();
        assertEquals(0, group.getElement().getChildCount());
    }

    // =========================================================================
    // InputGroup — content(Component...)
    // =========================================================================

    @Test
    void add_singleComponent_appendsIt() {
        InputGroup group = new InputGroup();
        group.add(new TextField());
        assertEquals(1, group.getElement().getChildCount());
    }

    @Test
    void add_multipleComponents_appendsAll() {
        InputGroup group = new InputGroup();
        group.add(new InputGroupText("@"), new TextField(), new Button("OK"));
        assertEquals(3, group.getElement().getChildCount());
    }

    @Test
    void add_nullElementInArray_skipsNull() {
        InputGroup group = new InputGroup();
        group.add(new TextField(), null, new Button("Go"));
        assertEquals(2, group.getElement().getChildCount(),
                "null element must be silently skipped");
    }

    @Test
    void add_nullArray_doesNotThrow() {
        InputGroup group = new InputGroup();
        assertDoesNotThrow(() -> group.add((Component[]) null));
        assertEquals(0, group.getElement().getChildCount());
    }

    @Test
    void add_sequential_appendsInOrder() {
        InputGroup group = new InputGroup();
        group.add(new InputGroupText("$"));
        group.add(new NumberField());
        group.add(new InputGroupText(".00"));
        assertEquals(3, group.getElement().getChildCount());
    }

    // =========================================================================
    // InputGroup — content(HasComponent...)
    // =========================================================================

    @Test
    void add_hasComponent_extractsUnderlyingComponent() {
        TextField tf = new TextField();
        HasComponent wrapper = mock(HasComponent.class);
        doReturn(tf).when(wrapper).getComponent();

        InputGroup group = new InputGroup();
        group.add(wrapper);

        assertEquals(1, group.getElement().getChildCount(),
                "underlying component must be added to the group");
    }

    @Test
    void add_hasComponent_nullArray_doesNotThrow() {
        InputGroup group = new InputGroup();
        assertDoesNotThrow(() -> group.add((HasComponent[]) null));
        assertEquals(0, group.getElement().getChildCount());
    }

    @Test
    void add_hasComponent_nullElement_isSkipped() {
        InputGroup group = new InputGroup();
        HasComponent wrapper = mock(HasComponent.class);
        doReturn(new TextField()).when(wrapper).getComponent();

        group.add(wrapper, null);

        assertEquals(1, group.getElement().getChildCount(),
                "null HasComponent element must be silently skipped");
    }

    @Test
    void add_hasComponent_nullReturnFromGetComponent_isSkipped() {
        HasComponent wrapper = mock(HasComponent.class);
        doReturn(null).when(wrapper).getComponent();

        InputGroup group = new InputGroup();
        group.add(wrapper);

        assertEquals(0, group.getElement().getChildCount(),
                "wrapper returning null component must produce no child");
    }

    @Test
    void add_hasComponent_multiple_allAdded() {
        HasComponent a = mock(HasComponent.class);
        HasComponent b = mock(HasComponent.class);
        doReturn(new TextField()).when(a).getComponent();
        doReturn(new Button("Go")).when(b).getComponent();

        InputGroup group = new InputGroup();
        group.add(a, b);

        assertEquals(2, group.getElement().getChildCount());
    }

    // =========================================================================
    // InputGroup — content(Input<?>...)
    // =========================================================================

    @Test
    @SuppressWarnings("unchecked")  // mock(Input.class) requires raw type
    void add_input_extractsUnderlyingComponent() {
        TextField tf = new TextField();
        Input<String> input = mock(Input.class);
        doReturn(tf).when(input).getComponent();

        InputGroup group = new InputGroup();
        group.add(input);

        assertEquals(1, group.getElement().getChildCount(),
                "Input<T> component must be added to the group");
    }

    @Test
    void add_input_nullArray_doesNotThrow() {
        InputGroup group = new InputGroup();
        assertDoesNotThrow(() -> group.add((Input<?>[]) null));
        assertEquals(0, group.getElement().getChildCount());
    }

    @Test
    @SuppressWarnings("unchecked")  // mock(Input.class) requires raw type
    void add_input_nullElement_isSkipped() {
        Input<String> input = mock(Input.class);
        doReturn(new TextField()).when(input).getComponent();

        InputGroup group = new InputGroup();
        group.add(input, null);

        assertEquals(1, group.getElement().getChildCount());
    }

    // =========================================================================
    // InputGroup — remove / removeAll
    // =========================================================================

    @Test
    void remove_specificComponent_removesIt() {
        Button btn = new Button("Go");
        InputGroup group = new InputGroup();
        group.add(new InputGroupText("@"), new TextField(), btn);

        group.remove(btn);

        assertEquals(2, group.getElement().getChildCount(),
                "only the removed component must be absent");
    }

    @Test
    void removeAll_clearsAllChildren() {
        InputGroup group = new InputGroup(new InputGroupText("$"), new NumberField(), new InputGroupText(".00"));
        assertEquals(3, group.getElement().getChildCount());

        group.removeAll();

        assertEquals(0, group.getElement().getChildCount(),
                "group must be empty after removeAll");
    }

    @Test
    void removeAll_onEmptyGroup_doesNotThrow() {
        InputGroup group = new InputGroup();
        assertDoesNotThrow(group::removeAll);
    }

    // =========================================================================
    // InputGroup — responsive modifier
    // =========================================================================

    @Test
    void inputGroup_responsiveModifier_canBeAdded() {
        InputGroup group = new InputGroup();
        group.addClassName("input-group--responsive");
        assertTrue(group.getClassNames().contains("input-group--responsive"),
                "responsive modifier class must be present after addClassName");
    }

    @Test
    void inputGroup_responsiveModifier_doesNotReplaceBaseClass() {
        InputGroup group = new InputGroup();
        group.addClassName("input-group--responsive");
        assertTrue(group.getClassNames().contains("input-group"),
                "base 'input-group' class must still be present alongside modifier");
    }

    // =========================================================================
    // InputGroupText — constructors and base state
    // =========================================================================

    @Test
    void inputGroupText_default_hasBaseClass() {
        InputGroupText text = new InputGroupText();
        assertTrue(text.getClassNames().contains("input-group__text"),
                "base 'input-group__text' class must be present");
    }

    @Test
    void inputGroupText_default_isEmpty() {
        InputGroupText text = new InputGroupText();
        assertEquals(0, text.getElement().getChildCount(),
                "empty constructor must produce no children");
        assertTrue(text.getText().isEmpty(),
                "empty constructor must produce no text content");
    }

    @Test
    void inputGroupText_stringConstructor_setsText() {
        InputGroupText text = new InputGroupText("@");
        assertEquals("@", text.getText(),
                "text must match the constructor argument");
    }

    @Test
    void inputGroupText_stringConstructor_hasClass() {
        InputGroupText text = new InputGroupText("$");
        assertTrue(text.getClassNames().contains("input-group__text"));
    }

    @Test
    void inputGroupText_nullString_doesNotThrow() {
        assertDoesNotThrow(() -> new InputGroupText((String) null));
    }

    @Test
    void inputGroupText_componentConstructor_hasClass() {
        InputGroupText text = new InputGroupText(new Span("icon"));
        assertTrue(text.getClassNames().contains("input-group__text"));
    }

    @Test
    void inputGroupText_componentConstructor_addsChild() {
        Span icon = new Span("★");
        InputGroupText text = new InputGroupText(icon);
        assertEquals(1, text.getElement().getChildCount(),
                "component constructor must content the component as a child");
    }

    @Test
    void inputGroupText_componentConstructor_multipleComponents_allAdded() {
        InputGroupText text = new InputGroupText(new Span("A"), new Span("B"));
        assertEquals(2, text.getElement().getChildCount(),
                "both components must be children of the addon");
    }

    @Test
    void inputGroupText_componentConstructor_nullElementSkipped() {
        InputGroupText text = new InputGroupText(new Span("A"), null, new Span("B"));
        assertEquals(2, text.getElement().getChildCount(),
                "null component element must be silently skipped");
    }

    @Test
    void inputGroupText_componentConstructor_nullArray_doesNotThrow() {
        assertDoesNotThrow(() -> new InputGroupText((Component[]) null));
    }

    // =========================================================================
    // Full-composition scenarios
    // =========================================================================

    @Test
    void fullComposition_prefixFieldButton() {
        // [@ addon] [TextField] [Button]
        InputGroup group = new InputGroup(
                new InputGroupText("@"),
                new TextField(),
                new Button("Go")
        );
        assertEquals(3, group.getElement().getChildCount(),
                "prefix + field + button = 3 children");
    }

    @Test
    void fullComposition_searchFieldButton() {
        // [TextField] [Button]  — the classic search bar
        InputGroup group = new InputGroup(
                new TextField(),
                new Button("Search")
        );
        assertEquals(2, group.getElement().getChildCount());
    }

    @Test
    void fullComposition_prefixFieldSuffix() {
        // [$] [NumberField] [.00]  — price input
        InputGroup group = new InputGroup(
                new InputGroupText("$"),
                new NumberField(),
                new InputGroupText(".00")
        );
        assertEquals(3, group.getElement().getChildCount());
    }

    @Test
    void fullComposition_urlInput() {
        // [https://] [TextField] [.com]
        InputGroup group = new InputGroup();
        group.add(new InputGroupText("https://"));
        group.add(new TextField());
        group.add(new InputGroupText(".com"));
        assertEquals(3, group.getElement().getChildCount());
    }

    @Test
    void fullComposition_onlyTextAddon_allowed() {
        // Edge case: a group with only a text addon (unusual but valid)
        InputGroup group = new InputGroup(new InputGroupText("prefix"));
        assertEquals(1, group.getElement().getChildCount());
    }

    @Test
    @SuppressWarnings("unchecked")  // mock(Input.class) requires raw type
    void fullComposition_mixedVaadinAndHolonInput() {
        // Mix a raw Vaadin component with a mocked Holon Input<T>
        TextField raw = new TextField();
        Input<String> holonInput = mock(Input.class);
        doReturn(new TextField()).when(holonInput).getComponent();

        InputGroup group = new InputGroup();
        group.add(new InputGroupText("@"));
        group.add(raw);
        group.add(holonInput);

        assertEquals(3, group.getElement().getChildCount(),
                "raw Vaadin component and Holon Input must both be added");
    }

    @Test
    void fullComposition_addThenRemoveThenAddAgain() {
        Button btn = new Button("Go");
        InputGroup group = new InputGroup(new TextField(), btn);
        assertEquals(2, group.getElement().getChildCount());

        group.remove(btn);
        assertEquals(1, group.getElement().getChildCount());

        group.add(new Button("New"));
        assertEquals(2, group.getElement().getChildCount());
    }

    // =========================================================================
    // InputGroup is a plain Div — HasSize and HasStyle are inherited
    // =========================================================================

    @Test
    void inputGroup_isInstanceOfDiv() {
        assertInstanceOf(com.vaadin.flow.component.html.Div.class, new InputGroup(),
                "InputGroup must extend Div");
    }

    @Test
    void inputGroup_setWidthFull_doesNotThrow() {
        InputGroup group = new InputGroup();
        assertDoesNotThrow(group::setWidthFull,
                "setWidthFull must not throw (HasSize is implemented)");
    }
}






