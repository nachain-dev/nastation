package org.nastation.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(indexes={
        @Index(name="sys_name",columnList="name"),
        @Index(name="sys_instanceId",columnList="instanceId"),
        @Index(name="sys_type",columnList="type")
})
public class SystemConfig extends AbstractEntity {

    @Column
    private String name;

    @Column(columnDefinition="TEXT")
    private String value;

    private int type;

    private long instanceId;

    public SystemConfig(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public SystemConfig(long instanceId,String name, String value) {
        this.instanceId = instanceId;
        this.name = name;
        this.value = value;
    }
}