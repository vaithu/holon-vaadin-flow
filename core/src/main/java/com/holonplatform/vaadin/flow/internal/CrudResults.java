package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.core.Initializer;

public interface CrudResults {

    void showDeleteResult(boolean success);

    void showInsertResult(boolean success);

    void showSaveResult(boolean success);

    void showUpdateResult(boolean success);


    void showDeleteResult(Initializer<Boolean> initializer);

    void showInsertResult(Initializer<Boolean> initializer);

    void showSaveResult(Initializer<Boolean> initializer);

    void showUpdateResult(Initializer<Boolean> initializer);

}
