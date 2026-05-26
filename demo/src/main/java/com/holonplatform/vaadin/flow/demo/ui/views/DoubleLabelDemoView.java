package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.DoubleLabel;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("DoubleLabel – Holon Demo")
@Route(value = "double-label", layout = DemoMainLayout.class)
public class DoubleLabelDemoView extends Div {

    public DoubleLabelDemoView() {
        addClassName("app-view");

        var title = new H1("DoubleLabel");

        var desc = new Paragraph(
                "A stacked two-line label with primary (top) and secondary (bottom) text. "
                + "Supports alignment (left/center), sizing (fixed/grow), and border removal modifiers. "
                + "Styled via double-label.css.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(defaultExample());
        examples.add(alignLeftExample());
        examples.add(noBorderExample());
        examples.add(multipleLabelsExample());

        add(title, desc, examples);
    }

    private DemoExample defaultExample() {
        var label = new DoubleLabel("Revenue", "$12,450");

        return new DemoExample("Default (Center-Aligned)", label, """
                new DoubleLabel("Revenue", "$12,450")""");
    }

    private DemoExample alignLeftExample() {
        var label = new DoubleLabel("Customer Name", "Acme Corporation");
        label.setAlignLeft();

        return new DemoExample("Left-Aligned", label, """
                var label = new DoubleLabel("Customer Name", "Acme Corporation");
                label.setAlignLeft();""");
    }

    private DemoExample noBorderExample() {
        var label = new DoubleLabel("Status", "Active");
        label.setNoBorder();

        return new DemoExample("No Border", label, """
                var label = new DoubleLabel("Status", "Active");
                label.setNoBorder();""");
    }

    private DemoExample multipleLabelsExample() {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);

        var lbl1 = new DoubleLabel("Q1", "$3,200");
        lbl1.setFixedWidth();
        var lbl2 = new DoubleLabel("Q2", "$4,100");
        lbl2.setFixedWidth();
        var lbl3 = new DoubleLabel("Q3", "$5,150");
        lbl3.setFixedWidth();

        layout.add(lbl1, lbl2, lbl3);

        return new DemoExample("Multiple with Fixed Width", layout, """
                var lbl1 = new DoubleLabel("Q1", "$3,200");
                lbl1.setFixedWidth();
                var lbl2 = new DoubleLabel("Q2", "$4,100");
                lbl2.setFixedWidth();
                // ... add to HorizontalLayout""");
    }
}
