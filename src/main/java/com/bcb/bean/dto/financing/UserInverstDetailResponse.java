package com.bcb.bean.dto.financing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 用户投资理财展示分页DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInverstDetailResponse {

    private String sEcho ="";
    private Integer iTotalRecords;
    private Integer iTotalDisplayRecords;
    List<UserInverstDetailDto> aaData;

    public UserInverstDetailResponse() {
    }

    public UserInverstDetailResponse(String sEcho, Integer iTotalRecords, Integer iTotalDisplayRecords, List<UserInverstDetailDto> aaData) {
        this.sEcho = sEcho;
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.aaData = aaData;
    }

    public String getsEcho() {
        return sEcho;
    }

    public void setsEcho(String sEcho) {
        this.sEcho = sEcho;
    }

    public Integer getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(Integer iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public Integer getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(Integer iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public List<UserInverstDetailDto> getAaData() {
        return aaData;
    }

    public void setAaData(List<UserInverstDetailDto> aaData) {
        this.aaData = aaData;
    }
}
