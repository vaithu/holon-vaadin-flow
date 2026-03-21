package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.html.Div;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMobileGridTemplate {

    @Test
    public void testBuilders() {
        Div div = Components.mobileGridColumn()
                .withPrimaryText("test")
                .withBadgeAsPrimary("1")
                .withSecondaryText("Secondary")
                .withTertiaryText("tertiary")
                .build();

        assertNotNull(div);
        assertEquals(3, div.getChildren().count());
        div.getChildren().findFirst()
                .ifPresent(component -> {
//                    System.out.println(component.getClassName());
                    assertEquals(2, component.getChildren().count());
                })
        ;
    }
}
