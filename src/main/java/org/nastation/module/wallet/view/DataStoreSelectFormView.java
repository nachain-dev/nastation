package org.nastation.module.wallet.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.module.pub.view.HomeView;
import org.nastation.module.pub.view.MainLayout;

@Route(value = DataStoreSelectFormView.Route_Value, layout = MainLayout.class)
@PageTitle("Wallet Data Store Location")
public class DataStoreSelectFormView extends VerticalLayout {

    public static final String Route_Value = "DataStoreSelectForm";
    public static final String Page_Title = "DataStoreSelectForm";

    private TextField blocks = new TextField("Estimated number of blocks");
    private TextField time = new TextField("Estimated synchronization time");
    private TextField filePath = new TextField("File path");


    public DataStoreSelectFormView() {
        addClassName("data-store-select-form-view");

        //------ comp

        H2 title = new H2();
        title.setText("Data Store Location");
        title.getStyle().set("text-align", "center");

        H4 desc = new H4("Please select a file path to save the wallet synchronization data");
        desc.getStyle().set("text-align", "center");

        Button nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button backBtn = new Button("Back");

        //------ form
        FormLayout formLayout = new FormLayout(title, desc, blocks, time, filePath, nextBtn, backBtn);

        // Restrict maximum width and center on page
        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");

        // Allow the form layout to be responsive. On device widths 0-490px we have one
        // column, then we have two. Field labels are always on top of the fields.
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        // These components take full width regardless if we use one column or two (it
        // just looks better that way)
        formLayout.setColspan(title, 2);
        formLayout.setColspan(desc, 2);

        formLayout.setColspan(blocks, 2);
        formLayout.setColspan(time, 2);
        formLayout.setColspan(filePath, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(backBtn, 2);

        add(formLayout);

        //------ action

        nextBtn.addClickListener(e -> clearForm());
        nextBtn.addClickListener(e -> {

            //redirect
            UI.getCurrent().navigate(CreateWalletFormView.class);


            Notification.show("ok");
            clearForm();
        });

        backBtn.addClickListener(e -> {
            UI.getCurrent().navigate(HomeView.class);
        });


        updateForm();
    }

    private void updateForm() {

        blocks.setPlaceholder("fetch estimated number of blocks...");
        blocks.setReadOnly(true);
        blocks.setValue("100000 blocks");

        blocks.setPlaceholder("fetch estimated synchronization time...");
        time.setReadOnly(true);
        time.setValue("100 Hours");
    }

    private void clearForm() {
    }

    //todo: how to end of right
    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");

        Button nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button backBtn = new Button("Back");

        buttonLayout.add(nextBtn);
        buttonLayout.add(backBtn);
        return buttonLayout;
    }


}
