package com.holonplatform.vaadin.flow.ai.datastore;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReadOnlySqlGuardTest {

    @Test
    void acceptsSimpleSelect() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("SELECT id, name FROM customers")).isTrue();
    }

    @Test
    void acceptsSelectWithTrailingSemicolon() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("select * from customers;")).isTrue();
    }

    @Test
    void acceptsWithCte() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect(
                "WITH top AS (SELECT id FROM customers) SELECT * FROM top")).isTrue();
    }

    @Test
    void rejectsNullOrBlank() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect(null)).isFalse();
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("   ")).isFalse();
    }

    @Test
    void rejectsNonSelectStatements() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("DELETE FROM customers")).isFalse();
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("UPDATE customers SET name = 'x'")).isFalse();
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("DROP TABLE customers")).isFalse();
    }

    @Test
    void rejectsMultipleStatements() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect("SELECT 1; DROP TABLE customers")).isFalse();
    }

    @Test
    void rejectsSelectSmugglingWriteKeyword() {
        assertThat(ReadOnlySqlGuard.isReadOnlySelect(
                "SELECT * FROM customers WHERE name = 'x' -- ; DELETE FROM customers")).isFalse();
    }

}
