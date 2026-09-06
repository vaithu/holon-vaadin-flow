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
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.ItemLineEditorBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A generic, domain-agnostic <strong>editable line-item list</strong>: a header with a live
 * item-count badge, a spreadsheet-style editable {@link Grid} (one row per item of an arbitrary
 * bean type {@code T}, one column per {@link Column} added via {@link #addColumn(Column)}), an
 * "Add item" action, per-row removal, an optional empty-state placeholder, and an optional
 * computed {@link TotalsCard} footer.
 *
 * <h3>Why this shape</h3>
 * <p>This mirrors the line-item editing pattern used by most invoicing / order / cart UIs
 * (e.g. QuickBooks and Xero invoice lines, Odoo's {@code one2many} editable list widget,
 * Shopify draft-order line items, spreadsheet-style grids such as AG Grid / Handsontable):
 * a set of always-editable rows, an explicit "add row" action that appends a blank item, an
 * inline per-row delete action, and a running total recomputed from the current rows. Rather
 * than hard-coding those fields (as a single invoice- or appraisal-specific component would),
 * this component is parameterized over the row bean type {@code T} and the column set, so the
 * very same component backs invoices, quotations, shopping carts, pawn-ticket appraisals, purchase
 * orders, etc. — only the {@link Column} definitions, the row factory and the totals function
 * differ per domain.</p>
 *
 * <h3>Usage</h3>
 * <p>Preferred usage is via the fluent {@link #builder(Class)} / {@link ItemLineEditorBuilder}
 * (or the equivalent {@link Components#itemLineEditor(Class)} factory), consistently with every
 * other Holon Platform component:
 * <pre>{@code
 * ItemLineEditor<InvoiceLine> editor = ItemLineEditor.builder(InvoiceLine.class)
 *     .title("Invoice Items")
 *     .rowFactory(InvoiceLine::new)
 *     .addColumn(ItemLineEditor.Column.of("description", "Description", line -> descriptionField(line))
 *         .flexGrow(2))
 *     .addColumn(ItemLineEditor.Column.of("qty", "Qty", line -> qtyField(line)).width("90px"))
 *     .emptyState("No items added yet", "Add a line item to start the invoice.")
 *     .footer(lines -> TotalsCard.builder()
 *         .row("Total", format(total(lines)), TotalsRow.Variant.GRAND_TOTAL)
 *         .build())
 *     .onChange(lines -> save(lines))
 *     .build();
 * }</pre>
 *
 * <p>Each {@link Column} renderer receives the row instance and returns the editable component
 * for that cell (e.g. a bound {@code TextField}/{@code NumberField}/{@code ComboBox}, a
 * {@link StatusBadge}, or any other {@link Component}) — the caller is fully responsible for
 * reading/writing the bean's properties and calling {@link #notifyRowChanged(Object)} as needed,
 * exactly as with {@link LineItemGrid}.</p>
 *
 * @param <T> the row/item bean type
 * @since 10.0.0
 */
@StyleSheet("context://item-line-editor.css")
public class ItemLineEditor<T> extends Composite<Div> implements HasComponent, HasSize, HasStyle, HasEnabled {

    /** Default cap on the number of rows, to keep server-side component counts bounded. */
    public static final int DEFAULT_MAX_ROWS = 200;

    // ── Column definition ─────────────────────────────────────────────────────

    /**
     * A single editable/rendered column of an {@link ItemLineEditor}.
     *
     * <p>Created via {@link #of(String, String, Function)} and further configured fluently
     * ({@link #width(String)}, {@link #detail()}) before being passed
     * to {@link #addColumn(Column)}.
     *
     * @param <T> the row bean type
     */
    public static final class Column<T> {

        private final String key;
        private final String header;
        private final Function<T, Component> renderer;
        private String width;
        private int flexGrow = 1;
        private boolean detail;
        private final List<Validator<T>> validators = new ArrayList<>();

        private Column(String key, String header, Function<T, Component> renderer) {
            this.key = key;
            this.header = header;
            this.renderer = renderer;
        }

        /**
         * Creates a new column.
         *
         * @param key      unique column key
         * @param header   column header text
         * @param renderer receives the row instance and returns the component to render/edit
         *                 that cell (a bound text field, combo box, {@link StatusBadge}, plain
         *                 span, etc.)
         * @param <T> the row bean type
         * @return a new, further-configurable {@link Column}
         */
        public static <T> Column<T> of(String key, String header, Function<T, Component> renderer) {
            return new Column<>(key, header, renderer);
        }

        /** Sets a fixed column width (e.g. {@code "120px"}); disables flex-grow for this column. */
        public Column<T> width(String width) {
            this.width = width;
            this.flexGrow = 0;
            return this;
        }

        /** Sets the column's flex-grow ratio (default {@code 1}). */
        public Column<T> flexGrow(int flexGrow) {
            this.flexGrow = flexGrow;
            return this;
        }

        /**
         * Marks this column as part of the optional "details" group: hidden by default and
         * revealed only when the user toggles "Show details".
         * Only meaningful when at least one column is marked as detail.
         */
        public Column<T> detail() {
            this.detail = true;
            return this;
        }

        /**
         * Adds a Holon {@link Validator} evaluated against the <strong>whole row bean</strong> {@code T}
         * every time the user edits this column's cell (see {@link ItemLineEditor#attachAutoNotify}).
         * When {@link Validator#validate(Object)} throws a {@link Validator.ValidationException}, its
         * message is shown on the rendered cell via {@link com.vaadin.flow.component.HasValidation}
         * (i.e. {@code setInvalid(true)} / {@code setErrorMessage(...)}) — supported out of the box by
         * every standard Vaadin field and every Holon {@code Input}-backed component. Multiple
         * validators may be added; they are evaluated in order and the first failure wins.
         */
        public Column<T> validator(Validator<T> validator) {
            ObjectUtils.argumentNotNull(validator, "Validator must be not null");
            this.validators.add(validator);
            return this;
        }

        /**
         * Convenience shortcut for {@link #validator(Validator)}: adds a "required" style validator
         * that fails (showing {@code message} on the cell) whenever {@code present} returns
         * {@code false} for the current row.
         */
        public Column<T> required(Predicate<T> present, String message) {
            return validator(Validator.create(present, message));
        }
    }

    // ── Internal state ────────────────────────────────────────────────────────

    private final Class<T> itemType;
    private final List<T> rows = new ArrayList<>();
    private final List<Column<T>> columns = new ArrayList<>();
    private final Map<String, Grid.Column<T>> gridColumnsByKey = new LinkedHashMap<>();

    /**
     * Rows added via {@link #addRow()} / {@link #addRows(int)} that have not yet been edited by
     * the user, tracked (by identity) so the grid row and mobile card can be rendered with a
     * "just added" highlight ({@code ile-row-new} / {@code ile-card-new}) until the row is edited
     * (see {@link #notifyRowChanged(Object)}), removed, or the row list is replaced wholesale via
     * {@link #setRows(List)}.
     */
    private final Set<T> newlyAddedRows = Collections.newSetFromMap(new IdentityHashMap<>());

    private Supplier<T> rowFactory;
    private int maxRows = DEFAULT_MAX_ROWS;
    private Function<List<T>, TotalsCard> footerFunction;
    private Consumer<List<T>> changeListener;

    private final Grid<T> grid;
    private final Div gridWrapper;
    private final Empty emptyState;
    private final Span titleSpan;
    private final Span countBadge;
    private final Button addBtn;
    private final Button emptyAddBtn;
    private final Div toolbarActions;
    private Div footerWrapper;
    private Button toggleDetailsBtn;
    private boolean detailsVisible = false;
    private Grid.Column<T> deleteColumn;

    private GridListDataView<T> dataView;

    // ── Responsive mobile card + edit Sheet (opt-in, generic) ────────────────
    private Function<T, String> mobileCardTitle;
    private Function<T, String> mobileCardSubtitle;
    private Function<T, String> mobileCardValue;
    private Div cardsContainer;

    // ── Responsive mobile FormLayout fallback (always on, used when no
    // mobileCard(...) card/sheet view has been configured) ───────────────────
    private final Div mobileFormsContainer;
    private final Map<T, Div> mobileFormsByRow = new IdentityHashMap<>();

    /**
     * Whether the client has ever reported being below the {@code 640px} mobile breakpoint (see
     * {@code item-line-editor.css}). Both {@link #mobileFormsContainer} and (if configured)
     * {@link #cardsContainer} are left empty and skip their per-row rebuild in {@link #refreshAll()}
     * until this flips {@code true} — set once, lazily, by {@link #installMobileActivationListener()}
     * — so a desktop-only session never pays the cost of rendering a duplicate, hidden mobile
     * representation of every row (grid cell components are otherwise mirrored 1:1 into a second,
     * always-built component tree, roughly doubling server-side component/listener count per row).
     */
    private boolean mobileEverActive = false;

    // ── Bulk "Add rows" toolbar stepper (opt-in, generic) ────────────────────
    private IntegerField bulkAddCountField;

    // ── Generic catalog/bulk-content picker dialog (opt-in) ──────────────────
    private String pickerButtonText = "Pick Items";
    @SuppressWarnings("rawtypes")
    private List pickerCatalog;
    @SuppressWarnings("rawtypes")
    private Function pickerPrimaryLabel;
    @SuppressWarnings("rawtypes")
    private Function pickerSecondaryLabel;
    @SuppressWarnings("rawtypes")
    private BiFunction pickerRowMapper;
    private Button pickerBtn;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a new, empty {@link ItemLineEditor} for the given row bean type. Prefer
     * {@link #builder(Class)} (or {@link Components#itemLineEditor(Class)}) over this
     * constructor for fluent configuration.
     *
     * @param itemType the row bean class (used to build the underlying {@link Grid}) (not null)
     */
    public ItemLineEditor(Class<T> itemType) {
        super();
        ObjectUtils.argumentNotNull(itemType, "Item type must be not null");
        this.itemType = itemType;

        getContent().addClassName("item-line-editor");

        this.countBadge = Components.span().styleName("ile-count-badge").build();
        // aria-live region: screen readers announce item count changes without needing focus
        countBadge.getElement().setAttribute("aria-live", "polite");
        this.titleSpan = Components.span()
                .text(resolveText(LocalizationProvider.localize("Items", "item_line_editor.title")))
                .styleName("ile-title").build();
        Div titleGroup = Components.div().add(titleSpan, countBadge).styleName("ile-title-group").build();

        String addItemText = LocalizationProvider.localize("Add item", "item_line_editor.add_item_btn");
        this.addBtn = Components.button().text(addItemText).icon(VaadinIcon.PLUS).tertiary()
                .withClickListener(e -> addRow()).build();
        addBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        this.toolbarActions = Components.div().add(addBtn).styleName("ile-toolbar-actions").build();

        Div toolbar = Components.div().add(titleGroup, toolbarActions).styleName("ile-toolbar").build();

        this.grid = buildGrid();
        this.gridWrapper = Components.div().add(grid).styleName("ile-table").build();

        this.emptyAddBtn = Components.button().text(addItemText).icon(VaadinIcon.PLUS).primary()
                .withClickListener(e -> addRow()).build();
        this.emptyState = Empty.builder()
                .icon(VaadinIcon.STOCK.create())
                .title(LocalizationProvider.localize("No items added yet", "item_line_editor.empty_title"))
                .description(LocalizationProvider.localize("Add an item to get started.",
                        "item_line_editor.empty_description"))
                .action(emptyAddBtn)
                .build();
        emptyState.setVisible(false);

        // Responsive mobile FormLayout fallback: shown below the 640px breakpoint whenever no
        // setMobileCard(...) summary/edit-sheet view has been configured (see item-line-editor.css),
        // so the editor is always usable on small screens without requiring extra per-domain wiring.
        this.mobileFormsContainer = Components.div().styleName("ile-mobile-forms").build();

        getContent().add(toolbar, gridWrapper, mobileFormsContainer, emptyState);

        updateGridAriaLabel();
        installDelegatedEnterNav();
        installMobileActivationListener();
        refreshAll();
    }

    // ── Configuration mutators ───────────────────────────────────────────────

    /** Sets the section title shown in the toolbar. */
    public void setTitle(String title) {
        titleSpan.setText(resolveText(title));
        updateGridAriaLabel();
    }

    /** Sets the section title from a {@link Localizable} descriptor. */
    public void setTitle(Localizable title) {
        setTitle(resolve(title));
    }

    /**
     * Sets the factory used to create a new, empty row instance whenever the user clicks
     * "Add item" (required before {@link #addRow()} / {@link #addRows(int)} can be used).
     */
    public void setRowFactory(Supplier<T> rowFactory) {
        this.rowFactory = rowFactory;
    }

    /**
     * Adds a column. The renderer receives the row instance and must return the component to
     * render/edit that cell (a text field, combo box, {@link StatusBadge}, plain span, etc.).
     * Columns are appended in call order; the automatic per-row delete action always stays last.
     */
    public void addColumn(Column<T> column) {
        ObjectUtils.argumentNotNull(column, "Column must be not null");
        columns.add(column);

        // Keep the delete column last: remove and re-append it if already present.
        if (deleteColumn != null) {
            grid.removeColumn(deleteColumn);
        }

        var gridColumn = grid.addColumn(new ComponentRenderer<>(row -> {
            Component cell = column.renderer.apply(row);
            cell.getElement().getClassList().add("ile-cell");
            if (newlyAddedRows.contains(row)) {
                cell.getElement().getClassList().add("ile-cell-new");
            }
            cell.getElement().setAttribute("data-ile-col", column.key);
            attachAutoNotify(cell, row, column);
            validateCell(column, row, cell);
            return cell;
        })).setHeader(column.header).setKey(column.key).setFlexGrow(column.flexGrow);
        if (column.width != null) {
            gridColumn.setWidth(column.width);
        }
        gridColumn.setVisible(!column.detail);
        gridColumnsByKey.put(column.key, gridColumn);

        if (column.detail && toggleDetailsBtn == null) {
            addDetailsToggleButton();
        }

        deleteColumn = grid.addColumn(new ComponentRenderer<>(row -> {
            Button btn = Components.button().icon(VaadinIcon.CLOSE_SMALL).tertiary().error()
                    .styleName("ile-delete-btn")
                    .ariaLabel("Remove item", "item_line_editor.delete_row_aria")
                    .withClickListener(e -> removeRow(row)).build();
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
            return btn;
        })).setWidth("48px").setFlexGrow(0).setKey("__delete");
    }

    /**
     * Convenience shortcut for {@code addColumn(Column.of(key, header, renderer))} — a simple,
     * full-flex-grow, always-visible column with no width customisation.
     */
    public void addColumn(String key, String header, Function<T, Component> renderer) {
        addColumn(Column.of(key, header, renderer));
    }

    /** Caps the maximum number of rows. Default {@value #DEFAULT_MAX_ROWS}. */
    public void setMaxRows(int maxRows) {
        this.maxRows = Math.max(1, maxRows);
    }

    /**
     * Sets a function that (re)builds the footer {@link TotalsCard} from the current row list,
     * called after every add/remove/replace and every {@link #notifyRowChanged(Object)} call.
     * Passing {@code null} removes the footer.
     */
    public void setFooter(Function<List<T>, TotalsCard> footer) {
        this.footerFunction = footer;
        if (footer != null && footerWrapper == null) {
            footerWrapper = Components.div().styleName("ile-footer-wrapper").build();
            getContent().add(footerWrapper);
        }
        updateFooter();
    }

    /** Sets the empty-state title and description shown when there are no rows. */
    public void setEmptyState(String title, String description) {
        emptyState.setTitle(resolveText(title));
        emptyState.setDescription(resolveText(description));
    }

    /** Sets the empty-state icon. Default {@link VaadinIcon#STOCK}. */
    public void setEmptyIcon(VaadinIcon icon) {
        emptyState.setIcon((icon != null ? icon : VaadinIcon.STOCK).create());
    }

    /** Sets the "Add item" button text (both the toolbar action and the empty-state action). */
    public void setAddButtonText(String text) {
        String resolved = resolveText(text);
        addBtn.setText(resolved);
        emptyAddBtn.setText(resolved);
    }

    /** Registers a listener invoked whenever rows are added, removed, replaced or edited. */
    public void setOnChangeListener(Consumer<List<T>> listener) {
        this.changeListener = listener;
    }

    // ── Public row API ───────────────────────────────────────────────────────

    /** Appends a new row created via the configured row factory, then refreshes the view. */
    public void addRow() {
        addRows(1);
    }

    /**
     * Appends {@code count} new rows created via the configured row factory (clamped to
     * {@link #setMaxRows}), highlighting them as "just added" (see {@link #notifyRowChanged(Object)}).
     */
    public void addRows(int count) {
        addRows(count, true);
    }

    /**
     * Appends {@code count} new rows created via the configured row factory (clamped to
     * {@link #setMaxRows}) <strong>without</strong> the "just added" highlight — intended for
     * initial, pre-populated rows (e.g. {@code ItemLineEditorBuilder#initialRows(int)}) rather
     * than rows the user explicitly adds, which should not look permanently "new" on first paint.
     */
    public void addInitialRows(int count) {
        addRows(count, false);
    }

    private void addRows(int count, boolean markAsNew) {
        if (rowFactory == null) {
            throw new IllegalStateException("A rowFactory must be set (see setRowFactory(...)) before adding rows");
        }
        int available = maxRows - rows.size();
        int n = Math.max(0, Math.min(count, available));
        for (int i = 0; i < n; i++) {
            T row = rowFactory.get();
            rows.add(row);
            if (markAsNew) {
                newlyAddedRows.add(row);
            }
        }
        refreshAll();
    }

    /** Removes the given row (if present) and refreshes the view. */
    public void removeRow(T row) {
        if (rows.remove(row)) {
            newlyAddedRows.remove(row);
            refreshAll();
        }
    }

    /** Returns an unmodifiable snapshot of the current rows. */
    public List<T> getRows() {
        return Collections.unmodifiableList(rows);
    }

    /** Replaces all current rows with the given list and refreshes the view. */
    public void setRows(List<T> newRows) {
        rows.clear();
        newlyAddedRows.clear();
        if (newRows != null) {
            rows.addAll(newRows);
        }
        refreshAll();
    }

    /**
     * Notifies the editor that a single row's data changed (e.g. from a cell value-change
     * listener installed by a {@link Column} renderer), triggering a targeted single-row grid
     * refresh, a footer recompute and the change listener — without a full row-list refresh.
     * Also clears the row's "just added" highlight, if any: editing a row is what marks it as no
     * longer new.
     */
    public void notifyRowChanged(T row) {
        newlyAddedRows.remove(row);
        if (dataView != null) {
            dataView.refreshItem(row);
        }
        refreshMobileFormRow(row);
        updateFooter();
        fireChange();
    }

    // ── Responsive mobile card + edit Sheet (generic, opt-in) ────────────────

    /**
     * Enables a responsive mobile card list — shown in place of the {@link Grid} below the
     * {@code 640px} breakpoint (see {@code item-line-editor.css}) — with a bottom {@link Sheet}
     * edit form opened on tap. The edit form is built automatically by re-invoking each
     * {@link Column}'s renderer against the same row and labelling the resulting field (when it
     * implements {@link HasLabel}) with the column header — no extra per-domain wiring required.
     *
     * @param title    extracts the card's primary (bold) text from a row
     * @param subtitle extracts the card's secondary (muted) text from a row
     * @param value    extracts the card's trailing value text from a row
     */
    public void setMobileCard(Function<T, String> title, Function<T, String> subtitle, Function<T, String> value) {
        this.mobileCardTitle = title;
        this.mobileCardSubtitle = subtitle;
        this.mobileCardValue = value;
        if (cardsContainer == null) {
            cardsContainer = Components.div().styleName("ile-cards").build();
            getContent().add(cardsContainer);
            // Only switch the mobile breakpoint from "scrollable table" to "card list" (see
            // item-line-editor.css) once a card fallback actually exists — otherwise consumers
            // that never call setMobileCard(...) would end up with nothing visible on mobile.
            getContent().addClassName("ile-has-mobile-cards");
        }
        if (mobileEverActive) {
            refreshCards();
        }
    }

    // ── Bulk "Add rows" toolbar stepper (generic, opt-in) ────────────────────

    /**
     * Enables a bulk row-count stepper (1-50) plus an "Add Rows" button in the toolbar, allowing
     * several rows to be appended in a single batch refresh.
     *
     * @param enabled {@code true} to show the bulk stepper controls
     */
    public void setBulkAddEnabled(boolean enabled) {
        if (enabled && bulkAddCountField == null) {
            addBulkAddControls();
        }
    }

    /** Sets whether the toolbar's single "Add item" button is visible. Default {@code true}. */
    public void setAddButtonVisible(boolean visible) {
        addBtn.setVisible(visible);
    }

    // ── Generic catalog / bulk-content picker dialog (opt-in) ────────────────

    /**
     * Configures a generic two-panel "picker" dialog — a searchable catalog on the left, the
     * current selection with per-item quantity steppers on the right — opened from a toolbar
     * button, letting the user create several rows at once from an arbitrary catalog/suggestion
     * type {@code S} (unrelated to the row bean type {@code T}).
     *
     * @param <S>             the catalog/suggestion item type
     * @param buttonText      the toolbar button text (e.g. {@code "Pick Items"})
     * @param catalog         the catalog to search/select from
     * @param primaryLabel    extracts the catalog entry's primary (bold) label
     * @param secondaryLabel  extracts the catalog entry's secondary (muted) label (may be null)
     * @param rowMapper       builds a new row from a selected catalog entry and its chosen quantity
     */
    @SuppressWarnings("unchecked")
    public <S> void setItemPicker(String buttonText, List<S> catalog, Function<S, String> primaryLabel,
            Function<S, String> secondaryLabel, BiFunction<S, Integer, T> rowMapper) {
        this.pickerButtonText = (buttonText != null) ? buttonText : "Pick Items";
        this.pickerCatalog = catalog;
        this.pickerPrimaryLabel = primaryLabel;
        this.pickerSecondaryLabel = secondaryLabel;
        this.pickerRowMapper = rowMapper;
        if (pickerBtn == null && catalog != null && !catalog.isEmpty()) {
            addItemPickerButton();
        }
    }

    // ── Static factory ────────────────────────────────────────────────────────

    /**
     * Creates a new {@link ItemLineEditorBuilder} for the given item type — the preferred,
     * fluent entry point (equivalent to {@link Components#itemLineEditor(Class)}).
     *
     * @param <T> the row bean type
     * @param itemType the row bean class (not null)
     * @return a new {@link ItemLineEditorBuilder}
     */
    public static <T> ItemLineEditorBuilder<T> builder(Class<T> itemType) {
        return ItemLineEditorBuilder.create(itemType);
    }

    // ── Internal validation (invoked by the builder) ─────────────────────────

    /**
     * Validates that this instance is minimally usable, throwing {@link IllegalStateException}
     * otherwise. Invoked by {@link ItemLineEditorBuilder#build()}.
     */
    public void validateConfiguration() {
        if (rowFactory == null) {
            throw new IllegalStateException("A rowFactory(...) is required to build an ItemLineEditor");
        }
        if (columns.isEmpty()) {
            throw new IllegalStateException("At least one addColumn(...) is required to build an ItemLineEditor");
        }
    }

    // ── Grid ──────────────────────────────────────────────────────────────────

    private Grid<T> buildGrid() {
        var g = Components.grid(itemType, false);
        g.setAllRowsVisible(true);
        g.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        g.addClassName("ile-grid");
        // Row highlighting for newly added rows is applied directly on each rendered cell
        // component (see addColumn(...)'s ComponentRenderer, class "ile-cell-new") rather than
        // via Grid#setPartNameGenerator(...): the generator only marks the shadow-DOM cell parts,
        // which produced a segmented per-cell border instead of a single clean row highlight.
        dataView = g.setItems(rows);
        return g;
    }

    /** Keeps the grid's {@code aria-label} in sync with the current title (A11Y). */
    private void updateGridAriaLabel() {
        String title = titleSpan != null ? titleSpan.getText() : "";
        grid.getElement().setAttribute("aria-label",
                LocalizationProvider.localize("{0} grid", "item_line_editor.grid_aria_label", title));
    }

    private void addDetailsToggleButton() {
        toggleDetailsBtn = Components.button()
                .text(LocalizationProvider.localize("Show Details", "item_line_editor.show_details"))
                .tertiary().build();
        toggleDetailsBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toggleDetailsBtn.getElement().setAttribute("aria-pressed", "false");
        toggleDetailsBtn.addClickListener(e -> {
            detailsVisible = !detailsVisible;
            columns.stream().filter(c -> c.detail)
                    .forEach(c -> gridColumnsByKey.get(c.key).setVisible(detailsVisible));
            toggleDetailsBtn.setText(detailsVisible
                    ? LocalizationProvider.localize("Hide Details", "item_line_editor.hide_details")
                    : LocalizationProvider.localize("Show Details", "item_line_editor.show_details"));
            toggleDetailsBtn.getElement().setAttribute("aria-pressed", String.valueOf(detailsVisible));
        });
        toolbarActions.add(toggleDetailsBtn);
    }

    // ── Refresh helpers ──────────────────────────────────────────────────────

    private void refreshAll() {
        if (dataView != null) {
            dataView.refreshAll();
        }
        boolean empty = rows.isEmpty();
        gridWrapper.setVisible(!empty);
        // Skip rebuilding the mobile representations entirely until the client has actually
        // reported crossing into the mobile breakpoint at least once (see #mobileEverActive) —
        // avoids maintaining a second, always-hidden copy of every row's components for
        // desktop-only sessions.
        if (mobileEverActive) {
            if (cardsContainer != null) {
                cardsContainer.setVisible(!empty);
                refreshCards();
            }
            mobileFormsContainer.setVisible(!empty);
            refreshMobileForms();
        }
        emptyState.setVisible(empty);
        countBadge.setText(rows.size() + (rows.size() == 1 ? " item" : " items"));
        countBadge.getElement().setAttribute("aria-label",
                LocalizationProvider.localize("{0} items in the list", "item_line_editor.count_aria", rows.size()));
        updateFooter();
        fireChange();
    }

    private void updateFooter() {
        if (footerFunction == null || footerWrapper == null) {
            return;
        }
        footerWrapper.removeAll();
        TotalsCard card = footerFunction.apply(getRows());
        if (card != null) {
            footerWrapper.add(card);
        }
    }

    private void fireChange() {
        if (changeListener != null) {
            changeListener.accept(getRows());
        }
    }

    // ── Keyboard navigation (Enter → same column, next row) ──────────────────

    /**
     * Installs a single, delegated {@code keydown} listener on the editor's root element (called
     * once from the constructor) that intercepts {@code Enter}: if no ComboBox overlay is open,
     * walks the event's composed path to find the closest ancestor carrying a {@code data-ile-col}
     * attribute (set on every {@link Column}'s rendered cell in {@link #addColumn(Column)}), then
     * finds all elements sharing that same column value (i.e. the same column, across every row)
     * and focuses the next one in DOM order.
     *
     * <p>Delegating a single listener at the root — rather than attaching one {@code executeJs} call
     * per rendered cell as before — turns an O(rows × columns) script-injection cost on every grid
     * refresh into a fixed O(1) cost per component instance, which matters once this editor is used
     * across thousands of concurrent sessions.
     */
    private void installDelegatedEnterNav() {
        getContent().getElement().executeJs("""
                const root = this;
                root.addEventListener('keydown', function(e) {
                    if (e.key !== 'Enter') return;
                    if (document.querySelector('vaadin-combo-box-overlay[opened]')) return;
                    var path = e.composedPath();
                    var cell = null;
                    for (var i = 0; i < path.length; i++) {
                        if (path[i].hasAttribute && path[i].hasAttribute('data-ile-col')) {
                            cell = path[i];
                            break;
                        }
                    }
                    if (!cell) return;
                    e.preventDefault();
                    e.stopPropagation();
                    var col = cell.getAttribute('data-ile-col');
                    var all = Array.from(document.querySelectorAll('[data-ile-col="' + col + '"]'));
                    all.sort(function(a, b) { return (a.compareDocumentPosition(b) & 4) ? -1 : 1; });
                    var idx = all.indexOf(cell);
                    if (idx >= 0 && idx < all.length - 1) {
                        all[idx + 1].focus();
                    }
                });
                """);
    }

    /**
     * Lazily activates the mobile representations ({@link #mobileFormsContainer}, and
     * {@link #cardsContainer} if configured) the first time — and only the first time — the client
     * reports matching the {@code (max-width: 640px)} media query used by {@code item-line-editor.css}
     * (a single {@code matchMedia} listener installed once here, re-dispatched as a lightweight custom
     * DOM event so the server can react without polling). Until that happens, {@link #refreshAll()}
     * skips building the mobile fallback entirely for this instance — see {@link #mobileEverActive}.
     */
    private void installMobileActivationListener() {
        getContent().getElement().addEventListener("ile-mobile-change", event -> {
            boolean mobile = event.getEventData().get("event.detail.mobile").asBoolean();
            if (mobile && !mobileEverActive) {
                mobileEverActive = true;
                refreshMobileForms();
                mobileFormsContainer.setVisible(!rows.isEmpty());
                if (cardsContainer != null) {
                    refreshCards();
                    cardsContainer.setVisible(!rows.isEmpty());
                }
            }
        }).addEventData("event.detail.mobile");

        getContent().getElement().executeJs("""
                const root = this;
                const mq = window.matchMedia('(max-width: 640px)');
                const notify = () => root.dispatchEvent(
                        new CustomEvent('ile-mobile-change', { detail: { mobile: mq.matches } }));
                mq.addEventListener('change', notify);
                notify();
                """);
    }

    /**
     * Automatically clears a row's "just added" highlight ({@code ile-row-new} / {@code ile-cell-new} /
     * {@code ile-card-new}) the first time the user actually edits one of its cells, without requiring
     * every {@link Column} renderer to remember to call {@link #notifyRowChanged(Object)} itself; also
     * (re)runs the column's Holon {@link Validator}s (see {@link Column#validator(Validator)}) against
     * the row on every such edit (see {@link #validateCell(Column, Object, Component)}).
     *
     * <p>When the rendered {@code cell} implements {@link HasValue}, a value-change listener is
     * attached that reacts as soon as a <em>client-originated</em> change is observed. Only
     * client-originated changes ({@link HasValue.ValueChangeEvent#isFromClient()}) are considered:
     * renderers commonly set the field's initial value programmatically when binding it to the row
     * bean, and such programmatic sets must not immediately clear the "new" highlight or run validation.
     */
    private void attachAutoNotify(Component cell, T row, Column<T> column) {
        if (cell instanceof HasValue<?, ?> hasValue) {
            hasValue.addValueChangeListener(event -> {
                if (event.isFromClient()) {
                    if (newlyAddedRows.contains(row)) {
                        notifyRowChanged(row);
                    }
                    validateCell(column, row, cell);
                }
            });
        }
    }

    /**
     * Runs {@code column}'s Holon {@link Validator}s (if any) against {@code row} and reflects the
     * outcome directly on the rendered {@code cell}, provided it implements {@link HasValidation} —
     * as every standard Vaadin field and every Holon {@code Input}-backed component does — via
     * {@code setInvalid(true)} / {@code setErrorMessage(...)}. Validators are evaluated in order; the
     * first {@link Validator.ValidationException} wins and its message is shown. A cell with no
     * configured validators, or one that does not implement {@link HasValidation}, is left untouched.
     */
    private void validateCell(Column<T> column, T row, Component cell) {
        if (column.validators.isEmpty() || !(cell instanceof HasValidation hasValidation)) {
            return;
        }
        String errorMessage = null;
        for (Validator<T> validator : column.validators) {
            try {
                validator.validate(row);
            } catch (Validator.ValidationException e) {
                errorMessage = e.getMessage();
                break;
            }
        }
        hasValidation.setInvalid(errorMessage != null);
        hasValidation.setErrorMessage(errorMessage != null ? errorMessage : "");
    }

    /**
     * A11Y helper: makes a plain, non-focusable {@link Div}/{@code Span} click target behave like
     * a native button for assistive technology and keyboard users — sets {@code role="button"},
     * {@code tabindex="0"}, an optional {@code aria-label}, and translates {@code Enter}/{@code Space}
     * keydowns into a click.
     */
    private static void makeAccessibleClickTarget(Component target, String ariaLabel) {
        target.getElement().setAttribute("role", "button");
        target.getElement().setAttribute("tabindex", "0");
        if (ariaLabel != null) {
            target.getElement().setAttribute("aria-label", ariaLabel);
        }
        target.getElement().executeJs("""
                const el = this;
                el.addEventListener('keydown', function(e) {
                    if (e.key === 'Enter' || e.key === ' ' || e.key === 'Spacebar') {
                        e.preventDefault();
                        el.click();
                    }
                });
                """);
    }

    // ── Responsive mobile cards ───────────────────────────────────────────────

    private void refreshCards() {
        if (cardsContainer == null) {
            return;
        }
        cardsContainer.removeAll();
        rows.forEach(row -> cardsContainer.add(buildCard(row)));
    }

    private Div buildCard(T row) {
        String titleText = mobileCardTitle != null ? mobileCardTitle.apply(row) : "";
        Span titleSpan = Components.span().text(titleText).styleName("ile-card-title").build();
        Span subtitleSpan = Components.span()
                .text(mobileCardSubtitle != null ? mobileCardSubtitle.apply(row) : "")
                .styleName("ile-card-subtitle").build();
        Span valueSpan = Components.span()
                .text(mobileCardValue != null ? mobileCardValue.apply(row) : "")
                .styleName("ile-card-value").build();

        Div left = Components.div().add(titleSpan, subtitleSpan).styleName("ile-card-left").build();
        Div right = Components.div().add(valueSpan).styleName("ile-card-right").build();
        Div card = Components.div().add(left, right).styleName("ile-card").build();
        if (newlyAddedRows.contains(row)) {
            card.addClassName("ile-card-new");
        }
        card.addClickListener(e -> openMobileEditSheet(row));
        makeAccessibleClickTarget(card,
                LocalizationProvider.localize("Edit {0}", "item_line_editor.mobile_card_aria", titleText));
        return card;
    }

    private void openMobileEditSheet(T row) {
        List<Component> fields = new ArrayList<>();
        for (Column<T> column : columns) {
            Component field = column.renderer.apply(row);
            if (field instanceof HasLabel hasLabel) {
                hasLabel.setLabel(column.header);
            }
            attachAutoNotify(field, row, column);
            validateCell(column, row, field);
            fields.add(field);
        }
        var form = new FormLayout(fields.toArray(new Component[0]));
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Button deleteBtn = Components.button()
                .text(LocalizationProvider.localize("Delete Row", "item_line_editor.mobile_delete_btn"))
                .error().tertiary()
                .withClickListener(e -> removeRow(row)).build();

        Sheet.builder(Sheet.Side.BOTTOM)
                .title(mobileCardTitle != null ? mobileCardTitle.apply(row)
                        : LocalizationProvider.localize("Edit Item", "item_line_editor.mobile_edit_default_title"))
                .content(form, deleteBtn)
                .fullscreenOnMobile(true)
                .onClose(this::refreshAll)
                .build()
                .open();
    }

    // ── Responsive mobile FormLayout fallback (always on, used when no
    // setMobileCard(...) card/sheet view is configured) ──────────────────────

    private void refreshMobileForms() {
        mobileFormsByRow.clear();
        mobileFormsContainer.removeAll();
        rows.forEach(row -> {
            Div form = buildMobileForm(row);
            mobileFormsByRow.put(row, form);
            mobileFormsContainer.add(form);
        });
    }

    /** Rebuilds and swaps in place the single row's mobile form (targeted refresh, mirroring
     * the grid's {@code dataView.refreshItem(row)}), avoiding a full container rebuild — and thus
     * avoiding disrupting focus/scroll on every other row — whenever a single field changes. */
    private void refreshMobileFormRow(T row) {
        Div oldForm = mobileFormsByRow.get(row);
        if (oldForm == null) {
            return;
        }
        Div newForm = buildMobileForm(row);
        mobileFormsContainer.replace(oldForm, newForm);
        mobileFormsByRow.put(row, newForm);
    }

    private Div buildMobileForm(T row) {
        List<Component> fields = new ArrayList<>();
        for (Column<T> column : columns) {
            Component field = column.renderer.apply(row);
            if (field instanceof HasLabel hasLabel) {
                hasLabel.setLabel(column.header);
            }
            attachAutoNotify(field, row, column);
            validateCell(column, row, field);
            fields.add(field);
        }
        var form = new FormLayout(fields.toArray(new Component[0]));
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.addClassName("ile-mobile-form-fields");

        Button deleteBtn = Components.button()
                .text(LocalizationProvider.localize("Remove", "item_line_editor.mobile_form_remove_btn"))
                .icon(VaadinIcon.TRASH).error().tertiary()
                .withClickListener(e -> removeRow(row)).build();
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Div rowForm = Components.div().add(form, deleteBtn).styleName("ile-mobile-form-row").build();
        if (newlyAddedRows.contains(row)) {
            rowForm.addClassName("ile-mobile-form-row-new");
        }
        return rowForm;
    }

    // ── Bulk "Add rows" toolbar controls ──────────────────────────────────────

    private void addBulkAddControls() {
        bulkAddCountField = new IntegerField();
        bulkAddCountField.addClassName("ile-row-count-field");
        bulkAddCountField.setValue(1);
        bulkAddCountField.setMin(1);
        bulkAddCountField.setMax(50);
        bulkAddCountField.setStepButtonsVisible(true);
        String rowsCountLabel = LocalizationProvider.localize("Number of rows to add", "item_line_editor.rows_count_aria");
        bulkAddCountField.setTitle(rowsCountLabel);
        bulkAddCountField.setAriaLabel(rowsCountLabel);

        Button addRowsBtn = Components.button()
                .text(LocalizationProvider.localize("Add Rows", "item_line_editor.add_rows_btn"))
                .icon(VaadinIcon.PLUS).tertiary()
                .withClickListener(e -> addRows(bulkAddCountField.getValue() != null ? bulkAddCountField.getValue() : 1))
                .build();
        addRowsBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        toolbarActions.add(bulkAddCountField, addRowsBtn);
    }

    // ── Generic catalog / bulk-content picker dialog ─────────────────────────

    private void addItemPickerButton() {
        pickerBtn = Components.button().text(pickerButtonText).icon(VaadinIcon.SEARCH).tertiary()
                .withClickListener(e -> openItemPickerDialog()).build();
        pickerBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toolbarActions.add(pickerBtn);
    }

    @SuppressWarnings("unchecked")
    private void openItemPickerDialog() {
        var dialog = new Dialog();
        dialog.setHeaderTitle(pickerButtonText);
        dialog.setWidth("900px");
        dialog.setCloseOnOutsideClick(true);

        var selection = new LinkedHashMap<Object, Integer>();
        var searchHolder = new String[]{""};
        var renderRef = new Runnable[1];

        var searchField = new TextField();
        searchField.setWidthFull();
        searchField.setPlaceholder(
                LocalizationProvider.localize("Type to search…", "item_line_editor.picker_search_placeholder"));
        searchField.setAriaLabel(LocalizationProvider.localize("Search catalog", "item_line_editor.picker_search_aria"));
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setValueChangeTimeout(300);

        Div searchWrapper = Components.div().add(searchField).styleName("ile-bulk-search-wrapper").build();
        Div itemList = Components.div().styleName("ile-bulk-item-list").build();
        itemList.getElement().setAttribute("role", "listbox");
        Div leftPanel = Components.div().add(searchWrapper, itemList).styleName("ile-bulk-left").build();

        Span selectedTitle = Components.span()
                .text(LocalizationProvider.localize("Selected Items", "item_line_editor.picker_selected_title"))
                .styleName("ile-bulk-selected-title").build();
        Span countBadge = Components.span().text("0").styleName("ile-bulk-count-badge").build();
        Span totalQtyLabel = Components.span()
                .text(LocalizationProvider.localize("Total Quantity: {0}", "item_line_editor.picker_total_qty", 0))
                .styleName("ile-bulk-total-qty").build();
        totalQtyLabel.getElement().setAttribute("aria-live", "polite");

        Div rightHeader = Components.div().add(selectedTitle, countBadge, totalQtyLabel)
                .styleName("ile-bulk-right-header").build();
        Div selectedList = Components.div().styleName("ile-bulk-selected-list").build();
        Div rightPanel = Components.div().add(rightHeader, selectedList).styleName("ile-bulk-right").build();

        Div split = Components.div().add(leftPanel, rightPanel).styleName("ile-bulk-split").build();
        dialog.add(split);

        renderRef[0] = () -> {
            itemList.removeAll();
            String filter = searchHolder[0].toLowerCase(Locale.ROOT);
            ((List<Object>) pickerCatalog).stream()
                    .filter(s -> filter.isEmpty()
                            || String.valueOf(pickerPrimaryLabel.apply(s)).toLowerCase(Locale.ROOT).contains(filter)
                            || (pickerSecondaryLabel != null && String.valueOf(pickerSecondaryLabel.apply(s))
                                    .toLowerCase(Locale.ROOT).contains(filter)))
                    .forEach(s -> {
                        boolean sel = selection.containsKey(s);
                        String primary = String.valueOf(pickerPrimaryLabel.apply(s));

                        Span nameSpan = Components.span().text(primary)
                                .styleName("ile-bulk-item-name").build();
                        Div textCol = Components.div().add(nameSpan).styleName("ile-bulk-item-text").build();
                        if (pickerSecondaryLabel != null) {
                            textCol.add(Components.span().text(String.valueOf(pickerSecondaryLabel.apply(s)))
                                    .styleName("ile-bulk-item-meta").build());
                        }
                        Div itemRow = Components.div().add(textCol).styleName("ile-bulk-item-row").build();
                        if (sel) {
                            itemRow.addClassName("ile-bulk-item-row--selected");
                            var check = VaadinIcon.CHECK_CIRCLE.create();
                            check.addClassName("ile-bulk-item-check");
                            check.getElement().setAttribute("aria-hidden", "true");
                            itemRow.add(check);
                        }
                        itemRow.getElement().setAttribute("role", "option");
                        itemRow.getElement().setAttribute("aria-selected", String.valueOf(sel));
                        makeAccessibleClickTarget(itemRow, LocalizationProvider.localize(
                                "{0}, press Enter to toggle selection", "item_line_editor.picker_item_aria", primary));
                        itemRow.addClickListener(e -> {
                            if (selection.remove(s) == null) {
                                selection.put(s, 1);
                            }
                            renderRef[0].run();
                        });
                        itemList.add(itemRow);
                    });

            selectedList.removeAll();
            selection.forEach((s, qty) -> {
                int[] qHolder = {qty};
                Span nameLabel = Components.span().text(String.valueOf(pickerPrimaryLabel.apply(s)))
                        .styleName("ile-bulk-selected-name").build();

                Button minusBtn = Components.button().icon(VaadinIcon.MINUS).tertiary()
                        .styleName("ile-bulk-qty-btn")
                        .ariaLabel("Decrease quantity", "item_line_editor.picker_decrease_qty_aria")
                        .build();
                minusBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
                Span qtySpan = Components.span().text(String.valueOf(qHolder[0])).styleName("ile-bulk-qty-value")
                        .build();
                Button plusBtn = Components.button().icon(VaadinIcon.PLUS).tertiary()
                        .styleName("ile-bulk-qty-btn")
                        .ariaLabel("Increase quantity", "item_line_editor.picker_increase_qty_aria")
                        .build();
                plusBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

                minusBtn.addClickListener(e -> {
                    if (qHolder[0] <= 1) {
                        selection.remove(s);
                        renderRef[0].run();
                        return;
                    }
                    qHolder[0]--;
                    selection.put(s, qHolder[0]);
                    qtySpan.setText(String.valueOf(qHolder[0]));
                    syncPickerTotals(selection, countBadge, totalQtyLabel);
                });
                plusBtn.addClickListener(e -> {
                    qHolder[0]++;
                    selection.put(s, qHolder[0]);
                    qtySpan.setText(String.valueOf(qHolder[0]));
                    syncPickerTotals(selection, countBadge, totalQtyLabel);
                });

                Div stepper = Components.div().add(minusBtn, qtySpan, plusBtn).styleName("ile-bulk-qty-stepper")
                        .build();
                Div selectedRow = Components.div().add(nameLabel, stepper).styleName("ile-bulk-selected-row").build();
                selectedList.add(selectedRow);
            });

            syncPickerTotals(selection, countBadge, totalQtyLabel);
        };

        renderRef[0].run();

        searchField.addValueChangeListener(e -> {
            searchHolder[0] = e.getValue() != null ? e.getValue() : "";
            renderRef[0].run();
        });

        var cancelBtn = Components.button()
                .text(LocalizationProvider.localize("Cancel", "item_line_editor.picker_cancel_btn"))
                .tertiary().withClickListener(e -> dialog.close()).build();
        var confirmBtn = Components.button()
                .text(LocalizationProvider.localize("Add Items", "item_line_editor.picker_add_btn"))
                .icon(VaadinIcon.PLUS).primary().build();
        confirmBtn.addClickListener(e -> {
            if (selection.isEmpty()) {
                return;
            }
            selection.forEach((s, qty) -> {
                if (rows.size() < maxRows) {
                    T newRow = (T) pickerRowMapper.apply(s, qty);
                    rows.add(newRow);
                    newlyAddedRows.add(newRow);
                }
            });
            refreshAll();
            dialog.close();
        });

        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }

    private void syncPickerTotals(Map<Object, Integer> selection, Span countBadge, Span totalQtyLabel) {
        int total = selection.values().stream().mapToInt(Integer::intValue).sum();
        countBadge.setText(String.valueOf(selection.size()));
        totalQtyLabel.setText(LocalizationProvider.localize("Total Quantity: {0}", "item_line_editor.picker_total_qty", total));
    }

    // ── Localization helpers ─────────────────────────────────────────────────

    private static String resolveText(String text) {
        return text != null ? text : "";
    }

    private static String resolve(Localizable localizable) {
        return LocalizationProvider.localize(localizable)
                .orElseGet(() -> localizable != null && localizable.getMessage() != null ? localizable.getMessage() : "");
    }

    // ── HasComponent ──────────────────────────────────────────────────────────

    @Override
    public Component getComponent() {
        return getContent();

    }
}



