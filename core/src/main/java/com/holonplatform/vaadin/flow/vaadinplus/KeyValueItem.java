package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Objects;

/**
 * Responsive key-value row that flattens into the parent {@link KeyValueList}
 * three-column CSS grid (<em>key-col | sep | value-col</em>) via {@code display:contents}.
 *
 * <p>Each item can optionally carry:
 * <ul>
 *   <li><strong>Super text</strong> — small tertiary label rendered <em>above</em> the key
 *       (e.g. a category or field name in a bank statement).</li>
 *   <li><strong>Sub text</strong>   — small tertiary label rendered <em>below</em> the value
 *       (e.g. a reference number or running balance).</li>
 * </ul>
 *
 * <p>DOM structure (inside each row):
 * <pre>
 *   .kv-item  [display:contents — 3 direct grid children]
 *     ├── .kv-key-col  [flex-col]
 *     │     ├── .kv-super   (optional — hidden when empty)
 *     │     └── .kv-key
 *     ├── .kv-sep
 *     └── .kv-value-col  [flex-col]
 *           ├── .kv-value
 *           └── .kv-sub     (optional — hidden when empty)
 * </pre>
 *
 * <p>Modifier classes (on the root {@code .kv-item}):
 * <ul>
 *   <li>{@code kv-divider}   — full-width ::after divider line spanning all 3 columns</li>
 *   <li>{@code kv-value-end} — right-aligns the value column on mobile screens only</li>
 *   <li>{@code kv-wrap}      — allows key and value text to wrap to multiple lines</li>
 * </ul>
 * <ul>
 *   <li>{@code kv-copyable}  — on the inner value div: monospace + user-select:text</li>
 * </ul>
 *
 * @see KeyValueList
 */
@StyleSheet("context://key-value-item.css")
public class KeyValueItem extends Div implements HasStyle, HasSize, HasTooltip {

    // Key column (grid child 1)
    private final Div    keyCol    = Components.div().build();
    private final Span   superText = Components.span().build();
    private final Span   key       = Components.span().build();

    // Separator (grid child 2)
    private final Span separator = Components.span().text(":").build();

    // Value column (grid child 3)
    private final Div  valueCol      = Components.div().build();
    private final Div  valueContainer = Components.div().build();
    private final Span subText        = Components.span().build();

    // ------------- Constructors -------------

    public KeyValueItem() {
        initLayout();
    }

    public KeyValueItem(String keyText, String valueText) {
        initLayout();
        setKey(keyText);
        setValue(valueText);
    }

    public KeyValueItem(String keyText, Component value) {
        initLayout();
        setKey(keyText);
        setValue(value);
    }

    // ------------- Initialization -------------

    private void initLayout() {
        addClassName("kv-item"); // display:contents — this IS the grid child

        // ── Key column ─────────────────────────────────────────────────────
        superText.addClassName("kv-super");
        superText.setVisible(false); // hidden until setSuperText() is called

        key.addClassName("kv-key");
        key.addClassName(Font.Weight.SEMIBOLD.getClassName());
        key.addClassName(Color.Text.SECONDARY.getClassName());

        keyCol.addClassName("kv-key-col");
        keyCol.add(superText, key);

        // ── Separator ──────────────────────────────────────────────────────
        separator.addClassName("kv-sep");
        separator.getElement().setAttribute("aria-hidden", "true");

        // ── Value column ───────────────────────────────────────────────────
        valueContainer.addClassName("kv-value");
        valueContainer.addClassName(Color.Text.BODY.getClassName());

        subText.addClassName("kv-sub");
        subText.setVisible(false); // hidden until setSubText() is called

        valueCol.addClassName("kv-value-col");
        valueCol.add(valueContainer, subText);

        add(keyCol, separator, valueCol);

        getElement().setAttribute("role", "group");
    }

    // ------------- Key API -------------

    public KeyValueItem setKey(String text) {
        key.setText(Objects.requireNonNullElse(text, ""));
        return this;
    }

    /**
     * Sets the key label from a {@link Localizable} descriptor.
     *
     * @param text localizable key label (not null)
     * @return this item for chaining
     */
    public KeyValueItem setKey(Localizable text) {
        return setKey(resolve(text));
    }

    public String getKey() {
        return key.getText();
    }

    public Span getKeyComponent() {
        return key;
    }

    // ------------- Super text (above key) -------------

    /**
     * Sets small tertiary text displayed <em>above</em> the key label.
     * Typical use: category, field type, or account label in a bank statement.
     * Passing {@code null} or blank hides the element.
     */
    public KeyValueItem setSuperText(String text) {
        superText.setText(Objects.requireNonNullElse(text, ""));
        superText.setVisible(text != null && !text.isBlank());
        return this;
    }

    /**
     * Sets the super text (above key) from a {@link Localizable} descriptor.
     *
     * @param text localizable super text (not null)
     * @return this item for chaining
     */
    public KeyValueItem setSuperText(Localizable text) {
        return setSuperText(resolve(text));
    }

    public String getSuperText() {
        return superText.getText();
    }

    // ------------- Value API -------------

    public KeyValueItem setValue(String text) {
        boolean empty = text == null || text.isBlank();
        valueContainer.getElement().setAttribute("data-empty", String.valueOf(empty));
        return setValue(Components.span().text(Objects.requireNonNullElse(text, "")).build());
    }

    /**
     * Sets the value text from a {@link Localizable} descriptor.
     *
     * @param text localizable value text (not null)
     * @return this item for chaining
     */
    public KeyValueItem setValue(Localizable text) {
        return setValue(resolve(text));
    }

    public KeyValueItem setValue(Component component) {
        valueContainer.removeAll();
        if (component != null) {
            valueContainer.add(component);
            valueContainer.getElement().setAttribute("data-empty", "false");
        } else {
            valueContainer.getElement().setAttribute("data-empty", "true");
        }
        return this;
    }

    /**
     * Returns the value container so callers can add multiple components,
     * badges, etc., without replacing the container itself.
     */
    public Div getValueContainer() {
        return valueContainer;
    }

    // ------------- Sub text (below value) -------------

    /**
     * Sets small tertiary text displayed <em>below</em> the value.
     * Typical use: reference number, running balance, or metadata in a bank statement.
     * Passing {@code null} or blank hides the element.
     */
    public KeyValueItem setSubText(String text) {
        subText.setText(Objects.requireNonNullElse(text, ""));
        subText.setVisible(text != null && !text.isBlank());
        return this;
    }

    /**
     * Sets the sub text (below value) from a {@link Localizable} descriptor.
     *
     * @param text localizable sub text (not null)
     * @return this item for chaining
     */
    public KeyValueItem setSubText(Localizable text) {
        return setSubText(resolve(text));
    }

    public String getSubText() {
        return subText.getText();
    }

    // ------------- Separator -------------

    /**
     * Show or hide the ":" separator (default: {@code true}).
     */
    public KeyValueItem setShowSeparator(boolean show) {
        separator.setVisible(show);
        return this;
    }

    public boolean isShowSeparator() {
        return separator.isVisible();
    }

    // ------------- UX helpers -------------

    /**
     * Marks the key with {@code data-required="true"} to trigger the
     * CSS asterisk indicator. Purely visual — validation belongs in the form.
     */
    public KeyValueItem setRequiredIndicatorVisible(boolean required) {
        key.getElement().setAttribute("data-required", String.valueOf(required));
        return this;
    }

    /**
     * Shows or hides a full-width divider line below this row (default: {@code false}).
     * Implemented via {@code ::after} spanning {@code grid-column: 1 / -1} — no extra DOM node.
     */
    public KeyValueItem setDividerVisible(boolean visible) {
        if (visible) addClassName("kv-divider");
        else removeClassName("kv-divider");
        return this;
    }

    public boolean isDividerVisible() {
        return hasClassName("kv-divider");
    }

    /**
     * Right-aligns the value column on mobile screens only (CSS {@code kv-value-end} modifier).
     * On desktop the 3-column grid layout is unchanged.
     * Useful for bank-statement style rows where amounts should sit at the trailing edge on narrow screens.
     */
    public KeyValueItem setValueEnd(boolean end) {
        if (end) addClassName("kv-value-end");
        else removeClassName("kv-value-end");
        return this;
    }

    /**
     * Allows key and value text to wrap to multiple lines (default: nowrap via CSS).
     * Applies the {@code kv-wrap} modifier class.
     */
    public KeyValueItem setValueWrap(boolean wrap) {
        if (wrap) addClassName("kv-wrap");
        else removeClassName("kv-wrap");
        return this;
    }

    /**
     * Adds {@code kv-copyable} on the value container: monospace font + user-select:text.
     */
    public KeyValueItem setValueCopyable(boolean copyable) {
        if (copyable) valueContainer.addClassName("kv-copyable");
        else valueContainer.removeClassName("kv-copyable");
        return this;
    }

    /**
     * Marks this row as interactive (default: {@code false}).
     *
     * <p>Adds the {@code kv-clickable} modifier class which applies:
     * <ul>
     *   <li>pointer cursor on all three grid cells</li>
     *   <li>subtle background highlight on hover / active</li>
     * </ul>
     *
     * <p>Wire navigation via the inherited {@code addClickListener()}:
     * <pre>{@code
     * item.setClickable(true);
     * item.addClickListener(e -> UI.getCurrent().navigate(DetailView.class));
     *
     * // With Holon Navigator + query parameter:
     * item.addClickListener(e ->
     *     Navigator.get().navigation(DetailView.class)
     *         .withQueryParameter("id", entity.getId())
     *         .navigate());
     * }</pre>
     */
    public KeyValueItem setClickable(boolean clickable) {
        if (clickable) addClassName("kv-clickable");
        else removeClassName("kv-clickable");
        return this;
    }

    // ------------- Localizable helper -------------

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    // ------------- Factory methods -------------

    public static KeyValueItem of(String key, String value) {
        return new KeyValueItem(key, value);
    }

    public static KeyValueItem of(String key, Component value) {
        return new KeyValueItem(key, value);
    }

    /**
     * Creates a {@link KeyValueItem} with localizable key and value.
     *
     * @param key   localizable key label (not null)
     * @param value localizable value text (not null)
     * @return new item
     */
    public static KeyValueItem of(Localizable key, Localizable value) {
        return new KeyValueItem(resolve(key), resolve(value));
    }

    /**
     * Creates a {@link KeyValueItem} with a localizable key and a component value.
     *
     * @param key   localizable key label (not null)
     * @param value value component
     * @return new item
     */
    public static KeyValueItem of(Localizable key, Component value) {
        KeyValueItem item = new KeyValueItem();
        item.setKey(key);
        item.setValue(value);
        return item;
    }

    // ------------- Fluent builder -------------

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String    key;
        private Component value;
        private String    superText;
        private String    subText;
        private boolean   showColon = true;
        private boolean   required  = false;
        private boolean   copyable  = false;
        private boolean   divider   = false;
        private boolean   valueEnd  = false;
        private boolean   wrap      = false;
        private boolean   clickable = false;
        private String    tooltip;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the key label from a {@link Localizable} descriptor.
         *
         * @param key localizable key label (not null)
         * @return this builder
         */
        public Builder key(Localizable key) {
            this.key = resolve(key);
            return this;
        }

        public Builder value(String value) {
            this.value = Components.span().text(Objects.requireNonNullElse(value, "")).build();
            return this;
        }

        /**
         * Sets the value text from a {@link Localizable} descriptor.
         *
         * @param value localizable value text (not null)
         * @return this builder
         */
        public Builder value(Localizable value) {
            this.value = Components.span().text(resolve(value)).build();
            return this;
        }

        public Builder value(Component value) {
            this.value = value;
            return this;
        }

        /** Small tertiary label rendered above the key. */
        public Builder superText(String superText) {
            this.superText = superText;
            return this;
        }

        /**
         * Small tertiary label rendered above the key, from a {@link Localizable} descriptor.
         *
         * @param superText localizable super text (not null)
         * @return this builder
         */
        public Builder superText(Localizable superText) {
            this.superText = resolve(superText);
            return this;
        }

        /** Small tertiary label rendered below the value. */
        public Builder subText(String subText) {
            this.subText = subText;
            return this;
        }

        /**
         * Small tertiary label rendered below the value, from a {@link Localizable} descriptor.
         *
         * @param subText localizable sub text (not null)
         * @return this builder
         */
        public Builder subText(Localizable subText) {
            this.subText = resolve(subText);
            return this;
        }

        // ...existing code...

        public Builder showColon(boolean showColon) {
            this.showColon = showColon;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder copyable(boolean copyable) {
            this.copyable = copyable;
            return this;
        }

        public Builder divider(boolean divider) {
            this.divider = divider;
            return this;
        }

        /** Right-aligns the value column on mobile only. */
        public Builder valueEnd(boolean valueEnd) {
            this.valueEnd = valueEnd;
            return this;
        }

        /** Allows key and value text to wrap. */
        public Builder wrap(boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        /** Adds pointer cursor and hover highlight; wire navigation via addClickListener(). */
        public Builder clickable(boolean clickable) {
            this.clickable = clickable;
            return this;
        }

        public Builder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Sets the tooltip text from a {@link Localizable} descriptor.
         *
         * @param tooltip localizable tooltip text (not null)
         * @return this builder
         */
        public Builder tooltip(Localizable tooltip) {
            this.tooltip = resolve(tooltip);
            return this;
        }

        public KeyValueItem build() {
            Objects.requireNonNull(key, "Key must not be null");
            KeyValueItem item = new KeyValueItem();
            item.setKey(key);
            item.setValue(value);
            if (superText != null) item.setSuperText(superText);
            if (subText   != null) item.setSubText(subText);
            item.setShowSeparator(showColon);
            item.setRequiredIndicatorVisible(required);
            item.setValueCopyable(copyable);
            item.setDividerVisible(divider);
            item.setValueEnd(valueEnd);
            item.setValueWrap(wrap);
            item.setClickable(clickable);
            if (tooltip != null) item.setTooltipText(tooltip);
            return item;
        }
    }
}

