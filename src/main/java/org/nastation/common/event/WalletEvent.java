package org.nastation.common.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import org.nastation.module.wallet.data.Wallet;


public abstract class WalletEvent extends ComponentEvent<Component> {
    private Wallet wallet;

    protected WalletEvent(Component source, Wallet wallet) {
        super(source, false);
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
