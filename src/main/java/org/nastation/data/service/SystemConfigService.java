package org.nastation.data.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nastation.data.entity.SystemConfig;
import org.nastation.data.repo.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
@Slf4j
@Data
public class SystemConfigService {

    private String key = "CurrentStationUUID";

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @PostConstruct
    public void init() {

        try {

            long id = CoreInstanceEnum.NAC.id;

            SystemConfig config = systemConfigRepository.findByInstanceIdAndName(id, key);
            if (config == null) {
                config = new SystemConfig();
                config.setValue(UUID.randomUUID().toString());
                config.setName(key);
                config.setInstanceId(CoreInstanceEnum.NAC.id);
                systemConfigRepository.save(config);
            }
        } catch (Exception e) {
            log.error("SystemConfigService init error:", e);
        }

    }

    public String getUUID() {
        long instanceId = CoreInstanceEnum.NAC.id;
        SystemConfig config = systemConfigRepository.findByInstanceIdAndName(instanceId, key);
        return config == null ? "" : config.getValue();
    }

}
