package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.ListingBundleBuilder;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HeaderConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/**
 * Holon-style master-detail configurator.
 *
 * @param <T> item type
 * @param <C> parent configurator type
 */
public interface MasterDetailConfigurator<T, C extends MasterDetailConfigurator<T, C>>
        extends HasSize, HasStyle {

    C build();

    Component getComponent();
    MasterNode<T, C> masterView();
    DefaultViewNode<T, C> defaultView();
    default DefaultViewNode<T, C> defaultView(MasterNode<T, ?> master) {
        if (master != null) {
            master.add();
        }
        return defaultView();
    }
    C detailLoader(Function<T, Optional<T>> detailLoader);
    C deepLink();
    C deepLink(Function<T, String> idExtractor, Function<String, Optional<T>> itemLoader);
    C itemId(Function<T, String> idExtractor, Function<String, Optional<T>> itemLoader);
    C autoShowFirst();
    C autoShowFirst(boolean auto);
    C selectionDebounce(int millis);
    C detailCacheSize(int size);
    C scrollToSelected(boolean scrollToSelected);
    C clearDetailOnPageChange(boolean clearDetailOnPageChange);
    C prefetchAdjacent(boolean prefetchAdjacent);
    C onDataChanged(Runnable listener);
    C onItemChanged(Consumer<T> listener);
    C withDetailSync(Component owner, Consumer<T> handler);
    C mobileSheetTitle(String title);
    void notifyDataChanged();
    void refreshItem(T item);
    void refreshCurrentItem();
    void clearSelection();
    void selectFirst();
    void restoreSelection(String idStr);
    void clickItem(T item);
    default C mobileMasterOnly() {
        return mobileMasterOnly(true);
    }
    C mobileMasterOnly(boolean mobileMasterOnly);
    Optional<Header> getMasterHeader();
    Optional<Separator> getSeparatorComponent();
    Optional<Header> getDetailHeader();
    Optional<Tabs> getDetailTabs();
    List<Component> getDetailContentComponents();
    List<Component> getDetailFooterComponents();
    Optional<ListingBundle<T>> getListingBundle();
    Optional<Div> getMasterDiv();
    Optional<Div> getDetailDiv();
    interface MasterNode<T, C extends MasterDetailConfigurator<T, C>> {
        HeaderNode<T, C> header();
        ListingBundleNode<T, C> listingBundle();

        C header(Header header);
        C listingBundle(ListingBundle<T> listingBundle);
        C add();
    }
    interface DefaultViewNode<T, C extends MasterDetailConfigurator<T, C>> {
        SeparatorNode<C> separator();

        DetailNode<T, C> detail();

        C master(Header header, ListingBundle<T> listingBundle);

        C add();
    }
    interface HeaderNode<T, C extends MasterDetailConfigurator<T, C>>
            extends HeaderConfigurator<HeaderNode<T, C>> {
        HeaderNode<T, C> withoutBorder();
        MasterNode<T, C> add();
    }
    interface ListingBundleNode<T, C extends MasterDetailConfigurator<T, C>> {
        ListingBundleNode<T, C> columns(String... cols);
        ListingBundleNode<T, C> mobileViewColumn(com.vaadin.flow.data.renderer.Renderer<T> renderer);
        ListingBundleNode<T, C> hidden(String... cols);
        ListingBundleNode<T, C> header(String column, String label);
        ListingBundleNode<T, C> header(String column, Localizable localizable);
        ListingBundleNode<T, C> header(String column, String defaultLabel, String messageCode);
        ListingBundleNode<T, C> pageSizes(Integer... sizes);
        ListingBundleNode<T, C> defaultPageSize(int size);
        ListingBundleNode<T, C> search(String placeholder);
        ListingBundleNode<T, C> search(Localizable localizable);
        ListingBundleNode<T, C> search(String defaultPlaceholder, String messageCode);
        ListingBundleNode<T, C> withFilterPanel();
        ListingBundleNode<T, C> advancedSearchLabel(String label);
        ListingBundleNode<T, C> retainFilterValues(boolean retain);
        ListingBundleNode<T, C> multiSelect();
        ListingBundleNode<T, C> gridHeader(String title);
        ListingBundleNode<T, C> gridHeader(Component... components);
        ListingBundleNode<T, C> paginated();
        ListingBundleNode<T, C> virtualScroll();
        ListingBundleNode<T, C> paginated(boolean paginated);
        ListingBundleNode<T, C> autoCreateColumns(boolean autoCreate);
        ListingBundleNode<T, C> importAction(Runnable action);
        ListingBundleNode<T, C> exportAction(Runnable action);
        ListingBundleNode<T, C> withMenuAction(String label, Runnable action);
        ListingBundleNode<T, C> withMenuAction(VaadinIcon icon, String label, Runnable action);
        ListingBundleNode<T, C> fetch(ListingBundleBuilder.FetchCallback<T> callback);
        ListingBundleNode<T, C> fetch(ListingBundleBuilder.FilteredFetchCallback<T> callback);
        ListingBundleNode<T, C> fetch(ListingBundleBuilder.ColumnAwareFilteredFetchCallback<T> callback);
        ListingBundleNode<T, C> viewModeSupplier(Supplier<ViewMode> supplier);
        ListingBundleNode<T, C> onItemClickListener(ViewMode viewMode,
                ComponentEventListener<ItemClickEvent<T>> listener);
        MasterNode<T, C> add();
        ListingBundleNode<T, C> mobileViewHeader(String text);
        ListingBundleNode<T, C> mobileViewHeader(Component component);
    }
    interface SeparatorNode<C extends MasterDetailConfigurator<?, C>> {
        SeparatorNode<C> visible(boolean visible);
        SeparatorNode<C> color(SeparatorColor color);
        C add();
    }
    interface DetailNode<T, C extends MasterDetailConfigurator<T, C>> {
        DetailHeaderNode<T, C> header();
        DetailTabsNode<T, C> tabs();
        DetailContentNode<T, C> content();
        DetailFooterNode<T, C> footer();
        DetailNode<T, C> withDetailSync(Component owner, Consumer<T> handler);
        C add();
    }
    interface DetailHeaderNode<T, C extends MasterDetailConfigurator<T, C>>
            extends HeaderConfigurator<DetailHeaderNode<T, C>> {
        DetailHeaderNode<T, C> withoutBorder();
        DetailNode<T, C> add();
    }
    interface DetailTabsNode<T, C extends MasterDetailConfigurator<T, C>>
            extends LazyTabsConfigurator<DetailTabsNode<T, C>>,
                    DeferrableLocalizationConfigurator<DetailTabsNode<T, C>> {
        DetailNode<T, C> add();
    }
    interface DetailContentNode<T, C extends MasterDetailConfigurator<T, C>> {
        DetailContentNode<T, C> content(Component... components);
        DetailNode<T, C> add();
    }
    interface DetailFooterNode<T, C extends MasterDetailConfigurator<T, C>> {
        DetailFooterNode<T, C> content(Component... components);
        DetailNode<T, C> add();
    }
}
