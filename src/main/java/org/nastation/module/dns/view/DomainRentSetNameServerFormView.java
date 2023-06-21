package org.nastation.module.dns.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = DomainRentSetNameServerFormView.Route_Value, layout = MainLayout.class)
@PageTitle(DomainRentSetNameServerFormView.Page_Title)
public class DomainRentSetNameServerFormView extends VerticalLayout {

    public static final String Route_Value = "DomainRentSetNameServerFormView";
    public static final String Page_Title = "Set NameServer of Domain";

    private TextField currentDomain;

    private TextField nameServer1;
    private TextField nameServer2;
    private TextField nameServer3;

    private PasswordField password;

    private Button submitBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    public DomainRentSetNameServerFormView(@Autowired WalletService walletService) {
        long currentInstanceId = walletService.getCurrentInstanceId();

        Object rentDomainName = UI.getCurrent().getSession().getAttribute("RentDomainName");

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        currentDomain = new TextField("Domain name for rent");
        currentDomain.setValue(String.valueOf(rentDomainName));
        currentDomain.setReadOnly(true);

        nameServer1 = new TextField("Name Server1");
        nameServer1.setValue("-");
        nameServer1.setReadOnly(true);

        nameServer2 = new TextField("Name Server2");
        nameServer2.setValue("-");
        nameServer2.setReadOnly(true);

        nameServer3 = new TextField("Name Server3");
        nameServer3.setValue("-");
        nameServer3.setReadOnly(true);

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        submitBtn = new Button("Back to list");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //------ form
        FormLayout formLayout = new FormLayout(
                title,
                currentDomain,
                nameServer1,
                nameServer2,
                nameServer3,
                //password,
                submitBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(currentDomain, 2);
        formLayout.setColspan(nameServer1, 2);
        formLayout.setColspan(nameServer2, 2);
        formLayout.setColspan(nameServer3, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(submitBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);

        submitBtn.addClickListener(e -> {
            UI.getCurrent().navigate(MyRentDomainListView.class);
            //CompUtil.showError("Domain name resolution failed");
        });

    }

}
