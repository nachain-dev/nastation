package org.nastation.data.rocksdb.v2;

import org.rocksdb.RocksDBException;

import java.io.IOException;

public class BlockDAO extends LocalRocksDAO {

    public static final String DB_NAME = "Block";

    public BlockDAO(long instance) throws RocksDBException, IOException {
        super(DB_NAME, instance);
    }

    public String getKey(long blockHeight) {
        return DB_NAME + "." + blockHeight;
    }

    /**
     * @param rawJson
     * @return
     * @throws RocksDBException
     */
    public boolean addJson(long blockHeight, String rawJson) throws RocksDBException {

        String key = getKey(blockHeight);

        String result = db.get(key);
        if (result != null) {
            return false;
        }

        db.put(key, rawJson);

        return true;
    }


    /**
     * @param blockHeight
     * @return
     * @throws RocksDBException
     */
    public String getJson(long blockHeight) throws RocksDBException {

        String key = getKey(blockHeight);

        String result = db.get(key);

        return result;
    }

    /**
     * @return
     * @throws RocksDBException
     */
    public long getLastHeight() throws RocksDBException {
        long height = 0;
        return height;
    }

}
