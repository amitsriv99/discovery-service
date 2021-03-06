package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabBean {
	  private String labId;
	  private String name;
	  private String group;
	  private String parentLabId;
	  private String status;
	  private String thumbnailImageUrl;
	  private String addressLine1;
	  private String addressLine2;
	  private String locality;
	  private String landmark;
	  private GeoLocationBean geoLocation;
	  private String postalCode;
	  private String city;
	  private String state;
	  private String country;
	  private String shortDescription;
	  
	  private String usefulTips;
	  private String externalReviewsUrl;
	  
	  @JsonProperty("labTestsIds")
	  private ArrayList<String> labTestsIds;

	public String getLabId() {
		return labId;
	}

	public void setLabId(String labId) {
		this.labId = labId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(String parentLabId) {
		this.parentLabId = parentLabId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}

	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public GeoLocationBean getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocationBean geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUsefulTips() {
		return usefulTips;
	}

	public void setUsefulTips(String usefulTips) {
		this.usefulTips = usefulTips;
	}

	public String getExternalReviewsUrl() {
		return externalReviewsUrl;
	}

	public void setExternalReviewsUrl(String externalReviewsUrl) {
		this.externalReviewsUrl = externalReviewsUrl;
	}

	public ArrayList<String> getLabTestsIds() {
		return labTestsIds;
	}

	public void setLabTestsIds(ArrayList<String> labTestsIds) {
		this.labTestsIds = labTestsIds;
	}
}
