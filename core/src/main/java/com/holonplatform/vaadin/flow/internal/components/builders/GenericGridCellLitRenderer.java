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

import com.holonplatform.vaadin.flow.components.builders.CellStyle;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.ValueProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Implementation of {@link LitRendererBuilder.GridCellBuilder}.
 *
 * <p>Produces a flexible Vaadin Grid cell with:
 * <ul>
 *   <li>An optional left-side <em>media</em> column (avatar / image / icon) that spans
 *       the full cell height.</li>
 *   <li>N horizontal rows stacked vertically in the content area.</li>
 *   <li>Each row has a <em>start</em> slot (flex-grow: 1) and an <em>end</em> slot
 *       (flex-shrink: 0, right-aligned).</li>
 * </ul>
 *
 * <p>All styling is driven by {@link CellStyle} — no raw CSS class strings in calling code.
 * Requires {@code grid-cell.css} to be loaded in the consuming view.
 *
 * @param <T> the grid item type
 * @since 10.0.0
 */
public class GenericGridCellLitRenderer<T> implements LitRendererBuilder.GridCellBuilder<T> {

    // ── Media ──────────────────────────────────────────────────────────────

    private String mediaHtml;

    // ── Rows ───────────────────────────────────────────────────────────────

    private final List<RowDef> rows = new ArrayList<>();

    // ── Interaction ────────────────────────────────────────────────────────

    private String itemClickFn;

    // ── Bindings ───────────────────────────────────────────────────────────

    private final Map<String, ValueProvider<T, ?>> properties = new LinkedHashMap<>();
    private final Map<String, SerializableBiConsumer<T, String>> functions = new LinkedHashMap<>();
    private final AtomicInteger propCounter = new AtomicInteger(0);

    // ── Media methods ─────────────────────────────────────────────────��─────

    @Override
    public GenericGridCellLitRenderer<T> mediaAvatar(ValueProvider<T, String> nameProvider) {
        return mediaAvatar(nameProvider, CellStyle.DEFAULT_AVATAR);
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaAvatar(ValueProvider<T, String> nameProvider,
                                                      CellStyle.AvatarStyle style) {
        var nameProp = bind(nameProvider);
        mediaHtml = "<vaadin-avatar class=\"" + style.toCss() + "\" name=\"${item." + nameProp + "}\"></vaadin-avatar>";
        return this;
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaAvatar(ValueProvider<T, String> nameProvider,
                                                      ValueProvider<T, String> imgProvider) {
        return mediaAvatar(nameProvider, imgProvider, CellStyle.DEFAULT_AVATAR);
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaAvatar(ValueProvider<T, String> nameProvider,
                                                      ValueProvider<T, String> imgProvider,
                                                      CellStyle.AvatarStyle style) {
        var nameProp = bind(nameProvider);
        var imgProp  = bind(imgProvider);
        mediaHtml = "<vaadin-avatar class=\"" + style.toCss() + "\" name=\"${item." + nameProp
                + "}\" img=\"${item." + imgProp + "}\"></vaadin-avatar>";
        return this;
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaImage(ValueProvider<T, String> srcProvider,
                                                     ValueProvider<T, String> altProvider) {
        return mediaImage(srcProvider, altProvider, CellStyle.DEFAULT_IMAGE);
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaImage(ValueProvider<T, String> srcProvider,
                                                     ValueProvider<T, String> altProvider,
                                                     CellStyle.ImageStyle style) {
        var srcProp = bind(srcProvider);
        var altProp = bind(altProvider);
        mediaHtml = "<img class=\"" + style.toCss() + "\" src=\"${item." + srcProp
                + "}\" alt=\"${item." + altProp + "}\" />";
        return this;
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaIcon(String iconName) {
        return mediaIcon(iconName, CellStyle.DEFAULT_ICON);
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaIcon(String iconName, CellStyle.IconStyle style) {
        mediaHtml = "<vaadin-icon class=\"" + style.toCss() + "\" icon=\"" + iconName + "\"></vaadin-icon>";
        return this;
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaIcon(ValueProvider<T, String> iconProvider) {
        return mediaIcon(iconProvider, CellStyle.DEFAULT_ICON);
    }

    @Override
    public GenericGridCellLitRenderer<T> mediaIcon(ValueProvider<T, String> iconProvider,
                                                    CellStyle.IconStyle style) {
        var iconProp = bind(iconProvider);
        mediaHtml = "<vaadin-icon class=\"" + style.toCss() + "\" icon=\"${item." + iconProp + "}\"></vaadin-icon>";
        return this;
    }

    // ── Rows ────────────────────────────────────────────────────────────────

    @Override
    public GenericGridCellLitRenderer<T> addRow(Consumer<LitRendererBuilder.GridCellRowBuilder<T>> rowConfigurator) {
        var rowDef = new RowDef();
        rowConfigurator.accept(new RowBuilderImpl(rowDef));
        rows.add(rowDef);
        return this;
    }

    // ── Interaction ─────────────────────────────────────────────────────────

    @Override
    public GenericGridCellLitRenderer<T> onItemClick(String functionName) {
        this.itemClickFn = functionName;
        return this;
    }

    @Override
    public GenericGridCellLitRenderer<T> withProperty(String name, ValueProvider<T, ?> provider) {
        properties.put(name, provider);
        return this;
    }

    @Override
    public GenericGridCellLitRenderer<T> withFunction(String name, SerializableBiConsumer<T, String> handler) {
        functions.put(name, handler);
        return this;
    }

    // ── Build ────────────────────────────────────────────────────────────────

    @Override
    public LitRenderer<T> build() {
        var sb = new StringBuilder();
        boolean hasMedia = mediaHtml != null;

        sb.append("<div class=\"gcb");
        if (hasMedia)            sb.append(" gcb-has-media");
        if (itemClickFn != null) sb.append(" gcb-clickable");
        sb.append("\"");
        if (itemClickFn != null) {
            sb.append(" @click=\"${").append(itemClickFn).append("}\"");
        }
        sb.append(">");

        if (hasMedia) {
            sb.append("<div class=\"gcb-media\">").append(mediaHtml).append("</div>");
            sb.append("<div class=\"gcb-body\">");
        }

        for (RowDef row : rows) {
            sb.append("<div class=\"gcb-row");
            if (row.alignClass != null) sb.append(' ').append(row.alignClass);
            sb.append("\">");

            if (!row.startFragments.isEmpty()) {
                sb.append("<div class=\"gcb-start\">");
                row.startFragments.forEach(sb::append);
                sb.append("</div>");
            }

            if (!row.endFragments.isEmpty()) {
                sb.append("<div class=\"gcb-end\">");
                row.endFragments.forEach(sb::append);
                sb.append("</div>");
            }

            sb.append("</div>"); // gcb-row
        }

        if (hasMedia) {
            sb.append("</div>"); // gcb-body
        }

        sb.append("</div>"); // gcb

        LitRenderer<T> renderer = LitRenderer.of(sb.toString());
        properties.forEach(renderer::withProperty);
        functions.forEach((name, fn) ->
                renderer.withFunction(name, (SerializableConsumer<T>) item -> fn.accept(item, "")));
        return renderer;
    }

    // ── Internal helpers ────────────────────────────────────────────��────────

    private String bind(ValueProvider<T, ?> provider) {
        String key = "g" + propCounter.getAndIncrement();
        properties.put(key, provider);
        return key;
    }

    // ── RowDef ────────────────────────────────────────────────────────────────

    private static final class RowDef {
        final List<String> startFragments = new ArrayList<>();
        final List<String> endFragments   = new ArrayList<>();
        String alignClass;
    }

    // ── RowBuilderImpl ─────────────────────────────────────────────────────────

    private final class RowBuilderImpl implements LitRendererBuilder.GridCellRowBuilder<T> {

        private final RowDef rowDef;

        RowBuilderImpl(RowDef rowDef) {
            this.rowDef = rowDef;
        }

        // ── Row alignment ────────────────────────────────────────────────────

        @Override public RowBuilderImpl alignStart()  { rowDef.alignClass = "gcb-align-start";  return this; }
        @Override public RowBuilderImpl alignEnd()    { rowDef.alignClass = "gcb-align-end";    return this; }
        @Override public RowBuilderImpl alignCenter() { rowDef.alignClass = "gcb-align-center"; return this; }

        // ── Fragment generators ──────────────────────────────────────────────

        private String textFrag(ValueProvider<T, String> tp, CellStyle.TextStyle s) {
            var prop = bind(tp);
            return "<span class=\"" + s.toCss() + "\" title=\"${item." + prop + "}\">${item." + prop + "}</span>";
        }

        private String spanFrag(ValueProvider<T, String> tp, CellStyle.SpanStyle s) {
            var prop = bind(tp);
            return "<span class=\"" + s.toCss() + "\">${item." + prop + "}</span>";
        }

        private String pillFrag(ValueProvider<T, String> tp, CellStyle.PillStyle s) {
            var prop = bind(tp);
            return "<span class=\"" + s.toCss() + "\" theme=\"" + s.toTheme() + "\">${item." + prop + "}</span>";
        }

        private String dynPillFrag(ValueProvider<T, String> tp, ValueProvider<T, CellStyle.PillStyle> sp) {
            var textProp  = bind(tp);
            var cssProp   = bind(item -> sp.apply(item).toCss());
            var themeProp = bind(item -> sp.apply(item).toTheme());
            return "<span class=\"${item." + cssProp + "}\" theme=\"${item." + themeProp + "}\">${item." + textProp + "}</span>";
        }

        private String imgFrag(ValueProvider<T, String> sp, ValueProvider<T, String> ap, CellStyle.ImageStyle s) {
            var srcProp = bind(sp);
            var altProp = bind(ap);
            return "<img class=\"" + s.toCss() + "\" src=\"${item." + srcProp + "}\" alt=\"${item." + altProp + "}\" />";
        }

        private String avatarFrag(ValueProvider<T, String> np, ValueProvider<T, String> ip, CellStyle.AvatarStyle s) {
            var nameProp = bind(np);
            if (ip != null) {
                var imgProp = bind(ip);
                return "<vaadin-avatar class=\"" + s.toCss() + "\" name=\"${item." + nameProp
                        + "}\" img=\"${item." + imgProp + "}\"></vaadin-avatar>";
            }
            return "<vaadin-avatar class=\"" + s.toCss() + "\" name=\"${item." + nameProp + "}\"></vaadin-avatar>";
        }

        private String iconFrag(String iconName, CellStyle.IconStyle s) {
            return "<vaadin-icon class=\"" + s.toCss() + "\" icon=\"" + iconName + "\"></vaadin-icon>";
        }

        private String dynIconFrag(ValueProvider<T, String> ip, CellStyle.IconStyle s) {
            var prop = bind(ip);
            return "<vaadin-icon class=\"" + s.toCss() + "\" icon=\"${item." + prop + "}\"></vaadin-icon>";
        }

        // ── Start slot ───────────────────────────────────────────────────────

        @Override public RowBuilderImpl startText(ValueProvider<T, String> p)                                                         { rowDef.startFragments.add(textFrag(p, CellStyle.DEFAULT_TEXT));    return this; }
        @Override public RowBuilderImpl startText(ValueProvider<T, String> p, CellStyle.TextStyle s)                                  { rowDef.startFragments.add(textFrag(p, s));                          return this; }
        @Override public RowBuilderImpl startText(String t)                                                                            { return startText(item -> t); }
        @Override public RowBuilderImpl startText(String t, CellStyle.TextStyle s)                                                    { return startText(item -> t, s); }

        @Override public RowBuilderImpl startSpan(ValueProvider<T, String> p)                                                         { rowDef.startFragments.add(spanFrag(p, CellStyle.DEFAULT_SPAN));    return this; }
        @Override public RowBuilderImpl startSpan(ValueProvider<T, String> p, CellStyle.SpanStyle s)                                  { rowDef.startFragments.add(spanFrag(p, s));                          return this; }
        @Override public RowBuilderImpl startSpan(String t)                                                                            { return startSpan(item -> t); }
        @Override public RowBuilderImpl startSpan(String t, CellStyle.SpanStyle s)                                                    { return startSpan(item -> t, s); }

        @Override public RowBuilderImpl startPill(ValueProvider<T, String> p, CellStyle.PillStyle s)                                  { rowDef.startFragments.add(pillFrag(p, s));                          return this; }
        @Override public RowBuilderImpl startPill(ValueProvider<T, String> p, ValueProvider<T, CellStyle.PillStyle> ds)               { rowDef.startFragments.add(dynPillFrag(p, ds));                      return this; }
        @Override public RowBuilderImpl startPill(String t, CellStyle.PillStyle s)                                                    { return startPill(item -> t, s); }

        @Override public RowBuilderImpl startImage(ValueProvider<T, String> sp, ValueProvider<T, String> ap)                          { rowDef.startFragments.add(imgFrag(sp, ap, CellStyle.DEFAULT_IMAGE)); return this; }
        @Override public RowBuilderImpl startImage(ValueProvider<T, String> sp, ValueProvider<T, String> ap, CellStyle.ImageStyle s)  { rowDef.startFragments.add(imgFrag(sp, ap, s));                        return this; }
        @Override public RowBuilderImpl startImage(String src, String alt, CellStyle.ImageStyle s)                                    { return startImage(item -> src, item -> alt, s); }

        @Override public RowBuilderImpl startAvatar(ValueProvider<T, String> np)                                                      { rowDef.startFragments.add(avatarFrag(np, null, CellStyle.DEFAULT_AVATAR)); return this; }
        @Override public RowBuilderImpl startAvatar(ValueProvider<T, String> np, CellStyle.AvatarStyle s)                            { rowDef.startFragments.add(avatarFrag(np, null, s));                        return this; }
        @Override public RowBuilderImpl startAvatar(ValueProvider<T, String> np, ValueProvider<T, String> ip)                        { rowDef.startFragments.add(avatarFrag(np, ip,   CellStyle.DEFAULT_AVATAR)); return this; }
        @Override public RowBuilderImpl startAvatar(ValueProvider<T, String> np, ValueProvider<T, String> ip, CellStyle.AvatarStyle s){ rowDef.startFragments.add(avatarFrag(np, ip,   s));                        return this; }

        @Override public RowBuilderImpl startIcon(String n)                                                                           { rowDef.startFragments.add(iconFrag(n, CellStyle.DEFAULT_ICON));      return this; }
        @Override public RowBuilderImpl startIcon(String n, CellStyle.IconStyle s)                                                    { rowDef.startFragments.add(iconFrag(n, s));                            return this; }
        @Override public RowBuilderImpl startIcon(ValueProvider<T, String> p)                                                         { rowDef.startFragments.add(dynIconFrag(p, CellStyle.DEFAULT_ICON));   return this; }
        @Override public RowBuilderImpl startIcon(ValueProvider<T, String> p, CellStyle.IconStyle s)                                  { rowDef.startFragments.add(dynIconFrag(p, s));                         return this; }

        @Override public RowBuilderImpl startHtml(String rawHtml) { rowDef.startFragments.add(rawHtml); return this; }

        // ── End slot ─────────────────────────────────────────────────────────

        @Override public RowBuilderImpl endText(ValueProvider<T, String> p)                                                           { rowDef.endFragments.add(textFrag(p, CellStyle.DEFAULT_TEXT));    return this; }
        @Override public RowBuilderImpl endText(ValueProvider<T, String> p, CellStyle.TextStyle s)                                    { rowDef.endFragments.add(textFrag(p, s));                          return this; }
        @Override public RowBuilderImpl endText(String t)                                                                              { return endText(item -> t); }
        @Override public RowBuilderImpl endText(String t, CellStyle.TextStyle s)                                                      { return endText(item -> t, s); }

        @Override public RowBuilderImpl endSpan(ValueProvider<T, String> p)                                                           { rowDef.endFragments.add(spanFrag(p, CellStyle.DEFAULT_SPAN));    return this; }
        @Override public RowBuilderImpl endSpan(ValueProvider<T, String> p, CellStyle.SpanStyle s)                                    { rowDef.endFragments.add(spanFrag(p, s));                          return this; }
        @Override public RowBuilderImpl endSpan(String t)                                                                              { return endSpan(item -> t); }
        @Override public RowBuilderImpl endSpan(String t, CellStyle.SpanStyle s)                                                      { return endSpan(item -> t, s); }

        @Override public RowBuilderImpl endPill(ValueProvider<T, String> p, CellStyle.PillStyle s)                                    { rowDef.endFragments.add(pillFrag(p, s));                          return this; }
        @Override public RowBuilderImpl endPill(ValueProvider<T, String> p, ValueProvider<T, CellStyle.PillStyle> ds)                 { rowDef.endFragments.add(dynPillFrag(p, ds));                      return this; }
        @Override public RowBuilderImpl endPill(String t, CellStyle.PillStyle s)                                                      { return endPill(item -> t, s); }

        @Override public RowBuilderImpl endImage(ValueProvider<T, String> sp, ValueProvider<T, String> ap)                            { rowDef.endFragments.add(imgFrag(sp, ap, CellStyle.DEFAULT_IMAGE)); return this; }
        @Override public RowBuilderImpl endImage(ValueProvider<T, String> sp, ValueProvider<T, String> ap, CellStyle.ImageStyle s)    { rowDef.endFragments.add(imgFrag(sp, ap, s));                        return this; }
        @Override public RowBuilderImpl endImage(String src, String alt, CellStyle.ImageStyle s)                                      { return endImage(item -> src, item -> alt, s); }

        @Override public RowBuilderImpl endAvatar(ValueProvider<T, String> np)                                                        { rowDef.endFragments.add(avatarFrag(np, null, CellStyle.DEFAULT_AVATAR)); return this; }
        @Override public RowBuilderImpl endAvatar(ValueProvider<T, String> np, CellStyle.AvatarStyle s)                              { rowDef.endFragments.add(avatarFrag(np, null, s));                        return this; }
        @Override public RowBuilderImpl endAvatar(ValueProvider<T, String> np, ValueProvider<T, String> ip)                          { rowDef.endFragments.add(avatarFrag(np, ip,   CellStyle.DEFAULT_AVATAR)); return this; }
        @Override public RowBuilderImpl endAvatar(ValueProvider<T, String> np, ValueProvider<T, String> ip, CellStyle.AvatarStyle s) { rowDef.endFragments.add(avatarFrag(np, ip,   s));                        return this; }

        @Override public RowBuilderImpl endIcon(String n)                                                                             { rowDef.endFragments.add(iconFrag(n, CellStyle.DEFAULT_ICON));      return this; }
        @Override public RowBuilderImpl endIcon(String n, CellStyle.IconStyle s)                                                      { rowDef.endFragments.add(iconFrag(n, s));                            return this; }
        @Override public RowBuilderImpl endIcon(ValueProvider<T, String> p)                                                           { rowDef.endFragments.add(dynIconFrag(p, CellStyle.DEFAULT_ICON));   return this; }
        @Override public RowBuilderImpl endIcon(ValueProvider<T, String> p, CellStyle.IconStyle s)                                    { rowDef.endFragments.add(dynIconFrag(p, s));                         return this; }

        @Override public RowBuilderImpl endHtml(String rawHtml) { rowDef.endFragments.add(rawHtml); return this; }
    }
}

