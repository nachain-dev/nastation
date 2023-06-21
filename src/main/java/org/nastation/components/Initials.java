package org.nastation.components;

import org.nastation.common.util.CompUtil;
import org.nastation.components.style.FontSize;
import org.nastation.components.style.FontWeight;
import org.nastation.components.style.LumoStyles;
import org.nastation.components.style.css.BorderRadius;

public class Initials extends FlexBoxLayout {

	private String CLASS_NAME = "initials";

	public Initials(String initials) {
		setAlignItems(Alignment.CENTER);
		setBackgroundColor(LumoStyles.Color.Contrast._10);
		setBorderRadius(BorderRadius.L);
		setClassName(CLASS_NAME);
		CompUtil.setFontSize(FontSize.S, this);
		CompUtil.setFontWeight(FontWeight._600, this);
		setHeight(LumoStyles.Size.M);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setWidth(LumoStyles.Size.M);

		add(initials);
	}

}
