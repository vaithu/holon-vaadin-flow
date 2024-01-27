package com.holonplatform.vaadin.flow.components.css;

public enum BadgeSize {

	S("small"), M("medium");

	private String style;

	BadgeSize(String style) {
		this.style = style;
	}

	public String getThemeName() {
		return style;
	}

}