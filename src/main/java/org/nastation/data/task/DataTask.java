package org.nastation.data.task;

import lombok.extern.slf4j.Slf4j;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.AppVersionService;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.data.service.TraceDataService;
import org.nastation.data.service.WalletDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataTask {

    @Autowired
    private NaScanHttpService naScanHttpService;

    @Autowired
    private WalletDataService walletDataService;

    @Autowired
    private AppVersionService appVersionService;

    @Autowired
    private TraceDataService traceDataService;

    // TODO temp
    @Async
    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void requestBlockDataBy0() {
        walletDataService.requestBlockData();
    }

    //6 hours
    @Async
    @Scheduled(fixedRate = 6 * 60 * 1000)
    public void postTrace() {
        HttpResult result = traceDataService.postTrace("online");
        log.info("Http post running state info in timer task :" + result.getFlag());
    }

    // 10 sec
    @Async
    @Scheduled(fixedRate = 10 * 1000)
    public void getMergeApiPackByScan() {
        walletDataService.refreshMergeApiPackVo();
    }

    //10 sec
    @Async
    @Scheduled(fixedRate = 10 * 1000)
    public void saveCurrentBlockChainData() {
        walletDataService.saveCurrentBlockChainData();
    }

    //30 sec
    @Async
    @Scheduled(fixedRate = 30 * 1000)
    public void requestPrices() {
        walletDataService.requestCoinPrices();
    }

    //3 minute
    @Async
    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void requestAppVersionCheck() {
        appVersionService.requestVersionCheck();
    }


}