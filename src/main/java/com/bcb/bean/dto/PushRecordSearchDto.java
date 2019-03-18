package com.bcb.bean.dto;

import com.bcb.domain.entity.PushRecord;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PushRecordSearchDto extends PushRecord {
    private Timestamp from;
    private Timestamp to;

    private Integer pageNum;
    private Integer pageSize;
}
