package com.bcb.bean.dto;

/**
 * Created by black on 18-5-28.
 */
public class SzgDictInfoDto {
    private Integer dicId; //主键

    private String dicTypeId;  //类别Id

    private String dicKey; //配置项名称

    private String dicValue;  //配置项值

    private String dicLangFlag; //配置项语言标志

    private Integer isUse; //是否启用 1启用 0未启用

    private Integer sortIndex; //排序子段

    private String createTime; //创建时间

    private String dicTypeCode; //类别编码

    private String dicTypeName; //类别名称

    public Integer getDicId() {
        return dicId;
    }

    public void setDicId(Integer dicId) {
        this.dicId = dicId;
    }

    public String getDicTypeId() {
        return dicTypeId;
    }

    public void setDicTypeId(String dicTypeId) {
        this.dicTypeId = dicTypeId;
    }

    public String getDicKey() {
        return dicKey;
    }

    public void setDicKey(String dicKey) {
        this.dicKey = dicKey;
    }

    public String getDicValue() {
        return dicValue;
    }

    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }

    public String getDicLangFlag() {
        return dicLangFlag;
    }

    public void setDicLangFlag(String dicLangFlag) {
        this.dicLangFlag = dicLangFlag;
    }

    public Integer getIsUse() {
        return isUse;
    }

    public void setIsUse(Integer isUse) {
        this.isUse = isUse;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDicTypeCode() {
        return dicTypeCode;
    }

    public void setDicTypeCode(String dicTypeCode) {
        this.dicTypeCode = dicTypeCode;
    }

    public String getDicTypeName() {
        return dicTypeName;
    }

    public void setDicTypeName(String dicTypeName) {
        this.dicTypeName = dicTypeName;
    }

    @Override
    public String toString() {
        return "SzgDictInfoDto{" +
                "dicId=" + dicId +
                ", dicTypeId='" + dicTypeId + '\'' +
                ", dicKey='" + dicKey + '\'' +
                ", dicValue='" + dicValue + '\'' +
                ", dicLangFlag='" + dicLangFlag + '\'' +
                ", isUse=" + isUse +
                ", sortIndex=" + sortIndex +
                ", createTime='" + createTime + '\'' +
                ", dicTypeCode='" + dicTypeCode + '\'' +
                ", dicTypeName='" + dicTypeName + '\'' +
                '}';
    }
}
