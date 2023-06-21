package org.nastation.data.vo;

public class Promotion {

    String clientName;

    String wallet;

    String fromTx;

    public String getClientName() {
        return clientName;
    }

    public Promotion setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public String getWallet() {
        return wallet;
    }

    public Promotion setWallet(String wallet) {
        this.wallet = wallet;
        return this;
    }

    public String getFromTx() {
        return fromTx;
    }

    public Promotion setFromTx(String fromTx) {
        this.fromTx = fromTx;
        return this;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "clientName='" + clientName + '\'' +
                ", wallet='" + wallet + '\'' +
                ", fromTx='" + fromTx + '\'' +
                '}';
    }
}
