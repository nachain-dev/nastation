package org.nastation.common.event;

import com.vaadin.flow.component.Component;
import org.nastation.module.wallet.data.Wallet;

public class WalletChangeDefaultEvent extends WalletEvent {

    public WalletChangeDefaultEvent(Component source, Wallet wallet) {
        super(source, wallet);
    }
}