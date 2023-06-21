
package org.nastation.common.util;

import com.google.common.collect.Lists;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.Token;
import org.nachain.core.token.TokenProtocolEnum;
import org.nastation.data.service.WalletDataService;

import java.util.List;
import java.util.stream.Collectors;

public class TokenUtil {

    public static List<Token> getEnableTokenList() {

        List<Token> tokenList = null;

        if (WalletDataService.MERGE_API_PACK_VO != null) {
            tokenList = WalletDataService.MERGE_API_PACK_VO.getTokenList();
        }

        if (tokenList == null) {
            tokenList = Lists.newArrayList();
            CoreTokenEnum[] values = CoreTokenEnum.values();
            for (CoreTokenEnum each : values) {

                if (each.id < 0) {
                    continue;
                }

                Token instance = adaptToToken(each);
                tokenList.add(instance);
            }
        }

        return tokenList;
    }

    public static List<Token> getEnableTokenExcludeNftList() {
        return getEnableTokenList().stream().filter(t->t.getTokenProtocol().id != TokenProtocolEnum.NFT.id).collect(Collectors.toList());
    }

    private static Token adaptToToken(CoreTokenEnum nac) {
        Token one = new Token();

        one.setInfo(nac.info);
        one.setName(nac.name);
        one.setSymbol(nac.symbol);
        one.setId(nac.id);

        return one;
    }

    public static Token getToken(long tokenId) {
        return getEnableTokenList().stream().filter(token -> token.getId() == tokenId).findFirst().orElse(null);
    }

    public static Token getTokenBySymbol(String symbol) {
        return getEnableTokenList().stream().filter(token -> token.getSymbol().equalsIgnoreCase(symbol)).findFirst().orElse(null);
    }

    public static String getTokenSymbol(long tokenId) {
        Token token1 = getToken(tokenId);
        if (token1 == null) {
            return "";
        }
        return token1.getSymbol();
    }

    public static Token getNacToken() {
        return getToken(CoreTokenEnum.NAC.id);
    }

    public static Token getNomcToken() {
        return getToken(CoreTokenEnum.NOMC.id);
    }

    public static Token getUsdnToken() {
        return getToken(CoreTokenEnum.USDN.id);
    }

    public static boolean isNftToken(Token token) {
        return token.getTokenProtocol() == TokenProtocolEnum.NFT;
    }

}