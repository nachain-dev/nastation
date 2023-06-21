package org.nastation.module.dns.view;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.common.util.CompUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route(value = DomainRentFormView.Route_Value, layout = MainLayout.class)
@PageTitle(DomainRentFormView.Page_Title)
public class DomainRentFormView extends VerticalLayout {

    public static final String Route_Value = "DomainRentForm";
    public static final String Page_Title = "Rent Domain";

    private TextField fromAddress;
    private TextField fromBalance;
    private TextField rentDomain;
    private TextField totalPrice;
    private NumberField blockYear;
    private PasswordField password;

    private Button nextBtn;
    private Button clearBtn;
    private Button listBtn;

    private Binder<Wallet> binder = new Binder(Wallet.class);

    public DomainRentFormView(@Autowired WalletService walletService,@Autowired WalletDataService walletDataService) {
        long currentInstanceId = walletService.getCurrentInstanceId();

        //------ comp
        int step = 1;
        int unit = 100;

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Account");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Account Balance");
        fromBalance.setValue(walletService.getDefaultWalletBalanceText(currentInstanceId));
        fromBalance.setReadOnly(true);

        rentDomain = new TextField("Rent Domain");
        rentDomain.setPlaceholder("");
        rentDomain.setClearButtonVisible(true);

        totalPrice = new TextField("Total price");
        totalPrice.setReadOnly(true);
        totalPrice.setValue(step * unit + " NAC");

        blockYear = new NumberField("Block Year");
        blockYear.setValue(1D);
        blockYear.setHasControls(true);
        blockYear.setMin(1D);
        blockYear.setMax(100D);
        blockYear.setStep(step);
        blockYear.setWidth("180px");

        HorizontalLayout layout = new HorizontalLayout(blockYear, this.totalPrice);
        layout.setFlexGrow(1.0, blockYear);

        blockYear.addValueChangeListener(new HasValue.ValueChangeListener() {
            @Override
            public void valueChanged(HasValue.ValueChangeEvent event) {
                totalPrice.setValue(blockYear.getValue() * unit + " NAC");
            }
        });

        password = new PasswordField("Password");
        password.setClearButtonVisible(true);

        nextBtn = new Button("Next");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");
        listBtn = new Button("Rent List");

        //------ form
        FormLayout formLayout = new FormLayout(
                title,
                fromAddress,
                fromBalance,
                rentDomain,
                layout,
                nextBtn);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);

        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);
        formLayout.setColspan(rentDomain, 2);
        formLayout.setColspan(blockYear, 2);
        formLayout.setColspan(totalPrice, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(nextBtn, 2);
        formLayout.setColspan(listBtn, 2);
        formLayout.setColspan(layout, 2);

        add(formLayout);

        //------ action

        binder.bindInstanceFields(this);
        clearForm();

        clearBtn.addClickListener(e -> clearForm());
        nextBtn.addClickListener(e -> {
            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();
                String ownerAddressAddressVal = defaultWalletAddress;

                long instance = CoreInstanceEnum.NAC.id;

                String rentDomainName = StringUtils.trim(rentDomain.getValue());

                if (StringUtils.isBlank(rentDomainName)) {
                    CompUtil.showError("Please enter domain name for rent");
                    return;
                }

                /*
                if (!rentDomainName.contains(".")) {
                    CompUtil.showError("Please enter valid domain name for rent");
                    return;
                }
                */

                if (true) {
                    CompUtil.showEnableByBlockHeightDialog(walletDataService.getNacLastBlockHeightByRequest());
                    nextBtn.setEnabled(true);
                    return;
                }

                /*
                HttpResult httpResult = this.nodeClusterHttpService.vote_submitVote(
                        instance,
                        ownerAddressVal,
                        ownerAddressAddressVal,
                        beneficiaryAddressVal,
                        nominateAddressVal,
                        voteAmountVal
                );

                if (!httpResult.getFlag()) {
                    CompUtil.showError("Failed to submit vote : " + httpResult.getMessage());
                } else {
                    CompUtil.showSuccess("Voting submitted successfully");
                    clearForm();
                }
                */

            } catch (Exception exception) {
                String msg = "Failed to submit vote: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                nextBtn.setEnabled(true);
            }

            //UI.getCurrent().getSession().setAttribute("RentDomainName", rentDomainName);
            //UI.getCurrent().navigate(DomainRentSetNameServerFormView.class);


        });
        listBtn.addClickListener(e -> {
            UI.getCurrent().navigate(MyRentDomainListView.class);
        });

    }

    private void clearForm() {

    }

}
