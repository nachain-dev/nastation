package org.nastation.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.nastation.common.model.AppNewVersion;
import org.nastation.common.util.HttpUtil;
import org.nastation.data.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
@Component
@Slf4j
public class AppVersionService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private EcologyUrlService ecologyUrlService;

    public void requestVersionCheck() {

        AppNewVersion appNewVersion = SystemService.me().getAppNewVersion();
        if (appNewVersion != null) {
            return;
        }

        String url = ecologyUrlService.buildVersionCheckUrlByWebsite();
        String projectVersionNumber = appConfig.getProjectVersionNumber();

        String json = "";
        try {
            // get json
            Connection.Response executeResult = HttpUtil.get(url).execute();
            json = executeResult.body();

                // parse to fetch coin price
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(json);
                JsonNode data = root.get("data");

                appNewVersion = objectMapper.treeToValue(data, AppNewVersion.class);

            if (appNewVersion != null && appNewVersion.getVersionNumber() > Integer.valueOf(projectVersionNumber)) {
                log.info("Found a new version:" + appNewVersion.toString());
                SystemService.me().setAppNewVersion(appNewVersion);
            }

        } catch (Exception e) {
            log.error("request version check error : ", e);
        }
    }


}
