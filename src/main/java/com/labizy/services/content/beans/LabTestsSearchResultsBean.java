package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestsSearchResultsBean {
	private SearchResultsSummaryBean resultSummary;
	
	@JsonProperty("labTests")
	private ArrayList<LabTestBean> labTests;
	
	private String errorCode;
	private String errorDescription;

	public SearchResultsSummaryBean getResultSummary() {
		return resultSummary;
	}
	public void setResultSummary(SearchResultsSummaryBean resultSummary) {
		this.resultSummary = resultSummary;
	}
	public ArrayList<LabTestBean> getLabTests() {
		return labTests;
	}
	public void setLabTests(ArrayList<LabTestBean> labTests) {
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