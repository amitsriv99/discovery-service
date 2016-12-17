package com.labizy.services.content.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labizy.services.content.beans.LabTestBean;
import com.labizy.services.content.beans.LabTestDetailsBean;
import com.labizy.services.content.beans.LabTestDetailsResultBean;
import com.labizy.services.content.beans.LabTestsSearchResultsBean;
import com.labizy.services.content.beans.SearchCriteriaBean;
import com.labizy.services.content.beans.SearchResultsSummaryBean;
import com.labizy.services.content.exceptions.DiscoveryItemsNotFoundException;
import com.labizy.services.content.exceptions.DiscoveryItemsProcessingException;

public class LabTestsCacheFactory {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");

	private long maxAge;
	private Map<String, LabTestsCacheFactory.CacheObject> cacheStore;
	
	private CommonUtils commonUtils;
	
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public LabTestsCacheFactory(int cacheMaxAgeInMinutes) {
		if(logger.isInfoEnabled()){
			logger.info("Inside IdentityOAuthCacheFactory c'tor");
		}

		if((cacheMaxAgeInMinutes <= 0) || (cacheMaxAgeInMinutes > 60)){
			cacheMaxAgeInMinutes = 1000 * 60 * 60;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("The max age of cached objects is set to " + cacheMaxAgeInMinutes + " minutes");
		}

		this.maxAge = 1000 * 60 * cacheMaxAgeInMinutes;
		this.cacheStore = new WeakHashMap<String, LabTestsCacheFactory.CacheObject>();
	}

	
	public LabTestsSearchResultsBean getCachedObject(String cacheKey, String cacheKeyType, SearchCriteriaBean searchCriteriaBean) 
									throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.getCachedObject(String, String, SearchCriteriaBean)");
		}
		
		LabTestsSearchResultsBean labTestsSearchResultsBean = null;
		SearchResultsSummaryBean searchResultsSummaryBean = null;
		ArrayList<LabTestBean> labTestsList = null;
		CacheObject cacheObject = null;
		
		if(! StringUtils.isEmpty(cacheKey)){
			cacheObject = cacheStore.get(cacheKey);
			
			if(cacheObject == null){
				if(logger.isInfoEnabled()){
					logger.info("Cache store was empty for cache key : {}", cacheKey);
				}
	
				synchronized(this){
					cacheObject = cacheStore.get(cacheKey);
					if(cacheObject == null){
						labTestsList = loadLabTestsBean(cacheKeyType);

						if(logger.isDebugEnabled()){
							logger.debug("Setting {} Lab Tests in the cache store..", ((labTestsList == null) ? 0 : labTestsList.size()));
						}
						cacheObject = new CacheObject(labTestsList);
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else if((System.currentTimeMillis() - cacheObject.birthTimestamp) > this.maxAge){
				if(logger.isInfoEnabled()){
					logger.info("Cache store has expired the cache key : {}", cacheKey);
				}
	
				synchronized(this){
					cacheObject = cacheStore.get(cacheKey);
					if((System.currentTimeMillis() - cacheObject.birthTimestamp) > this.maxAge){
						cacheStore.remove(cacheKey);
	
						cacheObject = new CacheObject(labTestsList);
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else{
				//Do nothing
				if(logger.isInfoEnabled()){
					logger.info("Cache store has a valid item for cache key : {}", cacheKey);
				}
			}

			if(logger.isDebugEnabled()){
				logger.debug("Setting the {} bean", "LabTestsSearchResultsBean");
			}
			
			labTestsSearchResultsBean = new LabTestsSearchResultsBean();
			searchResultsSummaryBean = new SearchResultsSummaryBean();
			
			if(searchCriteriaBean != null){
				searchResultsSummaryBean.setLimit(searchCriteriaBean.getLimit());
				searchResultsSummaryBean.setOffset(searchCriteriaBean.getOffset());
				searchResultsSummaryBean.setSortBy(searchCriteriaBean.getSortBy());
			}

			if(cacheStore.get(cacheKey).labTestsList != null){
				int sizeOfList = cacheStore.get(cacheKey).labTestsList.size();
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(sizeOfList));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(sizeOfList));
			}else{
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(0));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(0));
			}
			
			labTestsSearchResultsBean.setResultSummary(searchResultsSummaryBean);
			labTestsSearchResultsBean.setLabTests(cacheStore.get(cacheKey).labTestsList);
		}else{
			throw new DiscoveryItemsNotFoundException();
		}
		
		return labTestsSearchResultsBean;
	}

	public LabTestDetailsResultBean getCachedObject(String cacheKey, String cacheKeyType) 
										throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.getCachedObject(String, String)");
		}
		
		LabTestDetailsResultBean labTestDetailsResultBean = null;
		CacheObject cacheObject = null;
		
		if(! StringUtils.isEmpty(cacheKey)){
			cacheObject = cacheStore.get(cacheKey);
			
			if(cacheObject == null){
				if(logger.isInfoEnabled()){
				logger.info("Cache store was empty for cache key : {}", cacheKey);
				}
				
				synchronized(this){
					cacheObject = cacheStore.get(cacheKey);
					if(cacheObject == null){
						labTestDetailsResultBean = loadLabTestDetailsBean(cacheKey);
						
						if(logger.isDebugEnabled()){
							logger.debug("Storing Lab Test --> Id: {} in the cache store..", labTestDetailsResultBean.getLabTestDetails().getId());
						}
						cacheObject = new CacheObject(labTestDetailsResultBean.getLabTestDetails());
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else if((System.currentTimeMillis() - cacheObject.birthTimestamp) > this.maxAge){
				if(logger.isInfoEnabled()){
					logger.info("Cache store has expired the cache key : {}", cacheKey);
				}
				
				synchronized(this){
					cacheObject = cacheStore.get(cacheKey);
					if((System.currentTimeMillis() - cacheObject.birthTimestamp) > this.maxAge){
						cacheStore.remove(cacheKey);
	
						labTestDetailsResultBean = loadLabTestDetailsBean(cacheKey);

						if(logger.isDebugEnabled()){
							logger.debug("Storing Lab Test --> Id: {} in the cache store..", labTestDetailsResultBean.getLabTestDetails().getId());
						}
						cacheObject = new CacheObject(labTestDetailsResultBean.getLabTestDetails());
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else{
				//Do nothing
				if(logger.isInfoEnabled()){
					logger.info("Cache store has a valid item for cache key : {}", cacheKey);
				}
				
				labTestDetailsResultBean = new LabTestDetailsResultBean();
				labTestDetailsResultBean.setLabTestDetails(cacheStore.get(cacheKey).labTestDetailsBean);			
			}
		}else{
			throw new DiscoveryItemsNotFoundException();
		}

		return labTestDetailsResultBean;
	}
	
	private LabTestDetailsResultBean loadLabTestDetailsBean(String cacheKey) 
							throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.loadLabTestsBean(String)");
		}
		
		LabTestDetailsResultBean labTestDetailsResultBean = null;

		String contentRepository = System.getProperty("labtests.discovery.repository");
		File afile = new File(contentRepository + File.separator + "details" + File.separator + "labtests" + File.separator + (cacheKey + ".json"));
		
		if(! afile.exists()){
			throw new DiscoveryItemsNotFoundException(afile.getAbsolutePath() + " not found..");
		}

		if(logger.isDebugEnabled()){
			logger.debug("Loading Lab Tests from {}", afile.getAbsoluteFile());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			labTestDetailsResultBean = mapper.readValue(afile, LabTestDetailsResultBean.class);
		} catch (JsonParseException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (JsonMappingException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (IOException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		if(labTestDetailsResultBean == null){
			throw new DiscoveryItemsProcessingException("Failed to fetch lab tests from " + afile.getAbsolutePath());
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Loaded Lab Test --> Id : {}", labTestDetailsResultBean.getLabTestDetails().getId());
			}
		}

		return labTestDetailsResultBean;
	}
	
	private ArrayList<LabTestBean> loadLabTestsBean(String cacheKeyType) 
								throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.loadLabTestsBean(String)");
		}

		ArrayList<LabTestBean> labTestsList = null;
		LabTestsSearchResultsBean labTests = null;

		String contentRepository = System.getProperty("labtests.discovery.repository");
		File afile = new File(contentRepository + File.separator + "labtests-discovery.json");
		
		if(! afile.exists()){
			throw new DiscoveryItemsNotFoundException(afile.getAbsolutePath() + " not found..");
		}

		if(logger.isDebugEnabled()){
			logger.debug("Loading Lab Tests from {}", afile.getAbsoluteFile());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			labTests = mapper.readValue(afile, LabTestsSearchResultsBean.class);
		} catch (JsonParseException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (JsonMappingException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (IOException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		if(labTests == null){
			throw new DiscoveryItemsProcessingException("Failed to fetch lab tests from " + afile.getAbsolutePath());
		}else{
			labTestsList = labTests.getLabTests();
			if(logger.isDebugEnabled()){
				logger.debug("Loaded {} Lab Tests", ((labTestsList == null) ? 0 : labTestsList.size()));
			}
		}

		return labTestsList;
	}
	
    private class CacheObject{
		private long birthTimestamp;
		private LabTestDetailsBean labTestDetailsBean;
		private ArrayList<LabTestBean> labTestsList;
		
		public CacheObject(ArrayList<LabTestBean> labTestsList){
			this.labTestsList = labTestsList;
			this.birthTimestamp = System.currentTimeMillis();
		}
		
		public CacheObject(LabTestDetailsBean labTestDetailsBean){
			this.labTestDetailsBean = labTestDetailsBean;
			this.birthTimestamp = commonUtils.getInfinityTimestamp();
		}
	}
}