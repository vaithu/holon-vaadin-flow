package com.holonplatform.vaadin.flow;

import java.util.function.Consumer;

public interface Operation {
    void execute(Consumer<Boolean> result);

    void executeMethod();

}
