package com.labizy.services.content.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestsSearchResultsBean {
	private SearchResultsSummaryBean resultSummary;
	
	@JsonProperty("labTests")
	private List<LabTestDetailsBean> labTests;
	
	private String errorCode;
	private String errorDescription;

	public SearchResultsSummaryBean getResultSummary() {
		return resultSummary;
	}
	public void setResultSummary(SearchResultsSummaryBean resultSummary) {
		this.resultSummary = resultSummary;
	}
	public List<LabTestDetailsBean> getLabTests() {
		return labTests;
	}
	public void setLabTests(List<LabTestDetailsBean> labTests) {
		this.labTests = labTests;
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