package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Objects;

/**
 * Responsive key-value row rendered as a flex row (label | value) inside a
 * {@link KeyValueList} flex-column container.
 *
 * <p>Each item can optionally carry:
 * <ul>
 *   <li><strong>Super text</strong> — small ALL-CAPS tertiary label rendered <em>above</em>
 *       the key (e.g. a category or field type).</li>
 *   <li><strong>Sub text</strong>   — small tertiary label rendered <em>below</em> the value
 *       (e.g. a reference number or running balance).</li>
 * </ul>
 *
 * <p>DOM structure:
 * <pre>
 *   .kv-item  [flex-row, position:relative]
 *     ├── .kv-key-col  [flex-col, 168px fixed]
 *     │     ├── .kv-super   (optional — hidden when empty)
 *     │     └── .kv-key
 *     ├── .kv-sep     (hidden by default; add .kv-show-sep to reveal)
 *     └── .kv-value-col  [flex:1, right-aligned]
 *           ├── .kv-value
 *           └── .kv-sub     (optional — hidden when empty)
 * </pre>
 *
 * <p>Modifier classes (on the root {@code .kv-item}):
 * <ul>
 *   <li>{@code kv-divider}  — thicker border-bottom on this row</li>
 *   <li>{@code kv-wrap}     — allows key and value text to wrap to multiple lines</li>
 *   <li>{@code kv-dense}    — compact padding (9px vs 13px) for this row only</li>
 *   <li>{@code kv-show-sep} — reveals the colon separator</li>
 *   <li>{@code kv-copyable} — reveals .kv-copy-btn on hover</li>
 *   <li>{@code kv-editable} — reveals .kv-edit-btn on hover</li>
 * </ul>
 * <ul>
 *   <li>{@code kv-copyable} — on the inner value div: monospace + user-select:text</li>
 * </ul>
 *
 * @see KeyValueList
 */
@StyleSheet("context://key-value-item.css")
public class KeyValueItem extends Div implements HasTooltip {

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

        keyCol.addClassName("kv-key-col");
        keyCol.add(superText, key);

        // ── Separator ──────────────────────────────────────────────────────
        separator.addClassName("kv-sep");
        separator.getElement().setAttribute("aria-hidden", "true");

        // ── Value column ───────────────────────────────────────────────────
        valueContainer.addClassName("kv-value");

        subText.addClassName("kv-sub");
        subText.setVisible(false); // hidden until setSubText() is called

        valueCol.addClassName("kv-value-col");
        valueCol.add(valueContainer, subText);

        add(keyCol, separator, valueCol);

        // role="listitem" makes this an explicit list item within the KeyValueList (role="list")
        getElement().setAttribute("role", "listitem");
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
     * Returns the value container so callers can content multiple components,
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
     * Show or hide the ":" separator (default: {@code false} — Nexus design has no colon).
     * Uses the {@code kv-show-sep} CSS class on the row element so the CSS-hidden
     * {@code .kv-sep} span can be reliably revealed via a class selector.
     */
    public KeyValueItem setShowSeparator(boolean show) {
        if (show) addClassName("kv-show-sep");
        else removeClassName("kv-show-sep");
        return this;
    }

    public boolean isShowSeparator() {
        return hasClassName("kv-show-sep");
    }

    // ------------- Dense -------------

    /**
     * Applies compact row padding (9px vs 13px) to this item only.
     * To make every row compact, use {@link KeyValueList#asDense()} on the parent list.
     */
    public KeyValueItem setDense(boolean dense) {
        if (dense) addClassName("kv-dense");
        else removeClassName("kv-dense");
        return this;
    }

    public boolean isDense() {
        return hasClassName("kv-dense");
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

    // ------------- Field display mode (standalone) -------------

    /**
     * Switches this item to <em>field display mode</em> when used outside a
     * {@link KeyValueList#asFields()} container — stacked label-above-value,
     * no separator, bold dark label, muted gray value.
     *
     * <p>When items are placed inside a {@link KeyValueList#asFields()} container
     * the {@code kv-list--fields} CSS cascade handles the visual change automatically;
     * calling this method is only needed for standalone items.</p>
     *
     * @param field {@code true} to apply the {@code kv-item--field} modifier
     * @return this item for chaining
     */
    public KeyValueItem setFieldDisplay(boolean field) {
        if (field) addClassName("kv-item--field");
        else removeClassName("kv-item--field");
        return this;
    }

    /**
     * Returns {@code true} if the standalone field-display modifier is active.
     */
    public boolean isFieldDisplay() {
        return hasClassName("kv-item--field");
    }

    // ------------- Category / accent color -------------

    /**
     * Row category — drives the 2px coloured left border on the key column.
     * Maps to the {@code data-cat} attribute read by CSS.
     */
    public enum Category {
        /** Violet accent — financial figures, balances, amounts. */
        FINANCIAL("financial"),
        /** Green accent — active / healthy / resolved status. */
        STATUS("status"),
        /** Amber accent — warnings, pending, maturity. */
        ALERT("alert"),
        /** No accent — identity / assignment rows. */
        IDENTITY("identity");

        private final String dataCat;
        Category(String dataCat) { this.dataCat = dataCat; }
        public String getDataCat() { return dataCat; }
    }

    /**
     * Sets the row category which renders a 2px coloured left border on the key column.
     * Pass {@code null} to remove any category accent.
     */
    public KeyValueItem setCategory(Category category) {
        if (category != null) {
            getElement().setAttribute("data-cat", category.getDataCat());
        } else {
            getElement().removeAttribute("data-cat");
        }
        return this;
    }

    public Category getCategory() {
        String attr = getElement().getAttribute("data-cat");
        if (attr == null) return null;
        for (Category c : Category.values()) {
            if (c.getDataCat().equals(attr)) return c;
        }
        return null;
    }

    // ------------- Value type modifiers -------------

    /**
     * Renders the value in monospace — for IDs, codes, tokens, account numbers.
     * Adds {@code kv-mono} to the value container.
     */
    public KeyValueItem setValueMono(boolean mono) {
        if (mono) valueContainer.addClassName("kv-mono");
        else valueContainer.removeClassName("kv-mono");
        return this;
    }

    public boolean isValueMono() {
        return valueContainer.hasClassName("kv-mono");
    }

    /**
     * Renders the value in monospace bold — for financial figures, counts, metrics.
     * Adds {@code kv-numeric} to the value container.
     */
    public KeyValueItem setValueNumeric(boolean numeric) {
        if (numeric) valueContainer.addClassName("kv-numeric");
        else valueContainer.removeClassName("kv-numeric");
        return this;
    }

    public boolean isValueNumeric() {
        return valueContainer.hasClassName("kv-numeric");
    }

    /**
     * Renders the value in low-contrast italic — for empty / placeholder / "not provided" text.
     * Adds {@code kv-muted} to the value container.
     */
    public KeyValueItem setValueMuted(boolean muted) {
        if (muted) valueContainer.addClassName("kv-muted");
        else valueContainer.removeClassName("kv-muted");
        return this;
    }

    public boolean isValueMuted() {
        return valueContainer.hasClassName("kv-muted");
    }

    /**
     * Renders the value as left-aligned multi-line text — for notes, descriptions,
     * addresses. Adds {@code kv-text-wrap} to the value container.
     */
    public KeyValueItem setValueTextWrap(boolean wrap) {
        if (wrap) valueContainer.addClassName("kv-text-wrap");
        else valueContainer.removeClassName("kv-text-wrap");
        return this;
    }

    public boolean isValueTextWrap() {
        return valueContainer.hasClassName("kv-text-wrap");
    }

    // ------------- Copy affordance -------------

    private NativeButton copyButton;

    /**
     * Adds a copy icon button inside the value cell. The button is hidden at rest
     * and revealed on row hover. Attach behaviour via {@code getCopyButton().addClickListener(...)}.
     */
    public KeyValueItem setShowCopyButton(boolean show) {
        if (show) {
            addClassName("kv-copyable");
            if (copyButton == null) {
                copyButton = new NativeButton();
                copyButton.addClassName("kv-copy-btn");
                copyButton.getElement().setAttribute("aria-label",
                        LocalizationProvider.localize("Copy", "key_value.copy_aria"));
                copyButton.getElement().setProperty("innerHTML",
                    "<svg width='12' height='12' viewBox='0 0 24 24' fill='none'" +
                    " stroke='currentColor' stroke-width='2.2'>" +
                    "<rect x='9' y='9' width='11' height='11' rx='1.5'/>" +
                    "<path d='M5 15V5a2 2 0 0 1 2-2h10'/></svg>");
                valueContainer.add(copyButton);
            }
            copyButton.setVisible(true);
        } else {
            removeClassName("kv-copyable");
            if (copyButton != null) copyButton.setVisible(false);
        }
        return this;
    }

    public boolean isShowCopyButton() {
        return copyButton != null && copyButton.isVisible();
    }

    /** Returns the copy button so callers can attach click listeners. */
    public NativeButton getCopyButton() { return copyButton; }

    // ------------- Edit affordance -------------

    private NativeButton editButton;

    /**
     * Adds an edit (pencil) icon button inside the value cell. The button is hidden
     * at rest and revealed on row hover. Attach behaviour via {@code getEditButton().addClickListener(...)}.
     */
    public KeyValueItem setEditable(boolean editable) {
        if (editable) {
            addClassName("kv-editable");
            if (editButton == null) {
                editButton = new NativeButton();
                editButton.addClassName("kv-edit-btn");
                editButton.getElement().setAttribute("aria-label",
                        LocalizationProvider.localize("Edit", "key_value.edit_aria"));
                editButton.getElement().setProperty("innerHTML",
                    "<svg width='13' height='13' viewBox='0 0 24 24' fill='none'" +
                    " stroke='currentColor' stroke-width='2.2'>" +
                    "<path d='M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z'/></svg>");
                valueContainer.add(editButton);
            }
            editButton.setVisible(true);
        } else {
            removeClassName("kv-editable");
            if (editButton != null) editButton.setVisible(false);
        }
        return this;
    }

    public boolean isEditable() {
        return editButton != null && editButton.isVisible();
    }

    /** Returns the edit button so callers can attach click listeners. */
    public NativeButton getEditButton() { return editButton; }

    // ------------- Status pill variant -------------

    /**
     * Visual variant for {@link #pill(String, String, PillVariant)} factory items.
     * Maps to the {@code .kv-pill.success/warning/danger/neutral} CSS classes.
     */
    public enum PillVariant {
        SUCCESS("success"),
        WARNING("warning"),
        DANGER("danger"),
        NEUTRAL("neutral");

        private final String cssClass;
        PillVariant(String cssClass) { this.cssClass = cssClass; }
        public String getCssClass() { return cssClass; }
    }

    // ------------- Delta direction enum -------------

    /**
     * Direction of a trend annotation for {@link #withDelta(String, String, String, DeltaDirection)}.
     */
    public enum DeltaDirection { UP, DOWN }

    // ------------- Factory: status pill value -------------

    /**
     * Creates a {@link KeyValueItem} whose value is a coloured status pill.
     *
     * <pre>{@code
     * KeyValueItem.pill("Account status", "Active",         PillVariant.SUCCESS)
     * KeyValueItem.pill("Compliance",     "Review pending", PillVariant.WARNING)
     * KeyValueItem.pill("Error",          "3 errors",       PillVariant.DANGER)
     * KeyValueItem.pill("Role",           "Unassigned",     PillVariant.NEUTRAL)
     * }</pre>
     */
    public static KeyValueItem pill(String key, String label, PillVariant variant) {
        Span pill = new Span(label);
        pill.addClassNames("kv-pill", variant.getCssClass());
        return new KeyValueItem(key, pill);
    }

    // ------------- Factory: identity (avatar + name) value -------------

    /**
     * Creates a {@link KeyValueItem} whose value is a circular avatar with initials
     * paired with a display name.
     *
     * <pre>{@code
     * KeyValueItem.identity("Assigned to", "JC", "Jordan Cole")
     * }</pre>
     */
    public static KeyValueItem identity(String key, String initials, String name) {
        Span avatar = new Span(initials);
        avatar.addClassName("kv-avatar");

        Span nameSpan = new Span(name);

        Div wrapper = new Div();
        wrapper.addClassName("kv-identity");
        wrapper.add(avatar, nameSpan);

        return new KeyValueItem(key, wrapper);
    }

    // ------------- Factory: numeric value with delta annotation -------------

    /**
     * Creates a {@link KeyValueItem} with a monospace-bold numeric value followed
     * by a coloured trend annotation.
     *
     * <pre>{@code
     * KeyValueItem.withDelta("Monthly revenue", "$84,120", "+4.6%",  DeltaDirection.UP)
     * KeyValueItem.withDelta("Churn rate",      "1.8%",   "−0.3pt", DeltaDirection.DOWN)
     * }</pre>
     */
    public static KeyValueItem withDelta(String key, String value, String delta, DeltaDirection direction) {
        Span valueSpan = new Span(value);

        Span deltaSpan = new Span(delta);
        deltaSpan.addClassName("kv-delta");
        deltaSpan.addClassName(direction == DeltaDirection.UP ? "up" : "down");

        Div wrapper = new Div();
        wrapper.add(valueSpan, deltaSpan);

        KeyValueItem item = new KeyValueItem(key, wrapper);
        item.setValueNumeric(true);
        return item;
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

    /**
     * Creates a {@link KeyValueItem} pre-configured for standalone
     * <em>field display mode</em> — bold dark label, muted gray value, no separator.
     *
     * <p>Shorthand for {@code KeyValueItem.of(key, value).setFieldDisplay(true)}.
     * Not needed when the item is placed inside a {@link KeyValueList#asFields()}
     * container (the CSS cascade handles it automatically).</p>
     *
     * <pre>{@code
     * // Standalone usage
     * var item = KeyValueItem.field("Service", "UPS Next Day Air Saver®");
     * add(item);
     * }</pre>
     *
     * @param key   label text
     * @param value value text
     * @return new item in field display mode
     */
    public static KeyValueItem field(String key, String value) {
        return new KeyValueItem(key, value).setFieldDisplay(true);
    }

    /**
     * Creates a {@link KeyValueItem} in standalone field display mode with a component value.
     *
     * @param key   label text
     * @param value value component
     * @return new item in field display mode
     */
    public static KeyValueItem field(String key, Component value) {
        return new KeyValueItem(key, value).setFieldDisplay(true);
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
        private boolean   showColon    = false;  // Nexus design: no colon by default
        private boolean   required     = false;
        private boolean   copyable     = false;
        private boolean   divider      = false;
        private boolean   wrap         = false;
        private boolean   clickable    = false;
        private boolean   fieldDisplay = false;
        private String    tooltip;
        private Category  category;
        private boolean   mono         = false;
        private boolean   numeric      = false;
        private boolean   muted        = false;
        private boolean   textWrap     = false;
        private boolean   showCopyButton = false;
        private boolean   editable     = false;
        private boolean   dense        = false;

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

        /** Sets the row category — drives the 2px coloured left border on the key column. */
        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        /** Monospace font on the value — for IDs, codes, tokens. */
        public Builder mono(boolean mono) {
            this.mono = mono;
            return this;
        }

        /** Monospace bold on the value — for financial figures and counts. */
        public Builder numeric(boolean numeric) {
            this.numeric = numeric;
            return this;
        }

        /** Low-contrast italic on the value — for empty / placeholder text. */
        public Builder muted(boolean muted) {
            this.muted = muted;
            return this;
        }

        /** Left-aligned wrapping value — for notes and descriptions. */
        public Builder textWrap(boolean textWrap) {
            this.textWrap = textWrap;
            return this;
        }

        /** Reveals a copy icon button on row hover. */
        public Builder showCopyButton(boolean showCopyButton) {
            this.showCopyButton = showCopyButton;
            return this;
        }

        /** Reveals an edit icon button on row hover. */
        public Builder editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        /** Compact row padding for this item only (9px vs 13px). */
        public Builder dense(boolean dense) {
            this.dense = dense;
            return this;
        }

        /**
         * Applies standalone field display mode (bold label above muted value, no separator).
         * Not needed when the item is inside a {@link KeyValueList#asFields()} container.
         */
        public Builder fieldDisplay(boolean fieldDisplay) {
            this.fieldDisplay = fieldDisplay;
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
            item.setValueWrap(wrap);
            item.setClickable(clickable);
            item.setFieldDisplay(fieldDisplay);
            if (tooltip   != null) item.setTooltipText(tooltip);
            if (category  != null) item.setCategory(category);
            item.setValueMono(mono);
            item.setValueNumeric(numeric);
            item.setValueMuted(muted);
            item.setValueTextWrap(textWrap);
            item.setShowCopyButton(showCopyButton);
            item.setEditable(editable);
            item.setDense(dense);
            return item;
        }
    }
}

