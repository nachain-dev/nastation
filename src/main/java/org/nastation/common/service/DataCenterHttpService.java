package org.nastation.common.service;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.common.util.CollUtil;
import org.nastation.common.util.HttpUtil;
import org.nastation.data.config.AppConfig;
import org.nastation.data.vo.DcBlockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Data Center
 *
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
@Component
@Slf4j
public class DataCenterHttpService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private EcologyUrlService ecologyUrlService;

    // height : only one
    public DcBlockVo getDcBlockVoRetry(long height, long instanceId) {
        String url = ecologyUrlService.buildGetBlockUrlByDC();

        DcBlockVo o = null;
        String json = "";

        try {
            Connection conn = HttpUtil.get(url)
                    .data("instanceId", String.valueOf(instanceId))
                    .data("height", String.valueOf(height));

            Map<Object, Object> params = ImmutableMap.<Object, Object> builder()
                    .put("instanceId", instanceId)
                    .put("height", height)
                    .build();

            Connection.Response response = HttpUtil.retryConn(conn, url, 60, 3, params);
            json = response.body();

            //log.info("RAW JSON = \n\r{}", json);

            //HttpResult result = JSON.parseObject(json.getBytes(), HttpResult.class);
            //Object data = result.getData();
            //if (data != null) {
            //    o = JSON.parseObject(data.toString(), DcBlockVo.class);
            //}

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = new ObjectMapper().readTree(json);
            Object data = root.get("data");
            boolean flag = root.get("flag").asBoolean();

            if (flag) {
                String dataText = data.toString();
                if (data != null && StringUtils.isNotEmpty(dataText)) {
                    o = objectMapper.readValue(dataText.getBytes(), DcBlockVo.class);
                }
            } else {
                log.error("getDcBlockVo error [flag = {}, height={}, instanceId={}], JSON = \n\r{}", flag, height, instanceId, json);
            }
        } catch (Exception e) {
            log.error("getDcBlockVo error [height={}, instanceId={}], JSON = \n\r{}", height, instanceId, json, e);
        }

        return o;
    }

    // height : from to
    public List<DcBlockVo> getDcBlockVoListRetry(long fromHeight,long toHeight, long instanceId, int size) {
        String url = ecologyUrlService.buildGetBlockListUrlByDC();

        List<DcBlockVo> list = Lists.newArrayList();
        String json = "";
        String urlFull = "";

        try {
            Connection conn = HttpUtil.get(url)
                    .data("instanceId", String.valueOf(instanceId))
                    .data("fromHeight", String.valueOf(fromHeight))
                    .data("toHeight", String.valueOf(toHeight));

            urlFull = String.format("%s?instanceId=%s&fromHeight=%s&toHeight=%s", url, instanceId, String.valueOf(fromHeight), String.valueOf(toHeight));

            Map<Object, Object> params = ImmutableMap.<Object, Object> builder()
                    .put("instanceId", String.valueOf(instanceId))
                    .put("fromHeight", String.valueOf(fromHeight))
                    .put("toHeight", String.valueOf(toHeight))
                    .build();

            Connection.Response response = HttpUtil.retryConn(conn, urlFull, 30, 10,params);

            //try again
            try {
                json = response.body();
            } catch (Exception e) {
                log.error("When response get body content error then retry connection again :", e);
                response = HttpUtil.retryConn(conn, urlFull, 30, 10,params);
                json = response.body();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = new ObjectMapper().readTree(json);
            Object data = root.get("data");
            boolean flag = root.get("flag").asBoolean();

            if (flag) {
                String dataText = data.toString();
                if (data != null && StringUtils.isNotEmpty(dataText)) {

                    JavaType t = objectMapper.getTypeFactory().constructParametricType(List.class, DcBlockVo.class);
                    list = objectMapper.readValue(dataText, t);

                    if (CollUtil.isNotEmpty(list) && instanceId== CoreInstanceEnum.NAC.id) {
                        for (DcBlockVo vo : list) {
                            List<String> txs = vo.getTxs();
                            String block = vo.getBlock();

                            if (CollUtil.isEmpty(txs)) {
                                log.error("The block has no txs [height={}, instanceId={}], block = {}", fromHeight, instanceId, block);
                            }
                        }
                    }

                    int listSize = list.size();
                    log.info("Request batch {} block data list success , url = {}", listSize, urlFull);
                }
            } else {
                log.error("getDcBlockVo error [flag = {}, height={}, instanceId={}], url = {}, JSON = \n\r{}", flag, fromHeight, instanceId, urlFull, json);
            }
        } catch (Exception e) {
            log.error("getDcBlockVo error [height={}, instanceId={}], url = {}, JSON = \n\r{}", fromHeight, instanceId, urlFull, json, e);
        }

        return list;
    }



}

/*
//log.info("RAW JSON = \n\r{}", json);
//HttpResult result = JSON.parseObject(json.getBytes(), HttpResult.class);
//Object data = result.getData();
//if (data != null) {
//    o = JSON.parseObject(data.toString(), DcBlockVo.class);
//}
*/