package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MostPopularLabTestsBean {

	@JsonProperty("labTests")
	private ArrayList<LabTestBean> labTests;

	public ArrayList<LabTestBean> getLabTests() {
		return labTests;
	}

	public void setLabTests(ArrayList<LabTestBean> labTests) {
		this.labTests = labTests;
	}
}
