package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.Initializer;
import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.Operation;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.GridMainView;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.components.css.CSSConstants;
import com.holonplatform.vaadin.flow.internal.BeanRecord;
import com.holonplatform.vaadin.flow.internal.CrudDialogs;
import com.holonplatform.vaadin.flow.internal.CrudNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractGridMainView<T> implements GridMainView<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractGridMainView.class);
    private SearchBarBuilder searchBarBuilder;
    private LabelBuilder<Span> bulkActionLabelBuilder;
    private BulkActionBuilder bulkActionBuilder;
    private boolean isShowAndHideColumnsEnabled;
    private BeanRecord<T> beanRecord;

    private T selectedItem;
    private T oldEditingItem;
    private BeanListingBuilder<T> beanListingBuilder;

    public AbstractGridMainView(BeanRecord<T> beanRecord) {

        ObjectUtils.argumentNotNull(beanRecord, "BeanRecord must not be null here");

        this.beanRecord = beanRecord;

    }

    @Override
    public LabelBuilder<Span> createBulkActionLabelBuilder() {
        return bulkActionLabelBuilder = Components.span();
    }

    @Override
    public SearchBarBuilder createSearchBarBuilder() {
        return searchBarBuilder = Components.input.searchBar()
                .fullWidth()
                .search(stringValueChangeEvent -> refreshGrid(stringValueChangeEvent.getValue()))
                .newButton(this::newBtnActionPerformed)
                .optionsMenuBar(createSearchOptionsMenuBar())
                .withPostProcessor(configurator -> {
                    if (isShowAndHideColumnsEnabled) {
                        createShowHideColumnsButton(configurator);
                    }
                });
    }

    @Override
    public BulkActionBuilder createBulkActionBuilder() {
        return bulkActionBuilder = Components.bulkActionBar()
                .styleNames(
                        com.holonplatform.vaadin.flow.internal.lumo.Background.PRIMARY_10.getClassName(),
                        "bulk-action-bar--padded"
                )
                .selectAll(BooleanInputBuilder.create()
                        .withValueChangeListener(booleanValueChangeEvent -> {

                            if (booleanValueChangeEvent.getValue()) {
                                getBeanListing().selectAll();
                            } else {
                                deselectAll();
                            }
                        }))
                .selected(createBulkActionLabelBuilder())
                .optionsMenuBar(createBulkActionMenuBar())

                .closeButton(closeButtonConfigurator -> closeButtonConfigurator.onClick(buttonClickEvent -> {

                    showBulkActionBar(false);
                    showSearchBar(true);
                    deselectAll();
                }))
                .visible(false)

                ;
    }

    private void deselectAll() {
        getBeanListing().deselectAll();
    }

    @Override
    public void showAndHideGridColumns(boolean visible) {
        isShowAndHideColumnsEnabled = visible;
    }

    private void createShowHideColumnsButton(SearchBarConfigurator<SearchBarBuilder> configurator) {
        DefaultShowAndHideColumns<T> defaultShowAndHideColumns = new DefaultShowAndHideColumns<>();
        defaultShowAndHideColumns.showAndHideColumns(getBeanListing());
        configurator.getLayout().addComponentAtIndex(2, defaultShowAndHideColumns.getShowHideBtn());
        configurator.getLayout().addComponentAtIndex(3, defaultShowAndHideColumns.getPopover());

    }

    @Override
    public void showBulkActionBar(boolean visible) {
        searchBarBuilder.visible(!visible);
    }

    @Override
    public void showSearchBar(boolean visible) {
        bulkActionBuilder.visible(!visible);
    }

    @Override
    public void addGridSelectionEvent(Selectable.SelectionEvent<T> selectionEvent) {
        int selectedItems = selectionEvent.getAllSelectedItems().size();

        if (selectedItems > 0) {

            bulkActionLabelBuilder.text(String.format("%d selected", selectedItems));
            selectionEvent.getFirstSelectedItem().ifPresent(this::setCurrentItem);
            showSearchBar(false);
            showBulkActionBar(true);
        } else {
            showSearchBar(true);
            showBulkActionBar(false);
        }
    }

    @Override
    public Component createGrid(boolean mobile) {

        initializeBeanListingIfNull(mobile);

        if (mobile) {
            createMobileBeanListing();
        } else {
            createDesktopBeanListing();
        }
        return getBeanListing().getComponent();
    }

    @Override
    public Component createGrid() {
        return createGrid(false);
    }

    private void initializeBeanListingIfNull(boolean mobile) {

        if (mobile && (!Objects.equals(beanListingBuilder, getBeanListingBuilderForMobile()))) {
            beanListingBuilder = getBeanListingBuilderForMobile();
        } else if (!Objects.equals(beanListingBuilder, getBeanListingBuilderForDesktop())) {
            beanListingBuilder = getBeanListingBuilderForDesktop();
        }
    }
    @Override
    public BeanListingBuilder<T> getBeanListingBuilderForMobile() {
        return  BeanListing.builder(beanRecord.beanClass(), false);
    }
    @Override
    public BeanListingBuilder<T> getBeanListingBuilderForDesktop() {
        return BeanListing.builder(beanRecord.beanClass());
    }
    @Override
    public void setBeanListingBuilder(BeanListingBuilder<T> beanListingBuilder) {
        this.beanListingBuilder = beanListingBuilder;
    }

    private void createDesktopBeanListing() {
        this.beanListingBuilder
                .fullSize()
                .visibleColumns(beanRecord.columnList().stream().map(Path::getName).toList())
                .sortable(false)
                .columnsAutoWidth()
                .styleNames(CSSConstants.CARD, CSSConstants.Grid.GRID_CUSTOM_STYLE)
                .columnReorderingAllowed(true)
                .multiSelect()
                .toggleableColumns()
                .withSelectionListener(this::addGridSelectionEvent)
                .withItemClickListener(event -> {
                    setCurrentItem(event.getItem());
                });
                /*.partNameGenerator(newEditingItem -> {
                    if (oldEditingItem != null && Objects.equals(newEditingItem, oldEditingItem)) {
                        return CSSConstants.Grid.RECENTLY_EDITED_ROW;
                    } else if (getCurrentItem().stream().allMatch(t -> Objects.equals(t, newEditingItem))) {
                        return CSSConstants.Grid.HIGHLIGHT_ROW;
                    }
                    return null;
                });*/


    }

    private void createMobileBeanListing() {
        this.beanListingBuilder
                .fullSize()
                .multiSelect()
                .withItemClickListener(event -> setCurrentItem(event.getItem()))
                .withSelectionListener(this::addGridSelectionEvent)
                .withComponentColumn(this::addMobileComponentColumn);

    }

   /* private void highlightEditedUnit(Unit modifiedUnit) {
        Optional<Unit> prevUnit = Optional.ofNullable(lastModifiedUnit);
        lastModifiedUnit = modifiedUnit;
        refreshGrid(modifiedUnit);
        prevUnit.ifPresent(unit -> refreshGrid(unit));
    }*/

    @Override
    public void highlightEditedItem(T editingInstance) {
        Optional<T> prevItem = Optional.ofNullable(oldEditingItem);
        oldEditingItem = editingInstance;
        setCurrentItem(oldEditingItem);

        getBeanListing().deselect(oldEditingItem);
        refreshItem(oldEditingItem);
        prevItem.ifPresent(this::refreshItem);

    }

    @Override
    public void refreshItem(T beanInstance) {
        getBeanListing().refreshItem(beanInstance);
    }

    @Override
    public Optional<T> getCurrentItem() {
        log.info("Current item is {}", selectedItem);
        return Optional.ofNullable(selectedItem);
    }

    @Override
    public void showDeleteResult(boolean success) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(success);
            }

            @Override
            public void executeMethod() {
                refreshGrid();
                deselectAll();
            }
        };

        CrudDialogs.deleteDialog(operation);
    }

    @Override
    public void showDeleteResult(Initializer<Boolean> initializer) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(initializer.get());
            }

            @Override
            public void executeMethod() {
                refreshGrid();
                deselectAll();
            }
        };

        CrudDialogs.deleteDialog(operation);
    }

    @Override
    public void showInsertResult(boolean success) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(success);
            }

            @Override
            public void executeMethod() {
                refreshGrid();
                deselectAll();
            }
        };

        CrudNotification.insertNotification(operation);
    }

    @Override
    public void showInsertResult(Initializer<Boolean> initializer) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(initializer.get());
            }

            @Override
            public void executeMethod() {
                refreshGrid();
                deselectAll();
            }
        };

        CrudNotification.insertNotification(operation);
    }

    @Override
    public void showSaveResult(boolean success) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(success);
            }

            @Override
            public void executeMethod() {
                refreshGrid();
                deselectAll();
            }
        };

        CrudNotification.saveNotification(operation);
    }

    @Override
    public void showSaveResult(Initializer<Boolean> initializer) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(initializer.get());
            }

            @Override
            public void executeMethod() {
                refreshGrid();
                deselectAll();
            }
        };

        CrudNotification.saveNotification(operation);
    }

    @Override
    public void showUpdateResult(boolean success) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(success);
            }

            @Override
            public void executeMethod() {
//                beanListing.refreshItem(selectedItem);
                highlightEditedItem(selectedItem);
//                beanListing.select(selectedItem);
            }
        };

        CrudNotification.updateNotification(operation);
    }

    @Override
    public void showUpdateResult(Initializer<Boolean> initializer) {
        Operation operation = new Operation() {
            @Override
            public void execute(Consumer<Boolean> result) {
                result.accept(initializer.get());
            }

            @Override
            public void executeMethod() {
                ObjectUtils.argumentNotNull(selectedItem, "The selected item must not be null here");
//                beanListing.refreshItem(selectedItem);
                highlightEditedItem(selectedItem);
//                beanListing.select(selectedItem);

            }
        };

        CrudNotification.updateNotification(operation);
    }

    @Override
    public BeanRecord<T> getBeanRecord() {
        return beanRecord;
    }

    @Override
    public BeanListing<T> getBeanListing() {
        return beanListingBuilder.build();
    }

    @Override
    public void setBeanRecord(BeanRecord<T> beanRecord) {
        ObjectUtils.argumentNotNull(beanRecord, "BeanRecord must not be null");
        this.beanRecord = beanRecord;
    }

    @Override
    public void setCurrentItem(T selectedItem) {
        ObjectUtils.argumentNotNull(selectedItem, "Current Item must not be null");
        this.selectedItem = selectedItem;
    }

    @Override
    public void setCurrentItem(GridLazyDataView<T> gridLazyDataView) {
        try {
            setCurrentItem(gridLazyDataView.getItem(0));
        } catch (IndexOutOfBoundsException e) {
            getBeanListing().setEmptyStateText("No records found!");
            log.error("Unable to get first item", e);
        }
    }

}
