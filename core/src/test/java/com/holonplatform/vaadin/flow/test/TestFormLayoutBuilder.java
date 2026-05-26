package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FormLayoutBuilder} and {@link FormLayoutConfigurator}.
 */
class TestFormLayoutBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(FormLayoutBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsFormLayout() {
        FormLayout form = FormLayoutBuilder.create().build();
        assertNotNull(form);
    }

    // =========================================================================
    // Add components
    // =========================================================================

    @Nested
    class AddTests {

        @Test
        void add_single() {
            FormLayout form = FormLayoutBuilder.create()
                    .add(new TextField())
                    .build();
            assertEquals(1, form.getComponentCount());
        }

        @Test
        void add_multiple() {
            FormLayout form = FormLayoutBuilder.create()
                    .add(new TextField(), new TextField())
                    .build();
            assertEquals(2, form.getComponentCount());
        }

        @Test
        void addComponentAsFirst() {
            TextField first = new TextField("First");
            TextField second = new TextField("Second");
            FormLayout form = FormLayoutBuilder.create()
                    .add(second)
                    .addComponentAsFirst(first)
                    .build();
            assertSame(first, form.getComponentAt(0));
        }

        @Test
        void addComponentAtIndex() {
            TextField a = new TextField("A");
            TextField b = new TextField("B");
            TextField c = new TextField("C");
            FormLayout form = FormLayoutBuilder.create()
                    .add(a, c)
                    .addComponentAtIndex(1, b)
                    .build();
            assertSame(b, form.getComponentAt(1));
        }

        @Test
        void add_withColSpan() {
            TextField field = new TextField("Wide");
            FormLayout form = FormLayoutBuilder.create()
                    .add(2, field)
                    .build();
            assertEquals(2, form.getColspan(field));
        }
    }

    // =========================================================================
    // Responsive Steps
    // =========================================================================

    @Test
    void responsiveSteps_list() {
        List<ResponsiveStep> steps = List.of(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("600px", 2)
        );
        FormLayout form = FormLayoutBuilder.create()
                .responsiveSteps(steps)
                .build();
        assertEquals(2, form.getResponsiveSteps().size());
    }

    @Test
    void responsiveSteps_varargs() {
        FormLayout form = FormLayoutBuilder.create()
                .responsiveSteps(
                        new ResponsiveStep("0", 1),
                        new ResponsiveStep("600px", 2),
                        new ResponsiveStep("900px", 3)
                )
                .build();
        assertEquals(3, form.getResponsiveSteps().size());
    }

    // =========================================================================
    // FormItem
    // =========================================================================

    @Test
    void withFormItem_componentLabel() {
        TextField field = new TextField();
        FormLayout form = FormLayoutBuilder.create()
                .withFormItem(field, new Span("Name"))
                .build();
        assertTrue(form.getComponentCount() > 0);
    }

    @Test
    void withFormItem_stringLabel() {
        TextField field = new TextField();
        FormLayout form = FormLayoutBuilder.create()
                .withFormItem(field, "Email")
                .build();
        assertTrue(form.getComponentCount() > 0);
    }

    @Test
    void withFormItem_consumer() {
        AtomicReference<FormLayout.FormItem> captured = new AtomicReference<>();
        FormLayout form = FormLayoutBuilder.create()
                .withFormItem(new TextField(), "Phone", captured::set)
                .build();
        assertNotNull(captured.get());
    }

    // =========================================================================
    // ColSpan
    // =========================================================================

    @Test
    void colSpan_setsColspan() {
        TextField field = new TextField();
        FormLayout form = FormLayoutBuilder.create()
                .add(field)
                .colSpan(3, field)
                .build();
        assertEquals(3, form.getColspan(field));
    }

    // =========================================================================
    // Component configurator
    // =========================================================================

    @Test
    void id_setsId() {
        FormLayout form = FormLayoutBuilder.create()
                .id("my-form")
                .build();
        assertEquals("my-form", form.getId().orElse(null));
    }

    @Test
    void styleName_addsClass() {
        FormLayout form = FormLayoutBuilder.create()
                .styleName("registration")
                .build();
        assertTrue(form.getClassNames().contains("registration"));
    }

    @Test
    void width_setsWidth() {
        FormLayout form = FormLayoutBuilder.create()
                .width("400px")
                .build();
        assertEquals("400px", form.getWidth());
    }

    // =========================================================================
    // Configure (existing instance)
    // =========================================================================

    @Test
    void configure_existingInstance() {
        FormLayout form = new FormLayout();
        FormLayoutConfigurator.configure(form)
                .responsiveSteps(new ResponsiveStep("0", 1));
        assertEquals(1, form.getResponsiveSteps().size());
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        FormLayout form = FormLayoutBuilder.create()
                .responsiveSteps(
                        new ResponsiveStep("0", 1),
                        new ResponsiveStep("500px", 2)
                )
                .withFormItem(new TextField(), "First Name")
                .withFormItem(new TextField(), "Last Name")
                .id("user-form")
                .styleName("compact-form")
                .width("100%")
                .build();
        assertNotNull(form);
        assertEquals("user-form", form.getId().orElse(null));
        assertTrue(form.getClassNames().contains("compact-form"));
        assertEquals(2, form.getResponsiveSteps().size());
    }
}
