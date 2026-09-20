package com.holonplatform.vaadin.flow.ai.datastore;

import com.vaadin.flow.component.ai.provider.DatabaseProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A Holon-backed {@link DatabaseProvider} for open-ended, natural-language querying.
 *
 * <p>The LLM only ever sees the plain-text schema returned by {@link #getSchema()}. Rows
 * returned from {@link #executeQuery(String)} must be rendered directly in a UI component by the
 * caller and are never fed back into the model — this class does not retain or re-expose them
 * once returned.
 *
 * <h3>Guardrails enforced by this class</h3>
 * <ul>
 *   <li>Only single {@code SELECT}/{@code WITH ... SELECT} statements are executed
 *       ({@link ReadOnlySqlGuard}); anything else throws {@link IllegalArgumentException}.</li>
 *   <li>The supplied {@link DataSource} <b>must</b> be backed by a read-only database account —
 *       this class does not itself change grants, it only refuses to run non-SELECT text.</li>
 *   <li>A row/column cap is applied to avoid unbounded result sets.</li>
 * </ul>
 *
 * <p>Construct one instance per exposed schema slice — do not reuse the same instance across
 * unrelated use cases with different data-visibility requirements.
 */
public class DatastoreDatabaseProvider implements DatabaseProvider {

    /** Default maximum number of rows returned by {@link #executeQuery(String)}. */
    public static final int DEFAULT_MAX_ROWS = 200;

    private final DataSource readOnlyDataSource;
    private final String schemaDescription;
    private final int maxRows;

    /**
     * Creates a new provider.
     *
     * @param readOnlyDataSource a {@link DataSource} backed by a read-only database account
     *                           (not null)
     * @param schemaDescription  the plain-text schema description exposed to the LLM — include
     *                           only the tables/columns this use case is allowed to expose
     *                           (not null)
     */
    public DatastoreDatabaseProvider(DataSource readOnlyDataSource, String schemaDescription) {
        this(readOnlyDataSource, schemaDescription, DEFAULT_MAX_ROWS);
    }

    /**
     * Creates a new provider with a custom row cap.
     *
     * @param readOnlyDataSource a {@link DataSource} backed by a read-only database account
     *                           (not null)
     * @param schemaDescription  the plain-text schema description exposed to the LLM (not null)
     * @param maxRows            maximum number of rows to return per query (must be &gt; 0)
     */
    public DatastoreDatabaseProvider(DataSource readOnlyDataSource, String schemaDescription, int maxRows) {
        this.readOnlyDataSource = Objects.requireNonNull(readOnlyDataSource, "readOnlyDataSource must not be null");
        this.schemaDescription = Objects.requireNonNull(schemaDescription, "schemaDescription must not be null");
        if (maxRows <= 0) {
            throw new IllegalArgumentException("maxRows must be > 0");
        }
        this.maxRows = maxRows;
    }

    @Override
    public String getSchema() {
        return schemaDescription;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        if (!ReadOnlySqlGuard.isReadOnlySelect(sql)) {
            throw new IllegalArgumentException(
                    "Only single read-only SELECT statements are allowed for the AI assistant");
        }
        try (Connection connection = readOnlyDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.setMaxRows(maxRows);
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                return toRows(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error executing AI assistant query", e);
        }
    }

    private List<Map<String, Object>> toRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

}
