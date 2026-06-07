package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.ButtonGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonGroupConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ButtonGroup}, {@link ButtonGroupBuilder}, and
 * {@link ButtonGroupConfigurator}.
 */
class TestButtonGroup {

    // =========================================================================
    // ButtonGroup — construction
    // =========================================================================

    @Test
    void constructor_default_hasBaseClass() {
        ButtonGroup group = new ButtonGroup();
        assertTrue(group.getClassNames().contains("btn-group"));
    }

    @Test
    void constructor_default_noVerticalClass() {
        ButtonGroup group = new ButtonGroup();
        assertFalse(group.getClassNames().contains("btn-group--vertical"));
    }

    @Test
    void constructor_withButtons_addsButtons() {
        ButtonGroup group = new ButtonGroup(new Button("A"), new Button("B"));
        assertEquals(2, group.getChildren().count());
    }

    @Test
    void constructor_withOrientation_appliesVertical() {
        ButtonGroup group = new ButtonGroup(ButtonGroup.Orientation.VERTICAL, new Button("X"));
        assertTrue(group.getClassNames().contains("btn-group--vertical"));
        assertEquals(1, group.getChildren().count());
    }

    // =========================================================================
    // content / remove
    // =========================================================================

    @Test
    void add_buttons_addsChildren() {
        ButtonGroup group = new ButtonGroup();
        group.add(new Button("A"), new Button("B"), new Button("C"));
        assertEquals(3, group.getChildren().count());
    }

    @Test
    void add_nullArray_isNoOp() {
        ButtonGroup group = new ButtonGroup();
        group.add((Button[]) null);
        assertEquals(0, group.getChildren().count());
    }

    @Test
    void add_arrayWithNullElement_skipsNull() {
        ButtonGroup group = new ButtonGroup();
        group.add(new Button("A"), null, new Button("B"));
        assertEquals(2, group.getChildren().count());
    }

    @Test
    void remove_button_removesChild() {
        Button btn = new Button("Remove me");
        ButtonGroup group = new ButtonGroup(btn);
        assertEquals(1, group.getChildren().count());

        group.remove(btn);
        assertEquals(0, group.getChildren().count());
    }

    @Test
    void remove_nullArray_isNoOp() {
        ButtonGroup group = new ButtonGroup(new Button("A"));
        group.remove((Button[]) null);
        assertEquals(1, group.getChildren().count());
    }

    // =========================================================================
    // setOrientation
    // =========================================================================

    @Test
    void setOrientation_vertical_addsModifier() {
        ButtonGroup group = new ButtonGroup();
        group.setOrientation(ButtonGroup.Orientation.VERTICAL);
        assertTrue(group.getClassNames().contains("btn-group--vertical"));
    }

    @Test
    void setOrientation_horizontal_removesModifier() {
        ButtonGroup group = new ButtonGroup();
        group.setOrientation(ButtonGroup.Orientation.VERTICAL);
        group.setOrientation(ButtonGroup.Orientation.HORIZONTAL);
        assertFalse(group.getClassNames().contains("btn-group--vertical"));
    }

    // =========================================================================
    // ButtonGroupBuilder — factory
    // =========================================================================

    @Test
    void builder_create_returnsNonNull() {
        assertNotNull(ButtonGroupBuilder.create());
    }

    @Test
    void builder_staticFactory_returnsNonNull() {
        assertNotNull(ButtonGroup.builder());
    }

    @Test
    void builder_build_empty() {
        ButtonGroup group = ButtonGroupBuilder.create().build();
        assertNotNull(group);
        assertTrue(group.getClassNames().contains("btn-group"));
    }

    // =========================================================================
    // ButtonGroupBuilder — fluent content
    // =========================================================================

    @Test
    void builder_add_addsButtons() {
        ButtonGroup group = ButtonGroupBuilder.create()
                .add(new Button("A"), new Button("B"))
                .build();
        assertEquals(2, group.getChildren().count());
    }

    // =========================================================================
    // ButtonGroupBuilder — orientation
    // =========================================================================

    @Test
    void builder_vertical_setsVertical() {
        ButtonGroup group = ButtonGroupBuilder.create()
                .add(new Button("A"))
                .vertical()
                .build();
        assertTrue(group.getClassNames().contains("btn-group--vertical"));
    }

    @Test
    void builder_orientation_horizontal_setsHorizontal() {
        ButtonGroup group = ButtonGroupBuilder.create()
                .orientation(ButtonGroup.Orientation.HORIZONTAL)
                .build();
        assertFalse(group.getClassNames().contains("btn-group--vertical"));
    }

    // =========================================================================
    // ButtonGroupBuilder — inherited ComponentConfigurator
    // =========================================================================

    @Test
    void builder_id_setsId() {
        ButtonGroup group = ButtonGroupBuilder.create()
                .id("my-group")
                .build();
        assertTrue(group.getId().isPresent());
        assertEquals("my-group", group.getId().get());
    }

    @Test
    void builder_visible_false_hides() {
        ButtonGroup group = ButtonGroupBuilder.create()
                .visible(false)
                .build();
        assertFalse(group.isVisible());
    }

    @Test
    void builder_styleName_addsClassName() {
        ButtonGroup group = ButtonGroupBuilder.create()
                .styleName("custom")
                .build();
        assertTrue(group.getClassNames().contains("custom"));
    }

    // =========================================================================
    // ButtonGroupConfigurator.configure — existing instance
    // =========================================================================

    @Test
    void configurator_configure_returnsNonNull() {
        assertNotNull(ButtonGroupConfigurator.configure(new ButtonGroup()));
    }

    @Test
    void configurator_configure_mutatesExisting() {
        ButtonGroup group = new ButtonGroup();
        ButtonGroupConfigurator.configure(group)
                .add(new Button("A"))
                .vertical();
        assertEquals(1, group.getChildren().count());
        assertTrue(group.getClassNames().contains("btn-group--vertical"));
    }
}
