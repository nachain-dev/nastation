package org.nastation.module.wallet.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailType;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.CompUtil;
import org.nastation.common.util.InstanceUtil;
import org.nastation.module.protocol.view.TxDataListView;
import org.nastation.module.pub.view.MainLayout;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@Route(value = BroadcastTxView.Route_Value, layout = MainLayout.class)
@PageTitle(BroadcastTxView.Page_Title)
public class BroadcastTxView extends VerticalLayout {

    public static final String Route_Value = "BroadcastTxView";
    public static final String Page_Title = "Broadcast Raw Transaction";

    private TextArea txTextArea;

    private ComboBox<Instance> instanceCombo = new ComboBox<>("Instance");

    private Instance selectInstance;

    private Button sendBtn;
    private Button clearBtn;
    private Button txListBtn;

    private NodeClusterHttpService nodeClusterHttpService;
    private WalletService walletService;

    public BroadcastTxView(
            @Autowired NodeClusterHttpService nodeClusterHttpService,
            @Autowired WalletService walletService
    ) {
        this.nodeClusterHttpService = nodeClusterHttpService;
        this.walletService = walletService;

        addClassName("transfer-form-view");

        //------ comp

        H2 title = new H2();
        title.setText(Page_Title);
        title.getStyle().set("text-align", "center");

        instanceCombo.setItems(InstanceUtil.getEnableInstanceList());
        instanceCombo.setItemLabelGenerator(Instance::getSymbol);
        instanceCombo.setAllowCustomValue(false);

        Optional<Instance> first = InstanceUtil.getEnableInstanceList().stream().filter(e -> e.getId() == CoreInstanceEnum.NAC.id).findFirst();
        if (first.isPresent()) {
            instanceCombo.setValue(first.get());
        }
        instanceCombo.addValueChangeListener(event -> {
            selectInstance = event.getValue();
        });

        txTextArea = new TextArea("Raw Transaction");
        txTextArea.setPlaceholder("Raw mail json text");
        txTextArea.setValue("");

        sendBtn = new Button("Broadcast");
        sendBtn.setEnabled(true);
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        clearBtn = new Button("Clear");
        txListBtn = new Button("Transaction List");

        //------ form

        add(CompUtil.getCenterFormLayout(title, instanceCombo,txTextArea, sendBtn, clearBtn/*,txListBtn*/));

        //------ action

        clearForm();

        clearBtn.addClickListener(e -> clearForm());
        txListBtn.addClickListener(e -> {
            UI.getCurrent().navigate(TxDataListView.class);
        });
        sendBtn.addClickListener(e -> {

            try {

                if (selectInstance==null) {
                    CompUtil.showError("Please select target instance first");
                    return;
                }

                String mailJson = StringUtils.trim(txTextArea.getValue());
                if (StringUtils.isBlank(mailJson)) {
                    CompUtil.showError("Raw Transaction json text can not be empty");
                    return;
                }

                long instanceId = selectInstance.getId();

                Mail mail = Mail.newMail(MailType.MSG_SEND_TX, mailJson);
                String hash = mail.getHash();

                boolean flag = nodeClusterHttpService.broadcast(mailJson, instanceId);

                log.info("Broadcast raw tx detail: [ txJson = {} ,hash = {} , instanceId = {} , flag = {} ]",
                        mailJson, hash, instanceId, flag
                );

                if (flag) {

                    clearForm();

                    CompUtil.showSuccess("Raw transaction has been broadcast");

                    walletService.setCurrentWalletTxSentWrapper(new WalletTxSentWrapper(WalletTxSentWrapper.FROM_VIEW_CROSS_BROADCAST,hash, walletService.getDefaultWallet()));

                    UI.getCurrent().navigate(WalletSendResultView.class);

                } else {
                    CompUtil.showError("Raw transaction broadcast failed");
                }

            } catch (Exception exception) {
                String msg = "Raw transaction broadcast error";
                log.error(msg, exception);
                CompUtil.showError(msg);
            }

        });

    }

    private void clearForm() {
        txTextArea.setValue("");
    }


}
