package org.nastation.data.rocksdb.v2;

import org.nachain.core.chain.block.BlockBody;
import org.nachain.core.util.JsonUtils;
import org.rocksdb.RocksDBException;

public class BlockBodyDAO extends LocalRocksDAO {

    public static final String DB_NAME = "BlockBody";

    public BlockBodyDAO(long instance) throws Exception {
        super(DB_NAME, instance);
    }

    public String getKey(long blockHeight) {
        return DB_NAME + "." + blockHeight;
    }

    public boolean add(BlockBody blockBody) throws RocksDBException {

        String key = getKey(blockBody.getHeight());
        String result = db.get(key);

        if (result != null) {
            return false;
        }

        db.put(key, JsonUtils.objectToJson(blockBody));

        return true;
    }

    public BlockBody get(long blockHeight) throws RocksDBException {

        String key = getKey(blockHeight);

        String result = db.get(key);
        if (result == null) {
            return null;
        }

        return JsonUtils.jsonToPojo(result, BlockBody.class);
    }


}
