/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.components.builders;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemClickListener;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.data.ItemDataSource.ItemSort;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * {@link ItemListing} configurator.
 *
 * @param <T> Item type
 * @param <P> Item property type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ItemListingConfigurator<T, P, C extends ItemListingConfigurator<T, P, C>>
		extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
		FocusableConfigurator<Component, C>, HasDataSourceConfigurator<T, C> {

	/**
	 * Configure the column represented by given <code>property</code> to be displayed before any other listing column
	 * by default.
	 * @param property The property which represents the column to display as first (not null)
	 * @return this
	 */
	C displayAsFirst(P property);

	/**
	 * Configure the column represented by given <code>property</code> to be displayed after any other listing column by
	 * default.
	 * @param property The property which represents the column to display as last (not null)
	 * @return this
	 */
	C displayAsLast(P property);

	/**
	 * Configure the column represented by given <code>property</code> id to be displayed before the column which
	 * corresponds to the id specified by the given <code>beforeProperty</code>.
	 * @param property Property which represents the column to display before the other property (not null)
	 * @param beforeProperty Property which represents the column before which the first property has to be displayed
	 *        (not null)
	 * @return this
	 */
	C displayBefore(P property, P beforeProperty);

	/**
	 * Configure the column represented by given <code>property</code> id to be displayed after the column which
	 * corresponds to the id specified by the given <code>afterProperty</code>.
	 * @param property Property which represents the column to display after the other property (not null)
	 * @param afterProperty Property which represents the column after which the first property has to be displayed (not
	 *        null)
	 * @return this
	 */
	C displayAfter(P property, P afterProperty);

	/**
	 * Add a column which contents will be rendered as a {@link Component} using given <code>valueProvider</code>.
	 * @param valueProvider The value provider to use to provide the column {@link Component} using the current row item
	 *        instance (not null)
	 * @return An {@link ItemListingColumnBuilder} which allow further column configuration and provides the
	 *         {@link ItemListingColumnBuilder#add()} method to add the column to the listing
	 */
	ItemListingColumnBuilder<T, P, C> withComponentColumn(ValueProvider<T, Component> valueProvider);

	/**
	 * Set the visible columns list, using the item properties as column reference. The columns will be displayed in the
	 * order thay are provided.
	 * <p>
	 * Any property display order declaration configured using {@link #displayAsFirst(Object)},
	 * {@link #displayAsLast(Object)}, {@link #displayBefore(Object, Object)} and {@link #displayAfter(Object, Object)}
	 * will be ignored.
	 * </p>
	 * @param visibleColumns The visible column properties (not null)
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default C visibleColumns(P... visibleColumns) {
		return visibleColumns(Arrays.asList(visibleColumns));
	}

	/**
	 * Set the visible columns list, using the item properties as column reference. The columns will be displayed in the
	 * order thay are provided.
	 * <p>
	 * Any property display order declaration configured using {@link #displayAsFirst(Object)},
	 * {@link #displayAsLast(Object)}, {@link #displayBefore(Object, Object)} and {@link #displayAfter(Object, Object)}
	 * will be ignored.
	 * </p>
	 * @param visibleColumns The visible column properties (not null)
	 * @return this
	 */
	C visibleColumns(List<? extends P> visibleColumns);

	/**
	 * Set whether all the item listing columns are user-sortable.
	 * @param sortable Whether all the item listing columns are user-sortable
	 * @return this
	 */
	C sortable(boolean sortable);

	/**
	 * Set whether the column which corresponds to given property is user-sortable.
	 * @param property The property to configure (not null)
	 * @param sortable Whether given property is user-sortable
	 * @return this
	 */
	C sortable(P property, boolean sortable);

	/**
	 * Set whether all the item listing columns are user-resizable.
	 * @param resizable Whether all the item listing columns are user-resizable
	 * @return this
	 */
	C resizable(boolean resizable);

	/**
	 * Set whether the column which corresponds to given property is user-resizable.
	 * @param property The property to configure (not null)
	 * @param resizable Whether given property is user-resizable
	 * @return this
	 */
	C resizable(P property, boolean resizable);

	/**
	 * Set whether the column which corresponds to given property is visible.
	 * @param property The property to configure (not null)
	 * @param visible Whether given property is visible
	 * @return this
	 */
	C visible(P property, boolean visible);

	/**
	 * Set the column which corresponds to given property as hidden.
	 * @param property The property to configure (not null)
	 * @return this
	 * @see #visible(Object, boolean)
	 */
	default C hidden(P property) {
		return visible(property, false);
	}

	/**
	 * Set whether the column which corresponds to given property is frozen.
	 * @param property The property to configure (not null)
	 * @param frozen Whether given property is frozen
	 * @return this
	 */
	C frozen(P property, boolean frozen);

	/**
	 * Sets the number of frozen columns in this listing.
	 * @param frozenColumnsCount The number of columns that should be frozen
	 * @return this
	 */
	C frozenColumns(int frozenColumnsCount);

	/**
	 * Sets the width of the column which corresponds to given property as a CSS-string.
	 * @param property The property to configure (not null)
	 * @param width the width to set
	 * @return this
	 */
	C width(P property, String width);

	/**
	 * Sets the flex grow ratio for the column which corresponds to given property.
	 * <p>
	 * When set to 0, column width is fixed.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param flexGrow the flex grow ratio to set
	 * @return this
	 */
	C flexGrow(P property, int flexGrow);

	/**
	 * Sets the text alignment for the column which corresponds to given property.
	 * <p>
	 * Default is {@link ColumnAlignment#LEFT}.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param alignment the text alignment to set
	 * @return this
	 */
	C alignment(P property, ColumnAlignment alignment);

	/**
	 * Sets the {@link Renderer} to use for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param renderer The column renderer to use
	 * @return this
	 */
	C renderer(P property, Renderer<T> renderer);

	/**
	 * Sets the {@link ValueProvider} to use to obtain the text to display in the column which corresponds to given
	 * property.
	 * @param property The property to configure (not null)
	 * @param renderer The value provider to use
	 * @return this
	 */
	C valueProvider(P property, ValueProvider<T, String> valueProvider);

	/**
	 * Sets comparator to use with in-memory sorting for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param renderer The comparator to use with in-memory sorting
	 * @return this
	 */
	C sortComparator(P property, Comparator<T> comparator);

	/**
	 * Set the properties to use to implement the sort logic to apply when the column which corresponds to given
	 * property is user-sorted.
	 * @param property The property to configure (not null)
	 * @param sortProperties The properties to use to sort the column
	 * @return this
	 */
	C sortUsing(P property, List<P> sortProperties);

	/**
	 * Set the properties to use to implement the sort logic to apply when the column which corresponds to given
	 * property is user-sorted.
	 * @param property The property to configure (not null)
	 * @param sortProperties The properties to use to sort the column
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default C sortUsing(P property, P... sortProperties) {
		return sortUsing(property, Arrays.asList(sortProperties));
	}

	/**
	 * Set the function to use to obtain the {@link ItemSort}s to use when the column which corresponds to given
	 * property is user-sorted.
	 * @param property The property to configure (not null)
	 * @param sortProvider Sort provider
	 * @return this
	 */
	C sortProvider(P property, Function<SortDirection, Stream<ItemSort<P>>> sortProvider);

	/**
	 * Set the header text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param header Localizable column header text (not null)
	 * @return this
	 */
	C header(P property, Localizable header);

	/**
	 * Set the header text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param header The column header text
	 * @return this
	 */
	default C header(P property, String header) {
		return header(property, Localizable.builder().message(header).build());
	}

	/**
	 * Set the header text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param defaultHeader The default column header text
	 * @param headerMessageCode The column header text translation message code
	 * @return this
	 */
	default C header(P property, String defaultHeader, String headerMessageCode) {
		return header(property, Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
	}

	/**
	 * Set the {@link Component} to use as header for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param header The column header component
	 * @return this
	 */
	C headerComponent(P property, Component header);

	/**
	 * Hides the listing headers section.
	 * @return this
	 */
	C hideHeaders();

	/**
	 * Sets the page size, which is the number of items fetched at a time from the data source.
	 * <p>
	 * Note: the number of items in the server-side memory can be considerably higher than the page size, since the
	 * component can show more than one page at a time.
	 * </p>
	 * @param pageSize the maximum number of items sent per request. Should be greater than zero
	 * @return this
	 */
	C pageSize(int pageSize);

	/**
	 * If <code>true</code>, the listing's height is defined by the number of its rows. All items are fetched from the
	 * data provider, and the Grid shows no vertical scroll bar.
	 * @param heightByRows <code>true</code> to make listing compute its height by the number of rows,
	 *        <code>false</code> for the default behavior
	 * @return this
	 */
	C heightByRows(boolean heightByRows);

	/**
	 * Sets whether column reordering is allowed or not.
	 * @param columnReorderingAllowed <code>true</code> if column reordering is allowed
	 * @return this
	 */
	C columnReorderingAllowed(boolean columnReorderingAllowed);

	/**
	 * Set the renderer to use for displaying the item details rows.
	 * @param renderer the renderer to use for displaying item details rows
	 * @return this
	 */
	C itemDetailsRenderer(Renderer<T> renderer);

	/**
	 * Set the function to use for displaying the item details rows as a text.
	 * @param textProvider The function to provide the item details text
	 * @return this
	 */
	default C itemDetailsText(Function<T, String> textProvider) {
		ObjectUtils.argumentNotNull(textProvider, "Text provider must be not null");
		return itemDetailsRenderer(new TextRenderer<>(item -> textProvider.apply(item)));
	}

	/**
	 * Set the function to use for displaying the item details rows as a {@link Component}.
	 * @param componentProvider The function to provide the item details {@link Component}
	 * @return this
	 */
	default C itemDetailsComponent(Function<T, Component> componentProvider) {
		ObjectUtils.argumentNotNull(componentProvider, "Component provider must be not null");
		return itemDetailsRenderer(new ComponentRenderer<>(item -> componentProvider.apply(item)));
	}

	/**
	 * Sets whether the item details can be opened and closed by clicking the rows or not.
	 * @param detailsVisibleOnClick <code>true</code> to enable opening and closing item details by clicking the rows,
	 *        <code>false</code> otherwise
	 * @return this
	 * @see #setItemDetailsRenderer(Renderer)
	 */
	C itemDetailsVisibleOnClick(boolean detailsVisibleOnClick);

	/**
	 * Set the listing selection mode.
	 * @param selectionMode The selection mode to set (not null). Use {@link SelectionMode#NONE} to disable selection.
	 * @return this
	 */
	C selectionMode(SelectionMode selectionMode);

	/**
	 * Add a {@link SelectionListener} to listen to items selection changes.
	 * <p>
	 * {@link SelectionListener}s are triggred only when listing is selectable, i.e. (i.e. {@link SelectionMode} is not
	 * {@link SelectionMode#NONE}).
	 * </p>
	 * @param selectionListener The selection listener to add (not null)
	 * @return this
	 */
	C withSelectionListener(SelectionListener<T> selectionListener);

	/**
	 * Adds a listener that gets notified when user clicks on an item row.
	 * @param listener The {@link ItemClickListener} to add (not null)
	 * @return this
	 */
	C withItemClickListener(ItemClickListener<T> listener);

	/**
	 * Sets whether multiple column sorting is enabled on the client-side.
	 * @param multiSort <code>true</code> to enable sorting of multiple columns on the client-side, <code>false</code>
	 *        to disable
	 * @return this
	 */
	C multiSort(boolean multiSort);

	/**
	 * Enables or disables the vertical scrolling on the Grid web component. By default, the scrolling is enabled.
	 * @param enabled <code>true</code> to enable vertical scrolling, <code>false</code> to disabled it
	 * @return this
	 */
	C verticalScrollingEnabled(boolean enabled);

	/**
	 * Add given theme variants to the listing component.
	 * @param variants The theme variants to add
	 * @return this
	 */
	C withThemeVariants(GridVariant... variants);

	// TODO header/footer builders

	// TODO style generators

	// -------

	/**
	 * Enumeration of column text alignments.
	 */
	public enum ColumnAlignment {

		/**
		 * Left aligned column text
		 */
		LEFT,

		/**
		 * Centered column text
		 */
		CENTER,

		/**
		 * Right aligned column text
		 */
		RIGHT;

	}

	/**
	 * ItemListing column configurator.
	 *
	 * @param <T> Item type
	 * @param <P> Item property type
	 * @param <C> Concrete configurator type
	 * 
	 * @since 5.2.0
	 */
	public interface ItemListingColumnConfigurator<T, P, C extends ItemListingColumnConfigurator<T, P, C>> {

		/**
		 * Set whether the column is user-resizable.
		 * @param resizable Whether the column is user-resizable
		 * @return this
		 */
		C resizable(boolean resizable);

		/**
		 * Set whether the column is visible.
		 * @param visible Whether the column is visible
		 * @return this
		 */
		C visible(boolean visible);

		/**
		 * Set whether the column is frozen.
		 * @param frozen Whether the column is frozen
		 * @return this
		 */
		C frozen(boolean frozen);

		/**
		 * Sets the width of the column as a CSS-string.
		 * @param width the width to set
		 * @return this
		 */
		C width(String width);

		/**
		 * Sets the flex grow ratio for the column.
		 * <p>
		 * When set to 0, column width is fixed.
		 * </p>
		 * @param flexGrow the flex grow ratio to set
		 * @return this
		 */
		C flexGrow(int flexGrow);

		/**
		 * Sets comparator to use with in-memory sorting for the column.
		 * @param renderer The comparator to use with in-memory sorting
		 * @return this
		 */
		C sortComparator(Comparator<T> comparator);

		/**
		 * Set the properties to use to implement the sort logic to apply when the column is user-sorted.
		 * @param sortProperties The properties to use to sort the column
		 * @return this
		 */
		C sortUsing(List<P> sortProperties);

		/**
		 * Set the properties to use to implement the sort logic to apply when the column is user-sorted.
		 * @param sortProperties The properties to use to sort the column
		 * @return this
		 */
		@SuppressWarnings("unchecked")
		default C sortUsing(P... sortProperties) {
			return sortUsing(Arrays.asList(sortProperties));
		}

		/**
		 * Set the function to use to obtain the {@link ItemSort}s to use when the column is user-sorted.
		 * @param sortProvider Sort provider
		 * @return this
		 */
		C sortProvider(Function<SortDirection, Stream<ItemSort<P>>> sortProvider);

		/**
		 * Set the header text for the column.
		 * @param header Localizable column header text (not null)
		 * @return this
		 */
		C header(Localizable header);

		/**
		 * Set the header text for the column.
		 * @param header The column header text
		 * @return this
		 */
		default C header(String header) {
			return header(Localizable.builder().message(header).build());
		}

		/**
		 * Set the header text for the column.
		 * @param defaultHeader The default column header text
		 * @param headerMessageCode The column header text translation message code
		 * @return this
		 */
		default C header(String defaultHeader, String headerMessageCode) {
			return header(Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
		}

		/**
		 * Set the {@link Component} to use as header for the column.
		 * @param header The column header component
		 * @return this
		 */
		C headerComponent(Component header);

		/**
		 * Configure the column to be displayed before any other listing column.
		 * @return this
		 */
		C displayAsFirst();

		/**
		 * Configure the column to be displayed after any other listing column by default.
		 * @return this
		 */
		C displayAsLast();

		/**
		 * Configure the column to be displayed before the column which corresponds to the id specified by the given
		 * <code>beforeProperty</code>.
		 * @param beforeProperty Property which represents the column before which this column has to be displayed (not
		 *        null)
		 * @return this
		 */
		C displayBefore(P beforeProperty);

		/**
		 * Configure the column to be displayed after the column which corresponds to the id specified by the given
		 * <code>afterProperty</code>.
		 * @param afterProperty Property which represents the column after which this column has to be displayed (not
		 *        null)
		 * @return this
		 */
		C displayAfter(P afterProperty);

	}

	/**
	 * ItemListing column builder.
	 * 
	 * @param <T> Item type
	 * @param <P> Item property type
	 * @param <B> Parent builder type
	 * 
	 * @since 5.2.0
	 */
	public interface ItemListingColumnBuilder<T, P, B extends ItemListingConfigurator<T, P, B>>
			extends ItemListingColumnConfigurator<T, P, ItemListingColumnBuilder<T, P, B>> {

		/**
		 * Add the column to the listing.
		 * @return The parent listing builder
		 */
		B add();

	}

}