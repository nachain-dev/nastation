package org.nastation.module.vote.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.token.CoreTokenEnum;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.InstanceUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Route(value = VoteFormView.Route_Value, layout = MainLayout.class)
@PageTitle(VoteFormView.Page_Title)
public class VoteFormView extends Div { //vote-form-view

    public static final String Route_Value = "VoteForm";
    public static final String Page_Title = "Vote Form";

    private ComboBox<String> voteInstance = new ComboBox<>();
    private TextField voteAddress;
    private TextField beneficiaryAddress;
    private TextField nominateAddress;

    private NumberField numberField ;

    private PasswordField password;

    private Button nextBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private NodeClusterHttpService nodeClusterHttpService;

    public VoteFormView(@Autowired WalletService walletService, @Autowired WalletDataService walletDataService, @Autowired NodeClusterHttpService nodeClusterHttpService) {
        addClassName("vote-form-view");

        //------ comp

        this.nodeClusterHttpService = nodeClusterHttpService;

        List<String> instanceList = InstanceUtil.getEnableInstanceList().stream()
                .filter(one -> one.getId() == CoreTokenEnum.NAC.id)
                .map(one -> one.getSymbol())
                .collect(Collectors.toList());

        voteInstance.setLabel("Vote Instance");
        voteInstance.setItems(instanceList);
        voteInstance.setValue(CoreInstanceEnum.NAC.symbol);

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        voteAddress = new TextField("Vote Address");
        beneficiaryAddress = new TextField("Beneficiary Address");
        nominateAddress = new TextField("Nominate Address");
        password = new PasswordField("Password");

        numberField = new NumberField("Vote Amount(Max: 100NAC)");
        numberField.setValue(100D);
        numberField.setHasControls(true);
        numberField.setMin(1);
        numberField.setMax(100);

        nextBtn = new Button("Submit");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //------ form
        FormLayout formLayout = new FormLayout(title,voteInstance, voteAddress,beneficiaryAddress,nominateAddress,numberField, password, nextBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(voteInstance, 2);
        formLayout.setColspan(voteAddress, 2);
        formLayout.setColspan(beneficiaryAddress, 2);
        formLayout.setColspan(nominateAddress, 2);
        formLayout.setColspan(numberField, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(nextBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);
        clearForm();

        nextBtn.setDisableOnClick(true);
        nextBtn.addClickListener(e -> {
            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();
                String ownerAddressAddressVal = defaultWalletAddress;

                long instance = CoreInstanceEnum.NAC.id;

                String voteAddressVal = StringUtils.trim(voteAddress.getValue());
                String ownerAddressVal = defaultWalletAddress;
                String beneficiaryAddressVal = StringUtils.trim(beneficiaryAddress.getValue());
                String nominateAddressVal = StringUtils.trim(nominateAddress.getValue());
                int voteAmountVal = (int)(numberField.getValue().intValue());
                String psw = StringUtils.trim(password.getValue());

                if (StringUtils.isBlank(beneficiaryAddressVal)) {
                    beneficiaryAddressVal = defaultWalletAddress;
                }

                if (StringUtils.isBlank(voteAddressVal)) {
                    CompUtil.showError("Vote address can not be empty");
                    nextBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(beneficiaryAddressVal)) {
                    CompUtil.showError("Beneficiary address can not be empty");
                    nextBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(nominateAddressVal)) {
                    CompUtil.showError("Nominate address can not be empty");
                    nextBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(psw)) {
                    CompUtil.showError("Password can not be empty");
                    nextBtn.setEnabled(true);
                    return;
                }

                if (walletService.getWalletKey(defaultWallet,psw) == null ) {
                    CompUtil.showError("The password is incorrect");
                    nextBtn.setEnabled(true);
                    return;
                }

                if (true) {
                    CompUtil.showEnableByBlockHeightDialog(walletDataService.getNacLastBlockHeightByRequest());
                    nextBtn.setEnabled(true);
                    return;
                }

                HttpResult httpResult = this.nodeClusterHttpService.vote_submitVote(
                        instance,
                        ownerAddressVal,
                        ownerAddressAddressVal,
                        beneficiaryAddressVal,
                        nominateAddressVal,
                        voteAmountVal
                );

                if (!httpResult.getFlag()) {
                    CompUtil.showError("Failed to submit vote : " + httpResult.getMessage());
                } else {
                    CompUtil.showSuccess("Voting submitted successfully");
                    clearForm();
                }

            } catch (Exception exception) {
                String msg = "Failed to submit vote: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                nextBtn.setEnabled(true);
            }
        });
    }

    private void clearForm() {
        /*binder.setBean(new Wallet());*/
    }
}