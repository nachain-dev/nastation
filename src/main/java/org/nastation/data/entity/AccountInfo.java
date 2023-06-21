package org.nastation.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(indexes={
        @Index(name="ai_instanceId",columnList="instanceId"),
        @Index(name="ai_address",columnList="address"),
        @Index(name="ai_tokenId",columnList="tokenId")
})
public class AccountInfo extends AbstractEntity {

    private Long instanceId;

    private Long tokenId;

    private String address;

    private Double balance;

}