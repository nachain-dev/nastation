package org.nastation.module.dapp.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.common.util.CompUtil;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = PublishDAppFormView.Route_Value, layout = MainLayout.class)
@PageTitle(PublishDAppFormView.Page_Title)
public class PublishDAppFormView extends VerticalLayout {

    public static final String Route_Value = "PublishDAppForm";
    public static final String Page_Title = "Publish DApp";

    private TextField name;
    private TextField icon_tf;
    private TextField version_tf;
    private TextField size_tf;

    private Button submitBtn;
    private Button backBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    @Autowired
    private WalletService walletService;

    public PublishDAppFormView() {
        addClassName("publish-d-app-form-view");

        //------ comp

        H2 title = new H2();
        title.setText("Publish DApp");
        title.getStyle().set("text-align", "center");

        name = new TextField("Name");
        icon_tf = new TextField("Icon");
        version_tf = new TextField("Version");
        size_tf = new TextField("Size");

        submitBtn = new Button("Submit");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        backBtn = new Button("Back");

        //------ form
        FormLayout formLayout = new FormLayout(title, name,icon_tf, version_tf, size_tf, submitBtn, backBtn);

        formLayout.setMaxWidth("800px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(name, 2);
        formLayout.setColspan(icon_tf, 2);
        formLayout.setColspan(version_tf, 2);
        formLayout.setColspan(size_tf, 2);
        formLayout.setColspan(submitBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action

        //binder.bindInstanceFields(this);

        backBtn.addClickListener(e -> UI.getCurrent().navigate(UploadDAppFormView.class));

        submitBtn.addClickListener(e -> {

            //try {
            //    Wallet update = walletService.update(binder.getBean());
            //} catch (Exception exception) {
            //    exception.printStackTrace();
            //}

            //Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.");
            clearForm();

            UI.getCurrent().navigate(DeployDAppFormView.class);

        });


    }

    private Upload createIconUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Div output = new Div();

        upload.addSucceededListener(event -> {
            Component component = CompUtil.createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
            output.removeAll();
            CompUtil.showOutput(event.getFileName(), component, output);
        });

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            output.removeAll();
            CompUtil.showOutput(event.getErrorMessage(), component, output);
        });
        upload.getElement().addEventListener("file-remove", event -> {
            output.removeAll();
        });

        //add(upload, output);

        return upload;
    }

    private void clearForm() {
        //binder.setBean(new Wallet());
    }




}
