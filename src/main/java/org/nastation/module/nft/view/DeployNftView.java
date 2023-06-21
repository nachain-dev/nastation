package org.nastation.module.nft.view;

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
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.Token;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CollUtil;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.TokenUtil;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Route(value = DeployNftView.Route_Value, layout = MainLayout.class)
@PageTitle(DeployNftView.Page_Title)
public class DeployNftView extends VerticalLayout implements BeforeEnterObserver {

    public static final String Route_Value = "DeployNFTView";
    public static final String Page_Title = "Deploy NFT";

    private TextField fromAddress;

    private ComboBox<Map.Entry<Long, BigInteger>> fromAddressTokenComboBox;

    private TextField name;
    private TextField symbol;
    private TextField info;

    private ComboBox<Token> mintTokenCombo;

    private TextField mintPrices;
    private TextField mintPriceBatch;
    private TextField royaltyPayment;
    private TextField baseUri;

    private PasswordField password;

    private Button sendBtn;
    private Button clearBtn;

    private WalletService walletService;
    private WalletDataService walletDataService;
    private NodeClusterHttpService nodeClusterHttpService;
    private NaScanHttpService naScanHttpService;
    private Set<Map.Entry<Long, BigInteger>> walletBalanceMapEntry = Sets.newHashSet();

    public DeployNftView(
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired AddressRepository addressRepository,
            @Autowired WalletService walletService
    ) {

        this.walletDataService = walletDataService;
        this.walletService = walletService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.naScanHttpService = naScanHttpService;

        refresh_fromAddressTokenComboBox();

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Creator");
        fromAddress.setValue(walletService.getDefaultWalletAddress());
        fromAddress.setReadOnly(true);

        fromAddressTokenComboBox = new ComboBox("Creator Balance ( AppChain Instance )");
        fromAddressTokenComboBox.setItems(walletBalanceMapEntry);
        fromAddressTokenComboBox.setItemLabelGenerator(e->TokenUtil.getTokenSymbol(e.getKey()) + " : " + NumberUtil.bigIntToNacDouble(e.getValue()));
        select_fromAddressTokenComboBox();

        List<Token> enableTokenList = TokenUtil.getEnableTokenExcludeNftList();
        AtomicReference<Token> mintTokenSelectedRef = new AtomicReference<Token>();

        mintTokenCombo = new ComboBox("Mint Token");
        mintTokenCombo.setItems(enableTokenList);
        mintTokenCombo.setItemLabelGenerator(e-> e.getSymbol());
        mintTokenCombo.addValueChangeListener(e -> {
            mintTokenSelectedRef.set(e.getValue());
        });

        name = new TextField("NFT Name");
        name.setValue("");

        symbol = new TextField("NFT Symbol");
        symbol.setValue("");

        info = new TextField("NFT Info");
        info.setValue("");

        mintPrices = new TextField("Mint Prices");
        mintPrices.setValue("");
        mintPrices.setPlaceholder("1 (or 1,2,4)");

        mintPriceBatch = new TextField("Mint Price Batchs");
        mintPriceBatch.setValue("");
        mintPriceBatch.setPlaceholder("1000 (or 1000,10000,100000)");

        //royaltyPayment = new TextField("Royalty Payment");
        //royaltyPayment.setPlaceholder("(0.1=10% , 1=100%)");

        baseUri = new TextField("Resource Base URI");
        baseUri.setPlaceholder("https://static.your_domain.com/images/nft/");

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");

        HorizontalLayout nameLine = new HorizontalLayout(name,symbol);
        nameLine.setFlexGrow(2, name);

        //HorizontalLayout infoLine = new HorizontalLayout(info,royaltyPayment);
        //infoLine.setFlexGrow(2, info);

        //------ form
        add(CompUtil.getCenterFormLayout(title, fromAddress, fromAddressTokenComboBox,  nameLine, info, mintTokenCombo,mintPrices,mintPriceBatch,baseUri,password, sendBtn, clearBtn));

        //------ request

        //------ action

        clearBtn.addClickListener(e -> clearForm());

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {

            try {
                String mintPrices_value = StringUtils.trim(mintPrices.getValue());
                String mintPriceBatch_value = StringUtils.trim(mintPriceBatch.getValue());
                //String royaltyPayment_value = StringUtils.trim(royaltyPayment.getValue());
                String baseUri_value = StringUtils.trim(baseUri.getValue());

                if (StringUtils.isEmpty(mintPrices_value)) {
                    CompUtil.showError("Mint prices can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }
                if (StringUtils.isEmpty(mintPriceBatch_value)) {
                    CompUtil.showError("Mint prices batch can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                //if (StringUtils.isEmpty(royaltyPayment_value)) {
                //    CompUtil.showError("Royalty payment can not be empty");
                //    sendBtn.setEnabled(true);
                //    return;
                //}

                if (StringUtils.isEmpty(baseUri_value)) {
                    CompUtil.showError("Resource base uri can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (mintTokenSelectedRef.get() == null) {
                    CompUtil.showError("Mint token can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                long mintTokenId = mintTokenSelectedRef.get().getId();

                List<BigInteger> mintPriceList = Arrays.stream(mintPrices_value.split(",")).map(k -> Amount.toToken(Long.valueOf(k))).collect(Collectors.toList());

                List<Long> mintPriceBatchList = Arrays.stream(mintPriceBatch_value.split(",")).map(k -> Long.valueOf(k)).collect(Collectors.toList());

                double royaltyPaymentValue = 1D;//Double.valueOf(royaltyPayment_value);

                String passwordVal = password.getValue();

                String walletAddress = walletService.getDefaultWalletAddress();

                HttpResult httpResult = walletService.deployNft(
                        name.getValue(), symbol.getValue(), info.getValue(), walletAddress, String.valueOf(passwordVal), mintTokenId,
                        mintPriceList, mintPriceBatchList, royaltyPaymentValue, baseUri_value
                );

                Boolean flag = httpResult.getFlag();

                if (flag) {

                    clearForm();

                    walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_NFT_DEPLOY, String.valueOf(httpResult.getData()), walletService.getDefaultWallet()));

                    UI.getCurrent().navigate(WalletSendResultView.class);

                    CompUtil.showSuccess(httpResult.getMessage());

                } else {
                    CompUtil.showError(httpResult.getMessage());
                }

            } catch (Exception exception) {
                String msg = "Failed to deploy nft token from view: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                sendBtn.setEnabled(true);
            }
        });

    }

    private void refresh_fromAddressTokenComboBox() {

        clear_fromAddressTokenComboBox();

        //prepare token balance
        long fixAppChainInstanceId = CoreInstanceEnum.APPCHAIN.id;
        String defaultWalletAddress = walletService.getDefaultWalletAddress();

        UsedTokenBalanceDetail usedTokenBalanceDetail = this.naScanHttpService.getUsedTokenBalanceDetail(defaultWalletAddress, fixAppChainInstanceId);

        if (usedTokenBalanceDetail != null &&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            walletBalanceMapEntry = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
        }else{
            walletBalanceMapEntry = Sets.newHashSet();
        }

        if (fromAddressTokenComboBox !=null) {
            fromAddressTokenComboBox.clear();
            fromAddressTokenComboBox.setItems(walletBalanceMapEntry);
            fromAddressTokenComboBox.getDataProvider().refreshAll();

            if (CollUtil.isNotEmpty(walletBalanceMapEntry)) {
                Optional<Map.Entry<Long, BigInteger>> ifNac = walletBalanceMapEntry.stream().filter(e -> e.getKey().longValue() == CoreTokenEnum.NAC.id).findFirst();
                if (ifNac.isPresent()) {
                    if(fromAddressTokenComboBox!=null) fromAddressTokenComboBox.setValue(ifNac.get());
                }
            }
        }
    }

    private void select_fromAddressTokenComboBox() {

        if (CollUtil.isNotEmpty(walletBalanceMapEntry)) {
            Optional<Map.Entry<Long, BigInteger>> ifNac = walletBalanceMapEntry.stream().filter(e -> e.getKey().longValue() == CoreTokenEnum.NAC.id).findFirst();
            if (ifNac.isPresent()) {
                if(fromAddressTokenComboBox!=null) fromAddressTokenComboBox.setValue(ifNac.get());
            }
        }
    }

    private void clear_fromAddressTokenComboBox() {

        if (fromAddressTokenComboBox !=null) {
            fromAddressTokenComboBox.clear();
            fromAddressTokenComboBox.getDataProvider().refreshAll();
        }
    }

    private void clearForm() {
        this.password.setValue("");
        this.name.setValue("");
        this.symbol.setValue("");
        this.mintPrices.setValue("");
        this.mintPriceBatch.setValue("");
        //this.royaltyPayment.setValue("");
        this.baseUri.setValue("");
        this.info.setValue("");
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
