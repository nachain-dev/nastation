package org.nastation.web.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nachain.core.base.Amount;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.token.nft.dto.NftCollWrap;
import org.nachain.core.token.nft.dto.NftItemWrap;
import org.nastation.common.model.HttpResult;
import org.nastation.common.model.PageWrap;
import org.nastation.common.service.NodeClusterHttpService;
import org.nastation.common.util.InstanceUtil;
import org.nastation.module.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/station/api/nft")
@Slf4j
public class NftApiController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private NodeClusterHttpService nodeClusterHttpService;

    @ApiOperation(value = "Query nft collection list", notes = "")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/getNftCollList", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getNftCollList(Model model, HttpServletRequest request) {

        List<NftCollWrap> nftCollWrapList = nodeClusterHttpService.getAllNftCollectionByNodeCluster();

        return ResponseEntity.ok(HttpResult.me().asTrue().data(nftCollWrapList));
    }

    @ApiOperation(value = "Query the NFT item information by nft collection token id", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nftId", value = "nft item id", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "collTokenId", value = "token id of the nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "collInstanceId", value = "instanceId id of the nft collection", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/getNftItem", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getNftCollection(
            @RequestParam long nftId,
            @RequestParam long collTokenId,
            @RequestParam long collInstanceId,
            Model model, HttpServletRequest request) {

        NftCollWrap nftColl = nodeClusterHttpService.getNftCollectionByNodeCluster(collInstanceId,collTokenId);

        if (nftColl == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("The nft collection is empty,please check collTokenId param"));
        }

        NftItemWrap nftItemWrap = nodeClusterHttpService.getNftItemWrap(nftId,nftColl);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(nftItemWrap));
    }

    @ApiOperation(value = "Query the NFT collection information by instance id and token id", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "collInstanceId", value = "instance id of the nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "collTokenId", value = "token id of the nft collection", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/getNftCollection", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getNftCollection(
            @RequestParam long collInstanceId,
            @RequestParam long collTokenId,
            Model model, HttpServletRequest request) {

        NftCollWrap nftColl = nodeClusterHttpService.getNftCollectionByNodeCluster(collInstanceId,collTokenId);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(nftColl));
    }

    @ApiOperation(value = "Query nft instance list", notes = "")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/getNftInstanceList", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getNftInstanceList(
            Model model, HttpServletRequest request) {

        List<Instance> nftInstanceList = InstanceUtil.getNftInstanceList();

        return ResponseEntity.ok(HttpResult.me().asTrue().data(nftInstanceList));
    }


    @ApiOperation(value = "Mint nft", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nftCollInstanceId", value = "instance id of nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "nftCollTokenId", value = "instance id of nft token", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "mintTokenId", value = "token id to mint for pay", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "fromAddress", value = "wallet address to mint nft", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mintAmount", value = "mint amount ", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "password", value = "wallet password", required = true, paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/mintNft", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> mintNft(
            @RequestParam Long nftCollInstanceId,
            @RequestParam Long nftCollTokenId,
            @RequestParam Long mintTokenId,
            @RequestParam String fromAddress,
            @RequestParam Long mintAmount,
            @RequestParam String password,
            Model model, HttpServletRequest request) {

        HttpResult result = walletService.mintNft(nftCollInstanceId,mintTokenId,nftCollTokenId,fromAddress,mintAmount,password);

        return ResponseEntity.ok(result);
    }


    @ApiOperation(value = "Deploy nft", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tokenName", value = "token name of nft collection", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tokenSymbol", value = "token symbol of nft collection", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tokenInfo", value = "token info of nft collection", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mintTokenId", value = "nft item to mint for pay", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "mintPrices", value = "stepped mint prices(double array by ',')", required = true, paramType = "query", dataType = "array"),
            @ApiImplicitParam(name = "mintPricesBatch", value = "stepped mint prices batch(long array by ',')", required = true, paramType = "query", dataType = "array"),
            @ApiImplicitParam(name = "royaltyPayment", value = "each nft royalty [0-1]", required = true, paramType = "query", dataType = "double"),
            @ApiImplicitParam(name = "baseUri", value = "metadata base uri of nft collection", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fromAddress", value = "wallet address to deploy nft", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "wallet password", required = true, paramType = "query", dataType = "string")

    })
    @RequestMapping(value = "/deployNft", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> deployNft(
            @RequestParam String tokenName,
            @RequestParam String tokenSymbol,
            @RequestParam String tokenInfo,
            @RequestParam Long mintTokenId,
            @RequestParam Double[] mintPrices,
            @RequestParam Long[] mintPricesBatch,
            @RequestParam Double royaltyPayment,
            @RequestParam String baseUri,
            @RequestParam String fromAddress,
            @RequestParam String password,
            Model model, HttpServletRequest request) {

        List<BigInteger> mintPricesList = Arrays.stream(mintPrices).map(e -> Amount.toToken(e)).collect(Collectors.toList());
        List<Long> mintPricesBatchList = Arrays.stream(mintPricesBatch).collect(Collectors.toList());

        HttpResult result = walletService.deployNft(tokenName,tokenSymbol,tokenInfo,
                fromAddress,password,
                mintTokenId,mintPricesList,mintPricesBatchList,
                royaltyPayment,baseUri
                );

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Send nft", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nftCollInstanceId", value = "instance id of nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "nftCollTokenId", value = "instance id of nft token", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "toNftIds", value = "nft item id array to send", required = true, paramType = "query", dataType = "array"),
            @ApiImplicitParam(name = "fromAddress", value = "sender wallet address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "toAddress", value = "target wallet address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "sender wallet password", required = true, paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/sendNft", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> sendNft(
            @RequestParam Long nftCollInstanceId,
            @RequestParam Long nftCollTokenId,
            @RequestParam Long[] toNftIds,
            @RequestParam String fromAddress,
            @RequestParam String toAddress,
            @RequestParam String password,
            Model model, HttpServletRequest request) {

        if (toNftIds == null) {
            toNftIds = new Long[]{};
        }

        List<Long> toNftIdList = Arrays.stream(toNftIds).collect(Collectors.toList());

        HttpResult result = walletService.sendNft(nftCollInstanceId, nftCollTokenId, toNftIdList, fromAddress, toAddress, password);

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Paging to query the NFT collection information", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "page number", required = true, paramType = "query", dataType = "int")
    })
    @RequestMapping(value = "/getNftCollPage", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getNftCollPage(
            @RequestParam int pageNum,
            Model model, HttpServletRequest request) {

        PageWrap<NftCollWrap> accountNftCollPage = nodeClusterHttpService.getNftCollPage( pageNum);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(accountNftCollPage));
    }

    @ApiOperation(value = "Paging to query the NFT collection information of the wallet account", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletAddress", value = "wallet address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pageNum", value = "page number", required = true, paramType = "query", dataType = "int")
    })
    @RequestMapping(value = "/getAccountNftCollPage", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getAccountNftCollPage(
            @RequestParam String walletAddress,
            @RequestParam int pageNum,
            Model model, HttpServletRequest request) {

        PageWrap<NftCollWrap> accountNftCollPage = nodeClusterHttpService.getAccountNftCollPage(walletAddress, pageNum);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(accountNftCollPage));
    }

    @ApiOperation(value = "Paging to query the NFT item information in nft collection", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "collInstanceId", value = "instance id of the nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "collTokenId", value = "token id of the nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "pageNum", value = "page number", required = true, paramType = "query", dataType = "int")
    })
    @RequestMapping(value = "/getNftItemWrapPage", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getNftItemWrapPage(
            @RequestParam long collInstanceId,
            @RequestParam long collTokenId,
            @RequestParam int pageNum,
            Model model, HttpServletRequest request) {

        // the nft coll
        NftCollWrap nftCollWrap = nodeClusterHttpService.getNftCollectionByNodeCluster(collInstanceId,collTokenId);

        if (nftCollWrap == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("The nft collection is empty,please check collTokenId param"));
        }

        PageWrap<NftItemWrap> pw = nodeClusterHttpService.getNftItemWrapPage(nftCollWrap, nftCollWrap.getNftCollInstanceId(), pageNum);

        return ResponseEntity.ok(HttpResult.me().asTrue().data(pw));
    }

    @ApiOperation(value = "Paging to query the NFT item information in nft collection of the wallet account ", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletAddress", value = "wallet address", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pageNum", value = "page number", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "collInstanceId", value = "instance id of the nft collection", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "collTokenId", value = "token id of the nft collection", required = true, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/getAccountNftItemWrapPage", method = RequestMethod.GET)
    public ResponseEntity<HttpResult> getAccountNftCollPage(
            @RequestParam long collInstanceId,
            @RequestParam long collTokenId,
            @RequestParam String walletAddress,
            @RequestParam int pageNum,
            Model model, HttpServletRequest request) {

        NftCollWrap nftCollWrap = nodeClusterHttpService.getNftCollectionByNodeCluster(collInstanceId,collTokenId);
        if (nftCollWrap == null) {
            return ResponseEntity.ok(HttpResult.me().asFalse().msg("The nft collection is empty,please check collTokenId param"));
        }

        PageWrap<NftItemWrap> accountNftItemPage = nodeClusterHttpService.getAccountNftItemWrapPageByNodeCluster(
                nftCollWrap, walletAddress, nftCollWrap.getNftCollTokenId(), pageNum
        );

        return ResponseEntity.ok(HttpResult.me().asTrue().data(accountNftItemPage));
    }




}