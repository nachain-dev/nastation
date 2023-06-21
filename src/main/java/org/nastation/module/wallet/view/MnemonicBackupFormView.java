package org.nastation.module.wallet.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.CompUtil;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Route(value = MnemonicBackupFormView.Route_Value, layout = MainLayout.class)
@PageTitle("Mnemonic Backup")
@Slf4j
public class MnemonicBackupFormView extends Div {

    public static final String Page_Title = "Mnemonic Backup";
    public static final String Route_Value = "MnemonicBackupFormView";

    private Button back;
    private Button next;
    private Button copy;
    private int index = 0;

    public MnemonicBackupFormView(
            @Autowired WalletService walletService
    ) {
        addClassName("mnemonic-backup-form-view");

        Wallet wallet = walletService.genAndSaveToCurrentWallet();

        String mnemonic = wallet.getMnemonic();
        String address = wallet.getAddress();
        String[] words = mnemonic.split(" ");

        //------ comp

        H2 title = new H2();
        title.setText("Mnemonic Backup Preview");
        title.getStyle().set("text-align", "center");

        next = new Button("Next");
        back = new Button("Back");
        //back = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        //next = new Button("Next", new Icon(VaadinIcon.ARROW_RIGHT));
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        copy = new Button("Copy to backup", new Icon(VaadinIcon.COPY));
        copy.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        copy.addClickListener(event -> {
            String walletMnemonic = wallet.getMnemonic();
            CompUtil.setClipboardText(walletMnemonic);
        });

        //------ form
        FormLayout formLayout = new FormLayout();

        formLayout.setMaxWidth("32em");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("2em", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("8em", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        formLayout.setColspan(formLayout.addFormItem(title, ""), 4);

        /*form 2*/

        formLayout.setColspan(formLayout.addFormItem(new Hr(), "Preview Mnemonic:"), 4);

        /*line*/
        createBtnLine(words, formLayout, "1");

        createBtnLine(words, formLayout, "2");

        createBtnLine(words, formLayout, "3");

        createBtnLine(words, formLayout, "4");

        formLayout.setColspan(formLayout.addFormItem(new Div(), ""), 4);

        /*line*/
        createBtnLine(words, formLayout, "5");

        createBtnLine(words, formLayout, "6");

        createBtnLine(words, formLayout, "7");

        createBtnLine(words, formLayout, "8");

        formLayout.setColspan(formLayout.addFormItem(new Div(), ""), 4);


        /*line*/
        createBtnLine(words, formLayout, "9");

        createBtnLine(words, formLayout, "10");

        createBtnLine(words, formLayout, "11");

        createBtnLine(words, formLayout, "12");

        formLayout.setColspan(formLayout.addFormItem(new Hr(), ""), 4);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setWidthFull();
        //btnLayout.setPadding(true);
        //btnLayout.getStyle().set("background-color", "#dddddd");
        btnLayout.addAndExpand(back, copy, next);

        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        //checkboxGroup.setLabel("Please check the warning options:");

        checkboxGroup.setItems(
                "1.Please save the mnemonic phrase safely",
                "2.If you miss the mnemonic or the mnemonic is out of order, the wallet cannot be restored"
        );
        //checkboxGroup.setValue(Collections.singleton(""));
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        checkboxGroup.getStyle().set("color", "#FF1800");

        formLayout.setColspan(formLayout.addFormItem(checkboxGroup, "WARNING:"), 4);
        formLayout.setColspan(formLayout.addFormItem(btnLayout, ""), 4);
        formLayout.setColspan(formLayout.addFormItem(new Div(), ""), 4);
        formLayout.setColspan(formLayout.addFormItem(new Div(), ""), 4);

        add(formLayout);

        //------ action
        next.addClickListener(e -> {

            Set<String> selectedItems = checkboxGroup.getSelectedItems();

            if (SystemService.me().isDev()) {
                UI.getCurrent().navigate(MnemonicBackupConfirmFormView.class);
                return;
            }

            if (selectedItems.size() != 2) {
                CompUtil.showError("Please check all the warning options");
            } else {
                UI.getCurrent().navigate(MnemonicBackupConfirmFormView.class);
            }

        });
        back.addClickListener(e -> UI.getCurrent().navigate(CreateWalletFormView.class));

    }

    private void createBtnLine(String[] words, FormLayout formLayout, String s) {
        Button _btn1 = new Button(words[index++]);
        _btn1.setWidthFull();
        formLayout.setColspan(formLayout.addFormItem(_btn1, s), 1);
    }

}