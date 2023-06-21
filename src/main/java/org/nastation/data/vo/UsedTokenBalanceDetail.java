package org.nastation.data.vo;

import lombok.Data;

import java.math.BigInteger;
import java.util.Map;

@Data
public class UsedTokenBalanceDetail {

    private long instance;

    private Map<Long, BigInteger> tokenBalanceMap;

}