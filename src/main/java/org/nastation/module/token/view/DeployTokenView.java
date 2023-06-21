package org.nastation.module.token.view;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import org.nachain.core.crypto.Key;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailService;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.Token;
import org.nachain.core.token.TokenService;
import org.nachain.core.token.TokenTypeEnum;
import org.nachain.core.util.RegexpUtils;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.*;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.address.repo.AddressRepository;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.service.WalletService;
import org.nastation.module.wallet.view.WalletSendResultView;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Route(value = DeployTokenView.Route_Value, layout = MainLayout.class)
@PageTitle(DeployTokenView.Page_Title)
public class DeployTokenView extends VerticalLayout implements BeforeEnterObserver {

    public static final String Route_Value = "DeployTokenView";
    public static final String Page_Title = "Deploy Token";

    private ComboBox<String> tokenTypeCombo;
    private ComboBox<String> protocolCombo;

    private TextField fromAddress;
    private ComboBox<Map.Entry<Long, BigInteger>> fromAddressTokenComboBox;

    private TextField ownerAddress;
    private TextField name;
    private TextField symbol;
    private TextField info;
    private TextField totalSupply;
    private PasswordField password;

    private Button sendBtn;
    private Button clearBtn;

    private WalletService walletService;
    private NodeClusterHttpService nodeClusterHttpService;

    private NaScanHttpService naScanHttpService;

    private long fixAppChainInstanceId = CoreInstanceEnum.APPCHAIN.id;

    private Set<Map.Entry<Long, BigInteger>> tokenBalanceMapEntrySet = Sets.newHashSet();

    private void clear_fromAddressTokenComboBox() {

        if (fromAddressTokenComboBox !=null) {
            fromAddressTokenComboBox.clear();
            fromAddressTokenComboBox.getDataProvider().refreshAll();
        }
    }


    private void refresh_fromAddressTokenComboBox() {

        clear_fromAddressTokenComboBox();

        //prepare token balance
        String defaultWalletAddress = walletService.getDefaultWalletAddress();

        UsedTokenBalanceDetail usedTokenBalanceDetail = this.naScanHttpService.getUsedTokenBalanceDetail(defaultWalletAddress, fixAppChainInstanceId);

        if (usedTokenBalanceDetail != null &&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            tokenBalanceMapEntrySet = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
        }else{
            tokenBalanceMapEntrySet = Sets.newHashSet();
        }

        if (fromAddressTokenComboBox !=null) {
            fromAddressTokenComboBox.clear();
            fromAddressTokenComboBox.setItems(tokenBalanceMapEntrySet);
            fromAddressTokenComboBox.getDataProvider().refreshAll();
        }

        select_fromAddressTokenComboBox();
    }

    public void select_fromAddressTokenComboBox(){
        if (fromAddressTokenComboBox !=null) {
            if (CollUtil.isNotEmpty(tokenBalanceMapEntrySet)) {
                Optional<Map.Entry<Long, BigInteger>> ifNac = tokenBalanceMapEntrySet.stream().filter(e -> e.getKey().longValue() == CoreTokenEnum.NAC.id).findFirst();
                if (ifNac.isPresent()) {
                    if(fromAddressTokenComboBox!=null) fromAddressTokenComboBox.setValue(ifNac.get());
                }
            }
        }
    }

    public DeployTokenView(
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired AddressRepository addressRepository,
            @Autowired WalletService walletService
    ) {

        this.walletService = walletService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.naScanHttpService = naScanHttpService;

        //prepare token balance
        refresh_fromAddressTokenComboBox();

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Creator");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromAddressTokenComboBox = new ComboBox("Creator Balance ( AppChain Instance )");
        fromAddressTokenComboBox.setItems(tokenBalanceMapEntrySet);
        fromAddressTokenComboBox.setItemLabelGenerator(e->TokenUtil.getTokenSymbol(e.getKey()) + " : " + NumberUtil.bigIntToNacDouble(e.getValue()));
        fromAddressTokenComboBox.addValueChangeListener(e -> {
        });
        select_fromAddressTokenComboBox();

        ownerAddress = new TextField("Owner Address");
        ownerAddress.setValue(walletService.getDefaultWalletAddress());

        String[] protocols = {"Normal Protocol"};/*, "NFT Protocol"*/
        protocolCombo = new ComboBox("Protocol Type");
        protocolCombo.setItems(protocols);
        protocolCombo.setValue("Normal Protocol");
        protocolCombo.setWidth("180px");

        List<String> coinSymbolList = Arrays.stream(TokenTypeEnum.values())
                .filter(one -> one.id == TokenTypeEnum.FIXED.id)
                .map(one -> one.symbol)
                .collect(Collectors.toList());

        tokenTypeCombo = new ComboBox("Token Type");
        tokenTypeCombo.setItems(coinSymbolList);
        tokenTypeCombo.setValue(TokenTypeEnum.FIXED.symbol);

        HorizontalLayout layout = new HorizontalLayout(protocolCombo, this.tokenTypeCombo);
        layout.setFlexGrow(1.0, protocolCombo);

        name = new TextField("Token Name");
        name.setValue("");

        symbol = new TextField("Token Symbol");
        symbol.setValue("");

        info = new TextField("Token Info");
        info.setValue("");

        totalSupply = new TextField("Token TotalSupply");
        totalSupply.setValue("");

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");

        HorizontalLayout nameLine = new HorizontalLayout(name,symbol);
        nameLine.setFlexGrow(2, name);

        HorizontalLayout infoLine = new HorizontalLayout(info,totalSupply);
        infoLine.setFlexGrow(2, info);

        //------ form
        add(CompUtil.getCenterFormLayout(title, fromAddress, fromAddressTokenComboBox, ownerAddress, layout, nameLine, infoLine, password, sendBtn, clearBtn));


        //------ action

        clearBtn.addClickListener(e -> clearForm());

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {

            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();

                String tokenNameVal = StringUtils.trim(name.getValue());
                String tokenSymbolVal = StringUtils.trim(symbol.getValue());
                String tokenInfoVal = StringUtils.trim(info.getValue());
                String totalSupplyVal = StringUtils.trim(totalSupply.getValue());
                String ownerAddressVal = StringUtils.trim(ownerAddress.getValue());
                String psw = StringUtils.trim(password.getValue());

                if (StringUtils.isBlank(ownerAddressVal)) {
                    CompUtil.showError("Owner address can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (!WalletUtil.isAddressValid(ownerAddressVal)) {
                    CompUtil.showError("Invalid owner address");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(tokenNameVal)) {
                    CompUtil.showError("Token name can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if(!tokenNameVal.matches(RegexpUtils.REGEXP_NORMAL)){
                    CompUtil.showError("Token name only supports numbers or english letters");
                    sendBtn.setEnabled(true);
                    return;
                }

                Optional<Token> tokenNameExistsOpt = TokenUtil.getEnableTokenList().stream().filter(t -> t.getName().equalsIgnoreCase(tokenNameVal)).findFirst();
                if (tokenNameExistsOpt.isPresent()) {
                    CompUtil.showError("Invalid token name");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (tokenNameVal.length() > TokenService.VERIFY_NAME_LENGTH) {
                    CompUtil.showError("The length of token name cannot be greater than " + TokenService.VERIFY_NAME_LENGTH);
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(tokenSymbolVal)) {
                    CompUtil.showError("Token symbol can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }
                if (tokenSymbolVal.length() > TokenService.VERIFY_SYMBOL_LENGTH) {
                    CompUtil.showError("The length of token symbol cannot be greater than " + TokenService.VERIFY_SYMBOL_LENGTH);
                    sendBtn.setEnabled(true);
                    return;
                }

                if(!tokenSymbolVal.matches(RegexpUtils.REGEXP_NORMAL)){
                    CompUtil.showError("Token symbol only supports numbers or english letters");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(tokenInfoVal)) {
                    CompUtil.showError("Token info can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }
                if (tokenInfoVal.length() > TokenService.VERIFY_INFO_LENGTH) {
                    CompUtil.showError("The length of token info cannot be greater than " + TokenService.VERIFY_INFO_LENGTH);
                    sendBtn.setEnabled(true);
                    return;
                }

                if(!tokenInfoVal.matches(RegexpUtils.REGEXP_NORMAL)){
                    CompUtil.showError("Token info only supports numbers or english letters");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(totalSupplyVal)) {
                    CompUtil.showError("Token total supply can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                Long totalSupplyLong = 0L;
                try {
                    totalSupplyLong = Long.valueOf(totalSupplyVal);
                } catch (Exception exception) {
                    CompUtil.showError("Token total supply format error");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(psw)) {
                    CompUtil.showError("Password can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                Key fromKey = walletService.getWalletKey(defaultWallet, psw);
                if (fromKey == null) {
                    CompUtil.showError("Wallet password is incorrect");
                    sendBtn.setEnabled(true);
                    return;
                }

                //50u nac
                BigInteger sendValue = nodeClusterHttpService.getCalcDeployNacPrice();
                if (sendValue.longValue() == 0) {
                    CompUtil.showError("Fail to query nac required for deployment");
                    sendBtn.setEnabled(true);
                    return;
                }

                // need nac
                double sendValueDouble = NumberUtil.bigIntToNacDouble(sendValue);

                boolean isNacInAppChainEnough = walletDataService.isTokenInInstanceEnough(defaultWalletAddress, fixAppChainInstanceId, CoreTokenEnum.NAC.id, sendValueDouble);

                if (!isNacInAppChainEnough) {
                    CompUtil.showError("Insufficient amount of nac required, expected amount: " + sendValueDouble);
                    sendBtn.setEnabled(true);
                    return;
                }

                // deploy token need the APPCHAIN height
                long accountTxHeightInAppChain = nodeClusterHttpService.getAccountTxHeight(fixAppChainInstanceId, defaultWalletAddress, CoreTokenEnum.NAC.id);
                accountTxHeightInAppChain++;

                BigInteger gasFee = nodeClusterHttpService.get_gasFee(fixAppChainInstanceId);
                log.warn("get_gasFee(fixAppChainInstanceId):" + gasFee);

                //gasFee = gasFee.add(BigInteger.valueOf(100000));

                if (gasFee.longValue() <= 0) {
                    CompUtil.showError("Failed to get the gas fee of the instance");
                    sendBtn.setEnabled(true);
                    return;
                }

                log.info("DeployTokenView -> tokenNameVal = "+tokenNameVal +" , tokenSymbolVal = "+tokenSymbolVal +" , tokenInfoVal = "+tokenInfoVal +" , defaultWalletAddress = "+defaultWalletAddress +" , totalSupplyLong = "+Amount.of(totalSupplyLong, Unit.NAC)+" , ownerAddressVal = "+ownerAddressVal);

                Token token = TokenService.newNormalToken(tokenNameVal, tokenSymbolVal, tokenInfoVal, Amount.of(totalSupplyLong, Unit.NAC), ownerAddressVal);
                Mail newMail = MailService.newInstallTokenMail(token, defaultWalletAddress, sendValue, gasFee, accountTxHeightInAppChain, fromKey);

                String hash = token.getHash();
                String json = newMail.toJson();
                log.info("DeployTokenView broadcast() -> hash = "+hash +" , json = "+json);

                boolean flag = nodeClusterHttpService.broadcast(json, fixAppChainInstanceId);

                if (flag) {

                    CompUtil.showSuccess("Deploy token successfully");

                    walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_DEPLOY_TOKEN, hash, defaultWallet));

                    UI.getCurrent().navigate(WalletSendResultView.class);

                } else {
                    CompUtil.showError("Failed to deploy token");
                }

            } catch (Exception exception) {
                String msg = "Failed to deploy token: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                sendBtn.setEnabled(true);
            }
        });

    }



    private void clearForm() {
        this.password.setValue("");
        this.name.setValue("");
        this.symbol.setValue("");
        this.totalSupply.setValue("");
        this.info.setValue("");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
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
        Wallet wallet = event.getWallet();
        fromAddress.setValue(wallet.getAddress());

        refresh_fromAddressTokenComboBox();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (walletChangeDefaultEventReg != null)
            walletChangeDefaultEventReg.remove();
    }

    private Registration walletChangeDefaultEventReg;

}
