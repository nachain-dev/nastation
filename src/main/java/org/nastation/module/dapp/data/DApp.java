package org.nastation.module.dapp.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class DApp extends AbstractEntity {

    private String name;

    private String icon;

    private String domain;

    private String storageHash;

    private String dappHash;

    private String authorAddress;

    private String type;

    private String status;

    private String fileSize;

}
