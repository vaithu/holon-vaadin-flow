package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("IyenView")
@Route(value = "iyenview", layout = DemoMainLayout.class)
public class IyenView extends Main {

    private final ProductService productService;

    public IyenView(ProductService productService) {
        this.productService = productService;

        setSizeFull();

        add(Components.iyenView()
                .mobile(createMaster())
                .desktop(createMaster(),createDetail())
                .separator()
                .build()
        );

    }

    private IyenDetailBuilder createDetail() {
        return Components.detail()
                .content(createDesktopGrid());
    }

    private IyenMasterBuilder createMaster() {
        return Components.master()
                .content(createMobileGrid());
    }

    private Component createMobileGrid() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .autoCreateColumns(false)
                .columns("id", "name", "price")
                .gridHeader("All Products")
                .paginated(false)
                .search(("Search by name"))
                .withFilterPanel()
                .multiSelect()
                .fetch((query, searchText, filter, sort, columns) ->
                        productService.fetch(query.getOffset(), query.getLimit(), searchText, filter, sort, columns))
                .build();

        ItemListing<Product, ?> listing = bundle.listing();
        listing.setMobileColumn(Components.<Product>mobileGridColumnLit()
                .flexDirection(FlexDirection.ROW)
                .withAvatarAsPrimary(Product::getName)
                .withSecondaryText(Product::getName)
                .withTertiaryText(product -> String.valueOf(product.getPrice()))
                .build());

        listing.addItemClickListener(event -> {
            Product product = event.getItem();
            /*Components.sheet()
                    .fullscreenOnMobile(true)
                    .backButton(true)
                    .title(product.getName())
                    .content(
                            Components.keyValueList()
                                    .addFromBean(product)
                    )
                    .open();*/

            /*AccumUpdateDialog dialog = new AccumUpdateDialog(null);
            dialog.open();*/

            /*RadioButtonGroup<String> group = new RadioButtonGroup<String>();
            group.setLabel("Select anyone option");
            group.setRequired(true);
            group.setItems("Update POS Accumulator", "Replay with different TranId", "Remove Duplicates", "Resubmit Batch");
            group.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

            Components.dialog.confirm()
                    .withContent(
                            Components.input.string()
                                    .label("Enter the new TranId")
                                    .build().getComponent()
                    )
                    .okButtonConfigurator(result -> {
                        result.text("Submit");
                    })
                    .open();*/

            Components.alertDialog()
                    .title("Batch Release Failed!!!")
                    .description("BatchID 8FB1850F00364808B723149D9E4C2905 has not been released successfully.Contact SMIS team")
                    .headerIcon(new Icon(VaadinIcon.CLOSE_CIRCLE), Alert.Variant.DESTRUCTIVE)
                    .centered()
                    .withCancelButton(false)
                    .confirmText("Close")
                    .footerBackground(true)
                    .alertRole(false)   // routine — downgrade role="dialog"
                    .open();


        });


        return ResponsiveDiv.flex().column()
                .fullHeight()
                .gapS()
                .add(bundle.header(), bundle.toolbar(), bundle.grid(), bundle.footer())
                .build();


    }

    private Component createDesktopGrid() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .columns("name", "price")
                .gridHeader("All Products")
                .build();

        ItemListing<Product, ?> listing = bundle.listing();
        listing.addIndexColumn();


        return listing.getComponent();
    }
}
