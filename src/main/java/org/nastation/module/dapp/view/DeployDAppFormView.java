package org.nastation.module.dapp.view;

import com.vaadin.flow.component.UI;
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
import org.nastation.common.util.CompUtil;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = DeployDAppFormView.Route_Value, layout = MainLayout.class)
@PageTitle(DeployDAppFormView.Page_Title)
public class DeployDAppFormView extends VerticalLayout {

    public static final String Route_Value = "DeployDAppForm";
    public static final String Page_Title = "Deploy DApp";

    private TextField fromAddress;
    private TextField fromBalance;
    private DAppTypeField dappType;
    private DeployTargetField deployType;
    private TextField spaceHash;

    private PasswordField password;

    private Button sendBtn;
    private Button clearBtn;
    private Button listBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);
    private WalletService walletService;

    public DeployDAppFormView(@Autowired WalletService walletService) {
        this.walletService = walletService;
        long currentInstanceId = walletService.getCurrentInstanceId();

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");


        fromAddress = new TextField("Account");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Account Balance");
        fromBalance.setValue(walletService.getDefaultWalletBalanceText(currentInstanceId));
        fromBalance.setReadOnly(true);

        dappType = new DAppTypeField("DApp Type");
        dappType.setValue("DWeb");

        deployType = new DeployTargetField("Deploy Target");
        deployType.setValue("AppStore");

        spaceHash = new TextField("Space Hash");
        spaceHash.setClearButtonVisible(true);
        spaceHash.setValue("0x00000000000000000000001");

        //Button applyBtn = new Button("Apply");
        //storageSpaceHash.setSuffixComponent(applyBtn);

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");
        listBtn = new Button("DApp List");

        //------ form
        FormLayout formLayout = new FormLayout(title, fromAddress, fromBalance, spaceHash, dappType,deployType, password, sendBtn, /*clearBtn,*/ listBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);
        formLayout.setColspan(spaceHash, 2);
        formLayout.setColspan(dappType, 2);
        formLayout.setColspan(deployType, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(sendBtn, 2);
        //formLayout.setColspan(clearBtn, 2);
        formLayout.setColspan(listBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);
        clearForm();

        //clearBtn.addClickListener(e -> clearForm());
        sendBtn.addClickListener(e -> {
            CompUtil.showError("Turns on when a certain block height is reached...");
        });
        listBtn.addClickListener(e -> {
            UI.getCurrent().navigate(PublishDAppListView.class);
        });
    }

    private void clearForm() {
        binder.setBean(new Wallet());
    }


}
