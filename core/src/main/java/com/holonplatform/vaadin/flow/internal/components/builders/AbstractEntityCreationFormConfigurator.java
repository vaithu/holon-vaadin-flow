package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.EntityCreationFormConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityCreationForm;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.StickyActionBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base {@link EntityCreationFormConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractEntityCreationFormConfigurator<C extends EntityCreationFormConfigurator<C>>
        extends AbstractComponentConfigurator<EntityCreationForm, C>
        implements EntityCreationFormConfigurator<C> {

    private final List<ListItem> breadcrumbItems = new ArrayList<>();
    private String title;
    private String subtitle;
    private Component subtitleComponent;
    private String draftBadge;
    private final List<Component> headerActions = new ArrayList<>();
    private final List<FormStepCard> steps = new ArrayList<>();
    private String statusText;
    private StickyActionBar.Variant statusVariant = StickyActionBar.Variant.SUCCESS;
    private String progressLabel;
    private int progressPercent = 0;
    private boolean hasProgress = false;
    private final List<Component> barActions = new ArrayList<>();

    public AbstractEntityCreationFormConfigurator(EntityCreationForm component) {
        super(component);
    }

    @Override
    public C breadcrumb(ListItem... items) {
        if (items != null) {
            for (ListItem item : items) {
                if (item != null) breadcrumbItems.add(item);
            }
        }
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
        this.subtitleComponent = null;
        return getConfigurator();
    }

    @Override
    public C subtitle(Component component) {
        this.subtitleComponent = component;
        this.subtitle = null;
        return getConfigurator();
    }

    @Override
    public C draftBadge(String badgeText) {
        this.draftBadge = badgeText;
        return getConfigurator();
    }

    @Override
    public C headerAction(Component... components) {
        if (components != null) {
            for (Component c : components) {
                if (c != null) headerActions.add(c);
            }
        }
        return getConfigurator();
    }

    @Override
    public C step(FormStepCard card) {
        if (card != null) steps.add(card);
        return getConfigurator();
    }

    @Override
    public C steps(FormStepCard... cards) {
        if (cards != null) {
            for (FormStepCard card : cards) {
                if (card != null) steps.add(card);
            }
        }
        return getConfigurator();
    }

    @Override
    public C status(String text, StickyActionBar.Variant variant) {
        this.statusText    = text;
        this.statusVariant = variant != null ? variant : StickyActionBar.Variant.SUCCESS;
        return getConfigurator();
    }

    @Override
    public C progress(String label, int percent) {
        this.progressLabel   = label;
        this.progressPercent = percent;
        this.hasProgress     = true;
        return getConfigurator();
    }

    @Override
    public C barAction(Component... components) {
        if (components != null) {
            for (Component c : components) {
                if (c != null) barActions.add(c);
            }
        }
        return getConfigurator();
    }

    /**
     * Applies all accumulated state to the {@link EntityCreationForm} component.
     * Must be called from {@code build()} in concrete builder implementations.
     */
    protected void applyState() {
        EntityCreationForm form = getComponent();
        if (title != null) form.setHeader(title);
        if (subtitleComponent != null) {
            form.setSubtitle(subtitleComponent);
        } else if (subtitle != null) {
            form.setSubtitle(subtitle);
        }
        if (draftBadge != null && !draftBadge.isBlank()) {
            form.setDraftBadge(draftBadge);
        }
        if (!breadcrumbItems.isEmpty()) {
            form.setBreadcrumbItems(breadcrumbItems);
        }
        if (!headerActions.isEmpty()) {
            form.setPageHeaderActions(headerActions);
        }
        for (FormStepCard step : steps) {
            form.addStep(step);
        }
        if (statusText != null) {
            form.setStatus(statusText, statusVariant);
        }
        if (hasProgress && progressLabel != null) {
            form.setProgress(progressLabel, progressPercent);
        }
        if (!barActions.isEmpty()) {
            form.addBarAction(barActions.toArray(Component[]::new));
        }
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
