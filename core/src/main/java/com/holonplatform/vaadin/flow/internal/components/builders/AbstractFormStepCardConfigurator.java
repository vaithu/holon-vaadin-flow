package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FormStepCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;
import com.iyensoft.vaadin.flow.components.builders.PanelConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base {@link FormStepCardConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractFormStepCardConfigurator<C extends FormStepCardConfigurator<C>>
        extends AbstractComponentConfigurator<FormStepCard, C>
        implements FormStepCardConfigurator<C> {

    private int stepNumber = 1;
    private int totalSteps = 1;
    private String title;
    private String subtitle;
    private FormStepCard.StepState state = FormStepCard.StepState.PENDING;
    private String helpText;
    private final List<Component> contentComponents = new ArrayList<>();

    public AbstractFormStepCardConfigurator(FormStepCard component) {
        super(component);
    }

    @Override
    public C stepNumber(int stepNumber) {
        this.stepNumber = Math.max(1, stepNumber);
        return getConfigurator();
    }

    @Override
    public C totalSteps(int totalSteps) {
        this.totalSteps = Math.max(1, totalSteps);
        return getConfigurator();
    }

    @Override
    public C title(String title) {
        this.title = title;
        return getConfigurator();
    }

    @Override
    public C subtitle(String subtitle) {
        this.subtitle = subtitle;
        return getConfigurator();
    }

    @Override
    public C helpText(String helpText) {
        this.helpText = helpText;
        return getConfigurator();
    }

    @Override
    public C state(FormStepCard.StepState state) {
        this.state = state != null ? state : FormStepCard.StepState.PENDING;
        return getConfigurator();
    }

    @Override
    public C content(Component... components) {
        if (components != null) {
            for (Component c : components) {
                if (c != null) contentComponents.add(c);
            }
        }
        return getConfigurator();
    }

    /**
     * Assembles the DOM into the FormStepCard component.
     * Must be called from {@code build()} in concrete builder implementations.
     */
    protected void assembleCard() {
        FormStepCard card = getComponent();

        Span badge = new Span(state == FormStepCard.StepState.DONE ? "✓" : String.valueOf(stepNumber));
        badge.addClassName("fsc__badge");

        Span counter = new Span("STEP " + stepNumber + " OF " + totalSteps);
        counter.addClassName("fsc__counter");

        var panelConfigurator = PanelConfigurator.configure(card).card();
        var headerBuilder = panelConfigurator.header().prefix(badge).sticky(false);

        if (title != null && !title.isEmpty()) {
            headerBuilder.heading(title);
        }

        if (subtitle != null && !subtitle.isEmpty()) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.addClassName("fsc__subtitle");
            headerBuilder.details(subtitleSpan);
        }

        headerBuilder.actions(counter).add();

        if (!contentComponents.isEmpty()) {
            panelConfigurator.content(contentComponents.toArray(Component[]::new));
        }

        if (helpText != null && !helpText.isEmpty()) {
            Span helpSpan = new Span(helpText);
            helpSpan.addClassName("fsc__help");
            panelConfigurator.footer().details(helpSpan).add();
        }

        card.setState(state);
        card.initBadge(badge, stepNumber);
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
