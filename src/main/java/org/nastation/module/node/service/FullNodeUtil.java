package org.nastation.module.node.service;

import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author John | NaChain
 * @since 09/29/2021 10:07
 */
public class FullNodeUtil {

    /**
     *
     * @param index
     * @return
     */
    public static BigInteger calcNac(int index) {
        if (index >= 1 && index <= 100) {
            return Amount.of(BigDecimal.valueOf(2851.2), Unit.NAC).toBigInteger();
        } else if (index >= 101 && index <= 1000) {
            return Amount.of(BigDecimal.valueOf(570.24), Unit.NAC).toBigInteger();
        } else if (index >= 1001 && index <= 2000) {
            return Amount.of(BigDecimal.valueOf(285.12), Unit.NAC).toBigInteger();
        } else if (index >= 2001 && index <= 3000) {
            return Amount.of(BigDecimal.valueOf(142.56), Unit.NAC).toBigInteger();
        } else if (index >= 3001 && index <= 5000) {
            return Amount.of(BigDecimal.valueOf(142.56), Unit.NAC).toBigInteger();
        } else if (index >= 5001 && index <= 10000) {
            return Amount.of(BigDecimal.valueOf(85.536), Unit.NAC).toBigInteger();
        } else if (index >= 10001 && index <= 20000) {
            return Amount.of(BigDecimal.valueOf(42.768), Unit.NAC).toBigInteger();
        } else if (index >= 20001 && index <= 30000) {
            return Amount.of(BigDecimal.valueOf(21.384), Unit.NAC).toBigInteger();
        } else if (index >= 30001 && index <= 50000) {
            return Amount.of(BigDecimal.valueOf(19.008), Unit.NAC).toBigInteger();
        } else if (index >= 50001 && index <= 100000) {
            return Amount.of(BigDecimal.valueOf(11.4048), Unit.NAC).toBigInteger();
        } else {
            return Amount.of(BigDecimal.valueOf(10), Unit.NAC).toBigInteger();
        }
    }

    /**
     * @param index
     * @return
     */
    public static BigInteger calcNomc(int index) {
        if (index >= 1 && index <= 100) {
            return Amount.of(BigDecimal.valueOf(3), Unit.NAC).toBigInteger();
        } else if (index >= 101 && index <= 1000) {
            return Amount.of(BigDecimal.valueOf(3), Unit.NAC).toBigInteger();
        } else if (index >= 1001 && index <= 2000) {
            return Amount.of(BigDecimal.valueOf(2), Unit.NAC).toBigInteger();
        } else if (index >= 2001 && index <= 3000) {
            return Amount.of(BigDecimal.valueOf(2), Unit.NAC).toBigInteger();
        } else if (index >= 3001 && index <= 5000) {
            return Amount.of(BigDecimal.valueOf(1), Unit.NAC).toBigInteger();
        } else if (index >= 5001 && index <= 10000) {
            return Amount.of(BigDecimal.valueOf(1), Unit.NAC).toBigInteger();
        } else if (index >= 10001 && index <= 20000) {
            return Amount.of(BigDecimal.valueOf(0.5), Unit.NAC).toBigInteger();
        } else if (index >= 20001 && index <= 30000) {
            return Amount.of(BigDecimal.valueOf(0.5), Unit.NAC).toBigInteger();
        } else if (index >= 30001 && index <= 50000) {
            return Amount.of(BigDecimal.valueOf(0.1), Unit.NAC).toBigInteger();
        } else if (index >= 50001 && index <= 100000) {
            return Amount.of(BigDecimal.valueOf(0.1), Unit.NAC).toBigInteger();
        } else {
            return Amount.of(BigDecimal.valueOf(0.05), Unit.NAC).toBigInteger();
        }
    }


}
