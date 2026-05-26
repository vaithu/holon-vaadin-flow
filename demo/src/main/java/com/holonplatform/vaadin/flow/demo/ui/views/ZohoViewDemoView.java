package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.builders.ZohoBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;


@PageTitle("ZohoView – Holon Demo")
@Route(value = "zoho-view", layout = DemoMainLayout.class)
public class ZohoViewDemoView extends Div {

    public static final class Contact {
        private long id;
        private String name;
        private String email;

        public Contact() {}

        public Contact(long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public ZohoViewDemoView() {
        addClassName("app-view");

        var title = new H1("ZohoView");

        var desc = new Paragraph(
                "ZohoBuilder creates a master-detail split layout similar to Zoho-style UIs. "
                + "It combines a grid on the left (master) with a form/details panel on the right.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withSearchBarExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var listing = BeanListing.builder(Contact.class)
                .items(sampleContacts())
                .build();

        var detail = new Div(new Span("Select a contact to see details."));
        detail.getStyle().set("padding", "var(--space-m)");

        var zoho = ZohoBuilder.create()
                .grid(listing)
                .detailContent(detail)
                .separator()
                .build();
        zoho.setHeight("350px");

        return new DemoExample("Basic Master-Detail", zoho, """
                ZohoBuilder.create()
                    .grid(beanListing)
                    .detailContent(detailPanel)
                    .separator()
                    .build();""");
    }

    private DemoExample withSearchBarExample() {
        var listing = BeanListing.builder(Contact.class)
                .items(sampleContacts())
                .build();

        var detail = new Div(new Span("Detail panel content"));
        detail.getStyle().set("padding", "var(--space-m)");

        var zoho = ZohoBuilder.create()
                .searchBar()
                    .search(e -> {})
                    .newButton(btn -> btn.text("New Contact"))
                .add()
                .grid(listing)
                .detailContent(detail)
                .separator()
                .build();
        zoho.setHeight("400px");

        return new DemoExample("With SearchBar", zoho, """
                ZohoBuilder.create()
                    .searchBar()
                        .search(e -> { /* filter */ })
                        .newButton(btn -> btn.text("New Contact"))
                    .add()
                    .grid(listing)
                    .detailContent(detail)
                    .separator()
                    .build();""");
    }

    private java.util.List<Contact> sampleContacts() {
        return Arrays.asList(
                new Contact(1, "Alice Johnson", "alice@example.com"),
                new Contact(2, "Bob Smith", "bob@example.com"),
                new Contact(3, "Carol White", "carol@example.com"),
                new Contact(4, "Dave Brown", "dave@example.com")
        );
    }
}
