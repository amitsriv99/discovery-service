package com.labizy.services.content;

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
import com.labizy.services.content.beans.LabTestBean;
import com.labizy.services.content.beans.LabTestDetailsResultBean;
import com.labizy.services.content.beans.LabTestsSearchResultsBean;
import com.labizy.services.content.beans.SearchCriteriaBean;
import com.labizy.services.content.beans.StatusBean;
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
	
	@RequestMapping(value = "/lab-tests/v1/all", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabTestsSearchResultsBean getLabTests(@RequestParam MultiValueMap<String, String> requestParams,
															@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabTests()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		SearchCriteriaBean searchCriteriaBean = null;
		if(requestParams != null){
			searchCriteriaBean = new SearchCriteriaBean();
			RequestParamsValidator requestParamsValidator = new RequestParamsValidator();
			
			String offset = requestParams.getFirst(Constants.OFFSET_QUERY_PARAM);
			searchCriteriaBean.setOffset(requestParamsValidator.validate(Constants.OFFSET_QUERY_PARAM, offset));
			
			String limit = requestParams.getFirst(Constants.LIMIT_QUERY_PARAM);
			searchCriteriaBean.setLimit(requestParamsValidator.validate(Constants.LIMIT_QUERY_PARAM, limit));

			String sortBy = requestParams.getFirst(Constants.SORT_BY_QUERY_PARAM);
			searchCriteriaBean.setSortBy(requestParamsValidator.validate(Constants.SORT_BY_QUERY_PARAM, sortBy));
		}
		
		LabTestsSearchResultsBean labTestsSearchResultsBean = null;
		String cacheKey = getSearchCriteriaKey(searchCriteriaBean);
		String cacheKeyType = Constants.LAB_TESTS_CACHE_KEY_TYPE;
		
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
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabTests(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return labTestsSearchResultsBean;
	}

	@RequestMapping(value = "/lab-tests/v1/{id}", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabTestDetailsResultBean getLabTestDetails(@PathVariable("id") String id, @RequestParam MultiValueMap<String, String> requestParams,
															@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabTestDetails()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		LabTestDetailsResultBean labTestDetailsResultBean = null;
		String cacheKey = id;
		String cacheKeyType = Constants.LAB_TEST_CACHE_KEY_TYPE;
		
		String errorCode = Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		String errorDescription = "Unknown Exception. Please check the HomepageContentService application logs for further details.";

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
					labTestDetailsResultBean.setErrorDescription(" Lab test " + id + "could not be found. Check if it's a valid lab test id and then call the API with a valid id.");
					
					httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}			
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (labTestDetailsResultBean == null){
					labTestDetailsResultBean = new LabTestDetailsResultBean();
					labTestDetailsResultBean.setErrorCode(errorCode);
					labTestDetailsResultBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabTestDetails(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return labTestDetailsResultBean;
	}

	@RequestMapping(value = "/lab-tests/v1/labs/all", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabTestsSearchResultsBean getLabs(@RequestParam MultiValueMap<String, String> requestParams,
															@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabs()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		LabTestsSearchResultsBean contentModelBean = null;
		String cacheKey = "";
		String cacheKeyType = Constants.LAB_TESTS_CACHE_KEY_TYPE;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HomepageContentService application logs for further details.";

		if(StringUtils.isEmpty(oauthToken)){
			contentModelBean = new LabTestsSearchResultsBean();
			contentModelBean.setErrorCode("" + HttpServletResponse.SC_UNAUTHORIZED);
			contentModelBean.setErrorDescription("Unauthorized Access Exception. The AUTH token is not valid.");
			
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else{
			try {
				contentModelBean = null;//labTestsCacheFactory.getCachedObject(cacheKey, cacheKeyType);
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (contentModelBean == null){
					contentModelBean = new LabTestsSearchResultsBean();
					contentModelBean.setErrorCode(errorCode);
					contentModelBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabs(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return contentModelBean;
	}

	@RequestMapping(value = "/lab-tests/v1/labs/lab-1001", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody LabTestsSearchResultsBean getLabDetails(@RequestParam MultiValueMap<String, String> requestParams,
															@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "DiscoveryServiceController.getLabDetails()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		LabTestsSearchResultsBean contentModelBean = null;
		String cacheKey = "";//Constants.LAB_TESTS_CACHE_KEY;
		String cacheKeyType = Constants.LAB_TESTS_CACHE_KEY_TYPE;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HomepageContentService application logs for further details.";

		if(StringUtils.isEmpty(oauthToken)){
			contentModelBean = new LabTestsSearchResultsBean();
			contentModelBean.setErrorCode("" + HttpServletResponse.SC_UNAUTHORIZED);
			contentModelBean.setErrorDescription("Unauthorized Access Exception. The AUTH token is not valid.");
			
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else{
			try {
				contentModelBean = null;//labTestsCacheFactory.getCachedObject(cacheKey, cacheKeyType);
			} catch (Exception e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (contentModelBean == null){
					contentModelBean = new LabTestsSearchResultsBean();
					contentModelBean.setErrorCode(errorCode);
					contentModelBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}

		traceLogger.info("Inside DiscoveryServiceController.getLabDetails(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return contentModelBean;
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