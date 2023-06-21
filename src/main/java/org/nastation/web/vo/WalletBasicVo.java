package org.nastation.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author John | NaChain
 * @since 12/31/2021 1:09
 */
@Data
public class WalletBasicVo {

    private int id;

    private String name;

    private String address;

    private boolean defaultWallet;

    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime addTime;

}
