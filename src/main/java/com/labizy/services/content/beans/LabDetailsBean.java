package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabDetailsBean extends LabBean {
	
	@JsonProperty("labTests")
	private ArrayList<LabTestWithPricingPromoBean> labTests;

	public ArrayList<LabTestWithPricingPromoBean> getLabTests() {
		return labTests;
	}

	public void setLabTests(ArrayList<LabTestWithPricingPromoBean> labTests) {
		this.labTests = labTests;
	}
}
