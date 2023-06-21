package org.nastation.module.protocol.data;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nachain.core.chain.block.Block;

@Data
@NoArgsConstructor
@ApiModel("Block data row class")
public class BlockDataRow {

    @ApiModelProperty("block height text")
    private String heightText;

    @ApiModelProperty("block hash text")
    private String hashText;

    @ApiModelProperty("block time text")
    private String timeText;

    @ApiModelProperty("block miner hash text")
    private String minerText;

    @ApiModelProperty("raw block object")
    private Block rawBlock;

}
