package org.nastation.module.wallet.repo;

import org.nastation.module.wallet.data.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
public interface WalletRepository extends JpaRepository<Wallet, Integer>, JpaSpecificationExecutor<Wallet> {

    int countByName(String name);

    int countByAddress(String name);

    Wallet findByName(String name);

    Wallet findByAddress(String address);

    Wallet findTopByDefaultWalletOrderByIdDesc(boolean defaultWallet);

    Wallet findTopByOrderByIdDesc();

    @Modifying
    @Query("update Wallet w set w.defaultWallet = false")
    void updateDefaultAsFalse();

    Page<Wallet> findByDefaultWallet(boolean defaultWallet, Pageable pageable);

}