package com.bcb.bean.dto.statistics;

public class FinancingLanagerMessageDto {
	
	private String LanagerType;
	
	private String title;
	
	private String Attentions;
	
	public FinancingLanagerMessageDto() {}

	public FinancingLanagerMessageDto(String lanagerType, String title, String attentions) {
		LanagerType = lanagerType;
		this.title = title;
		Attentions = attentions;
	}

	public String getLanagerType() {
		return LanagerType;
	}

	public void setLanagerType(String lanagerType) {
		LanagerType = lanagerType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAttentions() {
		return Attentions;
	}

	public void setAttentions(String attentions) {
		Attentions = attentions;
	}
	
}
