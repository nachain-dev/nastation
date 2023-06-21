package org.nastation.common.event;

import com.vaadin.flow.component.Component;
import org.nastation.module.wallet.data.Wallet;

public class WalletCreateEvent extends WalletEvent {

    public WalletCreateEvent(Component source, Wallet wallet) {
        super(source, wallet);
    }
}