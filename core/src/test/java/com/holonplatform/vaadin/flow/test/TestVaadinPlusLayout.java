package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LayoutBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.textfield.TextField;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestVaadinPlusLayout {

    TextField name = new TextField();


    @Test
    public void test() {
        Layout layout1 = LayoutBuilder.create()
                .add(name)
                .build();

        Assertions.assertThat(layout1)
                .isNotNull();

       Layout layout2 = Components.layout(layout1)
                .build();

        Assertions.assertThat(layout2).isNotNull();
    }
}
