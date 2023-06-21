package org.nastation.module.wallet.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = MiningFormView.Route_Value, layout = MainLayout.class)
@PageTitle(MiningFormView.Page_Title)
public class MiningFormView extends VerticalLayout {

    public static final String Route_Value = "MiningForm";
    public static final String Page_Title = "Mining";

    private PasswordField password;

    private Button startBtn;
    private Button stopBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    @Autowired
    private WalletService walletService;

    public MiningFormView() {
        addClassName("creat-wallet-form-view");

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        RadioButtonGroup<String> modeRgb = new RadioButtonGroup<>();
        modeRgb.setItems("POW-FLOW", "DPOS");
        modeRgb.setLabel("MODE");

        password = new PasswordField("Password");

        startBtn = new Button("Start");
        startBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        stopBtn = new Button("Stop");
        stopBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        //------ form
        FormLayout formLayout = new FormLayout(title,modeRgb, password, startBtn,stopBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(startBtn, 2);
        formLayout.setColspan(stopBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);

        startBtn.addClickListener(e -> {

            try {
                Wallet update = walletService.getWalletRepository().save(binder.getBean());
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            clearForm();

        });


    }

    private void clearForm() {
        //binder.setBean(new Wallet());
    }


}
