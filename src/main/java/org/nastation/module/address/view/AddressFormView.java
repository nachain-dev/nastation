package org.nastation.module.address.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;

@Route(value = AddressFormView.Route_Value, layout = MainLayout.class)
@PageTitle(AddressFormView.Route_Value)
public class AddressFormView extends VerticalLayout { //address-form-view

    public static final String Route_Value = "AddressForm";
    public static final String Page_Title = "Address Form";

    private TextField address;
    private TextField name;
    private TextField desc;

    private Button nextBtn;
    private Button addressList;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    public AddressFormView() {
        addClassName("address-form-view");

        //------ comp

        H2 title = new H2();
        title.setText("New Address");
        title.getStyle().set("text-align", "center");

        address = new TextField("Address");
        name = new TextField("Name");
        desc = new TextField("Description(Optional)");

        nextBtn = new Button("Submit");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addressList = new Button("Address List");

        //------ form
        FormLayout formLayout = new FormLayout(title, address, name, desc, nextBtn, addressList);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(address, 2);
        formLayout.setColspan(name, 2);
        formLayout.setColspan(desc, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(addressList, 2);

        add(formLayout);

        //------ action

        //binder.bindInstanceFields(this);
        clearForm();

        addressList.addClickListener(e -> UI.getCurrent().navigate(AddressListView.class));
        nextBtn.addClickListener(e -> {

            try {
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.");
            clearForm();
        });
    }

    private void clearForm() {
        binder.setBean(new Wallet());
    }


}
