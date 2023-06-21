package org.nastation.common.service;

import lombok.extern.slf4j.Slf4j;
import org.nastation.data.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
@Component
@Slf4j
public class EcologyUrlService {

    @Autowired
    private AppConfig appConfig;

    /*----------------------------------------------*
     * Na Service
    /*----------------------------------------------*/

    public String buildGetPriceUrlByService() {
        return appConfig.getServiceUrl() + "/api/v1/getPrice";
    }

    /*----------------------------------------------*
     * Na Website
    /*----------------------------------------------*/

    public String buildVersionCheckUrlByWebsite() {
        return appConfig.getWebsiteUrl() + "/api/appVersionCheck?type=desktop";
    }

    /*----------------------------------------------*
     * Na Scan
    /*----------------------------------------------*/

    public String buildAccountUrlByScan(String address, long instanceId) {
        return appConfig.getNascanUrl() + "/account/detail?addr=" + address + "&instanceId=" + instanceId;
    }

    public String buildNftItemDetailUrlByScan(long nftItemId,long tokenId,long instanceId) {
        return appConfig.getNascanUrl() + String.format("/nftItem/detail?collInstanceId=%s&tokenId=%s&nftId=%s", instanceId, tokenId, nftItemId);
    }
    public String buildNftCollDetailUrlByScan(long instanceId,long tokenId) {
        return appConfig.getNascanUrl() + String.format("/nftColl/detail?collInstanceId=%s&collTokenId=%s", instanceId, tokenId);
    }

    public String buildBlockUrlFromHeightByScan(long height, long instanceId) {
        return appConfig.getNascanUrl() + "/block/detail?height=" + height + "&instanceId=" + instanceId;
    }

    public String buildBlockUrlFromHashByScan(String hash, long instanceId) {
        return appConfig.getNascanUrl() + "/block/detail?hash=" + hash + "&instanceId=" + instanceId;
    }

    public String buildTxUrlByScan(String hash, long instanceId) {
        return appConfig.getNascanUrl() + "/tx/detail?hash=" + hash + "&instanceId=" + instanceId;
    }

    public String buildGetLastBlockHeightUrlByScan(long instanceId) {
        return appConfig.getNascanUrl() + "/api/v1/getLastBlockHeight?instanceId=" + instanceId;
    }

    public String buildGetGasFeeUrlByScan(long instanceId) {
        return appConfig.getNascanUrl() + "/api/v1/getGasFee?instanceId=" + instanceId;
    }

    public String buildGetUsedTokenBalanceDetailByScan(String address, long instance) {
        return appConfig.getNascanUrl() + "/api/v1/getUsedTokenBalanceDetailByMap?address=" + address + "&instanceId=" + instance;
    }

    public String buildTokenDetailPageByScan(long id) {
        return appConfig.getNascanUrl() + "/token/detail?id=" + id;
    }

    public String buildInstanceDetailPageByScan(long id) {
        return appConfig.getNascanUrl() + "/instance/detail?id=" + id;
    }

    public String buildGetMergeApiPackByScan() {
        return appConfig.getNascanUrl() + "/api/v1/getMergeApiPack";
    }

    /*----------------------------------------------*
     * NodeCluster
    /*----------------------------------------------*/

    public String buildMergeApiV2ByNodeCluster() {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi";
    }

    public String buildGetBroadcastTxUrlByNodeCluster() {
        return appConfig.getNodeClusterUrl() + "/broadcast/mail/tx/v2";
    }

    public String buildGetAccountTxHeightUrlByNodeCluster() {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=account&method=getTxHeight";
    }

    public String buildGetCalcDeployNacPriceUrlByNodeCluster() {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=oracle&method=getCalcDeployNacPrice";
    }

    public String buildGetGasUrlByNodeCluster(long instanceId) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=tx&method=getGas&instance="+instanceId;
    }

    public String buildGetNftOrderTotalByNodeCluster(long instanceId,long nftTokenId,long mintAmount) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getNftOrderTotal&instance="+instanceId+"&nftTokenId="+nftTokenId+"&mintAmount="+mintAmount;
    }

    public String buildGetAllNftCollectionByNodeCluster() {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getAllNftCollection";
    }

    public String buildGetNftCollPageByNodeCluster(int pageNum) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getNftCollPage&pageNum="+ pageNum;
    }

    // get one nft coll
    public String buildGetNftCollectionByNodeCluster(long collInstanceId,long collTokenId) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getNftCollection&collTokenId="+collTokenId+"&collInstanceId="+collInstanceId;
    }

    // get one nft item
    public String buildGetNftItemByNodeCluster(long nftId,long collInstanceId) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getNftItem&collInstanceId="+collInstanceId +"&nftId="+nftId;
    }

    // get page by wallet address then returns NftColl list
    public String buildGetAccountNftCollPageByNodeCluster(String walletAddress,int page) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getAccountNftCollPage&walletAddress="+walletAddress+"&pageNum="+page;
    }

    // get page by wallet address and collection token id then returns NftItem list
    public String buildGetAccountNftItemPageByNodeCluster(String walletAddress,long collTokenId,int page) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getAccountNftItemPage&walletAddress="+walletAddress+"&collTokenId="+collTokenId+"&pageNum="+page;
    }

    public String buildGetAllAccountNftItemWrapByNodeCluster(String walletAddress,long collTokenId) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getAllAccountNftItemWrap&walletAddress="+walletAddress+"&collTokenId="+collTokenId;
    }

    public String buildGetNftItemPageByNodeCluster(long collInstanceId,int page) {
        return appConfig.getNodeClusterUrl() + "/api/v2/mergeApi?module=nft&method=getNftItemPage&pageNum="+page+"&collInstanceId="+collInstanceId;
    }

    /*----------------------------------------------*
     * Data Center
    /*----------------------------------------------*/

    public String buildGetBlockUrlByDC() {
        return appConfig.getDataCenterUrl() + "/dc/getBlock";
    }

    public String buildGetBlockListUrlByDC() {
        return appConfig.getDataCenterUrl() + "/dc/getBlockList";
    }

}
