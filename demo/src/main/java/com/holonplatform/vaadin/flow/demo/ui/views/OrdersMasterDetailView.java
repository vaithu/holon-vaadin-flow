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
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.DocumentRowBuilder.StatusType;
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
 * Purchase Orders master-detail — demonstrates {@link LitRendererBuilder#documentRow()} in a
 * real master-detail layout following the {@link MasterDetailDemoV2} pattern.
 *
 * <p>The mobile column renders each product as a 3-row document row using
 * {@code DocumentRowLitRenderer}:
 * <ul>
 *   <li>Row 1 (left): PO number in monospace — {@code "PO-2026-{id:04}"}</li>
 *   <li>Row 1 (right): formatted price — {@code "€X,XXX.XX"}</li>
 *   <li>Row 2: product name as the vendor/company title</li>
 *   <li>Row 3 (left): status badge driven by {@link StatusType} enum</li>
 *   <li>Row 3 (right): category + active flag as contextual metadata</li>
 * </ul>
 *
 * <p>Field mapping:
 * <table border="1">
 *   <tr><th>DocumentRow slot</th><th>Product field</th></tr>
 *   <tr><td>Reference</td><td>{@code "PO-2026-{id:04}"}</td></tr>
 *   <tr><td>Amount</td><td>{@code price} formatted as €X,XXX.XX</td></tr>
 *   <tr><td>Title</td><td>{@code name}</td></tr>
 *   <tr><td>Status label</td><td>derived from {@code active} + {@code id}</td></tr>
 *   <tr><td>Status type</td><td>{@link StatusType} from {@code active} + {@code id}</td></tr>
 *   <tr><td>Meta</td><td>{@code category} + active indicator</td></tr>
 * </table>
 */
@PageTitle("Purchase Orders — Holon Demo")
@Route(value = "orders-master-detail", layout = DemoMainLayout.class)
public class OrdersMasterDetailView extends Div {

    private final transient ProductService productService;

    // ── URL sync ──────────���───────────────────────────────────────────────────

    @QueryParameter("id")
    private String urlId;

    private MasterDetailLayout<Product> desktopLayout;

    // ── Live detail refs (assigned in detailXxx() methods) ────────────────────

    private Avatar         avatar;
    private Span           headingSpan;
    private BreadcrumbPage currentPage;
    private Span           statusBadge;

    // ── Constructor ───────────────────────────────────────────────────────────

    public OrdersMasterDetailView(ProductService productService) {
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
                                .heading("Purchase Orders")
                                .details(masterBadges())
                                .actions(masterActions()))
                        .listing(l -> l
                                .autoCreateColumns(false)
                                .columns("id", "name", "category", "price", "active")
                                .mobileViewHeader(mobileHeader())
                                .mobileViewColumn(mobileColumn())
                                .search("Search PO #, vendor, category…")
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
                        Components.iconBadge().size(Size.SM).variant(Variant.SUCCESS)
                                .text("Active").build(),
                        Components.iconBadge().size(Size.SM).variant(Variant.INFO)
                                .text("Pending").build())
                .build();
    }

    private Component mobileHeader() {
        return Components.hl()
                .addToStart(new Span("Reference"))
                .addToEnd(new Span("Amount"))
                .build();
    }

    /**
     * Mobile column built with {@link LitRendererBuilder#documentRow()}.
     *
     * <p>Produces a 3-row document row — zero CSS strings in this method:
     * <pre>
     *   [PO-2026-0042]          [€1,299.00]   ← li-row1
     *   Wireless Mouse 42                     ← li-row2
     *   [● In progress]    [Electronics · active]   ← li-row3
     * </pre>
     */
    private LitRenderer<Product> mobileColumn() {
        return LitRendererBuilder.<Product>documentRow()
                .withReference(OrdersMasterDetailView::poNumber)
                .withAmount(OrdersMasterDetailView::formattedAmount)
                .withTitle(Product::getName)
                .withStatus(OrdersMasterDetailView::statusLabel,
                            OrdersMasterDetailView::statusType)
                .withMeta(OrdersMasterDetailView::metaLabel)
                .build();
    }

    // ── Detail helpers ────────────────────────────────────────────────────────

    private Breadcrumb detailBreadcrumb() {
        currentPage = new BreadcrumbPage("—");
        return Components.breadcrumb()
                .addWithSeparators(
                        new BreadcrumbItem("Home",   IndexView.class),
                        new BreadcrumbItem("Orders", OrdersMasterDetailView.class),
                        currentPage)
                .build();
    }

    private Avatar detailAvatar() {
        return avatar = Components.avatar("?").build();
    }

    private Span detailHeading() {
        return headingSpan = new Span("Select an order");
    }

    /**
     * Status badge in the detail header — CSS class mirrors the {@link StatusType}
     * mapping used inside the mobile column renderer, keeping them visually consistent.
     */
    private Component detailStatus() {
        statusBadge = new Span("—");
        // Use .status as base class (from document-row-lit-renderer.css) + variant modifier
        statusBadge.addClassName("status");
        return statusBadge;
    }

    // ── Sync ──────────────────────────────────────────────────────────────────

    private void syncDetail(Product p) {
        avatar.setName(p.getName());
        headingSpan.setText(poNumber(p) + " · " + p.getName());
        currentPage.setText(poNumber(p));

        // Mirror the status badge CSS exactly as the grid renderer does internally
        statusBadge.setText(statusLabel(p));
        statusBadge.setClassName(statusType(p).toCssClass());

        if (desktopLayout != null) {
            desktopLayout.pushUrlState(getElement(), p, ViewMode.DESKTOP);
        }
    }

    // ── Field mapping helpers (static — usable as method references) ──────────

    private static String poNumber(Product p) {
        return "PO-2026-" + String.format("%04d", p.getId());
    }

    private static String formattedAmount(Product p) {
        return "€" + String.format(Locale.US, "%,.2f", p.getPrice().doubleValue());
    }

    private static String metaLabel(Product p) {
        String cat = p.getCategory() != null ? p.getCategory() : "Uncategorized";
        return cat + " · " + (p.isActive() ? "active" : "on hold");
    }

    private static String statusLabel(Product p) {
        if (!p.isActive())         return "Exception";
        if (p.getId() % 7 == 0)   return "Partial match";
        if (p.getId() % 4 == 0)   return "Matched";
        if (p.getId() % 2 == 0)   return "Ready";
        return "Pending";
    }

    /**
     * Maps product state to {@link StatusType} — zero CSS strings, all mapping
     * is internal to {@link StatusType#toCssClass()}.
     *
     * <p>Enum → CSS class → color (from {@code document-row-lit-renderer.css}):
     * <ul>
     *   <li>{@link StatusType#EXCEPTION} → {@code .st-exception} — red</li>
     *   <li>{@link StatusType#PARTIAL}   → {@code .st-partial}   — amber</li>
     *   <li>{@link StatusType#MATCHED}   → {@code .st-matched}   — green</li>
     *   <li>{@link StatusType#READY}     → {@code .st-ready}     — blue</li>
     *   <li>{@link StatusType#PENDING}   → {@code .st-pending}   — amber</li>
     * </ul>
     */
    private static StatusType statusType(Product p) {
        if (!p.isActive())         return StatusType.EXCEPTION;
        if (p.getId() % 7 == 0)   return StatusType.PARTIAL;
        if (p.getId() % 4 == 0)   return StatusType.MATCHED;
        if (p.getId() % 2 == 0)   return StatusType.READY;
        return StatusType.PENDING;
    }
}


