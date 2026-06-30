package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.builders.SheetBuilder;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.enums.ButtonPreset;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Master-Detail - Demo")
@Route(value = "master-detail", layout = DemoMainLayout.class)
public class MasterDetailLayoutDemo extends Div {
    private final ProductService productService;
    public MasterDetailLayoutDemo(ProductService productService) {
        this.productService = productService;
        ResponsiveDiv.configure(this)
                .fullHeight()
                .slotOnce(ViewMode.MOBILE, this::mobile)
                .slotOnce(ViewMode.DESKTOP, this::desktop)
                .build();
    }
    private Component desktop() {
        Master master = new Master();
        Detail detail = new Detail();

        master.bundle.addItemClickListener(event -> detail.show(event.getItem()));


        return Components.masterDetail(Product.class)
                .desktop(master.createMaster(), detail.createDetail())
                .build();
    }
    private Component mobile() {
        Master master = new Master();
        Detail detail = new Detail();

        master.bundle.addItemClickListener(event -> {
            SheetBuilder sheetBuilder = Components.sheet(Sheet.Side.RIGHT)
                    .fullscreenOnMobile(true)
                    .header(detail.createHeader())
                    .content(detail.createContentArea());

            detail.show(event.getItem());

            sheetBuilder.open();
        });

        return master.createMaster();
    }
    // --- Master ---
    class Master {
        final ListingBundle<Product> bundle;
        Master() {
            bundle = createListingBundle();
        }
        Div createMaster() {
            return Components.master()
                    .header(createHeader())
                    .content(bundle.header(), bundle.toolbar(), bundle.grid(), bundle.footer())
                    .build();
        }
        private Header createHeader() {
            return Components.header("Products")
                    .actions(new Button("New"))
                    .build();
        }
        private ListingBundle<Product> createListingBundle() {
            return Components.listing(Product.class)
                    .autoCreateColumns(false)
                    .columns("name", "category", "price", "active")
                    .multiSelect()
                    .mobileViewHeader(
                            Components.hl()
                                    .addToStart(new Span("Product Name"))
                                    .addToEnd(new Span("Price"))
                                    .build()
                    )
                    .mobileViewColumn(
                            Components.<Product>mobileGridColumnLit()
                                    .flexDirection(FlexDirection.ROW)
                                    .withAvatarAsPrimary(Product::getName)
                                    .withSecondaryText(Product::getCategory)
                                    .withTertiaryText(p -> String.valueOf(p.getPrice()))
                                    .build()
                    )
                    .gridHeader("")
                    .gridHeader(Components.button().preset(ButtonPreset.DELETE).build())
                    .pageSizes(10, 25, 50)
                    .defaultPageSize(10)
                    .search("Search products...")
                    .withFilterPanel()
                    .fetch((query, text, filter, sort) ->
                            productService.fetch(query.getOffset(), query.getLimit(), text, filter, sort))
                    .build();
        }
    }
    // --- Detail ---
    static class Detail {
        private  Header detailHeader;
        private Div contentArea;
        private Avatar avatar;
        Detail() {

        }
        Div createDetail() {
            return Components.detail()
                    .header(createHeader())
                    .content(createContentArea())
                    .build();
        }

        private Header createHeader() {

            if (detailHeader == null) {
                detailHeader = Components.header("Select a product")
                        .prefix( avatar = Components.avatar().name("?").build())
                        .build();
            }
            return detailHeader;
        }

        private Div createContentArea() {

            if (contentArea == null) {
                contentArea = Components.div().styleName("tab-container").build();
            }

            return contentArea;
        }

        void show(Product product) {
            detailHeader.setHeading(product.getName());
            avatar.setName(product.getName());
            contentArea.removeAll();
            contentArea.add(buildContent(product));
        }
        void showEmpty() {
            detailHeader.setHeading("Select a product");
            contentArea.removeAll();
        }
        private Component buildContent(Product product) {
            return new KeyValueList()
                    .addItem(KeyValueItem.of("Category",
                            product.getCategory() != null ? product.getCategory() : "-"))
                    .addItem(KeyValueItem.of("Price",
                            product.getPrice() != null ? "$" + product.getPrice() : "-"))
                    .addItem(KeyValueItem.of("Status",
                            product.isActive() ? "Active" : "Inactive"))
                    .addItem(KeyValueItem.of("Created",
                            product.getCreatedDate() != null
                                    ? product.getCreatedDate().toString() : "-"));
        }
    }
}
