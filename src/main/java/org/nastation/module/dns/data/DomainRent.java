package org.nastation.module.dns.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class DomainRent extends AbstractEntity {

    private String domainName;

    private String accountAddress;

    private Integer blockYear;

    private Long startBlock;

    private Long endBlock;

    private String nameserver1;

    private String nameserver2;

    private String nameserver3;

}
