package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import static javax.persistence.GenerationType.IDENTITY;


/**
 * Created by kx on 2018/1/13.
 */
@Entity
@Table(name = "custom_file_storage")
public class CustomFileStorage implements Serializable{
    @Id
    @GeneratedValue(strategy= IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "CreateTime")
    private Timestamp createTime;

    @Column(name = "FileName")
    private String fileName;

    @Column(name = "FileUrl")
    private String fileUrl;

    @Column(name = "FileType")
    private String fileType;

    @Column(name = "SeaFilePath")
    private String seaFilePath;

    @Column(name = "Status")
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeaFilePath() {
        return seaFilePath;
    }

    public void setSeaFilePath(String seaFilePath) {
        this.seaFilePath = seaFilePath;
    }
}
