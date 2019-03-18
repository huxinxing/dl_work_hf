package com.bcb.bean.dto.statistics;

import lombok.Data;

import java.util.List;

@Data
public class StatUserDto {
	
	private String userId;   //用户id
	
	private String DisPlayName;   //昵称
	
	private List<String> Address;   //钱包地址
	
	private String grade;   //代理等级
	
	private String ReturnPointColumn;   //代理返点比列

}
