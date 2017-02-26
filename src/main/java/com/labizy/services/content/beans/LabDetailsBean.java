package com.labizy.services.content.beans;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabDetailsBean extends LabBean {
	private String rank;
	
	private String distanceFromPoi;
	private String distanceFromPoiUom;

	private ImageBean mediumSizeImage1Url;
	private ImageBean mediumSizeImage2Url;
	private ImageBean mediumSizeImage3Url;

	private ImageBean largeSizeImage;

	@JsonProperty("labTests")
	private List<LabTestWithPricingPromoBean> labTests;

	public List<LabTestWithPricingPromoBean> getLabTests() {
		return labTests;
	}

	public void setLabTests(List<LabTestWithPricingPromoBean> labTests) {
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

	public ImageBean getMediumSizeImage1Url() {
		return mediumSizeImage1Url;
	}

	public void setMediumSizeImage1Url(ImageBean mediumSizeImage1Url) {
		this.mediumSizeImage1Url = mediumSizeImage1Url;
	}

	public ImageBean getMediumSizeImage2Url() {
		return mediumSizeImage2Url;
	}

	public void setMediumSizeImage2Url(ImageBean mediumSizeImage2Url) {
		this.mediumSizeImage2Url = mediumSizeImage2Url;
	}

	public ImageBean getMediumSizeImage3Url() {
		return mediumSizeImage3Url;
	}

	public void setMediumSizeImage3Url(ImageBean mediumSizeImage3Url) {
		this.mediumSizeImage3Url = mediumSizeImage3Url;
	}

	public ImageBean getLargeSizeImage() {
		return largeSizeImage;
	}

	public void setLargeSizeImage(ImageBean largeSizeImage) {
		this.largeSizeImage = largeSizeImage;
	}
}
