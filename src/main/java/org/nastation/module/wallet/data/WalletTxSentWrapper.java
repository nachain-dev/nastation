package org.nastation.module.wallet.data;

import lombok.Data;

/**
 * @author John | NaChain
 * @since 10/02/2021 23:51
 */
@Data
public class WalletTxSentWrapper {

    public static final int FROM_VIEW_TRANSFER = 0;
    public static final int FROM_VIEW_DEPLOY_TOKEN = 1;
    public static final int FROM_VIEW_CROSS_INSTANCE_TRANSFER = 2;
    public static final int FROM_VIEW_CROSS_BROADCAST = 3;

    public static final int FROM_VIEW_NFT_DEPLOY = 4;

    public static final int FROM_VIEW_NFT_MINT = 5;

    public static final int FROM_VIEW_NFT_SEND = 6;

    private int fromView = 0;

    private int instanceId = -1;

    private String hash;

    private Wallet wallet;

    public WalletTxSentWrapper() {
    }

    public WalletTxSentWrapper(int fromView, String hash, Wallet wallet) {
        this.fromView = fromView;
        this.hash = hash;
        this.wallet = wallet;
    }
}
