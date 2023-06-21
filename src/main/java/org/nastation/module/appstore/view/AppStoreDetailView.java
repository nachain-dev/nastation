package org.nastation.module.appstore.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
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
import org.nastation.common.util.CompUtil;
import org.nastation.components.FlexBoxLayout;
import org.nastation.components.ListItem;
import org.nastation.components.layout.size.*;
import org.nastation.components.style.BoxShadowBorders;
import org.nastation.components.style.IconSize;
import org.nastation.components.style.LumoStyles;
import org.nastation.components.style.TextColor;
import org.nastation.components.style.css.WhiteSpace;
import org.nastation.module.appstore.data.AppStoreDetailItem;
import org.nastation.module.appstore.data.AppStoreItem;
import org.nastation.module.appstore.repo.AppStoreItemRepository;
import org.nastation.module.pub.view.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = AppStoreDetailView.Route_Value + "/:id", layout = MainLayout.class)
@PageTitle(AppStoreDetailView.Page_Title)
public class AppStoreDetailView extends Div implements BeforeEnterObserver {

    public static final String Route_Value = "AppStoreDetailView";
    public static final String Page_Title = "AppStore Detail";
    public static final String Image_Url = "https://images.unsplash.com/photo-1519681393784-d120267933ba?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80";

    private ListItem appName;
    private ListItem installed;
    private ListItem author;

    @Autowired
    private AppStoreItemRepository appStoreItemRepository;
    private Optional<AppStoreItem> appStoreItem;

    public AppStoreDetailView() {
        addClassName("app-store-detail");
        add(createContent());
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(
                createTopButtons(),
                createHeadSection(),
                createIntroHeader(),
                createIntroContent(),
                createDetailsHeader(),
                createDetailsContent()
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
        back.addClickListener(e -> UI.getCurrent().navigate(AppStoreView.class));

        Button install = new Button("Install");
        install.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        install.getStyle().set("margin-left", "auto");

        hl.add(back, install);
        return hl;
    }

    private FlexBoxLayout createHeadSection() {
        //Image image = CompUtil.createImage("receive-qr-code.png");
        Image image = new Image();
        image.setSrc(Image_Url);

        image.addClassName(LumoStyles.Margin.Horizontal.L);
        //CompUtil.setBorderRadius(BorderRadius._50, image);

        image.setWidth("230px");
        image.setHeight("200px");

        appName = new ListItem(CompUtil.createTertiaryIcon(VaadinIcon.CUBE), "", "App Name");
        appName.getPrimary().addClassName(LumoStyles.Heading.H2);
        appName.setDividerVisible(true);
        appName.setId("appName");
        appName.setReverse(true);

        installed = new ListItem(CompUtil.createTertiaryIcon(VaadinIcon.DOWNLOAD), "", "App Installed");
        installed.setDividerVisible(true);
        installed.setId("installed");
        installed.setReverse(true);
        installed.setWhiteSpace(WhiteSpace.PRE_LINE);

        author = new ListItem(CompUtil.createTertiaryIcon(VaadinIcon.USER), "", "App Author");
        author.setReverse(true);

        FlexBoxLayout listItems = new FlexBoxLayout(appName, installed, author);
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

    private Component createIntroHeader() {
        Label title = CompUtil.createH3Label("Intro");

        Button copy = CompUtil.createSmallButton("Copy");
        copy.addClickListener(e -> CompUtil.showNotification("Not implemented yet."));
        copy.addClassName(LumoStyles.Margin.Left.AUTO);

        FlexBoxLayout header = new FlexBoxLayout(CompUtil.createIcon(IconSize.M, TextColor.TERTIARY, VaadinIcon.NEWSPAPER), title, copy);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.M, Top.L);
        header.setSpacing(Right.L);

        return header;
    }

    private Component createIntroContent() {
        Div box = new Div();
        box.addClassNames(BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L);

        box.add(new Span(""));

        return box;
    }

    private Component createDetailsHeader() {
        Label title = CompUtil.createH3Label("Details");

        FlexBoxLayout header = new FlexBoxLayout(CompUtil.createIcon(IconSize.M, TextColor.TERTIARY, VaadinIcon.TABLE), title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.M, Top.L);
        header.setSpacing(Right.L);

        return header;
    }

    private Component createDetailsContent() {
        List<AppStoreDetailItem> dataList = new ArrayList<AppStoreDetailItem>();

        dataList.add(new AppStoreDetailItem("Category", "Tool"));
        dataList.add(new AppStoreDetailItem("Size", "0.1 MB"));
        dataList.add(new AppStoreDetailItem("Hash", "0x000000000000000001"));
        dataList.add(new AppStoreDetailItem("Version", "1.0.1"));
        dataList.add(new AppStoreDetailItem("Author", "0x000000000000000001"));

        Grid<AppStoreDetailItem> grid = new Grid<AppStoreDetailItem>(AppStoreDetailItem.class);
        grid.setItems(dataList);
        grid.removeColumnByKey("id");
        grid.setColumns("name", "value");

        return grid;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        String id = event.getRouteParameters().get("id").get();
        appStoreItem = appStoreItemRepository.findById(Integer.valueOf(id));

        //initAppBar();
        if (appStoreItem.isPresent()) {
            AppStoreItem appStoreItem = this.appStoreItem.get();

            if (appStoreItem != null) {
                UI.getCurrent().getPage().setTitle(appStoreItem.getName());
                appName.setPrimaryText(appStoreItem.getName());
                installed.setPrimaryText("0");
                author.setPrimaryText(appStoreItem.getAuthor());
            }

        }

    }
}
