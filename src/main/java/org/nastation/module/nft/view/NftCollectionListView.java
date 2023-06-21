package org.nastation.module.nft.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.collection.NftCollectionDetail;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.protocol.NFTProtocol;
import org.nastation.common.model.PageWrap;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.*;
import org.nastation.module.address.view.AddressListView;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.nastation.module.nft.view.NftCollectionListView.Page_Title;
import static org.nastation.module.nft.view.NftCollectionListView.Route_Value;

@Route(value = Route_Value + "/:ID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(Page_Title)
public class NftCollectionListView extends Div implements BeforeEnterObserver {

    private final String INSTANCE_ID = "ID";

    public static final String Route_Value = "NftCollectionList";

    public static final String Page_Title = "NFT Collection List";

    private final String EDIT_ROUTE_TEMPLATE = "NftCollectionList/%d/edit";

    private Grid<NftCollWrap> grid = new Grid<>(NftCollWrap.class, false);

    private WalletService walletService;
    private NaScanHttpService naScanHttpService;
    private NodeClusterHttpService nodeClusterHttpService;
    private EcologyUrlService ecologyUrlService;

    private Button prevPageBtn;
    private Button nextPageBtn;
    private Button searchBtn;

    private int currentPageNumber = 1;

    private PageWrap<NftCollWrap> nftCollectionPageWrap;

    public NftCollectionListView(@Autowired NaScanHttpService naScanHttpService,
                               @Autowired NodeClusterHttpService nodeClusterHttpService,
                                 @Autowired EcologyUrlService ecologyUrlService,
                               @Autowired WalletService walletService
    ) {
        this.naScanHttpService = naScanHttpService;
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.ecologyUrlService = ecologyUrlService;
        this.walletService = walletService;

        addClassNames("flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            Instance instance = InstanceUtil.getInstance(row.getNftCollection().getInstance());
            span.setText(String.valueOf(instance.getId()));
        })).setAutoWidth(true).setHeader("Instance ID").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Image::new, (image, nftCollWrap) -> {
            NftCollectionDetail detail = nftCollWrap.getNftCollectionDetail();

            if (detail != null) {
                image.setHeight("50px");
                image.setWidth("50px");
                image.setSrc(StringUtils.defaultIfEmpty(detail.getCoverIcon(), ""));
            }
        })).setAutoWidth(true).setHeader("Preview").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            long instance = row.getNftCollection().getInstance();
            String instanceName = InstanceUtil.getInstanceName(instance);
            span.setText(instanceName);
            span.getElement().getThemeList().add("badge");
        })).setAutoWidth(true).setHeader("Name").setResizable(true);


        /*
        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            long token = row.getNftCollection().getToken();
            String tokenSymbol = TokenUtil.getTokenSymbol(token);
            span.setText(tokenSymbol);
        })).setAutoWidth(true).setHeader("Token").setResizable(true);
        */

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            NFTProtocol nftProtocol = row.getNftCollection().getNftProtocol();
            long token = nftProtocol.getMintTokenId();
            String tokenSymbol = TokenUtil.getTokenSymbol(token);
            span.setText(tokenSymbol);
        })).setAutoWidth(true).setHeader("Mint Token").setResizable(true);

        /*
        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            NFTProtocol nftProtocol = row.getNftCollection().getNftProtocol();
            NftContentTypeEnum contentType = nftProtocol.getContentType();
            String name = contentType.name;
            span.setText(name);
        })).setAutoWidth(true).setHeader("Content Type").setResizable(true);
        */
        /*
        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            span.setText(row.getNftProtocol().getBaseURI());
        })).setAutoWidth(true).setHeader("Base URI").setResizable(true);
        */

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            NFTProtocol nftProtocol = row.getNftCollection().getNftProtocol();
            String join = StringUtils.join(nftProtocol.getMintPrices().stream().map(e-> NumberUtil.bigIntToNacDouble(e)).collect(Collectors.toList()));
            span.setText(join);
        })).setAutoWidth(true).setHeader("Mint Prices").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
            NFTProtocol nftProtocol = row.getNftCollection().getNftProtocol();
            String join = StringUtils.join(nftProtocol.getMintPricesBatch());
            span.setText(join);
        })).setAutoWidth(true).setHeader("Mint Prices Batch").setResizable(true);

        //grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {
        //    NFTProtocol nftProtocol = row.getNftProtocol();
        //    span.setText(nftProtocol.getRoyaltyPayment()*100 +"%");
        //})).setAutoWidth(true).setHeader("Royalty Payment").setResizable(true);

        GridContextMenu<NftCollWrap> menu = grid.addContextMenu();
        menu.addItem("Copy raw json", event -> {
            Optional<NftCollWrap> row = event.getItem();
            if (row.isPresent()) {

                try {
                    String s = JsonUtil.toJsonByOm(row.get());
                    CompUtil.setClipboardText(s);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        grid.addColumn(new ComponentRenderer<>(Div::new, (div, nftCollWrap) -> {

            Button viewBtn = new Button("View");
            viewBtn.addClickListener(e -> {

                NftCollection nftCollection = nftCollWrap.getNftCollection();
                long instance = nftCollection.getInstance();
                long token = nftCollection.getToken();

                String url = ecologyUrlService.buildNftCollDetailUrlByScan(instance,token);
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setClassName("w-full flex-wrap");
            buttonLayout.setSpacing(true);
            buttonLayout.add(viewBtn);
            div.add(buttonLayout);

        })).setAutoWidth(true).setHeader("Action").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            NftCollWrap value = event.getValue();
            if (value != null) {
                UI.getCurrent().navigate(String.format(EDIT_ROUTE_TEMPLATE, value.getNftCollection().getInstance()));
            } else {
                UI.getCurrent().navigate(NftCollectionListView.class);
            }
        });

        refreshGrid();

    }


    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setPadding(true);
        searchLayout.setWidthFull();

        /*
        List<Instance> nftInstanceList = InstanceUtil.getNftInstanceList();
        nftInstanceCombo = new ComboBox("");
        nftInstanceCombo.setPlaceholder("NFT Instance");
        nftInstanceCombo.setItems(nftInstanceList);
        nftInstanceCombo.setItemLabelGenerator(e -> e.getAppName());
        nftInstanceCombo.addValueChangeListener(e -> {
            Instance inst = e.getValue();
            currSelectInstance = inst;
            refreshGrid();
        });*/

        searchBtn = new Button("Refresh");
        searchBtn.getStyle().set("margin-right", "auto");// expands the empty space right of button two
        prevPageBtn = new Button("Prev");
        nextPageBtn = new Button("Next");

        searchLayout.add(searchBtn,searchBtn,prevPageBtn,nextPageBtn);

        wrapper.add(searchLayout);
        wrapper.add(grid);

        searchBtn.addClickListener(e -> {
            currentPageNumber = 0;
            refreshGrid();
        });

        prevPageBtn.addClickListener(e -> {
            if (nftCollectionPageWrap != null && currentPageNumber != 0) {
                currentPageNumber--;
                refreshGrid();
            }
        });

        nextPageBtn.addClickListener(e -> {
            if (nftCollectionPageWrap != null) {
                int totalPages = nftCollectionPageWrap.getPageTotal();
                if (currentPageNumber != totalPages) {
                    currentPageNumber++;
                    refreshGrid();
                }
            }
        });
    }

    private TextField instanceTf;
    private Image previewImage;
    private TextField tokenTf;
    private TextField baseUriTf;
    private TextField infoTf;

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        previewImage = new Image();
        previewImage.setHeight("100px");
        previewImage.setWidth("100px");

        Div imgDiv = new Div();
        imgDiv.setWidthFull();
        imgDiv.add(previewImage);

        instanceTf = new TextField("Instance");
        tokenTf = new TextField("Token");
        baseUriTf = new TextField("Base URI");
        infoTf = new TextField("Info");
        Component[] fields = new Component[]{imgDiv,instanceTf,tokenTf,baseUriTf,infoTf};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> instanceIdOpt = event.getRouteParameters().getLong(INSTANCE_ID);
        if (instanceIdOpt.isPresent()) {
            Long instanceLong = instanceIdOpt.get();

            if (nftCollectionPageWrap == null) {
                return;
            }

            List<NftCollWrap> dataList = nftCollectionPageWrap.getDataList();

            if (CollUtil.isNotEmpty(dataList)) {
                Optional<NftCollWrap> first = dataList.stream().filter(e -> e.getNftCollection().getInstance() == instanceLong).findFirst();

                if (!first.isPresent()) {
                    return;
                }

                populateForm(first.get());
            } else {
                CompUtil.showError(String.format("The requested NFT collection was not found, ID = %d", instanceIdOpt.get()));
                refreshGrid();
                event.forwardTo(AddressListView.class);
            }
        }
    }

    private void populateForm(NftCollWrap nftCollWrap) {

        if (nftCollWrap == null) {
            return;
        }

        NftCollection nftCollection = nftCollWrap.getNftCollection();

        String instanceSymbol = InstanceUtil.getInstanceName(nftCollection.getInstance());
        String tokenSymbol = TokenUtil.getTokenSymbol(nftCollection.getToken());

        instanceTf.setValue(instanceSymbol);
        tokenTf.setValue(tokenSymbol);

        baseUriTf.setValue(nftCollection.getNftProtocol().getBaseURI());
        infoTf.setValue(nftCollection.getNftProtocol().getBaseURI());

        NftCollectionDetail nftCollectionDetail = nftCollWrap.getNftCollectionDetail();
        if (nftCollectionDetail != null) {
            previewImage.setSrc(nftCollectionDetail.getCoverIcon());
            infoTf.setValue(nftCollectionDetail.getInfo());
        }

    }


    private void refreshGrid() {

        nftCollectionPageWrap = nodeClusterHttpService.getNftCollPage(currentPageNumber);

        List<NftCollWrap> dataList = nftCollectionPageWrap.getDataList();

        if (CollUtil.isNotEmpty(dataList)) {
            grid.setItems(dataList);
            grid.getDataProvider().refreshAll();
        }
    }

}