package org.nastation.module.vote.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SuperNodeVote {

    private long voteInstance;

    private String voteTx;

    private long blockHeight;

    private long unlockHeight;

    private String voteAddress;

    private String beneficiaryAddress;

    private String nominateAddress;

    private BigInteger amount;

    private String hash;

    private String cancelTx;

    private String withdrawTx;
}