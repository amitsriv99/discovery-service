package com.labizy.services.content.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestWithPricingBean extends LabTestBean{
	private UnitPriceBean unitPrice;

	public UnitPriceBean getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(UnitPriceBean unitPrice) {
		this.unitPrice = unitPrice;
	}
}
