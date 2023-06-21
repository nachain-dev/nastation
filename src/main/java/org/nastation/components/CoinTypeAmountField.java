package org.nastation.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.nachain.core.token.CoreTokenEnum;
import org.nastation.common.util.TokenUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CoinTypeAmountField extends CustomField<String> {

    private ComboBox<String> labelCombo = new ComboBox<>();

    private TextField amountField = new TextField();

    public TextField getAmountField() {
        return amountField;
    }

    public CoinTypeAmountField(String label) {
        setLabel(label);

        this.labelCombo.setWidth("180px");
        this.labelCombo.addCustomValueSetListener(e -> this.labelCombo.setValue(e.getDetail()));

        //onl nac
        List<String> coinSymbolList = TokenUtil.getEnableTokenList().stream()
                .filter(one -> one.getId() == CoreTokenEnum.NAC.id)
                .map(one -> one.getSymbol())
                .collect(Collectors.toList());

        this.labelCombo.setItems(coinSymbolList);
        this.labelCombo.setValue(CoreTokenEnum.NAC.symbol);

        //amountField.setPattern("^[+-]?(0|([1-9]\\d*))(\\.\\d+)?$");
        //amountField.setPreventInvalidInput(true);

        HorizontalLayout layout = new HorizontalLayout(amountField, this.labelCombo);
        layout.setFlexGrow(1.0, amountField);
        add(layout);
    }

    @Override
    protected String generateModelValue() {
        return labelCombo.getValue();
    }

    @Override
    protected void setPresentationValue(String text) {

    }
}