package org.nastation.module.dfs.data;

import lombok.Data;
import org.nastation.data.entity.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Data
public class FileItem extends AbstractEntity {

    private String fileName;

    private String fileSize;

    private String fileType;

    private String bucketName;

    private String fileHash;

    private String authorAddress;

    private String fee;

}
