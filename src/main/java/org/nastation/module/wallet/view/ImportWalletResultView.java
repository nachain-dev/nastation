package org.nastation.module.wallet.view;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.components.Images;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "ImportWalletResultView", layout = MainLayout.class)
@PageTitle("Import Wallet Status ")
public class ImportWalletResultView extends VerticalLayout {

    private Button nextBtn;

    private WalletService walletService;

    public ImportWalletResultView(@Autowired WalletService walletService) {
        this.walletService = walletService;

        this.getStyle()
                .set("display","block")
                .set("padding","5em")
                .set("text-align","center")
        ;

        H2 title = new H2("Wallet import successfully");
        title.getStyle().set("text-align", "center");

        Image icon = Images.success();
        icon.getStyle().set("width", "96px");

        Div iconWrap = new Div();
        iconWrap.setWidthFull();
        iconWrap.getStyle().set("text-align", "center");
        iconWrap.add(icon);

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Html desc = new Html("<div style='text-align:center'>xxxxxx</div>");

        //------ form
        FormLayout formLayout = new FormLayout(iconWrap,desc,title,nextBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(desc, 2);
        formLayout.setColspan(iconWrap, 2);

        add(formLayout);
        add(nextBtn);

        //------ action

        nextBtn.setIconAfterText(true);
        nextBtn.addClickListener(e -> {

            UI.getCurrent().navigate(WalletListView.class);
        });
    }

    private void clearForm() {
    }

}
