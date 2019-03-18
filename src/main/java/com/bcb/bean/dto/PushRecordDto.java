package com.bcb.bean.dto;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class PushRecordDto implements Serializable {

    private Integer id;

    private String targetId;

    private String title;

    private String message;

    private Integer extraType;

    private String extraValue;

    private Date createTime;

    public String getCreateTime(){
        if(createTime == null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(createTime);
    }

    private Integer userId;

    private Boolean state;

}
