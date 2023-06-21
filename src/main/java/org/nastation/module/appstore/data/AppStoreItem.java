package org.nastation.module.appstore.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class AppStoreItem extends AbstractEntity {

    private String name;
    private String image;
    private String intro;
    private String author;
    private String versionText;
    private String category;
    private String size;
    private String hash;

}