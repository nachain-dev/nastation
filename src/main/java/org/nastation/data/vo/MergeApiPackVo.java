package org.nastation.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.chain.structure.instance.InstanceDetail;
import org.nachain.core.token.Token;

import java.util.List;

/**
 * @author John
 * @since 07/12/2022 22:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MergeApiPackVo {
    private List<ServerThreadVo> serverList;
    private List<Promotion> promotionList;
    private List<InstanceDetail> instanceDetailList;
    private List<Instance> instanceList;
    private List<InstanceUsedTokenDetail> instanceUsedTokenDetailList;
    private List<Token> tokenList;
}
