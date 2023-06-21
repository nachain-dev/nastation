package org.nastation.module.dfs.view;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route(value = BuySpaceFormView.Route_Value, layout = MainLayout.class)
@PageTitle(BuySpaceFormView.Page_Title)
public class BuySpaceFormView extends VerticalLayout {

    public static final String Route_Value = "BuySpaceFormView";
    public static final String Page_Title = "Buy Space Form";

    private TextField fromAddress;
    private TextField fromBalance;
    private TextField spaceSize;
    private TextField spacePrice;

    private NumberField buySize;
    private PasswordField password;

    private Button submitBtn;
    private Button listBtn;

    private WalletService walletService;
    private NodeClusterHttpService nodeClusterHttpService;

    public BuySpaceFormView(@Autowired WalletService walletService,@Autowired WalletDataService walletDataService,@Autowired NodeClusterHttpService nodeClusterHttpService) {
        this.walletService = walletService;
        long currentInstanceId = walletService.getCurrentInstanceId();

        this.nodeClusterHttpService = nodeClusterHttpService;

        //------ comp
        double step = 500D;

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        fromAddress = new TextField("Account");
        fromAddress.setValue(walletService.getDefaultWalletNameAndAddress());
        fromAddress.setReadOnly(true);

        fromBalance = new TextField("Account Balance");
        fromBalance.setValue(walletService.getDefaultWalletBalanceText_onlyNac(currentInstanceId));
        fromBalance.setReadOnly(true);

        spaceSize = new TextField("Account Space Size");
        spaceSize.setValue("0MB / 0MB");
        spaceSize.setReadOnly(true);

        spacePrice = new TextField("Total Price");
        spacePrice.setReadOnly(true);
        spacePrice.setValue(step+" USDN");

        buySize = new NumberField("Buy Size(MB)");
        buySize.setWidth("180px");
        buySize.setValue(step);
        buySize.setHasControls(true);
        buySize.setMin(step);
        buySize.setMax(1000*step);
        buySize.setStep(step);

        buySize.addValueChangeListener(new HasValue.ValueChangeListener() {
            @Override
            public void valueChanged(HasValue.ValueChangeEvent event) {
                spacePrice.setValue(buySize.getValue() + " USDN");
            }
        });

        HorizontalLayout layout = new HorizontalLayout(buySize, this.spacePrice);
        layout.setFlexGrow(1.0, buySize);

        password = new PasswordField("Password");

        submitBtn = new Button("Buy");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        listBtn = new Button("View Space Files");

        //------ form
        FormLayout formLayout = new FormLayout(title, fromAddress, fromBalance, spaceSize, layout,password, submitBtn/*, listBtn*/);

        formLayout.setMaxWidth("600px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("590px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(fromAddress, 2);
        formLayout.setColspan(fromBalance, 2);
        formLayout.setColspan(spaceSize, 2);
        formLayout.setColspan(layout, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(submitBtn, 2);
        formLayout.setColspan(listBtn, 2);

        add(formLayout);

        //------ action

        listBtn.addClickListener(e -> UI.getCurrent().navigate(PinFileItemListView.class));

        submitBtn.addClickListener(e -> {
            try {
                //CompUtil.showEnableByBlockHeightDialog(walletDataService.getLastBlockHeightCache());

                Wallet defaultWallet = walletService.getDefaultWallet();
                String defaultWalletAddress = defaultWallet.getAddress();
                String ownerAddressAddressVal = defaultWalletAddress;

                long instance = CoreInstanceEnum.NAC.id;

                int buySizeVal = (int)(buySize.getValue().intValue());
                String psw = StringUtils.trim(password.getValue());

                if (buySizeVal >=0 && false) {
                    CompUtil.showError("Insufficient USDN balance");
                    submitBtn.setEnabled(true);
                    return;
                }

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

                if (true) {
                    CompUtil.showEnableByBlockHeightDialog(walletDataService.getNacLastBlockHeightByRequest());
                    submitBtn.setEnabled(true);
                    return;
                }

                HttpResult httpResult = this.nodeClusterHttpService.dfs_buySpace(
                        instance,
                        defaultWalletAddress,
                        buySizeVal
                );

                if (!httpResult.getFlag()) {
                    CompUtil.showError("Failed to buy space : " + httpResult.getMessage());
                } else {
                    CompUtil.showSuccess("Buy space successfully");
                    clearForm();
                }

            } catch (Exception exception) {
                String msg = "Failed to buy space: " + exception.getMessage();
                log.error(msg, exception);
                CompUtil.showError(msg);
            } finally {
                submitBtn.setEnabled(true);
            }

        });
    }

    private void clearForm() {
        //binder.setBean(new Wallet());
    }

}
