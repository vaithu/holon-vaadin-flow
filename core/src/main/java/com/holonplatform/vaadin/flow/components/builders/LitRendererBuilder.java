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
}
