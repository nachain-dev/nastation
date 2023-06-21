package org.nastation.module.dapp.repo;

import org.nastation.module.dapp.data.DApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DAppRepository extends JpaRepository<DApp, Integer> {

}