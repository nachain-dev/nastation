package org.nastation.module.pub.view;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = WsApiView.Route_Value, layout = MainLayout.class)
@PageTitle(WsApiView.Page_Title)
public class WsApiView extends VerticalLayout {

    public static final String Route_Value = "WsApiView";
    public static final String Page_Title = "Websocket Api";

    public WsApiView() {
        addClassName("app-about-view-view");

        /* main */
        Pre pre_main = new Pre();
        pre_main.getStyle().set("text-align", "left");
        pre_main.setText("");

        Details component = new Details("1. Get the wallet network main info: ws://localhost/test/v1/main", pre_main);
        component.setOpened(true);
        component.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
        add(component);

        /* address */
        Pre address = new Pre();
        address.getStyle().set("text-align", "left");
        address.setText("");

        Details component_address = new Details("2. Get detailed info about a public address: ws://localhost/test/v1/addr", address);
        component_address.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
        add(component_address);

        /* tx */
        Pre tx = new Pre();
        tx.getStyle().set("text-align", "left");
        tx.setText("");

        Details component_tx = new Details("3. Get detailed info about tx hash: ws://localhost/test/v1/tx", tx);
        component_tx.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
        add(component_tx);

    }


}
