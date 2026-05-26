package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.utils.responsive.IyenResponsiveLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("IyenResponsive – Holon Demo")
@Route(value = "iyen-responsive", layout = DemoMainLayout.class)
public class IyenResponsiveDemoView extends Div {

    public IyenResponsiveDemoView() {
        addClassName("app-view");

        var title = new H1("IyenResponsiveLayout");

        var desc = new Paragraph(
                "IyenResponsiveLayout is a reactive master-detail layout that automatically recomposes itself "
                + "based on the browser viewport size. On mobile it shows master only; on tablet and above it shows "
                + "master + detail side by side. It uses the same component instances—no re-render on resize. "
                + "Subscribe to mode changes via addModeChangeListener() or the reactive viewModeSignal().");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(separatorExample());
        examples.add(forceModeMobileExample());
        examples.add(forceModeDesktopExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var masterContent = new Layout();
        masterContent.add(new H3("Master Panel"));
        masterContent.add(new Paragraph("This is the master content. On mobile viewports, only this panel is visible."));

        var detailContent = new Layout();
        detailContent.add(new H3("Detail Panel"));
        detailContent.add(new Paragraph("This is the detail content. Visible on tablet and larger viewports."));

        var masterBuilder = IyenMasterBuilder.create(masterContent);
        var detailBuilder = IyenDetailBuilder.create(detailContent);

        var layout = new IyenResponsiveLayout(masterBuilder, detailBuilder);
        layout.setWidthFull();

        return new DemoExample("Basic Master-Detail", layout, """
                var masterBuilder = IyenMasterBuilder.create(masterLayout);
                var detailBuilder = IyenDetailBuilder.create(detailLayout);

                var layout = new IyenResponsiveLayout(masterBuilder, detailBuilder);
                // MOBILE: master only
                // TABLET+: master + detail side by side""");
    }

    private DemoExample separatorExample() {
        var masterContent = new Layout();
        masterContent.add(new H3("Left"));
        masterContent.add(new Paragraph("Master with separator"));

        var detailContent = new Layout();
        detailContent.add(new H3("Right"));
        detailContent.add(new Paragraph("Detail with separator"));

        var masterBuilder = IyenMasterBuilder.create(masterContent);
        var detailBuilder = IyenDetailBuilder.create(detailContent);

        var layout = new IyenResponsiveLayout(masterBuilder, detailBuilder)
                .withSeparator(SeparatorColor.CONTRAST_20);
        layout.setWidthFull();

        return new DemoExample("With Separator", layout, """
                var layout = new IyenResponsiveLayout(masterBuilder, detailBuilder)
                    .withSeparator(SeparatorColor.CONTRAST_20);
                // Adds a visual divider between master and detail""");
    }

    private DemoExample forceModeMobileExample() {
        var modeLabel = new Span("Current mode: —");

        var masterContent = new Layout();
        masterContent.add(new H3("Master (forced MOBILE)"));
        masterContent.add(new Paragraph("This layout is forced to MOBILE mode — only master is visible."));

        var detailContent = new Layout();
        detailContent.add(new H3("Detail (hidden)"));

        var masterBuilder = IyenMasterBuilder.create(masterContent);
        var detailBuilder = IyenDetailBuilder.create(detailContent);

        var layout = new IyenResponsiveLayout(masterBuilder, detailBuilder, ViewMode.MOBILE);
        layout.setWidthFull();
        layout.addModeChangeListener(mode -> modeLabel.setText("Current mode: " + mode.name()));

        var wrapper = new Div(modeLabel, layout);
        return new DemoExample("Force Mobile Mode", wrapper, """
                var layout = new IyenResponsiveLayout(
                    masterBuilder, detailBuilder, ViewMode.MOBILE);

                layout.addModeChangeListener(mode ->
                    label.setText("Current mode: " + mode.name()));""");
    }

    private DemoExample forceModeDesktopExample() {
        var modeLabel = new Span("Current mode: —");

        var masterContent = new Layout();
        masterContent.add(new H3("Master (forced DESKTOP)"));
        masterContent.add(new Paragraph("Left pane"));

        var detailContent = new Layout();
        detailContent.add(new H3("Detail (forced DESKTOP)"));
        detailContent.add(new Paragraph("Right pane—always visible when forced to DESKTOP."));

        var masterBuilder = IyenMasterBuilder.create(masterContent);
        var detailBuilder = IyenDetailBuilder.create(detailContent);

        var layout = new IyenResponsiveLayout(masterBuilder, detailBuilder, ViewMode.DESKTOP)
                .withSeparator(SeparatorColor.PRIMARY);
        layout.setWidthFull();
        layout.addModeChangeListener(mode -> modeLabel.setText("Current mode: " + mode.name()));

        var wrapper = new Div(modeLabel, layout);
        return new DemoExample("Force Desktop Mode", wrapper, """
                var layout = new IyenResponsiveLayout(
                    masterBuilder, detailBuilder, ViewMode.DESKTOP)
                    .withSeparator(SeparatorColor.PRIMARY);

                layout.addModeChangeListener(mode ->
                    label.setText("Current mode: " + mode.name()));""");
    }
}
