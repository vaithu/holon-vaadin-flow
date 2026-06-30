/*
 * Copyright 2016-2026 Axioma srl.
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
package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.ChipVariant;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.RowVariant;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.StatusVariant;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.navigator.annotations.OnShow;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert.Variant;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbPage;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge.Size;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.enums.ButtonPreset;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Locale;

/**
 * Bills (AP) master-detail — demonstrates {@link LitRendererBuilder#mobileListItem()} in a
 * real master-detail layout following the {@link MasterDetailDemoV2} pattern.
 *
 * <p>The mobile column renders each product as an AP bill row using
 * {@code MobileListItemLitRenderer}:
 * <ul>
 *   <li>Inactive products → {@link RowVariant#EXCEPTION} (red amount + context label)</li>
 *   <li>Paid rows         → {@link RowVariant#PAID} (green amount via CSS cascade)</li>
 *   <li>Chip badge        → {@link ChipVariant#VARIANCE} "! exception" or
 *                           {@link ChipVariant#MATCHED} "matched"</li>
 *   <li>Status badge      → {@link StatusVariant} driven by {@code active} + {@code id}</li>
 * </ul>
 *
 * <p>Field mapping:
 * <table border="1">
 *   <tr><th>Bill slot</th><th>Product field</th></tr>
 *   <tr><td>Bill number</td><td>{@code "BILL-2026-{id:04}"}</td></tr>
 *   <tr><td>Vendor</td><td>{@code name}</td></tr>
 *   <tr><td>When/context</td><td>derived from {@code active} + {@code id}</td></tr>
 *   <tr><td>Amount</td><td>{@code price} formatted as €X,XXX.XX</td></tr>
 *   <tr><td>Linked PO</td><td>{@code "PO-2026-{id+100:04}"}</td></tr>
 *   <tr><td>Status</td><td>derived from {@code active} + {@code id}</td></tr>
 * </table>
 */
@PageTitle("Bills (AP) — Holon Demo")
@Route(value = "bills-master-detail", layout = DemoMainLayout.class)
public class BillsMasterDetailView extends Div {

    private final transient ProductService productService;

    // ── URL sync ──────────────────────────────────────────────────────────────

    @QueryParameter("id")
    private String urlId;

    private MasterDetailLayout<Product> desktopLayout;

    // ── Live detail refs (assigned in detailXxx() methods) ────────────────────

    private Avatar         avatar;
    private Span           headingSpan;
    private BreadcrumbPage currentPage;
    private Span           statusBadge;

    // ── Constructor ───────────────────────────────────────────────────────────

    public BillsMasterDetailView(ProductService productService) {
        this.productService = productService;
        ResponsiveDiv.configure(this)
                .slotOnce(ViewMode.MOBILE,  () -> buildLayout(ViewMode.MOBILE))
                .slotOnce(ViewMode.DESKTOP, () -> buildLayout(ViewMode.DESKTOP))
                .fullHeight()
                .build();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private MasterDetailLayout<Product> buildLayout(ViewMode viewMode) {
        MasterDetailLayout<Product> layout = Components.masterDetail(Product.class)
                .viewMode(viewMode)
                .withMobileSheet(Sheet.Side.RIGHT)
                .withUrlSync(
                        p  -> String.valueOf(p.getId()),
                        id -> productService.findById(Long.parseLong(id)))
                .master(m -> m
                        .header(h -> h
                                .heading("Bills (AP)")
                                .details(masterBadges())
                                .actions(masterActions()))
                        .listing(l -> l
                                .autoCreateColumns(false)
                                .columns("id", "name", "category", "price", "active")
                                .mobileViewHeader(mobileHeader())
                                .mobileViewColumn(mobileColumn())
                                .search("Search bills, vendors…")
                                .withFilterPanel()
                                .fetch((q, text, filter, sort) ->
                                        productService.fetch(q.getOffset(), q.getLimit(), text, filter, sort)))
                        .selectionKey(Product::getId))
                .lazyDetail(d -> d
                        .header(h -> h
                                .breadcrumb(detailBreadcrumb())
                                .prefix(detailAvatar())
                                .heading(detailHeading())
                                .details(detailStatus()))
                        .withDetailSync(this::syncDetail))
                .build();

        if (!viewMode.isMobile()) {
            desktopLayout = layout;
            layout.addAttachListener(e -> initDesktopSelection());
        }
        return layout;
    }

    // ── Navigation lifecycle ──────────────────────────────────────────────────

    @OnShow
    private void onShow() {
        if (desktopLayout != null) initDesktopSelection();
    }

    private void initDesktopSelection() {
        if (urlId != null && !urlId.isBlank()) {
            desktopLayout.restoreFromUrl(urlId);
        } else {
            desktopLayout.selectFirst(ViewMode.DESKTOP);
        }
    }

    // ── Master helpers ────────────────────────────────────────────────────────

    private Component masterActions() {
        return Components.button().preset(ButtonPreset.NEW).build();
    }

    private Component masterBadges() {
        return Components.hl().spacing()
                .addToStart(
                        Components.iconBadge().size(Size.SM).variant(Variant.WARNING)
                                .text("Exceptions").build(),
                        Components.iconBadge().size(Size.SM).variant(Variant.DESTRUCTIVE)
                                .text("Overdue").build())
                .build();
    }

    private Component mobileHeader() {
        return Components.hl()
                .addToStart(new Span("Vendor"))
                .addToEnd(new Span("Amount"))
                .build();
    }

    /**
     * Mobile column built with {@link LitRendererBuilder#mobileListItem()}.
     *
     * <p>All CSS class names are resolved internally from enum values —
     * no CSS strings appear in this method.
     */
    private LitRenderer<Product> mobileColumn() {
        return LitRendererBuilder.<Product>mobileListItem()
                .withRootVariant(BillsMasterDetailView::rowVariant)
                .withNumber(BillsMasterDetailView::billNumber)
                .withWhen(BillsMasterDetailView::whenLabel)
                .withVendor(Product::getName)
                .withChip(BillsMasterDetailView::chipLabel, BillsMasterDetailView::chipVariant)
                .withMetaRef(p -> "PO-2026-" + String.format("%04d", p.getId() + 100))
                .withAmount(BillsMasterDetailView::formattedAmount)
                .withStatus(BillsMasterDetailView::statusLabel, BillsMasterDetailView::statusVariant)
                .build();
    }

    // ── Detail helpers ────────────────────────────────────────────────────────

    private Breadcrumb detailBreadcrumb() {
        currentPage = new BreadcrumbPage("—");
        return Components.breadcrumb()
                .addWithSeparators(
                        new BreadcrumbItem("Home",  IndexView.class),
                        new BreadcrumbItem("Bills", BillsMasterDetailView.class),
                        currentPage)
                .build();
    }

    private Avatar detailAvatar() {
        return avatar = Components.avatar("?").build();
    }

    private Span detailHeading() {
        return headingSpan = new Span("Select a bill");
    }

    /**
     * Status badge shown in the detail header — class is updated dynamically
     * on each {@link #syncDetail(Product)} call using the same CSS classes
     * as the {@link StatusVariant} enum, keeping the detail visually consistent
     * with the master list renderer.
     */
    private Component detailStatus() {
        statusBadge = new Span("—");
        statusBadge.addClassName("mli-status");
        return statusBadge;
    }

    // ── Sync ──────────────────────────────────────────────────────────────────

    private void syncDetail(Product p) {
        avatar.setName(p.getName());
        headingSpan.setText(billNumber(p) + " · " + p.getName());
        currentPage.setText(billNumber(p));

        // Mirror the status badge CSS exactly as the grid renderer does internally
        statusBadge.setText(statusLabel(p));
        statusBadge.setClassName(statusVariant(p).toStatusClass());

        if (desktopLayout != null) {
            desktopLayout.pushUrlState(getElement(), p, ViewMode.DESKTOP);
        }
    }

    // ── Field mapping helpers (static — usable as method references) ──────────

    private static String billNumber(Product p) {
        return "BILL-2026-" + String.format("%04d", p.getId());
    }

    private static String formattedAmount(Product p) {
        return "€" + String.format(Locale.US, "%,.2f", p.getPrice().doubleValue());
    }

    /**
     * Root variant drives amount and when-label color via CSS cascade:
     * <ul>
     *   <li>Inactive products → {@link RowVariant#EXCEPTION} — amount + when turn red</li>
     *   <li>Paid (id % 3 == 0, active) → {@link RowVariant#PAID} — amount turns green</li>
     * </ul>
     */
    private static RowVariant rowVariant(Product p) {
        if (!p.isActive())       return RowVariant.EXCEPTION;
        if (p.getId() % 3 == 0) return RowVariant.PAID;
        return RowVariant.NONE;
    }

    private static String whenLabel(Product p) {
        if (!p.isActive())         return "exception";
        if (p.getId() % 5 == 0)   return "due in " + (p.getId() % 14 + 1) + "d";
        if (p.getId() % 3 == 0)   return "paid " + (p.getId() % 5 + 1) + "d ago";
        return "approved " + (p.getId() % 7 + 1) + "d ago";
    }

    private static String chipLabel(Product p) {
        return p.isActive() ? "matched" : "! exception";
    }

    private static ChipVariant chipVariant(Product p) {
        return p.isActive() ? ChipVariant.MATCHED : ChipVariant.VARIANCE;
    }

    private static String statusLabel(Product p) {
        if (!p.isActive())         return "Awaiting";
        if (p.getId() % 5 == 0)   return "Due soon";
        if (p.getId() % 3 == 0)   return "Paid";
        return "Approved";
    }

    private static StatusVariant statusVariant(Product p) {
        if (!p.isActive())         return StatusVariant.AWAITING;
        if (p.getId() % 5 == 0)   return StatusVariant.DUE;
        if (p.getId() % 3 == 0)   return StatusVariant.PAID;
        return StatusVariant.APPROVED;
    }
}


