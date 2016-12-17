package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchCriteriaBean {
	private String labTestId;
	private String labId;
	
	private String offset;
	private String limit;
	private String sortBy;
	
	public String getLabTestId() {
		return labTestId;
	}
	public void setLabTestId(String labTestId) {
		this.labTestId = labTestId;
	}
	public String getLabId() {
		return labId;
	}
	public void setLabId(String labId) {
		this.labId = labId;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
}