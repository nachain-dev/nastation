package org.nastation.common.util;

import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.transaction.TxUtils;
import org.nachain.core.crypto.Key;
import org.nachain.core.wallet.walletskill.NirvanaWalletSkill;

import java.security.spec.InvalidKeySpecException;

/**
 * @author John | NaChain
 * @since 12/24/2021 20:52
 */
public class WalletUtil {

    public static boolean isAddressHasKeyword(String address) {
        return TxUtils.isAddressHasKeyword(address);
    }


    public static boolean isAddressValid(String address) {
        address = address.trim();

        if (StringUtils.isBlank(address)) {
            return false;
        }

        if (!StringUtils.startsWithIgnoreCase(address, "N")) {
            return false;
        }

        return address.length() == 34;
    }


    public static String shortAddress(String data) {

        if (StringUtils.isEmpty(data)) {
            return "";
        }

        data = data.trim();

        if (StringUtils.isBlank(data)) {
            return "*";
        }

        if (isAddressHasKeyword(data)) {
            return data;
        }

        int length = data.length();

        if (length <= 8) {
            return data;
        }

        return data.substring(0, 6) + "...." + data.substring(length - 6, length);
    }

    public static String shortHash(String data) {

        if (StringUtils.isBlank(data)) {
            return "*";
        }

        data = data.trim();
        int length = data.length();

        if (length <= 8) {
            return data;
        }

        return data.substring(0, 8) + "...." + data.substring(length - 8, length);
    }

    public static Key getKey() {
        try {
            Key key = Key.toKey0x("");
            key.init(new NirvanaWalletSkill());
            String s = key.toWalletAddress();
            System.out.println(s);

            return key;
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


}
