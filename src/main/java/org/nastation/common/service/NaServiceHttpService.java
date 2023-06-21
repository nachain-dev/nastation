package org.nastation.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.nastation.common.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
@Component
@Slf4j
public class NaServiceHttpService {

    @Autowired
    private EcologyUrlService ecologyUrlService;

    public double getNacPrice() {
        return getCoinPrice("NAC");
    }

    public double getNOMCPrice() {
        return getCoinPrice("NOMC");
    }

    public Double getCoinPrice(String coin) {

        String url = ecologyUrlService.buildGetPriceUrlByService();

        String json = "";
        Double price = 0D;

        try {

            //HttpsUrlValidator.retrieveResponseFromServer(url);
            Connection.Response executeResult = HttpUtil.get(url).execute();

            json = executeResult.body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            for (int i = 0; i < data.size(); i++) {
                JsonNode item = data.get(i);

                String coinName = item.get("coinName").asText();
                Double coinPrice = item.get("coinPrice").asDouble();

                if (StringUtils.equals(coinName, coin)) {
                    price= coinPrice;
                    break;
                }
            }

        } catch (Exception e) {
            log.error("getCoinPrice error : ", e);
        }

        return price;
    }

}
