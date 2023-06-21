package org.nastation.module.wallet.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.chain.transaction.*;
import org.nachain.core.chain.transaction.context.TxContextService;
import org.nachain.core.crypto.Key;
import org.nachain.core.crypto.bip39.Language;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailService;
import org.nachain.core.mailbox.MailType;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.Token;
import org.nachain.core.token.TokenService;
import org.nachain.core.token.nft.NftContentTypeEnum;
import org.nachain.core.token.nft.collection.NftCollection;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.protocol.NFTProtocol;
import org.nachain.core.token.protocol.NFTProtocolService;
import org.nachain.core.util.RegexpUtils;
import org.nachain.core.wallet.WalletUtils;
import org.nachain.core.wallet.keystore.Keystore;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.*;
import org.nastation.components.Images;
import org.nastation.data.repo.AccountInfoRepository;
import org.nastation.data.rocksdb.v2.TxDAO;
import org.nastation.data.service.AccountInfoService;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.wallet.data.KeystoreWrapper;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.data.WalletRow;
import org.nastation.module.wallet.data.WalletTxSentWrapper;
import org.nastation.module.wallet.repo.WalletRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class WalletService {

    public static final String KEY_CURRENT_WALLET = "KEY_CURRENT_WALLET";
    public static final String KEY_CURRENT_INSTANCE = "KEY_CURRENT_INSTANCE";
    public static final String KEY_SENT_TX_HASH = "KEY_SENT_TX_HASH";
    public static final String KEY_BACKUP_WALLET = "KEY_BACKUP_WALLET";

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletDataService walletDataService;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private NodeClusterHttpService nodeClusterHttpService;

    @Autowired
    private NaScanHttpService naScanHttpService;

    @Autowired
    private AccountInfoService accountInfoService;


    /***********************************************
     * DefaultWallet
     /***********************************************/

    public String getDefaultWalletBalanceText(long instId) {
        Wallet defaultWallet = getDefaultWallet();

        if (defaultWallet == null) {
            return "NAC:0";/* , NOMC:0 , USDN:0*/
        }

        WalletRow walletRow = toWalletRow(defaultWallet, instId);

        return String.format("NAC:%s",/* , NOMC:%s , USDN:%s*/
                StringUtils.defaultIfBlank(walletRow.getNacBalance(), "0"));
    }

    public String getDefaultWalletBalanceText_excludeUsdn(long instId) {
        Wallet defaultWallet = getDefaultWallet();

        if (defaultWallet == null) {
            return "NAC:0 , NOMC:0";
        }

        WalletRow walletRow = toWalletRow(defaultWallet, instId);

        return String.format("NAC:%s , NOMC:%s",
                StringUtils.defaultIfBlank(walletRow.getNacBalance(), "0"),
                StringUtils.defaultIfBlank(walletRow.getNomcBalance(), "0"),
                StringUtils.defaultIfBlank(walletRow.getUsdnBalance(), "0"));
    }

    public String getDefaultWalletBalanceText_onlyNac(long instId) {
        Wallet defaultWallet = getDefaultWallet();

        if (defaultWallet == null) {
            return "NAC:0";
        }

        WalletRow walletRow = toWalletRow(defaultWallet, instId);

        return String.format("NAC:%s",
                StringUtils.defaultIfBlank(walletRow.getNacBalance(), "0"));
    }

    public String getDefaultWalletAddress() {
        return StringUtils.defaultIfBlank(getDefaultWallet().getAddress(), "");
    }

    public String getDefaultWalletNameAndAddress() {

        Wallet defaultWallet = getDefaultWallet();

        String name = defaultWallet.getName();
        String address = defaultWallet.getAddress();

        // if not have address then empty
        if (StringUtils.isEmpty(address)) {
            return "";
        }

        return name + " ( " + address + " ) ";
    }

    public Wallet getDefaultWallet() {

        Wallet defaultWallet = walletRepository.findTopByDefaultWalletOrderByIdDesc(true);

        if (defaultWallet == null) {
            defaultWallet = walletRepository.findTopByOrderByIdDesc();
        }

        if (defaultWallet == null) {
            defaultWallet = new Wallet();
        }

        return defaultWallet;
    }


    /***********************************************
     * CurrentInstance
     /***********************************************/

    public void setCurrentInstance(long instanceId) {
        Instance ins = InstanceUtil.getInstance(instanceId);
        if (ins != null) {
            UI.getCurrent().getSession().setAttribute(KEY_CURRENT_INSTANCE, ins);
        }
    }

    public Instance getCurrentInstance() {
        Instance inst = (Instance) UI.getCurrent().getSession().getAttribute(KEY_CURRENT_INSTANCE);
        if (inst == null) {
            long id = CoreInstanceEnum.NAC.id;
            setCurrentInstance(id);
            inst = InstanceUtil.getInstance(id);
        }
        return inst;
    }

    public long getCurrentInstanceId() {
        Instance currentInstance = getCurrentInstance();
        return currentInstance.getId();
    }


    /***********************************************
     * CurrentWallet
     /***********************************************/

    public void setCurrentWallet(Wallet wallet) {
        UI.getCurrent().getSession().setAttribute(KEY_CURRENT_WALLET, wallet);
    }

    public Wallet getCurrentWallet() {
        Wallet wallet = (Wallet) UI.getCurrent().getSession().getAttribute(KEY_CURRENT_WALLET);
        wallet = (wallet == null ? new Wallet() : wallet);

        return wallet;
    }

    public void setCurrentWalletTxSentWrapper(WalletTxSentWrapper wrapper) {
        UI.getCurrent().getSession().setAttribute(KEY_SENT_TX_HASH, wrapper);
    }

    public WalletTxSentWrapper getCurrentWalletTxSentWrapper() {
        return (WalletTxSentWrapper) UI.getCurrent().getSession().getAttribute(KEY_SENT_TX_HASH);
    }

    public void setCurrentWalletById(int id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isPresent()) {
            setCurrentWallet(wallet.get());
        }
    }

    public void clearCurrentWallet() {
        UI.getCurrent().getSession().setAttribute(KEY_CURRENT_WALLET, null);
    }


    public Wallet genAndSaveToCurrentWallet() {
        Wallet currentWallet = getCurrentWallet();
        String password = currentWallet.getPassword();
        String salt = currentWallet.getSalt();

        if (StringUtils.isBlank(currentWallet.getPassword())) {
            throw new RuntimeException("The password of the current wallet can not be empty");
        }

        try {
            Language eng = Language.ENGLISH;
            String words = WalletUtils.generateWords(eng);
            //System.out.println("#genAndSaveToCurrentWallet()# = " + words);

            //maybe salt is empty
            Keystore keystore = WalletUtils.generate(eng, words, StringUtils.isBlank(salt) ? null : salt, 0);

            String walletAddress = keystore.getWalletAddress();
            currentWallet.setAddress(walletAddress);

            String mnemonicEncrypt = AESUtil.encrypt(words, password);
            currentWallet.setMnemonicEncrypt(mnemonicEncrypt);

            //if salt not empty then encrypt
            if (StringUtils.isNotBlank(salt)) {

                String saltEncrypt = AESUtil.encrypt(salt, password);
                currentWallet.setSaltEncrypt(saltEncrypt);
            }

            currentWallet.setMnemonic(words);

            // save again
            setCurrentWallet(currentWallet);

        } catch (Exception exception) {
            log.error("Gen words and save to the current wallet error:", exception);
            throw new RuntimeException(exception);
        }

        return currentWallet;
    }


    public Wallet importAndSaveToCurrentWallet(Wallet wallet) {
        Wallet currentWallet = wallet;
        String password = currentWallet.getPassword();
        String salt = currentWallet.getSalt();

        if (StringUtils.isBlank(currentWallet.getPassword())) {
            throw new RuntimeException("The password of the current wallet can not be empty");
        }

        try {
            Language eng = Language.ENGLISH;

            //getMnemonic
            String words = wallet.getMnemonic();
            //System.out.println("#Import Mnemonic# = " + words);

            //maybe salt is empty
            Keystore keystore = WalletUtils.generate(eng, words, StringUtils.isBlank(salt) ? null : salt, 0);

            String walletAddress = keystore.getWalletAddress();
            currentWallet.setAddress(walletAddress);

            String mnemonicEncrypt = AESUtil.encrypt(words, password);
            currentWallet.setMnemonicEncrypt(mnemonicEncrypt);

            //if salt not empty then encrypt
            if (StringUtils.isNotBlank(salt)) {
                String saltEncrypt = AESUtil.encrypt(salt, password);
                currentWallet.setSaltEncrypt(saltEncrypt);
            }

            currentWallet.setMnemonic(words);

            // save again
            setCurrentWallet(currentWallet);

        } catch (Exception exception) {
            log.error("Import and save to the current wallet error:", exception);
            throw new RuntimeException(exception);
        }

        return currentWallet;
    }

    public Wallet persistCurrentWallet() {
        Wallet currentWallet = getCurrentWallet();
        return persistCurrentWallet(currentWallet);
    }

    public Wallet persistCurrentWallet(Wallet currentWallet) {
        try {

            if (currentWallet != null && StringUtils.isNotBlank(currentWallet.getName())) {
                String address = currentWallet.getAddress();

                // all will set to false
                walletRepository.updateDefaultAsFalse();

                // the new one set to true
                currentWallet.setAddTime(LocalDateTime.now());
                currentWallet.setUpdateTime(LocalDateTime.now());
                currentWallet.setDefaultWallet(true);
                walletRepository.save(currentWallet);
                walletRepository.flush();
            }

        } catch (Exception exception) {
            log.error("Persist current wallet error:", exception);
            throw new RuntimeException(exception);
        }

        return currentWallet;
    }

    /***********************************************
     * CurrentKeystore
     /***********************************************/

    public void setCurrentKeystore(KeystoreWrapper k) {
        UI.getCurrent().getSession().setAttribute(KEY_BACKUP_WALLET, k);
    }

    public void clearCurrentKeystore() {
        UI.getCurrent().getSession().setAttribute(KEY_BACKUP_WALLET, null);
    }

    public KeystoreWrapper getCurrentKeystore() {
        return (KeystoreWrapper) UI.getCurrent().getSession().getAttribute(KEY_BACKUP_WALLET);
    }

    /*------------InnerClazz------------*/

    public static class CreateTypes {

        public static final String DESKTOP_LABEL = "DESKTOP";
        public static final int DESKTOP_VALUE = 0;

        public static final String IMPORT_BY_MNEMONIC_LABEL = "IMPORT_BY_MNEMONIC";
        public static final int IMPORT_BY_MNEMONIC_VALUE = 1;

        public static final String IMPORT_BY_SECRET_LABEL = "IMPORT_BY_SECRET";
        public static final int IMPORT_BY_SECRET_VALUE = 2;

        public static final String API_LABEL = "API";
        public static final int API_VALUE = 3;

        public static Map<Integer, String> DataMap = null;

        static {
            DataMap = new LinkedHashMap<Integer, String>();
            DataMap.put(DESKTOP_VALUE, DESKTOP_LABEL);
            DataMap.put(IMPORT_BY_MNEMONIC_VALUE, IMPORT_BY_MNEMONIC_LABEL);
            DataMap.put(IMPORT_BY_SECRET_VALUE, IMPORT_BY_SECRET_LABEL);
            DataMap.put(API_VALUE, API_LABEL);
        }
    }

    public List<WalletRow> getWalletRowList(List<Wallet> list, long instId) {
        List<WalletRow> content = Lists.newArrayList();

        if (list != null) {
            for (Wallet w : list) {
                if (StringUtils.isNotBlank(w.getName())) {
                    content.add(toWalletRow(w, instId));
                }
            }
        }
        return content;
    }

    public WalletRow toWalletRow(Wallet w, long instId) {
        WalletRow wr = new WalletRow();

        if (w == null) {
            return wr;
        }

        BeanUtils.copyProperties(w, wr);
        wr.setAddTimeText(DateUtil.formatDateTimeFull(w.getAddTime()));

        //dont show balance by this way , by pop window
/*
        String address = w.getAddress();
        List<AccountInfo> accountInfoList = accountInfoRepository.findByInstanceIdAndAddress(instId, address);

        if (CollUtil.isEmpty(accountInfoList)) {
            wr.setNomcBalance("0");
            wr.setNacBalance("0");
            wr.setUsdnBalance("0");
        }

        for (AccountInfo ai : accountInfoList) {
            Long tokenId = ai.getTokenId();

            if (tokenId.intValue() == CoreTokenEnum.NOMC.id) {
                wr.setNomcBalance(String.valueOf(MathUtil.round(ai.getBalance(), 9)));
            } else if (tokenId.intValue() == CoreTokenEnum.NAC.id) {
                wr.setNacBalance(String.valueOf(MathUtil.round(ai.getBalance(), 9)));
            } else if (tokenId.intValue() == CoreTokenEnum.USDN.id) {
                wr.setUsdnBalance(String.valueOf(MathUtil.round(ai.getBalance(), 9)));
            }
        }
*/
        return wr;
    }

    public Optional<WalletRow> getWalletRow(Integer id, long instId) {
        Optional<Wallet> optionalWallet = walletRepository.findById(id);

        if (!optionalWallet.isPresent()) {
            return Optional.of(null);
        }

        WalletRow walletRow = toWalletRow(optionalWallet.get(), instId);
        return Optional.of(walletRow);
    }

    public double getBalanceAsUsd(WalletRow walletRow) {

        String nacBalance = walletRow.getNacBalance();
        String nomcBalance = walletRow.getNomcBalance();
        String usdnBalance = walletRow.getUsdnBalance();

        Double nacValue = Double.valueOf(StringUtils.defaultIfBlank(nacBalance, "0"));
        Double nomcValue = Double.valueOf(StringUtils.defaultIfBlank(nomcBalance, "0"));
        Double usdnValue = Double.valueOf(StringUtils.defaultIfBlank(usdnBalance, "0"));

        Map<String, Double> priceMap = walletDataService.getPriceMap();
        Double nacPrice = priceMap.get("NAC");
        Double nomcPrice = priceMap.get("NOMC");

        if (nacPrice == null) {
            nacPrice = 0D;
        }
        if (nomcPrice == null) {
            nomcPrice = 0D;
        }

        return Double.valueOf(nacPrice * nacValue + nomcValue * nomcPrice + usdnValue);
    }

    public void setDefaultWallet(int id) {

        walletRepository.updateDefaultAsFalse();

        Optional<Wallet> walletOptional = walletRepository.findById(id);
        if (walletOptional.isPresent()) {
            Wallet wallet = walletOptional.get();

            if (wallet != null && StringUtils.isNotBlank(wallet.getName())) {
                wallet.setDefaultWallet(true);
                walletRepository.save(wallet);
            }

        }
    }

    /**
     * travel all tx to count address balance
     *
     * @param instanceId
     * @param address
     * @param tokenId
     * @return
     * @throws Exception
     */
    public double txTravelToCountBalance(long instanceId, String address, long tokenId) throws Exception {

        BigInteger bal = BigInteger.ZERO;

        TxDAO dao = new TxDAO(instanceId);
        List<Tx> rawJsonList = dao.geTxListByAddress(address, Integer.MAX_VALUE);
        for (Tx tx : rawJsonList) {

            //System.out.println(tx.toString());

            String to = tx.getTo();
            String from = tx.getFrom();
            long instance = tx.getInstance();
            long token = tx.getToken();
            BigInteger value = tx.getValue();
            BigInteger gas = tx.getGas();

            if (instance != instanceId) {
                continue;
            }
            if (token != tokenId) {
                continue;
            }

            int txType = tx.getTxType();
            if (TxType.TRANSFER_CHANGE.value == txType) {
                continue;
            }

            if (to.equalsIgnoreCase(address)) {
                bal = bal.add(value);
            }

            if (from.equalsIgnoreCase(address)) {
                bal = bal.subtract(value);
                bal = bal.subtract(gas);
            }

        }

        return NumberUtil.bigIntToNacDouble(bal);
    }


    public HttpResult send(Long instanceId, Long tokenId, String fromAddress, String toAddress, String password, String value, String remark) {

        HttpResult result = HttpResult.me().asFalse();
        try {

            Wallet fromWallet = this.getWalletRepository().findByAddress(fromAddress);
            if (instanceId == null) {
                return result.msg("Target instance can not be empty");
            }

            if (tokenId == null) {
                return result.msg("Target token can not be empty");
            }

            Token token = TokenUtil.getToken(tokenId);
            if (TokenUtil.isNftToken(token)) {
                return result.msg("NFT token cannot be sent");
            }

            if (fromWallet == null) {
                return result.msg("From wallet not exist,please set a wallet first");
            }

            if (StringUtils.isBlank(toAddress)) {
                return result.msg("Recipient address can not be empty");
            }

            if (!WalletUtil.isAddressValid(toAddress)) {
                return result.msg("Recipient address format error");
            }

            if (StringUtils.isBlank(value)) {
                return result.msg("Recipient amount can not be empty");
            }

            double amountValue = 0;
            try {
                amountValue = Double.valueOf(value);
            } catch (Exception exx) {
                return result.msg("Recipient amount format error");
            }

            if (amountValue <= 0) {
                return result.msg("Recipient amount error");
            }

            BigInteger gasFee = nodeClusterHttpService.get_gasFee(instanceId);
            if (gasFee.longValue() <= 0) {
                return result.msg("Gas fee error");
            }

            /*
            WalletRow walletRow = walletService.toWalletRow(walletService.getDefaultWallet(), currentInstanceId);
            double accountBalance = Double.valueOf(walletRow.getNacBalance());
            if (amountValue > accountBalance) {
                return result.msg("Recipient amount is greater than the wallet balance");
                return;
            }
            */

            if (StringUtils.isBlank(password)) {
                return result.msg("Password can not be empty");
            }

            remark = StringUtil.escapeHTMLTag(remark);

            if (StringUtils.isNotBlank(remark) && remark.length() > 500) {
                return result.msg("The length of the memo text cannot be greater than 500 characters");
            }

            String mnemonic = "";
            String salt = "";
            try {
                String mnemonicEncrypt = fromWallet.getMnemonicEncrypt();
                mnemonic = AESUtil.decrypt(mnemonicEncrypt, password);

                String saltEncrypt = fromWallet.getSaltEncrypt();
                if (StringUtils.isNotBlank(saltEncrypt)) {
                    salt = AESUtil.decrypt(saltEncrypt, password);
                }

            } catch (Exception exception) {
                String msg = String.format("The password is incorrect: " + exception.getMessage());
                log.error(msg, exception);
                return result.msg(msg);
            }

            // restore keystore and key
            Keystore keystore = WalletUtils.generate(Language.ENGLISH, mnemonic, StringUtils.isBlank(salt) ? null : salt, 0);
            byte[] privateKey = keystore.getPrivateKey();
            Key fromKey = new Key(privateKey);

            long txHeight = nodeClusterHttpService.getAccountTxHeight(instanceId, fromAddress, tokenId) + 1;

            BigInteger amountBigInt = Amount.of(BigDecimal.valueOf(amountValue), Unit.NAC).toBigInteger();
            double gasFeeDouble = NumberUtil.bigIntToNacDouble(gasFee);

            //if nac token transfer
            if (tokenId == CoreTokenEnum.NAC.id) {

                double total = amountValue + gasFeeDouble;

                HttpResult httpResult = checkTokenEnough(fromAddress, instanceId, tokenId, total);

                // check: amount + gas
                if (!httpResult.getFlag()) {
                    return httpResult;
                }

            } else {
                HttpResult httpResult = checkTokenEnough(fromAddress, instanceId, CoreTokenEnum.NAC.id, gasFeeDouble);

                // check: gas
                if (!httpResult.getFlag()) {
                    return httpResult;
                }
            }

            // check: amount
            HttpResult httpResult = checkTokenEnough(fromAddress, instanceId, tokenId, amountValue);

            // gas
            if (!httpResult.getFlag()) {
                return httpResult;
            }

            Tx sendTx = TxService.newTx(
                    TxType.TRANSFER,
                    instanceId, tokenId,
                    fromAddress, toAddress, amountBigInt,
                    gasFee,
                    TxGasType.NAC.value,
                    txHeight,
                    TxContextService.newTransferContext(instanceId),
                    remark, 0, fromKey);

            Mail mail = Mail.newMail(MailType.MSG_SEND_TX, sendTx.toJson());
            String hash = sendTx.getHash();
            String json = mail.toJson();

            log.warn("### mail hash now: "+mail.getHash());

            boolean flag = nodeClusterHttpService.broadcast(json, instanceId);

            log.info("Send tx detail: [flag = {} , hash = {} , instanceId = {} , tokenId = {} , txHeight = {} , amountValue = {} , fromAddress = {} , toAddressText = {} , json = {}]",
                    flag, hash, instanceId, tokenId, txHeight, amountValue, fromAddress, toAddress, json
            );

            Map<String, String> map = Maps.newHashMap();
            map.put("hash", hash);
            map.put("mail", json);

            return result.setFlag(flag).data(map);

        } catch (Exception exception) {
            String msg = "Transaction send failed [exception]:" + exception.getMessage();
            log.error(msg, exception);
            return result.msg(msg);
        }

    }

    public Key getWalletKey(Wallet fromWallet, String password) throws Exception {

        if (StringUtils.isBlank(password)) {
            return null;
        }
        if (fromWallet==null) {
            return null;
        }

        String mnemonic = "";
        String salt = "";
        try {
            String mnemonicEncrypt = fromWallet.getMnemonicEncrypt();
            mnemonic = AESUtil.decrypt(mnemonicEncrypt, password);

            String saltEncrypt = fromWallet.getSaltEncrypt();
            if (StringUtils.isNotBlank(saltEncrypt)) {
                salt = AESUtil.decrypt(saltEncrypt, password);
            }

        } catch (Exception exception) {
            String msg = String.format("The password is incorrect: " + exception.getMessage());
            log.error(msg, exception);
            return null;
        }

        // restore keystore and key
        Keystore keystore = WalletUtils.generate(Language.ENGLISH, mnemonic, StringUtils.isBlank(salt) ? null : salt, 0);
        byte[] privateKey = keystore.getPrivateKey();
        Key fromKey = new Key(privateKey);

        return fromKey;
    }

    public boolean isPasswordValid(String psw) {
        return RegexpUtil.Password_PATTERN.matcher(psw).matches();
    }

    public boolean isWalletNameValid(String walletName) {
        return RegexpUtil.WalletName_PATTERN.matcher(walletName).matches();
    }

    public boolean isWalletNameInvalid(String walletName) {
        return !isWalletNameValid(walletName);
    }

    public boolean isPasswordInvalid(String psw) {
        return !isPasswordValid(psw);
    }

    /**
     * Change wallet password
     * @param fromWallet
     * @param oldPassword
     * @return
     * @throws Exception
     */
    public HttpResult changeWalletPassword(Wallet fromWallet, String oldPassword, String newPassword) {
        HttpResult result = HttpResult.me().asFalse();

        try {
            Key walletKey = getWalletKey(fromWallet, oldPassword);

            if (walletKey == null) {
                return result.msg("The original password failed to unlock the wallet");
            }

            if (StringUtils.isEmpty(newPassword)) {
                return result.msg("New password cannot be empty");
            }

            if (isPasswordInvalid(newPassword)) {
                return result.msg("New Password must be 8-20 digits,at least one lowercase letter, one uppercase letter and one number");
            }

            Optional<Wallet> walletOptional = walletRepository.findById(fromWallet.getId());
            if (!walletOptional.isPresent()) {
                return result.msg("The target wallet cant be found");
            }

            Wallet wallet = walletOptional.get();

            String mnemonicEncrypt = fromWallet.getMnemonicEncrypt();
            String mnemonic = AESUtil.decrypt(mnemonicEncrypt, oldPassword);
            // encrypt again by newPassword
            mnemonicEncrypt = AESUtil.encrypt(mnemonic, newPassword);
            wallet.setMnemonicEncrypt(mnemonicEncrypt);

            String saltEncrypt = fromWallet.getSaltEncrypt();
            if (StringUtils.isNotBlank(saltEncrypt)) {
                String salt = AESUtil.decrypt(saltEncrypt, oldPassword);
                // encrypt again by newPassword
                saltEncrypt = AESUtil.encrypt(salt, newPassword);
                wallet.setSaltEncrypt(saltEncrypt);
            }

            walletRepository.save(wallet);

            //recheck
            if (getWalletKey(wallet, newPassword) == null) {
                return result.msg("Failed to unlock with new password after change the password");
            }

        } catch (Exception exception) {
            String msg = String.format("Change wallet to new password error: " + exception.getMessage());
            log.error(msg, exception);
            return result.msg(msg);
        }

        return result.asTrue();
    }

    public HttpResult checkTokenEnough(String address, long instanceId, long token, double condition) {

        Set<Map.Entry<Long, BigInteger>> entries = Sets.newHashSet();

        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(address, instanceId);

        if (usedTokenBalanceDetail != null && usedTokenBalanceDetail.getTokenBalanceMap() != null) {
            entries = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
        }

        Optional<Map.Entry<Long, BigInteger>> hasTokenEnough = entries.stream().filter(k -> k.getKey().longValue() == token).findFirst();

        String msgTmpl = "Please make sure that the current wallet has enough %s under the %s instance.";
        String tokenSymbol = TokenUtil.getTokenSymbol(token);
        String instanceSymbol = InstanceUtil.getInstanceSymbol(instanceId);
        HttpResult result = HttpResult.me().asFalse();

        if (!hasTokenEnough.isPresent()) {
            return result.msg(String.format(msgTmpl, tokenSymbol, instanceSymbol));
        }

        Map.Entry<Long, BigInteger> longBigIntegerEntry = hasTokenEnough.get();

        BigInteger value = longBigIntegerEntry.getValue();

        BigInteger conditionBigInt = NumberUtil.nacDoubleToBigInt(condition);

        boolean flag = value.longValue() >= conditionBigInt.longValue();

        if (!flag) {
            return result.msg(String.format(msgTmpl, tokenSymbol, instanceSymbol));
        }

        return result.setFlag(flag);

    }

    public Image selectTokenIcon(long tokenId) {

        if (CoreTokenEnum.NAC.id == tokenId) {
            return Images.nac_72x72();
        } else if (CoreTokenEnum.NOMC.id == tokenId) {
            return Images.nomc_72x72();
        } else if (CoreTokenEnum.USDN.id == tokenId) {
            return Images.usdn_72x72();
        } else {
            return Images.token_common_72x72();
        }

    }

    public double calcUsedTokenBalanceUsdTotal(UsedTokenBalanceDetail ubt) {

        Map<String, Double> priceMap = this.walletDataService.getPriceMap();

        double usdTotal = 0;
        if (ubt != null && ubt.getTokenBalanceMap() != null) {
            Set<Map.Entry<Long, BigInteger>> entries = ubt.getTokenBalanceMap().entrySet();

            for (Map.Entry<Long, BigInteger> entry : entries) {
                Long tokenId = entry.getKey();
                BigInteger value = entry.getValue();

                double amount = NumberUtil.bigIntToNacDouble(value);
                String symbol = TokenUtil.getTokenSymbol(tokenId).toUpperCase();

                Double price = priceMap.get(symbol);
                if (price != null && price > 0 && amount > 0) {
                    usdTotal += price * amount;
                }
            }
        }

        return usdTotal;
    }

    public HttpResult importWallet(String walletName, String mnemonicText, String salt, String password) {
        HttpResult result = HttpResult.me().asFalse();

        try {
            walletName = StringUtils.trim(walletName);
            mnemonicText = StringUtils.trim(mnemonicText);
            salt = StringUtils.trim(salt);
            password = StringUtils.trim(password);

            if (StringUtils.isEmpty(walletName)) {
                return result.msg("Wallet name can not be empty");
            }

            if (walletName.length() > 50) {
                return result.msg("Wallet name must be less than 50 characters");
            }

            if (isWalletNameInvalid(walletName)) {
                return result.msg("Wallet name must be 1-50 digits,only consist of numbers and letters");
            }

            if (walletRepository.countByName(walletName) != 0) {
                return result.msg("Wallet name already exists");
            }

            if (StringUtils.isEmpty(mnemonicText)) {
                return result.msg("Wallet mnemonic can not be empty");
            }

            if (mnemonicText.split(" ").length != 12) {
                return result.msg("Incorrect mnemonic format");
            }

            if (isPasswordInvalid(password)) {
                return result.msg("Password must be 8-20 digits,at least one lowercase letter, one uppercase letter and one number");
            }

            //check
            Keystore keystore = WalletUtils.generate(Language.ENGLISH, mnemonicText);
            if (keystore == null) {
                return result.msg("Import wallet error, keystore is null");
            }

            String walletAddress = keystore.getWalletAddress();
            int count = walletRepository.countByAddress(walletAddress);
            if (count > 0) {
                return result.msg("Wallet already exists: " + walletAddress);
            }

            //count the address balance
            long tokenId = CoreTokenEnum.NAC.id;
            long currentInstanceId = CoreInstanceEnum.NAC.id;
            double walletBalance = 0;

            //prepare
            Wallet wallet = new Wallet();
            wallet.setName(walletName);
            wallet.setPassword(password);
            wallet.setPswTip("");
            wallet.setMnemonic(mnemonicText);
            wallet.setAddress(walletAddress);
            wallet.setSalt(salt);
            wallet.setCreateType(WalletService.CreateTypes.IMPORT_BY_MNEMONIC_VALUE);

            Language eng = Language.ENGLISH;

            //maybe salt is empty
            keystore = WalletUtils.generate(eng, mnemonicText, StringUtils.isBlank(salt) ? null : salt, 0);
            String mnemonicEncrypt = AESUtil.encrypt(mnemonicText, password);
            wallet.setMnemonicEncrypt(mnemonicEncrypt);

            //if salt not empty then encrypt
            if (StringUtils.isNotBlank(salt)) {
                String saltEncrypt = AESUtil.encrypt(salt, password);
                wallet.setSaltEncrypt(saltEncrypt);
            }

            // all will set to false
            walletRepository.updateDefaultAsFalse();

            // the new one set to true
            wallet.setAddTime(LocalDateTime.now());
            wallet.setUpdateTime(LocalDateTime.now());
            wallet.setDefaultWallet(true);
            walletRepository.save(wallet);
            walletRepository.flush();

            //save account balance
            accountInfoService.saveAccountInfo(currentInstanceId, walletAddress, tokenId, walletBalance);

            //cache account address
            //walletDataService.setInstanceAccountAddress(currentInstanceId, walletAddress);

            return result.asTrue().msg("New wallet has been imported").data(wallet);

        } catch (Exception e) {
            log.error("Import wallet error", e);
            return result.asFalse().msg("Import wallet error:" + e.getMessage());
        }

    }

    public HttpResult deployNft(
            String name, String symbol, String info,
            String fromAddress,
            String password,
            Long mintTokenId,
            List<BigInteger> mintPriceList,
            List<Long> mintPriceBatchList,
            double royaltyPayment,
            String baseUri
    ) {
        HttpResult result = HttpResult.me().asFalse();

        // TODO FIXED
        royaltyPayment = 1;

        try {

            long fixNacInstanceId = CoreInstanceEnum.NAC.id;
            long fixAppChainInstanceId = CoreInstanceEnum.APPCHAIN.id;

            String tokenNameVal = StringUtils.trim(name);
            String tokenSymbolVal = StringUtils.trim(symbol);
            String tokenInfoVal = StringUtils.trim(info);

            HttpResult checkTokenBasicInfoResult = checkTokenBasicInfo(tokenNameVal, tokenSymbolVal, tokenInfoVal, "NFT token");
            if (!checkTokenBasicInfoResult.getFlag()) {
                return result.msg(checkTokenBasicInfoResult.getMessage());
            }

            if (CollUtil.isEmpty(mintPriceList)) {
                return result.msg("Mint price list can not be empty");
            }
            if (CollUtil.isEmpty(mintPriceBatchList)) {
                return result.msg("Mint price batch list can not be empty");
            }
            if (mintPriceList.size()!=1) {
                return result.msg("Mint price only support one element now");
            }
            if (mintPriceBatchList.size()!=1) {
                return result.msg("Mint price batch only support one element now");
            }

            if (mintPriceBatchList.size() != mintPriceList.size()) {
                return result.msg("Mint price or batch list size dont match");
            }
            if (royaltyPayment < 0 || royaltyPayment>1) {
                return result.msg("Invalid royalty payment");
            }
            if (StringUtils.isEmpty(baseUri)) {
                return result.msg("Invalid nft resource base uri");
            }
            if (!baseUri.endsWith("/")) {
                baseUri = baseUri + "/";
            }

            boolean nftCollectionDetailReady = nodeClusterHttpService.isNftCollectionDetailReady(baseUri);
            if (!nftCollectionDetailReady) {
                return result.msg("Base uri + nft-coll-detail.json cannot be reached by http request");
            }

            Wallet fromWallet = this.getWalletRepository().findByAddress(fromAddress);
            if (fromWallet == null) {
                return result.msg("From wallet not exist,please set a wallet first");
            }

            if (mintTokenId == null) {
                return result.msg("Target token used to mint can not be empty");
            }

            Token mintToken = TokenUtil.getToken(mintTokenId);
            if (mintToken == null) {
                return result.msg("Target mint token not exists");
            }

            long totalSupply = mintPriceBatchList.stream().collect(Collectors.summingLong(Long::longValue));
            if (totalSupply <= 0) {
                return result.msg("Invalid nft total supply");
            }

            if (StringUtils.isBlank(password)) {
                return result.msg("Password can not be empty");
            }

            Key fromKey = getWalletKey(fromWallet, password);
            if (fromKey == null) {
                return result.msg("Wallet password is incorrect");
            }

            //50u nac
            BigInteger needNacBalance = nodeClusterHttpService.getCalcDeployNacPrice();
            if (needNacBalance.longValue() == 0) {
                return result.msg("Fail to query nac required for deployment nft");
            }

            BigInteger gasFee = nodeClusterHttpService.get_gasFee(fixAppChainInstanceId);
            log.warn("DeployNFT - get_gasFee(fixAppChainInstanceId):" + gasFee);

            //gasFee = gasFee.add(BigInteger.valueOf(100000));

            if (gasFee.longValue() <= 0) {
                return result.msg("Failed to get the gas fee of the instance:" + fixAppChainInstanceId);
            }

            needNacBalance = needNacBalance.add(gasFee);

            // need nac
            double needNacDouble = NumberUtil.bigIntToNacDouble(needNacBalance);

            boolean isNacInAppChainEnough = false;
            UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(fromAddress, fixAppChainInstanceId);
            Set<Map.Entry<Long, BigInteger>> tokenBalanceMapEntrySet = null;
            if (usedTokenBalanceDetail != null && usedTokenBalanceDetail.getTokenBalanceMap() != null) {
                tokenBalanceMapEntrySet = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();
            }

            if (tokenBalanceMapEntrySet == null) {
                return result.msg("Fail to query used token balance detail");
            }

            if (usedTokenBalanceDetail != null && usedTokenBalanceDetail.getTokenBalanceMap() != null) {
                tokenBalanceMapEntrySet = usedTokenBalanceDetail.getTokenBalanceMap().entrySet();

                for (Map.Entry<Long, BigInteger> entry : tokenBalanceMapEntrySet) {
                    Long tokenId = entry.getKey();
                    BigInteger tokenBalanceBigInt = entry.getValue();

                    //check wallet address in appchain has enough nac
                    if (tokenId.longValue() == CoreTokenEnum.NAC.id) {
                        double nacDouble = NumberUtil.bigIntToNacDouble(tokenBalanceBigInt);
                        if (nacDouble > needNacDouble) {
                            isNacInAppChainEnough = true;
                        }

                    }
                }
            }

            if (!isNacInAppChainEnough) {
                return result.msg("Insufficient amount of nac required, expected amount: " + needNacDouble);
            }

            // deploy token need the APPCHAIN height
            long accountTxHeightInAppChain = nodeClusterHttpService.getAccountTxHeight(fixAppChainInstanceId, fromAddress, CoreTokenEnum.NAC.id);
            accountTxHeightInAppChain++;

            log.info("DeployNFT -> tokenNameVal = " + tokenNameVal + " , tokenSymbolVal = " + tokenSymbolVal + " , tokenInfoVal = " + tokenInfoVal + " , fromWallet = " + fromAddress + " , totalSupplyLong = " + totalSupply);

            NFTProtocol nftProtocol = NFTProtocolService.newNFTProtocol(baseUri, ".json", NftContentTypeEnum.IMAGE, mintTokenId, mintPriceList, mintPriceBatchList, royaltyPayment,fromAddress);

            Amount amountTotal = Amount.of(nftProtocol.countAmount(), Unit.NAC);

            Token token = TokenService.newNFToken(tokenNameVal, tokenSymbolVal, tokenInfoVal, amountTotal, nftProtocol);

            Mail newMail = MailService.newInstallTokenMail(token, fromAddress, needNacBalance, gasFee, accountTxHeightInAppChain, fromKey);

            String hash = token.getHash();
            String json = newMail.toJson();
            log.info("DeployNFT broadcast() -> hash = " + hash + " , json = " + json);

            boolean flag = nodeClusterHttpService.broadcast(json, fixAppChainInstanceId);
            return result.setFlag(flag).data(hash).msg(flag ? "Deploy nft successfully" : "Failed to deploy nft");

        } catch (Exception exception) {
            String msg = "Failed to deploy nft: " + exception.getMessage();
            log.error(msg, exception);
            return result.msg(msg);
        }

    }

    public HttpResult checkTokenBasicInfo(String tokenNameVal, String tokenSymbolVal, String tokenInfoVal, String target) {

        HttpResult result = HttpResult.me().asFalse();

        if (StringUtils.isBlank(tokenNameVal)) {
            return result.msg(target + " name can not be empty");
        }

        if (!tokenNameVal.matches(RegexpUtils.REGEXP_NORMAL)) {
            return result.msg(target + " name only supports numbers or english letters");
        }

        Optional<Token> tokenNameExistsOpt = TokenUtil.getEnableTokenList().stream().filter(t -> t.getName().equalsIgnoreCase(tokenNameVal)).findFirst();
        if (tokenNameExistsOpt.isPresent()) {
            return result.msg("Invalid " + target + " name, because it already exists");
        }

        if (tokenNameVal.length() > TokenService.VERIFY_NAME_LENGTH) {
            return result.msg("The length of " + target + " name cannot be greater than " + TokenService.VERIFY_NAME_LENGTH);
        }

        if (StringUtils.isBlank(tokenSymbolVal)) {
            return result.msg(target + " symbol can not be empty");
        }
        if (tokenSymbolVal.length() > TokenService.VERIFY_SYMBOL_LENGTH) {
            return result.msg("The length of " + target + " symbol cannot be greater than " + TokenService.VERIFY_SYMBOL_LENGTH);
        }

        if (!tokenSymbolVal.matches(RegexpUtils.REGEXP_NORMAL)) {
            return result.msg(target + " symbol only supports numbers or english letters");
        }

        if (StringUtils.isBlank(tokenInfoVal)) {
            return result.msg(target + " info can not be empty");
        }
        if (tokenInfoVal.length() > TokenService.VERIFY_INFO_LENGTH) {
            return result.msg("The length of  " + target + " info cannot be greater than " + TokenService.VERIFY_INFO_LENGTH);
        }

        if (!tokenInfoVal.matches(RegexpUtils.REGEXP_NORMAL)) {
            return result.msg(target + " info only supports numbers or english letters");
        }

        return result.asTrue();
    }

    public HttpResult mintNft(Long nftInstanceId, Long payTokenId, Long nftTokenId, String fromAddress, Long mintAmount, String password) {

        HttpResult result = HttpResult.me().asFalse();
        try {

            Wallet fromWallet = this.getWalletRepository().findByAddress(fromAddress);
            if (fromWallet == null) {
                return result.msg("From wallet not exist,please set a wallet first");
            }

            if (nftInstanceId == null) {
                return result.msg("Nft instance can not be empty");
            }
            if (payTokenId == null) {
                return result.msg("Mint token can not be empty");
            }
            if (nftTokenId == null) {
                return result.msg("Nft token can not be empty");
            }

            if (mintAmount == null || mintAmount<=0) {
                return result.msg("Invalid mint amount");
            }

            BigInteger gasFee = nodeClusterHttpService.get_gasFee(nftInstanceId);
            //BigInteger amountBigInt = Amount.of(BigDecimal.valueOf(amountValue), Unit.NAC).toBigInteger();
            double gasFeeDouble = NumberUtil.bigIntToNacDouble(gasFee);

            if (gasFee.longValue() <= 0) {
                return result.msg("Fetch gas fee error of the nft instance");
            }

            if (StringUtils.isBlank(password)) {
                return result.msg("Password can not be empty");
            }

            NftCollWrap nftCollWrap = nodeClusterHttpService.getNftCollectionByNodeCluster(nftInstanceId,nftTokenId);
            if (nftCollWrap==null) {
                return result.msg("Invalid nft collection");
            }
            NftCollection nftCollection = nftCollWrap.getNftCollection();
            long mintTotal = nftCollection.getNftProtocol().getMintPricesBatch().stream().mapToLong(Long::longValue).sum();
            long mintedAmount = nftCollection.getMintAmount();

            if (mintedAmount + mintAmount > mintTotal) {
                return result.msg("The current remaining amount that can be minted:"+(mintTotal-mintedAmount));
            }

            BigInteger nftOrderTotalValue = nodeClusterHttpService.getNftOrderTotal(nftInstanceId, nftTokenId, mintAmount);
            double nftOrderTotalDouble = NumberUtil.bigIntToNacDouble(nftOrderTotalValue);

            if (nftOrderTotalValue.equals(BigInteger.ZERO)) {
                return result.msg("Invalid nft order total value");
            }

            //if nac token transfer
            if (nftTokenId == CoreTokenEnum.NAC.id) {

                double total = nftOrderTotalDouble + gasFeeDouble;

                HttpResult httpResult = checkTokenEnough(fromAddress, nftInstanceId, nftTokenId, total);

                // check: amount + gas
                if (!httpResult.getFlag()) {
                    return httpResult;
                }

            } else {

                // if other token then only check nac gas fee
                HttpResult httpResult = checkTokenEnough(fromAddress, nftInstanceId, CoreTokenEnum.NAC.id, gasFeeDouble);

                // check: gas
                if (!httpResult.getFlag()) {
                    return httpResult;
                }
            }

            // check: amount
            HttpResult httpResult = checkTokenEnough(fromAddress, nftInstanceId, payTokenId, nftOrderTotalDouble);

            // gas
            if (!httpResult.getFlag()) {
                return httpResult;
            }

            Key fromKey = null;
            try {
                fromKey = getWalletKey(fromWallet, password);
            } catch (Exception e) {
            }

            if (fromKey == null) {
                return result.msg("Invalid wallet password");
            }

            long txHeight = nodeClusterHttpService.getAccountTxHeight(nftInstanceId, fromAddress, payTokenId) + 1;
            String toAddress = TxReservedWord.INSTANCE.name;

            Tx sendTx = TxService.newTx(
                    TxType.TRANSFER,
                    nftInstanceId, payTokenId,
                    fromAddress, toAddress,
                    nftOrderTotalValue,
                    gasFee,
                    TxGasType.NAC.value,
                    txHeight,
                    TxContextService.newNftMintContext(nftInstanceId, nftTokenId, mintAmount),
                    "", 0, fromKey);

            Mail mail = Mail.newMail(MailType.MSG_SEND_TX, sendTx.toJson());
            String hash = sendTx.getHash();
            String json = mail.toJson();

            boolean flag = nodeClusterHttpService.broadcast(json, nftInstanceId);

            log.info("Send tx detail of mint nft: [flag = {} , hash = {} , instanceId = {} , tokenId = {} , txHeight = {} , ntOrderTotalValue = {} , fromAddress = {} , toAddressText = {} , json = {}]",
                    flag, hash, nftInstanceId, payTokenId, txHeight, nftOrderTotalValue, fromAddress, toAddress, json
            );

            Map<String, String> map = Maps.newHashMap();
            map.put("hash", hash);
            map.put("mail", json);

            return result.setFlag(flag).data(map);

        } catch (Exception exception) {
            String msg = "Mint nft transaction send failed [exception]:" + exception.getMessage();
            log.error(msg, exception);
            return result.msg(msg);
        }

    }

    public HttpResult sendNft(
            Long nftInstanceId, Long nftTokenId, List<Long> toNftIdList,
            String fromAddress, String toAddress, String password
    ) {

        HttpResult result = HttpResult.me().asFalse();
        try {

            long payTokenId = CoreTokenEnum.NAC.id;

            Wallet fromWallet = this.getWalletRepository().findByAddress(fromAddress);
            if (fromWallet == null) {
                return result.msg("From wallet not exist,please set a wallet first");
            }

            if (nftInstanceId == null) {
                return result.msg("Nft instance can not be empty");
            }

            if (nftTokenId == null) {
                return result.msg("Nft token can not be empty");
            }

            if (CollUtil.isEmpty(toNftIdList)) {
                return result.msg("To nft item id list can not be empty");
            }

            if ( ! WalletUtil.isAddressValid(toAddress)) {
                return result.msg("Invalid to address");
            }

            BigInteger gasFee = nodeClusterHttpService.get_gasFee(nftInstanceId);
            //BigInteger amountBigInt = Amount.of(BigDecimal.valueOf(amountValue), Unit.NAC).toBigInteger();
            double gasFeeDouble = NumberUtil.bigIntToNacDouble(gasFee);

            if (gasFee.longValue() <= 0) {
                return result.msg("Fetch gas fee error of the nft instance");
            }

            if (StringUtils.isBlank(password)) {
                return result.msg("Password can not be empty");
            }

            Key fromKey = getWalletKey(fromWallet, password);

            {
                // if other token then only check nac gas fee
                HttpResult httpResult = checkTokenEnough(fromAddress, nftInstanceId, CoreTokenEnum.NAC.id, gasFeeDouble);

                // check: gas
                if (!httpResult.getFlag()) {
                    return httpResult;
                }
            }

            long txHeight = nodeClusterHttpService.getAccountTxHeight(nftInstanceId, fromAddress, nftTokenId) + 1;
            BigInteger sendValue = Amount.of(BigInteger.valueOf(toNftIdList.size()), Unit.NAC).toBigInteger();

            Tx sendTx = TxService.newTx(
                    TxType.TRANSFER,
                    nftInstanceId, nftTokenId,
                    fromAddress, toAddress,
                    sendValue,
                    gasFee,
                    TxGasType.NAC.value,
                    txHeight,
                    TxContextService.newNftTransferContext(nftInstanceId, nftTokenId, toNftIdList),
                    "", 0, fromKey);

            Mail mail = Mail.newMail(MailType.MSG_SEND_TX, sendTx.toJson());
            String hash = sendTx.getHash();
            String json = mail.toJson();

            boolean flag = nodeClusterHttpService.broadcast(json, nftInstanceId);

            log.info("Send nft tx detail of mint nft: [flag = {} , hash = {} , instanceId = {} , tokenId = {} , txHeight = {} , sendValue = {} , fromAddress = {} , toAddressText = {} , json = {}]",
                    flag, hash, nftInstanceId, payTokenId, txHeight, sendValue, fromAddress, toAddress, json
            );

            Map<String, String> map = Maps.newHashMap();
            map.put("hash", hash);
            map.put("mail", json);

            return result.setFlag(flag).data(map);

        } catch (Exception exception) {
            String msg = "Send nft transaction failed [exception]:" + exception.getMessage();
            log.error(msg, exception);
            return result.msg(msg);
        }

    }


}
