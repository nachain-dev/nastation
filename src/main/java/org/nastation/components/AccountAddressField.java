package org.nastation.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import org.nastation.module.address.data.Address;

import java.util.List;
import java.util.stream.Collectors;

public class AccountAddressField extends CustomField<String> {

    private ComboBox<String> labelCombo = new ComboBox<>();
    private TextField addressTxtField = new TextField();

    public TextField getAddressTxtField() {
        return addressTxtField;
    }

    public AccountAddressField(String label, List<Address> addressList) {
        setLabel(label);

        this.labelCombo.setWidth("180px");
        this.labelCombo.setPlaceholder("Contact");
        this.labelCombo.setPreventInvalidInput(true);
        this.labelCombo.setItems(addressList.stream().map(x -> x.getLabel()).collect(Collectors.toList()));
        this.labelCombo.addCustomValueSetListener(e -> {
            this.labelCombo.setValue(e.getDetail());
        });
        //number.setPattern("\\d*");
        //number.setPreventInvalidInput(true);
        HorizontalLayout layout = new HorizontalLayout(addressTxtField, this.labelCombo);
        layout.setFlexGrow(1.0, addressTxtField);
        add(layout);
    }

    @Override
    protected String generateModelValue() {
        Address address = new Address();
        address.setAddress(labelCombo.getValue());
        address.setAddress(this.addressTxtField.getValue());
        return labelCombo.getValue() + "##" + this.addressTxtField.getValue();
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