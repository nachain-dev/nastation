package org.nastation.module.wallet.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.util.*;
import org.nastation.components.FlexBoxLayout;
import org.nastation.components.ListItem;
import org.nastation.components.QrImageSource;
import org.nastation.components.layout.size.Bottom;
import org.nastation.components.layout.size.Horizontal;
import org.nastation.components.layout.size.Top;
import org.nastation.components.layout.size.Vertical;
import org.nastation.components.style.BoxShadowBorders;
import org.nastation.components.style.LumoStyles;
import org.nastation.components.style.css.WhiteSpace;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.protocol.service.BlockDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.WalletRow;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Route(value = WalletProfileFormView.Route_Value + "/:walletID?/:action?(view)", layout = MainLayout.class)
@PageTitle(WalletProfileFormView.Page_Title)
@Slf4j
public class WalletProfileFormView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "WalletProfileForm";
    public static final String Page_Title = "Wallet Profile";
    private static final String WALLET_ID = "walletID";
    public static final String WALLET_PROFILE_ROUTE_TEMPLATE = Route_Value + "/%d/view";

    private ListItem balanceListItem;
    private ListItem addressListItem;
    private ListItem addTimeListItem;
    private WalletRow walletRow;
    private Image image;
    private BlockDataService blockDataService;
    private EcologyUrlService ecologyUrlService;
    private WalletService walletService;
    private NaScanHttpService naScanHttpService;

    public WalletProfileFormView(
            @Autowired WalletService walletService,
            @Autowired BlockDataService blockDataService,
            @Autowired NaScanHttpService naScanHttpService,
            @Autowired EcologyUrlService ecologyUrlService
    ) {
        addClassName("wallet-profile-form-view");
        this.walletService = walletService;
        this.blockDataService = blockDataService;
        this.ecologyUrlService = ecologyUrlService;
        this.naScanHttpService = naScanHttpService;
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(
                createTopButtons(),
                createLogoSection(),
                createRecentTransactionsHeader(),
                createRecentTransactionsList(),
                createMonthlyOverviewHeader(),
                createMonthlyOverviewChart()
        );
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_L);
        content.setMaxWidth("840px");
        return content;
    }

    private Component createTopButtons() {

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidthFull();
        hl.setPadding(false);

        Button back = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        back.addClickListener(e -> UI.getCurrent().navigate(WalletListView.class));

        Button send = new Button("Send", new Icon(VaadinIcon.ARROW_RIGHT));
        send.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        send.getStyle().set("margin-left", "auto");
        send.addClickListener(e -> UI.getCurrent().navigate(TransferFormView.class));

        hl.add(back, send);
        return hl;
    }

    private FlexBoxLayout createLogoSection() {

        image = new Image();
        image.addClassName(LumoStyles.Margin.Horizontal.L);

        String address = this.walletRow.getAddress();
        double usdTotal = this.walletService.getBalanceAsUsd(this.walletRow);

        try {
            QrImageSource qis = new QrImageSource();
            qis.setText(address);qis.setHeight(200);qis.setWidth(200);
            image = CompUtil.createQrImage(qis);
        } catch (IOException e) {
            log.error("Create qrcode image error ", e);
        }

        /* Address */
        addressListItem = new ListItem(CompUtil.createTertiaryIcon(VaadinIcon.OPTION), "", "Address");
        addressListItem.setDividerVisible(true);
        addressListItem.setId("accountInProfile");
        addressListItem.setReverse(true);
        addressListItem.setWhiteSpace(WhiteSpace.PRE_LINE);
        addressListItem.setPrimaryText("");

        /* Balance */
        balanceListItem = new ListItem(CompUtil.createTertiaryIcon(VaadinIcon.WALLET), "", "Balance");
        balanceListItem.getPrimary().addClassName(LumoStyles.Heading.H2);
        balanceListItem.setDividerVisible(true);
        balanceListItem.setId("balanceInProfile");
        balanceListItem.setReverse(true);
        balanceListItem.setPrimaryText("");

        /* Add Time */
        addTimeListItem = new ListItem(CompUtil.createTertiaryIcon(VaadinIcon.CALENDAR), "", "Add Time");
        addTimeListItem.setReverse(true);

        /* Set value */
        addressListItem.setPrimaryText(address);
        balanceListItem.setPrimaryText(String.valueOf(MathUtil.round(usdTotal,4)));
        addTimeListItem.setPrimaryText(DateUtil.formatDateTimeFull(walletRow.getAddTime()));

        FlexBoxLayout listItems = new FlexBoxLayout(addressListItem, balanceListItem, addTimeListItem);
        listItems.setFlexDirection(FlexLayout.FlexDirection.COLUMN);

        FlexBoxLayout section = new FlexBoxLayout(image, listItems);
        section.addClassName(BoxShadowBorders.BOTTOM);
        section.setAlignItems(FlexComponent.Alignment.CENTER);
        section.setFlex("1", listItems);
        section.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        section.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        section.setPadding(Bottom.L);
        return section;
    }

    private Component createRecentTransactionsHeader() {
        Label title = CompUtil.createH3Label("Tokens");

        Button viewAll = CompUtil.createSmallButton("View");
        viewAll.addClassName(LumoStyles.Margin.Left.AUTO);
        viewAll.addClickListener(e -> {
            //UI.getCurrent().navigate(TxDataListView.class);

            long currentInstanceId = this.walletService.getCurrentInstanceId();
            String url = ecologyUrlService.buildAccountUrlByScan(this.walletRow.getAddress(), currentInstanceId);
            getUI().ifPresent(ui -> ui.getPage().open(url));
        });

        FlexBoxLayout header = new FlexBoxLayout(title, viewAll);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.M, Horizontal.RESPONSIVE_L, Top.L);
        return header;
    }



    private Component createRecentTransactionsList() {
        Div items = new Div();
        items.addClassNames(BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L);

        String address = this.walletRow.getAddress();
        long currentInstanceId = walletService.getCurrentInstanceId();

        //prepare token balance
        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(address, currentInstanceId);
        Set<Map.Entry<Long, BigInteger>> entries = null;

        if (usedTokenBalanceDetail != null&&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();

            int count = 0;
            for (Map.Entry<Long, BigInteger> entry : entries) {
                Long tokenId = entry.getKey();
                BigInteger value = entry.getValue();

                double amount = NumberUtil.bigIntToNacDouble(value);
                ListItem item = new ListItem(
                        walletService.selectTokenIcon(tokenId),
                        TokenUtil.getTokenSymbol(tokenId),
                        TokenUtil.getToken(tokenId).getName(),
                        new Span(String.valueOf(amount))
                );
                item.setDividerVisible(count++ != entries.size()-1);
                items.add(item);
            }

        }

        double usdTotal = walletService.calcUsedTokenBalanceUsdTotal(usedTokenBalanceDetail);
        balanceListItem.setPrimaryText(String.valueOf(MathUtil.round(usdTotal,4)));

         /*
         int recentTxs = 4;
         for (int i = 0; i < recentTxs; i++) {
            Double amount = DummyData.getAmount();
            Label amountLabel = CompUtil.createAmountLabel(amount);
            if (amount > 0) {
                CompUtil.setTextColor(TextColor.SUCCESS, amountLabel);
            } else {
                CompUtil.setTextColor(TextColor.ERROR, amountLabel);
            }
            ListItem item = new ListItem(
                    DummyData.getLogo(),
                    "DummyData.getCompany()",
                    CompUtil.formatDate(LocalDate.now().minusDays(i)),
                    amountLabel
            );
            // Dividers for all but the last item
            item.setDividerVisible(i < recentTxs - 1);
            items.add(item);
        }*/

        //CompUtil.createAmountLabel(Double.valueOf(nacBalance))

        return items;
    }

    private Component createMonthlyOverviewHeader() {
        Label header = CompUtil.createH3Label("Monthly Overview");
        header.addClassNames(LumoStyles.Margin.Vertical.L, LumoStyles.Margin.Responsive.Horizontal.L);
        return header;
    }

    private Component createMonthlyOverviewChart() {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();
        conf.setTitle("");
        conf.getLegend().setEnabled(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Amount");

        // Withdrawals and deposits
        ListSeries withDrawals = new ListSeries("Send");
        ListSeries deposits = new ListSeries("Receive");

        for (int i = 0; i < 12; i++) {
            withDrawals.addData(0D);
            deposits.addData(0D);
        }

        conf.addSeries(withDrawals);
        conf.addSeries(deposits);

        FlexBoxLayout card = new FlexBoxLayout(chart);
        card.setHeight("400px");
        return card;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        Optional<Integer> walletId = event.getRouteParameters().getInteger(WALLET_ID);
        if (walletId.isPresent()) {
            long currentInstanceId = walletService.getCurrentInstanceId();
            Optional<WalletRow> walletFromBackend = walletService.getWalletRow(walletId.get(), currentInstanceId);
            if (walletFromBackend.isPresent()) {
                populateViewData(walletFromBackend.get());
            } else {
                CompUtil.showError(String.format("The requested wallet was not found, ID = %d", walletId.get()));
                event.forwardTo(WalletListView.class);
            }
        }

    }

    private void populateViewData(WalletRow walletRow) {
        this.walletRow = walletRow;
        String address = this.walletRow.getAddress();

        add(createContent());

        UI.getCurrent().getPage().setTitle(address);
    }
}
