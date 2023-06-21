package org.nastation.module.dns.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Data
public class Domain extends AbstractEntity {

    private String name;

    private LocalDateTime regDate;

    private Integer regBlock;

    private LocalDateTime expireDate;

    private Integer expireBlock;

    private Double paymentAmount;

    private Integer paymentCoinTypeId;

    private String paymentCoinType;

    private String nameserver1;

    private String nameserver2;

    private String nameserver3;

    private String txhash;

    private String accountAddress;


}
