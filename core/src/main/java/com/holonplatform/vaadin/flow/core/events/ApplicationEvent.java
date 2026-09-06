package com.holonplatform.vaadin.flow.core.events;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Sealed interface for application events.
 *
 * Enables type-safe event handling with exhaustive pattern matching.
 * Use for logging, auditing, and monitoring application behavior.
 *
 * Example:
 * <pre>{@code
 * void handleEvent(ApplicationEvent event) {
 *     String description = switch (event) {
 *         case ApplicationEvent.ViewAccessed v ->
 *             "User accessed view: " + v.viewName();
 *         case ApplicationEvent.DataModified d ->
 *             "Data modified: " + d.entityType();
 *         case ApplicationEvent.Error e ->
 *             "Error: " + e.errorType();
 *         case ApplicationEvent.Performance p ->
 *             "Performance: " + p.metricName() + " = " + p.value();
 *     };
 *     log.info(description);
 * }
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface ApplicationEvent permits
    ApplicationEvent.ViewAccessed,
    ApplicationEvent.DataModified,
    ApplicationEvent.Error,
    ApplicationEvent.Performance {

    /**
     * Get event timestamp.
     *
     * @return when this event occurred
     */
    LocalDateTime timestamp();

    /**
     * Get event type name.
     *
     * @return event type (e.g., "VIEW_ACCESSED")
     */
    String eventType();

    // =========================================================================
    // View Access Event
    // =========================================================================

    /**
     * User accessed a view.
     */
    final class ViewAccessed implements ApplicationEvent, Serializable {
        private final LocalDateTime ts;
        private final String viewName;
        private final String userId;

        public ViewAccessed(String viewName, String userId) {
            this.ts = LocalDateTime.now();
            this.viewName = viewName;
            this.userId = userId;
        }

        @Override
        public LocalDateTime timestamp() { return ts; }

        @Override
        public String eventType() { return "VIEW_ACCESSED"; }

        public String viewName() { return viewName; }
        public String userId() { return userId; }

        @Override
        public String toString() {
            return String.format("ViewAccessed[view=%s, user=%s]", viewName, userId);
        }
    }

    // =========================================================================
    // Data Modification Event
    // =========================================================================

    /**
     * Data was modified (create/update/delete).
     */
    final class DataModified implements ApplicationEvent, Serializable {
        private final LocalDateTime ts;
        private final String entityType;
        private final String operation;  // CREATE, UPDATE, DELETE
        private final Object entityId;

        public DataModified(String entityType, String operation, Object entityId) {
            this.ts = LocalDateTime.now();
            this.entityType = entityType;
            this.operation = operation;
            this.entityId = entityId;
        }

        @Override
        public LocalDateTime timestamp() { return ts; }

        @Override
        public String eventType() { return "DATA_MODIFIED"; }

        public String entityType() { return entityType; }
        public String operation() { return operation; }
        public Object entityId() { return entityId; }

        @Override
        public String toString() {
            return String.format("DataModified[%s %s id=%s]", operation, entityType, entityId);
        }
    }

    // =========================================================================
    // Error Event
    // =========================================================================

    /**
     * An error occurred.
     */
    final class Error implements ApplicationEvent, Serializable {
        private final LocalDateTime ts;
        private final String errorType;
        private final String message;
        private final Throwable cause;

        public Error(String errorType, String message, Throwable cause) {
            this.ts = LocalDateTime.now();
            this.errorType = errorType;
            this.message = message;
            this.cause = cause;
        }

        @Override
        public LocalDateTime timestamp() { return ts; }

        @Override
        public String eventType() { return "ERROR"; }

        public String errorType() { return errorType; }
        public String message() { return message; }
        public Throwable cause() { return cause; }

        @Override
        public String toString() {
            return String.format("Error[%s: %s]", errorType, message);
        }
    }

    // =========================================================================
    // Performance Metric Event
    // =========================================================================

    /**
     * Performance metric measurement.
     */
    final class Performance implements ApplicationEvent, Serializable {
        private final LocalDateTime ts;
        private final String metricName;
        private final double value;
        private final String unit;

        public Performance(String metricName, double value, String unit) {
            this.ts = LocalDateTime.now();
            this.metricName = metricName;
            this.value = value;
            this.unit = unit;
        }

        @Override
        public LocalDateTime timestamp() { return ts; }

        @Override
        public String eventType() { return "PERFORMANCE"; }

        public String metricName() { return metricName; }
        public double value() { return value; }
        public String unit() { return unit; }

        @Override
        public String toString() {
            return String.format("Performance[%s=%.2f%s]", metricName, value, unit);
        }
    }
}

