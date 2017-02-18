package com.labizy.services.content.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabsSearchResultsBean {

	private SearchResultsSummaryBean resultSummary;
	
	@JsonProperty("labs")
	private List<LabDetailsBean> labs;
	
	private String errorCode;
	private String errorDescription;

	public List<LabDetailsBean> getLabs() {
		return labs;
	}
	public void setLabs(List<LabDetailsBean> labs) {
		this.labs = labs;
	}
	public SearchResultsSummaryBean getResultSummary() {
		return resultSummary;
	}
	public void setResultSummary(SearchResultsSummaryBean resultSummary) {
		this.resultSummary = resultSummary;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}