package org.nastation.module.wallet.data;

import lombok.Data;
import org.nachain.core.wallet.keystore.Keystore;

/**
 * @author John | NaChain
 * @since 10/02/2021 23:51
 */
@Data
public class KeystoreWrapper {

    private Keystore keystore;

    private Wallet wallet;

    public KeystoreWrapper() {
    }

    public KeystoreWrapper(Keystore keystore, Wallet wallet) {
        this.keystore = keystore;
        this.wallet = wallet;
    }
}
