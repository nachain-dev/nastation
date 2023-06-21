package org.nastation.module.nft.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
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
import org.nachain.core.token.nft.NftContentTypeEnum;
import org.nachain.core.token.nft.NftItem;
import org.nachain.core.token.nft.NftItemAttr;
import org.nachain.core.token.nft.NftItemDetail;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.nft.dto.NftItemWrap;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.model.PageWrap;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CollUtil;
import org.nastation.common.util.CompUtil;
import org.nastation.module.nft.service.NftDomainService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route(value = NftItemListView.Route_Value + "/:ID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(NftItemListView.Page_Title)
@Slf4j
public class NftItemListView extends Div implements BeforeEnterObserver {

    public static final String Page_Title = "NFT Item List";
    public static final String Route_Value = "NftItemList";
    private final String ID = "ID";
    private final String WALLET_EDIT_ROUTE_TEMPLATE = Route_Value + "/%d/edit";

    private Grid<NftItemWrap> grid = new Grid<>(NftItemWrap.class, false);

    private ComboBox<NftCollWrap> nftCollectionCombo;

    private TextField nftItemIdTf;
    private TextField nftItemNameTf;

    private Image previewImage;

    private ComboBox<String> propCombo;

    private Button prevPageBtn;
    private Button nextPageBtn;
    private Button searchBtn;

    private Button refresh = new Button("Refresh");

    private NftItemWrap nftItemWrap;

    private WalletService walletService;

    private WalletRepository walletRepository;
    private NaScanHttpService naScanHttpService;
    private NodeClusterHttpService nodeClusterHttpService;
    private EcologyUrlService ecologyUrlService;

    private NftDomainService nftDomainService;

    private Long selectNftItemId = 0L;
    private NftItemWrap selectNftItemWrap;

    private List<NftCollWrap> dataList;

    public NftItemListView(
            @Autowired EcologyUrlService ecologyUrlService,
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired NftDomainService nftDomainService,
            @Autowired WalletService walletService
    ) {
        addClassNames("nftItem-list-view", "flex", "flex-col", "h-full");
        this.naScanHttpService = naScanHttpService;
        this.ecologyUrlService = ecologyUrlService;
        this.nftDomainService = nftDomainService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.walletService = walletService;

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        //splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(new ComponentRenderer<>(Span::new, (span, nftItemWrap) -> {
            long nftItemId = nftItemWrap.getNftItemId();
            span.setText(String.valueOf(nftItemId));
        })).setAutoWidth(true).setHeader("NFT ID").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, nftItemWrap) -> {
            NftItemDetail nid = nftItemWrap.getNftItemDetail();
            if (nid != null) {
                span.setText(nid.getName());
                span.getElement().getThemeList().add("badge");
            }
        })).setAutoWidth(true).setHeader("NFT Name").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Image::new, (image, nftItemWrap) -> {
            NftItemDetail nid = nftItemWrap.getNftItemDetail();

            if (nid != null) {
                image.setHeight("50px");
                image.setWidth("50px");

                if (NftContentTypeEnum.IMAGE.name.equals(nid.getContentType().name)) {
                    image.setSrc(StringUtils.defaultIfEmpty(nid.getPreview(), ""));
                }
            }
        })).setAutoWidth(true).setHeader("Preview").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, nftItemWrap) -> {
            NftItemDetail nid = nftItemWrap.getNftItemDetail();
            if (nid != null) {
                span.setText(nid.getContentType().name);
            }
        })).setAutoWidth(true).setHeader("Content Type").setResizable(true);

        /*
        grid.addColumn(new ComponentRenderer<>(ComboBox<String>::new, (comboBox, nftItemWrap) -> {
            NftItemDetail nid = nftItemWrap.getNftItemDetail();
            if (nid != null) {
                List<NftItemAttr> attrList = nid.getProperties();
                if (CollUtil.isNotEmpty(attrList)) {
                    List<String> list = Lists.newArrayList();

                    for (NftItemAttr attr :attrList) {
                        list.add(String.format("%s : %s(%s)", attr.getName(), attr.getValue(), attr.getPercent()+"%"));
                    }

                    comboBox.setItems(list);
                    if (list.size() > 0) {
                        comboBox.setValue(list.get(0));
                    }
                }

            }
        })).setAutoWidth(true).setHeader("Properties").setResizable(true);
        */

        grid.addColumn(new ComponentRenderer<>(Div::new, (div, nftItemWrap) -> {

            Button viewBtn = new Button("View");
            viewBtn.addClickListener(e -> {
                long nftItemId = nftItemWrap.getNftItemId();

                NftItem nftItem = nftItemWrap.getNftItem();
                long token = nftItem.getToken();
                long instance = nftItem.getInstance();

                String url = ecologyUrlService.buildNftItemDetailUrlByScan(nftItemId,token,instance);
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setClassName("w-full flex-wrap");
            buttonLayout.setSpacing(true);
            buttonLayout.add(viewBtn);
            div.add(buttonLayout);

        })).setAutoWidth(true).setHeader("Action").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.setHeightFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                selectNftItemWrap = event.getValue();
                selectNftItemId = event.getValue().getNftItemId();

                UI.getCurrent().navigate(String.format(WALLET_EDIT_ROUTE_TEMPLATE, selectNftItemId));

            } else {
                UI.getCurrent().navigate(NftItemListView.class);
            }
        });

        GridContextMenu<NftItemWrap> menu = grid.addContextMenu();
        menu.addItem("Copy tx hash", event -> {
            Optional<NftItemWrap> row = event.getItem();
            if (row.isPresent()) {
                NftItemWrap nftItemWrap = row.get();
                String fromTx = nftItemWrap.getNftItem().getFromTx();
                CompUtil.setClipboardText(fromTx);
            }
        });
        menu.addItem("Show description", event -> {
            Optional<NftItemWrap> row = event.getItem();
            if (row.isPresent()) {
                NftItemWrap nftItemWrap = row.get();
                String desc = nftItemWrap.getNftItemDetail().getDescription();
                CompUtil.setClipboardText(desc);
            }
        });
        menu.addItem("Preview", event -> {
            Optional<NftItemWrap> row = event.getItem();
            if (row.isPresent()) {

            }
        });

        refreshGrid();

        refresh.addClickListener(e -> {
            refreshGrid();
        });

    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> ID = event.getRouteParameters().getInteger(this.ID);
        if (ID.isPresent() && pageWrap != null) {

            List<NftItemWrap> nftItemWrapList = pageWrap.getDataList();

            if (CollUtil.isEmpty(nftItemWrapList)) {
                return;
            }

            Integer idVal = ID.get();
            Optional<NftItemWrap> targetNftItemWrap = nftItemWrapList.stream().filter(item -> String.valueOf(item.getNftItemId()).equals(String.valueOf(idVal))).findFirst();

            if (targetNftItemWrap.isPresent()) {
                populateForm(targetNftItemWrap.get());
            } else {
                CompUtil.showError(String.format("The requested nft item was not found, nft item id = %d", ID.get()));
                refreshGrid();
                event.forwardTo(NftItemListView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        nftItemIdTf = new TextField("NFT ID");
        nftItemIdTf.setReadOnly(true);

        nftItemNameTf = new TextField("NFT Name");
        nftItemNameTf.setReadOnly(true);

        previewImage = new Image();
        previewImage.setHeight("100px");
        previewImage.setWidth("100px");

        Div imgDiv = new Div();
        imgDiv.setWidthFull();
        imgDiv.add(previewImage);

        propCombo = new ComboBox<>("Properties");
        Component[] fields = new Component[]{imgDiv,nftItemIdTf, nftItemNameTf,propCombo};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        refresh.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(refresh);
        editorLayoutDiv.add(buttonLayout);
    }

    public void resetPageNumber () {
        currentPageNumber = 1;
    }



    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setPadding(true);
        searchLayout.setWidthFull();

        List<NftCollWrap> dataList = nftDomainService.getDefaultWalletNftCollWrapList();

        nftCollectionCombo = new ComboBox();
        nftCollectionCombo.setWidth("400px");
        nftCollectionCombo.setPlaceholder("My NFT Collection");
        nftCollectionCombo.setItems(dataList);
        nftCollectionCombo.setItemLabelGenerator(e -> nftDomainService.nftCollLabelGenerator(e));
        nftCollectionCombo.addValueChangeListener(e -> {
            NftCollWrap coll = e.getValue();
            currentNftCollWrap = coll;

            resetPageNumber();
            refreshGrid();
        });

        searchBtn = new Button("Search");
        searchBtn.getStyle().set("margin-right", "auto");// expands the empty space right of button two

        prevPageBtn = new Button("Prev");
        nextPageBtn = new Button("Next");

        searchLayout.add(nftCollectionCombo,searchBtn,prevPageBtn,nextPageBtn);

        wrapper.add(searchLayout);
        wrapper.add(grid);

        searchBtn.addClickListener(e -> {
            resetPageNumber();
            refreshGrid();
        });

        prevPageBtn.addClickListener(e -> {
            if (pageWrap != null && currentPageNumber != 0) {
                currentPageNumber = currentPageNumber-1;
                refreshGrid();
            }
        });

        nextPageBtn.addClickListener(e -> {
            if (pageWrap != null) {
                int totalPages = pageWrap.getPageTotal();
                if (currentPageNumber != totalPages) {
                    currentPageNumber = currentPageNumber+1;
                    refreshGrid();
                }
            }
        });
    }

    private void refresh_nftCollectionCombo() {
        dataList = nftDomainService.getDefaultWalletNftCollWrapList();

        if (nftCollectionCombo != null) {
            nftCollectionCombo.setItems(dataList);
            nftCollectionCombo.getDataProvider().refreshAll();
        }
    }


    private int currentPageNumber = 0;

    private NftCollWrap currentNftCollWrap;

    private PageWrap<NftItemWrap> pageWrap;

    private void refreshGrid() {
        String address = walletService.getDefaultWalletAddress();

        if (currentNftCollWrap != null) {

            NftCollection nftCollection = currentNftCollWrap.getNftCollection();
            long token = nftCollection.getToken();

            pageWrap = nodeClusterHttpService.getAccountNftItemWrapPageByNodeCluster(
                    currentNftCollWrap, address, token, currentPageNumber
            );

            List<NftItemWrap> dataList = pageWrap.getDataList();
            if (CollUtil.isNotEmpty(dataList)) {
                grid.setItems(dataList);
                grid.getDataProvider().refreshAll();
            }

        }

    }

    private void clearGrid() {
        currentPageNumber = 1;
        grid.setItems(Lists.newArrayList());
        grid.getDataProvider().refreshAll();
    }

    private void populateForm(NftItemWrap itemWrap) {

        if (itemWrap == null) {
            return;
        }

        this.nftItemWrap = itemWrap;

        long nftItemId = this.nftItemWrap.getNftItemId();
        String nftItemName = this.nftItemWrap.getNftItemDetail().getName();

        nftItemIdTf.setValue(String.valueOf(nftItemId));
        nftItemNameTf.setValue(nftItemName);

        NftItemDetail nftItemDetail = itemWrap.getNftItemDetail();
        if (nftItemDetail != null) {
            String preview = nftItemDetail.getPreview();
            if (NftContentTypeEnum.IMAGE.name.equals(nftItemDetail.getContentType().name)) {
                previewImage.setSrc(StringUtils.defaultIfEmpty(preview, ""));
            }
        }

        if (nftItemDetail != null) {

            List<NftItemAttr> attrList  = nftItemDetail.getProperties();
            if (CollUtil.isNotEmpty(attrList)) {
                List<String> list = Lists.newArrayList();

                for (NftItemAttr attr :attrList) {
                    list.add(String.format("%s : %s(%s)", attr.getName(), attr.getValue(), attr.getPercent()+"%"));
                }

                propCombo.setItems(list);
                if (list.size() > 0) {
                    propCombo.setValue(list.get(0));
                }
            }
        }

    }

    private Registration walletChangeDefaultEventReg;

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        refresh_nftCollectionCombo();
        clearGrid();
    }
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {
            walletChangeDefaultEventReg = ComponentUtil.addListener(mainLayout, WalletChangeDefaultEvent.class, event -> {walletChangeDefaultEventHandler(event);});
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (walletChangeDefaultEventReg != null) walletChangeDefaultEventReg.remove();
    }

}
