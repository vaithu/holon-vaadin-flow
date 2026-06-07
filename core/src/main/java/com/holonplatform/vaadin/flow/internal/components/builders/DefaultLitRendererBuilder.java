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
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.ValueProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Default implementation of {@link LitRendererBuilder}.
 *
 * <p>Accumulates template fragments and property/function bindings,
 * then produces a {@link LitRenderer} on {@link #build()}.
 *
 * @param <T> the item type
 * @since 10.0.0
 */
public class DefaultLitRendererBuilder<T> implements LitRendererBuilder<T> {

    private final List<String>                                    fragments  = new ArrayList<>();
    private final Map<String, ValueProvider<T, ?>>                properties = new LinkedHashMap<>();
    private final Map<String, SerializableBiConsumer<T, String>>  functions  = new LinkedHashMap<>();

    @Override
    public LitRendererBuilder<T> span(Consumer<SpanElement> configurator) {
        var el = new SpanElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> div(Consumer<DivElement> configurator) {
        var el = new DivElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> horizontalLayout(Consumer<LayoutElement> configurator) {
        var el = new LayoutElementImpl("vaadin-horizontal-layout");
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> verticalLayout(Consumer<LayoutElement> configurator) {
        var el = new LayoutElementImpl("vaadin-vertical-layout");
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> avatar(Consumer<AvatarElement> configurator) {
        var el = new AvatarElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> vaadinButton(Consumer<ButtonElement> configurator) {
        var el = new ButtonElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> icon(Consumer<IconElement> configurator) {
        var el = new IconElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> checkbox(Consumer<CheckboxElement> configurator) {
        var el = new CheckboxElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> img(Consumer<ImgElement> configurator) {
        var el = new ImgElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> progressBar(Consumer<ProgressBarElement> configurator) {
        var el = new ProgressBarElementImpl();
        configurator.accept(el);
        fragments.add(el.render());
        return this;
    }

    @Override
    public LitRendererBuilder<T> html(String rawTemplate) {
        fragments.add(rawTemplate);
        return this;
    }

    @Override
    public LitRendererBuilder<T> withProperty(String name, ValueProvider<T, ?> provider) {
        properties.put(name, provider);
        return this;
    }

    @Override
    public LitRendererBuilder<T> withFunction(String name, SerializableBiConsumer<T, String> handler) {
        functions.put(name, handler);
        return this;
    }

    @Override
    public LitRenderer<T> build() {
        String template = String.join("", fragments);
        LitRenderer<T> renderer = LitRenderer.of(template);
        properties.forEach(renderer::withProperty);
        // Use the unambiguous SerializableConsumer overload to avoid requiring jackson-databind
        // on the compile classpath. LitRenderer function handlers in this builder receive the
        // clicked item — client-side args are not forwarded (pass "" as the string arg).
        functions.forEach((name, fn) ->
                renderer.withFunction(name, (SerializableConsumer<T>) item -> fn.accept(item, "")));
        return renderer;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Base element
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Base for all element impls.  Uses a self-typed generic so that
     * {@code className()}/{@code style()}/{@code attribute()} return the
     * concrete subtype — satisfying each interface's covariant return type
     * contract without needing per-subclass override methods.
     */
    private static abstract class AbstractElementImpl<E extends AbstractElementImpl<E>> {
        protected final Map<String, String> attrs = new LinkedHashMap<>();
        protected String cssClass;
        protected String inlineStyle;

        @SuppressWarnings("unchecked")
        protected E self() { return (E) this; }

        public E className(String className)            { this.cssClass     = className;   return self(); }
        public E style(String inlineStyle)              { this.inlineStyle  = inlineStyle; return self(); }
        public E attribute(String name, String value)   { attrs.put(name, value);          return self(); }

        protected void appendCommonAttrs(StringBuilder sb) {
            if (cssClass    != null) sb.append(" class=\"").append(cssClass).append("\"");
            if (inlineStyle != null) sb.append(" style=\"").append(inlineStyle).append("\"");
            attrs.forEach((k, v) -> sb.append(' ').append(k).append("=\"").append(v).append("\""));
        }
    }

    // ── SpanElement ─────────────────────────────────────────────────────────

    private static class SpanElementImpl
            extends AbstractElementImpl<SpanElementImpl>
            implements SpanElement {

        private String text;

        @Override public SpanElement text(String textOrBinding) { this.text = textOrBinding; return this; }

        String render() {
            var sb = new StringBuilder("<span");
            appendCommonAttrs(sb);
            sb.append('>');
            if (text != null) sb.append(text);
            return sb.append("</span>").toString();
        }
    }

    // ── DivElement ──────────────────────────────────────────────────────────

    private static class DivElementImpl
            extends AbstractElementImpl<DivElementImpl>
            implements DivElement {

        private String text;
        private final List<String> children = new ArrayList<>();

        @Override public DivElement text(String textOrBinding) { this.text = textOrBinding; return this; }

        @Override
        public DivElement span(Consumer<SpanElement> configurator) {
            var el = new SpanElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public DivElement div(Consumer<DivElement> configurator) {
            var el = new DivElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public DivElement img(Consumer<ImgElement> configurator) {
            var el = new ImgElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public DivElement icon(Consumer<IconElement> configurator) {
            var el = new IconElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public DivElement html(String rawHtml) { children.add(rawHtml); return this; }

        String render() {
            var sb = new StringBuilder("<div");
            appendCommonAttrs(sb);
            sb.append('>');
            if (text != null) sb.append(text);
            children.forEach(sb::append);
            return sb.append("</div>").toString();
        }
    }

    // ── LayoutElement ───────────────────────────────────────────────────────

    private static class LayoutElementImpl
            extends AbstractElementImpl<LayoutElementImpl>
            implements LayoutElement {

        private final String       tagName;
        private       String       theme;
        private final List<String> children = new ArrayList<>();

        LayoutElementImpl(String tagName) { this.tagName = tagName; }

        @Override public LayoutElement theme(String theme) { this.theme = theme; return this; }

        @Override
        public LayoutElement span(Consumer<SpanElement> configurator) {
            var el = new SpanElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement div(Consumer<DivElement> configurator) {
            var el = new DivElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement avatar(Consumer<AvatarElement> configurator) {
            var el = new AvatarElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement vaadinButton(Consumer<ButtonElement> configurator) {
            var el = new ButtonElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement icon(Consumer<IconElement> configurator) {
            var el = new IconElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement img(Consumer<ImgElement> configurator) {
            var el = new ImgElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement checkbox(Consumer<CheckboxElement> configurator) {
            var el = new CheckboxElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement progressBar(Consumer<ProgressBarElement> configurator) {
            var el = new ProgressBarElementImpl(); configurator.accept(el); children.add(el.render()); return this;
        }

        @Override
        public LayoutElement html(String rawHtml) { children.add(rawHtml); return this; }

        String render() {
            var sb = new StringBuilder("<").append(tagName);
            if (theme != null) sb.append(" theme=\"").append(theme).append('"');
            appendCommonAttrs(sb);
            sb.append('>');
            children.forEach(sb::append);
            return sb.append("</").append(tagName).append('>').toString();
        }
    }

    // ── AvatarElement ───────────────────────────────────────────────────────

    private static class AvatarElementImpl
            extends AbstractElementImpl<AvatarElementImpl>
            implements AvatarElement {

        private String name;
        private String imgSrc;
        private String abbr;

        @Override public AvatarElement name(String nameOrBinding) { this.name   = nameOrBinding; return this; }
        @Override public AvatarElement img(String srcOrBinding)   { this.imgSrc = srcOrBinding;  return this; }
        @Override public AvatarElement abbr(String abbrOrBinding) { this.abbr   = abbrOrBinding; return this; }

        String render() {
            var sb = new StringBuilder("<vaadin-avatar");
            if (name   != null) sb.append(" name=\"").append(name).append('"');
            if (imgSrc != null) sb.append(" img=\"").append(imgSrc).append('"');
            if (abbr   != null) sb.append(" abbr=\"").append(abbr).append('"');
            appendCommonAttrs(sb);
            return sb.append("></vaadin-avatar>").toString();
        }
    }

    // ── ButtonElement ───────────────────────────────────────────────────────

    private static class ButtonElementImpl
            extends AbstractElementImpl<ButtonElementImpl>
            implements ButtonElement {

        private String text;
        private String theme;
        private String onClickFn;
        private String iconHtml;
        private String disabled;

        @Override public ButtonElement text(String textOrBinding)          { this.text      = textOrBinding; return this; }
        @Override public ButtonElement theme(String theme)                  { this.theme     = theme;         return this; }
        @Override public ButtonElement onClick(String functionName)         { this.onClickFn = functionName;  return this; }
        @Override public ButtonElement disabled(String booleanBinding)      { this.disabled  = booleanBinding; return this; }

        @Override
        public ButtonElement icon(Consumer<IconElement> configurator) {
            var el = new IconElementImpl(); configurator.accept(el); this.iconHtml = el.render(); return this;
        }

        String render() {
            var sb = new StringBuilder("<vaadin-button");
            if (theme     != null) sb.append(" theme=\"").append(theme).append('"');
            if (onClickFn != null) sb.append(" @click=\"${").append(onClickFn).append("}\"");
            if (disabled  != null) sb.append(" ?disabled=\"").append(disabled).append('"');
            appendCommonAttrs(sb);
            sb.append('>');
            if (iconHtml != null) sb.append(iconHtml);
            if (text     != null) sb.append(text);
            return sb.append("</vaadin-button>").toString();
        }
    }

    // ── IconElement ─────────────────────────────────────────────────────────

    private static class IconElementImpl
            extends AbstractElementImpl<IconElementImpl>
            implements IconElement {

        private String iconName;
        private String src;

        @Override public IconElement icon(String iconName)      { this.iconName = iconName; return this; }
        @Override public IconElement src(String srcOrBinding)   { this.src      = srcOrBinding; return this; }

        String render() {
            var sb = new StringBuilder("<vaadin-icon");
            if (iconName != null) sb.append(" icon=\"").append(iconName).append('"');
            if (src      != null) sb.append(" src=\"").append(src).append('"');
            appendCommonAttrs(sb);
            return sb.append("></vaadin-icon>").toString();
        }
    }

    // ── CheckboxElement ─────────────────────────────────────────────────────

    private static class CheckboxElementImpl
            extends AbstractElementImpl<CheckboxElementImpl>
            implements CheckboxElement {

        private String  checked;
        private boolean readOnly;

        @Override public CheckboxElement checked(String booleanBinding) { this.checked  = booleanBinding; return this; }
        @Override public CheckboxElement readOnly()                      { this.readOnly = true;           return this; }

        String render() {
            var sb = new StringBuilder("<vaadin-checkbox");
            if (checked  != null) sb.append(" ?checked=\"").append(checked).append('"');
            if (readOnly) sb.append(" onclick=\"return false;\" onkeydown=\"return false;\"");
            appendCommonAttrs(sb);
            return sb.append("></vaadin-checkbox>").toString();
        }
    }

    // ── ImgElement ──────────────────────────────────────────────────────────

    private static class ImgElementImpl
            extends AbstractElementImpl<ImgElementImpl>
            implements ImgElement {

        private String src;
        private String alt;
        private String width;
        private String height;

        @Override public ImgElement src(String srcOrBinding)    { this.src    = srcOrBinding; return this; }
        @Override public ImgElement alt(String altOrBinding)    { this.alt    = altOrBinding; return this; }
        @Override public ImgElement width(String width)         { this.width  = width;        return this; }
        @Override public ImgElement height(String height)       { this.height = height;       return this; }

        String render() {
            var sb = new StringBuilder("<img");
            if (src    != null) sb.append(" src=\"").append(src).append('"');
            if (alt    != null) sb.append(" alt=\"").append(alt).append('"');
            if (width  != null) sb.append(" width=\"").append(width).append('"');
            if (height != null) sb.append(" height=\"").append(height).append('"');
            appendCommonAttrs(sb);
            return sb.append(" />").toString();
        }
    }

    // ── ProgressBarElement ──────────────────────────────────────────────────

    private static class ProgressBarElementImpl
            extends AbstractElementImpl<ProgressBarElementImpl>
            implements ProgressBarElement {

        private String value;
        private String min;
        private String max;

        @Override public ProgressBarElement value(String valueBinding) { this.value = valueBinding; return this; }
        @Override public ProgressBarElement min(String minBinding)     { this.min   = minBinding;   return this; }
        @Override public ProgressBarElement max(String maxBinding)     { this.max   = maxBinding;   return this; }

        String render() {
            var sb = new StringBuilder("<vaadin-progress-bar");
            if (value != null) sb.append(" value=\"").append(value).append('"');
            if (min   != null) sb.append(" min=\"").append(min).append('"');
            if (max   != null) sb.append(" max=\"").append(max).append('"');
            appendCommonAttrs(sb);
            return sb.append("></vaadin-progress-bar>").toString();
        }
    }
}
