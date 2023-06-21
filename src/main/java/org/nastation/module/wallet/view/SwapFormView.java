package org.nastation.module.wallet.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

@Route(value = SwapFormView.Route_Value, layout = MainLayout.class)
@PageTitle(SwapFormView.Page_Title)
public class SwapFormView extends VerticalLayout {

    public static final String Route_Value = "SwapForm";
    public static final String Page_Title = "Swap Assets";

    private TextField fromAddress;
    private TextField fromBalance;
    private CoinAddressField convertAmount;
    private CoinAddressField receiveAmount;

    private PasswordField password;

    private Button swapBtn;
    private Button clearBtn;
    private Button txListBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    @Autowired
    private WalletService walletService;

    private static class CoinAddressField extends CustomField<String> {
        private ComboBox<String> labelCombo = new ComboBox<>();
        private TextField address = new TextField();

        public CoinAddressField(String label) {
            setLabel(label);
            this.labelCombo.setWidth("120px");
            this.labelCombo.setPlaceholder("Coin");
            this.labelCombo.setPreventInvalidInput(true);
            this.labelCombo.setItems("NAC", "NOMC");
            this.labelCombo.setValue("NAC");
            this.labelCombo.addCustomValueSetListener(e -> this.labelCombo.setValue(e.getDetail()));
            //number.setPattern("\\d*");
            //number.setPreventInvalidInput(true);
            HorizontalLayout layout = new HorizontalLayout( address,this.labelCombo);
            layout.setFlexGrow(1.0, address);
            add(layout);
        }

        @Override
        protected String generateModelValue() {
            if (labelCombo.getValue() != null && address.getValue() != null) {
                String s = labelCombo.getValue() + " " + address.getValue();
                return s;
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String text) {
            String[] parts = text != null ? text.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                labelCombo.clear();
                address.setValue(parts[0]);
            } else if (parts.length == 2) {
                labelCombo.setValue(parts[0]);
                address.setValue(parts[1]);
            } else {
                labelCombo.clear();
                address.clear();
            }
        }
    }

    public SwapFormView() {
        addClassName("transfer-form-view");

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Current Account");
        fromAddress.setValue("0x000000000000001");
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Current Balance");
        fromBalance.setValue("100 NAC");
        fromBalance.setReadOnly(true);

        convertAmount = new CoinAddressField("Convert Amount");
        convertAmount.setValue("100");

        receiveAmount = new CoinAddressField("Receive Amount");
        receiveAmount.setValue("1000");

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        swapBtn = new Button("Submit");
        swapBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");
        txListBtn = new Button("Transaction List");

        //------ form
        FormLayout formLayout = new FormLayout(title, fromAddress, fromBalance, convertAmount, receiveAmount, password, swapBtn, clearBtn,txListBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);
        formLayout.setColspan(convertAmount, 2);
        formLayout.setColspan(receiveAmount, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(swapBtn, 2);
        formLayout.setColspan(clearBtn, 2);
        formLayout.setColspan(txListBtn, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);
        clearForm();

        clearBtn.addClickListener(e -> clearForm());
        swapBtn.addClickListener(e -> {

            try {
                Wallet update = walletService.getWalletRepository().save(binder.getBean());
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
