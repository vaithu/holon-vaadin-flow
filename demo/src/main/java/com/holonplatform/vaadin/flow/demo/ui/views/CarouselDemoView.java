package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Carousel;
import com.holonplatform.vaadin.flow.vaadinplus.components.CarouselItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Carousel} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic horizontal carousel (single slide at a time)</li>
 *   <li>Loop-enabled carousel</li>
 *   <li>Multi-item carousel (3 slides visible at once)</li>
 *   <li>Vertical carousel</li>
 *   <li>Programmatic navigation</li>
 * </ol>
 */
@PageTitle("Carousel – Holon Demo")
@Route(value = "carousel", layout = DemoMainLayout.class)
public class CarouselDemoView extends Div {

    public CarouselDemoView() {
        addClassName("app-view");

        var title = new H1("Carousel");

        var desc = new Paragraph(
                "Accessible carousel component inspired by shadcn/ui Carousel. " +
                "Uses CSS scroll-snap for smooth, hardware-accelerated sliding — no external JS library. " +
                "Supports horizontal and vertical orientation, optional wrap-around looping, " +
                "multi-item (partial-slide) views, and programmatic navigation.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(loopExample());
        examples.add(multiItemExample());
        examples.add(verticalExample());
        examples.add(programmaticExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        Carousel carousel = new Carousel();
        for (int i = 1; i <= 5; i++) {
            carousel.addItem(slide("Slide " + i, i));
        }

        return new DemoExample("Basic (horizontal)", carousel, """
                Carousel carousel = new Carousel();
                for (int i = 1; i <= 5; i++) {
                    carousel.addItem(myCardComponent(i));
                }
                """);
    }

    private DemoExample loopExample() {
        Carousel carousel = Carousel.builder()
                .loop(true)
                .addItem(
                    slide("Slide 1 — loops back", 1),
                    slide("Slide 2 — loops back", 2),
                    slide("Slide 3 — loops back", 3)
                )
                .build();

        return new DemoExample("Loop enabled", carousel, """
                Carousel carousel = Carousel.builder()
                    .loop(true)
                    .addItem(slide1, slide2, slide3)
                    .build();
                """);
    }

    private DemoExample multiItemExample() {
        Div preview = new Div();

        Carousel carousel = new Carousel();
        // Override item basis to show ~3 slides at once
        for (int i = 1; i <= 6; i++) {
            CarouselItem item = new CarouselItem(slide("Slide " + i, i));
            item.setBasis("33.333%");
            carousel.getContent().add(item);
        }

        preview.add(carousel);

        return new DemoExample("Multi-item (3 visible)", preview, """
                Carousel carousel = new Carousel();
                for (int i = 1; i <= 6; i++) {
                    CarouselItem item = new CarouselItem(myCard(i));
                    item.setBasis("33.333%");   // show 3 at a time
                    carousel.getContent().add(item);
                }
                """);
    }

    private DemoExample verticalExample() {
        Div wrapper = new Div();

        Carousel carousel = Carousel.builder(Carousel.Orientation.VERTICAL)
                .addItem(
                    slide("Top",    1),
                    slide("Middle", 2),
                    slide("Bottom", 3)
                )
                .build();

        wrapper.add(carousel);

        return new DemoExample("Vertical", wrapper, """
                Carousel carousel = Carousel.builder(Carousel.Orientation.VERTICAL)
                    .addItem(top, middle, bottom)
                    .build();
                """);
    }

    private DemoExample programmaticExample() {
        Carousel carousel = new Carousel();
        for (int i = 1; i <= 5; i++) {
            carousel.addItem(slide("Slide " + i, i));
        }

        Span indicator = new Span("Current: 1 / 5");

        carousel.addSlideChangeListener(e -> {
            indicator.setText("Current: " + (e.getCurrentIndex() + 1) + " / " + carousel.getItemCount());
        });

        var btn1 = new Button("First",  e -> carousel.scrollTo(0));
        var btn3 = new Button("Third",  e -> carousel.scrollTo(2));
        var btnL = new Button("Last",   e -> carousel.scrollTo(carousel.getItemCount() - 1));

        var btnRow = new Div(btn1, btn3, btnL);

        var wrapper = new Div(carousel, indicator, btnRow);

        return new DemoExample("Programmatic navigation", wrapper, """
                Carousel carousel = new Carousel();
                // ... add items ...

                // Jump to a specific slide (0-based)
                carousel.scrollTo(2);

                // React to slide changes
                carousel.addSlideChangeListener(e ->
                    label.setText("Slide " + (e.getCurrentIndex() + 1)));
                """);
    }

    // ── Slide helper ────────────────────────────────────────────────────────

    /**
     * Builds a demo slide card with a numbered label and a subtle colour.
     */
    private static Div slide(String label, int index) {
        Div card = new Div(new Span(label));
        card.addClassName("demo-carousel-slide--" + ((index % 5) + 1));
        return card;
    }
}



