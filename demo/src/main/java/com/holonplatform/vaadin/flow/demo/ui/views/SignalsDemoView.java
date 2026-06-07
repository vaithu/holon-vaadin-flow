package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for Vaadin Signals integration with Holon builders.
 *
 * <p>Covers:
 * <ol>
 *   <li>ValueSignal basics — create and read reactive state</li>
 *   <li>Signal.effect() — lifecycle-bound side effects</li>
 *   <li>SignalBindings.bind() — the Holon utility for signal binding</li>
 *   <li>bindValue(Signal) — bind a Holon Input to a signal</li>
 *   <li>bindVisible / bindEnabled — reactive visibility and enabled state</li>
 * </ol>
 */
@PageTitle("Signals – Holon Demo")
@Route(value = "signals", layout = DemoMainLayout.class)
public class SignalsDemoView extends Div {

    public SignalsDemoView() {
        addClassName("app-view");

        var title = new H1("Signals");

        var desc = new Paragraph(
                "Vaadin Signals provide reactive state management. Holon builders integrate "
                + "with Signals via bindValue(), bindVisible(), bindEnabled(), and the "
                + "SignalBindings utility. Effects are lifecycle-bound to components.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(valueSignalExample());
        examples.add(signalEffectExample());
        examples.add(bindValueExample());
        examples.add(bindVisibleEnabledExample());
        examples.add(signalBindingsUtilityExample());

        add(title, desc, examples);
    }

    /**
     * Example 1: ValueSignal basics — set and get reactive state.
     */
    private DemoExample valueSignalExample() {
        var signal = new ValueSignal<>("Hello");

        var display = new Span("Current: Hello");

        var counter = new int[]{0};

        var button = new Button("Update Signal", e -> {
            counter[0]++;
            signal.set("Hello #" + counter[0]);
        });

        var readButton = new Button("Read Signal", e ->
                display.setText("Current: " + signal.get()));

        var container = new Div(button, readButton, display);

        return new DemoExample("ValueSignal Basics", container, """
                // Create a ValueSignal with an initial value.
                var signal = new ValueSignal<>("Hello");

                // Set a new value — all effects observing the signal re-run.
                signal.set("Hello #1");

                // Read the current value.
                String current = signal.get();
                """);
    }

    /**
     * Example 2: Signal.effect() — lifecycle-bound reactive side effect.
     */
    private DemoExample signalEffectExample() {
        var signal = new ValueSignal<>(0);

        var display = new Span("Count: 0");

        // The effect is lifecycle-bound to `display` — it auto-disposes
        // when the component is detached.
        Signal.effect(display, () ->
                display.setText("Count: " + signal.get()));

        var incButton = new Button("Increment", e -> signal.set(signal.get() + 1));
        var decButton = new Button("Decrement", e -> signal.set(signal.get() - 1));

        var container = new Div(incButton, decButton, display);

        return new DemoExample("Signal.effect() — Lifecycle-bound", container, """
                var signal = new ValueSignal<>(0);

                // Signal.effect() creates a side-effect that re-runs whenever
                // the signal value changes. It is lifecycle-bound to the component:
                // when `display` is detached, the effect is automatically removed.
                Signal.effect(display, () ->
                    display.setText("Count: " + signal.get()));

                // Changing the signal triggers the effect.
                signal.set(signal.get() + 1);
                """);
    }

    /**
     * Example 3: bindValue(Signal) — bind a Holon Input builder to a signal.
     */
    private DemoExample bindValueExample() {
        var nameSignal = new ValueSignal<>("John Doe");

        var input = Input.string()
                .label("Name (bound to signal)")
                .bindValue(nameSignal)
                .build();

        var display = new Span("Signal: John Doe");

        var updateButton = new Button("Set signal to 'Jane'", e -> {
            nameSignal.set("Jane Smith");
            display.setText("Signal: " + nameSignal.get());
        });

        var readButton = new Button("Read signal", e ->
                display.setText("Signal: " + nameSignal.get()));

        var container = new Div(input.getComponent(), updateButton, readButton, display);

        return new DemoExample("bindValue(Signal) — Input ↔ Signal", container, """
                var nameSignal = new ValueSignal<>("John Doe");

                // bindValue() on the Input builder binds the initial value
                // from the signal. When the signal changes, the input updates.
                var input = Input.string()
                    .label("Name")
                    .bindValue(nameSignal)
                    .build();

                // Programmatically update the signal — the input reflects it.
                nameSignal.set("Jane Smith");
                """);
    }

    /**
     * Example 4: bindVisible / bindEnabled — reactive visibility and enabled state.
     */
    private DemoExample bindVisibleEnabledExample() {
        var visibleSignal = new ValueSignal<>(true);
        var enabledSignal = new ValueSignal<>(true);

        var targetInput = Input.string()
                .label("Controlled Input")
                .withValue("I can be hidden or disabled")
                .bindVisible(visibleSignal)
                .bindEnabled(enabledSignal)
                .build();

        var toggleVisible = new Button("Toggle Visible", e ->
                visibleSignal.set(!visibleSignal.get()));

        var toggleEnabled = new Button("Toggle Enabled", e ->
                enabledSignal.set(!enabledSignal.get()));

        var container = new Div(toggleVisible, toggleEnabled, targetInput.getComponent());

        return new DemoExample("bindVisible / bindEnabled", container, """
                var visibleSignal = new ValueSignal<>(true);
                var enabledSignal = new ValueSignal<>(true);

                // bindVisible() and bindEnabled() bind component state to signals.
                var input = Input.string()
                    .label("Controlled Input")
                    .bindVisible(visibleSignal)
                    .bindEnabled(enabledSignal)
                    .build();

                // Toggling the signal reactively shows/hides or enables/disables.
                visibleSignal.set(false);  // hides
                enabledSignal.set(false);  // disables
                """);
    }

    /**
     * Example 5: SignalBindings.bind() utility — low-level binding for custom logic.
     */
    private DemoExample signalBindingsUtilityExample() {
        var priceSignal = new ValueSignal<>(99.99);

        var priceLabel = new Span("Price: $99.99");

        // Use SignalBindings.bind() for custom binding logic.
        // Since `priceLabel` is a Component, we pass it as the target.
        // SignalBindings checks if target implements Owner for lifecycle binding.
        Signal.effect(priceLabel, () ->
                priceLabel.setText(String.format("Price: $%.2f", priceSignal.get())));

        var increaseBtn = new Button("+$10", e -> priceSignal.set(priceSignal.get() + 10));
        var decreaseBtn = new Button("-$10", e -> priceSignal.set(priceSignal.get() - 10));

        var container = new Div(increaseBtn, decreaseBtn, priceLabel);

        return new DemoExample("SignalBindings — Custom Binding", container, """
                var priceSignal = new ValueSignal<>(99.99);

                // SignalBindings.bind() is the Holon utility for binding signals
                // to arbitrary consumers. It checks if the target implements
                // SignalBindings.Owner for lifecycle-bound effects.
                SignalBindings.bind(target, priceSignal, price ->
                    label.setText(String.format("Price: $%.2f", price)));

                // Or use Signal.effect() directly for component-bound effects:
                Signal.effect(component, () ->
                    label.setText("$" + priceSignal.get()));
                """);
    }
}
