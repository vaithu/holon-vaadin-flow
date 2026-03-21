package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;

public class GridHeader<T> extends Header {

    private String title;
    private Component[] defaultActions;
    private Component[] contextActions;
    private Grid<T> grid;

    public GridHeader(String title) {
        this(title, HeadingLevel.H2);
        setHeadingFontSize(Font.Size.LARGE);
    }

    public GridHeader(String title, Grid<T> grid) {
        this(title, HeadingLevel.H2);
        setGrid(grid);
    }

    public GridHeader(String title, BeanListing<T> beanListing) {
        this(title, HeadingLevel.H2);
        setGrid(beanListing);
    }

    public GridHeader(String title, HeadingLevel level) {
        super(title, level);
        this.title = title;
        setHeadingFontSize(Font.Size.LARGE);
    }

    public GridHeader(String title, HeadingLevel level, Grid<T>grid) {
        this(title, level);
        setGrid(grid);
    }

    public GridHeader(String title, HeadingLevel level, BeanListing<T> beanListing) {
        this(title, level);
        setGrid(beanListing);
    }

    public void setGrid(Grid<T>grid) {
        grid.addSelectionListener(this::addSelectionListener);
        this.grid = grid;
    }

    private void addSelectionListener(SelectionEvent<Grid<T>, T> selectionEvent) {
        int size = selectionEvent.getAllSelectedItems().size();
        updateActionsVisibility(size);
    }

    @SuppressWarnings("unchecked")
    public void setGrid(BeanListing<T> beanListing) {
        beanListing.addSelectionListener((SelectionListener<Grid<T>, T>) this::addSelectionListener);
        if (beanListing instanceof Grid<?>) {
            this.grid = (Grid<T>) beanListing.getComponent();
        } else {
            throw new IllegalArgumentException("BeanListing is not a grid");
        }
    }

    public void setDefaultActions(Component... components) {
        this.defaultActions = components;
        updateActions();
    }

    private void setDefaultActionsVisible(boolean visible) {
        if (this.defaultActions != null)
            for (Component defaultAction : this.defaultActions) {
                defaultAction.setVisible(visible);
            }
    }

    public void setContextActions(Component... components) {
        this.contextActions = components;
        updateActions();
    }

    private void setContextActionsVisible(boolean visible) {
        if (this.contextActions != null)
            for (Component contextAction : this.contextActions) {
                contextAction.setVisible(visible);
            }
    }

    private void updateActions() {
        this.actions.removeAll();
        addActions(this.defaultActions);
        addActions(this.contextActions);
        updateActionsVisibility(this.grid.getSelectedItems().size());
    }

    private void updateActionsVisibility(int size) {
        if (size == 0) {
            setHeading(this.title);
            setHeadingFontSize(Font.Size.LARGE);
            setHeadingFontWeight(Font.Weight.SEMIBOLD);
            // setHeadingLineHeight(Font.LineHeight.XSMALL);

            removeClassNames(Background.PRIMARY_10);
            setDefaultActionsVisible(true);
            setContextActionsVisible(false);

        } else {
            setHeading(size + " items selected");
            setHeadingFontSize(Font.Size.MEDIUM);
            setHeadingFontWeight(Font.Weight.NORMAL);
            // setHeadingLineHeight(Font.LineHeight.MEDIUM);

            addClassNames(Background.PRIMARY_10);
            setDefaultActionsVisible(false);
            setContextActionsVisible(true);
        }
    }

}