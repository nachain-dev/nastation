package org.nastation.data.rocksdb.v2;

import com.google.common.collect.Maps;
import org.nachain.core.persistence.IDB;
import org.nachain.core.util.FileUtils;
import org.nachain.core.util.JsonUtils;
import org.rocksdb.Checkpoint;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RocksDB implements IDB {
    private static final Logger logger = LoggerFactory.getLogger(RocksDB.class);
    private final int groupId;
    private org.rocksdb.RocksDB rocksDB;

    static {
        org.rocksdb.RocksDB.loadLibrary();
    }

    private String dbPath;

    public RocksDB(String kvPath) throws RocksDBException, IOException {
        this.groupId = 1;
        dbPath = kvPath + File.separator + groupId;
        init();
    }

    public RocksDB(int groupId, String kvPath) throws RocksDBException, IOException {
        this.groupId = groupId;
        dbPath = kvPath + File.separator + groupId;
        init();
    }

    public void init() throws RocksDBException, IOException {
        if (!Files.isSymbolicLink(Paths.get(dbPath))) {
            Files.createDirectories(Paths.get(dbPath));
        }
        Options options = new Options();
        options.setCreateIfMissing(true);

        rocksDB = org.rocksdb.RocksDB.open(options, dbPath);
    }

    /**
     * Getting data
     *
     * @param key
     * @return
     * @throws RocksDBException
     */
    public byte[] get(byte[] key) throws RocksDBException {
        long start = System.currentTimeMillis();
        byte[] retBuf = this.rocksDB.get(key);
        long cost = System.currentTimeMillis() - start;
        if (cost > 200) {
            logger.warn("rocksdb get cost {}", cost);
        }
        return retBuf;
    }

    /**
     * Getting data
     *
     * @param key
     * @return
     * @throws RocksDBException
     */
    public String get(String key) throws RocksDBException {
        byte[] bytes = get(key.getBytes());
        if (bytes != null) {
            return new String(bytes);
        }
        return null;
    }

    /**
     * save data
     *
     * @param key
     * @param value
     * @return
     * @throws RocksDBException
     */
    public boolean put(byte[] key, byte[] value) throws RocksDBException {
        long start = System.currentTimeMillis();
        this.rocksDB.put(key, value);
        long cost = System.currentTimeMillis() - start;
        if (cost > 200) {
            logger.warn("rocksdb put cost {}", cost);
        }
        return true;
    }

    /**
     * save data
     *
     * @param key
     * @param value
     * @return boolean The successful state
     */
    public boolean put(String key, String value) throws RocksDBException {
        return put(key.getBytes(), value.getBytes());
    }

    public boolean delete(byte[] key) throws RocksDBException {
        long start = System.currentTimeMillis();
        this.rocksDB.delete(key);
        long cost = System.currentTimeMillis() - start;
        if (cost > 200) {
            logger.warn("rocksdb delete cost {}", cost);
        }
        return true;
    }

    public boolean delete(String key) throws RocksDBException {
        return delete(key.getBytes());
    }

    /**
     * Read all the data
     *
     * @return
     */
    public List<byte[]> findAll() {
        List<byte[]> values = new ArrayList<>();
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                byte[] v = iterator.value();
                values.add(v);
            }
        }
        return values;
    }

    /**
     * Read all the data
     *
     * @return
     */
    public List<String> findAllString() {
        List<String> values = new ArrayList<>();
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                byte[] v = iterator.value();
                values.add(new String(v));
            }
        }
        return values;
    }

    /**
     * Read all the data by map
     *
     * @return
     */
    public Map<String, String> findAllMap() {
        Map<String, String> map = Maps.newTreeMap();

        try (final RocksIterator iterator = rocksDB.newIterator()) {
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                byte[] value = iterator.value();
                byte[] key = iterator.key();
                map.put(new String(key), new String(value));
            }
        }
        return map;
    }

    /**
     * Read all the data
     *
     * @return
     */
    public List findAll(Class clazz) {
        List values = new ArrayList<>();
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                byte[] v = iterator.value();
                values.add(JsonUtils.jsonToPojo(new String(v), clazz));
            }
        }
        return values;
    }

    /**
     *
     * @return
     */
    public int count() {
        int count = 0;
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                count++;
            }
        }
        return count;
    }

    public void close() {
        this.rocksDB.close();
        this.rocksDB = null;
        logger.info("rocksdb close groupId = {}", groupId);
    }

    /**
     * @param path
     * @throws RocksDBException
     */
    public void makeCheckPoint(String path) throws RocksDBException {
        Checkpoint checkpoint = Checkpoint.create(rocksDB);
        checkpoint.createCheckpoint(path);
    }

    /**
     * @param checkPointPath
     * @param fileList
     * @return
     * @throws RocksDBException
     */
    public boolean recoverCheckpoint(String checkPointPath, List<String> fileList) throws RocksDBException, IOException {
        if (rocksDB != null) {
            logger.info("rocksdb close");
            rocksDB.close();
            rocksDB = null;
        }
        File file = new File(dbPath);
        FileUtils.deleteDir(dbPath + ".bak");
        if (!file.renameTo(new File(dbPath + ".bak"))) {
            logger.error("rename file error");
            return false;
        }
        for (String s : fileList) {
            logger.info("checkpoint file {}", s);
        }
        File checkpoint = new File(checkPointPath);
        boolean b = checkpoint.renameTo(new File(dbPath));
        if (!b) {
            logger.error("rename check point error");
            return false;
        }
        init();
        return true;
    }

    public org.rocksdb.RocksDB getRocksDB() {
        return rocksDB;
    }

    public void setRocksDB(org.rocksdb.RocksDB rocksDB) {
        this.rocksDB = rocksDB;
    }
}