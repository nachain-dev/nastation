package org.nastation.module.vote.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class VoteRequest {

    private long voteInstance;

    private String voteAddress;

    private String beneficiaryAddress;

    private String nominateAddress;

    private BigInteger amount;

}
