package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResultsSummaryBean {

	private String offset;
	private String limit;
	private String sortBy;
	private String totalNumberOfRecordsFound;
	private String numberOfRecordsReturned;
	
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
	public String getTotalNumberOfRecordsFound() {
		return totalNumberOfRecordsFound;
	}
	public void setTotalNumberOfRecordsFound(String totalNumberOfRecordsFound) {
		this.totalNumberOfRecordsFound = totalNumberOfRecordsFound;
	}
	public String getNumberOfRecordsReturned() {
		return numberOfRecordsReturned;
	}
	public void setNumberOfRecordsReturned(String numberOfRecordsReturned) {
		this.numberOfRecordsReturned = numberOfRecordsReturned;
	}
}
