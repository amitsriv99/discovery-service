package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestDetailsResultBean {
	private LabTestDetailsBean labTestDetails;
	
	private String errorCode;
	private String errorDescription;

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
	public LabTestDetailsBean getLabTestDetails() {
		return labTestDetails;
	}
	public void setLabTestDetails(LabTestDetailsBean labTestDetails) {
		this.labTestDetails = labTestDetails;
	}
}