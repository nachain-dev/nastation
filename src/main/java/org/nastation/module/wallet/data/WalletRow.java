package org.nastation.module.wallet.data;

import lombok.Data;

@Data
public class WalletRow extends Wallet {

    private String nacBalance;
    private String nomcBalance;
    private String usdnBalance;
    private String addTimeText;

}
