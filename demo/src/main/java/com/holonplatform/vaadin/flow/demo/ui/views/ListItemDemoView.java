package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.ListItem;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("ListItem – Holon Demo")
@Route(value = "list-item", layout = DemoMainLayout.class)
public class ListItemDemoView extends Div {

    public ListItemDemoView() {
        addClassName("app-view");

        var title = new H1("ListItem");

        var desc = new Paragraph(
                "A flex list item with primary/secondary labels, optional prefix/suffix slots, "
                + "and divider support. Extends FlexBoxLayout. Styled via list-item.css.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(primaryOnlyExample());
        examples.add(withPrefixExample());
        examples.add(withSuffixExample());
        examples.add(withDividerExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var item = new ListItem("John Doe", "john.doe@example.com");

        return new DemoExample("Primary + Secondary", item, """
                new ListItem("John Doe", "john.doe@example.com")""");
    }

    private DemoExample primaryOnlyExample() {
        var item = new ListItem("Settings");

        return new DemoExample("Primary Only", item, """
                new ListItem("Settings")""");
    }

    private DemoExample withPrefixExample() {
        var avatar = new Avatar("Jane Smith");
        var item = new ListItem(avatar, "Jane Smith", "Product Manager");

        return new DemoExample("With Prefix (Avatar)", item, """
                var avatar = new Avatar("Jane Smith");
                new ListItem(avatar, "Jane Smith", "Product Manager")""");
    }

    private DemoExample withSuffixExample() {
        var badge = new Span("Admin");
        badge.getElement().setAttribute("theme", "badge small");
        var item = new ListItem("Alex Johnson", "alex@corp.com", badge);

        return new DemoExample("With Suffix (Badge)", item, """
                var badge = new Span("Admin");
                badge.getElement().setAttribute("theme", "badge small");
                new ListItem("Alex Johnson", "alex@corp.com", badge)""");
    }

    private DemoExample withDividerExample() {
        var container = new Div();
        var item1 = new ListItem(VaadinIcon.ENVELOPE.create(), "Messages", "3 unread");
        item1.setDividerVisible(true);
        var item2 = new ListItem(VaadinIcon.BELL.create(), "Notifications", "12 new");
        item2.setDividerVisible(true);
        var item3 = new ListItem(VaadinIcon.COG.create(), "Settings", "");

        container.add(item1, item2, item3);

        return new DemoExample("With Dividers", container, """
                var item1 = new ListItem(VaadinIcon.ENVELOPE.create(), "Messages", "3 unread");
                item1.setDividerVisible(true);
                var item2 = new ListItem(VaadinIcon.BELL.create(), "Notifications", "12 new");
                item2.setDividerVisible(true);
                var item3 = new ListItem(VaadinIcon.COG.create(), "Settings", "");""");
    }
}
