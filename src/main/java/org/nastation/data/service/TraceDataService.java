package org.nastation.data.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.common.model.HttpResult;
import org.nastation.common.util.HttpUtil;
import org.nastation.common.util.JsonUtil;
import org.nastation.data.config.AppConfig;
import org.nastation.data.vo.Trace;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class TraceDataService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletDataService walletDataService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * post a trace request
     *
     * @return
     */
    public HttpResult postTrace(String action) {

        String url = appConfig.getTraceUrl() + "/api/v1/trace";
        //String url = "http://localhost:8300/api/v1/trace";
        String uuid = systemConfigService.getUUID();

        if (StringUtils.isBlank(uuid)) {
            return HttpResult.me().asFalse().data(0);
        }

        try {
            long currentInstanceId = CoreInstanceEnum.NAC.id;
            long height = walletDataService.getCurrentSyncBlockHeightFromCache(currentInstanceId);

            String ip = getIp();
            if (StringUtils.isBlank(ip)) {
                return HttpResult.me().asFalse().data(0);
            }

            Trace one = new Trace();
            one.setProductId(1);
            one.setBlockHeight(height);
            one.setIp(getIp());
            one.setJson("");
            one.setAction(action);
            one.setUuid(uuid);

            String jsonText = JsonUtil.toJsonByGson(one);
            Connection.Response executeResult = HttpUtil.post(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .requestBody(jsonText).execute();


            String body = executeResult.body();
            return JsonUtil.parseObjectByOm(body, HttpResult.class);
        } catch (Exception e) {
            log.error("postTrace error : " + e.getMessage(), e);
        }

        return HttpResult.me().asFalse().data(0);
    }

    /**
     * @return
     */
    public String getIp() {
        String url = "https://api.myip.com/";
        try {
            Connection.Response executeResult = HttpUtil.get(url).execute();
            IpInfo parse = JsonUtil.parseObjectByOm(executeResult.body(), IpInfo.class);
            return parse.getIp();
        } catch (Exception e) {
            //log.error("get local ip error");
        }
        return "";
    }

    public static class IpInfo {

        private String ip;
        private String country;
        private String cc;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }
    }
}
