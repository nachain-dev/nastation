package org.nastation.data.rocksdb.v2;

import org.nachain.core.token.nft.collection.NftCollectionDetail;
import org.nachain.core.util.JsonUtils;
import org.rocksdb.RocksDBException;

public class NftCollectionDetailDAO extends LocalRocksDAO {

    public static final String DB_NAME = "NftCollectionDetail";

    public NftCollectionDetailDAO(long instance) throws Exception {
        super(DB_NAME, instance);
    }

    private String getKey(long collTokenId) {
        return DB_NAME + "." + collTokenId;
    }

    public boolean add(long collTokenId,NftCollectionDetail nftCollectionDetail) throws RocksDBException {

        String key = getKey(collTokenId);
        String result = db.get(key);

        if (result != null) {
            return false;
        }

        db.put(key, JsonUtils.objectToJson(nftCollectionDetail));

        return true;
    }

    public boolean add(long collTokenId,String json) throws RocksDBException {

        String key = getKey(collTokenId);
        String result = db.get(key);

        if (result != null) {
            return false;
        }

        db.put(key, json);

        return true;
    }

    public NftCollectionDetail get(long collTokenId) throws RocksDBException {

        String key = getKey(collTokenId);

        String result = db.get(key);
        if (result == null) {
            return null;
        }

        return JsonUtils.jsonToPojo(result, NftCollectionDetail.class);
    }


}
