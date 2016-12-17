package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabsBean {
	private String id;
	private String type;
	private String name;
	private LabTestImageBean image;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public LabTestImageBean getImage() {
		return image;
	}
	public void setImage(LabTestImageBean image) {
		this.image = image;
	}
}
