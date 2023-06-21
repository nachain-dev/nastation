package org.nastation.module.wallet.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.event.WalletChangeDefaultEvent;
import org.nastation.common.util.CompUtil;
import org.nastation.components.QrImageSource;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.stefan.LazyDownloadButton;

import java.io.IOException;
import java.io.InputStream;

@Route(value = ReceiveFormView.Route_Value, layout = MainLayout.class)
@PageTitle(ReceiveFormView.Page_Title)
@Slf4j
public class ReceiveFormView extends VerticalLayout {

    public static final String Route_Value = "ReceiveForm";
    public static final String Page_Title = "Receive";

    private Button copy;
    private String address;
    private Image image;
    private QrImageSource qis;
    private Div qrCodeBox;
    private Paragraph addressPara;

    private WalletService walletService;
    private Span walletNameSpan;

    public ReceiveFormView(
            @Autowired WalletRepository walletRepository,
            @Autowired WalletService walletService
    ) {

        this.walletService = walletService;

        addClassName("receive-form-view");

        Wallet defaultWallet = walletService.getDefaultWallet();

        address = walletService.getDefaultWalletAddress();
        address = StringUtils.defaultIfBlank(address, "Please create a wallet first");

        //------ comp

        copy = new Button("Copy");
        copy.setIcon(VaadinIcon.COPY.create());
        copy.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        H2 title = new H2();
        title.setText("Receive Address");
        title.getStyle().set("text-align", "center");

        addressPara = new Paragraph();
        addressPara.setText(address);
        addressPara.addClassName("address");

        Hr line = new Hr();

        qrCodeBox = new Div();
        qrCodeBox.addClassName("qr-code-box");

        showQrImage(address);

        walletNameSpan = new Span(StringUtils.defaultIfEmpty(defaultWallet.getName(), "-"));
        walletNameSpan.getElement().getThemeList().add("badge success pill");

        add(title);
        add(line);
        add(qrCodeBox);
        add(walletNameSpan);
        add(addressPara);

        LazyDownloadButton download = new LazyDownloadButton("Download", VaadinIcon.DOWNLOAD.create(), this::getFileName, this::createFileInputStream);
        download.setDisableOnClick(true);
        download.addClickListener(this::onClick);
        download.addDownloadStartsListener(this::onDownloadStarted);

        HorizontalLayout layout = new HorizontalLayout();
        layout.getStyle().set("text-align", "center");
        layout.getStyle().set("display", "block");
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.add(copy);
        layout.add(download);
        add(layout);

        //------ action

        copy.addClickListener(e -> {
            CompUtil.setClipboardText(addressPara.getText());
        });

    }

    private void showQrImage(String address) {
        //Span span = new Span("NAC");
        qis = new QrImageSource();
        qis.setText(address);
        qis.setHeight(250);
        qis.setWidth(250);

        image = new Image();
        try {
            image = CompUtil.createQrImage(qis);
        } catch (IOException e) {
            log.error("Create qrcode image error ", e);
        }
        qrCodeBox.add(image);
    }

    private void onDownloadStarted(LazyDownloadButton.DownloadStartsEvent downloadStartsEvent) {
        LazyDownloadButton button = downloadStartsEvent.getSource();
        button.setIcon(VaadinIcon.DOWNLOAD.create());
        button.setText("Download");
        button.setEnabled(true);
    }

    private void onClick(ClickEvent<Button> buttonClickEvent) {
        Button button = buttonClickEvent.getSource();
        button.setText("Preparing download...");
    }

    private String getFileName() {
        return "na-qrcode-address-" + this.address + ".png";
    }

    private InputStream createFileInputStream() {
        try {
            return qis.getStream();
        } catch (Exception e) {
            log.error("createFileInputStream error", e);
        }
        return null;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI mainLayout = CompUtil.getMainLayout();
        if (mainLayout != null) {
            walletChangeDefaultEventReg = ComponentUtil.addListener(
                    mainLayout,
                    WalletChangeDefaultEvent.class,
                    event -> {
                        walletChangeDefaultEventHandler(event);
                    }
            );
        }

    }

    private void walletChangeDefaultEventHandler(WalletChangeDefaultEvent event) {
        Wallet wallet = event.getWallet();

        address = wallet.getAddress();
        address = StringUtils.defaultIfBlank(address, "Please create a wallet first");

        qrCodeBox.remove(image);

        showQrImage(address);

        walletNameSpan.setText(StringUtils.defaultIfEmpty(wallet.getName(), "-"));

        addressPara.setText(address);

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (walletChangeDefaultEventReg != null)
            walletChangeDefaultEventReg.remove();
    }

    private Registration walletChangeDefaultEventReg;


}