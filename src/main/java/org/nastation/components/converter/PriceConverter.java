package org.nastation.components.converter;

import com.vaadin.flow.data.converter.StringToBigDecimalConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PriceConverter extends StringToBigDecimalConverter {

    public PriceConverter() {
        super(BigDecimal.ZERO, "Cannot convert value to a number.");
    }

    @Override
    protected NumberFormat getFormat(Locale locale) {
        // Always display currency with two decimals
        final NumberFormat format = super.getFormat(locale);
        if (format instanceof DecimalFormat) {
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(2);
        }
        return format;
    }
}