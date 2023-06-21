package org.nastation.module.appstore.repo;

import org.nastation.module.appstore.data.AppStoreItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppStoreItemRepository extends JpaRepository<AppStoreItem, Integer> {

    Optional<AppStoreItem> findTopByOrderByIdDesc();

}