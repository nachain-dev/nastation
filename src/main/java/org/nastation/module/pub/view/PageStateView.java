package org.nastation.module.pub.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.components.Images;
import org.nastation.module.pub.data.PageState;

import java.util.List;
import java.util.Map;

@Route(value = PageStateView.Route_Value)
@PageTitle(PageStateView.Page_Title)
@Slf4j
public class PageStateView extends VerticalLayout implements HasUrlParameter<String> {
    public static final String Route_Value = "PageStateView";
    public static final String Page_Title = "Page state info";

    private String tip = "Unknown";
    private String type;

    private H3 pageInfo;
    private H5 errorInfo;

    public PageStateView() {

        this.getStyle()
                .set("display", "block")
                .set("padding-top", "2em")
                .set("text-align", "center");

        Image logo = Images.nac_72x72();
        add(logo);

        H2 title = new H2("NaStation");
        title.getStyle().set("margin-top", "0.5em");
        add(title);

        pageInfo = new H3("An exception occurred");

        errorInfo = new H5("Error message: " + tip);
        errorInfo.getStyle().set("color", "#e64347");

        add(pageInfo);
        add(errorInfo);

        Button backHome = new Button("Home");
        backHome.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        backHome.addClickListener(e -> {
            UI.getCurrent().navigate(HomeView.class);
        });

        Button refresh = new Button("Refresh");
        refresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        refresh.addClickListener(e -> {
            UI.getCurrent().getPage().reload();
        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        btnLayout.setSpacing(true);
        btnLayout.setPadding(true);
        btnLayout.add(backHome, refresh);

        add(btnLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String type) {

        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        List<String> typeList = parametersMap.get("type");
        if (typeList != null) {
            String value = typeList.get(0);
            if (StringUtils.isNotEmpty(value)) {
                tip = PageState.getNameByValue(Integer.valueOf(value));
            }
        }
        errorInfo.setText("Error message: " + tip);
    }


}
