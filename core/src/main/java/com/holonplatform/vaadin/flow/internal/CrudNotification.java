package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.Operation;
import com.holonplatform.vaadin.flow.components.Components;

public class CrudNotification {
    public static void insertNotification(Operation operation) {

        showNotification(operation,"Insertion","insert");
    }

    private static void showNotification(Operation operation,String message, String messageCode) {
        operation.execute(aBoolean -> {
            if (aBoolean) {
                successNotification(message + " successful",messageCode +".successful.code");
                operation.executeMethod();
            } else {
                failureNotification(message + " Unsuccessful",messageCode +".unsuccessful.code");
            }
        });
    }

    static void failureNotification(String message, String messageCode) {
        Components.notification()
                .error()
                .autoClose(false)
                .text(Localizable.of(message,messageCode))
                .open();
    }

    static void successNotification(String message, String messageCode) {
        Components.notification()
                .success()
                .autoClose()
                .text(Localizable.of(message,messageCode))
                .open();
    }

    public static void updateNotification(Operation operation) {
        showNotification(operation,"Update","update");
    }

    public static void saveNotification(Operation operation) {
        showNotification(operation,"Save","save");
    }}
