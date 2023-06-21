package org.nastation.module.dns.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class DomainDnsItem extends AbstractEntity {

    private Integer domainId;

    private String host;

    private String recordType;

    private String address;

    private Integer priority;

    private boolean enable;

    private String accountAddress;

}
