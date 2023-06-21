package org.nastation.components.detailsdrawer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tabs;
import org.nastation.common.util.CompUtil;
import org.nastation.components.FlexBoxLayout;
import org.nastation.components.layout.size.Horizontal;
import org.nastation.components.layout.size.Right;
import org.nastation.components.layout.size.Vertical;
import org.nastation.components.style.BoxShadowBorders;

public class DetailsDrawerHeader extends FlexBoxLayout {

	private Button close;
	private Label title;

	public DetailsDrawerHeader(String title) {
		addClassName(BoxShadowBorders.BOTTOM);
		setFlexDirection(FlexDirection.COLUMN);
		setWidthFull();

		this.close = CompUtil.createTertiaryInlineButton(VaadinIcon.CLOSE);
		CompUtil.setLineHeight("1", this.close);

		this.title = CompUtil.createH4Label(title);

		FlexBoxLayout wrapper = new FlexBoxLayout(this.close, this.title);
		wrapper.setAlignItems(Alignment.CENTER);
		wrapper.setPadding(Horizontal.RESPONSIVE_L, Vertical.M);
		wrapper.setSpacing(Right.L);
		add(wrapper);
	}

	public DetailsDrawerHeader(String title, Tabs tabs) {
		this(title);
		add(tabs);
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void addCloseListener(ComponentEventListener<ClickEvent<Button>> listener) {
		this.close.addClickListener(listener);
	}

}
