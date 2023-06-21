package org.nastation.module.wallet.view;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.common.service.EcologyUrlService;
import org.nastation.common.util.CompUtil;
import org.nastation.components.Images;
import org.nastation.module.nft.view.DeployNftView;
import org.nastation.module.nft.view.NftItemListView;
import org.nastation.module.pub.view.HomeView;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.token.view.DeployTokenView;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "WalletSendResultView", layout = MainLayout.class)
@PageTitle("Wallet Send Result")
public class WalletSendResultView extends VerticalLayout {

    private Button nextBtn;
    private Button copyBtn;
    private Button viewBtn;

    private WalletService walletService;

    public WalletSendResultView(
            @Autowired WalletService walletService ,
            @Autowired EcologyUrlService ecologyUrlService
    ) {
        this.walletService = walletService;

        long currentInstanceId = walletService.getCurrentInstanceId();

        WalletTxSentWrapper wrap = this.walletService.getCurrentWalletTxSentWrapper();

        if (wrap == null) {
            UI.getCurrent().navigate(HomeView.class);
            return;
        }

        if (wrap.getInstanceId() != -1) {
            currentInstanceId = wrap.getInstanceId();
        }

        String hash = wrap.getHash();
        int fromView = wrap.getFromView();

        this.getStyle()
                .set("display","block")
                .set("padding","5em")
                .set("text-align","center");

        H2 title = new H2("Transaction sent successfully");
        title.getStyle().set("text-align", "center");

        Image icon = Images.success();
        icon.getStyle().set("width", "96px");

        Div iconWrap = new Div();
        iconWrap.setWidthFull();
        iconWrap.getStyle().set("text-align", "center");
        iconWrap.add(icon);

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        copyBtn = new Button("Copy");

        viewBtn = new Button("View");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.add(copyBtn);
        layout.add(viewBtn);
        layout.add(nextBtn);

        Html desc = new Html("<div style='text-align:center'>"+hash+"</div>");

        //------ form
        FormLayout formLayout = new FormLayout(iconWrap,title,desc,layout);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(desc, 2);
        formLayout.setColspan(iconWrap, 2);

        add(formLayout);
        add(layout);

        //------ action

        nextBtn.addClickListener(e -> {

            if (fromView == WalletTxSentWrapper.FROM_VIEW_TRANSFER) {
                UI.getCurrent().navigate(TransferFormView.class);
            } else if (fromView == WalletTxSentWrapper.FROM_VIEW_DEPLOY_TOKEN) {
                UI.getCurrent().navigate(DeployTokenView.class);
            }else if (fromView == WalletTxSentWrapper.FROM_VIEW_CROSS_INSTANCE_TRANSFER) {
                UI.getCurrent().navigate(CrossInstanceTransferFormView.class);
            }else if (fromView == WalletTxSentWrapper.FROM_VIEW_CROSS_BROADCAST) {
                UI.getCurrent().navigate(BroadcastTxView.class);

            }else if (fromView == WalletTxSentWrapper.FROM_VIEW_NFT_DEPLOY) {
                UI.getCurrent().navigate(DeployNftView.class);
            }else if (fromView == WalletTxSentWrapper.FROM_VIEW_NFT_MINT) {
                UI.getCurrent().navigate(NftItemListView.class);
            }else if (fromView == WalletTxSentWrapper.FROM_VIEW_NFT_SEND) {
                UI.getCurrent().navigate(NftItemListView.class);
            }

        });

        copyBtn.addClickListener(e -> {
            CompUtil.setClipboardText(hash);
        });

        long finalCurrentInstanceId = currentInstanceId;
        viewBtn.addClickListener(e -> {
            CompUtil.showSuccess("The browser will open to view the transaction...");
            getUI().ifPresent(ui -> ui.getPage().open(ecologyUrlService.buildTxUrlByScan(hash, finalCurrentInstanceId)));
        });
    }

    private void clearForm() {
    }

}
