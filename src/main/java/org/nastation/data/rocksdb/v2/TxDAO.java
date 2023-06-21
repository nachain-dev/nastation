package org.nastation.data.rocksdb.v2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.transaction.Tx;
import org.nachain.core.chain.transaction.TxType;
import org.nastation.common.util.JsonUtil;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.util.List;

@Slf4j
public class TxDAO extends LocalRocksDAO {

    public static final String DB_NAME = "Tx";

    private String getKey(String hash) {
        return DB_NAME + "." + hash;
    }

    public TxDAO(long instance) throws Exception {
        super("Tx", instance);
    }

    public boolean addJson(String hash, String rawJson) throws RocksDBException {

        if (StringUtils.isBlank(hash)) {
            return false;
        }

        String key = getKey(hash);

        String result = db.get(key);
        if (result != null) {
            return false;
        }

        db.put(key, rawJson);

        return true;
    }

    public String getJson(String hash) throws RocksDBException {
        String key = getKey(hash);
        String result = db.get(key);
        return result;
    }

    public List<Tx> geTxListByAddress(String address, int size) throws RocksDBException {
        List<Tx> list = Lists.newArrayList();
        RocksIterator iterator = null;

        try {
            iterator = this.db.getRocksDB().newIterator();

            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                String rawJson = new String(iterator.value());
                Tx txJsonBean = JsonUtil.parseObjectByGson(rawJson, Tx.class);

                int txType = txJsonBean.getTxType();
                if (txType == TxType.TRANSFER_CHANGE.value) {
                    continue;
                }

                String from = txJsonBean.getFrom();
                String to = txJsonBean.getTo();

                if (from.equals(address) || to.equals(address)) {
                    list.add(txJsonBean);
                    if (list.size() >= size) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("geTxListByAddress error:", e);
        } finally {
            if (iterator != null) {
                iterator.close();
            }
        }

        return list;
    }


}
