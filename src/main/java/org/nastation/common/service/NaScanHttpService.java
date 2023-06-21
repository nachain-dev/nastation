package org.nastation.common.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.nastation.common.model.HttpResult;
import org.nastation.common.util.HttpUtil;
import org.nastation.common.util.JsonUtil;
import org.nastation.data.vo.MergeApiPackVo;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * NA Scan
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
@Component
@Slf4j
public class NaScanHttpService {

    @Autowired
    private EcologyUrlService ecologyUrlService;

    public long getLastBlockHeightRetry(long instanceId) {

        String url = ecologyUrlService.buildGetLastBlockHeightUrlByScan(instanceId);
        try {

            Connection conn = HttpUtil.get(url);

            Connection.Response response = HttpUtil.retryConn(conn, url, 60,3,null);

            String json = response.body();

            HttpResult result = JsonUtil.parseObjectByOm(json, HttpResult.class);
            Object data = result.getData();
            return Long.valueOf(data.toString());

        } catch (Exception e) {
            log.error("[ InstanceId = {} ] getLastBlockHeight error : " + e.getMessage(),instanceId, e);
        }

        return 0L;
    }


    public Long getLastBlockHeight(long instanceId) {

        String url = ecologyUrlService.buildGetLastBlockHeightUrlByScan(instanceId);
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();
            HttpResult result = JsonUtil.parseObjectByOm(json, HttpResult.class);
            Object data = result.getData();
            return Long.valueOf(data.toString());

        } catch (Exception e) {
            log.error("[ InstanceId = {} ] getLastBlockHeight error : " + e.getMessage(),instanceId, e);
        }

        return 0L;
    }

    public double getGasFee(long instanceId) {

        String url = ecologyUrlService.buildGetGasFeeUrlByScan(instanceId);
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();
            HttpResult result = JsonUtil.parseObjectByOm(json, HttpResult.class);
            Object data = result.getData();
            return Double.valueOf(data.toString());

        } catch (Exception e) {
            log.error("[ InstanceId = {} ] getGasFee error : " + e.getMessage(),instanceId, e);
        }

        return 0;
    }


    public MergeApiPackVo getMergeApiPackByScan() {

        String url = ecologyUrlService.buildGetMergeApiPackByScan();
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            ObjectMapper objectMapper = JsonUtil.om();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            if (data == null) {
                return null;
            }

            return objectMapper.treeToValue(data, MergeApiPackVo.class);

        } catch (Exception e) {
            log.error("getMergeApiPackByScan error : " + e.getMessage(), e);
        }

        return null;
    }

    public UsedTokenBalanceDetail getUsedTokenBalanceDetail(String address, long instanceId) {

        String url = ecologyUrlService.buildGetUsedTokenBalanceDetailByScan(address,instanceId);
        UsedTokenBalanceDetail usedTokenBalanceDetail = null;
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            String json = executeResult.body();

            ObjectMapper objectMapper = JsonUtil.om();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");
            usedTokenBalanceDetail =  objectMapper.treeToValue(data, UsedTokenBalanceDetail.class);

        } catch (Exception e) {
            log.error("getUsedTokenBalanceDetail error : " + e.getMessage(), e);
        }

        return usedTokenBalanceDetail;
    }


}