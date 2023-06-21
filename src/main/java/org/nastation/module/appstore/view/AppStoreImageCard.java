package org.nastation.module.appstore.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.RouteParameters;
import lombok.Data;

@JsModule("./views/appstore/image-card.ts")
@Tag("appstore-image-card")
@Data
public class AppStoreImageCard extends LitTemplate {

    private int detailId;

    @Id
    private Image image;

    @Id
    private Span innerHeader;

    @Id
    private Span badge;

    public AppStoreImageCard() {
        image.addClickListener(event -> {
            btnClickListener();
        });
        innerHeader.addClickListener(event -> {
            btnClickListener();
        });

    }

    public void btnClickListener() {
        image.getUI().ifPresent(ui -> ui.navigate(
                AppStoreDetailView.class,
                new RouteParameters("id", String.valueOf(detailId))));

        //UI.getCurrent().navigate(About.class)

    }


}
