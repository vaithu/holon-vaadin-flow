package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.WizardFrameBuilder;
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
 * Default implementation of {@link WizardFrameBuilder}.
 */
public final class DefaultWizardFrameBuilder implements WizardFrameBuilder {

    private String title = "Wizard";
    private final List<WizardFrame.WizardStep> steps = new ArrayList<>();

    // Header customisation — composed into a single Consumer<Header> at build time
    private Breadcrumb breadcrumb;
    private Component[] headerActions;
    private Component[] headerPrefix;
    private Consumer<Header> headerConfig;

    private Consumer<ButtonBuilder> backBtnConfig;
    private Consumer<ButtonBuilder> nextBtnConfig;
    private Consumer<ButtonBuilder> finishBtnConfig;
    private Consumer<WizardFrame> onFinish;
    private IntPredicate beforeNext;
    private IntConsumer onStepChanged;

    @Override
    public WizardFrameBuilder title(String title) {
        this.title = title != null ? title : "Wizard";
        return this;
    }

    @Override
    public WizardFrameBuilder breadcrumb(ListItem... items) {
        if (items != null && items.length > 0) {
            Breadcrumb b = new Breadcrumb();
            ListItem[] nonNull = Arrays.stream(items).filter(i -> i != null).toArray(ListItem[]::new);
            if (nonNull.length > 0) {
                b.addWithSeparators(nonNull);
            }
            this.breadcrumb = b;
        }
        return this;
    }

    @Override
    public WizardFrameBuilder breadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
        return this;
    }

    @Override
    public WizardFrameBuilder headerActions(Component... components) {
        this.headerActions = components;
        return this;
    }

    @Override
    public WizardFrameBuilder headerPrefix(Component... components) {
        this.headerPrefix = components;
        return this;
    }

    @Override
    public WizardFrameBuilder configureHeader(Consumer<Header> config) {
        this.headerConfig = (this.headerConfig == null) ? config : this.headerConfig.andThen(config);
        return this;
    }

    @Override
    public WizardFrameBuilder step(String label, Component content) {
        this.steps.add(new WizardFrame.WizardStep(label, null, content));
        return this;
    }

    @Override
    public WizardFrameBuilder step(String label, String nextButtonLabel, Component content) {
        this.steps.add(new WizardFrame.WizardStep(label, nextButtonLabel, content));
        return this;
    }

    @Override
    public WizardFrameBuilder backButton(Consumer<ButtonBuilder> config) {
        this.backBtnConfig = config;
        return this;
    }

    @Override
    public WizardFrameBuilder nextButton(Consumer<ButtonBuilder> config) {
        this.nextBtnConfig = config;
        return this;
    }

    @Override
    public WizardFrameBuilder finishButton(Consumer<ButtonBuilder> config) {
        this.finishBtnConfig = config;
        return this;
    }

    @Override
    public WizardFrameBuilder onFinish(Consumer<WizardFrame> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    @Override
    public WizardFrameBuilder beforeNext(IntPredicate guard) {
        this.beforeNext = guard;
        return this;
    }

    @Override
    public WizardFrameBuilder onStepChanged(IntConsumer onStepChanged) {
        this.onStepChanged = onStepChanged;
        return this;
    }

    @Override
    public WizardFrame build() {
        if (steps.isEmpty()) {
            throw new IllegalStateException("WizardFrame: at least one step() must be configured");
        }
        // Compose all header customisations into one consumer
        Consumer<Header> composedHeader = buildHeaderConfig();
        return new WizardFrame(title, steps, composedHeader,
                backBtnConfig, nextBtnConfig, finishBtnConfig,
                onFinish, beforeNext, onStepChanged);
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
