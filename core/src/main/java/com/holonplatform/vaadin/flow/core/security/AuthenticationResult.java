package com.holonplatform.vaadin.flow.core.security;

import java.io.Serializable;

/**
 * Sealed interface for authentication results.
 *
 * Enables type-safe authentication handling with exhaustive pattern matching.
 * Use for login flows, permission checks, and security logging.
 *
 * Example:
 * <pre>{@code
 * void handleAuth(AuthenticationResult result) {
 *     String outcome = switch (result) {
 *         case AuthenticationResult.Authenticated a ->
 *             "User: " + a.userId() + ", Role: " + a.role();
 *         case AuthenticationResult.InvalidCredentials _ ->
 *             "Invalid username or password";
 *         case AuthenticationResult.AccountLocked a ->
 *             "Account locked for " + a.minutesRemaining() + " minutes";
 *         case AuthenticationResult.RequiresMFA _ ->
 *             "MFA required";
 *     };
 *     log.info(outcome);
 * }
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface AuthenticationResult permits
    AuthenticationResult.Authenticated,
    AuthenticationResult.InvalidCredentials,
    AuthenticationResult.AccountLocked,
    AuthenticationResult.RequiresMFA {

    /**
     * Get the result type name.
     *
     * @return type (AUTHENTICATED, INVALID, LOCKED, MFA_REQUIRED)
     */
    String resultType();

    /**
     * Whether authentication succeeded.
     *
     * @return true if authenticated, false otherwise
     */
    boolean isAuthenticated();

    // =========================================================================
    // Authenticated
    // =========================================================================

    /**
     * Authentication succeeded.
     */
    final class Authenticated implements AuthenticationResult, Serializable {
        private final String userId;
        private final String username;
        private final String role;
        private final String token;
        private final long expiresAt;

        public Authenticated(String userId, String username, String role,
                           String token, long expiresAt) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.token = token;
            this.expiresAt = expiresAt;
        }

        @Override
        public String resultType() { return "AUTHENTICATED"; }

        @Override
        public boolean isAuthenticated() { return true; }

        public String userId() { return userId; }
        public String username() { return username; }
        public String role() { return role; }
        public String token() { return token; }
        public long expiresAt() { return expiresAt; }

        @Override
        public String toString() {
            return String.format("Authenticated[user=%s, role=%s]", username, role);
        }
    }

    // =========================================================================
    // Invalid Credentials
    // =========================================================================

    /**
     * Authentication failed: invalid credentials.
     */
    final class InvalidCredentials implements AuthenticationResult, Serializable {
        private final String username;
        private final String attemptType;  // "password", "mfa", "certificate"

        public InvalidCredentials(String username, String attemptType) {
            this.username = username;
            this.attemptType = attemptType;
        }

        @Override
        public String resultType() { return "INVALID_CREDENTIALS"; }

        @Override
        public boolean isAuthenticated() { return false; }

        public String username() { return username; }
        public String attemptType() { return attemptType; }

        @Override
        public String toString() {
            return String.format("InvalidCredentials[%s, %s attempt]",
                username, attemptType);
        }
    }

    // =========================================================================
    // Account Locked
    // =========================================================================

    /**
     * Authentication failed: account is locked.
     */
    final class AccountLocked implements AuthenticationResult, Serializable {
        private final String userId;
        private final String reason;
        private final int minutesRemaining;

        public AccountLocked(String userId, String reason, int minutesRemaining) {
            this.userId = userId;
            this.reason = reason;
            this.minutesRemaining = minutesRemaining;
        }

        @Override
        public String resultType() { return "ACCOUNT_LOCKED"; }

        @Override
        public boolean isAuthenticated() { return false; }

        public String userId() { return userId; }
        public String reason() { return reason; }
        public int minutesRemaining() { return minutesRemaining; }

        @Override
        public String toString() {
            return String.format("AccountLocked[%s for %d min, reason: %s]",
                userId, minutesRemaining, reason);
        }
    }

    // =========================================================================
    // Requires MFA
    // =========================================================================

    /**
     * Authentication requires multi-factor authentication.
     */
    final class RequiresMFA implements AuthenticationResult, Serializable {
        private final String userId;
        private final String mfaMethod;  // "email", "sms", "authenticator"
        private final String sessionToken;

        public RequiresMFA(String userId, String mfaMethod, String sessionToken) {
            this.userId = userId;
            this.mfaMethod = mfaMethod;
            this.sessionToken = sessionToken;
        }

        @Override
        public String resultType() { return "REQUIRES_MFA"; }

        @Override
        public boolean isAuthenticated() { return false; }

        public String userId() { return userId; }
        public String mfaMethod() { return mfaMethod; }
        public String sessionToken() { return sessionToken; }

        @Override
        public String toString() {
            return String.format("RequiresMFA[user=%s, method=%s]",
                userId, mfaMethod);
        }
    }
}

