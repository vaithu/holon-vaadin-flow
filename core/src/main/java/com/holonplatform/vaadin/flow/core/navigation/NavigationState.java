package com.holonplatform.vaadin.flow.core.navigation;

import java.io.Serializable;

/**
 * Sealed interface for navigation state management.
 *
 * Enables type-safe navigation handling with exhaustive pattern matching.
 * Use for routing, navigation history, and state transitions.
 *
 * Example:
 * <pre>{@code
 * void handleNavigation(NavigationState state) {
 *     String action = switch (state) {
 *         case NavigationState.ViewEntered v ->
 *             "Entering view: " + v.viewPath();
 *         case NavigationState.ViewExited v ->
 *             "Exiting view: " + v.viewPath();
 *         case NavigationState.ParameterChanged p ->
 *             "Parameter changed: " + p.parameterName();
 *         case NavigationState.NavigationFailed f ->
 *             "Navigation failed: " + f.errorMessage();
 *     };
 *     log.info(action);
 * }
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface NavigationState permits
    NavigationState.ViewEntered,
    NavigationState.ViewExited,
    NavigationState.ParameterChanged,
    NavigationState.NavigationFailed {

    /**
     * Get the view path associated with this navigation state.
     *
     * @return view path (e.g., "/products", "/customers")
     */
    String viewPath();

    /**
     * Get the state type name.
     *
     * @return state type (VIEW_ENTERED, VIEW_EXITED, etc.)
     */
    String stateType();

    // =========================================================================
    // View Entered
    // =========================================================================

    /**
     * User entered a view.
     */
    final class ViewEntered implements NavigationState, Serializable {
        private final String viewPath;
        private final java.util.Map<String, String> parameters;

        public ViewEntered(String viewPath, java.util.Map<String, String> parameters) {
            this.viewPath = viewPath;
            this.parameters = parameters;
        }

        @Override
        public String viewPath() { return viewPath; }

        @Override
        public String stateType() { return "VIEW_ENTERED"; }

        public java.util.Map<String, String> parameters() { return parameters; }

        @Override
        public String toString() {
            return String.format("ViewEntered[%s]", viewPath);
        }
    }

    // =========================================================================
    // View Exited
    // =========================================================================

    /**
     * User exited a view.
     */
    final class ViewExited implements NavigationState, Serializable {
        private final String viewPath;
        private final String nextViewPath;

        public ViewExited(String viewPath, String nextViewPath) {
            this.viewPath = viewPath;
            this.nextViewPath = nextViewPath;
        }

        @Override
        public String viewPath() { return viewPath; }

        @Override
        public String stateType() { return "VIEW_EXITED"; }

        public String nextViewPath() { return nextViewPath; }

        @Override
        public String toString() {
            return String.format("ViewExited[%s → %s]", viewPath, nextViewPath);
        }
    }

    // =========================================================================
    // Parameter Changed
    // =========================================================================

    /**
     * URL parameter changed (e.g., ID in detail view).
     */
    final class ParameterChanged implements NavigationState, Serializable {
        private final String viewPath;
        private final String parameterName;
        private final Object oldValue;
        private final Object newValue;

        public ParameterChanged(String viewPath, String parameterName,
                               Object oldValue, Object newValue) {
            this.viewPath = viewPath;
            this.parameterName = parameterName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public String viewPath() { return viewPath; }

        @Override
        public String stateType() { return "PARAMETER_CHANGED"; }

        public String parameterName() { return parameterName; }
        public Object oldValue() { return oldValue; }
        public Object newValue() { return newValue; }

        @Override
        public String toString() {
            return String.format("ParameterChanged[%s.%s: %s → %s]",
                viewPath, parameterName, oldValue, newValue);
        }
    }

    // =========================================================================
    // Navigation Failed
    // =========================================================================

    /**
     * Navigation failed (access denied, not found, etc.).
     */
    final class NavigationFailed implements NavigationState, Serializable {
        private final String viewPath;
        private final String errorCode;
        private final String errorMessage;
        private final Exception cause;

        public NavigationFailed(String viewPath, String errorCode,
                               String errorMessage, Exception cause) {
            this.viewPath = viewPath;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.cause = cause;
        }

        @Override
        public String viewPath() { return viewPath; }

        @Override
        public String stateType() { return "NAVIGATION_FAILED"; }

        public String errorCode() { return errorCode; }
        public String errorMessage() { return errorMessage; }
        public Exception cause() { return cause; }

        @Override
        public String toString() {
            return String.format("NavigationFailed[%s: %s (%s)]",
                viewPath, errorCode, errorMessage);
        }
    }
}

