package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabDetailsResultBean {
	private LabDetailsBean labDetails;
	
	private String errorCode;
	private String errorDescription;

	public LabDetailsBean getLabDetails() {
		return labDetails;
	}
	public void setLabDetails(LabDetailsBean labDetails) {
		this.labDetails = labDetails;
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