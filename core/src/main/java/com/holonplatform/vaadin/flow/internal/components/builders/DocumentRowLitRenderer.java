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
 * Implementation of {@link LitRendererBuilder.DocumentRowBuilder}.
 *
 * <p>Produces a 3-row document/transaction row renderer using the CSS classes
 * defined in {@code document-row-lit-renderer.css}:
 *
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
 * <p>All CSS class names are hardcoded internally — callers only pass
 * {@link ValueProvider} lambdas and {@link LitRendererBuilder.DocumentRowBuilder.StatusType}
 * enum values. No CSS strings appear in calling code.
 *
 * @param <T> the grid item type
 * @since 10.0.0
 */
public class DocumentRowLitRenderer<T> implements LitRendererBuilder.DocumentRowBuilder<T> {

    // ── Slot bindings ─────────────────────────────────────────────────────

    private PropertyBinding reference;    // row1 left  — li-po
    private PropertyBinding amount;       // row1 right — li-amt
    private PropertyBinding title;        // row2       — li-row2
    private PropertyBinding statusLabel;  // row3 left  — text inside status badge
    private PropertyBinding statusClass;  // row3 left  — full CSS class e.g. "status st-pending"
    private PropertyBinding meta;         // row3 right — li-meta

    // ── Click ─────────────────────────────────────────────────────────────

    private String itemClickFn;

    // ── Property / function bindings ─────────────────────────────────────

    private final Map<String, ValueProvider<T, ?>> properties = new LinkedHashMap<>();
    private final Map<String, SerializableBiConsumer<T, String>> functions = new LinkedHashMap<>();
    private final AtomicInteger propCounter = new AtomicInteger(0);

    // ── Builder methods ───────────────────────────────────────────────────

    @Override
    public DocumentRowLitRenderer<T> withReference(ValueProvider<T, String> refProvider) {
        this.reference = bind(refProvider);
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> withAmount(ValueProvider<T, String> amountProvider) {
        this.amount = bind(amountProvider);
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> withTitle(ValueProvider<T, String> titleProvider) {
        this.title = bind(titleProvider);
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> withStatus(
            ValueProvider<T, String> labelProvider,
            ValueProvider<T, LitRendererBuilder.DocumentRowBuilder.StatusType> typeProvider) {
        this.statusLabel = bind(labelProvider);
        // Compute the full CSS class string server-side from the enum value
        this.statusClass = bind(item -> {
            LitRendererBuilder.DocumentRowBuilder.StatusType type = typeProvider.apply(item);
            return type != null ? type.toCssClass() : "status";
        });
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> withMeta(ValueProvider<T, String> metaProvider) {
        this.meta = bind(metaProvider);
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> onItemClick(String functionName) {
        this.itemClickFn = functionName;
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> withProperty(String name, ValueProvider<T, ?> provider) {
        properties.put(name, provider);
        return this;
    }

    @Override
    public DocumentRowLitRenderer<T> withFunction(String name, SerializableBiConsumer<T, String> handler) {
        functions.put(name, handler);
        return this;
    }

    // ── Build ─────────────────────────────────────────────────────────────

    @Override
    public LitRenderer<T> build() {
        var sb = new StringBuilder();

        // Root wrapper — optionally clickable
        sb.append("<div class=\"list-item\"");
        if (itemClickFn != null) {
            sb.append(" @click=\"${").append(itemClickFn).append("}\"");
        }
        sb.append(">");

        // ── Row 1: reference (left) + amount (right) ─────────────────────
        if (reference != null || amount != null) {
            sb.append("<div class=\"li-row1\">");
            if (reference != null) {
                sb.append("<span class=\"li-po\" title=\"${item.")
                        .append(reference.key()).append("}\">${item.")
                        .append(reference.key()).append("}</span>");
            }
            if (amount != null) {
                sb.append("<span class=\"li-amt\" title=\"${item.")
                        .append(amount.key()).append("}\">${item.")
                        .append(amount.key()).append("}</span>");
            }
            sb.append("</div>");
        }

        // ── Row 2: title / company name ──────────────────────────────────
        if (title != null) {
            sb.append("<div class=\"li-row2\" title=\"${item.")
                    .append(title.key()).append("}\">${item.")
                    .append(title.key()).append("}</div>");
        }

        // ── Row 3: status badge (left) + meta (right) ────────────────────
        if (statusLabel != null || meta != null) {
            sb.append("<div class=\"li-row3\">");
            if (statusLabel != null) {
                // statusClass property holds the full "status st-xxx" string
                sb.append("<span class=\"${item.").append(statusClass.key())
                        .append("}\">${item.").append(statusLabel.key()).append("}</span>");
            }
            if (meta != null) {
                sb.append("<span class=\"li-meta\">${item.")
                        .append(meta.key()).append("}</span>");
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

