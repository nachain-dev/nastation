package org.nastation.module.node.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.nastation.common.util.CompUtil;
import org.nastation.module.node.service.FullNodeService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = ApplyFullNodeFormView.Route_Value, layout = MainLayout.class)
@PageTitle(ApplyFullNodeFormView.Page_Title)
 @Slf4j
public class ApplyFullNodeFormView extends VerticalLayout {

    public static final String Route_Value = "ApplyFullNodeForm";
    public static final String Page_Title = "Apply Full Node";

    private TextField fromAddress;
    private TextField fromBalance;
    private TextField nacAmount;
    private TextField nomcAmount;
    private TextField targetAddress;

    private PasswordField password;

    private Button submitBtn;
    private Button applyListBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private WalletService walletService;
    private FullNodeService fullNodeService;

    public ApplyFullNodeFormView(@Autowired WalletService walletService, @Autowired FullNodeService fullNodeService) {
        this.walletService = walletService;
        this.fullNodeService = fullNodeService;

        addClassName("apply-full-node-form-view");

        double nacAmountVal = 100;
        double nomcAmountVal = 3;

        Wallet defaultWallet = walletService.getDefaultWallet();

        String address = "";
        if (defaultWallet != null) {
            address = defaultWallet.getAddress();
        }

        //------ comp
        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Apply Account");
        fromAddress.setValue(address);
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Apply Account Balance");
        fromBalance.setValue("0 NAC , 0 NOMC");
        fromBalance.setReadOnly(true);

        nacAmount = new TextField("Pledge NAC Amount");
        nacAmount.setPlaceholder("Please enter the pledge amount");
        nacAmount.setValue(nacAmountVal + " NAC");
        nacAmount.setClearButtonVisible(true);
        nacAmount.setReadOnly(true);

        nomcAmount = new TextField("Pledge NOMC Amount");
        nomcAmount.setPlaceholder("Please enter the pledge amount");
        nomcAmount.setValue(nomcAmountVal + " NOMC");
        nomcAmount.setClearButtonVisible(true);
        nomcAmount.setReadOnly(true);

        targetAddress = new TextField("Target Account Address");
        targetAddress.setPlaceholder("Please enter the account address");
        targetAddress.setValue(address);
        targetAddress.setClearButtonVisible(true);

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        submitBtn = new Button("Submit");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        applyListBtn = new Button("Apply List");

        //------ form
        FormLayout formLayout = new FormLayout(title, fromAddress, fromBalance, nacAmount, nomcAmount, targetAddress, password, submitBtn, applyListBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);
        formLayout.setColspan(nacAmount, 2);
        formLayout.setColspan(nomcAmount, 2);
        formLayout.setColspan(targetAddress, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(submitBtn, 2);
        formLayout.setColspan(applyListBtn, 2);

        add(formLayout);

        //------ action

        submitBtn.addClickListener(e -> {

            try {
                CompUtil.showSuccess("Your application has been submitted, please wait patiently for the chain to take effect");
            } catch (Exception exception) {
                String tip = "Your application submission failed";
                log.error(tip, exception);
                CompUtil.showSuccess(tip);
            }
        });
    }



}
