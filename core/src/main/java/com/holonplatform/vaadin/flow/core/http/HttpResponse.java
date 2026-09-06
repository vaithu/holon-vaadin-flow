package com.holonplatform.vaadin.flow.core.http;

import java.io.Serializable;
import java.util.Map;

/**
 * Sealed interface for HTTP response types.
 *
 * Enables type-safe REST response handling with exhaustive pattern matching.
 * Use for API response parsing and error handling.
 *
 * Example:
 * <pre>{@code
 * void handleResponse(HttpResponse response) {
 *     String result = switch (response) {
 *         case HttpResponse.Success s ->
 *             "Success: " + s.statusCode();
 *         case HttpResponse.ClientError c ->
 *             "Client error: " + c.errorMessage();
 *         case HttpResponse.ServerError s ->
 *             "Server error: " + s.errorMessage();
 *         case HttpResponse.Timeout t ->
 *             "Timeout after " + t.milliseconds() + "ms";
 *     };
 *     log.info(result);
 * }
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface HttpResponse permits
    HttpResponse.Success,
    HttpResponse.ClientError,
    HttpResponse.ServerError,
    HttpResponse.Timeout {

    /**
     * Get the HTTP status code (if available).
     *
     * @return status code or -1 if not applicable
     */
    int statusCode();

    /**
     * Get the response type name.
     *
     * @return type (SUCCESS, CLIENT_ERROR, SERVER_ERROR, TIMEOUT)
     */
    String responseType();

    // =========================================================================
    // Success Response (2xx)
    // =========================================================================

    /**
     * Successful HTTP response (200-299).
     */
    final class Success implements HttpResponse, Serializable {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;

        public Success(int statusCode, String body, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        @Override
        public int statusCode() { return statusCode; }

        @Override
        public String responseType() { return "SUCCESS"; }

        public String body() { return body; }
        public Map<String, String> headers() { return headers; }

        @Override
        public String toString() {
            return String.format("Success[%d, %d bytes]", statusCode,
                body != null ? body.length() : 0);
        }
    }

    // =========================================================================
    // Client Error Response (4xx)
    // =========================================================================

    /**
     * Client error HTTP response (400-499).
     */
    final class ClientError implements HttpResponse, Serializable {
        private final int statusCode;
        private final String errorMessage;
        private final String errorCode;

        public ClientError(int statusCode, String errorCode, String errorMessage) {
            this.statusCode = statusCode;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        @Override
        public int statusCode() { return statusCode; }

        @Override
        public String responseType() { return "CLIENT_ERROR"; }

        public String errorCode() { return errorCode; }
        public String errorMessage() { return errorMessage; }

        @Override
        public String toString() {
            return String.format("ClientError[%d: %s]", statusCode, errorCode);
        }
    }

    // =========================================================================
    // Server Error Response (5xx)
    // =========================================================================

    /**
     * Server error HTTP response (500-599).
     */
    final class ServerError implements HttpResponse, Serializable {
        private final int statusCode;
        private final String errorMessage;
        private final Exception cause;

        public ServerError(int statusCode, String errorMessage, Exception cause) {
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.cause = cause;
        }

        @Override
        public int statusCode() { return statusCode; }

        @Override
        public String responseType() { return "SERVER_ERROR"; }

        public String errorMessage() { return errorMessage; }
        public Exception cause() { return cause; }

        @Override
        public String toString() {
            return String.format("ServerError[%d: %s]", statusCode, errorMessage);
        }
    }

    // =========================================================================
    // Timeout Response
    // =========================================================================

    /**
     * HTTP request timed out.
     */
    final class Timeout implements HttpResponse, Serializable {
        private final long milliseconds;
        private final String url;

        public Timeout(long milliseconds, String url) {
            this.milliseconds = milliseconds;
            this.url = url;
        }

        @Override
        public int statusCode() { return -1; }

        @Override
        public String responseType() { return "TIMEOUT"; }

        public long milliseconds() { return milliseconds; }
        public String url() { return url; }

        @Override
        public String toString() {
            return String.format("Timeout[%dms for %s]", milliseconds, url);
        }
    }
}

