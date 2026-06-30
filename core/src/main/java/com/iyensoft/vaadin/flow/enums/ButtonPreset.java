package com.iyensoft.vaadin.flow.enums;

public enum ButtonPreset {
    SAVE("Save"),
    EDIT("Edit"),
    DELETE("Delete"),
    REFRESH("Refresh"),
    DUPLICATE("Duplicate"),
    EXPORT("Export"),
    IMPORT("Import"),
    RESET("Reset"),
    CANCEL("Cancel"),
    CLOSE("Close"),
    NEW("New");

    private final String defaultText;

    ButtonPreset(String defaultText) {
        this.defaultText = defaultText;
    }

    public String defaultText() {
        return defaultText;
    }
}