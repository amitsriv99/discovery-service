package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OurPartnerLabsBean {

	@JsonProperty("labs")
	private ArrayList<LabsBean> labs;

	public ArrayList<LabsBean> getLabs() {
		return labs;
	}
	public void setLabs(ArrayList<LabsBean> labs) {
		this.labs = labs;
	}
}
