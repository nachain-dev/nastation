package org.nastation.module.dapp.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DAppTypeField extends CustomField<String> {
    private ComboBox<String> labelCombo = new ComboBox<>();
    private TextField address = new TextField();

    public DAppTypeField(String label) {
        setLabel(label);
        this.labelCombo.setWidth("100%");
        this.labelCombo.setPlaceholder("Type");
        this.labelCombo.setPreventInvalidInput(true);
        this.labelCombo.setItems("DApp", "DWeb");
        this.labelCombo.addCustomValueSetListener(e -> this.labelCombo.setValue(e.getDetail()));
        //number.setPattern("\\d*");
        //number.setPreventInvalidInput(true);
        HorizontalLayout layout = new HorizontalLayout(this.labelCombo);
        layout.setFlexGrow(1.0, this.labelCombo);
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