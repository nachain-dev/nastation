package org.nastation.module.wallet.view;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.crypto.bip39.Language;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.wallet.WalletUtils;
import org.nachain.core.wallet.keystore.Keystore;
import org.nastation.common.event.WalletCreateEvent;
import org.nastation.common.util.CompUtil;
import org.nastation.data.service.AccountInfoService;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "ImportWalletForm", layout = MainLayout.class)
@PageTitle("Import Wallet")
@Slf4j
public class ImportWalletFormView extends VerticalLayout {

    public static final String Page_Title = "Import Wallet";
    public static final String Route_Value = "ImportWalletForm";

    private TextField nameField;
    private TextArea mnemonicField;
    private PasswordField passwordField;
    private PasswordField repeatPasswordField;
    private TextField pswTipField;
    private boolean enablePasswordValidation = false;

    private PasswordField saltField;
    private PasswordField repeatSaltField;
    private Checkbox addSaltCheckbox;
    private boolean enableSaltValidation = false;

    private Button importBtn;
    private Button backBtn;
    private String importBtnText;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private WalletService walletService;

    public ImportWalletFormView(
            @Autowired WalletDataService walletDataService,
            @Autowired AccountInfoService accountInfoService,
            @Autowired WalletRepository repository,
            @Autowired WalletService walletService
    ) {

        this.walletService = walletService;

        addClassName("import-wallet-form-view");
        long currentInstanceId = walletService.getCurrentInstanceId();

        //------ comp
        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        nameField = new TextField("Wallet Name");
        mnemonicField = new TextArea("Mnemonic");
        passwordField = new PasswordField("Password");
        repeatPasswordField = new PasswordField("Repeat Password");

        saltField = new PasswordField("Salt");
        saltField.setVisible(false);
        repeatSaltField = new PasswordField("Repeat Salt");
        repeatSaltField.setVisible(false);

        pswTipField = new TextField("Password Tip(Optional)");
        //importTipField = new TextField("New wallet is importing...");
        //importTipField.setReadOnly(true);
        //importTipField.setVisible(false);

        //importBtn = new Button("Import");
        //importBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        importBtn = new Button("Import");
        importBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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
        FormLayout formLayout = new FormLayout(title, nameField, addSaltCheckbox, saltField, repeatSaltField, mnemonicField, passwordField, repeatPasswordField, pswTipField,/*importTipField*/ importBtn, backBtn);

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

        formLayout.setColspan(mnemonicField, 2);
        formLayout.setColspan(passwordField, 2);
        formLayout.setColspan(repeatPasswordField, 2);
        formLayout.setColspan(pswTipField, 2);
        //formLayout.setColspan(importTipField, 2);
        formLayout.setColspan(importBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action

        binder.forField(nameField)
                .asRequired()
                .withValidator(
                        name -> StringUtils.isNotBlank(nameField.getValue()),
                        "Wallet name can not be empty")
                .withValidator(
                        name -> nameField.getValue().length() <= 50,
                        "Wallet name must be less than 50 characters")
                .withValidator(
                        name -> {
                            return repository.countByName(nameField.getValue()) == 0;
                        },
                        "Wallet name already exists")
                .bind(Wallet::getName, Wallet::setName);

        binder.forField(mnemonicField)
                .asRequired()
                .withValidator(
                        name -> {
                            return name.split(" ").length == 12;
                        },
                        "Incorrect mnemonic format")
                .bind(Wallet::getMnemonic, Wallet::setMnemonic);

        binder.forField(saltField)
                //.asRequired()
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


        backBtn.addClickListener(e -> UI.getCurrent().navigate(WalletListView.class));

        //importBtn.addClickListener(event -> {
        //    event.getSource().setText("Preparing import...");
        //});
        //importBtn.addDownloadStartsListener(event -> {
        //    LazyDownloadButton button = event.getSource();
        //    button.setText("START");
        //    button.setEnabled(true);
        //});

        importBtnText = importBtn.getText();

        importBtn.setDisableOnClick(true);
        importBtn.addClickListener(e -> {

            Button source = e.getSource();

            try {

                String mnemonic = mnemonicField.getValue();
                String password = passwordField.getValue();

                //check
                Keystore keystore = WalletUtils.generate(Language.ENGLISH, StringUtils.trim(mnemonic));
                if (keystore == null) {
                    CompUtil.showError("Import wallet error, keystore is null");
                    return;
                }

                String walletAddress = keystore.getWalletAddress();
                int count = repository.countByAddress(walletAddress);
                if (count > 0) {
                    CompUtil.showError("Wallet already exists: " + walletAddress);
                    return;
                }

                importBtnLoading(importBtn);

                //ConfirmDialog confirmDialog = CompUtil.showLoadingDialog("Preparing import...");

                //count the address balance
                long tokenId = CoreTokenEnum.NAC.id;
                double walletBalance = 0;//walletService.txTravelToCountBalance(currentInstanceId, walletAddress, CoreTokenEnum.NAC.id);

                //prepare
                Wallet wallet = new Wallet();
                binder.writeBean(wallet);
                wallet.setName(nameField.getValue());
                wallet.setPassword(password);
                wallet.setPswTip(pswTipField.getValue());
                wallet.setMnemonic(mnemonic);
                wallet.setAddress(walletAddress);
                wallet.setSalt(saltField.getValue());
                wallet.setCreateType(WalletService.CreateTypes.IMPORT_BY_MNEMONIC_VALUE);

                //import and save
                walletService.importAndSaveToCurrentWallet(wallet);
                walletService.persistCurrentWallet();

                //save account balance
                accountInfoService.saveAccountInfo(currentInstanceId, walletAddress, tokenId, walletBalance);

                //cache account address
                //walletDataService.setInstanceAccountAddress(currentInstanceId, walletAddress);

                //add
                ComponentUtil.fireEvent(CompUtil.getMainLayout(), new WalletCreateEvent(this, wallet));

                //UI.getCurrent().getSession().setAttribute("ImportWallet", wallet);
                CompUtil.showSuccess("New wallet has been imported");
                UI.getCurrent().navigate(WalletListView.class);

            } catch (Exception e1) {
                String msg = String.format("Wallet import failed: %s", e1.getMessage());
                log.error(msg, e1);
                CompUtil.showError(msg);
            } finally {
                importBtnDefault(source);
            }

        });

    }

    public void importBtnLoading(Button btn) {
        //CompUtil.showSuccess("New wallet is importing...");
        //btn.setVisible(false);
        //btn.setEnabled(false);
        //btn.setText("Loading");
    }

    public void importBtnDefault(Button btn) {
        btn.setEnabled(true);
        btn.setText(importBtnText);
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
