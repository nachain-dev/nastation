package org.nastation.data.repo;

import org.nastation.data.entity.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Integer> {

    @Modifying
    @Query("update AccountInfo acc set acc.balance = acc.balance + ?1 where acc.instanceId=?2 and acc.tokenId=?3 and acc.address=?4")
    void addBalance(double balance,long instanceId,long tokenId,String address);

    @Modifying
    @Query("update AccountInfo acc set acc.balance = acc.balance - ?1 where acc.instanceId=?2 and acc.tokenId=?3 and acc.address=?4")
    void removeBalance(double balance,long instanceId,long tokenId,String address);

    List<AccountInfo> findByInstanceIdAndAddress(long instanceId, String address);

    AccountInfo findByInstanceIdAndAddressAndTokenId(long instanceId, String address,long tokenId);
}