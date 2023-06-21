package org.nastation.module.wallet.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.nastation.components.Images;
import org.nastation.module.pub.view.HomeView;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = UnlockWalletFormView.Route_Value)
@PageTitle(UnlockWalletFormView.Page_Title)
public class UnlockWalletFormView extends VerticalLayout {

    public static final String Route_Value = "UnlockWalletForm";
    public static final String Page_Title = "Unlock NaStation";

    private PasswordField password;
    private Button nextBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    @Autowired
    private WalletService walletService;

    public UnlockWalletFormView() {
        addClassName("creat-wallet-form-view");

        //------ comp

        Image unlock = Images.unlock();
        unlock.getStyle().set("width", "100px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.add(unlock);
        add(layout);

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin", "0");

        password = new PasswordField("Password");
        nextBtn = new Button("UNLOCK");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //------ form
        FormLayout formLayout = new FormLayout(title, password, nextBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(nextBtn, 2);

        add(formLayout);

        //------ action
        //binder.bindInstanceFields(this);
        nextBtn.addClickListener(e -> {

            try {
                Wallet update = walletService.getWalletRepository().save(binder.getBean());
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            //Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.");
            clearForm();

            UI.getCurrent().navigate(HomeView.class);

        });


    }

    private void clearForm() {
        //binder.setBean(new Wallet());
    }


}
