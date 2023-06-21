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
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.protocol.NFTProtocol;
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.MathUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.service.WalletService;
import org.nastation.module.wallet.view.WalletSendResultView;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Route(value = MintNftView.Route_Value, layout = MainLayout.class)
@PageTitle(MintNftView.Page_Title)
public class MintNftView extends VerticalLayout implements BeforeEnterObserver{

    public static final String Route_Value = "MintNftView";
    public static final String Page_Title = "Mint NFT";

    private TextField fromAddress;
    private ComboBox<Map.Entry<Long, BigInteger>> fromTokenComboBox;

    private ComboBox<NftCollWrap> nftCollectionCombo;

    private TextField fee;

    private TextField mintAmount;
    private TextField mintCost;

    private PasswordField password;

    private Button sendBtn;
    private Button clearBtn;

    private Set<Map.Entry<Long, BigInteger>> walletTokenBalanceEntrySet = Sets.newHashSet();

    private WalletService walletService;
    private NaScanHttpService naScanHttpService;
    private NodeClusterHttpService nodeClusterHttpService;

    private NftCollWrap currentNftCollWrap;

    private List<NftCollWrap> nftCollectionList = Lists.newArrayList();

    public MintNftView(
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired WalletService walletService
    ) {

        addClassName("transfer-form-view");

        this.walletService = walletService;
        this.naScanHttpService = naScanHttpService;
        this.nodeClusterHttpService = nodeClusterHttpService;

        //------ comp

        H2 title = CompUtil.getTextCenterH2(Page_Title);

        fromAddress = new TextField("Minter");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromTokenComboBox = new ComboBox("NFT Instance Tokens");
        fromTokenComboBox.setItems(walletTokenBalanceEntrySet);
        fromTokenComboBox.setItemLabelGenerator(e -> CompUtil.tokenBalanceEntryLabelGenerator(e));

        nftCollectionCombo = new ComboBox("NFT Collection");
        nftCollectionCombo.setItems(nftCollectionList);
        nftCollectionCombo.setItemLabelGenerator(e -> CompUtil.nftCollLabelGenerator(e));
        nftCollectionCombo.addValueChangeListener(e -> {
            NftCollWrap nftColl = e.getValue();

            if (nftColl != null) {
                currentNftCollWrap = nftColl;
                long instance = nftColl.getNftCollection().getInstance();

                refresh_fromTokenComboBox(instance);
                refresh_gasFee(instance);
            }
        });

        mintAmount = new TextField("Mint Amount");
        mintAmount.addValueChangeListener(event -> {
            try {

                Long amount = Long.valueOf(mintAmount.getValue());
                NftCollWrap nftCollWrap = nftCollectionCombo.getValue();

                if (nftCollWrap != null) {

                    NftCollection nftColl = nftCollWrap.getNftCollection();

                    long mintTokenId1 = nftColl.getNftProtocol().getMintTokenId();
                    String tokenSymbol = TokenUtil.getTokenSymbol(mintTokenId1);
                    mintCost.setSuffixComponent(new Span(tokenSymbol));

                    long instance = nftColl.getInstance();
                    long token = nftColl.getToken();
                    BigInteger nftOrderTotalValue = nodeClusterHttpService.getNftOrderTotal(instance, token, amount);
                    double nftOrderTotalDouble = NumberUtil.bigIntToNacDouble(nftOrderTotalValue);
                    mintCost.setValue(String.valueOf(MathUtil.round(nftOrderTotalDouble, 8)));
                }
            } catch (Exception e) {
                log.error("When mint amount change but count the mint cost error", e);
            }

        });

        mintCost = new TextField("Mint Cost");
        mintCost.setReadOnly(true);

        fee = CompUtil.getNacFeeText("Fee");

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");

        add(CompUtil.getCenterFormLayout(title, fromAddress, nftCollectionCombo,fromTokenComboBox, mintAmount,mintCost, password,fee, sendBtn, clearBtn));

        //------ action

        clearBtn.addClickListener(e -> clearForm());

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {

            try {
                Wallet defaultWallet = walletService.getDefaultWallet();

                String mintAmountText = StringUtils.trim(mintAmount.getValue());
                String pswText = StringUtils.trim(password.getValue());

                if (StringUtils.isBlank(pswText)) {
                    CompUtil.showError("Password can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }
                if (StringUtils.isBlank(mintAmountText)) {
                    CompUtil.showError("Mint amount can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (currentNftCollWrap == null) {
                    CompUtil.showError("Please select one nft collection first");
                    sendBtn.setEnabled(true);
                    return;
                }

                NftCollection nftCollection = currentNftCollWrap.getNftCollection();

                long nftInstanceId = nftCollection.getInstance();
                long nftTokenId = nftCollection.getToken();
                long mintTokenId = nftCollection.getNftProtocol().getMintTokenId();

                HttpResult result = walletService.mintNft(nftInstanceId, mintTokenId, nftTokenId,defaultWallet.getAddress(), Long.valueOf(mintAmountText), password.getValue());

                if (!result.getFlag()) {
                    CompUtil.showError(result.getMessage());
                    return;
                }

                Map<String, String> map = (Map<String, String>) result.getData();
                String txHash = map.get("hash");

                clearForm();

                walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_NFT_MINT, txHash, defaultWallet));

                UI.getCurrent().navigate(WalletSendResultView.class);

                CompUtil.showTxSuccess(Page_Title);

            } catch(Exception exception){
                String msg = CompUtil.showTxFail(Page_Title);;
                log.error(msg, exception);
            }finally{
                sendBtn.setEnabled(true);
            }

        });

    }

    private void refresh_nftCollectionCombo() {
        nftCollectionList = nodeClusterHttpService.getAllNftCollectionByNodeCluster();

        if (nftCollectionCombo != null) {
            nftCollectionCombo.setItems(nftCollectionList);
            nftCollectionCombo.getDataProvider().refreshAll();
        }

    }

    private void refresh_gasFee(long currentInstanceId) {
        BigInteger gasFee = nodeClusterHttpService.get_gasFee(currentInstanceId);
        double gasFeeDouble = NumberUtil.bigIntToNacDouble(gasFee);

        fee.setValue(gasFeeDouble + " NAC");
    }


    private void refresh_fromTokenComboBox(long instanceId) {

        //prepare token balance
        String defaultWalletAddress = walletService.getDefaultWalletAddress();
        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWalletAddress, instanceId);

        if (usedTokenBalanceDetail != null && usedTokenBalanceDetail.getTokenBalanceMap() != null) {
            walletTokenBalanceEntrySet = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
        }else{
            walletTokenBalanceEntrySet = Sets.newHashSet();
        }

        if (fromTokenComboBox == null) {
            return;
        }

        /*handle UI*/
        fromTokenComboBox.clear();
        fromTokenComboBox.setItems(walletTokenBalanceEntrySet);
        fromTokenComboBox.getDataProvider().refreshAll();

        if (currentNftCollWrap ==null) {
            return;
        }

        NftCollection nftCollection = currentNftCollWrap.getNftCollection();

        NFTProtocol nftProtocol = nftCollection.getNftProtocol();
        if (nftProtocol == null) {
            return;
        }

        /*handle selection*/
        long mintTokenId = nftProtocol.getMintTokenId();
        Optional<Map.Entry<Long, BigInteger>> first = walletTokenBalanceEntrySet.stream().filter(k -> k.getKey().longValue() == mintTokenId).findFirst();

        if (first.isPresent()) {
            fromTokenComboBox.setValue(first.get());
        }

    }

    private void clearForm() {
        //Stream<Component> children = this.getChildren();
        //List<Component> collect = children.collect(Collectors.toList());

        /*
        for (Component component : collect) {
            System.out.println("component = " + component);
            System.out.println("getId = " + component.getId());
            System.out.println("getUI = " + component.getUI());
            System.out.println("getText = " + component.getElement().getText());
            System.out.println("getTag = " + component.getElement().getTag());
            System.out.println("getNode = " + component.getElement().getNode().toString());
        }
        */

        this.password.setValue("");
        this.mintAmount.setValue("");
        this.mintCost.setValue("");
        this.password.setValue("");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        long currentInstanceId = walletService.getCurrentInstanceId();

        refresh_nftCollectionCombo();

        refresh_gasFee(currentInstanceId);
    }
    private void instanceChangeEventHandler(InstanceChangeEvent event) {
    }

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());

        if (currentNftCollWrap != null) {
            NftCollection nftCollection = currentNftCollWrap.getNftCollection();
            refresh_fromTokenComboBox(nftCollection.getInstance());
        }

    }
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {
            walletChangeDefaultEventReg = ComponentUtil.addListener(mainLayout, WalletChangeDefaultEvent.class, event -> {walletChangeDefaultEventHandler(event);});
            instanceChangeEventReg = ComponentUtil.addListener(UI.getCurrent(), InstanceChangeEvent.class, event -> {instanceChangeEventHandler(event);});
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (walletChangeDefaultEventReg != null) walletChangeDefaultEventReg.remove();
        if (instanceChangeEventReg != null) instanceChangeEventReg.remove();
    }

    private Registration walletChangeDefaultEventReg;
    private Registration instanceChangeEventReg;

}
