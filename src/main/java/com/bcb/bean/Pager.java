package com.bcb.bean;

import java.util.List;
import java.util.Map;


public class Pager {
	public enum OrderType{
		asc, desc
	}

	public Pager(){

	}
	public Pager(Integer pageNo, Integer pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
	public static final Integer MAX_PAGE_SIZE = 500;

	private Integer pageNo = 1;
	private Integer pageSize = 20;
	private Integer totalCount = 0;
	private Integer pageCount = 0;
	private Map<String,String> queryBuilder;
	private String orderBy = null;
	private OrderType orderType = null;
	private List<?> list;


	public Integer getPageNo() {
			return pageNo;
		}

	public void setPageNumber(Integer pageNumber) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
			return pageSize;
		}

	public void setPageSize(Integer pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		} else if(pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}
		this.pageSize = pageSize;
	}
		
	public Integer getTotalCount() {
			return totalCount;
		}

	public void setTotalCount(Integer totalCount) {
			this.totalCount = totalCount;
		}

	public Integer getPageCount() {
		pageCount = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			pageCount ++;
		}
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
			this.pageCount = pageCount;
		}

	public String getOrderBy() {
			return orderBy;
		}

	public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}
		
	public OrderType getOrderType() {
			return orderType;
		}

	public void setOrderType(OrderType orderType) {
			this.orderType = orderType;
		}

	public List<?> getList() {
			return list;
		}

	public void setList(List<?> list) {
			this.list = list;
		}

	public Map<String, String> getQueryBuilder() {
			return queryBuilder;
		}

	public void setQueryBuilder(Map<String, String> queryBuilder) {
			this.queryBuilder = queryBuilder;
		}
}
