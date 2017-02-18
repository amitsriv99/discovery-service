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
import com.labizy.services.content.beans.SearchCriteriaBean;
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

			if(entry.getKey().equals("name")){
				labDetailsBean.setName(entry.getValue());
			}

			if(entry.getKey().equals("groupName")){
				labDetailsBean.setGroup(entry.getValue());
			}

			if(entry.getKey().equals("parentLabId")){
				labDetailsBean.setParentLabId(entry.getValue());
			}

			if(entry.getKey().equals("status")){
				labDetailsBean.setStatus(entry.getValue());
			}

			if(entry.getKey().equals("thumbnailImageUrl")){
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
			
			if(entry.getKey().equals("rank")){
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

	public List<LabDetailsBean> loadLabDetailsBean(SearchCriteriaBean searchCriteriaBean) 
									throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		List<LabDetailsBean> results = null;
		
		try {
			boolean isLooselyMatched = true;
			Map<String, String> searchCriteriaMap = new HashMap<String, String>();
			
			if(searchCriteriaBean != null){
				isLooselyMatched = searchCriteriaBean.isLenient();
				
				searchCriteriaMap.put("latitude", Float.toString(searchCriteriaBean.getLatitude()));
				searchCriteriaMap.put("longitude", Float.toString(searchCriteriaBean.getLongitude()));
				searchCriteriaMap.put("radialSearchUnit", Float.toString(searchCriteriaBean.getRadialSearchUnit()));
				searchCriteriaMap.put("radialSearchUom", searchCriteriaBean.getRadialSearchUom());
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
	
	public LabDetailsResultBean loadLabDetailsBean(String labId) 
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

	private LabTestDetailsBean getLabTestDetails(Map<String, String> labTestDetailsMap){
		LabTestDetailsBean labTestDetailsBean = new LabTestDetailsBean();

		for(Map.Entry<String, String> entry : labTestDetailsMap.entrySet()){
			if(entry.getKey().equals("productId")){
				labTestDetailsBean.setId(entry.getValue());
			}

			if(entry.getKey().equals("name")){
				labTestDetailsBean.setName(entry.getValue());
			}

			if(entry.getKey().equals("type")){
				labTestDetailsBean.setType(entry.getValue());
			}

			if(entry.getKey().equals("subType")){
				labTestDetailsBean.setSubType(entry.getValue());
			}

			if(entry.getKey().equals("shortDescription")){
				labTestDetailsBean.setShortDescription(entry.getValue());
			}

			if(entry.getKey().equals("searchTags")){
				labTestDetailsBean.setTags(entry.getValue());
			}

			if(entry.getKey().equals("status")){
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

			if(entry.getKey().equals("thumbnailImageUrl")){
				labTestDetailsBean.setThumbnailImageUrl(entry.getValue());
			}

			if(entry.getKey().equals("rank")){
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
	
	public List<LabTestDetailsBean> loadLabTestDetailsBean(SearchCriteriaBean searchCriteriaBean) 
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