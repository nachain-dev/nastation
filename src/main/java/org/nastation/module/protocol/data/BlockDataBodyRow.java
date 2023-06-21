package org.nastation.module.protocol.data;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel("Block body class")
public class BlockDataBodyRow{

    @ApiModelProperty("block data row object")
    private BlockDataRow blocDataRow;

    @ApiModelProperty("transaction hash list")
    private List<String> txList;

}
