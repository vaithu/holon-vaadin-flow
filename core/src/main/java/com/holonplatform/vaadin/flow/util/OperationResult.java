/*
 * Copyright 2022 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.holonplatform.vaadin.flow.util;

import java.util.function.Supplier;

/**
 * Operation result object.
 */
public interface OperationResult {

    static OperationResult fail() {
        return FailedOperationResult.INSTANCE;
    }

    static OperationResult success() {
        return SuccessOperationResult.INSTANCE;
    }

    /** @return status of the operation */
    Status getStatus();

    /** @return {@code true} if this result represents a successful operation */
    default boolean isSuccess() {
        return getStatus() == Status.SUCCESS;
    }

    /**
     * Creates new operation result that represents composition of two operation results.
     * If this result is successful, the next step will be obtained from the passed supplier.
     *
     * @param nextStep the next operation result supplier
     * @return new composite operation result
     */
    OperationResult compose(Supplier<OperationResult> nextStep);

    /**
     * Runs {@code runnable} if this result is a success.
     *
     * @param runnable callback
     * @return this
     */
    OperationResult then(Runnable runnable);

    /**
     * Runs {@code runnable} if this result is a failure.
     *
     * @param runnable callback
     * @return this
     */
    OperationResult otherwise(Runnable runnable);

    /**
     * Fluent alias for {@link #then(Runnable)} — runs {@code action} on success.
     * Allows callers to write {@code result.andThen(onSuccess).orElse(onFail)}
     * instead of {@code result.then(onSuccess).otherwise(onFail)}.
     *
     * @param action callback
     * @return this
     */
    default OperationResult andThen(Runnable action) {
        return then(action);
    }

    /**
     * Fluent alias for {@link #otherwise(Runnable)} — runs {@code action} on failure.
     *
     * @param action callback
     * @return this
     */
    default OperationResult orElse(Runnable action) {
        return otherwise(action);
    }

    /**
     * Chains success-path continuation and failure callback in a single call,
     * eliminating boilerplate for the common {@code .then(ok).otherwise(err)} pattern.
     *
     * @param onSuccess run when successful
     * @param onFailure run when failed
     * @return this
     */
    default OperationResult handle(Runnable onSuccess, Runnable onFailure) {
        return then(onSuccess).otherwise(onFailure);
    }

    enum Status {
        UNKNOWN,
        SUCCESS,
        FAIL
    }
}