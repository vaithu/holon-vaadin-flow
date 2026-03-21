package com.holonplatform.vaadin.flow;

import java.util.function.Consumer;

@FunctionalInterface
public interface DeleteOperation {
    void execute(Consumer<Boolean> result, Runnable postProcessor);
}
