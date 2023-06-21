package org.nastation.data.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Trace implements Serializable {

    private Integer id;

    private LocalDateTime addTime;
    private LocalDateTime updateTime;
    private String remark;

    private int productId;
    private String uuid;
    private String ip;
    private int version;
    private String json;
    private String action;
    private long blockHeight;

    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;

}
