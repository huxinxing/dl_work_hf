package com.bcb.bean.dto.statistics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LanguageDto {
	
	List<Map<String, String>> languageEnum;
	
	List<FinancingLanagerMessageDto> financingLanagerMessageDto;

	public LanguageDto() {}

	public LanguageDto(List<Map<String, String>> languageEnum,
			List<FinancingLanagerMessageDto> financingLanagerMessageDto) {
		this.languageEnum = languageEnum;
		this.financingLanagerMessageDto = financingLanagerMessageDto;
	}

	public List<Map<String, String>> getLanguageEnum() {
		return languageEnum;
	}

	public void setLanguageEnum(List<Map<String, String>> languageEnum) {
		this.languageEnum = languageEnum;
	}

	public List<FinancingLanagerMessageDto> getFinancingLanagerMessageDto() {
		return financingLanagerMessageDto;
	}

	public void setFinancingLanagerMessageDto(List<FinancingLanagerMessageDto> financingLanagerMessageDto) {
		this.financingLanagerMessageDto = financingLanagerMessageDto;
	}

}
