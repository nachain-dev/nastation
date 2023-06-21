package org.nastation.module.pub.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.compress.utils.Lists;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.Token;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.InstanceUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.data.service.WalletDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = TokenListView.Route_Value + "/:tokenID?/:action?(edit)", layout = MainLayout.class)
@PageTitle(TokenListView.Page_Title)
public class TokenListView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "TokenList";
    public static final String Page_Title = "Token List";

    private final String ID = "tokenID";

    private Grid<Token> grid = new Grid<>(Token.class, false);

    private List<Token> tokenList = Lists.newArrayList();
    private EcologyUrlService ecologyUrlService;

    public TokenListView(@Autowired EcologyUrlService ecologyUrlService) {
        this.ecologyUrlService = ecologyUrlService;

        addClassNames( "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);

        if (WalletDataService.MERGE_API_PACK_VO != null) {
            tokenList  =WalletDataService.MERGE_API_PACK_VO.getTokenList().stream().filter(e -> e.getId() > 0).collect(Collectors.toList());
        }

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true).setHeader("Token ID");
        grid.addComponentColumn(row -> createNameBadge(row)).setAutoWidth(true).setHeader("Token Name");
        grid.addColumn("symbol").setAutoWidth(true).setHeader("Token Symbol");

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, token) -> {
            long id = token.getId();
            Instance inst = InstanceUtil.getInstance(token.getInstanceId());
            String tokenAddress = inst.getAppAddress();

            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setText(WalletUtil.shortAddress(tokenAddress));
            button.addClickListener(e -> {
                String url = this.ecologyUrlService.buildTokenDetailPageByScan(id);
                getUI().ifPresent(ui -> ui.getPage().open(url));
            });
        })).setAutoWidth(true).setHeader("Token Address").setResizable(true);


        grid.addColumn(new ComponentRenderer<>(Span::new, (span, row) -> {

            BigInteger initialAmount = row.getInitialAmount();
            double value = NumberUtil.bigIntToNacDouble(initialAmount);
            span.setText(String.format("%.8f",value));

        })).setAutoWidth(true).setHeader("Initial Amount").setResizable(true);;
        grid.addComponentColumn(row -> createInfoBadge(row)).setAutoWidth(true).setHeader("Token Info");


        grid.setItems(tokenList);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        GridContextMenu<Token> menu = grid.addContextMenu();
        menu.addItem("Copy address", event -> {
            Optional<Token> row = event.getItem();
            if (row.isPresent()) {

                long id = row.get().getId();
                Instance token_token = InstanceUtil.getInstance(id);
                String tokenAddress = token_token.getAppAddress();

                CompUtil.setClipboardText(tokenAddress);
            }
        });

    }

    private Component createNameBadge(Token token) {
        String theme;
        String name = token.getName();

        Optional<CoreTokenEnum> target = Arrays.stream(CoreTokenEnum.values()).filter(t -> t.id == token.getId()).findFirst();
        if (target.isPresent()) {
            theme = "badge";
        }else{
            theme = "badge success";
        }
        Span badge = new Span(name);
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> tokenID = event.getRouteParameters().getInteger(ID);
    }


    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private Component createInfoBadge(Token token) {
        String info = token.getInfo();

        if (info.length() > 70) {
            info = info.substring(0, 70) +"...";
        }

        Span badge = new Span(info);
        return badge;
    }


}