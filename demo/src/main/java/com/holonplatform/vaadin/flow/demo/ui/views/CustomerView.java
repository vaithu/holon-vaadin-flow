package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.CustomerDetailService;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.EmptyStates;
import com.iyensoft.vaadin.flow.components.utils.UIUtils;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.EntityMasterDetailView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

/**
 * Database-backed customer master-detail screen.
 *
 * <p>The master and all related tabs use database-side offset/limit queries. Detail tabs are
 * created only when opened.</p>
 */
@PageTitle("Customers")
@Route(value = "customers", layout = DemoMainLayout.class)
@StyleSheet("context://customer-master-detail-material.css")
public final class CustomerView extends EntityMasterDetailView<Product> {

    private final transient ProductService productService;

    public CustomerView(ProductService productService,
                        CustomerDetailService customerDetailService) {
        super(Product.class, "customer");
        this.productService = productService;
        configureCustomerView(customerDetailService);
    }

    private void configureCustomerView(CustomerDetailService customerDetailService) {
        configureMasterDetail(builder -> builder
                .master(master -> master
                        .materialHeader(header -> header
                                .headline("Customers"))
                        .listing(listing -> listing
                                .autoCreateColumns(false)
                                .mobileViewHeader(mobileViewHeader())
                                .mobileViewColumn(mobileViewColumn())
                                .multiSelect()
                                .columns("name", "category", "price")
                                .fetch((query, text, filter, sort) ->
                                        this.productService.fetch(query.getOffset(), query.getLimit(),
                                                text, filter, sort)))
                        .selectionKey(Product::getId))
                .lazyDetail(detail -> {
                    CustomerDetail customerDetail = new CustomerDetail(customerDetailService);
                    detail
                            .materialHeader(header -> header.headline("Customer details"))
                            .withDetailSync(customerDetail::onItemSelected)
                            .content(customerDetail);
                }));
    }

    @Override
    protected String itemId(Product item) {
        return String.valueOf(item.getId());
    }

    @Override
    protected Optional<Product> findItemById(String id) {
        return productService.findById(Long.parseLong(id));
    }

    @Override
    protected Optional<Product> findFirstItem() {
        return productService.findFirst();
    }

    protected Component mobileViewHeader() {
        return UIUtils.mobileViewHeader("A","B");
    }

    protected LitRenderer<Product> mobileViewColumn() {
        return LitRendererBuilder.<Product>mobileListItem()
                .withVendor(Product::getName)
                .build();
    }
}
