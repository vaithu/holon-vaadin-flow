package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.WizardFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;

import java.util.function.Consumer;

/**
 * Fluent configurator for {@link WizardFrame} components.
 *
 * @param <C> concrete configurator type for fluent chaining
 */
public interface WizardFrameConfigurator<C extends WizardFrameConfigurator<C>> {

    /**
     * Sets the wizard title shown in the header.
     *
     * @param title wizard title (not null)
     * @return this configurator for chaining
     */
    C title(String title);

    /**
     * Sets a breadcrumb trail above the header title.
     * <p>
     * Build items with {@link com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem} (clickable link)
     * and {@link com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbPage} (current page, non-clickable).
     * Both extend {@link ListItem}.
     * </p>
     *
     * <pre>{@code
     * .breadcrumb(
     *     new BreadcrumbItem("Customers", CustomersView.class),
     *     new BreadcrumbPage("New Customer")
     * )
     * }</pre>
     *
     * @param items breadcrumb list items in display order (not null)
     * @return this configurator for chaining
     */
    C breadcrumb(ListItem... items);

    /**
     * Sets a pre-built {@link Breadcrumb} component in the header.
     *
     * @param breadcrumb the breadcrumb component (not null)
     * @return this configurator for chaining
     */
    C breadcrumb(Breadcrumb breadcrumb);

    /**
     * Sets components in the header actions slot (right of the title).
     * <p>
     * Use this to add pill badges, Cancel buttons, and primary action buttons
     * that appear alongside the wizard title. These are <em>header</em> actions,
     * distinct from the footer Back / Next navigation.
     * </p>
     *
     * <pre>{@code
     * .headerActions(
     *     new Tag("New"),
     *     Components.button().text("Cancel").build(),
     *     Components.button().preset(ButtonPreset.SAVE).text("Create").build()
     * )
     * }</pre>
     *
     * @param components action components (not null)
     * @return this configurator for chaining
     */
    C headerActions(Component... components);

    /**
     * Sets components in the header prefix slot (left of the title).
     * <p>
     * Typically used for an avatar or an icon that identifies the entity being created.
     * </p>
     *
     * @param components prefix components (not null)
     * @return this configurator for chaining
     */
    C headerPrefix(Component... components);

    /**
     * Escape hatch for full {@link Header} customisation not covered by the
     * dedicated convenience methods.
     * <p>
     * The consumer is called <em>after</em> the title and stepper have been set,
     * so you can override, augment, or inspect any header property.
     * </p>
     *
     * <pre>{@code
     * .configureHeader(h -> {
     *     h.setHeadingFontSize(Font.Size.XXLARGE);
     *     h.setBordered(false);
     * })
     * }</pre>
     *
     * @param config header configurator (not null)
     * @return this configurator for chaining
     */
    C configureHeader(Consumer<Header> config);

    /**
     * Adds a wizard step.
     *
     * @param label   step label shown in the {@link com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper}
     * @param content the step content component (e.g. {@code EntityFormPanel.bean(Foo.class).noFooter().build()})
     * @return this configurator for chaining
     */
    C step(String label, Component content);

    /**
     * Adds a wizard step with a custom Next button label for this specific step.
     *
     * @param label           step label shown in the stepper
     * @param nextButtonLabel custom label for the Next button while on this step
     * @param content         the step content component
     * @return this configurator for chaining
     */
    C step(String label, String nextButtonLabel, Component content);

    /**
     * Configures the Back button appearance.
     *
     * @param config button builder consumer (do not add click listeners)
     * @return this configurator for chaining
     */
    C backButton(Consumer<ButtonBuilder> config);

    /**
     * Configures the Next button appearance.
     *
     * @param config button builder consumer (do not add click listeners)
     * @return this configurator for chaining
     */
    C nextButton(Consumer<ButtonBuilder> config);

    /**
     * Configures the Finish button appearance (shown on the last step).
     *
     * @param config button builder consumer (do not add click listeners)
     * @return this configurator for chaining
     */
    C finishButton(Consumer<ButtonBuilder> config);

    /**
     * Registers a callback invoked when the user clicks Finish on the last step.
     *
     * @param onFinish finish callback (receives the {@link WizardFrame} instance)
     * @return this configurator for chaining
     */
    C onFinish(Consumer<WizardFrame> onFinish);

    /**
     * Registers an optional guard called before the wizard advances to the next step.
     *
     * @param guard a predicate receiving the current step index (0-based)
     * @return this configurator for chaining
     */
    C beforeNext(java.util.function.IntPredicate guard);

    /**
     * Registers a callback invoked whenever the wizard navigates to a different step.
     *
     * @param onStepChanged listener receiving the new step index
     * @return this configurator for chaining
     */
    C onStepChanged(java.util.function.IntConsumer onStepChanged);
}
