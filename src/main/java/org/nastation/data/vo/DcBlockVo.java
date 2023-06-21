package org.nastation.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Data center Block Vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DcBlockVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String block;
    private List<String> txs;

}