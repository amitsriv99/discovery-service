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
import com.labizy.services.content.beans.LabTestsSearchResultsBean;
import com.labizy.services.content.beans.SearchCriteriaBean;
import com.labizy.services.content.beans.SearchResultsSummaryBean;
import com.labizy.services.content.exceptions.DiscoveryItemsNotFoundException;
import com.labizy.services.content.exceptions.DiscoveryItemsProcessingException;

public class LabsCacheFactory {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");

	private long maxAge;
	private Map<String, LabsCacheFactory.CacheObject> cacheStore;
	
	private CommonUtils commonUtils;
	
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public LabsCacheFactory(int cacheMaxAgeInMinutes) {
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
		this.cacheStore = new WeakHashMap<String, LabsCacheFactory.CacheObject>();
	}

	public LabTestsSearchResultsBean getCachedObject(String cacheKey, String cacheKeyType, SearchCriteriaBean searchCriteriaBean) 
									throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.getCachedObject(String)");
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

						cacheObject = new CacheObject(labTestsList);
						cacheStore.put(cacheKey, cacheObject);
						
						for (LabTestBean labTestsBean : labTestsList) {
							cacheObject = new CacheObject(labTestsBean);
							cacheStore.put(labTestsBean.getId(), cacheObject);
						}
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
						
						for (LabTestBean labTestsBean : labTestsList) {
							cacheObject = new CacheObject(labTestsBean);
							if(cacheStore.containsKey(labTestsBean.getId())){
								cacheStore.remove(labTestsBean.getId());
							}else{
								cacheStore.put(labTestsBean.getId(), cacheObject);
							}
						}
					}
				}
			} else{
				//Do nothing
				if(logger.isInfoEnabled()){
					logger.info("Cache store has a valid item for cache key : {}", cacheKey);
				}
			}

			labTestsSearchResultsBean = new LabTestsSearchResultsBean();
			searchResultsSummaryBean = new SearchResultsSummaryBean();
			
			if(searchCriteriaBean != null){
				searchResultsSummaryBean.setLimit(searchCriteriaBean.getLimit());
				searchResultsSummaryBean.setOffset(searchCriteriaBean.getOffset());
				searchResultsSummaryBean.setSortBy(searchCriteriaBean.getSortBy());
			}

			if(cacheObject.labTestsList != null){
				int sizeOfList = cacheObject.labTestsList.size();
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(sizeOfList));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(sizeOfList));
			}else{
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(0));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(0));
			}
			
			labTestsSearchResultsBean.setResultSummary(searchResultsSummaryBean);
			labTestsSearchResultsBean.setLabTests(cacheObject.labTestsList);
		}else{
			throw new DiscoveryItemsNotFoundException();
		}
		
		return labTestsSearchResultsBean;
	}

	private ArrayList<LabTestBean> loadLabTestsBean(String cacheKeyType) 
								throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.getHomepageContentModelBean(String)");
		}

		ArrayList<LabTestBean> labTestsList = null;
		LabTestsSearchResultsBean labTests = null;

		String contentRepository = System.getProperty("labtests.discovery.repository");
		File afile = new File(contentRepository + File.separator + "labtests-discovery.json");
		
		if(! afile.exists()){
			throw new DiscoveryItemsNotFoundException(afile.getAbsolutePath() + " not found..");
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
		}

		return labTestsList;
	}
	
    private class CacheObject{
		private long birthTimestamp;
		private LabTestBean labTestsBean;
		private ArrayList<LabTestBean> labTestsList;
		
		public CacheObject(ArrayList<LabTestBean> labTestsList){
			this.labTestsList = labTestsList;
			this.birthTimestamp = System.currentTimeMillis();
		}
		
		public CacheObject(LabTestBean labTestsBean){
			this.labTestsBean = labTestsBean;
			this.birthTimestamp = commonUtils.getInfinityTimestamp();
		}
	}
}