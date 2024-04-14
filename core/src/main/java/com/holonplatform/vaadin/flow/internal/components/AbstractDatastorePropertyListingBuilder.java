package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.*;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.*;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultShortcutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;

import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

public abstract class AbstractDatastorePropertyListingBuilder implements PropertyListingBuilder.DatastorePropertyListingBuilder {


    private final DefaultPropertyListing.DefaultPropertyListingBuilder builder;

    public AbstractDatastorePropertyListingBuilder(DefaultPropertyListing.DefaultPropertyListingBuilder builder) {
        this.builder = builder;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
     * #withColumn(com.holonplatform. core.property.VirtualProperty)
     */
    @Override
    public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder.DatastorePropertyListingBuilder> withColumn(
            VirtualProperty<X> property) {
        ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
        builder.getInstance().addPropertyColumn(property)
                .setValueProvider(new DefaultPropertyListing.VirtualPropertyValueProvider<>(property));
        return new AbstractItemListing.DefaultItemListingColumnBuilder<>(property, builder.getInstance(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withColumn(com.vaadin.flow.function .ValueProvider)
     */
    @Override
    public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder.DatastorePropertyListingBuilder> withColumn(
            ValueProvider<PropertyBox, X> valueProvider) {
        ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
        return withColumn(VirtualProperty.create(Object.class, item -> valueProvider.apply(item)));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
     * #withComponentColumn(com. holonplatform.core.property.VirtualProperty)
     */
    @Override
    public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder.DatastorePropertyListingBuilder> withComponentColumn(
            VirtualProperty<? extends Component> property) {
        ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
        final ItemListingColumn<Property<?>, PropertyBox, ?> column = builder.getInstance()
                .addPropertyColumn(property);
        column.setRenderer(new ComponentRenderer<>(item -> property.getValueProvider().getPropertyValue(item)));
        return new AbstractItemListing.DefaultItemListingColumnBuilder<>(property, builder.getInstance(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withComponentColumn(com.vaadin.flow .function.ValueProvider)
     */
    @Override
    public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder.DatastorePropertyListingBuilder> withComponentColumn(
            ValueProvider<PropertyBox, Component> valueProvider) {
        ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
        return withComponentColumn(VirtualProperty.create(Component.class, item -> valueProvider.apply(item)));
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder hiddenColumns(List<? extends Property<?>> hiddenColumns) {
        builder.hiddenColumns(hiddenColumns);
        return this;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
     * #withValidator(com.holonplatform .core.property.Property,
     * com.holonplatform.core.Validator)
     */
    @Override
    public <V> PropertyListingBuilder.DatastorePropertyListingBuilder withValidator(Property<V> property, Validator<? super V> validator) {
        builder.withValidator(property, validator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
     * #editor(com.holonplatform.core. property.Property,
     * com.holonplatform.core.property.PropertyRenderer)
     */
    @Override
    public <T> PropertyListingBuilder.DatastorePropertyListingBuilder editor(Property<T> property,
                                                                             PropertyRenderer<Input<T>, T> renderer) {
        builder.editor(property, renderer);
        return this;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * editorComponent(java.lang.Object, java.util.function.Function)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder editorComponent(Property<?> property,
                                                                                  Function<PropertyBox, ? extends Component> editorComponentProvider) {
        builder.editorComponent(property, editorComponentProvider);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * displayAsFirst(java.lang.Object)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder displayAsFirst(Property<?> property) {
        builder.displayAsFirst(property);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * displayAsLast(java.lang.Object)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder displayAsLast(Property<?> property) {
        builder.displayAsLast(property);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * displayBefore(java.lang.Object, java.lang.Object)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder displayBefore(Property<?> property, Property<?> beforeProperty) {
        builder.displayBefore(property, beforeProperty);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * displayAfter(java.lang.Object, java.lang.Object)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder displayAfter(Property<?> property, Property<?> afterProperty) {
        builder.displayAfter(property, afterProperty);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * visibleColumns(java.util.List)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder visibleColumns(List<? extends Property<?>> visibleColumns) {
        builder.visibleColumns(visibleColumns);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * sortable(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder sortable(boolean sortable) {
        builder.sortable(sortable);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * sortable(java.lang.Object, boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder sortable(Property<?> property, boolean sortable) {
        builder.sortable(property, sortable);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * resizable(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder resizable(boolean resizable) {
        builder.resizable(resizable);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * resizable(java.lang.Object, boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder resizable(Property<?> property, boolean resizable) {
        builder.resizable(property, resizable);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder mobileColumn(ValueProvider<PropertyBox,Component> valueProvider) {
        builder.mobileColumn(valueProvider);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder mobileColumn(Renderer<PropertyBox> renderer) {
        builder.mobileColumn(renderer);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * visible(java.lang.Object, boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder visible(Property<?> property, boolean visible) {
        builder.visible(property, visible);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * readOnly(java.lang.Object, boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder readOnly(Property<?> property, boolean readOnly) {
        builder.readOnly(property, readOnly);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * frozen(java.lang.Object, boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder frozen(Property<?> property, boolean frozen) {
        builder.frozen(property, frozen);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * frozenColumns(int)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder frozenColumns(int frozenColumnsCount) {
        builder.frozenColumns(frozenColumnsCount);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * width(java.lang.Object, java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder width(Property<?> property, String width) {
        builder.width(property, width);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * flexGrow(java.lang.Object, int)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder flexGrow(Property<?> property, int flexGrow) {
        builder.flexGrow(property, flexGrow);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * styleNameGenerator(java.util. function.Function)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder styleNameGenerator(Function<PropertyBox, String> styleNameGenerator) {
        builder.styleNameGenerator(styleNameGenerator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * styleNameGenerator(java.lang. Object, java.util.function.Function)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder styleNameGenerator(Property<?> property,
                                                                                     Function<PropertyBox, String> styleNameGenerator) {
        builder.styleNameGenerator(property, styleNameGenerator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * expand(java.lang.Object)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder expand(Property<?> property) {
        builder.expand(property);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder autoWidth(Property<?> property, boolean autoWidth) {
        builder.autoWidth(property, autoWidth);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder columnsAutoWidth() {
        builder.columnsAutoWidth();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * alignment(java.lang.Object,
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
     * ColumnAlignment)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder alignment(Property<?> property, ColumnAlignment alignment) {
        builder.alignment(property, alignment);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * renderer(java.lang.Object, com.vaadin.flow.data.renderer.Renderer)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder renderer(Property<?> property, Renderer<PropertyBox> renderer) {
        builder.renderer(property, renderer);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * valueProvider(java.lang.Object, com.vaadin.flow.function.ValueProvider)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder valueProvider(Property<?> property,
                                                                                ValueProvider<PropertyBox, String> valueProvider) {
        builder.valueProvider(property, valueProvider);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * sortComparator(java.lang.Object, java.util.Comparator)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder sortComparator(Property<?> property,
                                                                                 Comparator<PropertyBox> comparator) {
        builder.sortComparator(property, comparator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * sortUsing(java.lang.Object, java.util.List)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder sortUsing(Property<?> property, List<Property<?>> sortProperties) {
        builder.sortUsing(property, sortProperties);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * sortProvider(java.lang.Object, java.util.function.Function)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder sortProvider(Property<?> property,
                                                                               Function<QuerySort.SortDirection, Stream<ItemSort<Property<?>>>> sortProvider) {
        builder.sortProvider(property, sortProvider);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * header(java.lang.Object, com.holonplatform.core.i18n.Localizable)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder header(Property<?> property, Localizable header) {
        builder.header(property, header);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * headerComponent(java.lang.Object, com.vaadin.flow.component.Component)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder headerComponent(Property<?> property, Component header) {
        builder.headerComponent(property, header);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder headerPartName(Property<?> property, String headerPartName) {
        builder.headerPartName(property, headerPartName);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * footer(java.lang.Object, com.holonplatform.core.i18n.Localizable)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder footer(Property<?> property, Localizable footer) {
        builder.footer(property, footer);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder footerPartName(Property<?> property, String footerPartName) {
        builder.footerPartName(property, footerPartName);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * footerComponent(java.lang.Object, com.vaadin.flow.component.Component)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder footerComponent(Property<?> property, Component footer) {
        builder.footerComponent(property, footer);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withColumnPostProcessor(
            ColumnPostProcessor<Property<?>> columnPostProcessor) {
        builder.withColumnPostProcessor(columnPostProcessor);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * pageSize(int)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder pageSize(int pageSize) {
        builder.pageSize(pageSize);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * allRowsVisible(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder allRowsVisible(boolean allRowsVisible) {
        builder.allRowsVisible(allRowsVisible);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * columnReorderingAllowed(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder columnReorderingAllowed(boolean columnReorderingAllowed) {
        builder.columnReorderingAllowed(columnReorderingAllowed);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * itemDetailsRenderer(com.vaadin.flow .data.renderer.Renderer)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder itemDetailsRenderer(Renderer<PropertyBox> renderer) {
        builder.itemDetailsRenderer(renderer);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * itemDetailsVisibleOnClick(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder itemDetailsVisibleOnClick(boolean detailsVisibleOnClick) {
        builder.itemDetailsVisibleOnClick(detailsVisibleOnClick);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * selectionMode(com.holonplatform.
     * vaadin.flow.components.Selectable.SelectionMode)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder selectionMode(Selectable.SelectionMode selectionMode) {
        builder.selectionMode(selectionMode);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder selectAllCheckboxVisibility(
            GridMultiSelectionModel.SelectAllCheckboxVisibility selectAllCheckBoxVisibility) {
        builder.selectAllCheckboxVisibility(selectAllCheckBoxVisibility);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withSelectionListener(com.
     * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withSelectionListener(Selectable.SelectionListener<PropertyBox> selectionListener) {
        builder.withSelectionListener(selectionListener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withItemClickListener(com.
     * holonplatform.vaadin.flow.components.events.ClickEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withItemClickListener(
            ClickEventListener<PropertyListing, ItemClickEvent<PropertyListing, PropertyBox>> listener) {
        builder.withItemClickListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withItemRefreshListener(com.
     * holonplatform.vaadin.flow.components.events.ItemEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withItemRefreshListener(
            ItemEventListener<PropertyListing, PropertyBox, ItemEvent<PropertyListing, PropertyBox>> listener) {
        builder.withItemRefreshListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * multiSort(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder multiSort(boolean multiSort) {
        builder.multiSort(multiSort);
        return this;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * contextMenu()
     */
    @Override
    public ItemListingContextMenuBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder.DatastorePropertyListingBuilder> contextMenu() {
        return new AbstractItemListing.DefaultItemListingContextMenuBuilder<>(builder.getInstance(),
                builder.getInstance().getGrid().addContextMenu(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * header(java.util.function.Consumer)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder header(
            Consumer<EditableItemListingSection<Property<?>>> headerConfigurator) {
        builder.header(headerConfigurator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * footer(java.util.function.Consumer)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder footer(
            Consumer<EditableItemListingSection<Property<?>>> footerConfigurator) {
        builder.footer(footerConfigurator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * editable(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder editable(boolean editable) {
        builder.editable(editable);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * editorBuffered(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder editorBuffered(boolean buffered) {
        builder.editorBuffered(buffered);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withEditorSaveListener(com.vaadin.
     * flow.component.grid.editor.EditorSaveListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withEditorSaveListener(
            ItemListing.EditorSaveListener<PropertyBox, Property<?>> listener) {
        builder.withEditorSaveListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withEditorCancelListener(com.vaadin
     * .flow.component.grid.editor.EditorCancelListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withEditorCancelListener(
            ItemListing.EditorCancelListener<PropertyBox, Property<?>> listener) {
        builder.withEditorCancelListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withEditorOpenListener(com.vaadin.
     * flow.component.grid.editor.EditorOpenListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withEditorOpenListener(
            ItemListing.EditorOpenListener<PropertyBox, Property<?>> listener) {
        builder.withEditorOpenListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withEditorCloseListener(com.vaadin.
     * flow.component.grid.editor.EditorCloseListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withEditorCloseListener(
            ItemListing.EditorCloseListener<PropertyBox, Property<?>> listener) {
        builder.withEditorCloseListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * withValidator(com.holonplatform. core.Validator)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withValidator(Validator<PropertyBox> validator) {
        builder.withValidator(validator);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
     * PropertySetInputGroupConfigurator#
     * defaultValue(com.holonplatform.core.property.Property,
     * java.util.function.Supplier)
     */
    @Override
    public <V> PropertyListingBuilder.DatastorePropertyListingBuilder defaultValue(Property<V> property,
                                                                                   Supplier<V> defaultValueProvider) {
        builder.defaultValue(property, defaultValueProvider);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
     * PropertySetInputGroupConfigurator#
     * withValueChangeListener(com.holonplatform.core.property.Property,
     * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
     */
    @Override
    public <V> PropertyListingBuilder.DatastorePropertyListingBuilder withValueChangeListener(Property<V> property,
                                                                                              ValueHolder.ValueChangeListener<V, GroupValueChangeEvent<V, Property<?>, Input<?>, ItemListing.EditorComponentGroup<Property<?>, PropertyBox>>> listener) {
        builder.withValueChangeListener(property, listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#
     * usePropertyRendererRegistry(com.
     * holonplatform.core.property.PropertyRendererRegistry)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder usePropertyRendererRegistry(
            PropertyRendererRegistry propertyRendererRegistry) {
        builder.usePropertyRendererRegistry(propertyRendererRegistry);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#
     * withValueChangeListener(com.
     * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withValueChangeListener(
            ValueHolder.ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, Input<?>, ItemListing.EditorComponentGroup<Property<?>, PropertyBox>>> listener) {
        builder.withValueChangeListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * required(java.lang.Object)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder required(Property<?> property) {
        builder.required(property);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * required(java.lang.Object, com.holonplatform.core.i18n.Localizable)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder required(Property<?> property, Localizable message) {
        builder.required(property, message);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * withPostProcessor(java.util.function .BiConsumer)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withPostProcessor(BiConsumer<Property<?>, Input<?>> postProcessor) {
        builder.withPostProcessor(postProcessor);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * validationStatusHandler(com.
     * holonplatform.vaadin.flow.components.ValidationStatusHandler)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder validationStatusHandler(
            ValidationStatusHandler<ItemListing.EditorComponentGroup<Property<?>, PropertyBox>> validationStatusHandler) {
        builder.validationStatusHandler(validationStatusHandler);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * groupValidationStatusHandler(com.
     * holonplatform.vaadin.flow.components.GroupValidationStatusHandler)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder groupValidationStatusHandler(
            GroupValidationStatusHandler<ItemListing.EditorComponentGroup<Property<?>, PropertyBox>, Property<?>, Input<?>> groupValidationStatusHandler) {
        builder.groupValidationStatusHandler(groupValidationStatusHandler);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * validationStatusHandler(java.lang. Object,
     * com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder validationStatusHandler(Property<?> property,
                                                                                          ValidationStatusHandler<Input<?>> validationStatusHandler) {
        builder.validationStatusHandler(property, validationStatusHandler);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
     * enableRefreshOnValueChange(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
        builder.enableRefreshOnValueChange(enableRefreshOnValueChange);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
     * java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder id(String id) {
        builder.id(id);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
     * visible(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder visible(boolean visible) {
        builder.visible(visible);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
     * elementConfiguration(java.util. function.Consumer)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder elementConfiguration(Consumer<Element> element) {
        builder.elementConfiguration(element);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
     * withAttachListener(com.vaadin.flow. component.ComponentEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withAttachListener(ComponentEventListener<AttachEvent> listener) {
        builder.withAttachListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
     * withDetachListener(com.vaadin.flow. component.ComponentEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withDetachListener(ComponentEventListener<DetachEvent> listener) {
        builder.withDetachListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
     * withThemeName(java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withThemeName(String themeName) {
        builder.withThemeName(themeName);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
     * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withEventListener(String eventType, DomEventListener listener) {
        builder.withEventListener(eventType, listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
     * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener,
     * java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withEventListener(String eventType, DomEventListener listener,
                                                                                    String filter) {
        builder.withEventListener(eventType, listener, filter);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
     * java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder width(String width) {
        builder.width(width);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
     * java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder height(String height) {
        builder.height(height);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
     * minWidth(java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder minWidth(String minWidth) {
        builder.minWidth(minWidth);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
     * maxWidth(java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder maxWidth(String maxWidth) {
        builder.maxWidth(maxWidth);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
     * minHeight(java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder minHeight(String minHeight) {
        builder.minHeight(minHeight);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
     * maxHeight(java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder maxHeight(String maxHeight) {
        builder.maxHeight(maxHeight);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
     * styleNames(java.lang.String[])
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder styleNames(String... styleNames) {
        builder.styleNames(styleNames);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
     * styleName(java.lang.String)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder styleName(String styleName) {
        builder.styleName(styleName);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
     * enabled(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder enabled(boolean enabled) {
        builder.enabled(enabled);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
     * tabIndex(int)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder tabIndex(int tabIndex) {
        builder.tabIndex(tabIndex);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
     * withFocusListener(com.vaadin.flow. component.ComponentEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withFocusListener(
            ComponentEventListener<FocusNotifier.FocusEvent<Component>> listener) {
        builder.withFocusListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
     * withBlurListener(com.vaadin.flow. component.ComponentEventListener)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withBlurListener(ComponentEventListener<BlurNotifier.BlurEvent<Component>> listener) {
        builder.withBlurListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
     * withFocusShortcut(com.vaadin.flow. component.Key)
     */
    @Override
    public ShortcutConfigurator<PropertyListingBuilder.DatastorePropertyListingBuilder> withFocusShortcut(Key key) {
        return new DefaultShortcutConfigurator<>(builder.getInstance().getGrid().addFocusShortcut(key), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasThemeVariantConfigurator
     * #withThemeVariants(java.lang. Enum[])
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withThemeVariants(GridVariant... variants) {
        builder.withThemeVariants(variants);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
     * frozen(boolean)
     */
    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder frozen(boolean frozen) {
        builder.frozen(frozen);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder rowsDraggable(boolean rowsDraggable) {
        builder.rowsDraggable(rowsDraggable);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder dragFilter(Predicate<PropertyBox> dragFilter) {
        builder.dragFilter(dragFilter);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder dragDataGenerator(String type,
                                                                                    Function<PropertyBox, String> dragDataGenerator) {
        builder.dragDataGenerator(type, dragDataGenerator);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withDragStartListener(
            ItemListingDnDListener<PropertyBox, Property<?>, ItemListingDragStartEvent<PropertyBox, Property<?>>> listener) {
        builder.withDragStartListener(listener);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withDragEndListener(
            ItemListingDnDListener<PropertyBox, Property<?>, ItemListingDragEndEvent<PropertyBox, Property<?>>> listener) {
        builder.withDragEndListener(listener);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder dropMode(GridDropMode dropMode) {
        builder.dropMode(dropMode);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder dropFilter(Predicate<PropertyBox> dropFilter) {
        builder.dropFilter(dropFilter);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withDropListener(
            ItemListingDnDListener<PropertyBox, Property<?>, ItemListingDropEvent<PropertyBox, Property<?>>> listener) {
        builder.withDropListener(listener);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withColumnResizeListener(
            ColumnResizeListener<PropertyBox, Property<?>> listener) {
        builder.withColumnResizeListener(listener);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder withColumnReorderListener(
            ColumnReorderListener<PropertyBox, Property<?>> listener) {
        builder.withColumnReorderListener(listener);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
     */
    @Override
    public PropertyListing build() {
        return builder.build();
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder items(BackEndDataProvider<PropertyBox, Void> dataProvider) {
        builder.items(dataProvider);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder items(CallbackDataProvider.FetchCallback<PropertyBox, Void> fetchCallback) {
        builder.items(fetchCallback);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder items(CallbackDataProvider.FetchCallback<PropertyBox, Void> fetchCallback,
                                                                        CallbackDataProvider.CountCallback<PropertyBox, Void> countCallback) {
        builder.items(fetchCallback, countCallback);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder itemCountEstimate(int itemCountEstimate) {
        builder.itemCountEstimate(itemCountEstimate);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder itemCountEstimateIncrease(int itemCountEstimateIncrease) {
        builder.itemCountEstimateIncrease(itemCountEstimateIncrease);
        return this;
    }

    @Override
    public PropertyListingBuilder.DatastorePropertyListingBuilder itemCountUnknown() {
        builder.itemCountUnknown();
        return this;
    }


}
