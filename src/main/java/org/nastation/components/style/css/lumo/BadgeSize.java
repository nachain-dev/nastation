package org.nastation.components.style.css.lumo;

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
