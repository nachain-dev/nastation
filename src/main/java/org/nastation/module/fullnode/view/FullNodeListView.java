package org.nastation.module.fullnode.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
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
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.module.fullnode.data.FullNodeRow;
import org.nastation.module.protocol.service.TxDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.nastation.module.fullnode.view.FullNodeListView.Page_Title;

@PageTitle(Page_Title)
@Route(value = "FullNodeList/:id?/:action?(edit)", layout = MainLayout.class)
@Slf4j
public class FullNodeListView extends Div implements BeforeEnterObserver {

    public static final String Page_Title = "FullNode List";
    public static final String Route_Value = "FullNodeList";
    public static final String TAB_MINE = "Mine";
    public static final String TAB_ALL = "All";

    private final String PARAM_ID = "id";
    private final String EDIT_ROUTE_TEMPLATE = "FullNodeList/%s/edit";

    private Grid<FullNodeRow> grid = new Grid<>(FullNodeRow.class, false);

    private TextField idText;
    private TextField ownerAddressText;
    private TextField beneficiaryAddressText;
    private TextField paidNomcTx;
    private TextField enabledText;

    private Button refresh = new Button("Refresh");

    private BeanValidationBinder<FullNodeRow> binder;

    private FullNodeRow txDataRow;
    private String selectedTabLabel = TAB_MINE;

    private WalletService walletService;
    private TxDataService txDataService;
    private EcologyUrlService ecologyUrlService;

    public FullNodeListView(
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

        /*Configure Grid*/

        /*Order ID*/
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortHash(rowData.getId()));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildTxUrlByScan(rowData.getId(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit tx detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("Order ID").setResizable(true);

        /*Owner*/
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String OperatorAddress = rowData.getOperatorAddress();

            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(OperatorAddress));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildAccountUrlByScan(OperatorAddress, currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("Owner").setResizable(true);

        /*Beneficiary*/
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String OwnerAddress = rowData.getOwnerAddress();

            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(OwnerAddress));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildAccountUrlByScan(OwnerAddress, currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("Beneficiary").setResizable(true);

        /*RequiredNomc*/
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String PaidNomc = rowData.getPaidNomc();
            String requiredNomc = rowData.getRequiredNomc();

            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(PaidNomc + " / " + requiredNomc);
            button.addClickListener(e -> {
            });
        })).setAutoWidth(true).setHeader("Required NOMC").setResizable(true);

        /*RequiredNac*/
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String PaidNac = rowData.getPaidNac();
            String requiredNac = rowData.getRequiredNac();

            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(PaidNac + " / " + requiredNac);
            button.addClickListener(e -> {
            });
        })).setAutoWidth(true).setHeader("Required NAC").setResizable(true);

        grid.addColumn("paidNomcTx").setAutoWidth(true).setHeader("Paid NomcTx");

        /*PaidNomcTx*/
        /*
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            String PaidNomcTx = rowData.getPaidNomcTx();

            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortHash(PaidNomcTx));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildAccountUrlByScan(PaidNomcTx, currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });
        })).setAutoWidth(true).setHeader("Paid Nomc Tx").setResizable(true);
        */

        /*Action*/
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, rowData) -> {
            Button payNacBtn = new Button("Pay NAC");
            payNacBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_SMALL);

            Button editOwnerBtn = new Button("Edit Beneficiary");
            editOwnerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST,ButtonVariant.LUMO_SMALL);

            Button activateBtn = new Button("Activate");
            activateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setClassName("w-full flex-wrap");
            buttonLayout.setSpacing(true);
            buttonLayout.add(payNacBtn, editOwnerBtn,activateBtn);
            div.add(buttonLayout);

        })).setAutoWidth(true).setHeader("Action").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();

        refreshGrid();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                String id = event.getValue().getId();
                UI.getCurrent().navigate(String.format(EDIT_ROUTE_TEMPLATE, id));
            } else {
                clearForm();
                UI.getCurrent().navigate(FullNodeListView.class);
            }
        });

        GridContextMenu<FullNodeRow> menu = grid.addContextMenu();
        menu.addItem("View in NaScan", event -> {
            Optional<FullNodeRow> row = event.getItem();
            if (row.isPresent()) {
                String url = this.ecologyUrlService.buildTxUrlByScan(row.get().getPaidNomcTx(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit tx detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(FullNodeRow.class);

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
        Optional<String> idOpt = event.getRouteParameters().get(PARAM_ID);
        if (idOpt.isPresent()) {

            long currentInstanceId = walletService.getCurrentInstanceId();

            FullNodeRow row = new FullNodeRow();//txDataService.getFullNodeRow(currentInstanceId, idOpt.get());
            if (row != null) {
                populateForm(row);
            } else {

                CompUtil.showError(String.format("The requested data was not found, Hash = %d", idOpt.get()));

                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FullNodeListView.class);
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
        idText = new TextField("Order Id");
        ownerAddressText = new TextField("Owner Address");
        beneficiaryAddressText = new TextField("Beneficiary Address");
        paidNomcTx = new TextField("Paid Nomc Tx");
        enabledText = new TextField("Status");
        Component[] fields = new Component[]{idText, ownerAddressText, beneficiaryAddressText, paidNomcTx, enabledText};

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
        Tab lastTab = new Tab(TAB_ALL);
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
        List<FullNodeRow> dataList = Lists.newArrayList();

        /*
        for (int i = 1; i <= 10; i++) {
            String addr = "N123" + i;

            FullNodeRow one = new FullNodeRow();
            one.setId(String.valueOf(i));
            one.setOperatorAddress(addr);
            one.setOwnerAddress(addr);
            one.setBeneficiaryAddress(addr);
            one.setRequiredNomc(String.valueOf(2));
            one.setRequiredNac(String.valueOf(286));
            one.setPaidNomc(String.valueOf(i));
            one.setPaidNac(String.valueOf(i));
            one.setPaidNomcTx(addr+i*110000);
            one.setEnabled(true);
            dataList.add(one);
        }
        */

        if (selectedTabLabel.equals(TAB_MINE)) {
            //dataList = txDataService.getMineDataList(currentInstanceId);
        } else if (selectedTabLabel.equals(TAB_ALL)) {
            //dataList = txDataService.getLastDataList(currentInstanceId);
        }

        grid.setItems(dataList);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(FullNodeRow value) {
        this.txDataRow = value;
        binder.readBean(this.txDataRow);
    }

    public static abstract class FullNodeListViewEvent extends ComponentEvent<FullNodeListView> {
        protected FullNodeListViewEvent(FullNodeListView source) {
            super(source, false);
        }
    }

    public static class LoadingOpenEvent extends FullNodeListViewEvent {
        LoadingOpenEvent(FullNodeListView source) {
            super(source);
        }
    }

    public static class LoadingCloseEvent extends FullNodeListViewEvent {
        LoadingCloseEvent(FullNodeListView source) {
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
