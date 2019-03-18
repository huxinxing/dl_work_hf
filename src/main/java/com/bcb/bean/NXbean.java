package com.bcb.bean;

import java.util.List;

public class NXbean {
	List<NXbeanItem> data;
	String code;
	String msg;

	public List<NXbeanItem> getData() {
		return data;
	}

	public void setData(List<NXbeanItem> data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}