package org.nastation.components;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.nachain.core.chain.structure.instance.Instance;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.InstanceUtil;
import org.nastation.common.util.NumberUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.service.WalletService;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class InstanceTokenSelectField extends CustomField<String> {

    private ComboBox<Instance> instanceCombo = new ComboBox<>();
    private ComboBox<Map.Entry<Long, BigInteger>> tokenCombo = new ComboBox<>();
    private long currentTokenId;

    public InstanceTokenSelectField(String label,
                                    NaScanHttpService naScanHttpService,
                                     NodeClusterHttpService nodeClusterHttpService,
                                     WalletService walletService
    ) {
        setLabel(label);

        instanceCombo.setWidth("160px");
        instanceCombo.setItems(InstanceUtil.getEnableInstanceList());
        instanceCombo.setItemLabelGenerator(e-> e.getSymbol() + " [" + e.getAppName()+"]");
        instanceCombo.setAllowCustomValue(false);

        instanceCombo.addValueChangeListener(event -> {

            Instance selectInst = this.instanceCombo.getValue();
            long currentInstanceId = selectInst.getId();

            //prepare token balance
            Wallet defaultWallet1 = walletService.getDefaultWallet();
            UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(defaultWallet1.getAddress(), currentInstanceId);

            if (usedTokenBalanceDetail != null&&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
                Set<Map.Entry<Long, BigInteger>> entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();

                tokenCombo.setItems(entries);
                tokenCombo.setAllowCustomValue(false);
                tokenCombo.setItemLabelGenerator(e-> TokenUtil.getTokenSymbol(e.getKey()) + " : " + String.format("%.8f",NumberUtil.bigIntToNacDouble(e.getValue())));
                tokenCombo.addValueChangeListener(e -> {
                    Map.Entry<Long, BigInteger> entry = e.getValue();

                    //balance
                    BigInteger value = entry.getValue();

                    //set curr tokenId
                    currentTokenId = entry.getKey();

                });
            }else{
                tokenCombo.setItems(Sets.newHashSet());
            }

        });

        HorizontalLayout layout = new HorizontalLayout(this.instanceCombo,tokenCombo);
        layout.setFlexGrow(1.0, instanceCombo);
        add(layout);
    }

    @Override
    protected String generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(String s) {

    }

    public ComboBox<Instance> getInstanceCombo() {
        return instanceCombo;
    }

    public void setInstanceCombo(ComboBox<Instance> instanceCombo) {
        this.instanceCombo = instanceCombo;
    }

    public ComboBox<Map.Entry<Long, BigInteger>> getTokenCombo() {
        return tokenCombo;
    }

    public void setTokenCombo(ComboBox<Map.Entry<Long, BigInteger>> tokenCombo) {
        this.tokenCombo = tokenCombo;
    }

    public long getCurrentTokenId() {
        return currentTokenId;
    }

    public void setCurrentTokenId(long currentTokenId) {
        this.currentTokenId = currentTokenId;
    }
}