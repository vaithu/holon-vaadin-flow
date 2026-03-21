package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenViewBuilder;
import org.junit.jupiter.api.Test;

public class TestIyenView {

@Test
    public void testView() {

    IyenViewBuilder builder = IyenViewBuilder.create()
            .mobile(createMaster());

    System.out.println(builder.build().getComponentCount());





    }

    private IyenMasterBuilder createMaster() {
        return IyenMasterBuilder.create();
    }
}
