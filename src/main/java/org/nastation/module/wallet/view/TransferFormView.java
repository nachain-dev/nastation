package org.nastation.module.wallet.view;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.components.AccountAddressField;
import org.nastation.components.CoinTypeAmountField;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.address.data.Address;
import org.nastation.module.address.repo.AddressRepository;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Route(value = TransferFormView.Route_Value, layout = MainLayout.class)
@PageTitle(TransferFormView.Page_Title)
public class TransferFormView extends VerticalLayout implements BeforeEnterObserver {

    public static final String Route_Value = "TransferForm";
    public static final String Page_Title = "Send";

    private TextField fromAddress;
    private ComboBox<Map.Entry<Long, BigInteger>> fromTokenComboBox;
    //private TextField fromBalance;
    private AccountAddressField toAddress;
    private CoinTypeAmountField amount;
    private TextField amountField;
    private TextField fee;
    private PasswordField password;

    private TextField remarkField;

    private Button sendBtn;
    private Button clearBtn;
    private Button txListBtn;

    private Long currentTokenId;
    private Set<Map.Entry<Long, BigInteger>> entries = Sets.newHashSet();

    private WalletService walletService;
    private WalletDataService walletDataService;
    private NaScanHttpService naScanHttpService;
    private NodeClusterHttpService nodeClusterHttpService;
    private AddressRepository addressRepository;

    public TransferFormView(
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired AddressRepository addressRepository,
            @Autowired WalletService walletService
    ) {
        addClassName("transfer-form-view");

        this.walletService = walletService;
        this.walletDataService = walletDataService;
        this.naScanHttpService = naScanHttpService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.addressRepository = addressRepository;

        long currentInstanceId = walletService.getCurrentInstanceId();
        refreshEntries();

        //------ comp

        H2 title = new H2();
        title.setText("Send");
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Sender");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        //fromBalance = new TextField("Sender Tokens");
        //fromBalance.setValue(walletService.getDefaultWalletBalanceText(currentInstanceId));
        //fromBalance.setReadOnly(true);

        fromTokenComboBox = new ComboBox("Wallet tokens");
        fromTokenComboBox.setItems(entries);
        fromTokenComboBox.setItemLabelGenerator(e->TokenUtil.getTokenSymbol(e.getKey()) + " : " + NumberUtil.bigIntToNacDouble(e.getValue()));
        fromTokenComboBox.addValueChangeListener(e -> {
            Map.Entry<Long, BigInteger> entry = e.getValue();

            if (entry != null) {
                //balance
                BigInteger value = entry.getValue();

                //set curr tokenId
                currentTokenId = entry.getKey();
            }

        });

        List<Address> addressList = addressRepository.findAllByOrderByIdDesc();

        toAddress = new AccountAddressField("Recipient Address", addressList);
        toAddress.addValueChangeListener(e -> {
            String onlyLabel = AccountAddressField.getOnlyLabel(e.getValue());
            Address address = addressRepository.findByLabel(onlyLabel);

            TextField addressTxtField = toAddress.getAddressTxtField();
            if (address != null && addressTxtField != null) {
                addressTxtField.setValue(address.getAddress());
            }

        });

        //amount = new CoinTypeAmountField("Recipient Amount");
        amountField = new TextField("Recipient Amount");
        remarkField = new TextField("Memo");
        remarkField.setPlaceholder("(Optional)");

        toAddress.addValueChangeListener(e -> {
            String coinType = e.getValue();
        });
        toAddress.getAddressTxtField().setValue("");

        fee = new TextField("Fee");
        fee.setValue("- NAC");
        fee.setReadOnly(true);

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");
        //txListBtn = new Button("Transaction List");

        //------ form

        add(CompUtil.getCenterFormLayout(title, fromAddress, fromTokenComboBox, toAddress, amountField, remarkField, password,fee, sendBtn, clearBtn));

        //------ request

        BigInteger gasFee = nodeClusterHttpService.get_gasFee(currentInstanceId);
        double gasFeeDouble = NumberUtil.bigIntToNacDouble(gasFee);

        fee.setValue(gasFeeDouble + " NAC");

        //------ action

        clearBtn.addClickListener(e -> clearForm());
        //txListBtn.addClickListener(e -> {
        //    UI.getCurrent().navigate(TxDataListView.class);
        //});

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {

            try {
                Wallet defaultWallet = walletService.getDefaultWallet();
                String toAddressText = toAddress.getAddressTxtField().getValue();
                String amountText = amountField.getValue();
                HttpResult result = walletService.send(currentInstanceId, currentTokenId, defaultWallet.getAddress(), toAddressText, password.getValue(), amountText, remarkField.getValue());

                if (result.getFlag()) {
                    Map<String, String> map = (Map<String, String>) result.getData();

                    String txHash = map.get("hash");
                    String mail = map.get("mail");

                    /* UI show */

                    clearForm();

                    CompUtil.showSuccess("New transaction has been sent");

                    walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_TRANSFER, txHash, defaultWallet));

                    UI.getCurrent().navigate(WalletSendResultView.class);
                } else {
                    CompUtil.showError(result.getMessage());
                }

        } catch(Exception exception){
            String msg = "Transaction send failed [exception] ";
            log.error(msg, exception);
            CompUtil.showError(msg);
        }finally{
            sendBtn.setEnabled(true);
        }

    });

}

    private void refreshEntries() {

        long currentInstanceId = walletService.getCurrentInstanceId();

        //prepare token balance
        Wallet defaultWallet1 = walletService.getDefaultWallet();
        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWallet1.getAddress(), currentInstanceId);

        if (usedTokenBalanceDetail != null && usedTokenBalanceDetail.getTokenBalanceMap() != null) {
            entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
        }

    }

    private void clearForm() {
        this.password.setValue("");
        //this.amount.getAmountField().setValue("");
        this.amountField.setValue("");
        this.toAddress.getAddressTxtField().setValue("");
        this.remarkField.setValue("");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //System.out.println("TransferFormView BeforeEnterEvent = " + event);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //System.out.println("TransferFormView onAttach = " + attachEvent);

        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {
            walletChangeDefaultEventReg = ComponentUtil.addListener(
                    mainLayout,
                    WalletChangeDefaultEvent.class,
                    event -> {
                        walletChangeDefaultEventHandler(event);
                    }
            );

            instanceChangeEventReg = ComponentUtil.addListener(
                    UI.getCurrent(),
                    InstanceChangeEvent.class,
                    event -> {
                        instanceChangeEventHandler(event);
                    }
            );
        }

    }

    private void instanceChangeEventHandler(InstanceChangeEvent event) {
        refreshEntries();
        fromTokenComboBox.setItems(entries);
    }

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        //Wallet wallet = event.getWallet();
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());

        instanceChangeEventHandler(null);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (walletChangeDefaultEventReg != null)
            walletChangeDefaultEventReg.remove();

        if (instanceChangeEventReg != null)
            instanceChangeEventReg.remove();
    }

    private Registration walletChangeDefaultEventReg;
    private Registration instanceChangeEventReg;


}
