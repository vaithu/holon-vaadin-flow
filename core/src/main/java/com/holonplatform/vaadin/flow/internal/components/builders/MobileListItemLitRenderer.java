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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link LitRendererBuilder.MobileListItemBuilder}.
 *
 * <p>Produces a 4-section mobile list item rendered as a client-side
 * {@link LitRenderer}. All CSS class names are hardcoded internally;
 * callers use only {@link ValueProvider} lambdas and enum values.
 *
 * <p>HTML structure produced (all sections optional):
 * <pre>{@code
 * <div class="mli [mli--exception|mli--warning|...]">
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
 * <p>Requires {@code mobile-list-lit-renderer.css} loaded in the consuming view.
 *
 * @param <T> the grid item type
 * @since 10.0.0
 */
public class MobileListItemLitRenderer<T> implements LitRendererBuilder.MobileListItemBuilder<T> {

    // ── Slot bindings ─────────────────────────────────────────────────────

    private PropertyBinding rootVariantClass;   // root: "mli" / "mli exception" / "mli on" ...
    private PropertyBinding number;             // mli-top left:  mli-num
    private PropertyBinding when;               // mli-top right: mli-when
    private PropertyBinding vendor;             // mli-vendor (full-width body)
    private PropertyBinding chipLabel;          // mli-meta: primary chip text
    private PropertyBinding chipClass;          // mli-meta: primary chip CSS class ("chip v" etc.)
    private PropertyBinding chip2Label;         // mli-meta: secondary chip text (optional)
    private PropertyBinding chip2Class;         // mli-meta: secondary chip CSS class
    private PropertyBinding metaRef;            // mli-meta: plain reference text
    private PropertyBinding amount;             // mli-bot left:  mli-amt
    private PropertyBinding statusLabel;        // mli-bot right: status text
    private PropertyBinding statusClass;        // mli-bot right: "mli-status st-await" etc.

    // ── Click ─────────────────────────────────────────────────────────────

    private String itemClickFn;

    // ── Bindings ──────────────────────────────────────────────────────────

    private final Map<String, ValueProvider<T, ?>> properties = new LinkedHashMap<>();
    private final Map<String, SerializableBiConsumer<T, String>> functions = new LinkedHashMap<>();
    private final AtomicInteger propCounter = new AtomicInteger(0);

    // ── Builder methods ───────────────────────────────────────────────────

    @Override
    public MobileListItemLitRenderer<T> withRootVariant(
            ValueProvider<T, LitRendererBuilder.MobileListItemBuilder.RowVariant> variantProvider) {
        this.rootVariantClass = bind(item -> {
            LitRendererBuilder.MobileListItemBuilder.RowVariant v = variantProvider.apply(item);
            return v != null ? v.toRootClass() : "mli";
        });
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> onItemClick(String functionName) {
        this.itemClickFn = functionName;
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withNumber(ValueProvider<T, String> numProvider) {
        this.number = bind(numProvider);
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withWhen(ValueProvider<T, String> whenProvider) {
        this.when = bind(whenProvider);
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withVendor(ValueProvider<T, String> vendorProvider) {
        this.vendor = bind(vendorProvider);
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withChip(
            ValueProvider<T, String> labelProvider,
            ValueProvider<T, LitRendererBuilder.MobileListItemBuilder.ChipVariant> variantProvider) {
        this.chipLabel = bind(labelProvider);
        this.chipClass = bind(item -> {
            LitRendererBuilder.MobileListItemBuilder.ChipVariant v = variantProvider.apply(item);
            return v != null ? v.toChipClass() : "chip";
        });
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withSecondaryChip(
            ValueProvider<T, String> labelProvider,
            ValueProvider<T, LitRendererBuilder.MobileListItemBuilder.ChipVariant> variantProvider) {
        this.chip2Label = bind(labelProvider);
        this.chip2Class = bind(item -> {
            LitRendererBuilder.MobileListItemBuilder.ChipVariant v = variantProvider.apply(item);
            return v != null ? v.toChipClass() : "chip";
        });
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withMetaRef(ValueProvider<T, String> refProvider) {
        this.metaRef = bind(refProvider);
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withAmount(ValueProvider<T, String> amountProvider) {
        this.amount = bind(amountProvider);
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withStatus(
            ValueProvider<T, String> labelProvider,
            ValueProvider<T, LitRendererBuilder.MobileListItemBuilder.StatusVariant> variantProvider) {
        this.statusLabel = bind(labelProvider);
        this.statusClass = bind(item -> {
            LitRendererBuilder.MobileListItemBuilder.StatusVariant v = variantProvider.apply(item);
            return v != null ? v.toStatusClass() : "mli-status";
        });
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withProperty(String name, ValueProvider<T, ?> provider) {
        properties.put(name, provider);
        return this;
    }

    @Override
    public MobileListItemLitRenderer<T> withFunction(String name, SerializableBiConsumer<T, String> handler) {
        functions.put(name, handler);
        return this;
    }

    // ── Build ─────────────────────────────────────────────────────────────

    @Override
    public LitRenderer<T> build() {
        var sb = new StringBuilder();

        // ── Root element — class is data-bound for variant support ────────
        // If no variant was set, use static class "mli"; otherwise bind it.
        if (rootVariantClass != null) {
            sb.append("<div class=\"${item.").append(rootVariantClass.key()).append("}\"");
        } else {
            sb.append("<div class=\"mli\"");
        }
        if (itemClickFn != null) {
            sb.append(" @click=\"${").append(itemClickFn).append("}\"");
        }
        sb.append(">");

        // ── Top row: number + when ────────────────────────────────────────
        if (number != null || when != null) {
            sb.append("<div class=\"mli-top\">");
            if (number != null) {
                sb.append("<div class=\"mli-num\" title=\"${item.")
                        .append(number.key()).append("}\">${item.")
                        .append(number.key()).append("}</div>");
            }
            if (when != null) {
                sb.append("<div class=\"mli-when\">${item.")
                        .append(when.key()).append("}</div>");
            }
            sb.append("</div>");
        }

        // ── Body: vendor / name ───────────────────────────────────────────
        if (vendor != null) {
            sb.append("<div class=\"mli-vendor\" title=\"${item.")
                    .append(vendor.key()).append("}\">${item.")
                    .append(vendor.key()).append("}</div>");
        }

        // ── Meta row: chip(s) + reference text ────────────────────────────────
        if (chipLabel != null || chip2Label != null || metaRef != null) {
            sb.append("<div class=\"mli-meta\">");
            if (chipLabel != null) {
                sb.append("<span class=\"${item.").append(chipClass.key())
                        .append("}\">${item.").append(chipLabel.key()).append("}</span>");
            }
            if (chip2Label != null) {
                sb.append("<span class=\"${item.").append(chip2Class.key())
                        .append("}\">${item.").append(chip2Label.key()).append("}</span>");
            }
            if (metaRef != null) {
                sb.append("${item.").append(metaRef.key()).append("}");
            }
            sb.append("</div>");
        }

        // ── Bottom row: amount + status ───────────────────────────────────
        if (amount != null || statusLabel != null) {
            sb.append("<div class=\"mli-bot\">");
            if (amount != null) {
                sb.append("<div class=\"mli-amt\">${item.")
                        .append(amount.key()).append("}</div>");
            }
            if (statusLabel != null) {
                sb.append("<span class=\"${item.").append(statusClass.key())
                        .append("}\">${item.").append(statusLabel.key()).append("}</span>");
            }
            sb.append("</div>");
        }

        sb.append("</div>");

        LitRenderer<T> renderer = LitRenderer.of(sb.toString());
        properties.forEach(renderer::withProperty);
        functions.forEach((name, fn) ->
                renderer.withFunction(name, (SerializableConsumer<T>) item -> fn.accept(item, "")));
        return renderer;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private PropertyBinding bind(ValueProvider<T, ?> provider) {
        String key = "p" + propCounter.getAndIncrement();
        properties.put(key, provider);
        return new PropertyBinding(key);
    }

    private record PropertyBinding(String key) {}
}




