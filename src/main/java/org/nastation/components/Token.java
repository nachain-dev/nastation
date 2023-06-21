package org.nastation.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.nastation.common.util.CompUtil;
import org.nastation.components.layout.size.Left;
import org.nastation.components.layout.size.Right;
import org.nastation.components.style.FontSize;
import org.nastation.components.style.LumoStyles;
import org.nastation.components.style.TextColor;
import org.nastation.components.style.css.BorderRadius;
import org.nastation.components.style.css.Display;

public class Token extends FlexBoxLayout {

	private final String CLASS_NAME = "token";

	public Token(String text) {
		setAlignItems(Alignment.CENTER);
		setBackgroundColor(LumoStyles.Color.Primary._10);
		setBorderRadius(BorderRadius.M);
		setClassName(CLASS_NAME);
		setDisplay(Display.INLINE_FLEX);
		setPadding(Left.S, Right.XS);
		setSpacing(Right.XS);

		Label label = CompUtil.createLabel(FontSize.S, TextColor.BODY, text);
		Button button = CompUtil.createButton(VaadinIcon.CLOSE_SMALL, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
		add(label, button);
	}

}
