package org.nastation.module.protocol.data;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nachain.core.chain.transaction.Tx;

@Data
@NoArgsConstructor
@ApiModel("Transaction data row class")
public class TxDataRow {

    @ApiModelProperty("tx hash text")
    private String hashText;

    @ApiModelProperty("tx height text")
    private String txHeightText;

    @ApiModelProperty("tx create time text")
    private String dateTimeText;

    @ApiModelProperty("from address text")
    private String fromText;

    @ApiModelProperty("to address text")
    private String toText;

    @ApiModelProperty("tx amount text")
    private String amountText;

    @ApiModelProperty("tx status text")
    private String statusText;

    //@ApiModelProperty("tx status")
    //private int status;

    @ApiModelProperty("tx fee text")
    private String feeText;

    @ApiModelProperty("tx raw data text")
    private Tx rawTx;

}
