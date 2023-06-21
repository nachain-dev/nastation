package org.nastation.data.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.block.Block;
import org.nachain.core.chain.block.BlockBody;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.chain.transaction.Tx;
import org.nachain.core.chain.transaction.TxType;
import org.nastation.common.model.AppNewVersion;
import org.nastation.common.service.*;
import org.nastation.common.util.*;
import org.nastation.data.config.AppConfig;
import org.nastation.data.entity.AccountInfo;
import org.nastation.data.entity.SystemConfig;
import org.nastation.data.repo.AccountInfoRepository;
import org.nastation.data.repo.SystemConfigRepository;
import org.nastation.data.rocksdb.v2.BlockBodyDAO;
import org.nastation.data.rocksdb.v2.BlockDAO;
import org.nastation.data.rocksdb.v2.TxDAO;
import org.nastation.data.vo.DcBlockVo;
import org.nastation.data.vo.MergeApiPackVo;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@Data
public class WalletDataService {

    public static final String CURRENT_BLOCK_HEIGHT = "currentBlockHeight_v2";

    public static final String CURRENT_TX_HASH_LIST = "currentTxHashList_v2";

    public int syncBlockSize = 3000;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private NodeClusterHttpService nodeClusterHttpService;

    @Autowired
    private NaServiceHttpService naServiceHttpService;

    @Autowired
    private NaScanHttpService naScanHttpService;

    @Autowired
    private DataCenterHttpService dataCenterHttpService;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    //@Autowired
    //private SimpMessagingTemplate messagingTemplate;

    private Map<Long, Set<String>> instanceAccountMap;

    private Map<String, Double> priceMap = Maps.newHashMap();

    private volatile Map<Long, Long> currentSyncBlockHeightMap = Maps.newHashMap();

    //private volatile Map<Long, Queue<String>> currentTxHashListMap = Maps.newHashMap();

    public static MergeApiPackVo MERGE_API_PACK_VO = null;

    @PostConstruct
    public void init() {
        //buildInstanceAccountMap();
        refreshMergeApiPackVo();
        buildSyncBlockHeightData();
    }


    public long getCurrentSyncBlockHeightFromCache(long instId) {
        Long lastHeight = currentSyncBlockHeightMap.get(instId);//sync

        //log.warn("getCurrentSyncBlockHeight() >> Instance id = {} , heightLoop = {} , currentSyncBlockHeightMap = {}",instId, lastHeight,currentSyncBlockHeightMap.toString());

        return lastHeight==null?0:lastHeight.longValue();
    }

    private void buildSyncBlockHeightData() {

        List<Instance> enableInstanceList = InstanceUtil.getEnableInstanceList();
        for (Instance inst : enableInstanceList) {
            long instId = inst.getId();

            //db
            SystemConfig conf = getCurrentBlockHeightSystemConfig(instId);
            if (conf != null) {
                currentSyncBlockHeightMap.put(instId, Long.valueOf(conf.getValue()));
            } else {
                currentSyncBlockHeightMap.put(instId, 0L);
                systemConfigRepository.save(new SystemConfig(instId, CURRENT_BLOCK_HEIGHT, String.valueOf(0)));
            }
        }

    }

    public boolean resetCurrentSyncBlockHeight(long instId) {

        try {
            BlockDAO blockDAO = new BlockDAO(instId);
            BlockBodyDAO blockBodyDAO = new BlockBodyDAO(instId);

            SystemConfig conf = getCurrentBlockHeightSystemConfig(instId);
            if (conf == null) {
                return false;
            }

            Long currentSyncBlockHeight = Long.valueOf(conf.getValue());

            for (int i = 1; i <= currentSyncBlockHeight; i++) {
                blockDAO.db().delete(blockDAO.getKey(i));
                blockBodyDAO.db().delete(blockBodyDAO.getKey(i));
            }

            currentSyncBlockHeightMap.put(instId, 0L);

            systemConfigRepository.updateValue(String.valueOf(0),instId, CURRENT_BLOCK_HEIGHT);

        } catch (Exception e) {
            log.error("resetCurrentSyncBlockHeight error", e);
            return false;
        }

        return true;
    }

    public SystemConfig getCurrentBlockHeightSystemConfig(long instanceId) {
        return systemConfigRepository.findByInstanceIdAndName(instanceId, CURRENT_BLOCK_HEIGHT);
    }

    public SystemConfig getCurrentTxHashListSystemConfig(long instanceId) {
        return systemConfigRepository.findByInstanceIdAndName(instanceId, CURRENT_TX_HASH_LIST);
    }

    /*private void buildInstanceAccountMap() {
        instanceAccountMap = new HashMap<>();

        List<Instance> instList = InstanceUtil.getEnableInstanceList();
        for (Instance inst : instList) {
            Set<String> set = Sets.newHashSet();
            instanceAccountMap.put(inst.getId(), set);
        }

        int pageNum = 1;
        while (true) {
            Page<AccountInfo> page = accountInfoRepository.findAll(PageRequest.of(pageNum - 1, 100, Sort.Direction.ASC, "id"));
            if (page == null) {
                break;
            }

            List<AccountInfo> list = page.getContent();
            if (list == null || list.size() == 0) {
                break;
            }

            //Map(instanceId,set)

            for (AccountInfo one : list) {
                String address = one.getAddress();
                Long instanceId = one.getInstanceId();

                boolean containsKey = instanceAccountMap.containsKey(instanceId);

                if (containsKey) {
                    Set<String> set = instanceAccountMap.get(instanceId);
                    set.add(address);
                } else {
                    Set<String> set = Sets.newHashSet();
                    set.add(address);
                    instanceAccountMap.put(instanceId, set);
                }

            }

            pageNum++;
        }

    }*/


    //TODO
    public long getNacLastBlockHeightByRequest() {
        return naScanHttpService.getLastBlockHeight(CoreInstanceEnum.NAC.id);
    }

    public List<Instance> getOrderEnableInstanceList() {
        List<Instance> list = Lists.newArrayList();
        List<Instance> instanceEnumList = InstanceUtil.getEnableInstanceList();
        for (Instance one : instanceEnumList) {
            long instanceId = one.getId();

            if (instanceId <= CoreInstanceEnum.APPCHAIN.id) {
                continue;
            }

            list.add(one);
        }

        list.add(InstanceUtil.convert(CoreInstanceEnum.APPCHAIN));

        return list;
    }

    public void refreshMergeApiPackVo() {
        MergeApiPackVo result = naScanHttpService.getMergeApiPackByScan();
        if (result != null) {
            WalletDataService.MERGE_API_PACK_VO = result;
        }else {
            log.error("TASK: when get the merge api data pack by nascan but result is empty [ MERGE_API_PACK_VO == null ]");
        }
    }

    public void requestBlockData() {

        //refresh block height
        buildSyncBlockHeightData();

        while (true) {

            try {

                //if stop
                if (SystemService.me().isStopRun()) {
                    break;
                }

                AppNewVersion appNewVersion = SystemService.me().getAppNewVersion();
                if (appNewVersion != null && appNewVersion.getForceUpdate() == 1) {
                    break;
                }

                SystemService.me().setRequestBlockDataOver(false);

                List<Instance> instanceEnumList = getOrderEnableInstanceList();

                //boolean isNacSyncOver = false;

                for (Instance one : instanceEnumList) {
                    long instanceId = one.getId();

                    if (instanceId < 0) {
                        continue;
                    }

                    //if (!isNacSyncOver) {
                    //    if (instanceId != CoreInstanceEnum.NAC.id) {
                    //        continue;
                    //    }
                    //}

                    //if stop
                    if (SystemService.me().isStopRun()) {
                        break;
                    }

                    long lastBlockHeight = naScanHttpService.getLastBlockHeightRetry(instanceId);
                    log.info("Instance id = {} :  request last block height = {}", instanceId, lastBlockHeight);

                    if (lastBlockHeight == 0) {
                        log.warn("Instance id = {} :  request last block height error , change instance to sync", instanceId);
                        continue;
                    }

                    BlockDAO blockDAO = new BlockDAO(instanceId);
                    long lastHeight = getCurrentSyncBlockHeightFromCache(instanceId);
                    Long fromHeight = (lastHeight ==0 ) ? 1 : lastHeight + 1;

                    if (fromHeight > lastBlockHeight) {
                        continue;
                    }

                    // nac block sync gap status is ok then chang flag
                    //if (instanceId == CoreInstanceEnum.NAC.id && lastBlockHeight - fromHeight < getSyncBlockSize()) {
                    //    isNacSyncOver = true;
                    //}

                    log.info("Instance id = {} : from height = {}, lastBlockHeight = {}", instanceId, fromHeight, lastBlockHeight);

                    for (; fromHeight <= lastBlockHeight; ) {

                        try {
                            //if stop
                            if (SystemService.me().isStopRun()) {
                                break;
                            }

                            log.info("Instance id = {} : currentBlockHeight = {} , lastBlockHeight = {}", instanceId, fromHeight, lastBlockHeight);

                            long toHeight = Math.min(fromHeight + getSyncBlockSize(), lastBlockHeight);
                            List<DcBlockVo> ncBlockVoList = dataCenterHttpService.getDcBlockVoListRetry(fromHeight, toHeight, instanceId, getSyncBlockSize());

                            // if get error then break;
                            if (CollUtil.isEmpty(ncBlockVoList)) {
                                break;
                            }

                            for (DcBlockVo ncBlockVo : ncBlockVoList) {

                                //if stop
                                if (SystemService.me().isStopRun()) {
                                    break;
                                }

                                // keep seq
                                if (ncBlockVo == null) {
                                    log.error("Instance id = {} :  from height = {} , ncBlockVo = null", instanceId, fromHeight);
                                    continue;
                                }

                                String blockRawJson = ncBlockVo.getBlock();
                                List<String> txRawJsonList = ncBlockVo.getTxs();

                                // keep seq
                                if (StringUtils.isEmpty(blockRawJson)) {
                                    log.error("Instance id = {} :  from height = {}  , lastBlockHeight = {} , blockRawJson = null", instanceId, fromHeight, lastBlockHeight);
                                    ThreadUtil.sleepSeconds(1);
                                    continue;
                                }

                                Block blockJsonBean = JsonUtil.parseObjectByGson(blockRawJson, Block.class);
                                long heightLoop = blockJsonBean.getHeight();

                                // if exists then skip
                                boolean addBlockFlag = blockDAO.addJson(heightLoop, blockRawJson);
                                if (!addBlockFlag) {
                                    this.currentSyncBlockHeightMap.put(instanceId, heightLoop);
                                    log.warn("Instance id = {} :  heightLoop = {}  , lastBlockHeight = {} , addBlockFlag = skip", instanceId, heightLoop, lastBlockHeight);
                                    continue;
                                }

                                //EventBusCenter.me().post(new BlockHeightChangeEvent(instanceId, heightLoop));

                                TxDAO txDAO = new TxDAO(instanceId);

                                List<String> hashList = Lists.newArrayList();

                                if (CollUtil.isNotEmpty(txRawJsonList)) {

                                    try{

                                        for (String txRaw : txRawJsonList) {

                                            Tx txJsonBean = JsonUtil.parseObjectByGson(txRaw, Tx.class);

                                            String hash = txJsonBean.getHash();
                                            String from = txJsonBean.getFrom();
                                            String to = txJsonBean.getTo();
                                            long token = txJsonBean.getToken();

                                            BigInteger gas = txJsonBean.getGas();
                                            BigInteger value = txJsonBean.getValue();

                                            double coinAmount = NumberUtil.bigIntToNacDouble(value);

                                            //continue
                                            int txType = txJsonBean.getTxType();
                                            if (txType == TxType.TRANSFER_CHANGE.value) {
                                                continue;
                                            }

                                            // skip keyword
                                            if (WalletUtil.isAddressHasKeyword(from) && WalletUtil.isAddressHasKeyword(to)) {
                                                continue;
                                            }

                                            //SAVE TX!!!
                                            boolean status = txDAO.addJson(hash, txRaw);

                                            hashList.add(hash);

                                            //if (status) {

                                                //refresh tx list
                                                //Queue<String> queue = this.currentTxHashListMap.get(instanceId);
                                                //if (queue != null) {
                                                //    queue.add(buildHeightHashJoinKey(heightLoop, hash));
                                                //}

                                                //log.warn("Instance id = {} , currentTxHashListMap = {}",instanceId, this.currentTxHashListMap);
                                                //try {
                                                //    toggleAddressBalance(instanceId, from, token, value, gas, false);
                                                //    toggleAddressBalance(instanceId, to, token, value, gas, true);
                                                //
                                                //    //websocket push
                                                //    messagingTemplate.convertAndSend("/topic/tx/" + instanceId, txRaw);
                                                //
                                                //} catch (Exception e) {
                                                //    log.error("toggleAddressBalance error: instance id = {} , heightLoop = {} , token = {} , coinAmount = {}", instanceId, heightLoop, token, coinAmount, e);
                                                //}
                                            //}
                                        }


                                    }catch (Exception e){
                                        log.error("for loop TxRawJson list error ,instance id = {} ,height = {} ,txRawJsonList = {}",instanceId, heightLoop,txRawJsonList ,e);
                                    }
                                }

                                else {
                                   log.error("[Tx hash list of block is empty] Instance id = {} :  height = {} .Current tx list of this block height will skip to parse", instanceId, heightLoop);
                                }

                                //refresh height
                                this.currentSyncBlockHeightMap.put(instanceId, heightLoop);
                                //log.warn("currentSyncBlockHeightMap >> Instance id = {} , heightLoop = {}",instanceId, heightLoop);

                                addBlockBodyJson(instanceId, heightLoop, hashList);

                            }

                            // next batch
                            fromHeight = fromHeight + ncBlockVoList.size();

                            systemConfigRepository.updateValue(String.valueOf(fromHeight),instanceId, CURRENT_BLOCK_HEIGHT);

                        } catch (Exception e) {

                            log.error("Each batch block loop error: instance id = {} , from height = {} , to height = {}, change instance to sync", instanceId, fromHeight, fromHeight + getSyncBlockSize(), e);
                        }

                    }

                    ThreadUtil.sleepSeconds(3);
                }

            } catch (Exception e) {
                log.error("Request block data error in timed loop", e);
            }
        }

        SystemService.me().setRequestBlockDataOver(true);
        log.warn("Stop requesting block data... now the server can be terminated normally...");
    }

    private boolean addBlockBodyJson(long instanceId, long height, List<String> hashList) throws Exception {
        BlockBodyDAO blockDAO = new BlockBodyDAO(instanceId);
        BlockBody body = new BlockBody(height);
        body.setHeight(height);
        body.setTransactions(hashList);
        return blockDAO.add(body);
    }

    public String buildHeightHashJoinKey(long h, String hash) {
        return h + "#" + hash;
    }

    private void toggleAddressBalance(long instanceId, String address, long token, BigInteger value, BigInteger gas, boolean isAdd) {

        Set<String> set = instanceAccountMap.get(instanceId);

        if (set == null) {
            instanceAccountMap.put(instanceId, Sets.newHashSet());
            set = instanceAccountMap.get(instanceId);
        }

        //check if has address
        if (!set.contains(address)) {

            //add
            AccountInfo ai = new AccountInfo();
            ai.setAddress(address);
            ai.setBalance(0D);
            ai.setTokenId(token);
            ai.setInstanceId(instanceId);
            accountInfoRepository.save(ai);

            //cache
            set.add(address);
        }

        /*
        BigInteger newValue = new BigInteger(value.toString());

        //remove
        if (!isAdd) {
            newValue = newValue.add(gas);
        }

        double newAmount = NumberUtil.bigIntToNacDouble(newValue);

        if (isAdd) {
            accountInfoRepository.addBalance(newAmount, instanceId, token, address);
        } else {
            accountInfoRepository.removeBalance(newAmount, instanceId, token, address);
        }
         */
    }

    public void saveCurrentBlockChainData() {
        try {

            //log.info("saveCurrentBlockChainData start");

            List<Instance> list = InstanceUtil.getEnableInstanceList();

            for (Instance inst : list) {
                long instId = inst.getId();

                //log.info("saveCurrentBlockChainData >>"+instId);

                //db
                SystemConfig currentBlockHeight_db = getCurrentBlockHeightSystemConfig(instId);

                //cache
                Long currentBlockHeight_cache = getCurrentSyncBlockHeightFromCache(instId);
                //log.warn("saveCurrentBlockChainData() Instance id = {} , currentBlockHeight = {} , currentBlockHeightConf.getValue() = {}",instId,currentBlockHeight,currentBlockHeightConf==null?"null":currentBlockHeightConf.getValue());

                if (currentBlockHeight_db != null && !StringUtils.equals(currentBlockHeight_db.getValue(), String.valueOf(currentBlockHeight_cache))) {
                    currentBlockHeight_db.setValue(String.valueOf(currentBlockHeight_cache));
                    //log.info("saveCurrentBlockChainData >> Instance = " + instId + " save newest block height = " + currentBlockHeight);
                    systemConfigRepository.save(currentBlockHeight_db);
                }

                //SystemConfig currentTxListConf = getCurrentTxHashListSystemConfig(instId);
                //Queue<String> currentTxHashList = currentTxHashListMap.get(instId);
                //
                //String joinTxHash = "";
                //if (currentTxHashList != null) {
                //    List<String> joinList = new ArrayList<>(currentTxHashList);
                //    joinTxHash = StringUtils.join(joinList, ",");
                //}
                //
                ////log.warn("saveCurrentBlockChainData() Instance id = {} , joinTxHash = {} , currentTxListConf.getValue() = {}",instId,joinTxHash,currentTxListConf==null?"null":currentTxListConf.getValue());
                //
                //if (currentTxListConf != null && !StringUtils.equals(currentTxListConf.getValue(), joinTxHash)) {
                //    currentTxListConf.setValue(joinTxHash);
                //    systemConfigRepository.save(currentTxListConf);
                //}

            }
        } catch (Exception e) {
            log.error("saveCurrentBlockChainData error:", e);
        }
    }

    public void requestCoinPrices() {
        Double nacPrice = naServiceHttpService.getNacPrice();
        Double nomcPrice = naServiceHttpService.getNOMCPrice();
        priceMap.put("NAC", nacPrice);
        priceMap.put("NOMC", nomcPrice);
    }

    public void setInstanceAccountAddress(long instanceId, String walletAddress) {

        Map<Long, Set<String>> _instanceAccountMap = getInstanceAccountMap();

        if (_instanceAccountMap == null) {
            return;
        }

        Set<String> set = _instanceAccountMap.get(instanceId);
        if (set != null) {
            set.add(walletAddress);
        }

    }

    public boolean isTokenInInstanceEnough(
            String address,
            long instanceId,
            long tokenId,
            double checkValue
    ) {

        boolean flag = false;
        UsedTokenBalanceDetail usedTokenBalanceDetail = this.naScanHttpService.getUsedTokenBalanceDetail(address, instanceId);

        if (usedTokenBalanceDetail != null &&usedTokenBalanceDetail.getTokenBalanceMap()!=null) {
            Set<Map.Entry<Long, BigInteger>> tokenBalanceMapEntrySet = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();

            for (Map.Entry<Long, BigInteger> entry : tokenBalanceMapEntrySet) {
                Long _tokenId = entry.getKey();
                BigInteger value = entry.getValue();

                //check wallet address in appchain has enough nac
                if (_tokenId.longValue() == tokenId) {
                    double nacDouble = NumberUtil.bigIntToNacDouble(value);
                    if (nacDouble > checkValue) {
                        flag = true;
                    }

                }
            }
        }

        return flag;
    }

}
