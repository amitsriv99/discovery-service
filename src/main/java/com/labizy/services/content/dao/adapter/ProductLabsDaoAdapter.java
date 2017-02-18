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
				labDetailsBean.setMediumSizeImage1Url(entry.getValue());
			}
			
			if(entry.getKey().equals("mediumSizeImage1Text")){
				labDetailsBean.setMediumSizeImage1Text(entry.getValue());
			}
			
			if(entry.getKey().equals("mediumSizeImage2Url")){
				labDetailsBean.setMediumSizeImage2Url(entry.getValue());
			}
			
			if(entry.getKey().equals("mediumSizeImage2Text")){
				labDetailsBean.setMediumSizeImage2Text(entry.getValue());
			}

			if(entry.getKey().equals("mediumSizeImage3Url")){
				labDetailsBean.setMediumSizeImage3Url(entry.getValue());
			}
			
			if(entry.getKey().equals("mediumSizeImage3Text")){
				labDetailsBean.setMediumSizeImage3Text(entry.getValue());
			}

			if(entry.getKey().equals("largeSizeImageUrl")){
				labDetailsBean.setLargeSizeImageUrl(entry.getValue());
			}
			
			if(entry.getKey().equals("largeSizeImageText")){
				labDetailsBean.setLargeSizeImageText(entry.getValue());
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
	
	/*
	public UserProfileDetailsResultBean getUserProfileDetails(String userId, boolean isPrimaryProfile) throws ServiceException, UserDoesNotExistException {
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "ProductLabsDaoAdapter.getUserProfileDetails()");
		}
		
		if(appLogger.isInfoEnabled()){
			appLogger.info("Retrieving Profile of UserId : {}", userId);
		}

		try {
			if(appLogger.isDebugEnabled()){
				appLogger.debug("Fetching User Profile Info..");
			}
			Map<String, String> userProfileMap = productLabsDaoManager.getUserProfileDetails(userId, isPrimaryProfile, null);
			userProfileDetailsResultBean = new UserProfileDetailsResultBean();
			UserProfileBean userProfileBean = getUserProfileDetails(userProfileMap);
			userProfileDetailsResultBean.setUserProfile(userProfileBean);
			
			UserCredentialsBean userLoginCredentialsBean = getUserLoginDetails(userProfileMap);
			userProfileDetailsResultBean.setUserLogin(userLoginCredentialsBean);

			if(appLogger.isDebugEnabled()){
				appLogger.debug("Fetching User Contact Info..");
			}
			List<Map<String, String>> contactList = productLabsDaoManager.getUserContactDetails(userId);
			UserContactsDetailsBean userContactsDetailsBean = getUserContactDetails(contactList);
			userProfileDetailsResultBean.setContactDetails(userContactsDetailsBean);
			
			if(appLogger.isDebugEnabled()){
				appLogger.debug("Fetching User Address Info..");
			}
			List<Map<String, String>> addressList = productLabsDaoManager.getUserAddressDetails(userId);
			UserAddressDetailsBean userAddressDetailsBean = getUserAddressDetails(addressList);
			userProfileDetailsResultBean.setAddressDetails(userAddressDetailsBean);

		} catch (DataNotFoundException e) {
			appLogger.error(e.getMessage());
			throw new UserDoesNotExistException(e);
		} catch (QueryExecutionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		} catch (DatabaseConnectionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		}
		
		return userProfileDetailsResultBean;
	}

	private UserProfileBean getUserProfileDetails(Map<String, String> userProfileMap) {
		UserProfileBean userProfileBean = new UserProfileBean();
		for(Map.Entry<String, String> entry : userProfileMap.entrySet()){
			if(entry.getKey().equals("userId")){
				userProfileBean.setUserId(entry.getValue());
			}
			if(entry.getKey().equals("title")){
				userProfileBean.setTitle(entry.getValue());
			}
			if(entry.getKey().equals("firstName")){
				userProfileBean.setFirstName(entry.getValue());
			}
			if(entry.getKey().equals("middleName")){
				userProfileBean.setMiddleName(entry.getValue());
			}
			if(entry.getKey().equals("lastName")){
				userProfileBean.setLastName(entry.getValue());
			}
			if(entry.getKey().equals("sex")){
				userProfileBean.setSex(entry.getValue());
			}
			if(entry.getKey().equals("dateOfBirth")){
				userProfileBean.setDateOfBirth(entry.getValue());
			}
			if(entry.getKey().equals("maritalStatus")){
				userProfileBean.setMaritalStatus(entry.getValue());
			}
			if(entry.getKey().equals("profilePictureUrl")){
				userProfileBean.setProfilePicture(entry.getValue());
			}
			if(entry.getKey().equals("isPrimaryProfile")){
				userProfileBean.setTitle(entry.getValue());
			}
		}

		return userProfileBean;
	}

	private UserCredentialsBean getUserLoginDetails(Map<String, String> userProfileMap){
		UserCredentialsBean userLoginCredentialsBean = new UserCredentialsBean();
		
		for(Map.Entry<String, String> entry : userProfileMap.entrySet()){
			if(entry.getKey().equals("emailId")){
				userLoginCredentialsBean.setEmailId(entry.getValue());
			}
			if(entry.getKey().equals("status")){
				userLoginCredentialsBean.setStatus(entry.getValue());
			}
			/*
			if(entry.getKey().equals("password")){
				userLoginCredentialsBean.setPassword(entry.getValue());
			}
			*/
/*		}
		
		return userLoginCredentialsBean;
	}
	
	private UserAddressDetailsBean getUserAddressDetails(List<Map<String, String>> addressList) {
		ArrayList<UserAddressBean> userAddressList = new ArrayList<UserAddressBean>(); 
		
		for (Map<String, String> addressMap : addressList) {
			UserAddressBean userAddressBean = new UserAddressBean(); 
			for(Map.Entry<String, String> entry : addressMap.entrySet()){
				if(entry.getKey().equals("addressId")){
					userAddressBean.setAddressId(entry.getValue());
				}
				if(entry.getKey().equals("houseOrFlatNumber")){
					userAddressBean.setHouseOrFlatNumber(entry.getValue());
				}
				if(entry.getKey().equals("houseOrApartmentName")){
					userAddressBean.setHouseOrApartmentName(entry.getValue());
				}
				if(entry.getKey().equals("streetAddress")){
					userAddressBean.setStreetAddress(entry.getValue());
				}
				if(entry.getKey().equals("localityName")){
					userAddressBean.setLocalityName(entry.getValue());
				}
				if(entry.getKey().equals("cityTownOrVillage")){
					userAddressBean.setCity(entry.getValue());
				}
				if(entry.getKey().equals("state")){
					userAddressBean.setState(entry.getValue());
				}
				if(entry.getKey().equals("country")){
					userAddressBean.setCountry(entry.getValue());
				}
				if(entry.getKey().equals("pinCode")){
					userAddressBean.setPinCode(entry.getValue());
				}
				if(entry.getKey().equals("landmark")){
					userAddressBean.setLandmark(entry.getValue());
				}
				if(entry.getKey().equals("latitude")){
					userAddressBean.setLatitude(entry.getValue());
				}
				if(entry.getKey().equals("longitude")){
					userAddressBean.setLongitude(entry.getValue());
				}
				if(entry.getKey().equals("isPrimaryAddress")){
					userAddressBean.setIsPrimaryAddress(entry.getValue());
				}
				if(entry.getKey().equals("isBillingAddress")){
					userAddressBean.setIsBillingAddress(entry.getValue());
				}
			}
			userAddressList.add(userAddressBean);
		}
		
		UserAddressDetailsBean userAddressDetailsBean = new UserAddressDetailsBean();
		userAddressDetailsBean.setAddressDetail(userAddressList);
		
		return userAddressDetailsBean;
	}

	private UserContactsDetailsBean getUserContactDetails(List<Map<String, String>> contactList) {
		ArrayList<UserContactBean> userContactList = new ArrayList<UserContactBean>(); 
		
		for (Map<String, String> contactMap : contactList) {
			UserContactBean userContactBean = new UserContactBean(); 
			for(Map.Entry<String, String> entry : contactMap.entrySet()){
				if(entry.getKey().equals("contactId")){
					userContactBean.setContactId(entry.getValue());
				}
				if(entry.getKey().equals("contactType")){
					userContactBean.setContactType(entry.getValue());
				}
				if(entry.getKey().equals("contactDetail")){
					userContactBean.setContactDetail(entry.getValue());
				}
				if(entry.getKey().equals("isPrimaryContact")){
					userContactBean.setIsPrimaryContact(entry.getValue());
				}
			}
			userContactList.add(userContactBean);
		}
		
		UserContactsDetailsBean userContactsDetailsBean = new UserContactsDetailsBean();
		userContactsDetailsBean.setContactDetail(userContactList);
		
		return userContactsDetailsBean;
	}
	
	public void deleteUserAndProfile(String userId) throws ServiceException {
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "ProductLabsDaoAdapter.deleteUserAndProfile()");
		}
		
		try {
			if(appLogger.isInfoEnabled()){
				appLogger.info("Deleting UserId : {}", userId);
			}

			productLabsDaoManager.deleteUser(userId);
		} catch (DataNotFoundException e) {
			appLogger.warn(e.getMessage());
		} catch (QueryExecutionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		} catch (DatabaseConnectionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		}
		
		return;
	}
*/

}