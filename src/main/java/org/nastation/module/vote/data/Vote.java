package org.nastation.module.vote.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Data
public class Vote extends AbstractEntity {

    private int voteInstance;
    private String voteAddress;
    private String beneficiaryAddress;
    private String nominateAddress;

    private Double amount;
    private Integer status;
    private String txHash;
    private LocalDateTime voteTime;

}
