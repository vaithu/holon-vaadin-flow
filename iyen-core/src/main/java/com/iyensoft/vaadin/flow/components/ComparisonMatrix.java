package com.iyensoft.vaadin.flow.components;

import com.iyensoft.vaadin.flow.components.builders.ComparisonMatrixBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Table;
import com.vaadin.flow.component.html.TableDataCell;
import com.vaadin.flow.component.html.TableHeaderCell;
import com.vaadin.flow.component.html.TableRow;

import java.util.Objects;

/**
 * A scrollable semantic table for comparing arbitrary subjects across labeled rows.
 * Use the returned table cells and rows to apply application-specific styles.
 *
 * <p>For example:
 * <pre>{@code
 * ComparisonMatrix matrix = Components.comparisonMatrix()
 *         .heading("Criteria")
 *         .column("Option A")
 *         .column("Option B")
 *         .row("Lead time", "10 days", "14 days")
 *         .build();
 * matrix.addRow("Action", new Button("Select A"), new Button("Select B"))
 *         .addClassName("action-row");
 * }</pre>
 */
@StyleSheet("context://comparison-matrix.css")
public class ComparisonMatrix extends Div {

    private final Table table = new Table();
    private final TableRow headerRow;
    private final TableHeaderCell rowLabelHeader;
    private int columnCount;
    private boolean hasRows;

    public ComparisonMatrix() {
        addClassName("matrix");
        table.addClassName("mx-table");
        headerRow = table.addHeaderRow();
        rowLabelHeader = headerRow.addColumnHeaderCell("");
        rowLabelHeader.addClassName("label");
        add(table);
    }

    public static ComparisonMatrixBuilder builder() {
        return ComparisonMatrixBuilder.create();
    }

    public Table getTable() {
        return table;
    }

    public void setHeading(String heading) {
        rowLabelHeader.setText(Objects.requireNonNull(heading, "heading"));
    }

    public TableHeaderCell addColumn(String title) {
        return addColumn(new Span(Objects.requireNonNull(title, "title")));
    }

    public TableHeaderCell addColumn(Component content) {
        Objects.requireNonNull(content, "content");
        if (hasRows) {
            throw new IllegalStateException("Add columns before rows");
        }
        TableHeaderCell cell = headerRow.addColumnHeaderCell(content);
        cell.addClassName("col-hd");
        columnCount++;
        return cell;
    }

    public TableRow addRow(String label, String... values) {
        Objects.requireNonNull(values, "values");
        Component[] components = new Component[values.length];
        for (int i = 0; i < values.length; i++) {
            components[i] = new Span(Objects.requireNonNull(values[i], "value"));
        }
        return addRow(label, components);
    }

    public TableRow addRow(String label, Component... values) {
        Objects.requireNonNull(label, "label");
        validateCount(Objects.requireNonNull(values, "values").length);
        TableRow row = table.addRow();
        TableHeaderCell heading = row.addRowHeaderCell(label);
        heading.addClassName("label");
        for (Component value : values) {
            TableDataCell cell = row.addDataCell(Objects.requireNonNull(value, "value"));
            cell.addClassName("val");
        }
        hasRows = true;
        return row;
    }

    public void addRow(TableRow row) {
        Objects.requireNonNull(row, "row");
        validateCount(row.getCells().size() - 1);
        table.addRows(row);
        hasRows = true;
    }

    private void validateCount(int values) {
        if (columnCount == 0 || values != columnCount) {
            throw new IllegalArgumentException("Expected one value per column (" + columnCount + ")");
        }
    }
}
