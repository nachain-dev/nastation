package org.nastation.module.dns.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class DomainApply extends AbstractEntity {

    private String contract;

    private String domainName;

    private Integer blockYear;

    private Long startBlock;

    private Long endBlock;

}
