package org.nastation.module.protocol.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.block.Block;
import org.nachain.core.chain.block.BlockBody;
import org.nastation.common.util.DateUtil;
import org.nastation.common.util.JsonUtil;
import org.nastation.data.entity.SystemConfig;
import org.nastation.data.rocksdb.v2.BlockBodyDAO;
import org.nastation.data.rocksdb.v2.BlockDAO;
import org.nastation.data.service.WalletDataService;
import org.nastation.module.protocol.data.BlockDataBodyRow;
import org.nastation.module.protocol.data.BlockDataRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BlockDataService {

    @Autowired
    private WalletDataService walletDataService;

    public List<BlockDataRow> getLastDataRowList(long instance,int size) {
        List<BlockDataRow> list = Lists.newArrayList();

        SystemConfig currentBlockHeightSystemConfig = walletDataService.getCurrentBlockHeightSystemConfig(instance);
        if (currentBlockHeightSystemConfig == null) {
            log.warn("Current block height system config object is null");
            return list;
        }

        String value = currentBlockHeightSystemConfig.getValue();
        if (StringUtils.isBlank(value)) {
            log.warn("Current block height system config value is empty");
            return list;
        }

        Long height = Long.valueOf(value);

        try {
            BlockDAO blockDAO = new BlockDAO(instance);
            for (long i = height; i >= height-size; i--) {
                if (i == 0) {
                    break;
                }
                String json = blockDAO.getJson(i);

                if (StringUtils.isEmpty(json)) {
                    continue;
                }

                Block block = JsonUtil.parseObjectByGson(json, Block.class);
                list.add(toDataRow(block));
            }
        } catch (Exception e) {
            log.error("getLastDataRowList error:", e);
        }

        return list;
    }

    private BlockDataRow toDataRow(Block b) {
        BlockDataRow row = new BlockDataRow();

        if (b == null) {
            return row;
        }

        row.setRawBlock(b);
        row.setHashText(b.getHash());
        row.setMinerText(b.getMiner());
        row.setHeightText(String.valueOf(b.getHeight()));
        row.setTimeText(DateUtil.timestampToDateTimeText(b.getTimestamp()));
        return row;
    }

    public BlockDataRow getBlockDataRow(long instance,int height) {
        Block b = null;

        try {
            BlockDAO blockDAO = new BlockDAO(instance);
            String json1 = blockDAO.getJson(height);

            if (StringUtils.isNotEmpty(json1)) {
                b = JsonUtil.parseObjectByGson(json1, Block.class);
            }

        } catch (Exception e) {
            log.error("getBlockDataRow error:", e);
        }

        return toDataRow(b);
    }

    public BlockDataBodyRow getBlockDataBodyRow(long instance, long height) throws Exception {
        Block b = null;

        try {
            BlockDAO blockDAO = new BlockDAO(instance);
            String json1 = blockDAO.getJson(height);

            if (StringUtils.isNotEmpty(json1)) {
                b = JsonUtil.parseObjectByGson(json1, Block.class);
            }

        } catch (Exception e) {
            log.error("getBlockDataBodyRow error:", e);
        }

        return toBlockDataBodyRow(instance, b);
    }

    public BlockDataBodyRow toBlockDataBodyRow(long instId,Block b) throws Exception {
        BlockDataRow row = toDataRow(b);
        long height = b.getHeight();

        BlockBodyDAO dao = new BlockBodyDAO(instId);
        BlockBody blockBody = dao.get(height);

        BlockDataBodyRow bodyRow = new BlockDataBodyRow();
        bodyRow.setBlocDataRow(row);
        bodyRow.setTxList(blockBody.getTransactions());

        return bodyRow;
    }


}
