package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.Operation;
import com.holonplatform.vaadin.flow.components.Components;


public class CrudDialogs {

    public static void deleteDialog(Operation deleteOperation) {
        Components.dialog.delete(confirmSelected -> {

                    if (confirmSelected) {
                        deleteOperation.execute(aBoolean -> {
                            if (aBoolean) {
                                CrudNotification.successNotification("Deletion successful","delete.successful.code");
                                deleteOperation.executeMethod();
                            } else {
                                CrudNotification.failureNotification("Deletion Unsuccessful","delete.unsuccessful.code");
                            }
                        });
                    }

                })
                .withTitle(Localizable.of("Delete", "delete.code"))
                .withContent(Localizable.of("Do you want delete these selected items?", "delete.statement.code"))
                .build()
                .open();

    }

    /*public  static <T> void saveDialog(HasFormView<T> formView,) {

    }*/


}
