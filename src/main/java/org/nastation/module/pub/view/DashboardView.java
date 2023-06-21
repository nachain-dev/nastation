package org.nastation.module.pub.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.components.WrapperCard;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.MergeApiPackVo;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Route(value = DashboardView.Route_Value, layout = MainLayout.class)
@PageTitle(DashboardView.Page_Title)
public class DashboardView extends Div {

    public static final String Route_Value = "DashboardView";
    public static final String Page_Title = "Dashboard";

    public DashboardView(
            @Autowired WalletDataService walletDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired WalletService walletService
    ) {

        addClassName("dashboard-view");

        Board board = new Board();
        board.addRow(
                new H3("Basic Information:")
        );

        long currentInstanceId = walletService.getCurrentInstanceId();
        long height = walletDataService.getCurrentSyncBlockHeightFromCache(currentInstanceId);
        long lastBlockHeight = naScanHttpService.getLastBlockHeight(currentInstanceId);

        Map<String, Double> priceMap = walletDataService.getPriceMap();
        Double nacPrice = priceMap.get("NAC");
        Double nomcPrice = priceMap.get("NOMC");
        double gasFeeValue = naScanHttpService.getGasFee(CoreInstanceEnum.NAC.id);

        int instSize = 0;
        int tokenSize = 0;
        int promotionSize = 0;
        MergeApiPackVo mergeApiPackVo = WalletDataService.MERGE_API_PACK_VO;
        if (mergeApiPackVo != null) {
            instSize = mergeApiPackVo.getInstanceList().size();
            tokenSize = mergeApiPackVo.getTokenList().size();
            promotionSize = mergeApiPackVo.getPromotionList().size();
        }

        //BsAlert alertPrimary = new BsAlert().withColor(BsColor.PRIMARY);
        //alertPrimary.addContent(new Span("A simple primary alert—check it out!"));
        //String ipAddress = VaadinSession.getCurrent().getBrowser().getAddress();

        board.addRow(
                createBadge("Local Blocks", new H4(String.valueOf(height)), "primary-text", "Local Blocks", "badge success"),
                createBadge("Newest Blocks", new H4(String.valueOf(lastBlockHeight)), "primary-text", "Newest Blocks", "badge success"),
                createBadge("Network", new H4("Mainnet"), "primary-text", "Network", "badge success")
        );
        board.addRow(
                createBadge("NAC Price", new H4(String.valueOf(nacPrice)), "primary-text", "NAC Price", "badge success"),
                createBadge("NOMC Price", new H4(String.valueOf(nomcPrice)), "primary-text", "NOMC Price", "badge success"),
                createBadge("Gas Fee", new H4(String.valueOf(gasFeeValue)), "primary-text", "Gas Fee", "badge success")
        );
        board.addRow(
                createBadge("Instance Total", new H4(String.valueOf(instSize)), "primary-text", "Instance Total", "badge success"),
                createBadge("Token Total", new H4(String.valueOf(tokenSize)), "primary-text", "Token Total", "badge success"),
                createBadge("Promotion Total", new H4(String.valueOf(promotionSize)), "primary-text", "Promotion Total", "badge success")
        );
        /*
        board.addRow(
                createBadge("Blocks", new H2("96582"), "primary-text", "Current blocks in the blockchain", "badge"),
                createBadge("Address", new H2("36517"), "success-text", "Account Address in the blockchain", "badge success"),
                createBadge("Full Nodes", new H2("50"), "error-text", "Full Nodes in the blockchain", "badge error")
        );
        board.addRow(
                createBadge("NOMC pledged", new H2("9562"), "primary-text", "Total NOMC of NAC pledged", "badge"),
                createBadge("NAC destroyed", new H2("3652"), "success-text", "Total number of NAC destroyed", "badge success"),
                createBadge("NAC pledged", new H2("69358"), "error-text", "Total number of NAC pledged", "badge error")
        );*/
        add(board);

    }

    private WrapperCard createBadge(String title, H4 h4, String h2ClassName, String description, String badgeTheme) {
        Div titleSpan = new Div();
        titleSpan.setText(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        h4.addClassName(h2ClassName);

        Div descriptionSpan = new Div();
        descriptionSpan.setText(description);
        descriptionSpan.addClassName("secondary-text");

        return new WrapperCard("wrapper", new Component[]{descriptionSpan, h4}, "card");
    }

}
