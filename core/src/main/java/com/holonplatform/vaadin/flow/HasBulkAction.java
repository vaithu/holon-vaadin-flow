package com.holonplatform.vaadin.flow;

import java.util.stream.Stream;

public interface HasBulkAction<T> {
    void delete(Stream<T> beanInstance);
}
