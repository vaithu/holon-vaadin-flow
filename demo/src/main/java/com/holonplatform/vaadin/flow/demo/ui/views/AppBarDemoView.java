package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.AppBar;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link AppBar} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Title in start slot only</li>
 *   <li>Start + end slots (logo + actions)</li>
 *   <li>All three slots (start + middle search + end actions)</li>
 *   <li>Avatar / user area in the end slot</li>
 * </ol>
 */
@PageTitle("AppBar – Holon Demo")
@Route(value = "app-bar", layout = DemoMainLayout.class)
public class AppBarDemoView extends Div {

    public AppBarDemoView() {
        addClassName("app-view");

        var title = new H1("AppBar");

        var desc = new Paragraph(
                "Responsive 3-slot page header extending the HTML <header> element. " +
                "The three slots — start, middle, end — are independent flex containers. " +
                "The middle slot grows to fill available space and centres its content; " +
                "start is left-aligned, end is right-aligned.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(titleOnlyExample());
        examples.add(startEndExample());
        examples.add(allThreeSlotsExample());
        examples.add(withAvatarExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample titleOnlyExample() {
        var bar = new AppBar();
        var logo = new Span("My Application");
        bar.addToStart(logo);

        return new DemoExample("Title in Start Slot", wrap(bar), """
                AppBar bar = new AppBar();

                Span logo = new Span("My Application");
                logo.addClassName("app-logo");

                bar.addToStart(logo);
                """);
    }

    private DemoExample startEndExample() {
        var bar = new AppBar();

        var logo = new Span("Acme Corp");
        bar.addToStart(logo);

        var notifBtn = new Button(VaadinIcon.BELL.create());
        var settingsBtn = new Button(VaadinIcon.COG.create());
        bar.addToEnd(notifBtn, settingsBtn);

        return new DemoExample("Start + End Slots", wrap(bar), """
                AppBar bar = new AppBar();

                // Start — brand / logo
                Span logo = new Span("Acme Corp");
                bar.addToStart(logo);

                // End — action icons
                bar.addToEnd(
                    new Button(VaadinIcon.BELL.create()),
                    new Button(VaadinIcon.COG.create())
                );
                """);
    }

    private DemoExample allThreeSlotsExample() {
        var bar = new AppBar();

        var menuBtn = new Button(VaadinIcon.MENU.create());
        var logo = new Span("Dashboard");
        bar.addToStart(menuBtn, logo);

        var search = new TextField();
        search.setPlaceholder("Search…");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        bar.addToMiddle(search);

        var notifBtn = new Button(VaadinIcon.BELL.create());
        var helpBtn = new Button(VaadinIcon.QUESTION_CIRCLE.create());
        bar.addToEnd(notifBtn, helpBtn);

        return new DemoExample("All Three Slots (start / middle / end)", wrap(bar), """
                AppBar bar = new AppBar();

                // Start — hamburger + title
                bar.addToStart(
                    new Button(VaadinIcon.MENU.create()),
                    new Span("Dashboard")
                );

                // Middle — full-width search field
                TextField search = new TextField();
                search.setPlaceholder("Search…");
                search.setPrefixComponent(VaadinIcon.SEARCH.create());
                bar.addToMiddle(search);

                // End — icon buttons
                bar.addToEnd(
                    new Button(VaadinIcon.BELL.create()),
                    new Button(VaadinIcon.QUESTION_CIRCLE.create())
                );
                """);
    }

    private DemoExample withAvatarExample() {
        var bar = new AppBar();

        var logo = new Span("Portal");
        bar.addToStart(logo);

        var avatar = new Avatar("Jane Doe");
        bar.addToEnd(avatar);

        return new DemoExample("User Avatar in End Slot", wrap(bar), """
                AppBar bar = new AppBar();

                bar.addToStart(new Span("Portal"));

                // Avatar in the end slot — shows user initials / photo
                Avatar avatar = new Avatar("Jane Doe");
                bar.addToEnd(avatar);
                """);
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    /** Wraps the bar in a container so it fills the preview area nicely. */
    private static Div wrap(AppBar bar) {
        var wrapper = new Div(bar);
        return wrapper;
    }
}

