package org.nastation.module.devstore.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

@JsModule("./views/devstore/image-card.ts")
@Tag("devstore-image-card")
public class DevStoreImageCard extends LitTemplate {

    @Id
    private Image image;

    @Id
    private Span innerHeader;

    @Id
    private Span innerSubtitle;

    @Id
    private Paragraph text;

    @Id
    private Span badge;

    public DevStoreImageCard(String text, String url) {
        this.image.setSrc(url);
        this.image.setAlt(text);
        this.innerHeader.setText("Title");
        this.innerSubtitle.setText("Card subtitle");
        this.text.setText(
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut.");
        this.badge.setText("Label");
    }
}
