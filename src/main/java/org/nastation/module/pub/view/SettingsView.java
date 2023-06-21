package org.nastation.module.pub.view;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.HttpUtil;
import org.nastation.components.FlexBoxLayout;
import org.nastation.components.ListItem;
import org.nastation.components.layout.size.Bottom;
import org.nastation.components.layout.size.Horizontal;
import org.nastation.components.layout.size.Top;
import org.nastation.components.layout.size.Vertical;
import org.nastation.components.style.BoxShadowBorders;
import org.nastation.components.style.LumoStyles;
import org.nastation.data.service.WalletDataService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = SettingsView.Route_Value, layout = MainLayout.class)
@PageTitle(SettingsView.Page_Title)
public class SettingsView extends Div {

    public static final String Route_Value = "SettingsView";
    public static final String Page_Title = "Settings";

    private WalletDataService walletDataService;

    public SettingsView(
            @Autowired WalletDataService walletDataService
    ) {

        this.walletDataService = walletDataService;

        addClassName("settings-view");
        add(createContent());
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader_Common(),
                createList_Common(),
                createHeader_Info(),
                createList_Info()
        );
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_L);
        content.setMaxWidth("840px");
        return content;
    }


    private Component createHeader_Common() {
        Label title = CompUtil.createH3Label(Page_Title);

        Button viewAll = CompUtil.createSmallButton("More...");
        viewAll.addClickListener(
                e -> CompUtil.showNotification("Not implemented yet."));
        viewAll.addClassName(LumoStyles.Margin.Left.AUTO);

        FlexBoxLayout header = new FlexBoxLayout(title, viewAll);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.M, Horizontal.RESPONSIVE_L, Top.L);
        return header;
    }

    private Component createHeader_Info() {
        Label title = CompUtil.createH3Label("Info");

        Button viewAll = CompUtil.createSmallButton("More...");
        viewAll.addClickListener(
                e -> CompUtil.showNotification("Not implemented yet."));
        viewAll.addClassName(LumoStyles.Margin.Left.AUTO);

        FlexBoxLayout header = new FlexBoxLayout(title, viewAll);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.M, Horizontal.RESPONSIVE_L, Top.L);
        return header;
    }

    private Component createList_Common() {
        Div items = new Div();
        items.addClassNames(BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L);

        /*Language*/
        Select<String> langSelect = new Select<>();
        langSelect.setItems("English");
        langSelect.setValue("English");

        org.nastation.components.ListItem item = new org.nastation.components.ListItem(
                VaadinIcon.GLOBE.create(),
                "Language",
                "Select your familiar UI language",
                langSelect
        );
        item.setDividerVisible(true);
        items.add(item);

        /*Language*/
        Select<String> networkSelect = new Select<>();
        networkSelect.setItems("Mainnet");
        networkSelect.setValue("Mainnet");

        item = new org.nastation.components.ListItem(
                VaadinIcon.SIGNAL.create(),
                "Network",
                "Select node network",
                networkSelect
        );
        item.setDividerVisible(true);
        items.add(item);

        /*Theme*/
        RadioButtonGroup<String> themeRbg = new RadioButtonGroup<>();
        themeRbg.setLabel(null);
        themeRbg.setItems("Light", "Dark");
        themeRbg.setHelperText(null);

        ToggleButton toggle = new ToggleButton("Light");
        toggle.setDisabled(true);

        Div message = new Div();
        toggle.addValueChangeListener(evt -> message.setText(
                String.format("Toggle button value changed from '%s' to '%s'",
                        evt.getOldValue(), evt.getValue())));

        item = new org.nastation.components.ListItem(
                VaadinIcon.PALETE.create(),
                "Theme",
                "Default offer both bright and dark themes",
                toggle
        );
        item.setDividerVisible(true);
        items.add(item);

        /*Currency*/
        Select<String> currSelect = new Select<>();
        currSelect.setItems("USD");
        currSelect.setValue("USD");
        item = new ListItem(
                VaadinIcon.MONEY.create(),
                "Currency Unit",
                "The currency unit displayed on the UI",
                currSelect
        );
        item.setDividerVisible(false);
        items.add(item);

        TextField timeoutField = new TextField();
        timeoutField.setValue(String.valueOf(HttpUtil.TimeOutSeconds));
        timeoutField.addBlurListener(event -> {
            String value = event.getSource().getValue();

            int seconds = 0;
            try {
                seconds = Integer.valueOf(value);
            } catch (Exception exx) {
            }

            if (seconds >= 30 && seconds <= 120) {
                HttpUtil.TimeOutSeconds = seconds;
                CompUtil.showSuccess("Http request timeout change to " + seconds + " seconds");
            }else{
                CompUtil.showError("Http request timeout must be between 30 and 120 seconds");
            }

        });

        timeoutField.setSuffixComponent(new Span("Seconds"));
        item = new ListItem(
                VaadinIcon.TIMER.create(),
                "Timeout",
                "Http request timeout",
                timeoutField
        );
        item.setDividerVisible(false);
        items.add(item);

        /* Sync */
        TextField batchSizeField = new TextField();
        batchSizeField.setValue(String.valueOf(walletDataService.getSyncBlockSize()));
        batchSizeField.addBlurListener(event -> {
            String value = event.getSource().getValue();

            int size = 0;
            try {
                size = Integer.valueOf(value);
            } catch (Exception exx) {
            }

            if (size >= 10 && size <= 3000) {
                walletDataService.setSyncBlockSize(size);
                CompUtil.showSuccess("Block sync batch size change to " + size + " records");
            }else{
                CompUtil.showError("Block sync batch size must be between 10 and 3000 records");
            }

        });

        item = new ListItem(
                VaadinIcon.CLOUD_DOWNLOAD.create(),
                "Sync",
                "Block sync batch size",
                batchSizeField
        );
        item.setDividerVisible(false);
        items.add(item);

        return items;
    }
    private Component createList_Info() {
        Div items = new Div();
        items.addClassNames(BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L);

        /*Log Level*/
        Select<String> levelSelect = new Select<>();
        levelSelect.setItems("INFO");
        levelSelect.setValue("INFO");

        org.nastation.components.ListItem item = new org.nastation.components.ListItem(
                VaadinIcon.TAGS.create(),
                "Log Level",
                "Select the level of wallet log output",
                levelSelect
        );
        item.setDividerVisible(true);
        items.add(item);

        /*Log Path*/
        Span folder = new Span("logs");
        add(folder);

        item = new org.nastation.components.ListItem(
                VaadinIcon.FILE_TEXT_O.create(),
                "Log Path",
                "Default log save location of wallet",
                folder
        );
        item.setDividerVisible(false);
        items.add(item);

        return items;
    }

}
