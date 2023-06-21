package org.nastation.module.vote.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SuperNodeCandidate {

    private String candidateAddress;

    private BigInteger pledgeAmount;

    private long blockHeight;

    private long lockedBlockHeight;

    private String fromTx;
}