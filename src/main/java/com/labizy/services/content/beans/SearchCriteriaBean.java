package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.labizy.services.content.utils.Constants;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchCriteriaBean {
	private String productId;
	private String productName;
	private String productType;
	private String productSearchTags;

	private String labId;
	private String labName;
	private String labGroupName;

	
	private String offset;
	private String limit;
	private String sortBy;
	
	private boolean isLenient;
	private float latitude;
	private float longitude;
	private float radialSearchUnit;
	private String radialSearchUom;
	
	public SearchCriteriaBean(){
		isLenient = true;
		latitude = -1;
		longitude = -1;
	}
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductSearchTags() {
		return productSearchTags;
	}

	public void setProductSearchTags(String productSearchTags) {
		this.productSearchTags = productSearchTags;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getLabGroupName() {
		return labGroupName;
	}

	public void setLabGroupName(String labGroupName) {
		this.labGroupName = labGroupName;
	}

	public String getLabId() {
		return labId;
	}
	public void setLabId(String labId) {
		this.labId = labId;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public boolean isLenient() {
		return isLenient;
	}
	public void setLenient(boolean isLenient) {
		this.isLenient = isLenient;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public float getRadialSearchUnit() {
		return radialSearchUnit;
	}
	public void setRadialSearchUnit(float radialSearchUnit) {
		this.radialSearchUnit = radialSearchUnit;
	}
	public String getRadialSearchUom() {
		return radialSearchUom;
	}
	public void setRadialSearchUom(String radialSearchUom) {
		this.radialSearchUom = radialSearchUom;
	}
}