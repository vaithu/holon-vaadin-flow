package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.NativeButtonBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("NativeButton – Holon Demo")
@Route(value = "native-button", layout = DemoMainLayout.class)
public class NativeButtonDemoView extends Div {

    public NativeButtonDemoView() {
        addClassName("app-view");

        var title = new H1("NativeButton");

        var desc = new Paragraph(
                "NativeButtonBuilder creates HTML <button> elements — "
                + "lighter than Vaadin Button, useful for custom-styled actions.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(styledExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var btn = NativeButtonBuilder.create()
                .text("Click Me")
                .onClick(e -> Notification.show("Native button clicked"))
                .build();

        return new DemoExample("Basic NativeButton", btn, """
                NativeButtonBuilder.create()
                    .text("Click Me")
                    .onClick(e -> Notification.show("Clicked"))
                    .build();""");
    }

    private DemoExample styledExample() {
        var primary = NativeButtonBuilder.create()
                .text("Primary")
                .styleName("btn-primary")
                .build();

        var secondary = NativeButtonBuilder.create()
                .text("Secondary")
                .styleName("btn-secondary")
                .build();

        var disabled = NativeButtonBuilder.create()
                .text("Disabled")
                .enabled(false)
                .build();

        var layout = new HorizontalLayout(primary, secondary, disabled);
        layout.setSpacing(true);

        return new DemoExample("With CSS Classes", layout, """
                NativeButtonBuilder.create()
                    .text("Primary")
                    .styleName("btn-primary")
                    .build();
                NativeButtonBuilder.create()
                    .text("Disabled")
                    .enabled(false)
                    .build();""");
    }
}
