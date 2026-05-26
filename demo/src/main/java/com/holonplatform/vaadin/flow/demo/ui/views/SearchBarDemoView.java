package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("SearchBar – Holon Demo")
@Route(value = "search-bar", layout = DemoMainLayout.class)
public class SearchBarDemoView extends Div {

    public SearchBarDemoView() {
        addClassName("app-view");

        var title = new H1("SearchBar");

        var desc = new Paragraph(
                "SearchBarBuilder provides a fluent API for a search input "
                + "with an optional \"New\" action button, built as a HorizontalLayout.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withNewButtonExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var searchBar = SearchBarBuilder.create()
                .search(e -> Notification.show("Searching: " + e.getValue()))
                .build();

        return new DemoExample("Basic SearchBar", searchBar, """
                SearchBarBuilder.create()
                    .search(e -> Notification.show("Searching: " + e.getValue()))
                    .build();""");
    }

    private DemoExample withNewButtonExample() {
        var searchBar = SearchBarBuilder.create()
                .search(e -> Notification.show("Searching: " + e.getValue()))
                .newButton(btn -> btn.text("Add New"))
                .build();

        return new DemoExample("With New Button", searchBar, """
                SearchBarBuilder.create()
                    .search(e -> Notification.show("Searching: " + e.getValue()))
                    .newButton(btn -> btn.text("Add New"))
                    .build();""");
    }
}
