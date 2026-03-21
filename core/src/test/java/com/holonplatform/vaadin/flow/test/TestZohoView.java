package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ZohoViewBuilder;
import com.holonplatform.vaadin.flow.components.builders.ZohoViewConfigurator;
import com.holonplatform.vaadin.flow.test.pojo.Person;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.Test;

public class TestZohoView {




    @Test
    public void test() {

        ZohoViewConfigurator.BuiltView<ZohoViewBuilder> build = ZohoViewBuilder.create()
                .master()
                .header(
                        Components.header("dfsdf").build()
                )
                .content(
                        Components.listing.items(Person.class).build()
                )
                .build();

        Layout layout = build.getLayout();
        printChildren(layout);

//        layout.getId().ifPresent(System.out::println);

        Layout rootLayout = build.add().build();
        printChildren(rootLayout);

//        rootLayout.getId().ifPresent(System.out::println);
        /*System.out.println(layout.getComponentCount());
        printChildren(layout);

        layout.getClassNames().forEach(System.out::println);

        Layout masterLayout1 = (Layout) layout.getComponentAt(0);

        masterLayout1.getId().ifPresent(System.out::println);
        System.out.println(masterLayout1.getComponentCount());

        printChildren(masterLayout1);*/


//        layout.getChildren().forEach(component -> component.);



    }

    void printChildren(Component component) {
        System.out.println("Displaying children of component of "+ component.getClass().getSimpleName() + " id " + component.getId().orElse(""));
        component.getChildren().forEach(component1 -> {
            System.out.println("Children: " + component1.getClass().getSimpleName());
//            component1.getClassNames().forEach(System.out::println);
        });
    }
}
