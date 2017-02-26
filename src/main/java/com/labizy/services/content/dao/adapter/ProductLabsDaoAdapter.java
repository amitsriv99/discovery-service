package com.labizy.services.content.dao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.content.beans.GeoLocationBean;
import com.labizy.services.content.beans.LabDetailsBean;
import com.labizy.services.content.beans.LabDetailsResultBean;
import com.labizy.services.content.beans.LabTestDetailsBean;
import com.labizy.services.content.beans.LabTestDetailsResultBean;
import com.labizy.services.content.beans.ImageBean;
import com.labizy.services.content.beans.LabTestWithPricingPromoBean;
import com.labizy.services.content.beans.PromotionBean;
import com.labizy.services.content.beans.SearchCriteriaBean;
import com.labizy.services.content.beans.UnitPriceBean;
import com.labizy.services.content.dao.manager.ProductLabsDaoManager;
import com.labizy.services.content.exceptions.DataNotFoundException;
import com.labizy.services.content.exceptions.DatabaseConnectionException;
import com.labizy.services.content.exceptions.DiscoveryItemsNotFoundException;
import com.labizy.services.content.exceptions.DiscoveryItemsProcessingException;
import com.labizy.services.content.exceptions.QueryExecutionException;
import com.labizy.services.content.utils.CommonUtils;

public class ProductLabsDaoAdapter {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");
	
	private ProductLabsDaoManager productLabsDaoManager;
	private CommonUtils commonUtils;

	public void setProductLabsDaoManager(ProductLabsDaoManager productLabsDaoManager) {
		this.productLabsDaoManager = productLabsDaoManager;
	}

	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	private LabDetailsBean getLabDetails(Map<String, String> labDetailsMap){
		LabDetailsBean labDetailsBean = new LabDetailsBean();
		
		GeoLocationBean geoLocationBean = new GeoLocationBean();
		labDetailsBean.setGeoLocation(geoLocationBean);
		
		for(Map.Entry<String, String> entry : labDetailsMap.entrySet()){
			if(entry.getKey().equals("labId")){
				labDetailsBean.setLabId(entry.getValue());
			}

			if((entry.getKey().equals("name")) || (entry.getKey().equals("labName"))){
				labDetailsBean.setName(entry.getValue());
			}
			
			if((entry.getKey().equals("shortDescription")) || (entry.getKey().equals("labShortDescription"))){
				labDetailsBean.setShortDescription(entry.getValue());
			}
			
			if((entry.getKey().equals("groupName")) || (entry.getKey().equals("labGroupName"))){
				labDetailsBean.setGroup(entry.getValue());
			}

			if(entry.getKey().equals("parentLabId")){
				labDetailsBean.setParentLabId(entry.getValue());
			}

			if((entry.getKey().equals("status")) || (entry.getKey().equals("labStatus"))){
				labDetailsBean.setStatus(entry.getValue());
			}

			if((entry.getKey().equals("thumbnailImageUrl")) || (entry.getKey().equals("labThumbnailImageUrl"))){
				labDetailsBean.setThumbnailImageUrl(entry.getValue());
			}
						
			if(entry.getKey().equals("addressLine1")){
				labDetailsBean.setAddressLine1(entry.getValue());
			}

			if(entry.getKey().equals("addressLine2")){
				labDetailsBean.setAddressLine2(entry.getValue());
			}
			
			if(entry.getKey().equals("localityName")){
				labDetailsBean.setLocality(entry.getValue());
			}

			if(entry.getKey().equals("cityTownOrVillage")){
				labDetailsBean.setCity(entry.getValue());
			}

			if(entry.getKey().equals("landmark")){
				labDetailsBean.setLandmark(entry.getValue());
			}

			if(entry.getKey().equals("state")){
				labDetailsBean.setState(entry.getValue());
			}

			if(entry.getKey().equals("country")){
				labDetailsBean.setCountry(entry.getValue());
			}

			if(entry.getKey().equals("pinCode")){
				labDetailsBean.setPostalCode(entry.getValue());
			}

			if(entry.getKey().equals("latitude")){
				labDetailsBean.getGeoLocation().setLatitude(entry.getValue());
			}

			if(entry.getKey().equals("longitude")){
				labDetailsBean.getGeoLocation().setLongitude(entry.getValue());
			}
			
			if(entry.getKey().equals("distance")){
				if((! StringUtils.isEmpty(entry.getValue())) && (! "-1".equals(entry.getValue()))){
					labDetailsBean.setDistanceFromPoi(entry.getValue());
					labDetailsBean.setDistanceFromPoiUom(labDetailsMap.get("distanceUom"));
				} else{
					labDetailsBean.setDistanceFromPoi(null);
					labDetailsBean.setDistanceFromPoiUom(null);
				}
			}
			
			if((entry.getKey().equals("rank")) || (entry.getKey().equals("labRank"))){
				labDetailsBean.setRank(entry.getValue());
			}
			
			if(entry.getKey().equals("usefulTips")){
				labDetailsBean.setUsefulTips(entry.getValue());
			}
			
			if(entry.getKey().equals("externalReviewsUrl")){
				labDetailsBean.setExternalReviewsUrl(entry.getValue());
			}
			
			if(entry.getKey().equals("mediumSizeImage1Url")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labDetailsMap.get("mediumSizeImage1Text"));

				labDetailsBean.setMediumSizeImage1Url(imageBean);
			}

			if(entry.getKey().equals("mediumSizeImage2Url")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labDetailsMap.get("mediumSizeImage2Text"));

				labDetailsBean.setMediumSizeImage2Url(imageBean);
			}

			if(entry.getKey().equals("mediumSizeImage3Url")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labDetailsMap.get("mediumSizeImage3Text"));

				labDetailsBean.setMediumSizeImage3Url(imageBean);
			}

			if(entry.getKey().equals("largeSizeImageUrl")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labDetailsMap.get("largeSizeImageText"));

				labDetailsBean.setLargeSizeImage(imageBean);
			}
		}
		
		return labDetailsBean;
	}

	public List<LabDetailsBean> loadLabDetailsWithPricePromoInfo (SearchCriteriaBean searchCriteriaBean) 
			throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		List<LabDetailsBean> results = null;
		try {
			boolean isLooselyMatched = true;
			Map<String, String> searchCriteriaMap = new HashMap<String, String>();
			
			if(searchCriteriaBean != null){
				isLooselyMatched = searchCriteriaBean.isLenient();
				
				searchCriteriaMap.put("rankBy", "labs");
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductId())){
					searchCriteriaMap.put("productId", searchCriteriaBean.getProductId());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getProductIds())){
					searchCriteriaMap.put("productIds", searchCriteriaBean.getProductIds());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getProductName())){
					searchCriteriaMap.put("productName", searchCriteriaBean.getProductName());
				}
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductSearchTags())){
					searchCriteriaMap.put("searchTags", searchCriteriaBean.getProductSearchTags());
				}
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductType())){
					searchCriteriaMap.put("type", searchCriteriaBean.getProductType());
				}
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductSubType())){
					searchCriteriaMap.put("subType", searchCriteriaBean.getProductSubType());
				}

				if((searchCriteriaBean.getLatitude() != -1) && (searchCriteriaBean.getLongitude() != -1)){
					searchCriteriaMap.put("latitude", Float.toString(searchCriteriaBean.getLatitude()));
					searchCriteriaMap.put("longitude", Float.toString(searchCriteriaBean.getLongitude()));
					searchCriteriaMap.put("radialSearchUnit", Float.toString(searchCriteriaBean.getRadialSearchUnit()));
					searchCriteriaMap.put("radialSearchUom", searchCriteriaBean.getRadialSearchUom());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getLabId())){
					searchCriteriaMap.put("labId", searchCriteriaBean.getLabId());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getLabIds())){
					searchCriteriaMap.put("labIds", searchCriteriaBean.getLabIds());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getLabName())){
					searchCriteriaMap.put("labName", searchCriteriaBean.getLabName());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getLabGroupName())){
					searchCriteriaMap.put("groupName", searchCriteriaBean.getLabGroupName());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getLocalityName())){
					searchCriteriaMap.put("localityName", searchCriteriaBean.getLocalityName());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getCityTownOrVillage())){
					searchCriteriaMap.put("cityTownOrVillage", searchCriteriaBean.getCityTownOrVillage());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getState())){
					searchCriteriaMap.put("state", searchCriteriaBean.getState());
				}

				if(! StringUtils.isEmpty(searchCriteriaBean.getCountry())){
					searchCriteriaMap.put("country", searchCriteriaBean.getCountry());
				}
			}
			
			List<Map<String, String>> labsDetailsList = productLabsDaoManager.searchLabsProducts(searchCriteriaMap, isLooselyMatched);

			results = new ArrayList<LabDetailsBean>();

			if(appLogger.isInfoEnabled()){
				appLogger.info(labsDetailsList.toString());
			}

			String lastLabIdProcessed = null;
			LabDetailsBean labDetailsBean = null;
			LabTestWithPricingPromoBean labTest = null;
			List<LabTestWithPricingPromoBean> labTests = null;
			
			for (Map<String, String> resultMap : labsDetailsList) {
				if(StringUtils.isEmpty(lastLabIdProcessed)){
					lastLabIdProcessed = resultMap.get("labId");

					labDetailsBean = getLabDetails(resultMap);
					labTests = new ArrayList<LabTestWithPricingPromoBean>();
					labDetailsBean.setLabTests(labTests);
					
					results.add(labDetailsBean);
				}
				
				if(! lastLabIdProcessed.equals(resultMap.get("labId"))){
					lastLabIdProcessed = resultMap.get("labId");
					
					labDetailsBean = getLabDetails(resultMap);
					labTests = new ArrayList<LabTestWithPricingPromoBean>();
					labDetailsBean.setLabTests(labTests);
					results.add(labDetailsBean);
				}

				labTest = getLabTestDetails(resultMap);
				labTests.add(labTest);
			}
		} catch (DataNotFoundException e) {
			throw new DiscoveryItemsNotFoundException(e);
		} catch (QueryExecutionException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (DatabaseConnectionException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		return results;
	}
	
	public List<LabDetailsBean> loadLabDetails(SearchCriteriaBean searchCriteriaBean) 
									throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		List<LabDetailsBean> results = null;
		
		try {
			boolean isLooselyMatched = true;
			Map<String, String> searchCriteriaMap = new HashMap<String, String>();
			
			if(searchCriteriaBean != null){
				isLooselyMatched = searchCriteriaBean.isLenient();
				
				if((searchCriteriaBean.getLatitude() != -1) && (searchCriteriaBean.getLongitude() != -1)){
					searchCriteriaMap.put("latitude", Float.toString(searchCriteriaBean.getLatitude()));
					searchCriteriaMap.put("longitude", Float.toString(searchCriteriaBean.getLongitude()));
					searchCriteriaMap.put("radialSearchUnit", Float.toString(searchCriteriaBean.getRadialSearchUnit()));
					searchCriteriaMap.put("radialSearchUom", searchCriteriaBean.getRadialSearchUom());
				}
			}
			
			List<Map<String, String>> labsDetailsList = productLabsDaoManager.searchLabs(searchCriteriaMap, isLooselyMatched);

			results = new ArrayList<LabDetailsBean>();

			if(appLogger.isInfoEnabled()){
				appLogger.info(labsDetailsList.toString());
			}
			
			for (Map<String, String> resultMap : labsDetailsList) {
				LabDetailsBean labDetailsBean = getLabDetails(resultMap);
				
				results.add(labDetailsBean);
			}
		} catch (DataNotFoundException e) {
			throw new DiscoveryItemsNotFoundException(e);
		} catch (QueryExecutionException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (DatabaseConnectionException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		return results;
	}
	
	public LabDetailsResultBean loadLabDetails(String labId) 
									throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		LabDetailsResultBean labDetailsResultBean = null;
		Map<String, String> labDetailsMap =null;
		
		try {
			labDetailsMap = productLabsDaoManager.getLabDetails(labId);
			
			if(appLogger.isInfoEnabled()){
				appLogger.info(labDetailsMap.toString());
			}
			
			LabDetailsBean labDetailsBean = getLabDetails(labDetailsMap);
			
			labDetailsResultBean = new LabDetailsResultBean();
			labDetailsResultBean.setLabDetails(labDetailsBean);
		} catch (DataNotFoundException e) {
			throw new DiscoveryItemsNotFoundException(e);
		} catch (QueryExecutionException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (DatabaseConnectionException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		return labDetailsResultBean;
	}

	private LabTestWithPricingPromoBean getLabTestDetails(Map<String, String> labTestDetailsMap){
		LabTestWithPricingPromoBean labTestDetailsBean = new LabTestWithPricingPromoBean();

		
		for(Map.Entry<String, String> entry : labTestDetailsMap.entrySet()){
			if(entry.getKey().equals("productId")){
				labTestDetailsBean.setId(entry.getValue());
			}

			if((entry.getKey().equals("name")) || (entry.getKey().equals("productName"))){
				labTestDetailsBean.setName(entry.getValue());
			}

			if((entry.getKey().equals("type")) || (entry.getKey().equals("Producttype"))){
				labTestDetailsBean.setType(entry.getValue());
			}

			if((entry.getKey().equals("subType")) || (entry.getKey().equals("productSubType"))){
				labTestDetailsBean.setSubType(entry.getValue());
			}

			if((entry.getKey().equals("shortDescription")) || (entry.getKey().equals("productShortDescription"))){
				labTestDetailsBean.setShortDescription(entry.getValue());
			}

			if((entry.getKey().equals("searchTags")) || (entry.getKey().equals("productSearchTags"))){
				labTestDetailsBean.setTags(entry.getValue());
			}

			if((entry.getKey().equals("status")) || (entry.getKey().equals("productStatus"))){
				labTestDetailsBean.setStatus(entry.getValue());
			}

			if(entry.getKey().equals("isProduct")){
				labTestDetailsBean.setIsProduct(entry.getValue());
			}

			if(entry.getKey().equals("isPackage")){
				labTestDetailsBean.setIsPackage(entry.getValue());
			}

			if(entry.getKey().equals("isService")){
				labTestDetailsBean.setIsService(entry.getValue());
			}
			
			if(entry.getKey().equals("unitPrice")){
				UnitPriceBean unitPriceBean = new UnitPriceBean();
				
				unitPriceBean.setUnitPrice(entry.getValue());
				unitPriceBean.setCurrencyDenomination(labTestDetailsMap.get("currencyCode"));
				unitPriceBean.setUnitOfMeasurement(labTestDetailsMap.get("uom"));
				
				labTestDetailsBean.setUnitPrice(unitPriceBean);
			}

			if(entry.getKey().equals("promoValue")){
				PromotionBean promotionBean = new PromotionBean();
				
				promotionBean.setPromoValue(entry.getValue());
				promotionBean.setPromoType(labTestDetailsMap.get("promoType"));
				
				labTestDetailsBean.setPromotion(promotionBean);
			}
			
			if((entry.getKey().equals("thumbnailImageUrl")) || (entry.getKey().equals("productThumbnailImageUrl"))){
				labTestDetailsBean.setThumbnailImageUrl(entry.getValue());
			}

			if((entry.getKey().equals("rank")) || (entry.getKey().equals("productRank"))){
				labTestDetailsBean.setRank(entry.getValue());
			}

			if(entry.getKey().equals("freeText")){
				labTestDetailsBean.setFreeText(entry.getValue());
			}

			if((entry.getKey().equals("aboutThisLine1Type")) || 
					(entry.getKey().equals("aboutThisLine2Type")) || 
							(entry.getKey().equals("aboutThisLine3Type"))){
				String entryValue = entry.getValue();
				
				if("why?".equalsIgnoreCase(entryValue)){
					labTestDetailsBean.setWhatDoesItMeasure(labTestDetailsMap.get("about_this_line1"));
				}
				
				if("what?".equalsIgnoreCase(entryValue)){
					labTestDetailsBean.setWhatDoesItMeasure(labTestDetailsMap.get("about_this_line2"));
				}

				if("how?".equalsIgnoreCase(entryValue)){
					labTestDetailsBean.setWhatDoesItMeasure(labTestDetailsMap.get("about_this_line3"));
				}
			}

			if(entry.getKey().equals("usefulTips")){
				labTestDetailsBean.setWhatPrecautionsPreventionsToConsider(entry.getValue());
			}

			if(entry.getKey().equals("externalBlogUrl")){
				labTestDetailsBean.setReadInFeaturedBlog(entry.getValue());
			}

			if(entry.getKey().equals("mediumSizeImage1Url")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labTestDetailsMap.get("mediumSizeImage1Text"));

				labTestDetailsBean.setMediumSizeImage1Url(imageBean);
			}

			if(entry.getKey().equals("mediumSizeImage2Url")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labTestDetailsMap.get("mediumSizeImage2Text"));

				labTestDetailsBean.setMediumSizeImage2Url(imageBean);
			}

			if(entry.getKey().equals("mediumSizeImage3Url")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labTestDetailsMap.get("mediumSizeImage3Text"));

				labTestDetailsBean.setMediumSizeImage3Url(imageBean);
			}

			if(entry.getKey().equals("largeSizeImageUrl")){
				ImageBean imageBean = new ImageBean();
				imageBean.setImageUrl(entry.getValue());
				imageBean.setAltText(labTestDetailsMap.get("largeSizeImageText"));

				labTestDetailsBean.setLargeSizeImage(imageBean);
			}
		}
		
		return labTestDetailsBean;
	}
	
	public List<LabTestDetailsBean> loadLabTestDetails(SearchCriteriaBean searchCriteriaBean) 
			throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{

		List<LabTestDetailsBean> results = null;
		
		try {
			boolean isLooselyMatched = true;
			Map<String, String> searchCriteriaMap = new HashMap<String, String>();
		
			if(searchCriteriaBean != null){
				isLooselyMatched = searchCriteriaBean.isLenient();
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductSearchTags())){
					searchCriteriaMap.put("searchTags", searchCriteriaBean.getProductSearchTags());
				}
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductType())){
					searchCriteriaMap.put("type", searchCriteriaBean.getProductType());
				}
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductName())){
					searchCriteriaMap.put("name", searchCriteriaBean.getProductName());
				}
				
				if(! StringUtils.isEmpty(searchCriteriaBean.getProductId())){
					searchCriteriaMap.put("productId", searchCriteriaBean.getProductId());
				}
			}
			
			List<Map<String, String>> productDetailsList = productLabsDaoManager.searchProducts(searchCriteriaMap, isLooselyMatched);
		
			results = new ArrayList<LabTestDetailsBean>();
			
			if(appLogger.isInfoEnabled()){
				appLogger.info(productDetailsList.toString());
			}
		
			for (Map<String, String> resultMap : productDetailsList) {
				LabTestDetailsBean labDetailsBean = getLabTestDetails(resultMap);
				
				results.add(labDetailsBean);
			}
		} catch (DataNotFoundException e) {
			throw new DiscoveryItemsNotFoundException(e);
		} catch (QueryExecutionException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (DatabaseConnectionException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		return results;
	}

	public LabTestDetailsResultBean loadLabTestDetailsBean(String productId)
										throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		LabTestDetailsResultBean labTestDetailsResultBean = null;
		
		Map<String, String> labTestDetailsMap =null;
		
		try {
			labTestDetailsMap = productLabsDaoManager.getProductDetails(productId);
			
			if(appLogger.isInfoEnabled()){
				appLogger.info(labTestDetailsMap.toString());
			}
			
			LabTestDetailsBean labTestDetailsBean = getLabTestDetails(labTestDetailsMap);
			
			labTestDetailsResultBean = new LabTestDetailsResultBean();
			labTestDetailsResultBean.setLabTestDetails(labTestDetailsBean);
		} catch (DataNotFoundException e) {
			throw new DiscoveryItemsNotFoundException(e);
		} catch (QueryExecutionException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (DatabaseConnectionException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		return labTestDetailsResultBean;
	}
}