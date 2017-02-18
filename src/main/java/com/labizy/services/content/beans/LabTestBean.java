package com.labizy.services.content.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestBean {
	private String id;
	
	@JsonProperty("labIds")
	private ArrayList<String> labIds;
	
	private String type;
	private String name;
	private String subType;
	private String shortDescription;
	private String status;
	private String tags;
	private String isProduct;
	private String isService;
	private String isPackage;
	private String thumbnailImageUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<String> getLabIds() {
		return labIds;
	}
	public void setLabIds(ArrayList<String> labIds) {
		this.labIds = labIds;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getIsProduct() {
		return isProduct;
	}
	public void setIsProduct(String isProduct) {
		this.isProduct = isProduct;
	}
	public String getIsService() {
		return isService;
	}
	public void setIsService(String isService) {
		this.isService = isService;
	}
	public String getIsPackage() {
		return isPackage;
	}
	public void setIsPackage(String isPackage) {
		this.isPackage = isPackage;
	}
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}