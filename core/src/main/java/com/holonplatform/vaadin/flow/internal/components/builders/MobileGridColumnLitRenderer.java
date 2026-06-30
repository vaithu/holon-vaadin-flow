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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.ValueProvider;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link LitRendererBuilder.MobileGridColumnBuilder}.
 *
 * <p>Produces the same HTML structure as the component-based {@code MobileGridColumnBuilder}
 * ({@code .mobile-grid-column} with primary/secondary/tertiary sections) but as a
 * client-side {@link LitRenderer} — zero server-side component overhead per row.
 *
 * @param <T> the grid item type
 * @since 10.0.0
 */
public class MobileGridColumnLitRenderer<T> implements LitRendererBuilder.MobileGridColumnBuilder<T> {

    private FlexDirection direction = FlexDirection.COLUMN;
    private String itemClickFn;

    // Image at root level
    private PropertyBinding imagePath;
    private PropertyBinding imageAlt;

    // Sections
    private final List<Fragment> primaryFragments = new ArrayList<>();
    private final List<Fragment> secondaryFragments = new ArrayList<>();
    private final List<Fragment> tertiaryFragments = new ArrayList<>();

    // Bindings
    private final Map<String, ValueProvider<T, ?>> properties = new LinkedHashMap<>();
    private final Map<String, SerializableBiConsumer<T, String>> functions = new LinkedHashMap<>();
    private final AtomicInteger propCounter = new AtomicInteger(0);

    // ─── Layout direction ────────────────────────────────────────────────

    @Override
    public MobileGridColumnLitRenderer<T> flexDirection(FlexDirection direction) {
        this.direction = direction;
        return this;
    }

    // ─── Image (root level) ──────────────────────────────────────────────

    @Override
    public MobileGridColumnLitRenderer<T> withImageAsPrimary(
            ValueProvider<T, String> imagePathProvider,
            ValueProvider<T, String> altTextProvider) {
        this.imagePath = bind(imagePathProvider);
        this.imageAlt = bind(altTextProvider);
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withImageAsPrimary(String imagePath, String altText) {
        return withImageAsPrimary(item -> imagePath, item -> altText);
    }

    // ─── Primary section ─────────────────────────────────────────────────

    @Override
    public MobileGridColumnLitRenderer<T> withAvatarAsPrimary(ValueProvider<T, String> nameProvider) {
        var nameProp = bind(nameProvider);
        primaryFragments.add(new Fragment(
                "<vaadin-avatar name=\"${item." + nameProp.key + "}\"></vaadin-avatar>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withAvatarAsPrimary(String name) {
        return withAvatarAsPrimary(item -> name);
    }

    @Override
    public MobileGridColumnLitRenderer<T> withAvatarAsPrimary(
            ValueProvider<T, String> nameProvider,
            ValueProvider<T, String> abbrProvider) {
        var nameProp = bind(nameProvider);
        primaryFragments.add(new Fragment(
                "<vaadin-avatar name=\"${item." + nameProp.key + "}\"></vaadin-avatar>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withPrimaryText(ValueProvider<T, String> textProvider) {
        var prop = bind(textProvider);
        primaryFragments.add(new Fragment(
                "<span class=\"mobile-grid-primary-text\" title=\"${item." + prop.key + "}\">${item." + prop.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withPrimaryText(String text) {
        return withPrimaryText(item -> text);
    }

    @Override
    public MobileGridColumnLitRenderer<T> withBadgeAsPrimary(ValueProvider<T, String> textProvider) {
        var prop = bind(textProvider);
        primaryFragments.add(new Fragment(
                "<span class=\"badge\" theme=\"badge small pill\" title=\"${item." + prop.key + "}\">${item." + prop.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withBadgeAsPrimary(String text) {
        return withBadgeAsPrimary(item -> text);
    }

    @Override
    public MobileGridColumnLitRenderer<T> withBadgeAsPrimary(
            ValueProvider<T, String> textProvider,
            ValueProvider<T, String> classProvider) {
        var textProp = bind(textProvider);
        var classProp = bind(classProvider);
        primaryFragments.add(new Fragment(
                "<span class=\"badge ${item." + classProp.key + "}\" theme=\"badge small pill\">${item." + textProp.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withPrimaryTextAndBadge(
            ValueProvider<T, String> textProvider,
            ValueProvider<T, String> badgeTextProvider) {
        withPrimaryText(textProvider);
        withBadgeAsPrimary(badgeTextProvider);
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withPrimaryHtml(String litHtml) {
        primaryFragments.add(new Fragment(litHtml));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withPrimaryAsLit(LitRenderer<T> renderer) {
        primaryFragments.add(new Fragment(templateOf(renderer)));
        mergeFrom(renderer);
        return this;
    }

    // ─── Secondary section ───────────────────────────────────────────────



    @Override
    public MobileGridColumnLitRenderer<T> withSecondaryText(ValueProvider<T, String> textProvider) {
        var prop = bind(textProvider);
        secondaryFragments.add(new Fragment(
                "<span class=\"mobile-grid-secondary-text\" title=\"${item." + prop.key + "}\">${item." + prop.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withSecondaryText(String text) {
        return withSecondaryText(item -> text);
    }

    @Override
    public MobileGridColumnLitRenderer<T> withSecondaryHtml(String litHtml) {
        secondaryFragments.add(new Fragment(litHtml));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withSecondaryAsLit(LitRenderer<T> renderer) {
        secondaryFragments.add(new Fragment(templateOf(renderer)));
        mergeFrom(renderer);
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> configureSecondary(
            java.util.function.Consumer<LitRendererBuilder.DivElement> configurator) {
        Objects.requireNonNull(configurator, "Configurator must not be null");
        var element = new DivElementImpl();
        configurator.accept(element);
        secondaryFragments.add(new Fragment(element.render()));
        return this;
    }

    // ─── Tertiary section ────────────────────────────────────────────────

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryText(ValueProvider<T, String> textProvider) {
        var valueProp = bind(textProvider);
        var classProp = bind(item -> {
            String val = textProvider.apply(item) != null ? textProvider.apply(item).toString() : "";
            if (NumberUtils.isCreatable(val)) {
                double d = NumberUtils.createNumber(val).doubleValue();
                return "mobile-grid-tertiary-value mobile-grid-currency " +
                        (d > 0 ? "mobile-grid-positive" : "mobile-grid-negative");
            }
            return "mobile-grid-tertiary-value";
        });
        var displayProp = bind(item -> {
            String val = textProvider.apply(item) != null ? textProvider.apply(item).toString() : "";
            if (NumberUtils.isCreatable(val)) {
                return "$" + val;
            }
            return val;
        });
        tertiaryFragments.add(new Fragment(
                "<span class=\"${item." + classProp.key + "}\" title=\"${item." + valueProp.key + "}\" style=\"flex-grow: 1;\">${item." + displayProp.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryText(String text) {
        return withTertiaryText(item -> text);
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryText(ValueProvider<T, String> textProvider, String textClassName) {
        var valueProp = bind(textProvider);
        var classProp = bind(item -> {
            String val = textProvider.apply(item) != null ? textProvider.apply(item).toString() : "";
            String classes = "mobile-grid-tertiary-value";
            if (NumberUtils.isCreatable(val)) {
                double d = NumberUtils.createNumber(val).doubleValue();
                classes += " mobile-grid-currency";
                if (textClassName != null && !textClassName.isBlank()) {
                    classes += " " + textClassName;
                } else {
                    classes += d > 0 ? " mobile-grid-positive" : " mobile-grid-negative";
                }
            } else if (textClassName != null && !textClassName.isBlank()) {
                classes += " " + textClassName;
            }
            return classes;
        });
        var displayProp = bind(item -> {
            String val = textProvider.apply(item) != null ? textProvider.apply(item).toString() : "";
            if (NumberUtils.isCreatable(val)) {
                return "$" + val;
            }
            return val;
        });
        tertiaryFragments.add(new Fragment(
                "<span class=\"${item." + classProp.key + "}\" title=\"${item." + valueProp.key + "}\" style=\"flex-grow: 1;\">${item." + displayProp.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryText(String text, String textClassName) {
        return withTertiaryText(item -> text, textClassName);
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryPlainText(ValueProvider<T, String> textProvider) {
        var prop = bind(textProvider);
        tertiaryFragments.add(new Fragment(
                "<span class=\"mobile-grid-tertiary-value\" title=\"${item." + prop.key + "}\" style=\"flex-grow: 1;\">${item." + prop.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryCurrencyValueAndDate(
            ValueProvider<T, String> currencyValueProvider,
            ValueProvider<T, LocalDate> dateProvider) {
        withTertiaryText(currencyValueProvider);
        var dateProp = bind(item -> {
            LocalDate d = dateProvider.apply(item);
            return d != null ? d.format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
        });
        tertiaryFragments.add(new Fragment(
                "<span class=\"mobile-grid-tertiary-date\" title=\"${item." + dateProp.key + "}\">${item." + dateProp.key + "}</span>"));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryHtml(String litHtml) {
        tertiaryFragments.add(new Fragment(litHtml));
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withTertiaryAsLit(LitRenderer<T> renderer) {
        tertiaryFragments.add(new Fragment(templateOf(renderer)));
        mergeFrom(renderer);
        return this;
    }

    // ─── Click handling ──────────────────────────────────────────────────

    @Override
    public MobileGridColumnLitRenderer<T> onItemClick(String functionName) {
        this.itemClickFn = functionName;
        return this;
    }

    // ─── Properties & functions ──────────────────────────────────────────

    @Override
    public MobileGridColumnLitRenderer<T> withProperty(String name, ValueProvider<T, ?> provider) {
        properties.put(name, provider);
        return this;
    }

    @Override
    public MobileGridColumnLitRenderer<T> withFunction(String name, SerializableBiConsumer<T, String> handler) {
        functions.put(name, handler);
        return this;
    }

    // ─── Build ───────────────────────────────────────────────────────────

    @Override
    public LitRenderer<T> build() {
        var sb = new StringBuilder();
        boolean isRow = direction == FlexDirection.ROW;

        // Root div — optionally clickable
        sb.append("<div class=\"mobile-grid-column");
        if (isRow) sb.append(" flex-row");
        sb.append("\"");
        if (itemClickFn != null) {
            sb.append(" @click=\"${").append(itemClickFn).append("}\"");
            sb.append(" style=\"cursor: pointer;\"");
        }
        sb.append(">");

        // Image (root level)
        if (imagePath != null) {
            sb.append("<img class=\"mobile-grid-image\" src=\"${item.")
                    .append(imagePath.key).append("}\" alt=\"${item.")
                    .append(imageAlt.key).append("}\" />");
        }

        // Primary section
        if (!primaryFragments.isEmpty()) {
            sb.append("<div class=\"mobile-grid-primary");
            if (isRow) sb.append(" mobile-grid-column-layout");
            sb.append("\" style=\"display: flex; justify-content: space-between;\">");
            primaryFragments.forEach(f -> sb.append(f.html));
            sb.append("</div>");
        }

        // Secondary section
        if (!secondaryFragments.isEmpty()) {
            sb.append("<div class=\"mobile-grid-secondary");
            if (isRow) sb.append(" mobile-grid-column-layout mobile-grid-grow");
            sb.append("\" style=\"display: flex;\">");
            secondaryFragments.forEach(f -> sb.append(f.html));
            sb.append("</div>");
        }

        // Tertiary section
        if (!tertiaryFragments.isEmpty()) {
            sb.append("<div class=\"mobile-grid-tertiary");
            if (isRow) sb.append(" mobile-grid-column-layout");
            sb.append("\" style=\"display: flex;\">");
            tertiaryFragments.forEach(f -> sb.append(f.html));
            sb.append("</div>");
        }

        sb.append("</div>");

        // Build the LitRenderer
        LitRenderer<T> renderer = LitRenderer.of(sb.toString());
        properties.forEach(renderer::withProperty);
        functions.forEach((name, fn) ->
                renderer.withFunction(name, (SerializableConsumer<T>) item -> fn.accept(item, "")));
        return renderer;
    }

    // ─── Internal helpers ────────────────────────────────────────────────

    private PropertyBinding bind(ValueProvider<T, ?> provider) {
        String key = "p" + propCounter.getAndIncrement();
        properties.put(key, provider);
        return new PropertyBinding(key);
    }

    private static String templateOf(LitRenderer<?> renderer) {
        Objects.requireNonNull(renderer, "Renderer must not be null");

        try {
            var method = LitRenderer.class.getDeclaredMethod("getTemplateExpression");
            method.setAccessible(true);
            return (String) method.invoke(renderer);
        } catch (ReflectiveOperationException ignored) {
            // fall through to field lookup
        }

        for (String fieldName : new String[] {"templateExpression", "template"}) {
            try {
                Field field = LitRenderer.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (String) field.get(renderer);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // try next
            }
        }

        return renderer.toString();
    }

    /**
     * Copies all property providers and client-callable functions from a nested
     * {@link LitRenderer} into this builder's own maps so that {@code ${item.xxx}}
     * bindings embedded via {@code withXxxAsLit} are actually resolved at render time.
     */
    @SuppressWarnings("unchecked")
    private void mergeFrom(LitRenderer<T> renderer) {
        // Vaadin internal field names for value providers — try both variants
        for (String fieldName : new String[] {"valueProviders", "propertyProviders"}) {
            try {
                Field f = LitRenderer.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                Map<String, ValueProvider<T, ?>> vp = (Map<String, ValueProvider<T, ?>>) f.get(renderer);
                if (vp != null) {
                    properties.putAll(vp);
                }
                break;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // try next name
            }
        }

        // Vaadin internal field names for client callables / event handlers
        for (String fieldName : new String[] {"clientCallables", "eventHandlers", "functions"}) {
            try {
                Field f = LitRenderer.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                Object raw = f.get(renderer);
                if (raw instanceof Map<?, ?> map) {
                    map.forEach((k, v) -> {
                        if (k instanceof String name && v instanceof SerializableBiConsumer<?, ?> fn) {
                            functions.put(name, (SerializableBiConsumer<T, String>) fn);
                        }
                    });
                }
                break;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // try next name
            }
        }
    }

    private abstract static class ElementSupport {

        private String className;
        private String style;
        private final Map<String, String> attrs = new LinkedHashMap<>();

        protected void setClassName(String className) {
            this.className = className;
        }

        protected void setStyle(String inlineStyle) {
            this.style = inlineStyle;
        }

        protected void setAttribute(String name, String value) {
            attrs.put(name, value);
        }

        protected void appendCommonAttrs(StringBuilder sb) {
            if (className != null) {
                sb.append(" class=\"").append(className).append('\"');
            }
            if (style != null) {
                sb.append(" style=\"").append(style).append('\"');
            }
            attrs.forEach((name, value) -> sb.append(' ').append(name).append("=\"").append(value).append('"'));
        }
    }

    private static final class SpanElementImpl extends ElementSupport implements LitRendererBuilder.SpanElement {

        private String text;

        @Override
        public SpanElementImpl className(String className) {
            setClassName(className);
            return this;
        }

        @Override
        public SpanElementImpl style(String inlineStyle) {
            setStyle(inlineStyle);
            return this;
        }

        @Override
        public SpanElementImpl attribute(String name, String value) {
            setAttribute(name, value);
            return this;
        }

        @Override
        public SpanElementImpl text(String textOrBinding) {
            this.text = textOrBinding;
            return this;
        }

        String render() {
            var sb = new StringBuilder("<span");
            appendCommonAttrs(sb);
            sb.append('>');
            if (text != null) {
                sb.append(text);
            }
            return sb.append("</span>").toString();
        }
    }

    private static final class DivElementImpl extends ElementSupport implements LitRendererBuilder.DivElement {

        private String text;
        private final List<String> children = new ArrayList<>();

        @Override
        public DivElementImpl className(String className) {
            setClassName(className);
            return this;
        }

        @Override
        public DivElementImpl style(String inlineStyle) {
            setStyle(inlineStyle);
            return this;
        }

        @Override
        public DivElementImpl attribute(String name, String value) {
            setAttribute(name, value);
            return this;
        }

        @Override
        public DivElementImpl text(String textOrBinding) {
            this.text = textOrBinding;
            return this;
        }

        @Override
        public DivElementImpl span(java.util.function.Consumer<LitRendererBuilder.SpanElement> configurator) {
            var element = new SpanElementImpl();
            configurator.accept(element);
            children.add(element.render());
            return this;
        }

        @Override
        public DivElementImpl div(java.util.function.Consumer<LitRendererBuilder.DivElement> configurator) {
            var element = new DivElementImpl();
            configurator.accept(element);
            children.add(element.render());
            return this;
        }

        @Override
        public DivElementImpl img(java.util.function.Consumer<LitRendererBuilder.ImgElement> configurator) {
            var element = new ImgElementImpl();
            configurator.accept(element);
            children.add(element.render());
            return this;
        }

        @Override
        public DivElementImpl icon(java.util.function.Consumer<LitRendererBuilder.IconElement> configurator) {
            var element = new IconElementImpl();
            configurator.accept(element);
            children.add(element.render());
            return this;
        }

        @Override
        public DivElementImpl html(String rawHtml) {
            children.add(rawHtml);
            return this;
        }

        String render() {
            var sb = new StringBuilder("<div");
            appendCommonAttrs(sb);
            sb.append('>');
            if (text != null) {
                sb.append(text);
            }
            children.forEach(sb::append);
            return sb.append("</div>").toString();
        }
    }

    private static final class IconElementImpl extends ElementSupport implements LitRendererBuilder.IconElement {

        private String icon;
        private String src;

        @Override
        public IconElementImpl className(String className) {
            setClassName(className);
            return this;
        }

        @Override
        public IconElementImpl style(String inlineStyle) {
            setStyle(inlineStyle);
            return this;
        }

        @Override
        public IconElementImpl attribute(String name, String value) {
            setAttribute(name, value);
            return this;
        }

        @Override
        public IconElementImpl icon(String iconName) {
            this.icon = iconName;
            return this;
        }

        @Override
        public IconElementImpl src(String srcOrBinding) {
            this.src = srcOrBinding;
            return this;
        }

        String render() {
            var sb = new StringBuilder("<vaadin-icon");
            appendCommonAttrs(sb);
            if (icon != null) {
                sb.append(" icon=\"").append(icon).append('\"');
            }
            if (src != null) {
                sb.append(" src=\"").append(src).append('\"');
            }
            return sb.append("></vaadin-icon>").toString();
        }
    }

    private static final class ImgElementImpl extends ElementSupport implements LitRendererBuilder.ImgElement {

        private String src;
        private String alt;
        private String width;
        private String height;

        @Override
        public ImgElementImpl className(String className) {
            setClassName(className);
            return this;
        }

        @Override
        public ImgElementImpl style(String inlineStyle) {
            setStyle(inlineStyle);
            return this;
        }

        @Override
        public ImgElementImpl attribute(String name, String value) {
            setAttribute(name, value);
            return this;
        }

        @Override
        public ImgElementImpl src(String srcOrBinding) {
            this.src = srcOrBinding;
            return this;
        }

        @Override
        public ImgElementImpl alt(String altOrBinding) {
            this.alt = altOrBinding;
            return this;
        }

        @Override
        public ImgElementImpl width(String width) {
            this.width = width;
            return this;
        }

        @Override
        public ImgElementImpl height(String height) {
            this.height = height;
            return this;
        }

        String render() {
            var sb = new StringBuilder("<img");
            appendCommonAttrs(sb);
            if (src != null) {
                sb.append(" src=\"").append(src).append('\"');
            }
            if (alt != null) {
                sb.append(" alt=\"").append(alt).append('\"');
            }
            if (width != null) {
                sb.append(" width=\"").append(width).append('\"');
            }
            if (height != null) {
                sb.append(" height=\"").append(height).append('\"');
            }
            return sb.append(" />").toString();
        }
    }

    private record PropertyBinding(String key) {}

    private record Fragment(String html) {}
}
