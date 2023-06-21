package org.nastation.module.protocol.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;
import org.nachain.core.chain.transaction.Tx;
import org.nachain.core.chain.transaction.TxStatus;
import org.nastation.common.util.*;
import org.nastation.data.entity.SystemConfig;
import org.nastation.data.rocksdb.v2.TxDAO;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.protocol.data.TxDataRow;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TxDataService {

    @Autowired
    private WalletDataService walletDataService;

    @Autowired
    private WalletService walletService;

    public List<TxDataRow> getLastDataList(long instance) {
        List<TxDataRow> list = Lists.newArrayList();

        SystemConfig currentTxListSystemConfig = walletDataService.getCurrentTxHashListSystemConfig(instance);
        if (currentTxListSystemConfig == null) {
            log.warn("Current tx hash list by system config object is null");
            return list;
        }

        String value = currentTxListSystemConfig.getValue();
        if (StringUtils.isBlank(value)) {
            log.warn("Current tx hash list by system config object is empty");
            return list;
        }

        try {
            TxDAO txDao = new TxDAO(instance);

            String[] split = value.split(",");
            for (String height_hash_key : split) {

                if (StringUtils.isEmpty(height_hash_key)) {
                    continue;
                }

                String[] pair = height_hash_key.split("#");
                String height = pair[0];
                String hashText = pair[1];
                String json = txDao.getJson(hashText);

                if (StringUtils.isEmpty(json)) {
                    continue;
                }

                Tx tx = JsonUtil.parseObjectByGson(json, Tx.class);
                list.add(toTxDataRow(tx));
            }
        } catch (Exception e) {
            log.error("Instance = {}, getLastDataList error:", instance, e);
        }

        Collections.reverse(list);

        return list;
    }

    public List<TxDataRow> getMineDataList(long instance) {
        List<TxDataRow> list = Lists.newArrayList();

        try {
            TxDAO dao = new TxDAO(instance);
            int size = 200;

            String defaultWalletAddress = walletService.getDefaultWalletAddress();

            if (StringUtils.isNotBlank(defaultWalletAddress) && WalletUtil.isAddressValid(defaultWalletAddress)) {
                List<Tx> jsonList = dao.geTxListByAddress(defaultWalletAddress, size);

                for (Tx tx : jsonList) {
                    list.add(toTxDataRow(tx));
                }
            }

        } catch (Exception e) {
            log.error("getMineDataList error:", e);
        }

        return list;
    }

    private TxDataRow toTxDataRow(Tx tx) {
        TxDataRow row = new TxDataRow();

        if (tx == null) {
            return null;
        }

        row.setHashText(tx.getHash());
        row.setTxHeightText(String.valueOf(tx.getTxHeight()));
        row.setDateTimeText(DateUtil.timestampToDateTimeText(tx.getTimestamp()));
        row.setFromText(tx.getFrom());
        row.setToText(tx.getTo());
        row.setAmountText(String.valueOf(MathUtil.round(NumberUtil.bigIntToNacDouble(tx.getValue()), 8)));
        row.setStatusText(TxStatus.getName(tx.getStatus()));

        BigDecimal fee = Amount.of(tx.getGas()).toDecimal(Unit.NAC);
        row.setFeeText(String.valueOf(fee.doubleValue()));

        row.setRawTx(tx);

        return row;
    }

    public TxDataRow getTxDataRow(long instance, String hash) {
        TxDataRow row = null;

        try {
            TxDAO txDAO = new TxDAO(instance);
            String json1 = txDAO.getJson(hash);

            if (StringUtils.isNotEmpty(json1)) {
                Tx tx = JsonUtil.parseObjectByGson(json1, Tx.class);
                row = toTxDataRow(tx);
            }
        } catch (Exception e) {
            log.error("getTxDataRow error:", e);
        }

        return row;
    }

}
