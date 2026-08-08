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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLitRendererBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DocumentRowLitRenderer;
import com.holonplatform.vaadin.flow.internal.components.builders.GenericGridCellLitRenderer;
import com.holonplatform.vaadin.flow.internal.components.builders.MobileListItemLitRenderer;
import com.holonplatform.vaadin.flow.internal.components.builders.MobileGridColumnLitRenderer;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Fluent builder for constructing {@link LitRenderer} instances using composable
 * Lit template fragments. Instead of writing raw HTML template strings, this builder
 * provides a type-safe DSL to compose elements, bind properties, and attach event handlers.
 *
 * <p>Usage examples:
 * <pre>{@code
 * // Simple text column
 * LitRenderer<Product> renderer = LitRendererBuilder.<Product>create()
 *     .span(s -> s.text("${item.name}").className("font-bold"))
 *     .build();
 *
 * // Avatar + name layout
 * LitRenderer<Person> renderer = LitRendererBuilder.<Person>create()
 *     .horizontalLayout(h -> h
 *         .theme("spacing")
 *         .style("align-items: center")
 *         .avatar(a -> a.name("${item.fullName}").img("${item.avatar}"))
 *         .span(s -> s.text("${item.fullName}")))
 *     .withProperty("fullName", Person::getFullName)
 *     .withProperty("avatar", Person::getAvatarUrl)
 *     .build();
 *
 * // Mobile grid column (data-bound, same structure as MobileGridColumnBuilder)
 * LitRenderer<Product> renderer = LitRendererBuilder.<Product>mobileGridColumn()
 *     .flexDirection(FlexDirection.ROW)
 *     .withAvatarAsPrimary(Product::getName)
 *     .withSecondaryText(Product::getName)
 *     .configureSecondary(stack -> stack
 *         .className("stack")
 *         .span(s -> s.text("${item.status}"))
 *         .span(s -> s.text("${item.updatedAt}")))
 *     .withTertiaryText(p -> String.valueOf(p.getPrice()))
 *     .onItemClick("handleClick")
 *     .withFunction("handleClick", (product, key) -> openDetail(product))
 *     .build();
 *
 * // Button with click handler
 * LitRenderer<Task> renderer = LitRendererBuilder.<Task>create()
 *     .vaadinButton(b -> b
 *         .theme("small tertiary")
 *         .text("Delete")
 *         .onClick("handleDelete"))
 *     .withFunction("handleDelete", (task, ignored) -> taskService.delete(task))
 *     .build();
 * }</pre>
 *
 * @param <T> the type of the data item
 * @since 10.0.0
 */
public interface LitRendererBuilder<T> {

    // -----------------------------------------------------------------------
    // Template elements
    // -----------------------------------------------------------------------

    /**
     * Add a {@code <span>} element.
     */
    LitRendererBuilder<T> span(Consumer<SpanElement> configurator);

    /**
     * Add a {@code <div>} element.
     */
    LitRendererBuilder<T> div(Consumer<DivElement> configurator);

    /**
     * Add a {@code <vaadin-horizontal-layout>} element.
     */
    LitRendererBuilder<T> horizontalLayout(Consumer<LayoutElement> configurator);

    /**
     * Add a {@code <vaadin-vertical-layout>} element.
     */
    LitRendererBuilder<T> verticalLayout(Consumer<LayoutElement> configurator);

    /**
     * Add a {@code <vaadin-avatar>} element.
     */
    LitRendererBuilder<T> avatar(Consumer<AvatarElement> configurator);

    /**
     * Add a {@code <vaadin-button>} element.
     */
    LitRendererBuilder<T> vaadinButton(Consumer<ButtonElement> configurator);

    /**
     * Add a {@code <vaadin-icon>} element.
     */
    LitRendererBuilder<T> icon(Consumer<IconElement> configurator);

    /**
     * Add a {@code <vaadin-checkbox>} element (read-only display).
     */
    LitRendererBuilder<T> checkbox(Consumer<CheckboxElement> configurator);

    /**
     * Add an {@code <img>} element.
     */
    LitRendererBuilder<T> img(Consumer<ImgElement> configurator);

    /**
     * Add a {@code <vaadin-progress-bar>} element.
     */
    LitRendererBuilder<T> progressBar(Consumer<ProgressBarElement> configurator);

    /**
     * Add raw Lit template HTML.
     */
    LitRendererBuilder<T> html(String rawTemplate);

    // -----------------------------------------------------------------------
    // Property bindings
    // -----------------------------------------------------------------------

    /**
     * Bind a named property for use in the template as {@code ${item.propertyName}}.
     */
    LitRendererBuilder<T> withProperty(String name, ValueProvider<T, ?> provider);

    // -----------------------------------------------------------------------
    // Event handlers
    // -----------------------------------------------------------------------

    /**
     * Register a server-side function callable from the template via {@code @click="${handleName}"}.
     */
    LitRendererBuilder<T> withFunction(String name, SerializableBiConsumer<T, String> handler);

    // -----------------------------------------------------------------------
    // Build
    // -----------------------------------------------------------------------

    /**
     * Build the configured {@link LitRenderer}.
     */
    LitRenderer<T> build();

    // -----------------------------------------------------------------------
    // Element DSL interfaces
    // -----------------------------------------------------------------------

    /**
     * Base element configurator for shared attributes.
     */
    interface BaseElement<E extends BaseElement<E>> {
        E className(String className);
        E style(String inlineStyle);
        E attribute(String name, String value);
    }

    /**
     * {@code <span>} element configurator.
     */
    interface SpanElement extends BaseElement<SpanElement> {
        SpanElement text(String textOrBinding);
    }

    /**
     * {@code <div>} element configurator supporting nested children.
     */
    interface DivElement extends BaseElement<DivElement> {
        DivElement text(String textOrBinding);
        DivElement span(Consumer<SpanElement> configurator);
        DivElement div(Consumer<DivElement> configurator);
        DivElement img(Consumer<ImgElement> configurator);
        DivElement icon(Consumer<IconElement> configurator);
        DivElement html(String rawHtml);
    }

    /**
     * Layout element ({@code <vaadin-horizontal-layout>} / {@code <vaadin-vertical-layout>}) configurator.
     */
    interface LayoutElement extends BaseElement<LayoutElement> {
        LayoutElement theme(String theme);
        LayoutElement span(Consumer<SpanElement> configurator);
        LayoutElement div(Consumer<DivElement> configurator);
        LayoutElement avatar(Consumer<AvatarElement> configurator);
        LayoutElement vaadinButton(Consumer<ButtonElement> configurator);
        LayoutElement icon(Consumer<IconElement> configurator);
        LayoutElement img(Consumer<ImgElement> configurator);
        LayoutElement checkbox(Consumer<CheckboxElement> configurator);
        LayoutElement progressBar(Consumer<ProgressBarElement> configurator);
        LayoutElement html(String rawHtml);
    }

    /**
     * {@code <vaadin-avatar>} element configurator.
     */
    interface AvatarElement extends BaseElement<AvatarElement> {
        AvatarElement name(String nameOrBinding);
        AvatarElement img(String srcOrBinding);
        AvatarElement abbr(String abbrOrBinding);
    }

    /**
     * {@code <vaadin-button>} element configurator.
     */
    interface ButtonElement extends BaseElement<ButtonElement> {
        ButtonElement text(String textOrBinding);
        ButtonElement theme(String theme);
        ButtonElement onClick(String functionName);
        ButtonElement icon(Consumer<IconElement> configurator);
        ButtonElement disabled(String booleanBinding);
    }

    /**
     * {@code <vaadin-icon>} element configurator.
     */
    interface IconElement extends BaseElement<IconElement> {
        IconElement icon(String iconName);
        IconElement src(String srcOrBinding);
    }

    /**
     * {@code <vaadin-checkbox>} element configurator (read-only for display).
     */
    interface CheckboxElement extends BaseElement<CheckboxElement> {
        CheckboxElement checked(String booleanBinding);
        CheckboxElement readOnly();
    }

    /**
     * {@code <img>} element configurator.
     */
    interface ImgElement extends BaseElement<ImgElement> {
        ImgElement src(String srcOrBinding);
        ImgElement alt(String altOrBinding);
        ImgElement width(String width);
        ImgElement height(String height);
    }

    /**
     * {@code <vaadin-progress-bar>} element configurator.
     */
    interface ProgressBarElement extends BaseElement<ProgressBarElement> {
        ProgressBarElement value(String valueBinding);
        ProgressBarElement min(String minBinding);
        ProgressBarElement max(String maxBinding);
    }

    // -----------------------------------------------------------------------
    // Document Row sub-builder
    // -----------------------------------------------------------------------

    /**
     * Data-bound sub-builder for a 3-row document/transaction row renderer.
     *
     * <p>Produces:
     * <pre>{@code
     * <div class="list-item">
     *   <div class="li-row1">
     *     <span class="li-po">PO-2026-0483</span>
     *     <span class="li-amt">€18,720.00</span>
     *   </div>
     *   <div class="li-row2">Lumen Health Inc.</div>
     *   <div class="li-row3">
     *     <span class="status st-pending">1 exception</span>
     *     <span class="li-meta">⏱  2 days</span>
     *   </div>
     * </div>
     * }</pre>
     *
     * <p>All CSS class names are hardcoded internally — callers pass only
     * {@link ValueProvider} lambdas and a {@link StatusType} enum value.
     * No CSS strings appear in calling code.
     *
     * <p>Usage:
     * <pre>{@code
     * LitRenderer<Order> renderer = LitRendererBuilder.<Order>documentRow()
     *     .withReference(Order::poNumber)
     *     .withAmount(Order::formattedAmount)
     *     .withTitle(Order::company)
     *     .withStatus(Order::statusLabel, o -> StatusType.from(o.getStatus()))
     *     .withMeta(Order::meta)
     *     .build();
     * }</pre>
     *
     * <p>Requires {@code document-row-lit-renderer.css} to be loaded in the consuming view
     * (via {@code @StyleSheet("context://document-row-lit-renderer.css")}).
     *
     * @param <T> the grid item type
     */
    interface DocumentRowBuilder<T> {

        /**
         * Row 1 – left slot: reference / PO number (monospace, bold).
         * Rendered as {@code <span class="li-po">}.
         */
        DocumentRowBuilder<T> withReference(ValueProvider<T, String> refProvider);

        /**
         * Row 1 – right slot: monetary amount (monospace, bold).
         * Rendered as {@code <span class="li-amt">}.
         */
        DocumentRowBuilder<T> withAmount(ValueProvider<T, String> amountProvider);

        /**
         * Row 2 – full-width title / company / counterparty name.
         * Rendered as {@code <div class="li-row2">}.
         */
        DocumentRowBuilder<T> withTitle(ValueProvider<T, String> titleProvider);

        /**
         * Row 3 – left slot: status badge.
         * The {@code typeProvider} returns a {@link StatusType} enum value;
         * the builder maps it to the correct CSS classes internally
         * (e.g. {@code StatusType.PENDING} → {@code "status st-pending"}).
         */
        DocumentRowBuilder<T> withStatus(
                ValueProvider<T, String> labelProvider,
                ValueProvider<T, StatusType> typeProvider);

        /**
         * Row 3 – right slot: metadata / timestamp / secondary context.
         * Rendered as {@code <span class="li-meta">}.
         */
        DocumentRowBuilder<T> withMeta(ValueProvider<T, String> metaProvider);

        /**
         * Makes the entire row clickable; the named server function is invoked on click.
         *
         * <p>Use with {@link #withFunction(String, SerializableBiConsumer)}:
         * <pre>{@code
         * .onItemClick("open")
         * .withFunction("open", (order, ignored) -> navigate(order))
         * }</pre>
         */
        DocumentRowBuilder<T> onItemClick(String functionName);

        /** Register an extra property binding for use in custom {@link #withTitle} expressions. */
        DocumentRowBuilder<T> withProperty(String name, ValueProvider<T, ?> provider);

        /** Register a server-side function callable from the template. */
        DocumentRowBuilder<T> withFunction(String name, SerializableBiConsumer<T, String> handler);

        /** Build the configured {@link LitRenderer}. */
        LitRenderer<T> build();

        // ── Status semantic enum ─────────────────────────────────────────────

        /**
         * Semantic status type — maps to the CSS variant classes defined in
         * {@code document-row-lit-renderer.css}.
         *
         * <p>Callers never write CSS strings; they return one of these enum constants
         * from the {@code typeProvider} lambda passed to
         * {@link #withStatus(ValueProvider, ValueProvider)}.
         *
         * <p>All fallback hex values match the design system tokens:
         * {@code --warn:#b8860b}, {@code --danger:#c0392b}, {@code --success:#2e9a6a},
         * {@code --primary:#1576d3}.
         */
        enum StatusType {
            /** Awaiting review / has 1 exception — amber ({@code .status .st-pending}). */
            PENDING,
            /** Partial match / multiple exceptions — amber ({@code .status .st-partial}). */
            PARTIAL,
            /** Hard exception: price mismatch, qty error — red ({@code .status .st-exception}). */
            EXCEPTION,
            /** Fully approved / matched — green ({@code .status .st-matched}). */
            MATCHED,
            /** Goods received / fulfilled — green ({@code .status .st-received}). */
            RECEIVED,
            /** Ready to process / approve — primary blue ({@code .status .st-ready}). */
            READY,
            /** No variant — base {@code .status} class only. */
            DEFAULT;

            /** Returns the full CSS class string for this status type. */
            public String toCssClass() {
                return switch (this) {
                    case PENDING   -> "status st-pending";
                    case PARTIAL   -> "status st-partial";
                    case EXCEPTION -> "status st-exception";
                    case MATCHED   -> "status st-matched";
                    case RECEIVED  -> "status st-received";
                    case READY     -> "status st-ready";
                    default        -> "status";
                };
            }
        }
    }

    // -----------------------------------------------------------------------
    // Mobile List Item sub-builder
    // -----------------------------------------------------------------------

    /**
     * Data-bound sub-builder for a 4-section mobile list item renderer.
     *
     * <p>Produces:
     * <pre>{@code
     * <div class="mli mli--exception">       <!-- root variant optional -->
     *   <div class="mli-top">
     *     <div class="mli-num">BILL-2026-0331</div>
     *     <div class="mli-when">price variance</div>
     *   </div>
     *   <div class="mli-vendor">Helix Robotics GmbH</div>
     *   <div class="mli-meta">
     *     <span class="chip cv">! +€420</span>
     *     PO-2026-0178
     *   </div>
     *   <div class="mli-bot">
     *     <div class="mli-amt">€18,820.00</div>
     *     <span class="mli-status st-await">Awaiting</span>
     *   </div>
     * </div>
     * }</pre>
     *
     * <p>All CSS class names are hardcoded internally. Callers supply only
     * {@link ValueProvider} lambdas and enum values ({@link RowVariant},
     * {@link ChipVariant}, {@link StatusVariant}).
     * Zero CSS strings in calling code.
     *
     * <p>Usage:
     * <pre>{@code
     * LitRenderer<Bill> renderer = LitRendererBuilder.<Bill>mobileListItem()
     *     .withRootVariant(b -> RowVariant.EXCEPTION)
     *     .withNumber(Bill::billNumber)
     *     .withWhen(Bill::exceptionLabel)
     *     .withVendor(Bill::vendorName)
     *     .withChip(Bill::chipLabel, b -> ChipVariant.VARIANCE)
     *     .withMetaRef(Bill::poReference)
     *     .withAmount(Bill::formattedAmount)
     *     .withStatus(Bill::statusLabel, b -> StatusVariant.AWAITING)
     *     .build();
     * }</pre>
     *
     * <p>All slots are optional — omitting a slot removes that section entirely.
     * Requires {@code mobile-list-lit-renderer.css} loaded in the consuming view.
     *
     * @param <T> the grid item type
     */
    interface MobileListItemBuilder<T> {

        // ── Root ─────────────────────────────────────────────────────────────

        /**
         * Adds a semantic variant modifier class to the root {@code .mli} element
         * (e.g. {@link RowVariant#EXCEPTION} → {@code "mli mli--exception"}).
         * Provides row-level background/border highlight via CSS.
         */
        MobileListItemBuilder<T> withRootVariant(ValueProvider<T, RowVariant> variantProvider);

        /**
         * Makes the entire row clickable. The named server function is invoked on click.
         * Use with {@link #withFunction(String, SerializableBiConsumer)}.
         */
        MobileListItemBuilder<T> onItemClick(String functionName);

        // ── Top row (mli-top) ────────────────────────────────────────────────

        /**
         * Left slot of {@code .mli-top}: reference / bill number.
         * Rendered as {@code <div class="mli-num">}.
         */
        MobileListItemBuilder<T> withNumber(ValueProvider<T, String> numProvider);

        /**
         * Right slot of {@code .mli-top}: exception type / date context / label.
         * Rendered as {@code <div class="mli-when">}.
         */
        MobileListItemBuilder<T> withWhen(ValueProvider<T, String> whenProvider);

        // ── Body (mli-vendor) ────────────────────────────────────────────────

        /**
         * Full-width body row: vendor / counterparty / description.
         * Rendered as {@code <div class="mli-vendor">}.
         */
        MobileListItemBuilder<T> withVendor(ValueProvider<T, String> vendorProvider);

        // ── Meta row (mli-meta) ──────────────────────────────────────────────

        /**
         * Inline chip badge inside {@code .mli-meta}.
         * {@link ChipVariant} controls the color style; no CSS string required.
         */
        MobileListItemBuilder<T> withChip(
                ValueProvider<T, String> labelProvider,
                ValueProvider<T, ChipVariant> variantProvider);

        /**
         * Optional second chip badge inside {@code .mli-meta}, rendered after the first.
         *
         * <p>Example — qty mismatch + 3-way label:
         * <pre>{@code
         * .withChip(b -> "! qty mismatch", b -> ChipVariant.VARIANCE)
         * .withSecondaryChip(b -> "3-way",       b -> ChipVariant.MATCHED)
         * }</pre>
         * The second chip is simply omitted when its label provider returns null or blank.
         */
        MobileListItemBuilder<T> withSecondaryChip(
                ValueProvider<T, String> labelProvider,
                ValueProvider<T, ChipVariant> variantProvider);

        /**
         * Plain reference text inside {@code .mli-meta} (e.g. "PO-2026-0178").
         * Rendered as a bare text node after any chip badges.
         */
        MobileListItemBuilder<T> withMetaRef(ValueProvider<T, String> refProvider);

        // ── Bottom row (mli-bot) ─────────────────────────────────────────────

        /**
         * Left slot of {@code .mli-bot}: formatted monetary amount.
         * Rendered as {@code <div class="mli-amt">}.
         */
        MobileListItemBuilder<T> withAmount(ValueProvider<T, String> amountProvider);

        /**
         * Right slot of {@code .mli-bot}: status badge.
         * {@link StatusVariant} drives the {@code st-*} modifier; no CSS string required.
         */
        MobileListItemBuilder<T> withStatus(
                ValueProvider<T, String> labelProvider,
                ValueProvider<T, StatusVariant> variantProvider);

        // ── Passthrough ──────────────────────────────────────────────────────

        MobileListItemBuilder<T> withProperty(String name, ValueProvider<T, ?> provider);

        MobileListItemBuilder<T> withFunction(String name, SerializableBiConsumer<T, String> handler);

        LitRenderer<T> build();

        // ── Enum: row-level variant ───────────────────────────────────────────

        /**
         * Row-level modifier applied to the root {@code .mli} element.
         *
         * <p>The CSS cascade in {@code mobile-list-lit-renderer.css} drives slot colors automatically:
         * <ul>
         *   <li>{@link #EXCEPTION} → {@code .mli.exception .mli-amt} turns red,
         *       {@code .mli-when} turns red + bold</li>
         *   <li>{@link #PAID}      → {@code .mli.paid .mli-amt} turns green</li>
         *   <li>{@link #SELECTED}  → {@code .mli.on} primary-soft background + left border</li>
         * </ul>
         * No changes to {@code withAmount()} or {@code withWhen()} are needed.
         */
        enum RowVariant {
            /** No modifier — plain row. */
            NONE,
            /** Exception / mismatch row — red amount + red when-label via CSS cascade. */
            EXCEPTION,
            /** Selected / open row — primary-soft background with left accent border. */
            SELECTED,
            /** Selected AND exception (e.g. currently open exception row). */
            SELECTED_EXCEPTION,
            /** Paid row — green amount via CSS cascade. */
            PAID;

            /** Returns the full class string for the root {@code .mli} element. */
            public String toRootClass() {
                return switch (this) {
                    case EXCEPTION          -> "mli exception";
                    case SELECTED           -> "mli on";
                    case SELECTED_EXCEPTION -> "mli on exception";
                    case PAID               -> "mli paid";
                    default                 -> "mli";
                };
            }
        }

        // ── Enum: chip badge variant ──────────────────────────────────────────

        /**
         * Color variant for the inline {@code .chip} badge inside {@code .mli-meta}.
         *
         * <p>Maps to {@code .chip.v} (variance), {@code .chip.m} (matched),
         * {@code .chip.p} (pending). CSS in {@code mobile-list-lit-renderer.css}.
         */
        enum ChipVariant {
            /** Neutral — base chip, no color modifier. */
            NEUTRAL,
            /** Variance / exception chip — danger-toned ({@code .chip.v}). */
            VARIANCE,
            /** Matched / success chip — success-toned ({@code .chip.m}). */
            MATCHED,
            /** Pending / warning chip — warn-toned ({@code .chip.p}). */
            PENDING;

            /** Returns the full class string for the chip {@code <span>} element. */
            public String toChipClass() {
                return switch (this) {
                    case VARIANCE -> "chip v";
                    case MATCHED  -> "chip m";
                    case PENDING  -> "chip p";
                    default       -> "chip";
                };
            }
        }

        // ── Enum: status badge variant ────────────────────────────────────────

        /**
         * Status variant for the {@code .mli-status} badge in {@code .mli-bot}.
         *
         * <p>Each value maps to a {@code .st-*} CSS modifier defined in
         * {@code mobile-list-lit-renderer.css}. The badge renders a colored dot ({@code ::before})
         * automatically via CSS.
         */
        enum StatusVariant {
            /** Awaiting approval — amber ({@code .st-await}). */
            AWAITING,
            /** Approved — violet ({@code .st-approve}). */
            APPROVED,
            /** Paid — green ({@code .st-paid}). */
            PAID,
            /** Due soon — primary blue ({@code .st-due}). */
            DUE,
            /** Overdue — red ({@code .st-overdue}). */
            OVERDUE,
            /** Draft — neutral gray ({@code .st-draft}). */
            DRAFT,
            /** Void / cancelled ({@code .st-void}). */
            VOID,
            /** Default — base badge only, no color modifier. */
            DEFAULT;

            /** Returns the full class string for the status badge element. */
            public String toStatusClass() {
                return switch (this) {
                    case AWAITING -> "mli-status st-await";
                    case APPROVED -> "mli-status st-approve";
                    case PAID     -> "mli-status st-paid";
                    case DUE      -> "mli-status st-due";
                    case OVERDUE  -> "mli-status st-overdue";
                    case DRAFT    -> "mli-status st-draft";
                    case VOID     -> "mli-status st-void";
                    default       -> "mli-status";
                };
            }
        }
    }

    // -----------------------------------------------------------------------
    // Mobile Grid Column sub-builder
    // -----------------------------------------------------------------------

    /**
     * Data-bound sub-builder for the mobile grid column pattern — produces the same
     * HTML structure as {@code MobileGridColumnBuilder} but as a client-side {@link LitRenderer}.
     *
     * <p>Unlike the base builder (which uses template strings like {@code ${item.name}}),
     * this sub-builder accepts {@link ValueProvider} lambdas directly and auto-generates
     * property bindings.
     *
     * @param <T> the grid item type
     */
    interface MobileGridColumnBuilder<T> {

        MobileGridColumnBuilder<T> flexDirection(FlexDirection direction);

        // ── Image ────────────────────────────────────────────────────────────

        MobileGridColumnBuilder<T> withImageAsPrimary(ValueProvider<T, String> imagePathProvider, ValueProvider<T, String> altTextProvider);

        MobileGridColumnBuilder<T> withImageAsPrimary(String imagePath, String altText);

        // ── Primary section ──────────────────────────────────────────────────

        MobileGridColumnBuilder<T> withAvatarAsPrimary(ValueProvider<T, String> nameProvider);

        MobileGridColumnBuilder<T> withAvatarAsPrimary(String name);

        MobileGridColumnBuilder<T> withAvatarAsPrimary(ValueProvider<T, String> nameProvider, ValueProvider<T, String> abbrProvider);

        MobileGridColumnBuilder<T> withPrimaryText(ValueProvider<T, String> textProvider);

        MobileGridColumnBuilder<T> withPrimaryText(String text);

        MobileGridColumnBuilder<T> withBadgeAsPrimary(ValueProvider<T, String> textProvider);

        MobileGridColumnBuilder<T> withBadgeAsPrimary(String text);

        MobileGridColumnBuilder<T> withBadgeAsPrimary(ValueProvider<T, String> textProvider, ValueProvider<T, String> classProvider);

        MobileGridColumnBuilder<T> withPrimaryTextAndBadge(ValueProvider<T, String> textProvider, ValueProvider<T, String> badgeTextProvider);

        MobileGridColumnBuilder<T> withPrimaryHtml(String litHtml);
        MobileGridColumnBuilder<T> withPrimaryAsLit(LitRenderer<T> renderer);

        // ── Secondary section ────────────────────────────────────────────────

        MobileGridColumnBuilder<T> withSecondaryText(ValueProvider<T, String> textProvider);

        MobileGridColumnBuilder<T> withSecondaryText(String text);

        MobileGridColumnBuilder<T> withSecondaryHtml(String litHtml);
        MobileGridColumnBuilder<T> withSecondaryAsLit(LitRenderer<T> renderer);

        /**
         * Configure the secondary section as a structured Lit fragment.
         *
         * <p>Use this when the secondary area needs multiple stacked elements instead
         * of a single text node.
         */
        MobileGridColumnBuilder<T> configureSecondary(Consumer<DivElement> configurator);

        // ── Tertiary section ─────────────────────────────────────────────────

        /** Tertiary text with auto-currency formatting (detects numeric values). */
        MobileGridColumnBuilder<T> withTertiaryText(ValueProvider<T, String> textProvider);

        MobileGridColumnBuilder<T> withTertiaryText(String text);

        /** Tertiary text with auto-currency formatting and an explicit semantic text class. */
        MobileGridColumnBuilder<T> withTertiaryText(ValueProvider<T, String> textProvider, String textClassName);

        /** Tertiary text with an explicit semantic text class. */
        MobileGridColumnBuilder<T> withTertiaryText(String text, String textClassName);

        /** Plain tertiary text without currency formatting. */
        MobileGridColumnBuilder<T> withTertiaryPlainText(ValueProvider<T, String> textProvider);

        MobileGridColumnBuilder<T> withTertiaryCurrencyValueAndDate(ValueProvider<T, String> currencyValueProvider, ValueProvider<T, LocalDate> dateProvider);

        MobileGridColumnBuilder<T> withTertiaryHtml(String litHtml);
        MobileGridColumnBuilder<T> withTertiaryAsLit(LitRenderer<T> renderer);

        // ── Click handling ───────────────────────────────────────────────────

        /**
         * Wraps the entire cell content in a clickable container that invokes the
         * named function on the server when the row is clicked.
         *
         * <p>Use with {@link #withFunction(String, SerializableBiConsumer)} to handle:
         * <pre>{@code
         * .onItemClick("handleClick")
         * .withFunction("handleClick", (item, key) -> navigateTo(item))
         * }</pre>
         */
        MobileGridColumnBuilder<T> onItemClick(String functionName);

        // ── Properties & functions ───────────────────────────────────────────

        MobileGridColumnBuilder<T> withProperty(String name, ValueProvider<T, ?> provider);

        MobileGridColumnBuilder<T> withFunction(String name, SerializableBiConsumer<T, String> handler);

        // ── Build ────────────────────────────────────────────────────────────

        LitRenderer<T> build();
    }

    // -----------------------------------------------------------------------
    // Generic Grid Cell sub-builder
    // -----------------------------------------------------------------------

    /**
     * Flexible grid cell builder — N rows stacked vertically; each row has start and end
     * slots for placing components (text, span, pill, image, avatar, icon) horizontally.
     *
     * <p>When a media element (avatar / image / icon) is set via {@code media*()} methods
     * it is placed in a dedicated left column that spans the full cell height, and the
     * rows stack to the right.
     *
     * <p>Usage:
     * <pre>{@code
     * LitRenderer<Customer> renderer = LitRendererBuilder.<Customer>gridCell()
     *     .mediaAvatar(Customer::getFullName, Customer::getPhotoUrl)
     *     .addRow(row -> row
     *         .startText(Customer::getFullName, CellStyle.text().title())
     *         .endPill(Customer::getStatus, item -> CellStyle.pill().success()))
     *     .addRow(row -> row
     *         .startSpan(Customer::getEmail, CellStyle.span().caption())
     *         .endText(Customer::getCity, CellStyle.text().caption()))
     *     .onItemClick("open")
     *     .withFunction("open", (c, k) -> navigateTo(c))
     *     .build();
     * }</pre>
     *
     * <p>Requires {@code grid-cell.css} loaded in the consuming view via
     * {@code @StyleSheet("context://grid-cell.css")}.
     *
     * @param <T> the grid item type
     */
    interface GridCellBuilder<T> {

        // ── Media (full-height left slot) ────────────────────────────────────

        /** Avatar as the left-side media element (default style). */
        GridCellBuilder<T> mediaAvatar(ValueProvider<T, String> nameProvider);

        /** Avatar as the left-side media element with explicit style. */
        GridCellBuilder<T> mediaAvatar(ValueProvider<T, String> nameProvider, CellStyle.AvatarStyle style);

        /** Avatar with photo as the left-side media element (default style). */
        GridCellBuilder<T> mediaAvatar(ValueProvider<T, String> nameProvider,
                                       ValueProvider<T, String> imgProvider);

        /** Avatar with photo as the left-side media element with explicit style. */
        GridCellBuilder<T> mediaAvatar(ValueProvider<T, String> nameProvider,
                                       ValueProvider<T, String> imgProvider,
                                       CellStyle.AvatarStyle style);

        /** Image as the left-side media element (default style). */
        GridCellBuilder<T> mediaImage(ValueProvider<T, String> srcProvider,
                                      ValueProvider<T, String> altProvider);

        /** Image as the left-side media element with explicit style. */
        GridCellBuilder<T> mediaImage(ValueProvider<T, String> srcProvider,
                                      ValueProvider<T, String> altProvider,
                                      CellStyle.ImageStyle style);

        /** Static icon as the left-side media element (default style). */
        GridCellBuilder<T> mediaIcon(String iconName);

        /** Static icon as the left-side media element with explicit style. */
        GridCellBuilder<T> mediaIcon(String iconName, CellStyle.IconStyle style);

        /** Dynamic icon (bound per-row) as the left-side media element (default style). */
        GridCellBuilder<T> mediaIcon(ValueProvider<T, String> iconProvider);

        /** Dynamic icon (bound per-row) as the left-side media element with explicit style. */
        GridCellBuilder<T> mediaIcon(ValueProvider<T, String> iconProvider, CellStyle.IconStyle style);

        // ── Rows ─────────────────────────────────────────────────────────────

        /**
         * Append a horizontal row to the cell.
         * Use {@link GridCellRowBuilder} to add components to the start and end slots.
         */
        GridCellBuilder<T> addRow(Consumer<GridCellRowBuilder<T>> rowConfigurator);

        // ── Interaction ───────────────────────────────────────────────────────

        /**
         * Make the whole cell clickable; the named server function is invoked on click.
         * Use with {@link #withFunction(String, SerializableBiConsumer)}.
         */
        GridCellBuilder<T> onItemClick(String functionName);

        GridCellBuilder<T> withProperty(String name, ValueProvider<T, ?> provider);

        GridCellBuilder<T> withFunction(String name, SerializableBiConsumer<T, String> handler);

        // ── Build ─────────────────────────────────────────────────────────────

        LitRenderer<T> build();
    }

    /**
     * Row-level builder used by {@link GridCellBuilder#addRow(java.util.function.Consumer)}.
     *
     * <p>Each row is a horizontal flex container with a <em>start</em> slot (flex: 1, grows to
     * fill available space) and an <em>end</em> slot (shrinks to content, right-aligned).
     * Slots that receive no components are omitted from the rendered output.
     *
     * <p>All styling is expressed through the {@link CellStyle} type-safe API — no raw
     * CSS class name strings required:
     * <pre>{@code
     * row.startText(Order::getRef,    CellStyle.text().title())
     *    .endPill  (Order::getStatus, item -> CellStyle.pill().success())
     *    .startSpan(Order::getVendor, CellStyle.span().body())
     *    .endText  (Order::getDate,   CellStyle.text().caption())
     * }</pre>
     *
     * @param <T> the grid item type
     */
    interface GridCellRowBuilder<T> {

        // ── Row alignment ─────────────────────────────────────────────────────

        /** Override vertical alignment for items in this row. */
        GridCellRowBuilder<T> alignStart();
        GridCellRowBuilder<T> alignEnd();
        GridCellRowBuilder<T> alignCenter();

        // ── Start slot — text (truncating with ellipsis) ──────────────────────

        /** Truncating text with default style. */
        GridCellRowBuilder<T> startText(ValueProvider<T, String> provider);

        /** Truncating text with explicit {@link CellStyle.TextStyle}. */
        GridCellRowBuilder<T> startText(ValueProvider<T, String> provider, CellStyle.TextStyle style);

        /** Static truncating text with default style. */
        GridCellRowBuilder<T> startText(String text);

        /** Static truncating text with explicit style. */
        GridCellRowBuilder<T> startText(String text, CellStyle.TextStyle style);

        // ── Start slot — span (wrapping) ──────────────────────────────────────

        /** Wrapping span with default style. */
        GridCellRowBuilder<T> startSpan(ValueProvider<T, String> provider);

        /** Wrapping span with explicit {@link CellStyle.SpanStyle}. */
        GridCellRowBuilder<T> startSpan(ValueProvider<T, String> provider, CellStyle.SpanStyle style);

        /** Static wrapping span with default style. */
        GridCellRowBuilder<T> startSpan(String text);

        /** Static wrapping span with explicit style. */
        GridCellRowBuilder<T> startSpan(String text, CellStyle.SpanStyle style);

        // ── Start slot — pill / badge ─────────────────────────────────────────

        /** Badge with explicit static {@link CellStyle.PillStyle}. */
        GridCellRowBuilder<T> startPill(ValueProvider<T, String> provider, CellStyle.PillStyle style);

        /**
         * Badge with a <em>dynamic</em> style resolved per-row.
         * <pre>{@code
         * .startPill(Order::getStatus, o -> switch (o.getState()) {
         *     case ACTIVE   -> CellStyle.pill().success();
         *     case OVERDUE  -> CellStyle.pill().error();
         *     default       -> CellStyle.pill().neutral();
         * })
         * }</pre>
         */
        GridCellRowBuilder<T> startPill(ValueProvider<T, String> provider,
                                        ValueProvider<T, CellStyle.PillStyle> dynamicStyle);

        /** Static badge with explicit style. */
        GridCellRowBuilder<T> startPill(String text, CellStyle.PillStyle style);

        // ── Start slot — image ────────────────────────────────────────────────

        /** Image with default style. */
        GridCellRowBuilder<T> startImage(ValueProvider<T, String> srcProvider,
                                         ValueProvider<T, String> altProvider);

        /** Image with explicit {@link CellStyle.ImageStyle}. */
        GridCellRowBuilder<T> startImage(ValueProvider<T, String> srcProvider,
                                         ValueProvider<T, String> altProvider,
                                         CellStyle.ImageStyle style);

        /** Static image with explicit style. */
        GridCellRowBuilder<T> startImage(String src, String alt, CellStyle.ImageStyle style);

        // ── Start slot — avatar ───────────────────────────────────────────────

        /** Avatar with default style. */
        GridCellRowBuilder<T> startAvatar(ValueProvider<T, String> nameProvider);

        /** Avatar with explicit {@link CellStyle.AvatarStyle}. */
        GridCellRowBuilder<T> startAvatar(ValueProvider<T, String> nameProvider, CellStyle.AvatarStyle style);

        /** Avatar with photo and default style. */
        GridCellRowBuilder<T> startAvatar(ValueProvider<T, String> nameProvider,
                                          ValueProvider<T, String> imgProvider);

        /** Avatar with photo and explicit style. */
        GridCellRowBuilder<T> startAvatar(ValueProvider<T, String> nameProvider,
                                          ValueProvider<T, String> imgProvider,
                                          CellStyle.AvatarStyle style);

        // ── Start slot — icon ─────────────────────────────────────────────────

        /** Static icon with default style. */
        GridCellRowBuilder<T> startIcon(String iconName);

        /** Static icon with explicit {@link CellStyle.IconStyle}. */
        GridCellRowBuilder<T> startIcon(String iconName, CellStyle.IconStyle style);

        /** Dynamic icon with default style. */
        GridCellRowBuilder<T> startIcon(ValueProvider<T, String> iconProvider);

        /** Dynamic icon with explicit style. */
        GridCellRowBuilder<T> startIcon(ValueProvider<T, String> iconProvider, CellStyle.IconStyle style);

        // ── Start slot — raw HTML ─────────────────────────────────────────────

        /** Raw Lit HTML fragment in the start slot (escape hatch). */
        GridCellRowBuilder<T> startHtml(String rawHtml);

        // ── End slot — text ───────────────────────────────────────────────────

        /** Truncating text in the end slot with default style. */
        GridCellRowBuilder<T> endText(ValueProvider<T, String> provider);

        /** Truncating text in the end slot with explicit style. */
        GridCellRowBuilder<T> endText(ValueProvider<T, String> provider, CellStyle.TextStyle style);

        /** Static text in the end slot with default style. */
        GridCellRowBuilder<T> endText(String text);

        /** Static text in the end slot with explicit style. */
        GridCellRowBuilder<T> endText(String text, CellStyle.TextStyle style);

        // ── End slot — span ───────────────────────────────────────────────────

        /** Wrapping span in the end slot with default style. */
        GridCellRowBuilder<T> endSpan(ValueProvider<T, String> provider);

        /** Wrapping span in the end slot with explicit style. */
        GridCellRowBuilder<T> endSpan(ValueProvider<T, String> provider, CellStyle.SpanStyle style);

        /** Static span in the end slot with default style. */
        GridCellRowBuilder<T> endSpan(String text);

        /** Static span in the end slot with explicit style. */
        GridCellRowBuilder<T> endSpan(String text, CellStyle.SpanStyle style);

        // ── End slot — pill / badge ───────────────────────────────────────────

        /** Badge in the end slot with explicit static style. */
        GridCellRowBuilder<T> endPill(ValueProvider<T, String> provider, CellStyle.PillStyle style);

        /**
         * Badge in the end slot with a <em>dynamic</em> style resolved per-row.
         * <pre>{@code
         * .endPill(Order::getStatus, o -> switch (o.getState()) {
         *     case ACTIVE  -> CellStyle.pill().success();
         *     case OVERDUE -> CellStyle.pill().error();
         *     default      -> CellStyle.pill().neutral();
         * })
         * }</pre>
         */
        GridCellRowBuilder<T> endPill(ValueProvider<T, String> provider,
                                      ValueProvider<T, CellStyle.PillStyle> dynamicStyle);

        /** Static badge in the end slot with explicit style. */
        GridCellRowBuilder<T> endPill(String text, CellStyle.PillStyle style);

        // ── End slot — image ──────────────────────────────────────────────────

        /** Image in the end slot with default style. */
        GridCellRowBuilder<T> endImage(ValueProvider<T, String> srcProvider,
                                       ValueProvider<T, String> altProvider);

        /** Image in the end slot with explicit style. */
        GridCellRowBuilder<T> endImage(ValueProvider<T, String> srcProvider,
                                       ValueProvider<T, String> altProvider,
                                       CellStyle.ImageStyle style);

        /** Static image in the end slot with explicit style. */
        GridCellRowBuilder<T> endImage(String src, String alt, CellStyle.ImageStyle style);

        // ── End slot — avatar ─────────────────────────────────────────────────

        /** Avatar in the end slot with default style. */
        GridCellRowBuilder<T> endAvatar(ValueProvider<T, String> nameProvider);

        /** Avatar in the end slot with explicit style. */
        GridCellRowBuilder<T> endAvatar(ValueProvider<T, String> nameProvider, CellStyle.AvatarStyle style);

        /** Avatar with photo in the end slot with default style. */
        GridCellRowBuilder<T> endAvatar(ValueProvider<T, String> nameProvider,
                                        ValueProvider<T, String> imgProvider);

        /** Avatar with photo in the end slot with explicit style. */
        GridCellRowBuilder<T> endAvatar(ValueProvider<T, String> nameProvider,
                                        ValueProvider<T, String> imgProvider,
                                        CellStyle.AvatarStyle style);

        // ── End slot — icon ───────────────────────────────────────────────────

        /** Static icon in the end slot with default style. */
        GridCellRowBuilder<T> endIcon(String iconName);

        /** Static icon in the end slot with explicit style. */
        GridCellRowBuilder<T> endIcon(String iconName, CellStyle.IconStyle style);

        /** Dynamic icon in the end slot with default style. */
        GridCellRowBuilder<T> endIcon(ValueProvider<T, String> iconProvider);

        /** Dynamic icon in the end slot with explicit style. */
        GridCellRowBuilder<T> endIcon(ValueProvider<T, String> iconProvider, CellStyle.IconStyle style);

        // ── End slot — raw HTML ───────────────────────────────────────────────

        /** Raw Lit HTML fragment in the end slot (escape hatch). */
        GridCellRowBuilder<T> endHtml(String rawHtml);
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a new {@link LitRendererBuilder} for composing raw Lit templates.
     *
     * @param <T> the item type
     * @return a new builder instance
     */
    static <T> LitRendererBuilder<T> create() {
        return new DefaultLitRendererBuilder<>();
    }

    /**
     * Create a {@link DocumentRowBuilder} — a semantic 3-row document/transaction
     * renderer with zero CSS class names in calling code.
     *
     * <p>Row structure produced:
     * <ul>
     *   <li>Row 1: reference/PO number (left) + amount (right)</li>
     *   <li>Row 2: title / company name</li>
     *   <li>Row 3: status badge (left) + metadata (right)</li>
     * </ul>
     *
     * <p>Requires {@code document-row-lit-renderer.css} loaded in the consuming view.
     *
     * @param <T> the grid item type
     * @return a new document row builder
     */
    static <T> DocumentRowBuilder<T> documentRow() {
        return new DocumentRowLitRenderer<>();
    }

    /**
     * Create a {@link MobileListItemBuilder} — a semantic 4-section mobile list item
     * renderer with zero CSS class names in calling code.
     *
     * <p>Row structure produced:
     * <ul>
     *   <li><b>Top</b>: number/reference (left) + context label (right)</li>
     *   <li><b>Body</b>: vendor / counterparty name (full-width)</li>
     *   <li><b>Meta</b>: chip badge(s) + reference text</li>
     *   <li><b>Bottom</b>: amount (left) + status badge (right)</li>
     * </ul>
     *
     * <p>An optional {@link MobileListItemBuilder.RowVariant} adds a semantic
     * highlight (border + background tint) to the entire row without any CSS
     * string in calling code.
     *
     * <p>Requires {@code mobile-list-lit-renderer.css} loaded in the consuming view.
     *
     * @param <T> the grid item type
     * @return a new mobile list item builder
     */
    static <T> MobileListItemBuilder<T> mobileListItem() {
        return new MobileListItemLitRenderer<>();
    }

    /**
     * Create a {@link MobileGridColumnBuilder} — a data-bound Lit renderer that produces
     * the same HTML as the component-based {@code MobileGridColumnBuilder} but with
     * zero server-side component overhead per row.
     *
     * @param <T> the grid item type
     * @return a new mobile grid column builder
     */
    static <T> MobileGridColumnBuilder<T> mobileGridColumn() {
        return new MobileGridColumnLitRenderer<>();
    }

    /**
     * Create a {@link GridCellBuilder} — a flexible, generic Lit renderer for
     * Vaadin Grid columns that supports N horizontal rows, optional left-side media
     * (avatar / image / icon), and arbitrary start/end components per row.
     *
     * <p>Requires {@code grid-cell.css} loaded in the consuming view.
     *
     * @param <T> the grid item type
     * @return a new grid cell builder
     */
    static <T> GridCellBuilder<T> gridCell() {
        return new GenericGridCellLitRenderer<>();
    }
}
