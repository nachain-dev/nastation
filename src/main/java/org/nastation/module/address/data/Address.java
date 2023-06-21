package org.nastation.module.address.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(indexes={
    @Index(name="add_address",columnList="address"),
    @Index(name="add_label",columnList="label"),
    @Index(name="add_tokenId",columnList="tokenId")
})
public class Address extends AbstractEntity {

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 255)/*, message = "{address.label.invalid}"*/
    //@Column(unique = true)
    private String label;

    private Long tokenId;

}
