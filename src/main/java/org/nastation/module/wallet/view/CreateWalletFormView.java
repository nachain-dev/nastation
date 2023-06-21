package org.nastation.module.wallet.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.CompUtil;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Route(value = CreateWalletFormView.Route_Value, layout = MainLayout.class)
@PageTitle(CreateWalletFormView.Page_Title)
@Slf4j
public class CreateWalletFormView extends VerticalLayout {

    public static final String Page_Title = "Create Wallet";
    public static final String Route_Value = "CreateWalletForm";

    private boolean enablePasswordValidation = false;
    private boolean enableSaltValidation = false;

    private TextField nameField;

    private PasswordField passwordField;
    private PasswordField repeatPasswordField;

    private PasswordField saltField;
    private PasswordField repeatSaltField;

    private TextField pswTipField;

    private Button nextBtn;
    private Button backBtn;
    private Checkbox addSaltCheckbox;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private WalletService walletService;

    public CreateWalletFormView(
            @Autowired WalletRepository repository,
            @Autowired WalletService walletService
    ) {

        this.walletService = walletService;
        //------ comp

        H2 title = new H2();
        title.setText("Create a new wallet");
        title.getStyle().set("text-align", "center");

        nameField = new TextField("Wallet Name");
        passwordField = new PasswordField("Password");
        repeatPasswordField = new PasswordField("Repeat Password");

        saltField = new PasswordField("Salt");
        saltField.setVisible(false);
        repeatSaltField = new PasswordField("Repeat Salt");
        repeatSaltField.setVisible(false);

        pswTipField = new TextField("Password Tip(Optional)");

        if (SystemService.me().isDev()) {

            String name = "W" + new Date().getTime();

            nameField.setValue(name);
            passwordField.setValue(name);
            repeatPasswordField.setValue(name);

            saltField.setValue("");
            repeatSaltField.setValue("");

            pswTipField.setValue(name);
        }

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextBtn.addClickShortcut(Key.ENTER);
        getElement().addEventListener("keydown", event -> {
            //System.out.println("event = " + event);
        }).setFilter("event.key == 'ENTER'");

        backBtn = new Button("Back");

        addSaltCheckbox = new Checkbox();
        addSaltCheckbox.setLabel("Add key salt?");
        addSaltCheckbox.setValue(false);

        addSaltCheckbox.addClickListener(event -> {
            Boolean isSelected = addSaltCheckbox.getValue();
            saltField.setVisible(isSelected);
            repeatSaltField.setVisible(isSelected);
        });

        //------ form
        FormLayout formLayout = new FormLayout(title, nameField, addSaltCheckbox, saltField, repeatSaltField, passwordField, repeatPasswordField, pswTipField, nextBtn, backBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(nameField, 2);

        formLayout.setColspan(addSaltCheckbox, 2);
        formLayout.setColspan(saltField, 2);
        formLayout.setColspan(repeatSaltField, 2);

        formLayout.setColspan(passwordField, 2);
        formLayout.setColspan(repeatPasswordField, 2);

        formLayout.setColspan(pswTipField, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action

        /*
        Wallet w = new Wallet();
        w.setName("abc");
        binder.setBean(w);
        binder.bindInstanceFields(this);
        binder.bind(name, Wallet::getName,Wallet::setName);
        */

        binder.forField(nameField)
                .asRequired()
                .withValidator(
                        name -> StringUtils.isNotBlank(nameField.getValue()),
                        "Wallet name can not be empty")
                .withValidator(
                        name -> walletService.isWalletNameValid(nameField.getValue()),
                        "Wallet name must be 1-50 digits,only consist of numbers and letters")
                .withValidator(
                        name -> {
                            return repository.countByName(nameField.getValue()) == 0;
                        },
                        "Wallet name already exists")
                .bind(Wallet::getName, Wallet::setName);

        binder.forField(saltField)
                .withValidator(this::saltValidator)
                .bind(Wallet::getSalt, Wallet::setSalt);

        binder.forField(passwordField)
                .asRequired()
                .withValidator(this::passwordValidator)
                .bind(Wallet::getPassword, Wallet::setPassword);

        repeatPasswordField.addValueChangeListener(e -> {
            enablePasswordValidation = true;
            binder.validate();
        });

        repeatSaltField.addValueChangeListener(e -> {
            enableSaltValidation = true;
            binder.validate();
        });

        /*
        String repeatPasswordValue = StringUtils.trim(repeatPasswordField.getValue());
        binder.forField(repeatPasswordField)
                .asRequired()
                .withValidator(
                        name -> StringUtils.equals(repeatPasswordValue,passwordValue),
                        "The two passwords are inconsistent")
                ;
        */

        nextBtn.addClickListener(e -> {

            try {

                Wallet wallet = new Wallet();
                binder.writeBean(wallet);
                wallet.setName(nameField.getValue());
                wallet.setPassword(passwordField.getValue());
                wallet.setSalt(saltField.getValue());
                wallet.setPswTip(pswTipField.getValue());
                wallet.setCreateType(WalletService.CreateTypes.DESKTOP_VALUE);

                walletService.setCurrentWallet(wallet);
                UI.getCurrent().navigate(WalletBackupTipView.class);

            } catch (Exception e1) {
                String msg = String.format("Wallet data verification failed");
                log.error(msg, e1);
                CompUtil.showError(msg);
            }

        });

        backBtn.addClickListener(e -> UI.getCurrent().navigate(WalletListView.class));

    }

    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {

        if (walletService.isPasswordInvalid(pass1)) {
            return ValidationResult.error("Password must be 8-20 digits,at least one lowercase letter, one uppercase letter and one number");
        }

        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = repeatPasswordField.getValue();

        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }

    private ValidationResult saltValidator(String salt1, ValueContext ctx) {

        if (addSaltCheckbox.getValue() && StringUtils.isBlank(salt1)) {
            return ValidationResult.error("Salt must be not empty");
        }

        if (!enableSaltValidation) {
            enableSaltValidation = true;
            return ValidationResult.ok();
        }

        String salt = repeatSaltField.getValue();

        if (salt1 != null && salt1.equals(salt)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Salt do not match");
    }


}
