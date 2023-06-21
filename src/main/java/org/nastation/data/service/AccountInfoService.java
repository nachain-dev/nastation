package org.nastation.data.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.nastation.data.config.AppConfig;
import org.nastation.data.entity.AccountInfo;
import org.nastation.data.repo.AccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class AccountInfoService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    public AccountInfo saveAccountInfo(long instanceId, String address, long tokenId, double balance) {
        AccountInfo one = new AccountInfo();
        one.setAddress(address);
        one.setInstanceId(instanceId);
        one.setTokenId(tokenId);
        one.setBalance(balance);

        return accountInfoRepository.save(one);
    }

    public AccountInfo saveAccountInfo(AccountInfo accountInfo) {
        if (accountInfo == null) {
            return accountInfo;

        }
        return accountInfoRepository.save(accountInfo);
    }

}
