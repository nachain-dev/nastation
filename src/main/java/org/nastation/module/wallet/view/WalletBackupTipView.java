package org.nastation.module.wallet.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.CompUtil;
import org.nastation.components.Images;
import org.nastation.module.pub.view.MainLayout;

import java.util.Set;


@Route(value = WalletBackupTipView.Route_Value, layout = MainLayout.class)
@PageTitle("Wallet Backup Tip")
public class WalletBackupTipView extends VerticalLayout {

    public static final String Page_Title = "Wallet Backup Tip";
    public static final String Route_Value = "WalletBackupTipView";

    private Button nextBtn;
    private Button backBtn;

    public WalletBackupTipView() {
        addClassName("wallet-backup-tip-view");

        H2 title = new H2("RISK WARNING");
        title.getStyle().set("text-align", "center");

        Image icon = Images.warn();
        icon.getStyle().set("width", "96px");

        Div iconWrap = new Div();
        iconWrap.setWidthFull();
        iconWrap.getStyle().set("text-align", "center");
        iconWrap.add(icon);

        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Please check the warning options:");
        checkboxGroup.setItems(
                "1.Please do not take photos to backup",
                "2.Please do not use screenshots to backup",
                "3.It is strongly recommended that you save it correctly by manual copying",
                "4.It is strongly recommended that you store it safely in a password safe",
                "5.If you lose your mnemonic phrase, you will not be able to restore your wallet account"
        );
        //checkboxGroup.setValue(Collections.singleton("Option one"));
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        checkboxGroup.getStyle().set("color", "#FF1800");

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        backBtn = new Button("Back");

        //------ form
        FormLayout formLayout = new FormLayout(title, checkboxGroup, nextBtn, backBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        //formLayout.setColspan(iconWrap, 2);
        formLayout.setColspan(title, 2);
        formLayout.setColspan(checkboxGroup, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action

        nextBtn.setIconAfterText(true);
        nextBtn.addClickListener(e -> {

            Set<String> selectedItems = checkboxGroup.getSelectedItems();

            if (SystemService.me().isDev() || selectedItems.size() == 5) {
                UI.getCurrent().navigate(MnemonicBackupFormView.class);
                return;
            } else {
                CompUtil.showError("Please check all the options");
            }

        });

        backBtn.addClickListener(e -> UI.getCurrent().navigate(CreateWalletFormView.class));

    }


}
