package org.nastation.module.fullnode.data;

import lombok.Data;

@Data
public class FullNodeRow {

    private String id;

    private String operatorAddress;

    private String ownerAddress;

    private String beneficiaryAddress;

    private String beneficiaryChangeTx;

    private String requiredNomc;

    private String requiredNac;

    private String paidNomc;

    private String paidNac;

    private String paidNomcTx;

    private boolean enabled;

}