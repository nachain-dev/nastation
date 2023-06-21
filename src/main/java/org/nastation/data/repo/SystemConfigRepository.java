package org.nastation.data.repo;

import org.nastation.data.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Integer> {

    SystemConfig findByInstanceIdAndName(long instanceId,String name);

    @Transactional
    @Modifying
    @Query("update SystemConfig sc set sc.value = ?1 where sc.instanceId=?2 and sc.name=?3")
    void updateValue(String value,long instanceId,String name);

}