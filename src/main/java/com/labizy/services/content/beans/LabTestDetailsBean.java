package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestDetailsBean extends LabTestBean{
	
	private String rank;
	private String freeText;
	
	private String whyIsThisTest;
	private String whatDoesItMeasure;
	private String HowDoesItAffectYou;
	
	private String whatPrecautionsPreventionsToConsider;
	private String readInFeaturedBlog;

	private ImageBean mediumSizeImage1Url;
	private ImageBean mediumSizeImage2Url;
	private ImageBean mediumSizeImage3Url;

	private ImageBean largeSizeImage;
	
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
	public String getHowDoesItAffectYou() {
		return HowDoesItAffectYou;
	}
	public void setHowDoesItAffectYou(String howDoesItAffectYou) {
		HowDoesItAffectYou = howDoesItAffectYou;
	}
	public String getWhatPrecautionsPreventionsToConsider() {
		return whatPrecautionsPreventionsToConsider;
	}
	public void setWhatPrecautionsPreventionsToConsider(
			String whatPrecautionsPreventionsToConsider) {
		this.whatPrecautionsPreventionsToConsider = whatPrecautionsPreventionsToConsider;
	}
	public String getReadInFeaturedBlog() {
		return readInFeaturedBlog;
	}
	public void setReadInFeaturedBlog(String readInFeaturedBlog) {
		this.readInFeaturedBlog = readInFeaturedBlog;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getFreeText() {
		return freeText;
	}
	public void setFreeText(String freeText) {
		this.freeText = freeText;
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