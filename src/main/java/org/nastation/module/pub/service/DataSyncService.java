package org.nastation.module.pub.service;

import org.apache.commons.compress.utils.Lists;
import org.nachain.core.chain.structure.instance.Instance;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.util.InstanceUtil;
import org.nastation.common.util.MathUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.pub.data.ProcessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSyncService {


    @Autowired
    private WalletDataService walletDataService;

    @Autowired
    private NaScanHttpService naScanHttpService;

    public List<ProcessInfo> getProcessInfoList() {

        List<Instance> enableInstanceList = InstanceUtil.getEnableInstanceList();
        List<ProcessInfo> list = Lists.newArrayList();

        int index = 1;
        for (Instance inst : enableInstanceList) {
            long instId = inst.getId();

            Long currentHeight = walletDataService.getCurrentSyncBlockHeightFromCache(instId);
            Long lastBlockHeight = naScanHttpService.getLastBlockHeight(instId);

            double percentDouble = 0;
            if (lastBlockHeight != 0) {
                percentDouble = MathUtil.round(((double) currentHeight * 1.0) / (double) lastBlockHeight * 100,2);
            }

            if (percentDouble > 100) {
                percentDouble = 100;
            }

            ProcessInfo one = new ProcessInfo();
            one.setId(index++);
            one.setInstanceId(instId);
            one.setPercent(percentDouble + "%");
            one.setCurrentHeight(currentHeight);
            one.setLastBlockHeight(lastBlockHeight);

            list.add(one);
        }

        return list;
    }
}
