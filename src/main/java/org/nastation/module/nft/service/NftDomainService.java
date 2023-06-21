package org.nastation.module.nft.service;

import org.apache.commons.compress.utils.Lists;
import org.nachain.core.token.Token;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.protocol.NFTProtocol;
import org.nastation.common.model.PageWrap;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.InstanceUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author John
 * @since 10/05/2022 18:06
 */
@Component
public class NftDomainService {

    @Autowired
    private WalletService walletService;

    @Autowired
    private NaScanHttpService naScanHttpService;

    @Autowired
    private NodeClusterHttpService nodeClusterHttpService;

    public List<NftCollWrap> getDefaultWalletNftCollWrapList() {
        String address = walletService.getDefaultWalletAddress();

        PageWrap<NftCollWrap> accountNftCollPage = nodeClusterHttpService.getAccountNftCollPage(address, 1);

        List<NftCollWrap> dataList = accountNftCollPage.getDataList();
        if (dataList == null) {
            dataList = Lists.newArrayList();
        }

        return dataList;
    }

    public String nftCollLabelGenerator(NftCollWrap nftCollWrap) {

        if (nftCollWrap == null) {
            return "";
        }

        NftCollection nftCollection = nftCollWrap.getNftCollection();

        long instance = nftCollection.getInstance();
        String instanceName = InstanceUtil.getInstanceName(instance);

        Token token = TokenUtil.getToken(nftCollection.getToken());
        String tokenName = token.getName();

        NFTProtocol nftProtocol = nftCollection.getNftProtocol();
        String protocolName = nftProtocol.getProtocolName();

        long mintTokenId = nftProtocol.getMintTokenId();
        String mintTokenSymbol = TokenUtil.getTokenSymbol(mintTokenId);

        return String.format("%s [Mint by %s]", tokenName, mintTokenSymbol);
    }
}
