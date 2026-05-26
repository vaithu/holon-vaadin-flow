package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenViewBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestIyenView {

    @Test
    void testView() {
        IyenViewBuilder builder = IyenViewBuilder.create()
                .mobile(createMaster());
        Layout layout = builder.build();
        assertNotNull(layout);
        assertTrue(layout.getComponentCount() >= 0);
    }

    private IyenMasterBuilder createMaster() {
        return IyenMasterBuilder.create();
    }
}
