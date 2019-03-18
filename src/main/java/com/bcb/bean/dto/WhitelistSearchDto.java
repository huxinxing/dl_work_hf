package com.bcb.bean.dto;

import com.bcb.domain.entity.Whitelist;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class WhitelistSearchDto extends Whitelist {

    private Integer pageSize;
    private Integer pageNum;
    private String address;
    private Timestamp from;
    private Timestamp to;
}
