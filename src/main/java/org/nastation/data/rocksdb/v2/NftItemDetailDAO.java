package org.nastation.data.rocksdb.v2;

import org.nachain.core.token.nft.NftItemDetail;
import org.nachain.core.util.JsonUtils;
import org.rocksdb.RocksDBException;

public class NftItemDetailDAO extends LocalRocksDAO {

    public static final String DB_NAME = "NftItemDetail";

    public NftItemDetailDAO(long instance) throws Exception {
        super(DB_NAME, instance);
    }

    private String getKey(long nftItemId) {
        return DB_NAME + "." + nftItemId;
    }

    public boolean add(NftItemDetail nid) throws RocksDBException {

        if (nid == null) {
            return false;
        }

        long nftItemId = nid.getNftItemId();

        String key = getKey(nftItemId);
        String result = db.get(key);

        if (result != null) {
            return false;
        }

        db.put(key, JsonUtils.objectToJson(nid));

        return true;
    }

    public NftItemDetail get(long nftItemId) throws RocksDBException {

        String key = getKey(nftItemId);

        String result = db.get(key);
        if (result == null) {
            return null;
        }

        return JsonUtils.jsonToPojo(result, NftItemDetail.class);
    }


}
