package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;

import java.util.Optional;

@StyleSheet("context://grid-header.css")
public class GridHeader extends Header {

    private String title;
    private Component[] defaultActions;
    private Component[] contextActions;
    private Grid<?> grid;
    private LabelBuilder<?> labelBuilder;

    public GridHeader(String title) {
        this(title, HeadingLevel.H2);
        setHeadingFontSize(Font.Size.LARGE);
        addClassName("grid-header");
    }

    public GridHeader(LabelBuilder<?> labelBuilder) {
        super(labelBuilder);
        this.labelBuilder = labelBuilder;
        addClassName("grid-header");
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
        if (this.defaultActions != null) {
            for (Component defaultAction : this.defaultActions) {
                defaultAction.setVisible(visible);
            }
        }
    }

    public void setContextActions(Component... components) {
        this.contextActions = components;
        updateActions();
    }

    private void setContextActionsVisible(boolean visible) {
        if (this.contextActions != null) {
            for (Component contextAction : this.contextActions) {
                contextAction.setVisible(visible);
            }
        }
    }

    public void updateActions() {
        setActions(this.defaultActions);
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
        if (size > 0) {
            if (labelBuilder != null) {
                labelBuilder.styleNames("grid-header__label--selected");
            } else if (title != null) {
                setHeading(size + " selected");
            }
            addClassName("grid-header--selected");
            setDefaultActionsVisible(false);
            setContextActionsVisible(true);
        } else {
            if (labelBuilder != null) {
                labelBuilder.styleNames("grid-header__label--default");
            } else if (title != null) {
                setHeading(title);
            }
            removeClassName("grid-header--selected");
            setDefaultActionsVisible(true);
            setContextActionsVisible(false);
        }
    }

}