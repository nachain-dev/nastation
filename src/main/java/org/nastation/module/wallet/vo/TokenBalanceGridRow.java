package org.nastation.module.wallet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * @author John
 * @since 07/17/2022 11:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenBalanceGridRow {

    private Long token;
    private Long instance;
    private BigInteger balance;
    private String address;

}
