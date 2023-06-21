package org.nastation.module.pub.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.compress.utils.Lists;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.Promotion;

import java.util.List;
import java.util.Optional;

@Route(value = PromotionListView.Route_Value + "/:PromotionID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(PromotionListView.Page_Title)
public class PromotionListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "PromotionList";
    public static final String Page_Title = "Promotion List";

    //    String clientName;
    //    String wallet;
    //    String fromTx;

    private final String ID = "PromotionID";

    private Grid<Promotion> grid = new Grid<>(Promotion.class, false);

    private List<Promotion> list = Lists.newArrayList();

    public PromotionListView() {
        addClassNames("my-vote-list-view", "flex", "flex-col", "h-full");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);

        if (WalletDataService.MERGE_API_PACK_VO != null) {
            list = WalletDataService.MERGE_API_PACK_VO.getPromotionList();
        }

        // Configure Grid
        grid.addColumn("clientName").setAutoWidth(true).setHeader("Client Name");
        grid.addColumn("wallet").setAutoWidth(true).setHeader("Wallet Address");
        grid.addColumn("fromTx").setAutoWidth(true).setHeader("From Transaction");

        grid.setItems(list);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> PromotionID = event.getRouteParameters().getInteger(ID);
    }


    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

}