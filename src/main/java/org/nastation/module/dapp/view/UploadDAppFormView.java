package org.nastation.module.dapp.view;

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
import org.nastation.common.util.CompUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;


@Route(value = UploadDAppFormView.Route_Value, layout = MainLayout.class)
@PageTitle(UploadDAppFormView.Page_Title) @Slf4j
public class UploadDAppFormView extends VerticalLayout {

    public static final String Route_Value = "UploadDAppForm";
    public static final String Page_Title = "Upload DApp";

    private Upload icon;

    private TextField file_tf;
    private Upload file;
    private PasswordField password;

    private Button nextBtn;
    private Button listBtn;

    private TextField fromAddress;
    private TextField fromBalance;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private WalletService walletService;

    public UploadDAppFormView(@Autowired WalletService walletService,@Autowired WalletDataService walletDataService) {
        addClassName("upload-dapp-form-view");

        long currentInstanceId = walletService.getCurrentInstanceId();
        this.walletService = walletService;

        //------ comp

        H2 title = new H2();
        title.setText("Upload DApp");
        title.getStyle().set("text-align", "center");

        file_tf = new TextField("Package File");

        icon = createIconUpload();
        icon.getStyle().set("border", "none");

        file = createIconUpload();
        file.getStyle().set("border", "none");

        password = new PasswordField("Password");

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        listBtn = new Button("DApp List");

        fromAddress = new TextField("Account");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Account Balance");
        fromBalance.setValue(walletService.getDefaultWalletBalanceText_onlyNac(currentInstanceId));
        fromBalance.setReadOnly(true);

        //------ form
        FormLayout formLayout = new FormLayout(title, fromAddress, fromBalance,/*file_tf,*/file,password, nextBtn, listBtn);

        formLayout.setMaxWidth("800px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        //formLayout.addFormItem(firstName, "First Name");
        //formLayout.addFormItem(lastName, "Last Name");
        //formLayout.addFormItem(gender, "Status");
        //formLayout.addFormItem(phone, "Phone");
        //formLayout.addFormItem(email, "Email");
        //formLayout.addFormItem(company, "Company");
        //formLayout.addFormItem(new Upload(), "Image");

        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);

        formLayout.setColspan(title, 2);
        //formLayout.setColspan(name, 2);
        //formLayout.setColspan(icon, 2);
        //formLayout.setColspan(icon_tf, 2);
        formLayout.setColspan(file_tf, 2);
        formLayout.setColspan(file, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(listBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);

        listBtn.addClickListener(e -> UI.getCurrent().navigate(PublishDAppListView.class));

        nextBtn.addClickListener(e -> {
            //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());
            //UI.getCurrent().navigate(PublishDAppFormView.class);
            CompUtil.showError("File upload format error");
        });

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        file.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();
            String fileName = event.getFileName();

            if (fileName.endsWith(".npp")) {
                CompUtil.showError("The file content format is incorrect, please strictly refer to the NPP technical specification to generate the program package.");
            }else{
                CompUtil.showError("Please upload program package in .npp format");
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
        //
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
