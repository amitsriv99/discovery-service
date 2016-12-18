package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestWithPricingPromoBean extends LabTestWithPricingBean{
	private PromotionBean promotion;

	public PromotionBean getPromotion() {
		return promotion;
	}

	public void setPromotion(PromotionBean promotion) {
		this.promotion = promotion;
	}
}
