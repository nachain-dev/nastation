package org.nastation.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppNewVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int versionNumber;
    private String versionNumberDesc;
    private String appUrl;
    private String versionInfo;
    private int forceUpdate;

}