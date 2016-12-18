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
import com.labizy.services.content.beans.LabBean;
import com.labizy.services.content.beans.LabDetailsBean;
import com.labizy.services.content.beans.LabDetailsResultBean;
import com.labizy.services.content.beans.LabTestBean;
import com.labizy.services.content.beans.LabTestDetailsBean;
import com.labizy.services.content.beans.LabTestDetailsResultBean;
import com.labizy.services.content.beans.LabTestsSearchResultsBean;
import com.labizy.services.content.beans.LabsSearchResultsBean;
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
			logger.info("Inside LabsCacheFactory c'tor");
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

	
	public LabsSearchResultsBean getCachedObject(String cacheKey, String cacheKeyType, SearchCriteriaBean searchCriteriaBean) 
									throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabsCacheFactory.getCachedObject(String, String, SearchCriteriaBean)");
		}
		
		LabsSearchResultsBean labsSearchResultsBean = null;
		SearchResultsSummaryBean searchResultsSummaryBean = null;
		ArrayList<LabBean> labsList = null;
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
						labsList = loadLabsBean(cacheKeyType);

						if(logger.isDebugEnabled()){
							logger.debug("Setting {} Lab Tests in the cache store..", ((labsList == null) ? 0 : labsList.size()));
						}
						cacheObject = new CacheObject(labsList);
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
						labsList = loadLabsBean(cacheKeyType);
						
						cacheObject = new CacheObject(labsList);
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else{
				labsList = cacheStore.get(cacheKey).labsList;
				if(logger.isInfoEnabled()){
					logger.info("Cache store has a valid item for cache key : {}", cacheKey);
				}
			}

			if(logger.isDebugEnabled()){
				logger.debug("Setting the {} bean", "LabsSearchResultsBean");
			}
			
			labsSearchResultsBean = new LabsSearchResultsBean();
			searchResultsSummaryBean = new SearchResultsSummaryBean();
			
			if(searchCriteriaBean != null){
				searchResultsSummaryBean.setLimit(searchCriteriaBean.getLimit());
				searchResultsSummaryBean.setOffset(searchCriteriaBean.getOffset());
				searchResultsSummaryBean.setSortBy(searchCriteriaBean.getSortBy());
			}

			if(labsList != null){
				int sizeOfList = labsList.size();
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(sizeOfList));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(sizeOfList));
			}else{
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(0));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(0));
			}
			
			labsSearchResultsBean.setResultSummary(searchResultsSummaryBean);
			labsSearchResultsBean.setLabs(labsList);
		}else{
			throw new DiscoveryItemsNotFoundException();
		}
		
		return labsSearchResultsBean;
	}

	public LabDetailsResultBean getCachedObject(String cacheKey, String cacheKeyType) 
										throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabsCacheFactory.getCachedObject(String, String)");
		}
		
		LabDetailsResultBean labDetailsResultBean = null;
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
						labDetailsResultBean = loadLabDetailsBean(cacheKey);
						
						if(logger.isDebugEnabled()){
							logger.debug("Storing Lab --> LabId: {} in the cache store..", labDetailsResultBean.getLabDetails().getLabId());
						}
						cacheObject = new CacheObject(labDetailsResultBean.getLabDetails());
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
	
						labDetailsResultBean = loadLabDetailsBean(cacheKey);

						if(logger.isDebugEnabled()){
							logger.debug("Storing Lab --> LabId: {} in the cache store..", labDetailsResultBean.getLabDetails().getLabId());
						}
						cacheObject = new CacheObject(labDetailsResultBean.getLabDetails());
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else{
				//Do nothing
				if(logger.isInfoEnabled()){
					logger.info("Cache store has a valid item for cache key : {}", cacheKey);
				}
				
				labDetailsResultBean = new LabDetailsResultBean();
				labDetailsResultBean.setLabDetails(cacheStore.get(cacheKey).labDetailsBean);			
			}
		}else{
			throw new DiscoveryItemsNotFoundException();
		}

		return labDetailsResultBean;
	}
	
	private LabDetailsResultBean loadLabDetailsBean(String cacheKey) 
							throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabsCacheFactory.loadLabTestsBean(String)");
		}
		
		LabDetailsResultBean labDetailsResultBean = null;

		String contentRepository = System.getProperty("labtests.discovery.repository");
		File afile = new File(contentRepository + File.separator + "details" + File.separator + "labs" + File.separator + (cacheKey + ".json"));
		
		if(! afile.exists()){
			throw new DiscoveryItemsNotFoundException(afile.getAbsolutePath() + " not found..");
		}

		if(logger.isDebugEnabled()){
			logger.debug("Loading Lab Tests from {}", afile.getAbsoluteFile());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			labDetailsResultBean = mapper.readValue(afile, LabDetailsResultBean.class);
		} catch (JsonParseException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (JsonMappingException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (IOException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		if(labDetailsResultBean == null){
			throw new DiscoveryItemsProcessingException("Failed to fetch labs from " + afile.getAbsolutePath());
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Loaded Lab --> LabId : {}", labDetailsResultBean.getLabDetails().getLabId());
			}
		}

		return labDetailsResultBean;
	}
	
	private ArrayList<LabBean> loadLabsBean(String cacheKeyType) 
								throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabsCacheFactory.loadLabTestsBean(String)");
		}

		ArrayList<LabBean> labsList = null;
		LabsSearchResultsBean labs = null;

		String contentRepository = System.getProperty("labtests.discovery.repository");
		File afile = new File(contentRepository + File.separator + "labs-discovery.json");
		
		if(! afile.exists()){
			throw new DiscoveryItemsNotFoundException(afile.getAbsolutePath() + " not found..");
		}

		if(logger.isDebugEnabled()){
			logger.debug("Loading Lab Tests from {}", afile.getAbsoluteFile());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			labs = mapper.readValue(afile, LabsSearchResultsBean.class);
		} catch (JsonParseException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (JsonMappingException e) {
			throw new DiscoveryItemsProcessingException(e);
		} catch (IOException e) {
			throw new DiscoveryItemsProcessingException(e);
		}
		
		if(labs == null){
			throw new DiscoveryItemsProcessingException("Failed to fetch labs from " + afile.getAbsolutePath());
		}else{
			labsList = labs.getLabs();
			if(logger.isDebugEnabled()){
				logger.debug("Loaded {} Lab Tests", ((labsList == null) ? 0 : labsList.size()));
			}
		}

		return labsList;
	}
	
    private class CacheObject{
		private long birthTimestamp;
		private LabDetailsBean labDetailsBean;
		private ArrayList<LabBean> labsList;
		
		public CacheObject(ArrayList<LabBean> labsList){
			this.labsList = labsList;
			this.birthTimestamp = System.currentTimeMillis();
		}
		
		public CacheObject(LabDetailsBean labDetailsBean){
			this.labDetailsBean = labDetailsBean;
			this.birthTimestamp = commonUtils.getInfinityTimestamp();
		}
	}
}