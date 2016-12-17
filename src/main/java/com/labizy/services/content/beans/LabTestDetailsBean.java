package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestDetailsBean extends LabTestBean{
	private LabTestImageBean image;
	private String whyIsThisTest;
	private String whatDoesItMeasure;
	private String whatDoTheResultsMean;
	
	@JsonProperty("whatPrecautionsPreventionsToConsider")
	private ArrayList<String> whatPrecautionsPreventionsToConsider;
	private String readInFeaturedBlog;
	
	public LabTestImageBean getImage() {
		return image;
	}
	public void setImage(LabTestImageBean image) {
		this.image = image;
	}
	public String getWhyIsThisTest() {
		return whyIsThisTest;
	}
	public void setWhyIsThisTest(String whyIsThisTest) {
		this.whyIsThisTest = whyIsThisTest;
	}
	public String getWhatDoesItMeasure() {
		return whatDoesItMeasure;
	}
	public void setWhatDoesItMeasure(String whatDoesItMeasure) {
		this.whatDoesItMeasure = whatDoesItMeasure;
	}
	public String getWhatDoTheResultsMean() {
		return whatDoTheResultsMean;
	}
	public void setWhatDoTheResultsMean(String whatDoTheResultsMean) {
		this.whatDoTheResultsMean = whatDoTheResultsMean;
	}
	public ArrayList<String> getWhatPrecautionsPreventionsToConsider() {
		return whatPrecautionsPreventionsToConsider;
	}
	public void setWhatPrecautionsPreventionsToConsider(ArrayList<String> whatPrecautionsPreventionsToConsider) {
		this.whatPrecautionsPreventionsToConsider = whatPrecautionsPreventionsToConsider;
	}
	public String getReadInFeaturedBlog() {
		return readInFeaturedBlog;
	}
	public void setReadInFeaturedBlog(String readInFeaturedBlog) {
		this.readInFeaturedBlog = readInFeaturedBlog;
	}
}