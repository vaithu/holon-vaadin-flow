package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.WizardFrameConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.WizardFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

/**
 * Base {@link WizardFrameConfigurator} implementation.
 *
 * <p>Holds all builder state and provides implementations for every configurator method.
 * Subclasses supply the concrete configurator type via {@link #getConfigurator()} and
 * may override {@link #buildWizardFrame()} to customize assembly.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractWizardFrameConfigurator<C extends WizardFrameConfigurator<C>>
        implements WizardFrameConfigurator<C> {

    // ── Builder state ─────────────────────────────────────────────────────────

    protected String title = "Wizard";
    protected final List<WizardFrame.WizardStep> steps = new ArrayList<>();

    protected Breadcrumb breadcrumb;
    protected Component[] headerActions;
    protected Component[] headerPrefix;
    protected Consumer<Header> headerConfig;

    protected Consumer<ButtonBuilder> backBtnConfig;
    protected Consumer<ButtonBuilder> nextBtnConfig;
    protected Consumer<ButtonBuilder> finishBtnConfig;
    protected Consumer<WizardFrame> onFinish;
    protected IntPredicate beforeNext;
    protected IntConsumer onStepChanged;

    private List<Consumer<WizardFrame>> postProcessors;

    // ── Abstract hook ─────────────────────────────────────────────────────────

    /**
     * Returns the concrete configurator instance for fluent chaining.
     *
     * @return this configurator
     */
    protected abstract C getConfigurator();

    // ── WizardFrameConfigurator implementation ────────────────────────────────

    @Override
    public C title(String title) {
        this.title = title != null ? title : "Wizard";
        return getConfigurator();
    }

    @Override
    public C breadcrumb(ListItem... items) {
        if (items != null && items.length > 0) {
            Breadcrumb b = new Breadcrumb();
            ListItem[] nonNull = Arrays.stream(items).filter(i -> i != null).toArray(ListItem[]::new);
            if (nonNull.length > 0) {
                b.addWithSeparators(nonNull);
            }
            this.breadcrumb = b;
        }
        return getConfigurator();
    }

    @Override
    public C breadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
        return getConfigurator();
    }

    @Override
    public C headerActions(Component... components) {
        this.headerActions = components;
        return getConfigurator();
    }

    @Override
    public C headerPrefix(Component... components) {
        this.headerPrefix = components;
        return getConfigurator();
    }

    @Override
    public C configureHeader(Consumer<Header> config) {
        this.headerConfig = (this.headerConfig == null) ? config : this.headerConfig.andThen(config);
        return getConfigurator();
    }

    @Override
    public C step(String label, Component content) {
        this.steps.add(new WizardFrame.WizardStep(label, null, content));
        return getConfigurator();
    }

    @Override
    public C step(String label, String nextButtonLabel, Component content) {
        this.steps.add(new WizardFrame.WizardStep(label, nextButtonLabel, content));
        return getConfigurator();
    }

    @Override
    public C backButton(Consumer<ButtonBuilder> config) {
        this.backBtnConfig = config;
        return getConfigurator();
    }

    @Override
    public C nextButton(Consumer<ButtonBuilder> config) {
        this.nextBtnConfig = config;
        return getConfigurator();
    }

    @Override
    public C finishButton(Consumer<ButtonBuilder> config) {
        this.finishBtnConfig = config;
        return getConfigurator();
    }

    @Override
    public C onFinish(Consumer<WizardFrame> onFinish) {
        this.onFinish = onFinish;
        return getConfigurator();
    }

    @Override
    public C beforeNext(IntPredicate guard) {
        this.beforeNext = guard;
        return getConfigurator();
    }

    @Override
    public C onStepChanged(IntConsumer onStepChanged) {
        this.onStepChanged = onStepChanged;
        return getConfigurator();
    }

    // ── Post-processor support ────────────────────────────────────────────────

    /**
     * Registers a callback invoked with the assembled {@link WizardFrame} at the end of
     * {@code build()}, before it is returned. Multiple calls compose in registration order.
     *
     * @param postProcessor consumer called with the built wizard; {@code null} is ignored
     * @return this configurator
     */
    public C withBuildPostProcessor(Consumer<WizardFrame> postProcessor) {
        if (postProcessor != null) {
            if (postProcessors == null) postProcessors = new ArrayList<>();
            postProcessors.add(postProcessor);
        }
        return getConfigurator();
    }

    // ── Protected build helpers ───────────────────────────────────────────────

    /**
     * Assembles and returns a new {@link WizardFrame} from the accumulated state.
     *
     * @return the configured {@link WizardFrame}
     * @throws IllegalStateException if no steps have been configured
     */
    protected WizardFrame buildWizardFrame() {
        if (steps.isEmpty()) {
            throw new IllegalStateException("WizardFrame: at least one step() must be configured");
        }
        WizardFrame frame = new WizardFrame(title, steps, buildHeaderConfig(),
                backBtnConfig, nextBtnConfig, finishBtnConfig,
                onFinish, beforeNext, onStepChanged);
        applyPostProcessors(frame);
        return frame;
    }

    /**
     * Applies all registered post-processors to the given frame.
     *
     * @param frame the assembled wizard frame
     */
    protected void applyPostProcessors(WizardFrame frame) {
        if (postProcessors != null) {
            postProcessors.forEach(pp -> pp.accept(frame));
        }
    }

    private Consumer<Header> buildHeaderConfig() {
        Consumer<Header> config = null;
        if (breadcrumb != null) {
            final Breadcrumb b = breadcrumb;
            config = h -> h.setBreadcrumb(b);
        }
        if (headerPrefix != null && headerPrefix.length > 0) {
            final Component[] prefix = headerPrefix;
            config = andThen(config, h -> h.setPrefix(prefix));
        }
        if (headerActions != null && headerActions.length > 0) {
            final Component[] actions = headerActions;
            config = andThen(config, h -> h.setActions(actions));
        }
        if (headerConfig != null) {
            config = andThen(config, headerConfig);
        }
        return config;
    }

    private static Consumer<Header> andThen(Consumer<Header> existing, Consumer<Header> next) {
        return existing == null ? next : existing.andThen(next);
    }
}
