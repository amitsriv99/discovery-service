package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabDetailsBean extends LabBean {
	private String rank;
	
	private String distanceFromPoi;
	private String distanceFromPoiUom;
	
	@JsonProperty("labTests")
	private ArrayList<LabTestWithPricingPromoBean> labTests;

	public ArrayList<LabTestWithPricingPromoBean> getLabTests() {
		return labTests;
	}

	public void setLabTests(ArrayList<LabTestWithPricingPromoBean> labTests) {
		this.labTests = labTests;
	}

	public String getDistanceFromPoi() {
		return distanceFromPoi;
	}

	public void setDistanceFromPoi(String distanceFromPoi) {
		this.distanceFromPoi = distanceFromPoi;
	}

	public String getDistanceFromPoiUom() {
		return distanceFromPoiUom;
	}

	public void setDistanceFromPoiUom(String distanceFromPoiUom) {
		this.distanceFromPoiUom = distanceFromPoiUom;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
}
