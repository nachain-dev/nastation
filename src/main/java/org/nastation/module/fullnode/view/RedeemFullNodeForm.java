package org.nastation.module.fullnode.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.address.repo.AddressRepository;
import org.nastation.module.node.service.FullNodeUtil;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;

@Slf4j
@Route(value = RedeemFullNodeForm.Route_Value, layout = MainLayout.class)
@PageTitle(RedeemFullNodeForm.Page_Title)
public class RedeemFullNodeForm extends VerticalLayout implements BeforeEnterObserver {

    public static final String Route_Value = "RedeemFullNodeForm";
    public static final String Page_Title = "Redeem FullNode";

    private TextField ownerAddress;
    private TextField ownerBalance;
    private TextField paidCoin;
    private TextField requiredCoin;
    private TextField payNomc;

    private TextField orderId;
    private TextField beneficiaryAddress;

    private PasswordField password;

    private Button sendBtn;
    private Button clearBtn;

    private WalletService walletService;
    private NodeClusterHttpService nodeClusterHttpService;

    public RedeemFullNodeForm(
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired AddressRepository addressRepository,
            @Autowired WalletService walletService
    ) {

        this.walletService = walletService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        long currentInstanceId = walletService.getCurrentInstanceId();

        int orderIdVal = 1222;

        BigInteger nac = FullNodeUtil.calcNac(orderIdVal);
        BigInteger nomc = FullNodeUtil.calcNomc(orderIdVal);
        BigDecimal nacDecimal = Amount.of(nac).toDecimal(4, Unit.NAC);
        BigDecimal nomcDecimal = Amount.of(nomc).toDecimal(4, Unit.NAC);

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        ownerAddress = new TextField("Owner Address");
        ownerAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        ownerAddress.setReadOnly(true);

        ownerBalance = new TextField("Owner Balance");
        ownerBalance.setValue(walletService.getDefaultWalletBalanceText_excludeUsdn(currentInstanceId));
        ownerBalance.setReadOnly(true);

        paidCoin = new TextField("Paid Coin");
        paidCoin.setValue("NOMC: - , NAC: -");
        paidCoin.setReadOnly(true);
        // when pay nomc again then show
        paidCoin.setVisible(false);

        requiredCoin = new TextField("Required Coin");
        requiredCoin.setValue(String.format("NOMC: %s , NAC: %s", nomcDecimal.doubleValue(), nacDecimal.doubleValue()));
        requiredCoin.setReadOnly(true);

        orderId = new TextField("Order ID");
        orderId.setValue(String.valueOf(orderIdVal));

        beneficiaryAddress = new TextField("Beneficiary Address(Optional)");
        beneficiaryAddress.setValue("");

        payNomc = new TextField("Need Nomc(More than half of required)");
        payNomc.setValue(String.valueOf(nomcDecimal.doubleValue() / 2));

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");

        //------ form
        FormLayout formLayout = new FormLayout(title, ownerAddress, ownerBalance, requiredCoin, paidCoin, orderId, beneficiaryAddress, payNomc, password, sendBtn, clearBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(ownerAddress, 2);
        formLayout.setColspan(ownerBalance, 2);
        formLayout.setColspan(requiredCoin, 2);
        formLayout.setColspan(paidCoin, 2);
        formLayout.setColspan(orderId, 2);
        formLayout.setColspan(beneficiaryAddress, 2);
        formLayout.setColspan(payNomc, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(sendBtn, 2);
        formLayout.setColspan(clearBtn, 2);

        add(formLayout);

        //------ request

        //payedCoin.setValue();

        //------ action

        clearBtn.addClickListener(e -> clearForm());

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {
            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();

                long instance = CoreInstanceEnum.NAC.id;
                String beneficiaryAddressVal = StringUtils.trim(beneficiaryAddress.getValue());
                String ownerAddressAddressVal = defaultWalletAddress;
                String payNomcVal = StringUtils.trim(payNomc.getValue());
                String psw = StringUtils.trim(password.getValue());

                if (StringUtils.isBlank(beneficiaryAddressVal)) {
                    beneficiaryAddressVal = defaultWalletAddress;
                }

                if (StringUtils.isBlank(payNomcVal)) {
                    CompUtil.showError("Pay nomc can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(psw)) {
                    CompUtil.showError("Password can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (walletService.getWalletKey(defaultWallet,psw) == null ) {
                    CompUtil.showError("The password is incorrect");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (true) {
                    CompUtil.showEnableByBlockHeightDialog(walletDataService.getNacLastBlockHeightByRequest());
                    sendBtn.setEnabled(true);
                    return;
                }

                long orderIdValue = Long.valueOf(orderId.getValue());

                HttpResult httpResult = this.nodeClusterHttpService.submitRedeem(
                        instance,
                        orderIdValue,
                        ownerAddressAddressVal,
                        beneficiaryAddressVal,
                        Double.valueOf(payNomcVal)
                );

                if (!httpResult.getFlag()) {
                    CompUtil.showError("Redeem full node failed: " + httpResult.getMessage());
                } else {
                    CompUtil.showSuccess("Redeem full node successfully");
                    clearForm();
                }

            } catch (Exception exception) {
                String msg = "Redeem full node failed: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                sendBtn.setEnabled(true);
            }
        });

    }

    private void clearForm() {
        this.beneficiaryAddress.setValue("");
        this.password.setValue("");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        Wallet defaultWallet = walletService.getDefaultWallet();
        String defaultWalletAddress = defaultWallet.getAddress();

        long instance = CoreInstanceEnum.NAC.id;

        long orderIdVal = this.nodeClusterHttpService.fullnode_getOrderId(
                instance,
                defaultWalletAddress
        );

        if (orderIdVal > 0) {
            this.orderId.setValue(String.valueOf(orderIdVal));
        }

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {
            walletChangeDefaultEventReg = ComponentUtil.addListener(
                    mainLayout,
                    WalletChangeDefaultEvent.class,
                    event -> {
                        walletChangeDefaultEventHandler(event);
                    }
            );
        }

    }

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        //Wallet wallet = event.getWallet();
        ownerAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        ownerBalance.setValue(walletService.getDefaultWalletBalanceText(walletService.getCurrentInstanceId()));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (walletChangeDefaultEventReg != null)
            walletChangeDefaultEventReg.remove();
    }

    private Registration walletChangeDefaultEventReg;

}
