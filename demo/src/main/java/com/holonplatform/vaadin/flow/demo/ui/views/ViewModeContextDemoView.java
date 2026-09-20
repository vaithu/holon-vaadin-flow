package com.holonplatform.vaadin.flow.demo.ui.views;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.ResponsiveDiv;
import com.iyensoft.vaadin.flow.components.MaterialAppBar;
import com.iyensoft.vaadin.flow.components.MaterialHeader;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.iyensoft.vaadin.flow.utils.responsive.ViewModeContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;

import java.util.function.Consumer;

/**
 * Demo for {@link ViewModeContext} — the session-scoped {@link ViewMode} signal kept in sync by
 * {@code WindowSizeTracker} — showing two independent components, {@link MaterialAppBar} and
 * {@link MaterialHeader}, that read the same context without being wired to each other.
 *
 * <p>Both components expose a plain {@code setViewMode(ViewMode)} setter and explicitly document
 * that they "do not inspect browser dimensions" themselves — they expect the application to feed
 * the mode in from its own responsive infrastructure. {@link ViewModeContext} is exactly that
 * infrastructure: resize the browser window once and every subscribed component updates, with no
 * manual toggle button and no direct reference between the components.
 */
@PageTitle("ViewModeContext - Holon Demo")
@Route(value = "viewmode-context", layout = DemoMainLayout.class)
public class ViewModeContextDemoView extends Div {

    public ViewModeContextDemoView() {
        addClassName("app-view");

        var title = new H1("ViewModeContext");
        var description = new Paragraph(
                "ViewModeContext holds the current ViewMode as a session-scoped Signal, kept up to "
                        + "date automatically by WindowSizeTracker. Any component, anywhere in the app, "
                        + "can read it — once (a snapshot) or reactively (auto-updating). The two examples "
                        + "below drive MaterialAppBar and MaterialHeader from it: resize the browser window "
                        + "and watch both react, with no button and no direct link between them.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(oneOffSnapshotExample());
        examples.add(liveAppBarExample());
        examples.add(liveHeaderExample());
        examples.add(sharedContextExample());

        add(title, description, examples);
    }

    // ── Example 1 ────────────────────────────────────────────────────────────

    /**
     * A plain, non-reactive snapshot: {@code getCurrent()} is read once, at build time, to pick
     * the initial variant. It will not update again if the window is later resized.
     */
    private DemoExample oneOffSnapshotExample() {
        ViewMode initialMode = ViewModeContext.getCurrent().orElse(ViewMode.DESKTOP);

        var header = Components.materialHeader()
                .variant(initialMode == ViewMode.MOBILE ? MaterialHeader.Variant.SMALL : MaterialHeader.Variant.MEDIUM)
                .headline("Account")
                .subtitle(new Span("Variant chosen once from ViewModeContext.getCurrent()"))
                .primaryAction(new Button("Edit"))
                .build();

        return new DemoExample("One-off read — ViewModeContext.getCurrent()", wrap(header), """
                // A plain snapshot: read once, no further updates even if the window is resized.
                // Here it works safely from the constructor because DemoMainLayout (the outer
                // AppLayout, which attaches before this view) already calls WindowSizeTracker.enable(this)
                // in ITS constructor — so ViewModeContext is already populated by the time this view
                // is built. Without that shell-level tracking, prefer reading it from onAttach()/
                // addAttachListener instead of a view's constructor.
                ViewMode initialMode = ViewModeContext.getCurrent().orElse(ViewMode.DESKTOP);

                MaterialHeader header = Components.materialHeader()
                        .variant(initialMode == ViewMode.MOBILE
                                ? MaterialHeader.Variant.SMALL
                                : MaterialHeader.Variant.MEDIUM)
                        .headline("Account")
                        .primaryAction(new Button("Edit"))
                        .build();
                """);
    }

    // ── Example 2 ────────────────────────────────────────────────────────────

    /**
     * Reactive read: a {@code Signal.effect} keeps {@code MaterialAppBar.setViewMode(...)} in sync
     * with {@link ViewModeContext#getSignal()} for as long as the app bar is attached.
     */
    private DemoExample liveAppBarExample() {
        var appBar = Components.materialAppBar()
                .variant(MaterialAppBar.Variant.SMALL)
                .headline("Inbox")
                .leading(action(VaadinIcon.MENU, "Open navigation"))
                .actions(action(VaadinIcon.PLUS, "Create item"))
                .responsiveAction(new Button("Filter"), "Filter", () -> { })
                .responsiveAction(new Button("Sort"), "Sort", () -> { })
                .build();

        var modeLabel = new Span();
        appBar.addAttachListener(event -> {
                        event.getUI().beforeClientResponse(appBar, context -> {
                                bindViewMode(appBar, appBar::setViewMode);
                                ViewModeContext.getSignal().ifPresent(signal ->
                                                modeLabel.getElement().bindText(signal.map(mode -> "Current ViewMode: " + mode)));
                        });
        });

        var hint = new Paragraph(
                "Resize the browser window: no button, no manual wiring — the label and the app "
                        + "bar's collapsed actions both update from the same ViewModeContext signal.");

        var preview = new Div(modeLabel, appBar, hint);
        preview.getStyle().set("display", "grid").set("gap", "var(--lumo-space-m)");

        return new DemoExample("Live-resize app bar — ViewModeContext.getSignal()", preview, """
                MaterialAppBar appBar = Components.materialAppBar()
                        .variant(MaterialAppBar.Variant.SMALL)
                        .headline("Inbox")
                        .leading(new Button(VaadinIcon.MENU.create()))
                        .actions(new Button(VaadinIcon.PLUS.create()))
                        .responsiveAction(new Button("Filter"), "Filter", () -> {})
                        .responsiveAction(new Button("Sort"), "Sort", () -> {})
                        .build();

                // No manual toggle button needed: ViewModeContext is already kept in sync
                // by WindowSizeTracker.enable(...) at the app-shell level (see DemoMainLayout).
                appBar.addAttachListener(event -> ViewModeContext.getSignal().ifPresent(signal ->
                        Signal.effect(appBar, () -> {
                            ViewMode mode = signal.get();
                            if (mode != null) {
                                appBar.setViewMode(mode);
                            }
                        })));
                """);
    }

    // ── Example 3 ────────────────────────────────────────────────────────────

    /**
     * Same reactive pattern applied to {@link MaterialHeader}.
     */
    private DemoExample liveHeaderExample() {
        var header = Components.materialHeader()
                .variant(MaterialHeader.Variant.MEDIUM)
                .headline("Sensor catalogue")
                .subtitle(new Span("128 active items"))
                .secondaryAction(new Button("Share"))
                .primaryAction(new Button("Edit"))
                .build();

        header.addAttachListener(event -> bindViewMode(header, header::setViewMode));

        return new DemoExample("Live-resize header — ViewModeContext.getSignal()", wrap(header), """
                MaterialHeader header = Components.materialHeader()
                        .variant(MaterialHeader.Variant.MEDIUM)
                        .headline("Sensor catalogue")
                        .subtitle(new Span("128 active items"))
                        .secondaryAction(new Button("Share"))
                        .primaryAction(new Button("Edit"))
                        .build();

                header.addAttachListener(event -> ViewModeContext.getSignal().ifPresent(signal ->
                        Signal.effect(header, () -> {
                            ViewMode mode = signal.get();
                            if (mode != null) {
                                header.setViewMode(mode);
                            }
                        })));
                """);
    }

    // ── Example 4 ────────────────────────────────────────────────────────────

    /**
     * Two components, wired independently, that stay in sync purely because both read the same
     * session-scoped {@link ViewModeContext} signal — no reference between them.
     */
    private DemoExample sharedContextExample() {
        var header = Components.materialHeader()
                .variant(MaterialHeader.Variant.SMALL)
                .headline("Team members")
                .primaryAction(new Button("Invite"))
                .build();

        var appBar = Components.materialAppBar()
                .variant(MaterialAppBar.Variant.SMALL)
                .headline("Team members")
                .leading(action(VaadinIcon.MENU, "Open navigation"))
                .responsiveAction(new Button("Filter"), "Filter", () -> { })
                .build();

        header.addAttachListener(event -> bindViewMode(header, header::setViewMode));
        appBar.addAttachListener(event -> bindViewMode(appBar, appBar::setViewMode));

        var wrapper = ResponsiveDiv.flex().column().gapM().build();
        wrapper.add(new Span("MaterialHeader"), header, new Span("MaterialAppBar"), appBar);

        return new DemoExample("Single source of truth — two unrelated components", wrapper, """
                // Neither component references the other; both simply subscribe to
                // ViewModeContext.getSignal(). Resize the browser once and watch both update.
                header.addAttachListener(event -> ViewModeContext.getSignal().ifPresent(signal ->
                        Signal.effect(header, () -> header.setViewMode(signal.get()))));

                appBar.addAttachListener(event -> ViewModeContext.getSignal().ifPresent(signal ->
                        Signal.effect(appBar, () -> appBar.setViewMode(signal.get()))));
                """);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Keeps {@code setter} in sync with {@link ViewModeContext#getSignal()} for as long as {@code owner} is attached. */
    private static void bindViewMode(Component owner, Consumer<ViewMode> setter) {
                owner.getUI().ifPresent(ui -> ui.beforeClientResponse(owner, context ->
                                ViewModeContext.getSignal().ifPresent(signal ->
                                                Signal.effect(owner, () -> {
                                                        ViewMode mode = signal.get();
                                                        if (mode != null) {
                                                                setter.accept(mode);
                                                        }
                                                }))));
    }

    private static Button action(VaadinIcon icon, String label) {
        var button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        button.setAriaLabel(label);
        return button;
    }

    private static Div wrap(Component component) {
        return new Div(component);
    }
}
