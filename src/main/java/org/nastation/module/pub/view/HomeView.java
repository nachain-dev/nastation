package org.nastation.module.pub.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.nastation.common.model.AppNewVersion;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.CompUtil;
import org.nastation.components.Images;
import org.nastation.data.config.AppConfig;
import org.nastation.module.wallet.view.CreateWalletFormView;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Home")
@Route(value = "HomeView", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    private Button createWalletBtn;

    public HomeView(
            @Autowired AppConfig appConfig
    ) {
        addClassName("home-view");

        /*
        LazyDownloadButton start = new LazyDownloadButton("START");
        start.setDisableOnClick(true);
        start.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        start.addClickListener(event -> {
            event.getSource().setText("Preparing start...");
            UI.getCurrent().navigate(CreateWalletFormView.class);
        });

        start.addDownloadStartsListener(event -> {
            LazyDownloadButton button = event.getSource();
            button.setText("START");
            button.setEnabled(true);
        });

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);

        */

        /*
        Carousel carousel = Carousel.create().withAutoplay().withDuration(3, TimeUnit.SECONDS);
        carousel.setWidth("400px");
        carousel.setHeight("180px");

        Image image1 = CompUtil.createImage("slide1.png");
        Image image2 = CompUtil.createImage("slide2.png");
        Image image3 = CompUtil.createImage("slide3.png");

        image1.setWidth("300px");
        image1.setHeight("150px");
        image2.setWidth("300px");
        image2.setHeight("150px");
        image3.setWidth("300px");
        image3.setHeight("150px");

        carousel.add(image1);
        carousel.add(image2);
        carousel.add(image3);

        add(carousel);
        */

        createWalletBtn = new Button("Create Wallet");
        createWalletBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createWalletBtn.addClickListener(e -> {
            UI.getCurrent().navigate(CreateWalletFormView.class);
        });

        Image logo = Images.nac_144x144();
        add(logo);

        H1 title = new H1(appConfig.getProjectName());
        title.getStyle().set("margin-top", "0");
        add(title);

        //add(new Html("<p>NaStation is an integrated workstation. <br/>Nirvana chain wallet, N++ (DEVELOPMENT ENVIRONMENT), NA DNS (DOMAIN NAME MANAGEMENT)<br/> NA DFS (DISTRIBUTED FILE SYSTEM), NA DevStore (DAPP DEVELOPMENT PLATFORM), NA AppStore (DAPP DEPLOY PLATFORM)</p>"));
        add(new Html("<p>NaStation is an integrated workstation.<br/>NA Wallet, N++, NA DNS, NA DFS, NA DevStore, NA AppStore</p>"));

        Button websiteUrlBtn = new Button("Website");
        websiteUrlBtn.addClickListener(e -> {
            //LaunchUtil.launchBrowser(appConfig.getWebsiteUrl(), "Visit website url");
            getUI().ifPresent(ui -> ui.getPage().open(appConfig.getWebsiteUrl()));

        });
        Button whitePaperUrlBtn = new Button("White Paper");
        whitePaperUrlBtn.addClickListener(e -> {
            //LaunchUtil.launchBrowser(appConfig.getWhitePaperUrl(), "Visit white paper url");
            getUI().ifPresent(ui -> ui.getPage().open(appConfig.getWhitePaperUrl()));

        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        btnLayout.setSizeUndefined();
        btnLayout.setSpacing(true);
        btnLayout.setPadding(true);
        btnLayout.add(websiteUrlBtn, whitePaperUrlBtn);

        add(btnLayout);
        add(createWalletBtn);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //UI ui = attachEvent.getUI();

        AppNewVersion appNewVersion = SystemService.me().getAppNewVersion();
        if (appNewVersion != null) {
            CompUtil.showNewVersionDialog(appNewVersion);
        }
    }


}
