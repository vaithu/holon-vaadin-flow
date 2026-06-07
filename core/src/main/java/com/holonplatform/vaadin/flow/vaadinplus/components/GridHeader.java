package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.selection.SelectionEvent;

import java.util.Optional;

@StyleSheet("context://grid-header.css")
public class GridHeader extends Header {

    private static final String NO_TITLE_CLASS = "grid-header--no-title";
    private static final String HIDDEN_ACTION_CLASS = "grid-header__action--hidden";
    private static final String SELECTION_COUNT_CLASS = "grid-header__selection-count";
    private static final String TITLE_CLASS = "grid-header__title";

    private String title;
    private Component[] defaultActions;
    private Component[] contextActions;
    private final Span selectionCount = new Span("0 selected");
    private Component titleComponent;
    private boolean selectionCountVisible;
    private Grid<?> grid;
    private LabelBuilder<?> labelBuilder;

    public GridHeader(String title) {
        this(title, HeadingLevel.H2);
        setHeadingFontSize(Font.Size.LARGE);
        addClassName("grid-header");
        configureSelectionCount();
    }

    public GridHeader(LabelBuilder<?> labelBuilder) {
        super(labelBuilder);
        this.labelBuilder = labelBuilder;
        addClassName("grid-header");
        configureSelectionCount();
    }

    public GridHeader(String title, Grid<?> grid) {
        this(title, HeadingLevel.H2);
        setGrid(grid);
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public GridHeader(String title, HeadingLevel level) {
        super(title, level);
        this.title = title;
        setHeadingFontSize(Font.Size.LARGE);
        addClassName("grid-header");
        configureSelectionCount();
    }

    public GridHeader(String title, HeadingLevel level, Grid<?> grid) {
        this(title, level);
        setGrid(grid);
    }

    // ── Localizable constructors ────────────────────────────────���─────────────

    public GridHeader(Localizable title) {
        this(resolve(title), HeadingLevel.H2);
    }

    public GridHeader(Localizable title, Grid<?> grid) {
        this(resolve(title), HeadingLevel.H2);
        setGrid(grid);
    }

    public GridHeader(Localizable title, HeadingLevel level) {
        this(resolve(title), level);
    }

    public GridHeader(Localizable title, HeadingLevel level, Grid<?> grid) {
        this(resolve(title), level);
        setGrid(grid);
    }

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    public void setGrid(Grid<?> grid) {
        this.grid = grid;
        this.grid.addSelectionListener(this::addSelectionListener);
    }

    private void addSelectionListener(SelectionEvent<? extends Grid<?>, ?> selectionEvent) {
        int size = selectionEvent.getAllSelectedItems().size();
        updateActionsVisibility(size);
    }

    public void setDefaultActions(Component... components) {
        this.defaultActions = components;
        updateActions();
    }

    private void setDefaultActionsVisible(boolean visible) {
        setActionsVisible(this.defaultActions, visible);
    }

    public void setContextActions(Component... components) {
        this.contextActions = components;
        updateActions();
    }

    private void setContextActionsVisible(boolean visible) {
        setActionsVisible(this.contextActions, visible);
    }

    public void updateActions() {
        updateNoTitleState();
        updatePrefix();
        setActions();
        addActions(this.defaultActions);
        addActions(this.contextActions);

        if (this.grid != null) {
            updateActionsVisibility(this.grid.getSelectedItems().size());
        } else {
            updateActionsVisibility(0);
        }
    }

    /**
     * Update header appearance and actions visibility based on selection size.
     */
    public void updateActionsVisibility(int size) {
        updateNoTitleState();
        if (size > 0) {
            addClassName("grid-header--selected");
            selectionCount.setText(size + " selected");
            setSelectionCountVisible(true);
            setDefaultActionsVisible(false);
            setContextActionsVisible(true);
        } else {
            removeClassName("grid-header--selected");
            selectionCount.setText("0 selected");
            setSelectionCountVisible(false);
            setDefaultActionsVisible(true);
            setContextActionsVisible(false);
        }
        updatePrefix();
    }

    private void setActionsVisible(Component[] components, boolean visible) {
        if (components == null) {
            return;
        }
        for (Component component : components) {
            if (component != null) {
                setActionVisible(component, visible);
            }
        }
    }

    private void setActionVisible(Component component, boolean visible) {
        component.setVisible(visible);
        if (visible) {
            component.removeClassName(HIDDEN_ACTION_CLASS);
        } else {
            component.addClassName(HIDDEN_ACTION_CLASS);
        }
    }

    private void configureSelectionCount() {
        selectionCount.addClassName(SELECTION_COUNT_CLASS);
        setSelectionCountVisible(false);
        if (this.titleComponent == null) {
            this.titleComponent = createTitleComponent();
        }
        hideHeadingColumn();
        updatePrefix();
    }

    private void updatePrefix() {
        boolean hasSelection = selectionCountVisible;
        boolean hasVisibleTitle = hasVisibleTitle();

        if (hasSelection) {
            hideHeadingColumn();
            setPrefix(this.selectionCount);
        } else if (hasVisibleTitle) {
            setHeading(this.titleComponent);
        } else {
            hideHeadingColumn();
            setPrefix();
        }
    }

    private Component createTitleComponent() {
        Component component;
        if (labelBuilder != null) {
            component = labelBuilder.build();
        } else {
            component = new Span(title != null ? title : "");
        }
        component.addClassName(TITLE_CLASS);
        return component;
    }

    private void hideHeadingColumn() {
        setHeadingVisible(false);
    }

    private void setSelectionCountVisible(boolean visible) {
        this.selectionCountVisible = visible;
        selectionCount.setVisible(visible);
        setActionVisible(selectionCount, visible);
    }

    private void updateNoTitleState() {
        if (hasVisibleTitle()) {
            removeClassName(NO_TITLE_CLASS);
        } else {
            addClassName(NO_TITLE_CLASS);
        }
    }

    private boolean hasVisibleTitle() {
        return labelBuilder != null || (title != null && !title.isBlank());
    }

}