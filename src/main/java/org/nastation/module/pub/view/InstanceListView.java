package org.nastation.module.pub.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.chain.structure.instance.Instance;
import org.nastation.common.event.InstanceChangeEvent;
import org.nastation.common.event.InstanceRefreshEvent;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Route(value = InstanceListView.Route_Value + "/:instanceID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(InstanceListView.Page_Title)
@Slf4j
public class InstanceListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "InstanceList";
    public static final String Page_Title = "Instance List";

    private final String ID = "instanceID";

    private Grid<Instance> grid = new Grid<>(Instance.class, false);

    private List<Instance> instanceList = Lists.newArrayList();

    private EcologyUrlService ecologyUrlService;
    private WalletService walletService;

    public void intInstanceListData(){

        if (WalletDataService.MERGE_API_PACK_VO != null) {
            instanceList = WalletDataService.MERGE_API_PACK_VO.getInstanceList();
        }

    }

    public InstanceListView(@Autowired EcologyUrlService ecologyUrlService, @Autowired WalletService walletService) {
        this.ecologyUrlService = ecologyUrlService;
        this.walletService = walletService;
        addClassNames("flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);

        intInstanceListData();

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true).setHeader("ID");
        grid.addComponentColumn(row -> createNameBadge(row)).setAutoWidth(true).setHeader("Name");
        grid.addColumn("symbol").setAutoWidth(true).setHeader("Symbol");

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, instance) -> {
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(instance.getAppAddress()));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildInstanceDetailPageByScan(instance.getId());
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });
        })).setAutoWidth(true).setHeader("App Address").setResizable(true);

        grid.addComponentColumn(row -> createInfoBadge(row)).setAutoWidth(true).setHeader("Info");
        grid.addColumn("appVersion").setAutoWidth(true).setHeader("Version");
        //grid.addColumn("hash").setAutoWidth(true).setHeader("Hash");

        grid.addColumn(new ComponentRenderer<>(Div::new, (div, instRow) -> {

            long currentInstanceId = walletService.getCurrentInstance().getId();

            if (currentInstanceId == instRow.getId()) {

                Span badge = new Span("Default");
                badge.getElement().getThemeList().add("badge success");
                div.add(badge);

            }else{
                Button viewBtn = new Button("Change");
                viewBtn.addClickListener(e -> {

                    long instId = instRow.getId();

                    String symbol = instRow.getSymbol();

                    // save data
                    walletService.setCurrentInstance(instId);

                    log.info("Has changed current instance = " + symbol);
                    CompUtil.showSuccess("Current instance: " + symbol);

                    ComponentUtil.fireEvent(UI.getCurrent(), new InstanceChangeEvent(this, walletService.getCurrentInstance()));

                });

                HorizontalLayout buttonLayout = new HorizontalLayout();
                buttonLayout.setClassName("w-full flex-wrap");
                buttonLayout.setSpacing(true);
                buttonLayout.add(viewBtn);
                div.add(buttonLayout);
            }

        })).setAutoWidth(true).setHeader("Action").setResizable(true);
        
        grid.setItems(instanceList);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        GridContextMenu<Instance> menu = grid.addContextMenu();
        menu.addItem("Copy address", event -> {
            Optional<Instance> row = event.getItem();
            if (row.isPresent()) {
                String tokenAddress = row.get().getAppAddress();
                CompUtil.setClipboardText(tokenAddress);
            }
        });
    }

    private Component createNameBadge(Instance inst) {
        String theme;
        String name = inst.getAppName();

        Optional<CoreInstanceEnum> target = Arrays.stream(CoreInstanceEnum.values()).filter(t -> t.id == inst.getId()).findFirst();
        if (target.isPresent()) {
            theme = "badge";
        }else{
            theme = "badge success";
        }
        Span badge = new Span(name);
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private Component createInfoBadge(Instance inst) {
        String info = inst.getInfo();

        if (info.length() > 70) {
            info = info.substring(0, 70) +"...";
        }

        Span badge = new Span(info);
        return badge;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> instanceID = event.getRouteParameters().getInteger(ID);

        //refresh instance menu
        ComponentUtil.fireEvent(UI.getCurrent(), new InstanceRefreshEvent(this, walletService.getCurrentInstance()));

    }


    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //System.out.println("TransferFormView onAttach = " + attachEvent);

        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {

            instanceChangeEventReg = ComponentUtil.addListener(
                    UI.getCurrent(),
                    InstanceChangeEvent.class,
                    event -> {
                        instanceChangeEventHandler(event);
                    }
            );
        }

    }

    private void instanceChangeEventHandler(InstanceChangeEvent event) {
        intInstanceListData();

        grid.setItems(instanceList);
        grid.getDataProvider().refreshAll();

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        if (instanceChangeEventReg != null)
            instanceChangeEventReg.remove();
    }

    private Registration instanceChangeEventReg;

}