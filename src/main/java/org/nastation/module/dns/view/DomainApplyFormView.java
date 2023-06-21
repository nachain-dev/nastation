package org.nastation.module.dns.view;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.MathUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Route(value = DomainApplyFormView.Route_Value, layout = MainLayout.class)
@PageTitle(DomainApplyFormView.Page_Title)
public class DomainApplyFormView extends VerticalLayout {

    public static final String Route_Value = "DomainApplyForm";
    public static final String Page_Title = "Apply Domain";

    private TextField fromAddress;
    private TextField fromBalance;
    private TextField totalPrice;
    private DomainNameField domainName;
    private NumberField blockYear;

    private PasswordField password;

    private Button sendBtn;
    private Button listBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    private static class DomainNameField extends CustomField<String> {
        private ComboBox<String> domainNameCombo = new ComboBox<>();
        private TextField subDomain = new TextField();

        public DomainNameField(String label) {
            setLabel(label);
            this.domainNameCombo.setWidth("200px");
            this.domainNameCombo.setPlaceholder(".bele.pro");
            this.domainNameCombo.setPreventInvalidInput(true);
            this.domainNameCombo.setItems(".klxy.info", ".sdfo.vip", ".yuko.cc", ".qxvx.org", ".bele.pro", ".ruaa.vip");
            this.domainNameCombo.addCustomValueSetListener(e -> this.domainNameCombo.setValue(e.getDetail()));
            //number.setPattern("\\d*");
            //number.setPreventInvalidInput(true);
            HorizontalLayout layout = new HorizontalLayout(subDomain,this.domainNameCombo);
            layout.setFlexGrow(1.0, subDomain);
            add(layout);
        }

        @Override
        protected String generateModelValue() {
            if (domainNameCombo.getValue() != null && subDomain.getValue() != null) {
                String s = domainNameCombo.getValue() + " " + subDomain.getValue();
                return s;
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String input) {
            String[] parts = input != null ? input.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                domainNameCombo.clear();
                subDomain.setValue(parts[0]);
            } else if (parts.length == 2) {
                domainNameCombo.setValue(parts[0]);
                subDomain.setValue(parts[1]);
            } else {
                domainNameCombo.clear();
                subDomain.clear();
            }
        }
    }

    public DomainApplyFormView(
            @Autowired WalletService walletService,
            @Autowired WalletDataService walletDataService
    ) {
        long currentInstanceId = walletService.getCurrentInstanceId();

        Map<String, Double> priceMap = walletDataService.getPriceMap();
        Double nacPrice = priceMap.get("NAC");

        //------ comp
        int step = 1;
        int unit = 100;

        H2 title = new H2();
        title.setText("Domain Apply");
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Account");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);
        fromAddress.setVisible(false);

        fromBalance = new TextField("Account Balance");
        fromBalance.setValue(walletService.getDefaultWalletBalanceText(currentInstanceId));
        fromBalance.setReadOnly(true);
        fromAddress.setVisible(false);

        ComboBox<String> dAppList = new ComboBox<>();
        dAppList.setLabel("Apply DApp");
        dAppList.setPlaceholder("Please select your DApp");

        domainName = new DomainNameField("Apply Sub Domain Name");
        domainName.setValue("");

        totalPrice = new TextField("Total price");
        totalPrice.setReadOnly(true);

        String nacValue = "-";
        if (nacPrice != 0) {
            nacValue = String.valueOf(MathUtil.round(step * unit / nacPrice, 4));

        }
        totalPrice.setValue(String.format("%s USDN(%s NAC)",step * unit,(nacValue)));

        blockYear = new NumberField("Apply Block Year");
        blockYear.setValue(1D);
        blockYear.setHasControls(true);
        blockYear.setMin(1D);
        blockYear.setMax(10D);
        blockYear.setStep(step);

        HorizontalLayout layout = new HorizontalLayout(blockYear, this.totalPrice);
        layout.setFlexGrow(1.0, totalPrice);

        blockYear.addValueChangeListener(new HasValue.ValueChangeListener() {
            @Override
            public void valueChanged(HasValue.ValueChangeEvent event) {
                double usdnTotal = blockYear.getValue() * unit;

                String nacValue = "-";
                if (nacPrice != 0) {
                    nacValue = String.valueOf((MathUtil.round(usdnTotal / nacPrice, 4)));
                }
                totalPrice.setValue(String.format("%s USDN(%s NAC)", usdnTotal, nacValue));
            }
        });

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        sendBtn = new Button("Submit");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        listBtn = new Button("Apply List");

        //------ form
        FormLayout formLayout = new FormLayout(title, dAppList, domainName,layout,
                password, sendBtn
                //, listBtn
        );

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(dAppList, 2);
        formLayout.setColspan(domainName, 2);
        formLayout.setColspan(blockYear, 2);
        formLayout.setColspan(totalPrice, 2);

        formLayout.setColspan(password, 2);
        formLayout.setColspan(sendBtn, 2);
        formLayout.setColspan(listBtn, 2);
        formLayout.setColspan(layout, 2);

        add(formLayout);

        //------ action

        //binder.bindInstanceFields(this);
        clearForm();

        //clearBtn.addClickListener(e -> clearForm());
        sendBtn.addClickListener(e -> {

            String value = dAppList.getValue();
            if (StringUtils.isBlank(value)) {
                CompUtil.showError("Please select your DApp");
                return;
            }

        });

        listBtn.addClickListener(e -> {
            UI.getCurrent().navigate(MyApplyDomainListView.class);
        });

    }

    private void clearForm() {
    }

}
