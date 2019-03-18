package com.bcb.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author dana
 * @Title: FinancingCnInfo
 * @Package com.df.entity
 * @Description: 理财英文信息表
 * @date 4/24/201810:08 AM
 */
@Entity
@Table(name = "financing_en_info")
public class FinancingEnInfo {
    private static final long serialVersionUID = 5271367905881878559L;
    @Id
    @ApiModelProperty(value = "理财uuid",dataType = "String",required = false)
    @Column(name = "FinancingUuid", unique = true, nullable = false)
    private String financingUuid;

    @ApiModelProperty(value = "理财标题",required = true)
    @Column(name = "Title")
    private String title;

    @ApiModelProperty(value = "理财标签",required = true)
    @Column(name ="Tag")
    private String tag;
    
    @ApiModelProperty(value = "注意事件",required = true)
    @Column(name="Attentions")
    private String attentions;     // 注意事件

	@ApiModelProperty(value = "理财列表背景墙url",required = true)
	@Column(name="FinancingIconUrl")
	private String financingIconUrl;     // 理财列表背景墙url

	@ApiModelProperty(value = "理财详情背景墙url",required = true)
	@Column(name="FinancingDetailUrl")
	private String financingDetailUrl;     // 理财详情背景墙url

	public String getFinancingIconUrl() {
		return financingIconUrl;
	}

	public void setFinancingIconUrl(String financingIconUrl) {
		this.financingIconUrl = financingIconUrl;
	}

	public String getFinancingDetailUrl() {
		return financingDetailUrl;
	}

	public void setFinancingDetailUrl(String financingDetailUrl) {
		this.financingDetailUrl = financingDetailUrl;
	}

	public String getFinancingUuid() {
		return financingUuid;
	}

	public void setFinancingUuid(String financingUuid) {
		this.financingUuid = financingUuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAttentions() {
		return attentions;
	}

	public void setAttentions(String attentions) {
		this.attentions = attentions;
	}
}