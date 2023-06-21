package org.nastation.module.nft.view;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.nft.NftItemDetail;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.nft.dto.NftItemWrap;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CollUtil;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.components.AccountAddressField;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.address.data.Address;
import org.nastation.module.address.repo.AddressRepository;
import org.nastation.module.nft.service.NftDomainService;
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
import java.util.stream.Collectors;

@Slf4j
@Route(value = TransferNftView.Route_Value, layout = MainLayout.class)
@PageTitle(TransferNftView.Page_Title)
public class TransferNftView extends VerticalLayout implements BeforeEnterObserver {

    public static final String Route_Value = "TransferNftView";
    public static final String Page_Title = "Transfer NFT";

    private TextField fromAddress;

    private ComboBox<Map.Entry<Long, BigInteger>> fromTokenComboBox;
    private Set<Map.Entry<Long, BigInteger>> usedTokenBalanceEntries = Sets.newHashSet();

    private ComboBox<NftCollWrap> nftCollWrapCombo;
    private ComboBox<NftItemWrap> nftItemWrapCombo;

    private AccountAddressField toAddress;

    private NftCollWrap selectNftCollWrap;

    private TextField fee;

    private PasswordField password;

    private Button sendBtn;

    private WalletService walletService;
    private NaScanHttpService naScanHttpService;
    private NodeClusterHttpService nodeClusterHttpService;

    private AddressRepository addressRepository;
    private NftDomainService nftDomainService;

    public TransferNftView(
            @Autowired AddressRepository addressRepository,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired NftDomainService nftDomainService,
            @Autowired WalletService walletService
    ) {
        addClassName("transfer-form-view");

        this.walletService = walletService;
        this.naScanHttpService = naScanHttpService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.addressRepository = addressRepository;
        this.nftDomainService = nftDomainService;

        String defaultWalletAddress = walletService.getDefaultWalletAddress();

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Sender");
        fromAddress.setValue(defaultWalletAddress);
        fromAddress.setReadOnly(true);

        //--- nftCollectionCombo
        nftCollWrapCombo = new ComboBox("NFT Collection");
        nftCollWrapCombo.setWidth("250px");
        nftCollWrapCombo.setItemLabelGenerator(e -> nftDomainService.nftCollLabelGenerator(e));
        nftCollWrapCombo.addValueChangeListener(e -> {
            NftCollWrap nftCollWrap = e.getValue();
            if (nftCollWrap != null) {
                selectNftCollWrap = e.getValue();

                refreshFromTokenComboBox();

                refreshGasFee();

                refreshNftItemComboboxData();
            }
        });

        //--- NftItemGrid
        //initNftItemGrid();
        initNftItemWrapCombo();

        //--- FromTokenComboBox
        initFromTokenComboBox();

        //--- FromTokenComboBox
        VerticalLayout nftItemGridBox = new VerticalLayout();
        nftItemGridBox.setPadding(false);
        nftItemGridBox.setSpacing(false);

        HorizontalLayout nftCollAndTokenLayout = new HorizontalLayout(nftCollWrapCombo, fromTokenComboBox);
        nftCollAndTokenLayout.setFlexGrow(1.0, nftCollWrapCombo);

        nftItemGridBox.add(nftCollAndTokenLayout);
        //nftItemGridBox.add(nftItemGrid);

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

        fee = new TextField("Fee");
        fee.setValue("- NAC");
        fee.setReadOnly(true);

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //------ form
        add(CompUtil.getCenterFormLayout(title, fromAddress, nftItemGridBox,  nftItemWrapCombo, toAddress, password, fee, sendBtn));

        //------ request

        refreshNftCollectionCombo();

        //------ action

        sendBtn.setDisableOnClick(true);
        sendBtn.addClickListener(e -> {

            try {
                Wallet wallet = walletService.getDefaultWallet();
                String toAddressText = StringUtils.trim(toAddress.getAddressTxtField().getValue());
                String pswText = StringUtils.trim(password.getValue());

                if (wallet == null) {
                    CompUtil.showError("Current wallet can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(toAddressText)) {
                    CompUtil.showError("To address can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (StringUtils.isBlank(pswText)) {
                    CompUtil.showError("Password can not be empty");
                    sendBtn.setEnabled(true);
                    return;
                }

                if (selectNftCollWrap == null) {
                    CompUtil.showError("Please select one nft collection first");
                    sendBtn.setEnabled(true);
                    return;
                }

                NftCollection nftCollection = selectNftCollWrap.getNftCollection();
                long nftCollInstance = nftCollection.getInstance();
                long token = nftCollection.getToken();

                HttpResult result = walletService.sendNft(nftCollInstance, token, selectNftItemIdList, wallet.getAddress(), toAddressText, password.getValue());

                if (result.getFlag()) {
                    Map<String, String> map = (Map<String, String>) result.getData();

                    String txHash = map.get("hash");

                    /* UI show */

                    clearForm();

                    walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_NFT_SEND, txHash, wallet));

                    UI.getCurrent().navigate(WalletSendResultView.class);

                    CompUtil.showSuccess("Transfer nft transaction has been sent");

                } else {
                    CompUtil.showError(result.getMessage());
                }

            } catch (Exception exception) {
                String msg = "Transfer nft failed from view [exception] ";
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                sendBtn.setEnabled(true);
            }

        });
    }

    private void initNftItemListBox() {

        nftListBox = new MultiSelectListBox<>();
        nftListBox.setRenderer(new ComponentRenderer<>(itemSwap -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setPadding(false);
            row.setSpacing(false);
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            NftItemDetail nftItemDetail = itemSwap.getNftItemDetail();

            Image image = new Image();
            image.setSrc(nftItemDetail == null ? "" : nftItemDetail.getPreview());

            row.add(image);
            row.add(nftItemDetail == null ? "" : nftItemDetail.getName());

            return row;
        }));

        nftListBox.setHeight("200px");

        nftListBox.addSelectionListener(e -> {
            Set<NftItemWrap> allSelectedItems = e.getAllSelectedItems();

            if (CollUtil.isEmpty(allSelectedItems)) {
                return;
            }

            selectNftItemIdList = allSelectedItems.stream().map(item -> item.getNftItemId()).collect(Collectors.toList());

        });
    }

    private void initFromTokenComboBox() {
        fromTokenComboBox = new ComboBox<Map.Entry<Long, BigInteger>>("NFT Instance Token Balances");
        fromTokenComboBox.setAllowCustomValue(false);
        fromTokenComboBox.setItemLabelGenerator(e -> TokenUtil.getTokenSymbol(e.getKey()) + " : " + String.format("%.8f", NumberUtil.bigIntToNacDouble(e.getValue())));
        fromTokenComboBox.setWidth("250px");

    }

    private void initNftItemWrapCombo() {
        nftItemWrapCombo = new ComboBox<NftItemWrap>("Target NFT Item");
        nftItemWrapCombo.setAllowCustomValue(false);
        nftItemWrapCombo.setItemLabelGenerator(e -> nftItemLabelGen(e));

        nftItemWrapCombo.addValueChangeListener(e -> {
            NftItemWrap nftColl = e.getValue();

            if (nftColl == null) {
                return;
            }

            long nftItemId = nftColl.getNftItemId();

            if (selectNftItemIdList != null) {
                selectNftItemIdList.clear();
                selectNftItemIdList.add(nftItemId);
            }

        });
    }

    private String nftItemLabelGen(NftItemWrap e) {
        NftItemDetail nftItemDetail = e.getNftItemDetail();
        if (nftItemDetail != null) {
            String name = nftItemDetail.getName();
            return "#" + e.getNftItemId() +" "+ name ;
        } else {
            return "#" + e.getNftItemId();
        }

    }

    private void refreshFromTokenComboBox() {
        refreshWalletTokenBalanceList();
        if (fromTokenComboBox != null) {
            fromTokenComboBox.setItems(usedTokenBalanceEntries);
            fromTokenComboBox.getDataProvider().refreshAll();

            if (CollUtil.isNotEmpty(usedTokenBalanceEntries)) {
                Optional<Map.Entry<Long, BigInteger>> ifNac = usedTokenBalanceEntries.stream().filter(e -> e.getKey().longValue() == CoreTokenEnum.NAC.id).findFirst();
                if (ifNac.isPresent()) {
                    fromTokenComboBox.setValue(ifNac.get());
                }
            }
        }
    }

    private Grid<NftItemWrap> nftItemGrid = new Grid<>(NftItemWrap.class, false);

    private MultiSelectListBox<NftItemWrap> nftListBox = new MultiSelectListBox<>();

    private List<Long> selectNftItemIdList = Lists.newArrayList();

    private List<NftItemWrap> currentAllAccountNftItemWrapList;

    private void initNftItemGrid() {

        nftItemGrid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            NftItemDetail nftItemDetail = row.getNftItemDetail();

            if (nftItemDetail == null) {
                span.setText(nftItemDetail.getPreview());
            }
        })).setAutoWidth(true).setHeader("Preview").setResizable(true);

        nftItemGrid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            long nftItemId = row.getNftItemId();
            span.setText(String.valueOf(nftItemId));
        })).setAutoWidth(true).setHeader("NFT ID").setResizable(true);

        nftItemGrid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            NftItemDetail nftItemDetail = row.getNftItemDetail();

            if (nftItemDetail == null) {
                span.setText(nftItemDetail.getName());
            }
        })).setAutoWidth(true).setHeader("NFT Name").setResizable(true);

        ///------------
        nftItemGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        nftItemGrid.addSelectionListener(selection -> {

            Set<NftItemWrap> allSelectedItems = selection.getAllSelectedItems();

            if (CollUtil.isEmpty(allSelectedItems)) {
                return;
            }

            selectNftItemIdList = allSelectedItems.stream().map(e -> e.getNftItemId()).collect(Collectors.toList());
        });

    }

    private void refreshNftItemGridData() {

        if (selectNftCollWrap == null) {
            return;
        }

        String defaultWalletAddress = walletService.getDefaultWalletAddress();

        currentAllAccountNftItemWrapList = nodeClusterHttpService.getAllAccountNftItemWrap(defaultWalletAddress, selectNftCollWrap);
        if (CollUtil.isEmpty(currentAllAccountNftItemWrapList)) {
            return;
        }
        nftItemGrid.setItems(currentAllAccountNftItemWrapList);
        nftItemGrid.getDataProvider().refreshAll();
    }

    private void refreshNftItemListBoxData() {

        if (selectNftCollWrap == null) {
            return;
        }

        String defaultWalletAddress = walletService.getDefaultWalletAddress();

        currentAllAccountNftItemWrapList = nodeClusterHttpService.getAllAccountNftItemWrap(defaultWalletAddress, selectNftCollWrap);
        if (CollUtil.isEmpty(currentAllAccountNftItemWrapList)) {
            return;
        }
        nftListBox.setItems(currentAllAccountNftItemWrapList);
        nftListBox.getDataProvider().refreshAll();
    }

    private void refreshNftItemComboboxData() {

        if (selectNftCollWrap == null) {
            return;
        }

        String defaultWalletAddress = walletService.getDefaultWalletAddress();

        currentAllAccountNftItemWrapList = nodeClusterHttpService.getAllAccountNftItemWrap(defaultWalletAddress, selectNftCollWrap);
        if (CollUtil.isEmpty(currentAllAccountNftItemWrapList)) {
            return;
        }
        nftItemWrapCombo.setItems(currentAllAccountNftItemWrapList);
        nftItemWrapCombo.getDataProvider().refreshAll();
    }

    public void refreshNftCollectionCombo() {
        List<NftCollWrap> dataList = nftDomainService.getDefaultWalletNftCollWrapList();
        if (nftCollWrapCombo != null) {
            nftCollWrapCombo.clear();
            nftCollWrapCombo.setItems(dataList);
            nftCollWrapCombo.getDataProvider().refreshAll();
        }
    }

    public void clear_fromTokenComboBox() {
        if (fromTokenComboBox != null) {
            fromTokenComboBox.clear();
            fromTokenComboBox.setItems(Lists.newArrayList());
            fromTokenComboBox.getDataProvider().refreshAll();
        }
    }
    public void clear_nftItemWrapCombo() {
        if (nftItemWrapCombo != null) {
            nftItemWrapCombo.clear();
            nftItemWrapCombo.setItems(Lists.newArrayList());
            nftItemWrapCombo.getDataProvider().refreshAll();
        }
    }

    private void refreshGasFee() {

        if (selectNftCollWrap != null) {

            long instance = selectNftCollWrap.getNftCollInstanceId();

            BigInteger gasFee = nodeClusterHttpService.get_gasFee(instance);
            double gasFeeDouble = NumberUtil.bigIntToNacDouble(gasFee);

            fee.setValue(gasFeeDouble + " NAC");
        }

    }



    private void refreshWalletTokenBalanceList() {

        if (selectNftCollWrap != null) {
            long instance = selectNftCollWrap.getNftCollInstanceId();

            //prepare token balance
            String defaultWalletAddress = walletService.getDefaultWalletAddress();
            UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWalletAddress, instance);

            if (usedTokenBalanceDetail != null && usedTokenBalanceDetail.getTokenBalanceMap() != null) {
                usedTokenBalanceEntries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
            }
        }

    }


    private void clearForm() {
        this.password.setValue("");
        toAddress.getAddressTxtField().setValue("");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
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

        String address = walletService.getDefaultWalletAddress();
        fromAddress.setValue(address);

        selectNftCollWrap = null;

        refreshNftCollectionCombo();
        clear_fromTokenComboBox();
        clear_nftItemWrapCombo();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (walletChangeDefaultEventReg != null)
            walletChangeDefaultEventReg.remove();

    }

    private Registration walletChangeDefaultEventReg;


}
