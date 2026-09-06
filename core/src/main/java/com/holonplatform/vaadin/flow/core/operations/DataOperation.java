package com.holonplatform.vaadin.flow.core.operations;

import java.io.Serializable;

/**
 * Sealed interface for CRUD operation types.
 *
 * Enables exhaustive pattern matching on database operations at compile time.
 * Used for auditing, logging, and security checks.
 *
 * Example usage:
 * <pre>{@code
 * AuditLog log = switch (operation) {
 *     case DataOperation.Create create -> recordCreate(create.entity());
 *     case DataOperation.Read read -> recordRead(read.id());
 *     case DataOperation.Update update -> recordUpdate(update.entity());
 *     case DataOperation.Delete delete -> recordDelete(delete.id());
 *     case DataOperation.Bulk bulk -> recordBulk(bulk.count());
 * };
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface DataOperation permits
    DataOperation.Create,
    DataOperation.Read,
    DataOperation.Update,
    DataOperation.Delete,
    DataOperation.Bulk {

    /**
     * Get the operation type name.
     *
     * @return operation type (e.g., "CREATE", "READ")
     */
    String getOperationType();

    // =========================================================================
    // Create Operation
    // =========================================================================

    /**
     * CREATE operation: Insert new record.
     */
    final class Create implements DataOperation, Serializable {
        private final String entityType;
        private final Object entity;

        public Create(String entityType, Object entity) {
            this.entityType = entityType;
            this.entity = entity;
        }

        @Override
        public String getOperationType() {
            return "CREATE";
        }

        public String entityType() { return entityType; }
        public Object entity() { return entity; }

        @Override
        public String toString() {
            return String.format("Create(%s)", entityType);
        }
    }

    // =========================================================================
    // Read Operation
    // =========================================================================

    /**
     * READ operation: Fetch existing record by ID.
     */
    final class Read implements DataOperation, Serializable {
        private final String entityType;
        private final Object id;

        public Read(String entityType, Object id) {
            this.entityType = entityType;
            this.id = id;
        }

        @Override
        public String getOperationType() {
            return "READ";
        }

        public String entityType() { return entityType; }
        public Object id() { return id; }

        @Override
        public String toString() {
            return String.format("Read(%s[%s])", entityType, id);
        }
    }

    // =========================================================================
    // Update Operation
    // =========================================================================

    /**
     * UPDATE operation: Modify existing record.
     */
    final class Update implements DataOperation, Serializable {
        private final String entityType;
        private final Object entity;
        private final Object id;

        public Update(String entityType, Object entity, Object id) {
            this.entityType = entityType;
            this.entity = entity;
            this.id = id;
        }

        @Override
        public String getOperationType() {
            return "UPDATE";
        }

        public String entityType() { return entityType; }
        public Object entity() { return entity; }
        public Object id() { return id; }

        @Override
        public String toString() {
            return String.format("Update(%s[%s])", entityType, id);
        }
    }

    // =========================================================================
    // Delete Operation
    // =========================================================================

    /**
     * DELETE operation: Remove existing record.
     */
    final class Delete implements DataOperation, Serializable {
        private final String entityType;
        private final Object id;

        public Delete(String entityType, Object id) {
            this.entityType = entityType;
            this.id = id;
        }

        @Override
        public String getOperationType() {
            return "DELETE";
        }

        public String entityType() { return entityType; }
        public Object id() { return id; }

        @Override
        public String toString() {
            return String.format("Delete(%s[%s])", entityType, id);
        }
    }

    // =========================================================================
    // Bulk Operation
    // =========================================================================

    /**
     * BULK operation: Multiple records affected (insert/update/delete).
     */
    final class Bulk implements DataOperation, Serializable {
        private final String operationType;  // "BULK_CREATE", "BULK_UPDATE", "BULK_DELETE"
        private final String entityType;
        private final int count;

        public Bulk(String operationType, String entityType, int count) {
            this.operationType = operationType;
            this.entityType = entityType;
            this.count = count;
        }

        @Override
        public String getOperationType() {
            return operationType;
        }

        public String entityType() { return entityType; }
        public int count() { return count; }

        @Override
        public String toString() {
            return String.format("Bulk(%s, %d items)", operationType, count);
        }
    }
}

