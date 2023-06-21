package org.nastation.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.token.Token;

import java.util.List;
import java.util.stream.Collectors;

public class InstanceTokenField extends CustomField<String> {

    private ComboBox<String> instanceCombo = new ComboBox<>();
    private ComboBox<String> tokenCombo = new ComboBox<>();
    private TextField amountField = new TextField();

    public TextField getAmountField() {
        return amountField;
    }

    public InstanceTokenField(String label, List<Instance> instanceList, List<Token> tokenList) {
        setLabel(label);

        this.instanceCombo.setWidth("150px");
        this.instanceCombo.setPlaceholder("Instance");
        this.instanceCombo.setPreventInvalidInput(true);
        this.instanceCombo.setItems(instanceList.stream().map(x -> x.getSymbol()).collect(Collectors.toList()));
        this.instanceCombo.addCustomValueSetListener(e -> {
            this.instanceCombo.setValue(e.getDetail());
        });

        this.tokenCombo.setWidth("150px");
        this.tokenCombo.setPlaceholder("Token");
        this.tokenCombo.setPreventInvalidInput(true);
        this.tokenCombo.setItems(tokenList.stream().map(x -> x.getSymbol()).collect(Collectors.toList()));
        this.tokenCombo.addCustomValueSetListener(e -> {
            this.tokenCombo.setValue(e.getDetail());
        });
        //number.setPattern("\\d*");
        //number.setPreventInvalidInput(true);
        HorizontalLayout layout = new HorizontalLayout(this.tokenCombo,this.instanceCombo, amountField);
        //layout.setFlexGrow(1.0, instanceCombo);
        //layout.setFlexGrow(1.0, instanceCombo);
        add(layout);
    }

    @Override
    protected String generateModelValue() {
        return instanceCombo.getValue() + "##" +tokenCombo.getValue()+ "##" + this.amountField.getValue();
    }

    @Override
    protected void setPresentationValue(String label) {
    }

    public static String getOnlyAddress(String text) {

        if (StringUtils.isBlank(text)) {
            return "";
        }

        String[] split = text.split("##");
        String address = split[1];
        return address;
    }

    public static String getOnlyLabel(String text) {

        if (StringUtils.isBlank(text)) {
            return "";
        }

        String[] split = text.split("##");
        String label = split[0];
        return label;
    }
}