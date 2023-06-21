package org.nastation.module.wallet.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.nachain.core.util.Hex;
import org.nachain.core.wallet.keystore.Keystore;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.KeystoreWrapper;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = BackupWalletFormView.Route_Value, layout = MainLayout.class)
@PageTitle("Backup Wallet")
@Slf4j
public class BackupWalletFormView extends VerticalLayout {

    public static final String Page_Title = "Backup Wallet";
    public static final String Route_Value = "BackupWalletForm";

    private TextField walletName;
    private TextField address;
    private TextArea mnemonic;
    private TextArea keystore;
    private TextArea privatekey;
    private TextArea seedkey;

    private Button nextBtn;
    private Button backBtn;

    private WalletService walletService;
    private WalletRepository walletRepository;

    public BackupWalletFormView(
            @Autowired WalletRepository walletRepository,
            @Autowired WalletService walletService
    ) {
        addClassName("backup-wallet-form-view");

        this.walletService = walletService;
        this.walletRepository = walletRepository;

        //UI.getCurrent().getPage().addJavaScript("./scripts/copytoclipboard.js");

        KeystoreWrapper keystoreWrapper = this.walletService.getCurrentKeystore();
        Keystore k = keystoreWrapper.getKeystore();
        Wallet wallet = keystoreWrapper.getWallet();

        String privateKeyText = Hex.encode0x(k.getPrivateKey());
        String seedText = Hex.encode0x(k.getSeed());
        String walletNameText = wallet.getName();
        String mnemonicText = k.getMnemonic();
        String keystoreText = k.toString();
        String walletAddress = k.getWalletAddress();

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        walletName = new TextField("Wallet Name");
        address = new TextField("Wallet Address");
        mnemonic = new TextArea("Mnemonic");
        this.keystore = new TextArea("Keystore");
        privatekey = new TextArea("Private key");
        seedkey = new TextArea("Seed");

        Button copy1 = new Button("COPY");
        copy1.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        Button copy2 = new Button("COPY");
        copy2.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        Button copy3 = new Button("COPY");
        copy3.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        Button copy4 = new Button("COPY");
        copy4.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        copy1.addClickListener(copyMnemonicListener());
        copy2.addClickListener(copyKeystoreListener());
        copy3.addClickListener(copyPrivateKeyListener());
        copy4.addClickListener(copySeedListener());

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextBtn.setVisible(false);

        backBtn = new Button("Back");
        backBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        walletName.setReadOnly(true);
        walletName.setValue(walletNameText);
        address.setValue(walletAddress);

        //mnemonic.setSuffixComponent(copy1);
        //keystore.setSuffixComponent(copy2);
        //privatekey.setSuffixComponent(copy3);
        //seedkey.setSuffixComponent(copy4);

        mnemonic.setValue(mnemonicText);
        keystore.setValue(keystoreText);
        privatekey.setValue(privateKeyText);
        seedkey.setValue(seedText);

        //final Link link = new Link("Google", new ExternalResource("http://www.google.com"));
        //link.setTargetName("_blank");

        //------ form
        FormLayout formLayout = new FormLayout(title, walletName, mnemonic, this.keystore, privatekey, seedkey, nextBtn, backBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(walletName, 2);
        formLayout.setColspan(mnemonic, 2);
        formLayout.setColspan(this.keystore, 2);
        formLayout.setColspan(privatekey, 2);
        formLayout.setColspan(seedkey, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action
        backBtn.addClickListener(e -> {
            this.walletService.clearCurrentKeystore();
            UI.getCurrent().getPage().getHistory().back();
        });
    }

    private ComponentEventListener<ClickEvent<Button>> copySeedListener() {
        //CompUtil.setClipboardText(this.seedkey.getValue());
        return null;
    }

    private ComponentEventListener<ClickEvent<Button>> copyPrivateKeyListener() {
        //CompUtil.setClipboardText(this.privatekey.getValue());
        return null;
    }

    private ComponentEventListener<ClickEvent<Button>> copyKeystoreListener() {
        //CompUtil.setClipboardText(this.keystore.getValue());
        return null;
    }

    private ComponentEventListener<ClickEvent<Button>> copyMnemonicListener() {
        //CompUtil.setClipboardText(this.mnemonic.getValue());
        return null;
    }
}
