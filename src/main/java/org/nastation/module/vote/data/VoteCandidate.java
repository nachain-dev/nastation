package org.nastation.module.vote.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class VoteCandidate {

    private String candidateAddress;

    private BigInteger pledgeAmount;

    private BigInteger voteAmount;
}



