package org.nastation.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nachain.core.token.Token;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceUsedTokenDetail {

    private long instance;

    private Set<Token> tokenSet;

}
