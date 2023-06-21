package org.nastation.web.controller;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.crypto.bip39.Language;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailType;
import org.nachain.core.token.Token;
import org.nachain.core.util.Hex;
import org.nachain.core.wallet.WalletUtils;
import org.nachain.core.wallet.keystore.Keystore;
import org.nastation.common.model.HttpResult;
import org.nastation.common.service.NaScanHttpService;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.service.SystemService;
import org.nastation.common.util.AESUtil;
import org.nastation.common.util.InstanceUtil;
import org.nastation.common.util.TokenUtil;
import org.nastation.common.util.WalletUtil;
import org.nastation.data.service.WalletDataService;
import org.nastation.data.vo.UsedTokenBalanceDetail;
import org.nastation.module.protocol.data.BlockDataBodyRow;
import org.nastation.module.protocol.data.TxDataRow;
import org.nastation.module.protocol.service.BlockDataService;
import org.nastation.module.protocol.service.TxDataService;
import org.nastation.module.pub.data.ProcessInfo;
import org.nastation.module.pub.service.DataSyncService;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.nastation.module.wallet.service.WalletService;
import org.nastation.web.vo.WalletBasicVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/station/api")
@Slf4j
public class CommonApiController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletDataService walletDataService;

    @Autowired
    private BlockDataService blockDataService;

    @Autowired
    private TxDataService txDataService;

    @Autowired
    private NodeClusterHttpService nodeClusterHttpService;

    @Autowired
    private NaScanHttpService naScanHttpService;

    @Autowired
    private DataSyncService dataSyncService;


    @ApiOperation(value = "Ping test", notes = "")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> ping(
            Model model, HttpServletRequest request) {
        return ResponseEntity.ok(HttpResult.me().asTrue().data("pong"));
    }

    @ApiOperation(value = "Query data sync process info of each instance", notes = "")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/getDataSyncProcessInfoList", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getProcessInfoList(
            Model model, HttpServletRequest request) {

        List<ProcessInfo> processInfoList = dataSyncService.getProcessInfoList();

        return ResponseEntity.ok(HttpResult.me().asTrue().data(processInfoList));
    }

    @ApiOperation(value = "Manually set stop data sync", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "set flag of data sync", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/stopDataSync", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> stopDataSync(
            @RequestParam(defaultValue = "true") String flag,
            Model model, HttpServletRequest request) {

        SystemService.me().setStopRun(Boolean.valueOf(flag));
        boolean stopRun = SystemService.me().isStopRun();
        boolean isRequestBlockDataOver = SystemService.me().isRequestBlockDataOver();

        Map<String, String> map = Maps.newHashMap();
        map.put("isRequestBlockDataOver", String.valueOf(isRequestBlockDataOver));
        map.put("stopRun", String.valueOf(stopRun));
        map.put("tips", String.valueOf("You can execute \"kill - 2 PID\" after 10 seconds to stop the server"));

        return ResponseEntity.ok(HttpResult.me().asTrue().data(map));
    }

    @ApiOperation(value = "Create a new wallet account", notes = "The salt parameter is optional, it is recommended that the password parameter and salt parameter be as complex as possible")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "wallet name", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "wallet password(length must be greater than 8 digits)", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "salt", value = "wallet salt", required = false, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/account/new", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> newAccount(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String password,
            @RequestParam(defaultValue = "") String salt,
            Model model, HttpServletRequest request) {

        WalletRepository repository = walletService.getWalletRepository();

        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet name can not be empty"));
        }

        if (walletService.isWalletNameInvalid(name)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet name must be 1-50 digits,only consist of numbers and letters"));
        }

        int count = repository.countByName(name);
        if (count > 0) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet name already exists"));
        }

        if (StringUtils.isBlank(password)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet password can not be empty"));
        }

        //check psw
        if (walletService.isPasswordInvalid(password)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Password must be 8-20 digits,at least one lowercase letter, one uppercase letter and one number"));
        }

        Wallet currentWallet = new Wallet();
        currentWallet.setName(name);

        Language lang = Language.ENGLISH;
        String words = WalletUtils.generateWords(lang);
        Map<String, String> map = Maps.newHashMap();

        try {

            boolean isSaltBlank = StringUtils.isBlank(salt);

            //maybe salt is empty
            Keystore keystore = WalletUtils.generate(lang, words, isSaltBlank ? null : salt, 0);

            String walletAddress = keystore.getWalletAddress();
            currentWallet.setAddress(walletAddress);

            byte[] privateKey = keystore.getPrivateKey();
            String pkHex = Hex.encode0x(privateKey);

            String mnemonicEncrypt = AESUtil.encrypt(words, password);
            currentWallet.setMnemonicEncrypt(mnemonicEncrypt);

            //if salt not empty then encrypt
            if (!isSaltBlank) {
                String saltEncrypt = AESUtil.encrypt(salt, password);
                currentWallet.setSaltEncrypt(saltEncrypt);
            }

            walletService.persistCurrentWallet(currentWallet);

            //map.put("id", String.valueOf(currentWallet.getId()));
            map.put("name", currentWallet.getName());
            map.put("address", walletAddress);
            map.put("password", password);
            map.put("mnemonic", words);
            map.put("privateKey", pkHex);
            map.put("salt", salt);

        } catch (Exception exception) {
            String msg = "Create wallet account error:" + exception.getMessage();
            log.error(msg, exception);
            return ResponseEntity.ok(HttpResult.me().asFalse().msg(msg));
        }

        return ResponseEntity.ok(HttpResult.me().asTrue().data(map));
    }

    @ApiOperation(value = "Import a new wallet account", notes = "The salt parameter is optional, it is recommended that the password parameter and salt parameter be as complex as possible")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletName", value = "wallet name", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mnemonicText", value = "mnemonic text(separated by spaces)", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "salt", value = "wallet salt", required = false, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "wallet password", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/account/import", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> importAccount(
            @RequestParam(defaultValue = "") String walletName,
            @RequestParam(defaultValue = "") String mnemonicText,
            @RequestParam(defaultValue = "") String salt,
            @RequestParam(defaultValue = "") String password,
            Model model, HttpServletRequest request) {

        HttpResult result = walletService.importWallet(walletName, mnemonicText, salt, password);

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Change wallet password", notes = "Modify the password of the wallet according to the name, the original password is required, and the new password must conform to the password format")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletName", value = "wallet name", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "oldPassword", value = "old wallet password", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "newPassword", value = "new wallet password", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/account/changePassword", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> changePassword(
            @RequestParam(defaultValue = "") String walletName,
            @RequestParam(defaultValue = "") String oldPassword,
            @RequestParam(defaultValue = "") String newPassword,
            Model model, HttpServletRequest request) {

        if (StringUtils.isBlank(walletName)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet name can not be empty"));
        }

        Wallet wallet = walletService.getWalletRepository().findByName(walletName);
        if (wallet == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet object can not be empty"));
        }

        HttpResult result = walletService.changeWalletPassword(wallet, oldPassword, newPassword);

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query wallet info", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletName", value = "wallet name", required = true, paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/account/query", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> accountQuery(
            @RequestParam(defaultValue = "") String walletName,
            Model model, HttpServletRequest request) {

        walletName = StringUtils.trim(walletName);

        if (StringUtils.isBlank(walletName)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet name can not be empty"));
        }

        Wallet wallet = walletService.getWalletRepository().findByName(walletName);
        if (wallet == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet object can not be empty"));
        }

        //copy
        WalletBasicVo vo = new WalletBasicVo();
        BeanUtils.copyProperties(wallet, vo);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(vo));
    }

    @ApiOperation(value = "Wallet account send", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "from address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "toAddress", value = "to address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "wallet password", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "value", value = "send amount", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "token", value = "token id", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "remark", value = "remark", required = true, paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/account/send", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> accountSend(
            @RequestParam(defaultValue = "") String fromAddress,
            @RequestParam(defaultValue = "") String toAddress,
            @RequestParam(defaultValue = "") String password,
            @RequestParam(defaultValue = "") String value,
            @RequestParam(defaultValue = "") Long instanceId,
            @RequestParam(defaultValue = "") Long token,
            @RequestParam(defaultValue = "") String remark,
            Model model, HttpServletRequest request) {

        HttpResult send = walletService.send(instanceId,token,fromAddress, toAddress, password, value,remark);

        return ResponseEntity.ok(send);
    }


    @ApiOperation(value = "Paging query wallet account list", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "page number (start from 1) ", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "page size", required = true, paramType = "query", dataType = "int")
    })
    @RequestMapping(value = "/account/list", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> listAccount(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            Model model, HttpServletRequest request) {

        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize, Sort.Direction.DESC, "id");
        Page<Wallet> page = walletService.getWalletRepository().findAll(pageRequest);

        List<Wallet> content = page.getContent();
        List<WalletBasicVo> list = Lists.newArrayList();

        if (content != null) {
            for (Wallet w : content) {
                WalletBasicVo vo = new WalletBasicVo();
                BeanUtils.copyProperties(w, vo);
                list.add(vo);
            }
        }

        return ResponseEntity.ok(HttpResult.me().asTrue().data(list));
    }

    @ApiOperation(value = "Query wallet account detail", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "wallet address", required = true, paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/account/detail", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> accountDetail(
            @RequestParam(defaultValue = "") String address,
            Model model, HttpServletRequest request) {

        Wallet wallet = walletService.getWalletRepository().findByAddress(address);
        if (wallet != null) {
            wallet.setMnemonic("");
            wallet.setMnemonicEncrypt("");
            wallet.setSalt("");
            wallet.setSaltEncrypt("");
            wallet.setPassword("");
        }
        return ResponseEntity.ok(HttpResult.me().asTrue().data(wallet));
    }

    @ApiOperation(value = "Count wallet account", notes = "")
    @RequestMapping(value = "/account/count", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> accountCount(
            Model model, HttpServletRequest request) {

        long wallet = walletService.getWalletRepository().count();

        return ResponseEntity.ok(HttpResult.me().asTrue().data(wallet));
    }


    @ApiOperation(value = "Query wallet account balance", notes = "Different instances have different token balance")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "wallet address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/account/balance", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> accountBalance(
            @RequestParam(name = "address", defaultValue = "") String address,
            @RequestParam(name = "instanceId", defaultValue = "") Long instanceId,
            Model model, HttpServletRequest request) {

        if (instanceId == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Instance id can not be empty"));
        }

        if (StringUtils.isBlank(address)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet address can not be empty"));
        }

        if (!WalletUtil.isAddressValid(address)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Wallet address is not valid"));
        }

        UsedTokenBalanceDetail usedTokenBalanceDetail = naScanHttpService.getUsedTokenBalanceDetail(address, instanceId);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(usedTokenBalanceDetail));
    }

    @ApiOperation(value = "Query block detail info", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "height", value = "block height", required = false, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = false, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/block/detail", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> blockDetail(
            @RequestParam(defaultValue = "") Long height,
            @RequestParam(defaultValue = "") Long instanceId,
            Model model, HttpServletRequest request) {


        if (height == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Block height can not be empty"));
        }
        if (instanceId == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Instance id can not be empty"));
        }
        BlockDataBodyRow row = null;

        try {
            row = blockDataService.getBlockDataBodyRow(instanceId, height);
        } catch (Exception ex) {
            log.error("request /block/detail api error: [ height = {} , instanceId = {} ] ", height, instanceId, ex);
        }

        return ResponseEntity.ok(HttpResult.me().setFlag(row != null).data(row));
    }

    @ApiOperation(value = "Query last block height", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/block/lastHeight", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> blockLastHeight(
            @RequestParam(defaultValue = "") Long instanceId,
            Model model, HttpServletRequest request) {

        if (instanceId == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Instance id can not be empty"));
        }

        long lastBlockHeight = walletDataService.getCurrentSyncBlockHeightFromCache(instanceId);

        return ResponseEntity.ok(HttpResult.me().setFlag(lastBlockHeight > 0).data(lastBlockHeight));
    }

    @ApiOperation(value = "Query last block height by scan", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/block/getLastHeightByScan", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getLastHeightByNode(
            @RequestParam(defaultValue = "") Long instanceId,
            Model model, HttpServletRequest request) {

        if (instanceId == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Instance id can not be empty"));
        }

        long lastBlockHeight = naScanHttpService.getLastBlockHeightRetry(instanceId);

        return ResponseEntity.ok(HttpResult.me().setFlag(lastBlockHeight > 0).data(lastBlockHeight));
    }

    @ApiOperation(value = "Query tx detail info", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hash", value = "tx hash", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/tx/detail", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> txDetail(
            @RequestParam(defaultValue = "") Long instanceId,
            @RequestParam(defaultValue = "") String hash,
            Model model, HttpServletRequest request) {

        if (instanceId == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Instance id can not be empty"));
        }

        if (StringUtils.isBlank(hash)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Tx hash can not be empty"));
        }

        //String key = walletDataService.buildTxDbKey(height, hash);
        TxDataRow txDataRow = txDataService.getTxDataRow(instanceId, hash);

        return ResponseEntity.ok(HttpResult.me().setFlag(txDataRow != null).data(txDataRow));
    }

    @ApiOperation(value = "Broadcast raw json", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "txJson", value = "tx json text", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/tx/broadcastRaw", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> txBroadcast(
            @RequestParam(defaultValue = "") String txJson,
            @RequestParam(defaultValue = "") Long instanceId,
            Model model, HttpServletRequest request) {

        if (StringUtils.isBlank(txJson)) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("Tx json can not be empty"));
        }

        boolean flag = false;
        String mailHash = "";
        String msg = "";
        try {

            // only NAC now
            //instanceId = CoreInstanceEnum.NAC.id;

            Mail mail = Mail.newMail(MailType.MSG_SEND_TX, txJson);
            mailHash = mail.getHash();
            flag = nodeClusterHttpService.broadcast(txJson, instanceId);

        } catch (Exception e) {
            log.error("nodeClusterHttpService broadcast error:", e);
            msg = "broadcast error:" + e.getMessage();
        }

        return ResponseEntity.ok(HttpResult.me().msg(msg).setFlag(flag).data(""));
    }

    @ApiOperation(value = "Query instance list", notes = "")
    @RequestMapping(value = "/instance/list", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> instanceList(Model model, HttpServletRequest request) {
        List<Instance> list = InstanceUtil.getEnableInstanceList();
        return ResponseEntity.ok(HttpResult.me().asTrue().data(list));
    }

    @ApiOperation(value = "Query token list", notes = "")
    @RequestMapping(value = "/token/list", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> instanceTokenList(Model model, HttpServletRequest request) {
        List<Token> enableTokenList = TokenUtil.getEnableTokenList();
        return ResponseEntity.ok(HttpResult.me().asTrue().data(enableTokenList));
    }

    @ApiOperation(value = "Query gas fee", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "instance id", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/gas/fee", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getGasFee(
            @RequestParam(defaultValue = "1") Long instanceId,
            Model model, HttpServletRequest request) {

        double gasFeeValue = naScanHttpService.getGasFee(instanceId);
        return ResponseEntity.ok(HttpResult.me().asTrue().data(gasFeeValue));
    }




}