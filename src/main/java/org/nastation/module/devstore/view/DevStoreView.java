package org.nastation.module.devstore.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.components.Images;
import org.nastation.data.config.AppConfig;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import static org.nastation.module.devstore.view.DevStoreView.Page_Title;
import static org.nastation.module.devstore.view.DevStoreView.Route_Value;


@PageTitle(Page_Title)
@Route(value = Route_Value, layout = MainLayout.class)
public class DevStoreView extends VerticalLayout {

    public static final String Route_Value = "DevStoreView";
    public static final String Page_Title = "DevStore";

    public DevStoreView(@Autowired AppConfig appConfig) {
        addClassName("home-view");

        Image logo = Images.nac_72x72();
        add(logo);

        H1 title = new H1(Page_Title);
        title.getStyle().set("margin-top", "0");
        add(title);

        add(new Html("<p>Create and publish custom NA apps, extensions, smart contracts</p>"));

        Button openBtn = new Button("Open " + Page_Title);
        openBtn.addClickListener(e -> {

            getUI().ifPresent(ui -> ui.getPage().executeJs(
                    "if ($1 == '_self') this.stopApplication(); window.open($0, $1, $2)",
                    "https://store.nachain.org/devstore/home", Page_Title, "height=600, width=1280"));

            //new AdvancedBrowser("https://www.nachain.org/");
        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        btnLayout.setSizeUndefined();
        btnLayout.setSpacing(true);
        btnLayout.setPadding(true);
        btnLayout.add(openBtn);

        add(btnLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //UI ui = attachEvent.getUI();
    }
}