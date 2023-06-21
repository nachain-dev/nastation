package org.nastation.module.dfs.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route(value = TempSpaceFileUploadFormView.Route_Value, layout = MainLayout.class)
@PageTitle(TempSpaceFileUploadFormView.Page_Title)
public class TempSpaceFileUploadFormView extends VerticalLayout {

    public static final String Route_Value = "TempSpaceFileUploadFormView";
    public static final String Page_Title = "Temporary File Upload";

    private Upload upload;

    private Upload fileUpload;
    private PasswordField password;

    private Button submitBtn;
    private Button listBtn;

    private TextField fromAddress;
    private TextField fromBalance;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private WalletService walletService;
    private NodeClusterHttpService nodeClusterHttpService;

    public TempSpaceFileUploadFormView(@Autowired WalletService walletService,@Autowired WalletDataService walletDataService,@Autowired NodeClusterHttpService nodeClusterHttpService) {
        this.walletService = walletService;
        long currentInstanceId = walletService.getCurrentInstanceId();

        this.nodeClusterHttpService = nodeClusterHttpService;

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        upload = createIconUpload();
        upload.getStyle().set("border", "none");

        fileUpload = createIconUpload();
        fileUpload.getStyle().set("border", "none");

        password = new PasswordField("Password");

        submitBtn = new Button("Upload");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        listBtn = new Button("File List");

        fromAddress = new TextField("Account");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Account Balance");
        fromBalance.setValue(walletService.getDefaultWalletBalanceText_onlyNac(currentInstanceId));
        fromBalance.setReadOnly(true);


        //------ form
        FormLayout formLayout = new FormLayout(title, fromAddress, fromBalance,  fileUpload,password, submitBtn /*listBtn*/);

        formLayout.setMaxWidth("800px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);
        formLayout.setColspan(title, 2);
        formLayout.setColspan(fileUpload, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(submitBtn, 2);
        formLayout.setColspan(listBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);

        listBtn.addClickListener(e -> UI.getCurrent().navigate(PinFileItemListView.class));

        submitBtn.addClickListener(e -> {
            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();
                String ownerAddressAddressVal = defaultWalletAddress;

                String psw = StringUtils.trim(password.getValue());

                if (StringUtils.isBlank(psw)) {
                    CompUtil.showError("Password can not be empty");
                    submitBtn.setEnabled(true);
                    return;
                }

                if (walletService.getWalletKey(defaultWallet,psw) == null ) {
                    CompUtil.showError("The password is incorrect");
                    submitBtn.setEnabled(true);
                    return;
                }

                CompUtil.showError("Please purchase storage space first");

            } catch (Exception exception) {
                String msg = "Failed upload temp file: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                submitBtn.setEnabled(true);
            }

        });
    }

    private Upload createIconUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Div output = new Div();

        //upload.addSucceededListener(event -> {
        //    Component component = CompUtil.createComponent(event.getMIMEType(),
        //            event.getFileName(), buffer.getInputStream());
        //    output.removeAll();
        //    CompUtil.showOutput(event.getFileName(), component, output);
        //});
        //
        //upload.addFileRejectedListener(event -> {
        //    Paragraph component = new Paragraph();
        //    output.removeAll();
        //    CompUtil.showOutput(event.getErrorMessage(), component, output);
        //});
        //upload.getElement().addEventListener("file-remove", event -> {
        //    output.removeAll();
        //});

        //add(upload, output);
        return upload;
    }

    private void clearForm() {
        //binder.setBean(new Wallet());
    }

}
