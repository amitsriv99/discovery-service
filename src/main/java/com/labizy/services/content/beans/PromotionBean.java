package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionBean {
	private String promoType;
	private String promoValue;

	public String getPromoType() {
		return promoType;
	}
	public void setPromoType(String promoType) {
		this.promoType = promoType;
	}
	public String getPromoValue() {
		return promoValue;
	}
	public void setPromoValue(String promoValue) {
		this.promoValue = promoValue;
	}
}
