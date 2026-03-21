package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.html.Div;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMobileGridTemplate {

    @Test
    public void testBuilders() {
        Div div = Components.mobileGrid()
                .primaryText("test")
                .badge("1")
                .secondaryText("Secondary")
                .tertiaryText("tertiary")
                .build();

             assertNotNull(div);
        assertEquals(1, div.getChildren().count());
        div.getChildren().findFirst()
                .ifPresent(component -> {
//                    System.out.println(component.getClassName());
                    assertEquals(3, component.getChildren().count());
                })
                ;
    }
}
