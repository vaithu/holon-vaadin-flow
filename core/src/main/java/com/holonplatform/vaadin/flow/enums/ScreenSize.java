package com.holonplatform.vaadin.flow.enums;

public enum ScreenSize {

    MOBILE ("Mobile"),
    MOBILE_LANDSCAPE ("Mobile_Landscape"),
    MOBILE_PORTRAIT ("Mobile_Portrait"),
    TABLET("Tablet"),
    DESKTOP("Desktop");

    private final String mode;


    ScreenSize(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "ScreenSize{" +
                "mode='" + mode + '\'' +
                '}';
    }
}
