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
import com.vaadin.flow.component.html.Span;
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
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.chain.transaction.Tx;
import org.nachain.core.chain.transaction.TxCrossService;
import org.nachain.core.chain.transaction.TxGasType;
import org.nachain.core.chain.transaction.context.TxContext;
import org.nachain.core.chain.transaction.context.TxContextService;
import org.nachain.core.crypto.Key;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailType;
import org.nachain.core.token.CoreTokenEnum;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.components.InstanceTokenSelectField;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.address.repo.AddressRepository;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

@Slf4j
@Route(value = CrossInstanceTransferFormView.Route_Value, layout = MainLayout.class)
@PageTitle(CrossInstanceTransferFormView.Page_Title)
public class CrossInstanceTransferFormView extends VerticalLayout implements BeforeEnterObserver {

    public static final String Route_Value = "CrossInstanceTransfer";
    public static final String Page_Title = "Cross Instance Transfer";

    private TextField fromAddress;
    private TextField fromBalance;
    //private ComboBox<Map.Entry<Long, BigInteger>> fromTokenComboBox;

    //private CoinTypeAmountField tokenAmount;
    private TextField crossAmount;
    private InstanceTokenSelectField fromInstanceTokenSelect;
    private InstanceTokenSelectField toInstanceTokenSelect;

    private PasswordField password;
    private TextField fee;

    private Button sendBtn;
    private Button clearBtn;
    private Double nacGasFee;

    private WalletService walletService;
    private NodeClusterHttpService nodeClusterHttpService;

    private Instance currentFromInstance;
    private Long currentFromTokenId;

    private Instance currentToInstance;
    private Set<Map.Entry<Long, BigInteger>> entries = Sets.newHashSet();

    public CrossInstanceTransferFormView(
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired AddressRepository addressRepository,
            @Autowired WalletService walletService
    ) {

        this.walletService = walletService;
        long currentInstanceId = walletService.getCurrentInstanceId();

        this.nodeClusterHttpService = nodeClusterHttpService;

        //prepare token balance
        Wallet defaultWallet1 = walletService.getDefaultWallet();
        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWallet1.getAddress(), currentInstanceId);

        if (usedTokenBalanceDetail != null &&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
        }

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Sender");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        //fromTokenComboBox = new ComboBox("Sender Tokens");
        //fromTokenComboBox.setItems(entries);
        //fromTokenComboBox.setItemLabelGenerator(e-> TokenUtil.getTokenSymbol(e.getKey()) + " : " + NumberUtil.bigIntToNacDouble(e.getValue()));
        //fromTokenComboBox.addValueChangeListener(e -> {
        //    Map.Entry<Long, BigInteger> entry = e.getValue();
        //
        //    //balance
        //    BigInteger value = entry.getValue();
        //
        //    //set curr tokenId
        //    currentTokenId = entry.getKey();
        //
        //});

        crossAmount = new TextField("Cross Amount");

        fromInstanceTokenSelect = new InstanceTokenSelectField("From Instance",naScanHttpService,nodeClusterHttpService,walletService);
        toInstanceTokenSelect = new InstanceTokenSelectField("To Instance",naScanHttpService,nodeClusterHttpService,walletService);

        ComboBox<Map.Entry<Long, BigInteger>> fromTokenCombo = fromInstanceTokenSelect.getTokenCombo();
        fromTokenCombo.addValueChangeListener(event -> {

            Map.Entry<Long, BigInteger> entry = event.getValue();

            //balance
            BigInteger value = entry.getValue();

            //set curr tokenId
            currentFromTokenId = entry.getKey();

            crossAmount.setSuffixComponent(new Span(TokenUtil.getTokenSymbol(currentFromTokenId)));

            //String tokenSymbol = TokenUtil.getTokenSymbol(currentFromTokenId);
            //CompUtil.showSuccess(tokenSymbol);
        });

        fromInstanceTokenSelect.getInstanceCombo().addValueChangeListener(event -> {
            currentFromInstance = event.getValue();

            BigInteger fromGas = nodeClusterHttpService.get_gasFee(currentFromInstance.getId());
            double feeDouble = NumberUtil.bigIntToNacDouble(fromGas);
            fee.setValue(feeDouble + " NAC");

        });
        toInstanceTokenSelect.getInstanceCombo().addValueChangeListener(event -> {
            currentToInstance = event.getValue();
        });

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");

        fee = new TextField("Fee");
        fee.setValue("- NAC");
        fee.setReadOnly(true);

        //------ form

        add(CompUtil.getCenterFormLayout(title, fromAddress, /*fromTokenComboBox,*/ fromInstanceTokenSelect, toInstanceTokenSelect, crossAmount, password, fee,sendBtn, clearBtn));

        //------ request

        //nacGasFee = walletDataService.getGasFeeValue();

        //------ action

        clearBtn.addClickListener(e -> clearForm());

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {

            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();

                String psw = StringUtils.trim(password.getValue());

                /*

                Key sendKey = MinerConfig.MINER_KEY;
                String sendWallet = "NacqYpEAwb6ojnE8KrHQjnP6J5LUq3DuZF";

                long instance = CoreInstanceEnum.NAC.id;
                long toInstance = CoreInstanceEnum.NOMC.id;

                long tokenId = CoreTokenEnum.NAC.id;

                BigInteger gas = TxGasService.calcGasAmount(instance);

                long txHeight = AccountTxHeightService.nextTxHeight(sendWallet, instance, CoreTokenEnum.NAC.id);

                BigInteger sendValue = Amount.of(new BigInteger("20000"), Unit.NAC).toBigInteger();

                TxContext txContext = TxContextService.newTransferContext(instance);
                txContext.setReferrerInstance(instance);
                txContext.setCrossToInstance(toInstance);

                Tx sendTx = TxCrossService.newCrossOutTx(instance, toInstance, tokenId, sendWallet, sendValue, gas, TxGasType.NAC.value, txHeight, txContext, sendKey);

                Mail mail = Mail.newMail(MailType.MSG_SEND_TX, sendTx.toJson());

                */


                //currentFromInstance = InstanceUtil.convert(CoreInstanceEnum.SUPERNODE);
                //currentToInstance = InstanceUtil.convert(CoreInstanceEnum.APPCHAIN);
                //currentFromTokenId = CoreTokenEnum.NAC.id;

                if (currentFromInstance == null) {
                    CompUtil.showError("Please select from instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (currentFromTokenId == null) {
                    CompUtil.showError("Please select the token of from instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (currentToInstance == null) {
                    CompUtil.showError("Please select to instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                UsedTokenBalanceDetail fromTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWallet1.getAddress(), currentFromInstance.getId());
                if (fromTokenBalanceDetail == null) {
                    CompUtil.showError("Failed to check token for from instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                double fromNacBalance = 0;
                double fromTokenBalance = 0;
                Set<Map.Entry<Long, BigInteger>> fromTokenBalanceEntrySet = fromTokenBalanceDetail.getTokenBalanceMap().entrySet();
                for (Map.Entry<Long, BigInteger> entry : fromTokenBalanceEntrySet) {
                    Long tokenId = entry.getKey();

                    // if nac
                    if (CoreTokenEnum.NAC.id == tokenId.longValue()) {
                        fromNacBalance = NumberUtil.bigIntToNacDouble(entry.getValue());
                    }

                    // from instance token
                    if (tokenId.longValue() == currentFromTokenId.longValue()) {
                        fromTokenBalance = NumberUtil.bigIntToNacDouble(entry.getValue());
                    }
                }


                double crossAmountVal = 0;
                try {
                    crossAmountVal = Double.parseDouble(StringUtils.trim(crossAmount.getValue()));
                } catch (Exception ex) {
                    CompUtil.showError("Incorrect cross amount format");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (crossAmountVal > fromTokenBalance) {
                    CompUtil.showError("Cross amount cannot be greater than " + fromTokenBalance);
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(psw)) {
                    CompUtil.showError("Password can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                Key fromKey = walletService.getWalletKey(defaultWallet,psw);
                if (fromKey == null ) {
                    CompUtil.showError("Wallet password is incorrect");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (false) {
                    CompUtil.showEnableByBlockHeightDialog(walletDataService.getNacLastBlockHeightByRequest());
                    sendBtn.setEnabled(true);
                    return;
                }

                long fromInstance = currentFromInstance.getId();
                long toInstance = currentToInstance.getId();
                long crossTokenId = currentFromTokenId;

                long txHeight = nodeClusterHttpService.getAccountTxHeight(fromInstance,defaultWalletAddress, CoreTokenEnum.NAC.id);
                txHeight++;

                BigInteger fromGas = nodeClusterHttpService.get_gasFee(fromInstance);//TxGasService.calcGasAmount(fromInstance);
                if (fromGas.longValue() == 0) {
                    CompUtil.showError("Incorrect gas for the from instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (fromNacBalance <= NumberUtil.bigIntToNacDouble(fromGas)) {
                    CompUtil.showError("Insufficient amount of NAC token for the from instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                BigInteger sendValue = NumberUtil.nacDoubleToBigInt(crossAmountVal);

                TxContext txContext = TxContextService.newTransferContext(fromInstance);
                txContext.setReferrerInstance(fromInstance);
                txContext.setCrossToInstance(toInstance);

                log.info("CrossInstanceTransferFormView -> fromInstance = "+fromInstance +" , toInstance = "+toInstance +" , crossTokenId = "+crossTokenId +" , fromGas = "+fromGas +" , txHeight = "+txHeight+" , sendValue = "+sendValue+" , txContext = "+txContext);

                Tx sendTx = TxCrossService.newCrossOutTx(fromInstance, toInstance, crossTokenId, defaultWalletAddress, sendValue, fromGas, TxGasType.NAC.value, txHeight, txContext, fromKey);
                Mail mail = Mail.newMail(MailType.MSG_SEND_TX, sendTx.toJson());
                String hash = sendTx.getHash();
                String json = mail.toJson();

                boolean flag = nodeClusterHttpService.broadcast(json, fromInstance);
                log.info("CrossInstanceTransferFormView broadcast() -> flag = "+flag +" , hash = "+hash +" , json = "+json);

                //HttpResult httpResult = this.nodeClusterHttpService.dapp_submitDeploy(
                //        instance,
                //        ownerAddressVal,
                //        1,
                //        1,
                //        tokenNameVal,
                //        tokenSymbolVal,
                //        Long.valueOf(totalSupplyVal)
                //);

                if (flag) {

                    CompUtil.showSuccess("Cross-instance transfer successfully");

                    walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_CROSS_INSTANCE_TRANSFER, hash, defaultWallet));

                    UI.getCurrent().navigate(WalletSendResultView.class);

                } else {
                    CompUtil.showError("Failed to cross-instance transfer ");
                }


                /*
                HttpResult httpResult = this.nodeClusterHttpService.dns_submitCross(
                        instance,
                        defaultWalletAddress,
                        1,
                        1,
                        1D
                );
                if (!httpResult.getFlag()) {
                    CompUtil.showError("Failed to cross instance transfer : " + httpResult.getMessage());
                } else {
                    CompUtil.showSuccess("Cross instance transfer successfully");
                    clearForm();
                }
                */

            } catch (Exception exception) {
                String msg = "Failed to submit cross instance transfer: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                sendBtn.setEnabled(true);
            }

        });

    }

    private void clearForm() {
        this.password.setValue("");
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
        }

    }

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        //Wallet wallet = event.getWallet();
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromBalance.setValue(walletService.getDefaultWalletBalanceText(walletService.getCurrentInstanceId()));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (walletChangeDefaultEventReg != null)
            walletChangeDefaultEventReg.remove();
    }

    private Registration walletChangeDefaultEventReg;

}
