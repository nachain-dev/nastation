package org.nastation.module.wallet.view;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.event.WalletCreateEvent;
import org.nastation.common.util.CompUtil;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = MnemonicBackupConfirmFormView.Route_Value, layout = MainLayout.class)
@PageTitle("Mnemonic Backup Confirm")
@Slf4j
public class MnemonicBackupConfirmFormView extends VerticalLayout {

    public static final String Page_Title = "Mnemonic Backup Confirm";
    public static final String Route_Value = "MnemonicBackupConfirmView";

    private Button nextBtn;
    private Button backBtn;
    private TextArea wordArea;

    public MnemonicBackupConfirmFormView(
            @Autowired WalletRepository walletRepository,
            @Autowired WalletService walletService
            ) {
        addClassName("mnemonic-backup-confirm-form-view");

        Wallet wallet = (Wallet) UI.getCurrent().getSession().getAttribute("CreateWallet");
        wallet = (wallet == null ? new Wallet() : wallet);
        String mnemonic = StringUtils.defaultIfBlank(wallet.getMnemonic(), "");
        String[] array = mnemonic.split(" ");

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        wordArea = new TextArea("Please enter 12 mnemonic words separated by spaces");

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        backBtn = new Button("Back");

        //------ form
        FormLayout formLayout = new FormLayout(title, wordArea, nextBtn, backBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(wordArea, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action

        nextBtn.addClickListener(e -> {
            try {

                String value = wordArea.getValue();
                Wallet currentWallet = walletService.getCurrentWallet();
                String mnemonic1 = currentWallet.getMnemonic();

                if (StringUtils.equals(value, mnemonic1)) {

                    // save to db
                    walletService.persistCurrentWallet();

                    //add
                    ComponentUtil.fireEvent(CompUtil.getMainLayout(), new WalletCreateEvent(this, currentWallet));

                    CompUtil.showSuccess("New wallet has been saved");
                    UI.getCurrent().navigate(CreateWalletResultView.class);

                }else{
                    CompUtil.showError("The mnemonic are incorrect");
                }


            } catch (Exception e1) {
                String msg = String.format("The mnemonic backup confirm failed: ", e1.getMessage());
                log.error(msg, e1);
                CompUtil.showError(msg);
            }

        });

        backBtn.addClickListener(e -> UI.getCurrent().navigate(MnemonicBackupFormView.class));

    }


}
