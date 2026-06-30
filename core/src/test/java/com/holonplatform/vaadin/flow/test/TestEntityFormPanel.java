package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.EntityFormPanel;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.formlayout.FormLayout;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEntityFormPanel {

    @Test
    void stretchLastRow_balancesFinalRowAcrossAvailableColumns() {
        EntityFormPanel<RowBean> panel = EntityFormPanel.bean(RowBean.class)
                .properties("first", "second", "third", "fourth", "fifth")
                .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                .stretchLastRow(true)
                .saveButton(btn -> btn.text("Save"), bean -> { })
                .clearButton(btn -> btn.text("Clear"))
                .build();

        FormLayout layout = (FormLayout) panel.getForm().getComponent();
        List<Component> children = layout.getChildren().toList();

        assertEquals(5, children.size());
        assertEquals(1, layout.getColspan(children.get(0)));
        assertEquals(1, layout.getColspan(children.get(1)));
        assertEquals(1, layout.getColspan(children.get(2)));
        assertEquals(2, layout.getColspan(children.get(3)));
        assertEquals(1, layout.getColspan(children.get(4)));
    }

    @Test
    void stretchLastRow_disabledKeepsDefaultSingleColumnSpans() {
        EntityFormPanel<RowBean> panel = EntityFormPanel.bean(RowBean.class)
                .properties("first", "second", "third", "fourth", "fifth")
                .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                .saveButton(btn -> btn.text("Save"), bean -> { })
                .clearButton(btn -> btn.text("Clear"))
                .build();

        FormLayout layout = (FormLayout) panel.getForm().getComponent();
        List<Component> children = layout.getChildren().toList();

        assertEquals(5, children.size());
        assertEquals(1, layout.getColspan(children.get(0)));
        assertEquals(1, layout.getColspan(children.get(1)));
        assertEquals(1, layout.getColspan(children.get(2)));
        assertEquals(1, layout.getColspan(children.get(3)));
        assertEquals(1, layout.getColspan(children.get(4)));
    }

    @Test
    void divStretchLastRow_balancesFinalRowAcrossAvailableColumns() {
        EntityFormPanel<RowBean> panel = EntityFormPanel.beanDiv(RowBean.class)
                .properties("first", "second", "third", "fourth", "fifth")
                .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                .stretchLastRow(true)
                .saveButton(btn -> btn.text("Save"), bean -> { })
                .clearButton(btn -> btn.text("Clear"))
                .build();

        Div layout = (Div) panel.getForm().getComponent();
        List<Component> children = layout.getChildren().toList();

        assertTrue(layout.getClassNames().contains("grid"));
        assertTrue(layout.getClassNames().contains("grid-cols-12"));
        assertEquals(5, children.size());
        assertTrue(children.get(0).getClassNames().contains("col-span-12"));
        assertTrue(children.get(0).getClassNames().contains("lg:col-span-4"));
        assertTrue(children.get(3).getClassNames().contains("lg:col-span-6"));
        assertTrue(children.get(4).getClassNames().contains("lg:col-span-6"));
    }

    @Test
    void divStretchLastRow_disabledKeepsDefaultSingleColumnSpans() {
        EntityFormPanel<RowBean> panel = EntityFormPanel.beanDiv(RowBean.class)
                .properties("first", "second", "third", "fourth", "fifth")
                .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                .saveButton(btn -> btn.text("Save"), bean -> { })
                .clearButton(btn -> btn.text("Clear"))
                .build();

        Div layout = (Div) panel.getForm().getComponent();
        List<Component> children = layout.getChildren().toList();

        assertEquals(5, children.size());
        assertTrue(children.get(0).getClassNames().contains("col-span-12"));
        assertTrue(children.get(0).getClassNames().contains("lg:col-span-4"));
        assertTrue(children.get(3).getClassNames().contains("lg:col-span-4"));
        assertTrue(children.get(4).getClassNames().contains("lg:col-span-4"));
    }

    @Test
    void divInitializerAndPostProcessor_areApplied() {
        EntityFormPanel<RowBean> panel = EntityFormPanel.beanDiv(RowBean.class)
                .properties("first", "second")
                .initializer(layout -> layout.addClassName("custom-grid"))
                .withPostProcessor((layout, property, input) -> {
                    if ("second".equals(property.getName())) {
                        input.getComponent().addClassName("custom-field");
                    }
                })
                .saveButton(btn -> btn.text("Save"), bean -> { })
                .clearButton(btn -> btn.text("Clear"))
                .build();

        Div layout = (Div) panel.getForm().getComponent();
        List<Component> children = layout.getChildren().toList();

        assertTrue(layout.getClassNames().contains("custom-grid"));
        assertTrue(children.get(1).getClassNames().contains("custom-field"));
    }

    @Test
    void requiredFieldsKeepTheirRequiredIndicator() {
        EntityFormPanel<RowBean> panel = EntityFormPanel.beanDiv(RowBean.class)
                .properties("first", "second")
                .configure(fb -> fb.property("first").ifPresent(p -> fb.configure(inner -> inner.required(p))))
                .saveButton(btn -> btn.text("Save"), bean -> { })
                .clearButton(btn -> btn.text("Clear"))
                .build();

        assertTrue(panel.getForm().getBindings()
                .filter(binding -> "first".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());
        assertFalse(panel.getForm().getBindings()
                .filter(binding -> "second".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());
    }

            @Test
            void autoRequiredIndicators_toggleBeanValidationAnnotations() {
            BeanPropertyInputForm<AnnotatedBean> form = BeanPropertyInputForm.formLayout(AnnotatedBean.class).build();

            form.setAutoRequiredIndicators(true);

            assertTrue(form.getBindings()
                .filter(binding -> "firstName".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());
            assertTrue(form.getBindings()
                .filter(binding -> "age".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());
            assertFalse(form.getBindings()
                .filter(binding -> "notes".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());

            form.setAutoRequiredIndicators(false);

            assertFalse(form.getBindings()
                .filter(binding -> "firstName".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());
            assertFalse(form.getBindings()
                .filter(binding -> "age".equals(binding.getProperty().getName()))
                .findFirst()
                .orElseThrow()
                .getElement()
                .isRequired());
            }

    public static class RowBean {
        private String first;
        private String second;
        private String third;
        private String fourth;
        private String fifth;

        public RowBean() {
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        public String getThird() {
            return third;
        }

        public void setThird(String third) {
            this.third = third;
        }

        public String getFourth() {
            return fourth;
        }

        public void setFourth(String fourth) {
            this.fourth = fourth;
        }

        public String getFifth() {
            return fifth;
        }

        public void setFifth(String fifth) {
            this.fifth = fifth;
        }
    }

    public static class AnnotatedBean {

        @jakarta.validation.constraints.NotBlank
        private String firstName;

        @jakarta.validation.constraints.NotNull
        private Integer age;

        private String notes;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}










