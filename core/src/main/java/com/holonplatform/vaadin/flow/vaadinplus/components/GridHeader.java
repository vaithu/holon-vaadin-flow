package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;

import java.util.Optional;

public class GridHeader extends Header {

    private String title;
    private Component[] defaultActions;
    private Component[] contextActions;
    private Grid<?> grid;
    // Keep a reference so we can fall back to it when title is null
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
     * Uses `title` when not null; otherwise falls back to LabelBuilder if present.
     */
    public void updateActionsVisibility(int size) {
        if (size == 0) {

            if (this.title != null) {
                setHeading(title);
                setHeadingFontSize(Font.Size.LARGE);
                setHeadingFontWeight(Font.Weight.SEMIBOLD);
                // setHeadingLineHeight(Font.LineHeight.XSMALL);


            } else {
                labelBuilder.styleNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
            }

            removeClassNames(Background.PRIMARY_10);
            setDefaultActionsVisible(true);
            setContextActionsVisible(false);

        } else {
            if (this.title != null) {
                setHeading(size + " items selected");
                setHeadingFontSize(Font.Size.MEDIUM);
                setHeadingFontWeight(Font.Weight.NORMAL);
                // setHeadingLineHeight(Font.LineHeight.MEDIUM);


            } else {
                labelBuilder.styleNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.NORMAL);
            }

            addClassNames(Background.PRIMARY_10);
            setDefaultActionsVisible(false);
            setContextActionsVisible(true);

        }
    }

}