/*
 * Copyright 2024 Haulmont.
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

package com.holonplatform.vaadin.flow.spring.boot.jmix.asynctask;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The class provides methods for executing asynchronous tasks from UI views. The class may be used when, in the UI
 * view, you need to execute some long-running task in a separate thread, for example, loading data from a database, and
 * when the task is completed (data is loaded), update the UI using the returned data.
 * <p>
 * Use {@link UiAsyncTasks} when you don't need to handle asynchronous task progress or display a modal dialog with a
 * progress indicator.
 * <p>
 * The class wraps actions that perform tasks (for example, loading data) into a special wrapper
 * ({@link DelegatingSecuritySupplier} or {@link DelegatingSecurityRunnable}). These wrappers set the original
 * {@link org.springframework.security.core.context.SecurityContext} before executing the action. After that, all
 * services invoked in the asynchronous task will be executed with the permissions of the current user.
 * <p>
 * Actions that process the result can update Vaadin UI components because they are wrapped by the
 * {@link UI#access(Command)} method.
 * <p>
 * Usage example:
 * <pre>{@code
 * uiAsyncTasks.supplierConfigurer(() -> customerService.loadCustomers())
 *      .withResultHandler(customers -> {
 *          customersDc.getMutableItems().addAll(customers);
 *          notifications.create("Customers loaded: " + customers.size()).show();
 *      })
 *      .withExceptionHandler(ex -> {
 *          errorTextField.setValue(ex.getMessage());
 *      })
 *      .supplyAsync();}</pre>
 * <p>
 * By default, asynchronous tasks are executed with the default timeout configured by the
 * {@link UiAsyncTaskProperties#defaultTimeoutSec}. After this timeout, the {@link TimeoutException} is thrown.
 * <p>
 * If asynchronous task doesn't return any result, use the {@link #runnableConfigurer(Runnable)} builder for configuring
 * and running the task.
 *
 * @see UiAsyncTaskProperties
 * @see #supplierConfigurer(Supplier)
 * @see #runnableConfigurer(Runnable)
 */
@Component("flowui_UiAsyncTasks")
public class UiAsyncTasks {

    private static final Logger log = LoggerFactory.getLogger(UiAsyncTasks.class);

    private static final String THREAD_NAME_PREFIX = "UiAsyncTask-";

    private ExecutorService executorService;

    private Consumer<Throwable> defaultExceptionHandler;

    private final UiAsyncTaskProperties uiAsyncTaskProperties;

    public UiAsyncTasks(UiAsyncTaskProperties uiAsyncTaskProperties) {
        this.uiAsyncTaskProperties = uiAsyncTaskProperties;
    }

    @PostConstruct
    protected void onInit() {
        executorService = createExecutorService();
        defaultExceptionHandler = createDefaultExceptionHandler();
    }

    @PreDestroy
    protected void preDestroy() {
        executorService.shutdownNow();
    }

    /**
     * Creates a builder for an asynchronous task that returns a result. The task is executed on a separate thread. The
     * result can be handled with a {@link Consumer} that is set using the
     * {@link SupplierConfigurer#withResultHandler(Consumer)} or left unhandled to get the result using the
     * {@link CompletableFuture} API.
     *
     * @param asyncTask the task to execute
     * @param <T>       the result type
     * @return a builder for an asynchronous task that returns a result
     */
    public <T> SupplierConfigurer<T> supplierConfigurer(Supplier<T> asyncTask) {
        return new SupplierConfigurer<>(asyncTask);
    }

    /**
     * Creates a builder for an asynchronous task that does not return a result. The task is executed on a separate
     * thread. The action that must be performed after the asynchronous task is completed may be set using the
     * {@link RunnableConfigurer#withResultHandler(Runnable)} method.
     *
     * @param asyncTask the task to execute
     * @return a builder for an asynchronous task that does not return a result
     */
    public RunnableConfigurer runnableConfigurer(Runnable asyncTask) {
        return new RunnableConfigurer(asyncTask);
    }

    /**
     * Abstract base class for asynchronous task builders.
     */
    protected abstract class AbstractAsyncTaskConfigurer {
        protected Consumer<Throwable> exceptionHandler;
        protected UI ui;
        protected int timeout;
        protected TimeUnit timeoutUnit;

        public AbstractAsyncTaskConfigurer() {
            this.ui = UI.getCurrent();
            this.exceptionHandler = defaultExceptionHandler;
        }

        protected void configureTimeout(CompletableFuture<Void> resultCompletableFuture) {
            if (timeout > 0) {
                resultCompletableFuture.orTimeout(timeout, timeoutUnit != null ? timeoutUnit : TimeUnit.SECONDS);
            } else {
                int defaultTimeoutSec = uiAsyncTaskProperties.getDefaultTimeoutSec();
                if (defaultTimeoutSec > 0) {
                    resultCompletableFuture.orTimeout(defaultTimeoutSec, TimeUnit.SECONDS);
                }
            }
        }

        protected void configureExceptionHandler(CompletableFuture<Void> completableFuture) {
            Consumer<Throwable> handler = exceptionHandler;
            completableFuture.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    ui.access(() -> handler.accept(throwable));
                }
            });
        }
    }

    /**
     * A builder class for configuring and executing asynchronous tasks that return a result.
     *
     * @param <T> the result type of the asynchronous task
     */
    public class SupplierConfigurer<T> extends AbstractAsyncTaskConfigurer {
        private Supplier<T> asyncTask;
        private Consumer<? super T> resultHandler;

        public SupplierConfigurer(Supplier<T> asyncTask) {
            super();
            this.asyncTask = asyncTask;
        }

        /**
         * Sets a handler that is called when the asynchronous task completes successfully. The handler can safely
         * update Vaadin UI components.
         */
        public SupplierConfigurer<T> withResultHandler(Consumer<? super T> resultHandler) {
            this.resultHandler = resultHandler;
            return this;
        }

        /**
         * Sets a handler that is called when the asynchronous task throws an exception. The handler can safely update
         * Vaadin UI components. If not set, then the default exception handler will be used.
         */
        public SupplierConfigurer<T> withExceptionHandler(Consumer<Throwable> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Sets the timeout for the asynchronous task. If the task is not completed within the specified timeout, a
         * {@link TimeoutException} is thrown.
         */
        public SupplierConfigurer<T> withTimeout(int timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        /**
         * Configures and returns a {@link CompletableFuture} that asynchronously executes the task.
         */
        public CompletableFuture<Void> supplyAsync() {
            DelegatingSecuritySupplier<T> wrappedSupplier = new DelegatingSecuritySupplier<>(asyncTask);
            CompletableFuture<T> future = CompletableFuture.supplyAsync(wrappedSupplier, executorService);
            CompletableFuture<Void> resultCompletableFuture;

            if (resultHandler != null) {
                resultCompletableFuture = future.thenAccept(data -> ui.access(() -> resultHandler.accept(data)));
            } else {
                resultCompletableFuture = future.thenAccept(t -> {
                });
            }

            configureExceptionHandler(resultCompletableFuture);
            configureTimeout(resultCompletableFuture);

            return resultCompletableFuture;
        }
    }

    /**
     * A builder class for configuring and executing asynchronous tasks that do not return a result.
     */
    public class RunnableConfigurer extends AbstractAsyncTaskConfigurer {

        private Runnable asyncTask;
        private Runnable resultHandler;

        public RunnableConfigurer(Runnable asyncTask) {
            super();
            this.asyncTask = asyncTask;
        }

        /**
         * Sets a handler that is called when the asynchronous task completes successfully. The handler can safely
         * update Vaadin UI components.
         */
        public RunnableConfigurer withResultHandler(Runnable resultHandler) {
            this.resultHandler = resultHandler;
            return this;
        }

        /**
         * Sets a handler that is called when the asynchronous task throws an exception. The handler can safely update
         * Vaadin UI components. If not set, then the default exception handler will be used.
         */
        public RunnableConfigurer withExceptionHandler(Consumer<Throwable> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Sets the timeout for the asynchronous task. If the task is not completed within the specified timeout, a
         * {@link TimeoutException} is thrown.
         */
        public RunnableConfigurer withTimeout(int timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        /**
         * Configures and returns a {@link CompletableFuture} that asynchronously executes the task.
         */
        public CompletableFuture<Void> runAsync() {
            DelegatingSecurityRunnable wrappedRunnable = new DelegatingSecurityRunnable(asyncTask);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(wrappedRunnable, executorService);
            if (resultHandler != null) {
                completableFuture = completableFuture.thenRun(() -> ui.access(resultHandler::run));
            }
            configureExceptionHandler(completableFuture);
            configureTimeout(completableFuture);
            return completableFuture;
        }
    }

    protected ExecutorService createExecutorService() {
        int maximumPoolSize = uiAsyncTaskProperties.getExecutorService().getMaximumPoolSize();
        executorService = new ThreadPoolExecutor(
                maximumPoolSize,
                maximumPoolSize,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat(THREAD_NAME_PREFIX + "%d")
                        .build()
        );
        ((ThreadPoolExecutor) executorService).allowCoreThreadTimeOut(true);
        return executorService;
    }

    protected Consumer<Throwable> createDefaultExceptionHandler() {
        return throwable -> {
            if (throwable instanceof TimeoutException) {
                log.error("UI async task finished on timeout");
            } else {
                log.error("UI async task error", throwable);
            }
        };
    }

    public void setDefaultExceptionHandler(Consumer<Throwable> defaultExceptionHandler) {
        this.defaultExceptionHandler = defaultExceptionHandler;
    }
}