package org.nastation.module.pub.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.nachain.core.chain.structure.instance.Instance;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.util.InstanceUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.data.ProcessInfo;
import org.nastation.module.pub.service.DataSyncService;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = DataSyncView.Route_Value + "/:itemID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(DataSyncView.Page_Title)
@Slf4j
public class DataSyncView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "DataSyncView";
    public static final String Page_Title = "Data Sync Info";

    private final String ID = "itemID";

    private Grid<ProcessInfo> grid = new Grid<>(ProcessInfo.class, false);

    private EcologyUrlService ecologyUrlService;
    private WalletService walletService;
    private WalletDataService walletDataService;
    private NaScanHttpService naScanHttpService;

    private DataSyncService dataSyncService;

    private Button refresh = new Button("Refresh");

    public DataSyncView(
            @Autowired EcologyUrlService ecologyUrlService,
            @Autowired WalletService walletService,
            @Autowired WalletDataService walletDataService,
            @Autowired DataSyncService dataSyncService,
            @Autowired NaScanHttpService naScanHttpService

    ) {
        this.ecologyUrlService = ecologyUrlService;
        this.walletService = walletService;
        this.walletDataService = walletDataService;
        this.naScanHttpService = naScanHttpService;
        this.dataSyncService = dataSyncService;

        addClassNames("flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(new ComponentRenderer<>(Span::new, (span, info) -> {
            long instanceId = info.getInstanceId();
            span.setText(String.valueOf(instanceId));
        })).setAutoWidth(true).setHeader("ID").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, info) -> {

            long instanceId = info.getInstanceId();

            Instance instance = InstanceUtil.getInstance(instanceId);

            String symbol = instance == null ? "" : instance.getSymbol();

            span.setText(symbol);

        })).setAutoWidth(true).setHeader("Instance").setResizable(true);

        grid.addColumn("currentHeight").setAutoWidth(true).setHeader("Current Block Height");
        grid.addColumn("lastBlockHeight").setAutoWidth(true).setHeader("Latest Block Height");
        grid.addColumn("percent").setAutoWidth(true).setHeader("Percent");

        refreshProcessInfoList();

        refreshGrid();
    }

    private List<ProcessInfo> processInfoList = Lists.newArrayList();

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
    }

    private void refreshProcessInfoList() {
        processInfoList = dataSyncService.getProcessInfoList();
    }

    private void refreshGrid() {
        grid.setItems(processInfoList);
        grid.setHeightFull();
        grid.getDataProvider().refreshAll();
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }


    private Button refreshBtn;

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        /* action buttons */
        VerticalLayout btnLayout = new VerticalLayout();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        btnLayout.setWidthFull();
        btnLayout.setSpacing(false);
        btnLayout.setPadding(false);

        refreshBtn = new Button("Refresh");//VaadinIcon.PLUS_CIRCLE_O.create()
        refreshBtn.setWidthFull();
        refreshBtn.addClickListener(e -> refreshBtnClick());

        btnLayout.add(refreshBtn);
        btnLayout.setFlexGrow(1, refreshBtn);
        formLayout.add(btnLayout);

        editorDiv.add(formLayout);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void refreshBtnClick() {

        refreshProcessInfoList();

        refreshGrid();

    }


}