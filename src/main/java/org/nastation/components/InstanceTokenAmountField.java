package org.nastation.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.nachain.core.token.CoreTokenEnum;
import org.nastation.common.util.InstanceUtil;

import java.util.List;
import java.util.stream.Collectors;

public class InstanceTokenAmountField extends CustomField<String> {

    private ComboBox<String> instanceCombo = new ComboBox<>();

    private TextField amountField = new TextField();

    public TextField getAmountField() {
        return amountField;
    }

    public InstanceTokenAmountField(String label) {
        setLabel(label);

        this.instanceCombo.setWidth("180px");
        this.instanceCombo.addCustomValueSetListener(e -> this.instanceCombo.setValue(e.getDetail()));

        //onl nac
        List<String> instanceList = InstanceUtil.getEnableInstanceList().stream()
                .filter(one -> one.getId() >= CoreTokenEnum.NAC.id)
                .map(one -> one.getSymbol())
                .collect(Collectors.toList());

        this.instanceCombo.setItems(instanceList);
        this.instanceCombo.setValue(CoreTokenEnum.NAC.symbol);

        //amountField.setPattern("^[+-]?(0|([1-9]\\d*))(\\.\\d+)?$");
        //amountField.setPreventInvalidInput(true);

        amountField.setReadOnly(true);
        amountField.setValue("0 " + CoreTokenEnum.NAC.symbol);

        HorizontalLayout layout = new HorizontalLayout(this.instanceCombo,amountField);
        layout.setFlexGrow(1.0, instanceCombo);
        add(layout);
    }

    @Override
    protected String generateModelValue() {
        return instanceCombo.getValue();
    }

    @Override
    protected void setPresentationValue(String phoneNumber) {

    }
}