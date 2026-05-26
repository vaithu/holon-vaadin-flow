package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("AlertModal – Holon Demo")
@Route(value = "alert-modal", layout = DemoMainLayout.class)
public class AlertModalDemoView extends Div {

    public AlertModalDemoView() {
        addClassName("app-view");

        var title = new H1("AlertModal");

        var desc = new Paragraph(
                "A dismissible modal overlay wrapping an Alert. "
                + "Suitable for informational messages that don't require an explicit user decision. "
                + "Closable via ESC key and outside click by default. "
                + "Use AlertDialog instead when you need cancel/confirm actions.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(defaultExample());
        examples.add(variantsExample());
        examples.add(withTitleAndDescriptionExample());

        add(title, desc, examples);
    }

    private DemoExample defaultExample() {
        var btn = new Button("Open Default Modal", e ->
                AlertModal.builder()
                        .title("Information")
                        .description("This is a default alert modal.")
                        .build()
                        .open());

        return new DemoExample("Default", btn, """
                AlertModal.builder()
                    .title("Information")
                    .description("This is a default alert modal.")
                    .build()
                    .open();""");
    }

    private DemoExample variantsExample() {
        var successBtn = new Button("Success", e ->
                AlertModal.builder(Alert.Variant.SUCCESS)
                        .title("Success")
                        .description("Changes saved successfully.")
                        .build()
                        .open());

        var warningBtn = new Button("Warning", e ->
                AlertModal.builder(Alert.Variant.WARNING)
                        .title("Warning")
                        .description("You have unsaved changes.")
                        .build()
                        .open());

        var destructiveBtn = new Button("Destructive", e ->
                AlertModal.builder(Alert.Variant.DESTRUCTIVE)
                        .title("Error")
                        .description("Something went wrong.")
                        .build()
                        .open());

        var infoBtn = new Button("Info", e ->
                AlertModal.builder(Alert.Variant.INFO)
                        .title("Heads up")
                        .description("A new version is available.")
                        .build()
                        .open());

        var layout = new HorizontalLayout(successBtn, warningBtn, destructiveBtn, infoBtn);
        return new DemoExample("Variants", layout, """
                AlertModal.builder(Alert.Variant.SUCCESS)
                    .title("Success")
                    .description("Changes saved successfully.")
                    .build()
                    .open();""");
    }

    private DemoExample withTitleAndDescriptionExample() {
        var btn = new Button("Open with Action", e -> {
            var modal = AlertModal.builder(Alert.Variant.INFO)
                    .title("Profile updated")
                    .description("Your profile has been updated successfully.")
                    .build();
            modal.setAction(new Button("OK", ev -> modal.close()));
            modal.open();
        });

        return new DemoExample("With Action Button", btn, """
                var modal = AlertModal.builder(Alert.Variant.INFO)
                    .title("Profile updated")
                    .description("Your profile has been updated successfully.")
                    .build();
                modal.setAction(new Button("OK", e -> modal.close()));
                modal.open();""");
    }
}
