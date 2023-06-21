package org.nastation.components.navigation.tab;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.nastation.common.util.CompUtil;
import org.nastation.components.style.FontSize;

public class ClosableNaviTab extends NaviTab {

	private final Button close;

	public ClosableNaviTab(String label,
	                       Class<? extends Component> navigationTarget) {
		super(label, navigationTarget);
		getElement().setAttribute("closable", true);

		close = CompUtil.createButton(VaadinIcon.CLOSE, ButtonVariant.LUMO_TERTIARY_INLINE);
		// ButtonVariant.LUMO_SMALL isn't small enough.
		CompUtil.setFontSize(FontSize.XXS, close);
		add(close);
	}

	public Button getCloseButton() {
		return close;
	}
}
