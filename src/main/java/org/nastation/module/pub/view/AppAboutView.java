package org.nastation.module.pub.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.common.model.AppNewVersion;
import org.nastation.common.service.AppVersionService;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.CompUtil;
import org.nastation.components.Images;
import org.nastation.data.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = AppAboutView.Route_Value, layout = MainLayout.class)
@PageTitle(AppAboutView.Page_Title)
public class AppAboutView extends VerticalLayout {

    public static final String Route_Value = "AppAboutView";
    public static final String Page_Title = "About";

    public AppAboutView(
            @Autowired AppConfig appConfig,
            @Autowired AppVersionService appVersionService
            ) {
        addClassName("app-about-view");

        this.getStyle()
                .set("display","block")
                .set("padding-top","5em")
                .set("text-align","center");

        String projectName = appConfig.getProjectName();
        String projectVersion = appConfig.getProjectVersion();
        String websiteUrl = appConfig.getWebsiteUrl();
        String whitePaperUrl = appConfig.getWhitePaperUrl();
        String nascanUrl = appConfig.getNascanUrl();
        String telegramUrl = appConfig.getTelegramUrl();
        String twitterUrl = appConfig.getTwitterUrl();

        Image logo = Images.nac_144x144();
        add(logo);

        H3 title = new H3(projectName + " " + projectVersion);
        title.getStyle().set("margin-top", "0");
        add(title);

        // Website
        Accordion accordion = new Accordion();
        HorizontalLayout websiteDiv = new HorizontalLayout();

        Anchor anchor1 = new Anchor(websiteUrl, CompUtil.createButton("Official website", VaadinIcon.EXTERNAL_LINK));anchor1.setTarget("_blank");
        Anchor anchor2 = new Anchor(nascanUrl, CompUtil.createButton("Blockchain Explorer", VaadinIcon.EXTERNAL_LINK));anchor2.setTarget("_blank");
        Anchor anchor3 = new Anchor(whitePaperUrl, CompUtil.createButton("White Paper", VaadinIcon.EXTERNAL_LINK));anchor3.setTarget("_blank");
        Anchor anchor4 = new Anchor(telegramUrl, CompUtil.createButton("Telegram", VaadinIcon.EXTERNAL_LINK));anchor4.setTarget("_blank");
        Anchor anchor5 = new Anchor(twitterUrl, CompUtil.createButton("Twitter", VaadinIcon.EXTERNAL_LINK));anchor5.setTarget("_blank");
        websiteDiv.add(anchor1,anchor2,anchor3,anchor4,anchor5);
        websiteDiv.setFlexGrow(1,anchor1,anchor2,anchor3,anchor4,anchor5);

        accordion.add("Website", websiteDiv);

        // Check Software Update
        Paragraph paragraph = new Paragraph();
        paragraph.setText("Please make sure you have backed up your wallet data! This process may involve memory and software changes!");

        Button checkVersion = new Button("Check Version");
        checkVersion.setEnabled(false);
        checkVersion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        checkVersion.addClickListener(e -> {

            appVersionService.requestVersionCheck();

            AppNewVersion appNewVersion = SystemService.me().getAppNewVersion();
            if (appNewVersion != null) {
                CompUtil.showNewVersionDialog(appNewVersion);
            }else{
                CompUtil.showSuccess("No new version found");
            }
        });

        Checkbox consent = new Checkbox("I SURE");
        consent.getStyle().set("text-align", "left");
        consent.addValueChangeListener(e -> checkVersion.setEnabled(e.getValue()));

        HorizontalLayout bottomPanel = new HorizontalLayout(consent, checkVersion);
        bottomPanel.setWidthFull();
        bottomPanel.setFlexGrow(1, consent);
        VerticalLayout terms = new VerticalLayout(paragraph, bottomPanel);
        accordion.add("Version", terms);

        add(accordion);
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
