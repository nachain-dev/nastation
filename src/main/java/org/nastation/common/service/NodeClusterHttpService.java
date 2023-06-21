package org.nastation.common.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.nachain.core.token.nft.NftItem;
import org.nachain.core.token.nft.NftItemDetail;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.collection.NftCollectionDetail;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.nft.dto.NftItemWrap;
import org.nastation.common.model.ApiResult;
import org.nastation.common.model.HttpResult;
import org.nastation.common.model.PageWrap;
import org.nastation.common.util.HttpUtil;
import org.nastation.common.util.JsonUtil;
import org.nastation.data.rocksdb.v2.NftCollectionDetailDAO;
import org.nastation.data.rocksdb.v2.NftItemDetailDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Node Cluster
 *
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
@Component
@Slf4j
public class NodeClusterHttpService {

    @Autowired
    private EcologyUrlService ecologyUrlService;

    /*------------
     * fullnode
     * ------------*/

    public long fullnode_getOrderId(long instance, String address) {
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "fullnode")
                    .data("method", "getOrderId")
                    .execute();

            String resultJson = executeResult.body();
            HttpResult result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
            return Integer.valueOf(result.getData().toString());
        } catch (Exception e) {
            log.error("fullnode_getOrderId error:", e);
        }

        return 0;
    }

    public HttpResult submitRedeem(long instance
            , long orderID
            , String ownerAddress
            , String beneficiaryAddress
            , double needNomc
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "fullnode")
                    .data("method", "submitRedeem")
                    .data("orderID", String.valueOf(orderID))

                    .data("beneficiaryAddress", String.valueOf(beneficiaryAddress))
                    .data("ownerAddress", String.valueOf(ownerAddress))
                    .data("payNomc", String.valueOf(needNomc))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("fullnode_submitRedeem error:", e);
        }
        return result;
    }

    public HttpResult getMyList(long instance, String address) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "fullnode")
                    .data("method", "getMyList")
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("fullnode_getMyList error:", e);
        }
        return result;
    }

    public HttpResult getList(long instance, String address) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "fullnode")
                    .data("method", "getList")
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("fullnode_getList error:", e);
        }
        return result;
    }

    /*------------
     * vote
     * ----------*/

    public HttpResult vote_submitVote(long instance
            , String voteAddress
            , String ownerAddress
            , String beneficiaryAddress
            , String nominateAddress
            , int voteAmount
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "vote")
                    .data("method", "submitVote")
                    .data("instance", String.valueOf(instance))
                    .data("voteAddress", voteAddress)
                    .data("ownerAddress", ownerAddress)
                    .data("beneficiaryAddress", beneficiaryAddress)
                    .data("nominateAddress", nominateAddress)
                    .data("voteAmount", String.valueOf(voteAmount))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("vote_submitVote error:", e);
        }
        return result;
    }


    public HttpResult vote_getList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "vote")
                    .data("method", "getList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("vote_getList error:", e);
        }
        return result;
    }

    public HttpResult vote_getMyList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "vote")
                    .data("method", "getMyList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("vote_getMyList error:", e);
        }
        return result;
    }

    public HttpResult vote_getNodeStatList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "vote")
                    .data("method", "getNodeStatList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("getNodeStatList error:", e);
        }
        return result;
    }

    /*------------
     * dfs
     * ----------*/

    public HttpResult dfs_buySpace(long instance
            , String walletAddress
            , int buySize
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dfs")
                    .data("method", "submitVote")
                    .data("instance", String.valueOf(instance))
                    .data("walletAddress", walletAddress)
                    .data("buySize", String.valueOf(buySize))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("dfs_buySpace error:", e);
        }
        return result;
    }

    public HttpResult dfs_getTempFileList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dfs")
                    .data("method", "getTempFileList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("dfs_getTempFileList error:", e);
        }
        return result;
    }

    public HttpResult dfs_getPinFileList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dfs")
                    .data("method", "getPinFileList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("getPinFileList error:", e);
        }
        return result;
    }

    /*------------
     * dapp
     * ----------*/

    public HttpResult dapp_submitDeploy(long instance
            , String ownerAddress
            , int protocolType
            , long tokenType
            , String tokenName
            , String tokenSymbol
            , long tokenTotalSupply
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dfs")
                    .data("method", "submitVote")
                    .data("instance", String.valueOf(instance))

                    .data("ownerAddress", ownerAddress)
                    .data("protocolType", String.valueOf(protocolType))
                    .data("tokenType", String.valueOf(tokenType))
                    .data("tokenName", String.valueOf(tokenName))
                    .data("tokenSymbol", String.valueOf(tokenSymbol))
                    .data("tokenTotalSupply", String.valueOf(tokenTotalSupply))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("dapp_submitDeploy error:", e);
        }
        return result;
    }


    /*------------
     * dns
     * ----------*/

    public HttpResult dns_submitRent(long instance
            , String rentDomain
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dns")
                    .data("method", "submitRent")
                    .data("instance", String.valueOf(instance))

                    .data("rentDomain", rentDomain)
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("dns_submitRent error:", e);
        }

        return result;
    }

    public HttpResult dns_submitApply(long instance
            , String dappHash
            , String subDomainName
            , String domainName
            , int blockYear
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dns")
                    .data("method", "submitApply")
                    .data("instance", String.valueOf(instance))

                    .data("dappHash", dappHash)
                    .data("subDomainName", subDomainName)
                    .data("domainName", domainName)
                    .data("blockYear", String.valueOf(blockYear))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("dns_submitApply error:", e);
        }

        return result;
    }


    public HttpResult dns_getMyRentList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dns")
                    .data("method", "getMyRentList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("getMyRentList error:", e);
        }
        return result;
    }

    public HttpResult dns_getMyApplyList(long instance) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dns")
                    .data("method", "getMyApplyList")
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("getMyApplyList error:", e);
        }
        return result;
    }


    /*------------
     * cross
     * ----------*/

    public HttpResult dns_submitCross(long instance
            , String walletAddress
            , int fromInstance
            , int toInstance
            , double amount
    ) {
        HttpResult result = HttpResult.me().asFalse();
        String url = ecologyUrlService.buildMergeApiV2ByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("module", "dns")
                    .data("method", "submitRent")
                    .data("instance", String.valueOf(instance))

                    .data("walletAddress", walletAddress)
                    .data("fromInstance", String.valueOf(fromInstance))
                    .data("toInstance", String.valueOf(toInstance))
                    .data("amount", String.valueOf(toInstance))
                    .execute();

            String resultJson = executeResult.body();
            result = JsonUtil.parseObjectByOm(resultJson, HttpResult.class);
        } catch (Exception e) {
            log.error("dns_submitCross error:", e);
        }

        return result;
    }

    /*------------
     * other
     * ----------*/

    public long getAccountTxHeight(long instance, String address, long token) {
        String url = ecologyUrlService.buildGetAccountTxHeightUrlByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url)
                    .data("address", address)
                    .data("token", String.valueOf(token))
                    .data("instance", String.valueOf(instance))
                    .execute();

            String resultJson = executeResult.body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(resultJson, JsonNode.class);

            JsonNode dataJsonNode = jsonNode.get("data");
            JsonNode flagJsonNode = jsonNode.get("flag");
            boolean flag = flagJsonNode.asBoolean();
            if (flag) {
                return dataJsonNode.asLong();
            }
        } catch (Exception e) {
            log.error("getAccountTxHeight error:", e);
        }

        return 0L;
    }

    public boolean broadcast(String mailJson, long instanceId) {

        boolean flag = false;
        String url = ecologyUrlService.buildGetBroadcastTxUrlByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.post(url)
                    .data("mail", mailJson)
                    .data("instance", String.valueOf(instanceId))
                    .execute();

            String resultJson = executeResult.body();
            flag = resultJson.contains("true");

        } catch (Exception e) {
            log.error("broadcast error:", e);
        }

        return flag;
    }

    public BigInteger getNftOrderTotal(long instanceId, long nftTokenId, long mintAmount) {
        String url = ecologyUrlService.buildGetNftOrderTotalByNodeCluster(instanceId, nftTokenId, mintAmount);
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);

            JsonNode dataJsonNode = jsonNode.get("data");
            JsonNode flagJsonNode = jsonNode.get("flag");
            boolean flag = flagJsonNode.asBoolean();
            if (flag) {
                //price
                //nextNftId
                //total
                return new BigInteger(dataJsonNode.get("total").toString());
            }

        } catch (Exception e) {
            log.error("getNftOrderTotal error : " + e.getMessage(), e);
        }

        return BigInteger.ZERO;
    }

    public BigInteger get_gasFee(long instanceId) {
        String url = ecologyUrlService.buildGetGasUrlByNodeCluster(instanceId);
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            HttpResult result = JsonUtil.parseObjectByOm(json, HttpResult.class);
            return new BigInteger(String.valueOf(result.getData()));

        } catch (Exception e) {
            log.error("get_gasFee error : " + e.getMessage(), e);
        }

        return BigInteger.ZERO;
    }

    public BigInteger getCalcDeployNacPrice() {
        String url = ecologyUrlService.buildGetCalcDeployNacPriceUrlByNodeCluster();
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            if (StringUtils.isBlank(json)) {
                return BigInteger.ZERO;
            }

            HttpResult result = JsonUtil.parseObjectByOm(json, HttpResult.class);
            return BigInteger.valueOf(Long.valueOf(String.valueOf(result.getData())));

        } catch (Exception e) {
            log.error("getCalcDeployNacPrice error : " + e.getMessage(), e);
        }

        return BigInteger.ZERO;
    }

    public NftCollWrap toNftCollWrap(NftCollection nftColl){

        if (nftColl == null) {
            return null;
        }

        long nftCollInstance = nftColl.getInstance();
        long token = nftColl.getToken();

        NftCollWrap wrap = new NftCollWrap();
        wrap.setNftCollInstanceId(nftCollInstance);
        wrap.setNftCollTokenId(token);
        wrap.setNftCollection(nftColl);

        wrap.setNftCollectionDetail(toNftCollectionDetail(nftColl));

        return wrap;
    }

    public NftCollectionDetail toNftCollectionDetail(NftCollection nftColl) {

        if (nftColl == null) {
            return new NftCollectionDetail();
        }

        String baseURI = nftColl.getNftProtocol().getBaseURI();
        long collTokenId = nftColl.getToken();
        long nftCollInstance = nftColl.getInstance();

        String nftUrl = String.format("%s%s.json", baseURI, "nft-coll-detail");
        NftCollectionDetail nftCollCover = getNftCollCoverParseCache(nftCollInstance,collTokenId, nftUrl);
        return nftCollCover;
    }

    private Map<String, NftCollectionDetail> nftCollCoverMap = Maps.newHashMap();

    private NftCollectionDetail getNftCollCoverParseCache(long nftCollInstance, long collTokenId, String detailUrl) {
        String key = "ns-ncd-"+collTokenId;

        NftCollectionDetail cover = nftCollCoverMap.get(key);
        if (cover == null) {

            try {

                NftCollectionDetailDAO nftCollDetailDAO = new NftCollectionDetailDAO(nftCollInstance);

                // first  : from redis
                cover = nftCollDetailDAO.get(collTokenId);

                // check again
                if (cover == null) {

                    // 2: from url
                    Connection.Response executeResult = HttpUtil.getNftResource(detailUrl).execute();
                    String json = executeResult.body();

                    if (StringUtils.isNotBlank(json)) {
                        cover = JsonUtil.parseObjectByOm(json, NftCollectionDetail.class);
                        nftCollCoverMap.put(key, cover);

                        //save cache
                        nftCollDetailDAO.add(collTokenId, json);
                    }
                }

            } catch (Exception e) {
                log.error("getNftCollCoverParseCache error : " + e.getMessage() +",detail url = "+detailUrl, e);
            }

        }
        return cover;
    }

    public boolean isNftCollectionDetailReady(String baseUri) {
        String detailUrl = String.format("%s%s.json", baseUri, "nft-coll-detail");
        try {
            Connection.Response executeResult = HttpUtil.getNftResource(detailUrl).execute();
            String json = executeResult.body();

            if (StringUtils.isNotBlank(json)) {
                NftCollectionDetail detail = JsonUtil.parseObjectByOm(json, NftCollectionDetail.class);

                if (detail != null) {
                    return StringUtils.isNotEmpty(detail.getCoverIcon());
                }
            }
        } catch (Exception e) {
            log.error("Check if nft collection can be accessed error : " + e.getMessage() +",detail url = "+detailUrl, e);
        }

        return false;
    }

    public PageWrap<NftCollWrap> toNftCollWrapPage(PageWrap<NftCollection> pageWrap){

        PageWrap<NftCollWrap> result = new PageWrap<NftCollWrap>();

        if (pageWrap == null) {
            return result;
        }

        List<NftCollection> dataList = pageWrap.getDataList();

        if (dataList == null) {
            return result;
        }

        List<NftCollWrap> list = com.google.common.collect.Lists.newArrayList();
        for (NftCollection nftColl : dataList) {
            list.add(toNftCollWrap(nftColl));
        }

        result.setDataList(list);
        result.setPageSize(pageWrap.getPageSize());
        result.setDataTotal(pageWrap.getDataTotal());
        result.setPageTotal(pageWrap.getPageTotal());
        result.setPageNumber(pageWrap.getPageNumber());

        return result;
    }

    public List<NftCollWrap> getAllNftCollectionByNodeCluster() {
        String url = ecologyUrlService.buildGetAllNftCollectionByNodeCluster();
        List<NftCollWrap> list = com.google.common.collect.Lists.newArrayList();

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            ObjectMapper objectMapper = JsonUtil.om();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            NftCollection[] nftActivities = objectMapper.treeToValue(data, NftCollection[].class);

            if (ArrayUtils.isNotEmpty(nftActivities)) {
                for (NftCollection nftColl : nftActivities) {
                    list.add(toNftCollWrap(nftColl));
                }
            }


        } catch (Exception e) {
            log.error("getAllNftCollectionByNodeCluster error : " + e.getMessage(), e);
        }

        return list;
    }

    public PageWrap<NftCollWrap> getNftCollPage(int pageNum) {
        String url = ecologyUrlService.buildGetNftCollPageByNodeCluster(pageNum);
        PageWrap<NftCollWrap> result = new PageWrap<NftCollWrap>();

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            if (StringUtils.isEmpty(json)) {
                return result;
            }

            ApiResult<PageWrap<NftCollection>> pageWrapApiResult = JsonUtil.om().readValue(json, new TypeReference<ApiResult<PageWrap<NftCollection>>>() {
            });
            return toNftCollWrapPage(pageWrapApiResult.getData());

        } catch (Exception e) {
            log.error("getNftCollPage error : " + e.getMessage(), e);
        }

        return result;
    }

    public PageWrap<NftCollWrap> getAccountNftCollPage(String walletAddress, int pageNum) {
        String url = ecologyUrlService.buildGetAccountNftCollPageByNodeCluster(walletAddress, pageNum);
        PageWrap<NftCollWrap> result = new PageWrap<NftCollWrap>();

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            if (StringUtils.isEmpty(json)) {
                return result;
            }

            ApiResult<PageWrap<NftCollection>> pageWrapApiResult = JsonUtil.om().readValue(json, new TypeReference<ApiResult<PageWrap<NftCollection>>>() {
            });
            return toNftCollWrapPage(pageWrapApiResult.getData());

        } catch (Exception e) {
            log.error("getAccountNftCollPage error : " + e.getMessage(), e);
        }

        return result;
    }

    public NftCollWrap getNftCollectionByNodeCluster(long  collInstanceId,long collTokenId) {
        String url = ecologyUrlService.buildGetNftCollectionByNodeCluster(collInstanceId,collTokenId);
        NftCollWrap nftCollWrap = null;

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            ObjectMapper objectMapper = JsonUtil.om();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            NftCollection nftCollection = objectMapper.treeToValue(data, NftCollection.class);
            nftCollWrap = toNftCollWrap(nftCollection);
        } catch (Exception e) {
            log.error("getNftCollectionByNodeCluster error : " + e.getMessage(), e);
        }

        return nftCollWrap;
    }

    public PageWrap<NftItemWrap> getAccountNftItemWrapPageByNodeCluster(NftCollWrap nftCollWrap, String walletAddress, long collTokenId, int page) {
        String url = ecologyUrlService.buildGetAccountNftItemPageByNodeCluster(walletAddress, collTokenId, page);

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            if (StringUtils.isEmpty(json)) {
                return new PageWrap<NftItemWrap>();
            }

            ApiResult<PageWrap<NftItem>> pageWrapApiResult = JsonUtil.om().readValue(json, new TypeReference<ApiResult<PageWrap<NftItem>>>() {
            });
            Boolean flag = pageWrapApiResult.getFlag();

            if (!flag) {
                return new PageWrap<NftItemWrap>();
            }

            PageWrap<NftItem> nftItemPageWrap = pageWrapApiResult.getData();
            List<NftItem> dataList = nftItemPageWrap.getDataList();

            List<NftItemWrap> list = Lists.newArrayList();
            if (dataList != null) {
                for (NftItem nftItem : dataList) {
                    NftItemWrap wrap = new NftItemWrap();
                    wrap.setNftItemId(nftItem.getNftItemId());
                    wrap.setNftItem(nftItem);
                    wrap.setNftItemDetail(toNftItemDetail(nftCollWrap, nftItem));
                    list.add(wrap);
                }
            }

            PageWrap<NftItemWrap> wrap = new PageWrap<NftItemWrap>();
            wrap.setDataList(list);
            wrap.setPageNumber(nftItemPageWrap.getPageNumber());
            wrap.setDataTotal(nftItemPageWrap.getDataTotal());
            wrap.setPageSize(nftItemPageWrap.getPageSize());
            wrap.setPageNumber(nftItemPageWrap.getPageNumber());
            wrap.setPageTotal(nftItemPageWrap.getPageTotal());

            return wrap;
        } catch (Exception e) {
            log.error("getAccountNftItemWrapPageByNodeCluster error : " + e.getMessage(), e);
        }

        return new PageWrap<NftItemWrap>();
    }

    public List<NftItemWrap> getAllAccountNftItemWrap(String walletAddress, NftCollWrap nftCollWrap) {

        if (nftCollWrap == null) {
            return Lists.newArrayList();
        }

        NftCollection nftCollection = nftCollWrap.getNftCollection();

        long token = nftCollection.getToken();

        String url = ecologyUrlService.buildGetAllAccountNftItemWrapByNodeCluster(walletAddress, token);

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            if (StringUtils.isEmpty(json)) {
                return Lists.newArrayList();
            }

            ObjectMapper objectMapper = JsonUtil.om();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            NftItemWrap[] wraps = objectMapper.treeToValue(data, NftItemWrap[].class);
            List<NftItemWrap> list = new ArrayList<>(wraps.length);

            for (NftItemWrap item : wraps) {
                item.setNftItemDetail(toNftItemDetail(nftCollWrap,item.getNftItem()));
            }

            Collections.addAll(list, wraps);
            return list;

        } catch (Exception e) {
            log.error("getAllAccountNftItemWrap error : " + e.getMessage(), e);
        }

        return Lists.newArrayList();
    }

    public NftItemDetail getNftItemDetailParseCache(long collInstanceId, long nftItemId, String detailUrl) {
        NftItemDetail nftItemDetail = null;

        try {

            NftItemDetailDAO dao = new NftItemDetailDAO(collInstanceId);
            nftItemDetail = dao.get(nftItemId);

            // check again
            if (nftItemDetail == null) {

                Connection.Response executeResult = HttpUtil.getNftResource(detailUrl).execute();
                String json = executeResult.body();

                nftItemDetail = JsonUtil.parseObjectByOm(json, NftItemDetail.class);

                //save cache
                dao.add(nftItemDetail);
            }

        } catch (Exception e) {
            log.error("getNftItemDetailParseCache error : " + e.getMessage() + ",detail url = " + detailUrl, e);
        }

        return nftItemDetail;
    }

    public NftItemDetail toNftItemDetail(NftCollWrap nftCollWrap, NftItem nftItem) {
        if (nftItem == null) {
            return new NftItemDetail();
        }
        if (nftCollWrap == null) {
            return new NftItemDetail();
        }

        NftCollection nftCollection = nftCollWrap.getNftCollection();

        long nftItemId = nftItem.getNftItemId();
        String baseURI = nftCollection.getNftProtocol().getBaseURI();
        long instance = nftCollection.getInstance();

        String nftUrl = String.format("%s%s.json", baseURI, nftItemId);
        NftItemDetail nftItemDetail = getNftItemDetailParseCache(instance, nftItemId, nftUrl);
        return nftItemDetail;
    }

    public NftItemWrap getNftItemWrap(long nftId, NftCollWrap nftCollWrap) {
        NftItemWrap nftItemWrap = new NftItemWrap();

        if (nftCollWrap == null) {
            return nftItemWrap;
        }

        NftCollection nftCollection = nftCollWrap.getNftCollection();
        long collInstanceId = nftCollection.getInstance();
        String url = ecologyUrlService.buildGetNftItemByNodeCluster(nftId, collInstanceId);

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            ObjectMapper objectMapper = JsonUtil.om();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            NftItem nftItem = objectMapper.treeToValue(data, NftItem.class);
            nftItemWrap.setNftItemId(nftItem.getNftItemId());
            nftItemWrap.setNftItem(nftItem);
            nftItemWrap.setNftItemDetail(toNftItemDetail(nftCollWrap,nftItem));
        } catch (Exception e) {
            log.error("getNftItemWrap error : " + e.getMessage(), e);
        }

        return nftItemWrap;
    }


    //
    public PageWrap<NftItemWrap> getNftItemWrapPage(NftCollWrap nftCollWrap, long collInstanceId, int page) {
        String url = ecologyUrlService.buildGetNftItemPageByNodeCluster(collInstanceId, page);

        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            if (StringUtils.isEmpty(json)) {
                return new PageWrap<NftItemWrap>();
            }

            ApiResult<PageWrap<NftItem>> pageWrapApiResult = JsonUtil.om().readValue(json, new TypeReference<ApiResult<PageWrap<NftItem>>>() {
            });
            Boolean flag = pageWrapApiResult.getFlag();

            if (!flag) {
                return new PageWrap<NftItemWrap>();
            }

            PageWrap<NftItem> nftItemPageWrap = pageWrapApiResult.getData();
            List<NftItem> dataList = nftItemPageWrap.getDataList();

            List<NftItemWrap> list = com.google.common.collect.Lists.newArrayList();
            if (dataList != null) {
                for (NftItem nftItem : dataList) {

                    NftItemWrap wrap = new NftItemWrap();
                    wrap.setNftItem(nftItem);
                    wrap.setNftItemId(nftItem.getNftItemId());
                    wrap.setNftItemDetail(toNftItemDetail(nftCollWrap,nftItem));

                    list.add(wrap);
                }
            }

            PageWrap<NftItemWrap> wrap = new PageWrap<NftItemWrap>();
            wrap.setDataList(list);
            wrap.setPageNumber(nftItemPageWrap.getPageNumber());
            wrap.setDataTotal(nftItemPageWrap.getDataTotal());
            wrap.setPageSize(nftItemPageWrap.getPageSize());
            wrap.setPageNumber(nftItemPageWrap.getPageNumber());
            wrap.setPageTotal(nftItemPageWrap.getPageTotal());

            return wrap;
        } catch (Exception e) {
            log.error("getNftItemWrapPage error : " + e.getMessage(), e);
        }

        return new PageWrap<NftItemWrap>();
    }



}
