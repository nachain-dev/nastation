package org.nastation.module.wallet.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Data
@NoArgsConstructor
@Table(indexes={
        @Index(name="wal_name",columnList="name"),
        @Index(name="wal_address",columnList="address"),
        @Index(name="wal_createType",columnList="createType"),
        @Index(name="wal_defaultWallet",columnList="defaultWallet")
})
@ApiModel("Wallet entity class")
public class Wallet extends AbstractEntity {

    @ApiModelProperty("wallet name")
    private String name;

    @Transient
    private String password;

    @Transient
    private String mnemonic;

    @ApiModelProperty("encrypted wallet mnemonic")
    private String mnemonicEncrypt;

    @Transient
    private String salt;

    @ApiModelProperty("encrypted wallet salt")
    private String saltEncrypt;

    @ApiModelProperty("wallet address")
    private String address;

    @ApiModelProperty("wallet password tip")
    private String pswTip;

    private int createType;

    private boolean fullNode;

    @ApiModelProperty("if default wallet?")
    private boolean defaultWallet;

    private boolean hasBackup;

}
