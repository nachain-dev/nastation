package org.nastation.module.vote.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class VoteNode extends AbstractEntity {

    private int voteInstance;
    private String voteAddress;
    private Double amount;

}
