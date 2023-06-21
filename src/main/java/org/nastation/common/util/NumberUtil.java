package org.nastation.common.util;

import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author John | NaChain
 * @since 07/24/2021 2:49
 */
public class NumberUtil {

    public static String doubleFormat8(double d) {
        return String.format("%.9f", d);
    }

    public static double balanceAsLong2Double(long value) {

        if (value == 0) {
            return 0;
        }

        BigDecimal balance1 = BigDecimal.valueOf(value);
        return balance1.doubleValue();
    }

    public static double balanceAsLong2Double(BigDecimal value) {
        BigDecimal balance1 = value;
        BigDecimal valueDec = Amount.of(value.toBigInteger()).toDecimal(Unit.NAC);
        return valueDec.doubleValue();
    }

    public static double bigIntToNacDouble(BigInteger bi) {

        if (bi.intValue() == 0) {
            return 0D;
        }

        Amount amount = Amount.of(bi);
        BigDecimal bigDecimal = amount.toDecimal(Unit.NAC);
        return MathUtil.round(bigDecimal.doubleValue(), 9);
    }

    public static BigInteger only_unit_doubleToBigInt(double value) {
        return BigDecimal.valueOf(value).toBigInteger();
    }

    public static BigInteger nacDoubleToBigInt(double value ) {
        return Amount.of(new BigDecimal(value), Unit.NAC).toBigInteger();
    }


}
