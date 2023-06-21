package org.nastation.data.rocksdb.v2;

import org.nachain.core.persistence.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LocalRocksDAO {

    public final static String DATA_PATH = "Nirvana-Data";

    public static final String CHAIN_DIR = "chaindata";


    private static final Map<String, RocksDB> holder = new ConcurrentHashMap<>();

    protected RocksDB db;

    protected String dbName;

    protected long groupID = 0;

    public LocalRocksDAO() {
    }

    /**
     * @throws RocksDBException
     * @throws IOException
     */
    public LocalRocksDAO(Class clazz, long groupID) throws RocksDBException, IOException {
        init(clazz.getSimpleName(), groupID);
    }

    /**
     * @throws RocksDBException
     * @throws IOException
     */
    public LocalRocksDAO(String dbName) throws RocksDBException, IOException {
        init(dbName, groupID);
    }


    /**
     * @param dbName
     * @param groupID
     * @throws RocksDBException
     * @throws IOException
     */
    public LocalRocksDAO(String dbName, long groupID) throws RocksDBException, IOException {
        init(dbName, groupID);
    }

    /**
     * @param dbName
     * @param groupID
     * @throws RocksDBException
     * @throws IOException
     */
    protected void init(String dbName, long groupID) throws RocksDBException, IOException {
        this.dbName = dbName;
        this.groupID = groupID;
        synchronized (holder) {
            db = holder.get(getHolderKey());
            if (db == null) {
                db = new RocksDB(dbName, groupID, DATA_PATH + File.separator + CHAIN_DIR + File.separator + dbName);
                holder.put(getHolderKey(), db);
            }
        }
    }

    /**
     * @return
     */
    public RocksDB db() {
        return this.db;
    }

    /**
     * @return
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return
     */
    public long getGroupID() {
        return groupID;
    }

    /**
     * @return
     */
    private String getHolderKey() {
        return dbName + "." + groupID;
    }


    public List<String> findAll() {
        return db.findAllString();
    }

    public Map<String, String> findAllMap() {
        return db.findAllMap();
    }

    public List findAll(Class clazz) {
        return db.findAll(clazz);
    }

    public long count() {
        return db.count();
    }

    public void close() {
        synchronized (holder) {
            if (db != null) {
                holder.remove(dbName);
                db.close();
            }
        }
    }
}
