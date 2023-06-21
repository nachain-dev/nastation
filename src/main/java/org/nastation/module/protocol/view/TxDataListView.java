package org.nastation.module.protocol.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.nachain.core.chain.transaction.Tx;
import org.nachain.core.chain.transaction.TxStatus;
import org.nachain.core.token.Token;
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.JsonUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.module.protocol.data.TxDataRow;
import org.nastation.module.protocol.service.TxDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@PageTitle("Tx Data List")
@Route(value = "TxDataList/:hash?/:height?/:action?(edit)", layout = MainLayout.class)
@Slf4j
public class TxDataListView extends Div implements BeforeEnterObserver {

    public static final String Page_Title = "Tx Data List";
    public static final String Route_Value = "TxDataList";
    public static final String TAB_MINE = "Mine";
    public static final String TAB_LAST = "Last";

    private final String PARAM_HASH = "hash";
    private final String PARAM_HEIGHT = "height";

    private final String TXDATA_EDIT_ROUTE_TEMPLATE = "TxDataList/%s/%d/edit";

    private Grid<TxDataRow> grid = new Grid<>(TxDataRow.class, false);

    private TextField hashText;
    private TextField txHeightText;
    private TextField dateTimeText;
    private TextField fromText;
    private TextField toText;
    private TextField amountText;
    //private TextField feeText;
    private TextField statusText;

    private Button refresh = new Button("Refresh");

    private BeanValidationBinder<TxDataRow> binder;

    private TxDataRow txDataRow;
    private String selectedTabLabel = TAB_LAST;

    private WalletService walletService;
    private TxDataService txDataService;
    private EcologyUrlService ecologyUrlService;

    public TxDataListView(
            @Autowired WalletService walletService,
            @Autowired EcologyUrlService ecologyUrlService,
            @Autowired TxDataService txDataService
    ) {
        this.txDataService = txDataService;
        this.ecologyUrlService = ecologyUrlService;
        this.walletService = walletService;

        long currentInstanceId = walletService.getCurrentInstanceId();

        addClassNames("tx-list-view", "flex", "flex-col", "h-full");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortHash(rowData.getHashText()));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildTxUrlByScan(rowData.getHashText(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit tx detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("Hash").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortHash(rowData.getTxHeightText()));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildBlockUrlFromHeightByScan(Long.valueOf(rowData.getTxHeightText()), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit block detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("Block").setResizable(true);


        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String fromText = rowData.getFromText();

            boolean isAddressHasKeyword = WalletUtil.isAddressHasKeyword(fromText);
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(rowData.getFromText()));
            button.addClickListener(e -> {

                if (isAddressHasKeyword) {
                    return;
                }

                String url = this.ecologyUrlService.buildAccountUrlByScan(rowData.getFromText(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("From Address").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String toText = rowData.getToText();

            boolean isAddressHasKeyword = WalletUtil.isAddressHasKeyword(toText);
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(rowData.getToText()));
            button.addClickListener(e -> {

                if (isAddressHasKeyword) {
                    return;
                }

                String url = this.ecologyUrlService.buildAccountUrlByScan(rowData.getToText(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("To Address").setResizable(true);

        grid.addColumn("dateTimeText").setAutoWidth(true).setHeader("Time");

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, rowData) -> {
            long tokenVal = rowData.getRawTx().getToken();
            Token tokenObj = TokenUtil.getToken(tokenVal);

            span.setText(rowData.getAmountText() + " " + tokenObj.getSymbol());
        })).setAutoWidth(true).setHeader("Amount").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Span::new, (span, rowData) -> {
            boolean isOk = rowData.getRawTx().getStatus() == TxStatus.COMPLETED.value;
            String theme = String.format("badge %s", isOk ? "success" : "");
            span.getElement().setAttribute("theme", theme);
            span.setText(rowData.getStatusText());
        })).setAutoWidth(true).setHeader("Status").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();

        refreshGrid();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {

                Tx value = event.getValue().getRawTx();
                String hash = value.getHash();
                long blockHeight = value.getBlockHeight();

                UI.getCurrent().navigate(String.format(TXDATA_EDIT_ROUTE_TEMPLATE, hash, blockHeight));
            } else {
                clearForm();
                UI.getCurrent().navigate(TxDataListView.class);
            }
        });

        GridContextMenu<TxDataRow> menu = grid.addContextMenu();
        menu.addItem("View in NaScan", event -> {
            Optional<TxDataRow> row = event.getItem();
            if (row.isPresent()) {
                String url = this.ecologyUrlService.buildTxUrlByScan(row.get().getHashText(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit tx detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            }
        });
        menu.addItem("Copy json text", event -> {
                    Optional<TxDataRow> row = event.getItem();
                    if (row.isPresent()) {
                        try {
                            CompUtil.setClipboardText(JsonUtil.toJsonByGson(row.get()));
                        } catch (Exception e) {
                            log.error("Copy json text error", e);
                        }
                    }
                }
        );


        // Configure Form
        binder = new BeanValidationBinder<>(TxDataRow.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.forField(txHeight).withConverter(new StringToLongConverter("Only numbers are allowed")).bind("txHeight");

        binder.bindInstanceFields(this);

        refresh.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> hashOpt = event.getRouteParameters().get(PARAM_HASH);
        Optional<Integer> heightOpt = event.getRouteParameters().getInteger(PARAM_HEIGHT);
        if (hashOpt.isPresent() && heightOpt.isPresent()) {

            long currentInstanceId = walletService.getCurrentInstanceId();

            TxDataRow row = txDataService.getTxDataRow(currentInstanceId, hashOpt.get());
            if (row != null) {
                populateForm(row);
            } else {

                CompUtil.showError(String.format("The requested data was not found, Hash = %d", hashOpt.get()));

                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TxDataListView.class);
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
        hashText = new TextField("Hash");
        txHeightText = new TextField("Block");
        dateTimeText = new TextField("Time");
        fromText = new TextField("From Address");
        toText = new TextField("To Address");
        amountText = new TextField("Amount");
        //feeText = new TextField("Fee");
        statusText = new TextField("Status");
        Component[] fields = new Component[]{hashText, txHeightText, dateTimeText, fromText, toText, amountText,/*feeText, */statusText};

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
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);

        Tab mineTab = new Tab(TAB_MINE);
        Tab lastTab = new Tab(TAB_LAST);
        Tabs tabs = new Tabs(lastTab, mineTab);

        tabs.addSelectedChangeListener(event ->
                refreshTabLabel(event.getSelectedTab())
        );

        wrapper.add(tabs, grid);
    }

    private void refreshTabLabel(Tab selectedTab) {

        selectedTabLabel = selectedTab.getLabel();

        ComponentUtil.fireEvent(UI.getCurrent(), new LoadingOpenEvent(this));

        refreshGrid();

        ComponentUtil.fireEvent(UI.getCurrent(), new LoadingCloseEvent(this));

    }

    private void refreshGrid() {

        grid.select(null);

        long currentInstanceId = walletService.getCurrentInstanceId();
        List<TxDataRow> dataList = Lists.newArrayList();

        if (selectedTabLabel.equals(TAB_MINE)) {
            dataList = txDataService.getMineDataList(currentInstanceId);
        } else if (selectedTabLabel.equals(TAB_LAST)) {
            dataList = txDataService.getLastDataList(currentInstanceId);
        }

        grid.setItems(dataList);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(TxDataRow value) {
        this.txDataRow = value;
        binder.readBean(this.txDataRow);
    }

    public static abstract class TxDataListViewEvent extends ComponentEvent<TxDataListView> {
        protected TxDataListViewEvent(TxDataListView source) {
            super(source, false);
        }
    }

    public static class LoadingOpenEvent extends TxDataListViewEvent {
        LoadingOpenEvent(TxDataListView source) {
            super(source);
        }
    }

    public static class LoadingCloseEvent extends TxDataListViewEvent {
        LoadingCloseEvent(TxDataListView source) {
            super(source);
        }
    }

    private Registration loadingOpenEventReg;
    private Registration loadingCloseEventReg;
    private Registration instanceChangeEventReg;

    private void instanceChangeEventHandler(InstanceChangeEvent event) {
        refreshGrid();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        loadingOpenEventReg = ComponentUtil.addListener(
                UI.getCurrent(),
                LoadingOpenEvent.class,
                event -> {
                    loadingOpenEventHandler(event);
                }
        );

        loadingCloseEventReg = ComponentUtil.addListener(
                UI.getCurrent(),
                LoadingCloseEvent.class,
                event -> {
                    loadingCloseEventHandler(event);
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

    private Notification notification;

    private void loadingOpenEventHandler(LoadingOpenEvent event) {
        notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_CENTER);

        Div text = new Div(
                new Text("Loading...")
        );

        notification.add(text);
        notification.open();
    }

    private void loadingCloseEventHandler(LoadingCloseEvent event) {
        if (notification != null) {
            notification.close();
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (loadingOpenEventReg != null) {
            loadingOpenEventReg.remove();
        }
        if (loadingCloseEventReg != null) {
            loadingCloseEventReg.remove();
        }

        if (instanceChangeEventReg != null) {
            instanceChangeEventReg.remove();
        }

    }





}
