package com.holonplatform.vaadin.flow.demo.ui.dialog;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AccumUpdateDialog extends Dialog {

    private final Connection connection; // Provided by caller. Not closed here.

    private final TextField batchGuidField = new TextField("BATCH_GUID (hex)");
    private final Grid<Row> grid = new Grid<>(Row.class, false);
    private final List<Row> items = new ArrayList<>();
    private final ListDataProvider<Row> dataProvider = new ListDataProvider<>(items);

    private final Button addBtn = new Button("Add Row");
    private final Button removeBtn = new Button("Remove Selected");
    private final Button clearBtn = new Button("Clear");
    private final Button submitBtn = new Button("Submit Updates");
    private final Button closeBtn = new Button("Close", e -> close());

    // 🔹 Editor components promoted to fields so we can focus them programmatically
    private TextField accumIdEditor;
    private BigDecimalField amountEditor;

    public AccumUpdateDialog(Connection connection) {
//        if (connection == null) throw new IllegalArgumentException("Connection must not be null.");
        this.connection = connection;

        setHeaderTitle("POS Accumulator Batch Updater");
        setDraggable(true);
        setResizable(true);
        setWidth(900, Unit.PIXELS);
        setHeight(600, Unit.PIXELS);

        buildContent();
        buildGrid();
        wireActions();
    }

    private void buildContent() {
        batchGuidField.setWidthFull();
        batchGuidField.setPlaceholder("e.g., be19968016ca4c0c82a8dfbf206e996a");

        grid.setDataProvider(dataProvider);
        grid.setWidthFull();
        grid.setHeight("420px");

        addBtn.getElement().setProperty("theme", "primary");
        submitBtn.getElement().setProperty("theme", "primary");

        HorizontalLayout controls = new HorizontalLayout(addBtn, removeBtn, clearBtn, new Span(), submitBtn, closeBtn);
        controls.setWidthFull();
        controls.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        controls.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout layout = new VerticalLayout(batchGuidField, grid, controls);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setSizeFull();

        add(layout);
    }

    private void buildGrid() {
        // 🔹 Initialize editor components as fields
        accumIdEditor = new TextField();
        accumIdEditor.setWidth("160px");
        accumIdEditor.setPlaceholder("e.g., 93");
        accumIdEditor.setAutoselect(true);

        amountEditor = new BigDecimalField();
        amountEditor.setWidth("140px");
        amountEditor.setPlaceholder("0.00");
        amountEditor.setClearButtonVisible(true);

        grid.addColumn(Row::getAccumId)
            .setHeader("ACCUM_ID")
            .setAutoWidth(true)
            .setEditorComponent(accumIdEditor);

        grid.addColumn(row -> row.getAmount() == null ? "" : row.getAmount().toPlainString())
            .setHeader("AMOUNT (decimal)")
            .setAutoWidth(true)
            .setEditorComponent(amountEditor);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        // Binder + editor
        Editor<Row> editor = grid.getEditor();
        Binder<Row> binder = new Binder<>(Row.class);
        editor.setBinder(binder);

        binder.forField(accumIdEditor)
              .withConverter(String::trim, s -> s)
              .bind(Row::getAccumId, Row::setAccumId);

        binder.forField(amountEditor)
              .bind(Row::getAmount, Row::setAmount);

        // Open editor on double-click
        grid.addItemDoubleClickListener(ev -> {
            editor.editItem(ev.getItem());
            accumIdEditor.focus();
        });

        // Save/cancel with user behavior
        grid.getElement().addEventListener("keydown", e -> editor.cancel())
            .setFilter("event.key === 'Escape'");

        grid.addItemClickListener(ev -> {
            if (editor.isOpen() && !Objects.equals(editor.getItem(), ev.getItem())) {
                editor.save();
            }
        });

        // 🔹 TAB on the last field (amount) should content a new row and focus ACCUM_ID on it
        amountEditor.addKeyDownListener(Key.TAB, e -> {
            // Only when TAB (not Shift+TAB) is pressed inside the last editor field
            if (e.getKey() != Key.SHIFT) {
                // Let the browser move focus, then content the row and force focus back to new row
                getUI().ifPresent(ui -> ui.access(() -> {
                    addNewBlankRowAndEdit();
                }));
            }
        });
    }

    private void wireActions() {
        addBtn.addClickListener(e -> {
            Row r = new Row("", null);
            items.add(r);
            dataProvider.refreshAll();
            grid.getSelectionModel().select(r);
            grid.scrollToIndex(items.size() - 1);
            // Optionally start editing immediately
            grid.getEditor().editItem(r);
            accumIdEditor.focus();
        });

        removeBtn.addClickListener(e -> {
            Row selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                items.remove(selected);
                dataProvider.refreshAll();
            } else {
                notifyWarn("Select a row to remove.");
            }
        });

        clearBtn.addClickListener(e -> {
            items.clear();
            dataProvider.refreshAll();
            batchGuidField.clear();
        });

        submitBtn.addClickListener(e -> onSubmit());
    }

    /**
     * Adds a new blank row at the end, selects it, opens the editor, and focuses ACCUM_ID.
     */
    private void addNewBlankRowAndEdit() {
        Row r = new Row("", null);
        items.add(r);
        dataProvider.refreshAll();

        grid.getSelectionModel().select(r);
        grid.scrollToIndex(items.size() - 1);

        // Start editing new row
        grid.getEditor().editItem(r);
        // Focus the first field (ACCUM_ID)
        accumIdEditor.focus();
        accumIdEditor.setAutoselect(true);
    }

    private void onSubmit() {
        String batchHex = trim(batchGuidField.getValue()).toLowerCase(Locale.ROOT);
        if (!isValidHex(batchHex)) {
            notifyWarn("BATCH_GUID must be a non-empty even-length hex string (0-9, a-f).");
            return;
        }
        if (items.isEmpty()) {
            notifyWarn("Please content at least one ACCUM_ID + AMOUNT row.");
            return;
        }

        // Validate rows
        for (int i = 0; i < items.size(); i++) {
            Row r = items.get(i);
            if (isBlank(r.getAccumId())) {
                notifyWarn("Row " + (i + 1) + ": ACCUM_ID is required.");
                return;
            }
            if (r.getAmount() == null) {
                notifyWarn("Row " + (i + 1) + ": AMOUNT is required.");
                return;
            }
        }

        final String sql =
            "UPDATE POS_XS_CALC_CLOSE_ACCUMS_TB " +
            "   SET AMOUNT = ? " +
            " WHERE BATCH_GUID = HEXTORAW(?) " +
            "   AND UPPER(ACCUM_ID) = UPPER(?)";

        boolean oldAuto = true;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            oldAuto = connection.getAutoCommit();
            if (oldAuto) {
                connection.setAutoCommit(false);
            }

            for (Row r : items) {
                ps.setBigDecimal(1, r.getAmount());
                ps.setString(2, batchHex);
                ps.setString(3, r.getAccumId());
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            connection.commit();

            int updated = 0;
            StringBuilder zeroes = new StringBuilder();
            for (int i = 0; i < results.length; i++) {
                int cnt = results[i];
                if (cnt == 0) {
                    zeroes.append("\n - No rows updated for ACCUM_ID=")
                          .append(items.get(i).getAccumId());
                } else if (cnt > 0 || cnt == Statement.SUCCESS_NO_INFO) {
                    updated += (cnt > 0 ? cnt : 1);
                }
            }

            Notification n = Notification.show(
                "Done. Batch executed. Rows affected (aggregate): " + updated +
                (zeroes.length() > 0 ? ("\nSome IDs did not match:" + zeroes) : ""),
                5000, Notification.Position.MIDDLE
            );
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (Exception ex) {
            try { connection.rollback(); } catch (Exception ignored) {}
            Notification n = Notification.show("Failed to execute updates: " + ex.getMessage(),
                    6000, Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            ex.printStackTrace();
        } finally {
            try { connection.setAutoCommit(oldAuto); } catch (Exception ignored) {}
        }
    }

    // --- helpers ---

    private void notifyWarn(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.MIDDLE);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    private static boolean isValidHex(String s) {
        if (s == null || s.isEmpty() || (s.length() % 2 != 0)) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean hex = (c >= '0' && c <= '9') ||
                          (c >= 'a' && c <= 'f') ||
                          (c >= 'A' && c <= 'F');
            if (!hex) return false;
        }
        return true;
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean isBlank(String s) {
        return trim(s).isEmpty();
    }

    // Bean for grid
    public static class Row {
        private String accumId;
        private BigDecimal amount;

        public Row() { }
        public Row(String accumId, BigDecimal amount) {
            this.accumId = accumId;
            this.amount = amount;
        }

        public String getAccumId() { return accumId; }
        public void setAccumId(String accumId) { this.accumId = accumId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}