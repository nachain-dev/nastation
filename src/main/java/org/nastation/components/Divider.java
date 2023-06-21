package org.nastation.components;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import org.nastation.common.util.CompUtil;
import org.nastation.components.style.LumoStyles;

public class Divider extends FlexBoxLayout implements HasSize, HasStyle {

	private String CLASS_NAME = "divider";

	private Div divider;

	public Divider(String height) {
		this(FlexComponent.Alignment.CENTER, height);
	}

	public Divider(FlexComponent.Alignment alignItems, String height) {
		setAlignItems(alignItems);
		setClassName(CLASS_NAME);
		setHeight(height);

		divider = new Div();
		CompUtil.setBackgroundColor(LumoStyles.Color.Contrast._10, divider);
		divider.setHeight("1px");
		divider.setWidthFull();
		add(divider);
	}

}
