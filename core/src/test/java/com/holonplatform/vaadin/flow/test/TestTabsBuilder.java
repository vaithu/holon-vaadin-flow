package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.TabBuilder;
import com.holonplatform.vaadin.flow.components.builders.TabsBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTabsBuilder {

    @Test
    public void testTabs() {
        TabsBuilder tabsBuilder = TabsBuilder.create();
        assertNotNull(tabsBuilder);

        TabBuilder tabBuilder = TabBuilder.create();
        assertNotNull(tabBuilder);





    }
}
