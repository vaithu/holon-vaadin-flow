package com.holonplatform.vaadin.flow.vaadinplus.utilities;

public class Font {

    private Font() {}

    public enum Size {
        XXSMALL("font-size-xxsmall"),
        XSMALL("font-size-xsmall"),
        SMALL("font-size-small"),
        MEDIUM("font-size-medium"),
        LARGE("font-size-large"),
        XLARGE("font-size-xlarge"),
        XXLARGE("font-size-xxlarge"),
        XXXLARGE("font-size-xxxlarge");

        private final String className;

        Size(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }

    public enum Weight {
        THIN("font-weight-thin"),
        EXTRALIGHT("font-weight-extralight"),
        LIGHT("font-weight-light"),
        NORMAL("font-weight-normal"),
        MEDIUM("font-weight-medium"),
        SEMIBOLD("font-weight-semibold"),
        BOLD("font-weight-bold"),
        EXTRABOLD("font-weight-extrabold"),
        BLACK("font-weight-black");

        private final String className;

        Weight(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }

    public enum LineHeight {
        NONE("line-height-none"),
        XSMALL("line-height-xsmall"),
        SMALL("line-height-small"),
        MEDIUM("line-height-medium");

        private final String className;

        LineHeight(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }

}