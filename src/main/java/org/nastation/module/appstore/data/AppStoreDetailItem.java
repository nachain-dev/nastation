package org.nastation.module.appstore.data;

import lombok.Data;

@Data
public class AppStoreDetailItem implements Cloneable {

    private int id;
    private String name;
    private String value;

    public AppStoreDetailItem(String name, String value) {
        this.name = name;
        this.value = value;
    }
}