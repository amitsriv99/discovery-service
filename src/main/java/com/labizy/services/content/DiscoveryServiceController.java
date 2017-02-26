package com.labizy.services.content;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labizy.services.content.beans.LabDetailsResultBean;
import com.labizy.services.content.beans.LabTestDetailsResultBean;
import com.labizy.services.content.beans.LabTestsSearchResultsBean;
import com.labizy.services.content.beans.LabsSearchResultsBean;
import com.labizy.services.content.beans.SearchCriteriaBean;
import com.labizy.services.content.beans.StatusBean;
import com.labizy.services.content.exceptions.DiscoveryItemsNotFoundException;
import com.labizy.services.content.utils.LabTestsCacheFactory;
import com.labizy.services.content.utils.Constants;
import com.labizy.services.content.utils.LabsCacheFactory;
import com.labizy.services.content.utils.RequestParamsValidator;

@RestController
public class DiscoveryServiceController {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");
	private static Logger traceLogger = LoggerFactory.getLogger("com.labizy.services.content.TraceLogger");

	private LabTestsCacheFactory labTestsCacheFactory;
	private LabsCacheFactory labsCacheFactory;

	private String getCommaDelimitedString(List<String> itemList){
		String items = null;
		
		if((itemList != null) && (itemList.size() > 0)){
			StringBuffer itremsBuff = new StringBuffer();
			
			Collections.sort(itemList);
			
			for (String item : itemList) {
				itremsBuff.append(",").append(item);
			}
			
			items = itremsBuff.substring(1);
		}
		
		return items;
	}
	
	private SearchCriteriaBean getSearchCriteriaFromRequestParams(MultiValueMap<String, String> requestParams){
		SearchCriteriaBean searchCriteriaBean = null;
		if((requestParams != null) && (! requestParams.isEmpty())){
			searchCriteriaBean = new SearchCriteriaBean();
			RequestParamsValidator requestParamsValidator = new RequestParamsValidator();
			
			String offset = requestParams.getFirst(Constants.OFFSET_QUERY_PARAM);
			searchCriteriaBean.setOffset(requestParamsValidator.validate(Constants.OFFSET_QUERY_PARAM, offset));
			
			String limit = requestParams.getFirst(Constants.LIMIT_QUERY_PARAM);
			searchCriteriaBean.setLimit(requestParamsValidator.validate(Constants.LIMIT_QUERY_PARAM, limit));

			String sortBy = requestParams.getFirst(Constants.SORT_BY_QUERY_PARAM);
			searchCriteriaBean.setSortBy(requestParamsValidator.validate(Constants.SORT_BY_QUERY_PARAM, sortBy));

			String isLenient = requestParams.getFirst(Constants.IS_LENIENT_PARAM);
			if(! StringUtils.isEmpty(isLenient)) {
				searchCriteriaBean.setLenient(requestParamsValidator.validateBoolean(Constants.IS_LENIENT_PARAM, isLenient));
			}else{
				searchCriteriaBean.setLenient(true);
			}
			
			String isIncludeProducts = requestParams.getFirst(Constants.INCLUDE_PRODUCTS_PARAM);
			if(! StringUtils.isEmpty(isIncludeProducts)) {
				searchCriteriaBean.setIncludeProducts(requestParamsValidator.validateBoolean(Constants.INCLUDE_PRODUCTS_PARAM, isIncludeProducts));
			}else{
				searchCriteriaBean.setIncludeProducts(false);
			}

			String isIncludeLabs = requestParams.getFirst(Constants.INCLUDE_LABS_PARAM);
			if(! StringUtils.isEmpty(isIncludeLabs)) {
				searchCriteriaBean.setIncludeLabs(requestParamsValidator.validateBoolean(Constants.INCLUDE_LABS_PARAM, isIncludeLabs));
			}else{
				searchCriteriaBean.setIncludeLabs(false);
			}

			List<String> productIdList = requestParams.get(Constants.PRODUCT_ID_PARAM);
			if((productIdList == null) || (productIdList.size() <= 1)){
				String productId = requestParams.getFirst(Constants.PRODUCT_ID_PARAM);
				if(! StringUtils.isEmpty(productId)){
					searchCriteriaBean.setProductId(productId);
				}
			}else{
				String productIds = getCommaDelimitedString(productIdList);
				if(! StringUtils.isEmpty(productIds)){
					searchCriteriaBean.setProductIds(productIds);
				}
			}
			
			String productName = requestParams.getFirst(Constants.PRODUCT_NAME_PARAM);
			if(! StringUtils.isEmpty(productName)){
				searchCriteriaBean.setProductName(productName);
			}

			String productType = requestParams.getFirst(Constants.PRODUCT_TYPE_PARAM);
			if(! StringUtils.isEmpty(productType)){
				searchCriteriaBean.setProductType(productType);
			}

			String productSubType = requestParams.getFirst(Constants.PRODUCT_SUB_TYPE_PARAM);
			if(! StringUtils.isEmpty(productSubType)){
				searchCriteriaBean.setProductSubType(productSubType);
			}
			
			String productSearchTags = requestParams.getFirst(Constants.PRODUCT_SEARCH_TAGS_PARAM);
			if(! StringUtils.isEmpty(productSearchTags)){
				searchCriteriaBean.setProductSearchTags(productSearchTags);
			}
			
			List<String> labIdList = requestParams.get(Constants.LAB_ID_PARAM);
			if((labIdList == null) || (labIdList.size() <= 1)){
				String labId = requestParams.getFirst(Constants.LAB_ID_PARAM);
				if(! StringUtils.isEmpty(labId)){
					searchCriteriaBean.setLabId(labId);
				}
			}else{
				String labIds = getCommaDelimitedString(labIdList);
				if(! StringUtils.isEmpty(labIds)){
					searchCriteriaBean.setLabIds(labIds);
				}
			}

			String labName = requestParams.getFirst(Constants.LAB_NAME_PARAM);
			if(! StringUtils.isEmpty(labName)){
				searchCriteriaBean.setLabName(labName);
			}

			String labGroupName = requestParams.getFirst(Constants.LAB_GROUP_NAME_PARAM);
			if(! StringUtils.isEmpty(labGroupName)){
				searchCriteriaBean.setLabGroupName(labGroupName);
			}
			
			String labLocalityName = requestParams.getFirst(Constants.LAB_LOCALITY_NAME_PARAM);
			if(! StringUtils.isEmpty(labLocalityName)){
				searchCriteriaBean.setLocalityName(labLocalityName);
			}
			
			String labCityTownVillage = requestParams.getFirst(Constants.LAB_CITY_TOWN_VILLAGE_PARAM);
			if(! StringUtils.isEmpty(labCityTownVillage)){
				searchCriteriaBean.setCityTownOrVillage(labCityTownVillage);
			}
			
			String labState = requestParams.getFirst(Constants.LAB_STATE_PARAM);
			if(! StringUtils.isEmpty(labState)){
				searchCriteriaBean.setState(labState);
			}
			
			String labCountry = requestParams.getFirst(Constants.LAB_COUNTRY_PARAM);
			if(! StringUtils.isEmpty(labCountry)){
				searchCriteriaBean.setCountry(labCountry);
			}
			
			float latitude = requestParamsValidator.validateGeoLocations(Constants.LATITUDE_PARAM, requestParams.getFirst(Constants.LATITUDE_PARAM));
			if(latitude != -1){
				searchCriteriaBean.setLatitude(latitude);
			}

			float longitude = requestParamsValidator.validateGeoLocations(Constants.LONGITUDE_PARAM, requestParams.getFirst(Constants.LONGITUDE_PARAM));
			if(longitude != -1){
				searchCriteriaBean.setLongitude(longitude);
			}

			float radialSearchUnit = requestParamsValidator.validateGeoLocations(Constants.RADIUS_PARAM, requestParams.getFirst(Constants.RADIUS_PARAM));
			if(radialSearchUnit != -1){
				searchCriteriaBean.setRadialSearchUnit(radialSearchUnit);
			}
			
			String radialSearchUom = requestParams.getFirst(Constants.RADIUS_UOM_PARAM);
			searchCriteriaBean.setRadialSearchUom(radialSearchUom);
		}
		
		return searchCriteriaBean;
	}
	
	private String getSearchCriteriaKey(SearchCriteriaBean searchCriteriaBean){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getSearchCriteriaKey()");
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String key = "{}";
		if(searchCriteriaBean != null){
			try {
				key = mapper.writeValueAsString(searchCriteriaBean);
			} catch (JsonProcessingException e) {
				appLogger.warn(e.toString());
			}
		}
		
		if(appLogger.isInfoEnabled()){
			appLogger.info("Search Criteria Key : {}", key);
		}

		return key;
	}
	
	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST },headers="Accept=application/json")
	public @ResponseBody StatusBean get(final HttpServletResponse httpServletResponse){
		StatusBean status = new StatusBean();
		status.setStatusCode("" + HttpServletResponse.SC_FORBIDDEN);
		status.setStatusMessage("Directory listing is forbidden..!");
		
		httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		return status;
	}
	
	@RequestMapping(value = "/_status", method = { RequestMethod.GET, RequestMethod.POST },headers="Accept=application/json")
	public @ResponseBody StatusBean getStatus(final HttpServletResponse httpServletResponse){
		StatusBean status = new StatusBean();
		status.setStatusCode("" + HttpServletResponse.SC_OK);
		status.setStatusMessage("Healthy..!");
		
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		
		return status;
	}
	
	@RequestMapping(value = "/lab-tests/v1/search/", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabTestsSearchResultsBean getLabTests(@RequestParam MultiValueMap<String, String> requestParams,
																		@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																			final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabTests()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		SearchCriteriaBean searchCriteriaBean = getSearchCriteriaFromRequestParams(requestParams);
		
		LabTestsSearchResultsBean labTestsSearchResultsBean = null;
		String cacheKey = getSearchCriteriaKey(searchCriteriaBean);
		String cacheKeyType = (searchCriteriaBean.isIncludeLabs()) ? Constants.PRICE_PROMO_CACHE_KEY_TYPE : Constants.LAB_TESTS_CACHE_KEY_TYPE;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the DiscoveryService application logs for further details.";

		if(StringUtils.isEmpty(oauthToken)){
			labTestsSearchResultsBean = new LabTestsSearchResultsBean();
			labTestsSearchResultsBean.setErrorCode("" + HttpServletResponse.SC_UNAUTHORIZED);
			labTestsSearchResultsBean.setErrorDescription("Unauthorized Access Exception. The AUTH token is not valid.");
			
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else{
			try {
				labTestsSearchResultsBean = labTestsCacheFactory.getCachedObject(cacheKey, cacheKeyType, searchCriteriaBean);
			} catch(DiscoveryItemsNotFoundException e){
				appLogger.error("Caught Exception {}", e);

				labTestsSearchResultsBean = new LabTestsSearchResultsBean();
				labTestsSearchResultsBean.setErrorCode(Integer.toString(HttpServletResponse.SC_NOT_FOUND));
				labTestsSearchResultsBean.setErrorDescription(e.getMessage());
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (labTestsSearchResultsBean == null){
					labTestsSearchResultsBean = new LabTestsSearchResultsBean();
					labTestsSearchResultsBean.setErrorCode(errorCode);
					labTestsSearchResultsBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					if(StringUtils.isEmpty(labTestsSearchResultsBean.getErrorCode())){
						httpServletResponse.setStatus(HttpServletResponse.SC_OK);
					}else{
						httpServletResponse.setStatus(Integer.parseInt(labTestsSearchResultsBean.getErrorCode()));
					}
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabTests(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return labTestsSearchResultsBean;
	}

	@RequestMapping(value = "/lab-tests/v1/id/{prdId}", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabTestDetailsResultBean getLabTestDetails(@PathVariable("prdId") String prdId, 
																		@RequestParam MultiValueMap<String, String> requestParams,
																			@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																				final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabTestDetails()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		LabTestDetailsResultBean labTestDetailsResultBean = null;
		String cacheKey = prdId;
		String cacheKeyType = Constants.LAB_TEST_CACHE_KEY_TYPE;
		
		String errorCode = Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		String errorDescription = "Unknown Exception. Please check the DiscoveryService application logs for further details.";

		if(StringUtils.isEmpty(oauthToken)){
			labTestDetailsResultBean = new LabTestDetailsResultBean();
			labTestDetailsResultBean.setErrorCode(Integer.toString(HttpServletResponse.SC_UNAUTHORIZED));
			labTestDetailsResultBean.setErrorDescription("Unauthorized Access Exception. The AUTH token is not valid.");
			
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else{
			try {
				labTestDetailsResultBean = labTestsCacheFactory.getCachedObject(cacheKey, cacheKeyType);
				
				if((labTestDetailsResultBean != null) && (labTestDetailsResultBean.getLabTestDetails() != null)){
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				} else{
					labTestDetailsResultBean = new LabTestDetailsResultBean();
					labTestDetailsResultBean.setErrorCode(Integer.toString(HttpServletResponse.SC_NOT_FOUND));
					labTestDetailsResultBean.setErrorDescription("Lab test " + prdId + " could not be found. Check if it's a valid lab test id and then call the API with a valid id.");
					
					httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}			
			} catch(DiscoveryItemsNotFoundException e){
				appLogger.error("Caught Exception {}", e);

				labTestDetailsResultBean = new LabTestDetailsResultBean();
				labTestDetailsResultBean.setErrorCode(Integer.toString(HttpServletResponse.SC_NOT_FOUND));
				labTestDetailsResultBean.setErrorDescription(e.getMessage());
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (labTestDetailsResultBean == null){
					labTestDetailsResultBean = new LabTestDetailsResultBean();
					labTestDetailsResultBean.setErrorCode(errorCode);
					labTestDetailsResultBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					if(StringUtils.isEmpty(labTestDetailsResultBean.getErrorCode())){
						httpServletResponse.setStatus(HttpServletResponse.SC_OK);
					}else{
						httpServletResponse.setStatus(Integer.parseInt(labTestDetailsResultBean.getErrorCode()));
					}
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabTestDetails(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return labTestDetailsResultBean;
	}

	@RequestMapping(value = "/labs/v1/search/", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabsSearchResultsBean getLabs(@RequestParam MultiValueMap<String, String> requestParams,
																@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																	final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabs()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		LabsSearchResultsBean labsSearchResultsBean = null;
		
		SearchCriteriaBean searchCriteriaBean = getSearchCriteriaFromRequestParams(requestParams);
		
		String cacheKey = getSearchCriteriaKey(searchCriteriaBean);
		String cacheKeyType = (searchCriteriaBean.isIncludeProducts()) ? Constants.PRICE_PROMO_CACHE_KEY_TYPE : Constants.LABS_CACHE_KEY_TYPE;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the DiscoveryService application logs for further details.";

		if(StringUtils.isEmpty(oauthToken)){
			labsSearchResultsBean = new LabsSearchResultsBean();
			labsSearchResultsBean.setErrorCode("" + HttpServletResponse.SC_UNAUTHORIZED);
			labsSearchResultsBean.setErrorDescription("Unauthorized Access Exception. The AUTH token is not valid.");
			
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else{
			try {
				labsSearchResultsBean = labsCacheFactory.getCachedObject(cacheKey, cacheKeyType, searchCriteriaBean);
			} catch(DiscoveryItemsNotFoundException e){
				appLogger.error("Caught Exception {}", e);

				labsSearchResultsBean = new LabsSearchResultsBean();
				labsSearchResultsBean.setErrorCode(Integer.toString(HttpServletResponse.SC_NOT_FOUND));
				labsSearchResultsBean.setErrorDescription(e.getMessage());
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (labsSearchResultsBean == null){
					labsSearchResultsBean = new LabsSearchResultsBean();
					labsSearchResultsBean.setErrorCode(errorCode);
					labsSearchResultsBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					if(StringUtils.isEmpty(labsSearchResultsBean.getErrorCode())){
						httpServletResponse.setStatus(HttpServletResponse.SC_OK);
					}else{
						httpServletResponse.setStatus(Integer.parseInt(labsSearchResultsBean.getErrorCode()));
					}
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabs(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return labsSearchResultsBean;
	}

	@RequestMapping(value = "/labs/v1/id/{labId}", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabDetailsResultBean getLabDetails(@PathVariable("labId") String labId, 
																@RequestParam MultiValueMap<String, String> requestParams,
																	@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																		final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabDetails()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		LabDetailsResultBean labDetailsResultBean = null;
		String cacheKey = labId;
		String cacheKeyType = Constants.LAB_TEST_CACHE_KEY_TYPE;
		
		String errorCode = Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		String errorDescription = "Unknown Exception. Please check the DiscoveryService application logs for further details.";

		if(StringUtils.isEmpty(oauthToken)){
			labDetailsResultBean = new LabDetailsResultBean();
			labDetailsResultBean.setErrorCode(Integer.toString(HttpServletResponse.SC_UNAUTHORIZED));
			labDetailsResultBean.setErrorDescription("Unauthorized Access Exception. The AUTH token is not valid.");
			
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else{
			try {
				labDetailsResultBean = labsCacheFactory.getCachedObject(cacheKey, cacheKeyType);
				
				if((labDetailsResultBean != null) && (labDetailsResultBean.getLabDetails() != null)){
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				} else{
					labDetailsResultBean = new LabDetailsResultBean();
					labDetailsResultBean.setErrorCode(Integer.toString(HttpServletResponse.SC_NOT_FOUND));
					labDetailsResultBean.setErrorDescription("Lab " + labId + " could not be found. Check if it's a valid lab test id and then call the API with a valid id.");
					
					httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}			
			} catch(DiscoveryItemsNotFoundException e){
				appLogger.error("Caught Exception {}", e);

				labDetailsResultBean = new LabDetailsResultBean();
				labDetailsResultBean.setErrorCode(Integer.toString(HttpServletResponse.SC_NOT_FOUND));
				labDetailsResultBean.setErrorDescription(e.getMessage());
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (labDetailsResultBean == null){
					labDetailsResultBean = new LabDetailsResultBean();
					labDetailsResultBean.setErrorCode(errorCode);
					labDetailsResultBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					if(StringUtils.isEmpty(labDetailsResultBean.getErrorCode())){
						httpServletResponse.setStatus(HttpServletResponse.SC_OK);
					}else{
						httpServletResponse.setStatus(Integer.parseInt(labDetailsResultBean.getErrorCode()));
					}
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabDetails(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return labDetailsResultBean;
	}

	public LabTestsCacheFactory getLabTestsCacheFactory() {
		return labTestsCacheFactory;
	}

	public void setLabTestsCacheFactory(LabTestsCacheFactory labTestsCacheFactory) {
		this.labTestsCacheFactory = labTestsCacheFactory;
	}

	public LabsCacheFactory getLabsCacheFactory() {
		return labsCacheFactory;
	}

	public void setLabsCacheFactory(LabsCacheFactory labsCacheFactory) {
		this.labsCacheFactory = labsCacheFactory;
	}
}