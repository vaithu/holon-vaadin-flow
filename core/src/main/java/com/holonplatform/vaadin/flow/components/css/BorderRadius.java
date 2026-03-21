package com.holonplatform.vaadin.flow.components.css;

public enum BorderRadius {

	S("var(--lumo-border-radius-s)"), M("var(--lumo-border-radius-m)"), L(
			"var(--lumo-border-radius-l)"),

	_50("50%");

	private final String value;

	BorderRadius(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}