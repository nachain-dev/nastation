package org.nastation.module.protocol.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.nachain.core.chain.block.Block;
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.JsonUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.module.protocol.data.BlockDataRow;
import org.nastation.module.protocol.service.BlockDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@PageTitle("Block Data List")
@Route(value = "BlockDataList/:height?/:action?(edit)", layout = MainLayout.class)
@Slf4j
public class BlockDataListView extends Div implements BeforeEnterObserver {

    public static final String Page_Title = "Block Data List";
    public static final String Route_Value = "BlockDataList";

    private final String BLOCKDATA_ID = "height";
    private final String BLOCKDATA_EDIT_ROUTE_TEMPLATE = "BlockDataList/%d/edit";

    private Grid<BlockDataRow> grid = new Grid<>(BlockDataRow.class, false);

    private TextField heightText;
    private TextField hashText;
    private TextField timeText;
    private TextField minerText;

    private Button refresh = new Button("Refresh");

    private BeanValidationBinder<BlockDataRow> binder;

    private BlockDataRow blockDataRow;

    private BlockDataService blockDataService;
    private EcologyUrlService ecologyUrlService;
    private WalletService walletService;

    public BlockDataListView(
            @Autowired WalletService walletService,
            @Autowired BlockDataService blockDataService,
            @Autowired EcologyUrlService ecologyUrlService
    ) {
        this.blockDataService = blockDataService;
        this.ecologyUrlService = ecologyUrlService;
        this.walletService = walletService;

        long currentInstanceId = walletService.getCurrentInstanceId();

        addClassNames("block-list-view", "flex", "flex-col", "h-full");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("heightText").setAutoWidth(true).setHeader("Height").setResizable(true);

        //grid.addColumn(new ComponentRenderer<>(Span::new, (span, blockDataRow) -> {
        //    span.setText(WalletUtil.shortHash(blockDataRow.getHashText()));
        //})).setAutoWidth(true).setHeader("Hash").setResizable(true);;

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortHash(rowData.getHashText()));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildBlockUrlFromHashByScan(rowData.getHashText(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit block detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });
        })).setAutoWidth(true).setHeader("Hash").setResizable(true);

        grid.addColumn("timeText").setAutoWidth(true).setHeader("Time").setResizable(true);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, rowData) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(rowData.getMinerText()));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildAccountUrlByScan(rowData.getMinerText(), currentInstanceId);
                //LaunchUtil.launchBrowser(url, "Visit account detail url");
                getUI().ifPresent(ui -> ui.getPage().open(url));

            });
        })).setAutoWidth(true).setHeader("Miner").setResizable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();

        grid.setItems(blockDataService.getLastDataRowList(currentInstanceId, 100));

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Block value = event.getValue().getRawBlock();
                Long height = value.getHeight();

                UI.getCurrent().navigate(String.format(BLOCKDATA_EDIT_ROUTE_TEMPLATE, height));
            } else {
                clearForm();
                UI.getCurrent().navigate(BlockDataListView.class);
            }
        });

        GridContextMenu<BlockDataRow> menu = grid.addContextMenu();
        menu.addItem("View in NaScan", event -> {
            Optional<BlockDataRow> row = event.getItem();
            if (row.isPresent()) {
                String url = this.ecologyUrlService.buildBlockUrlFromHashByScan(row.get().getHashText(), currentInstanceId);
                getUI().ifPresent(ui -> ui.getPage().open(url));
                //LaunchUtil.launchBrowser(url, "Visit block detail url");
            }
        });
        menu.addItem("Copy json text", event -> {
            Optional<BlockDataRow> row = event.getItem();
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
        binder = new BeanValidationBinder<>(BlockDataRow.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.forField(heightText).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("height");

        binder.bindInstanceFields(this);

        refresh.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> height = event.getRouteParameters().getInteger(BLOCKDATA_ID);
        if (height.isPresent()) {

            long currentInstanceId = walletService.getCurrentInstanceId();

            BlockDataRow row = blockDataService.getBlockDataRow(currentInstanceId, height.get());
            if (row != null) {
                populateForm(row);
            } else {
                CompUtil.showError(String.format("The requested data was not found, Hash = %d", height.get()));
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(BlockDataListView.class);
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
        heightText = new TextField("Height");
        hashText = new TextField("Hash");
        timeText = new TextField("Time");
        minerText = new TextField("Miner");
        Component[] fields = new Component[]{heightText, hashText, timeText, minerText};

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

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        if (grid != null) {

            long currentInstanceId = walletService.getCurrentInstanceId();
            grid.setItems(blockDataService.getLastDataRowList(currentInstanceId, 100));

            grid.select(null);
            grid.getDataProvider().refreshAll();
        }
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(BlockDataRow value) {
        this.blockDataRow = value;
        binder.readBean(this.blockDataRow);
    }


    private Registration instanceChangeEventReg;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        instanceChangeEventReg = ComponentUtil.addListener(
                UI.getCurrent(),
                InstanceChangeEvent.class,
                event -> {
                    instanceChangeEventHandler(event);
                }
        );

    }

    private void instanceChangeEventHandler(InstanceChangeEvent event) {
        refreshGrid();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {

        if (instanceChangeEventReg != null) {
            instanceChangeEventReg.remove();
        }

        super.onDetach(detachEvent);
    }



}
