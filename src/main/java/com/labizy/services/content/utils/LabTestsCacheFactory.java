package com.labizy.services.content.utils;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.content.beans.LabTestDetailsBean;
import com.labizy.services.content.beans.LabTestDetailsResultBean;
import com.labizy.services.content.beans.LabTestsSearchResultsBean;
import com.labizy.services.content.beans.SearchCriteriaBean;
import com.labizy.services.content.beans.SearchResultsSummaryBean;
import com.labizy.services.content.dao.adapter.ProductLabsDaoAdapter;
import com.labizy.services.content.exceptions.DiscoveryItemsNotFoundException;
import com.labizy.services.content.exceptions.DiscoveryItemsProcessingException;

public class LabTestsCacheFactory {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");

	private long maxAge;
	private Map<String, LabTestsCacheFactory.CacheObject> cacheStore;
	
	private ProductLabsDaoAdapter productLabsDaoAdapter;
	private CommonUtils commonUtils;
	
	public void setProductLabsDaoAdapter(ProductLabsDaoAdapter productLabsDaoAdapter) {
		this.productLabsDaoAdapter = productLabsDaoAdapter;
	}
	
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public LabTestsCacheFactory(int cacheMaxAgeInMinutes) {
		if(logger.isInfoEnabled()){
			logger.info("Inside LabTestsCacheFactory c'tor");
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
		List<LabTestDetailsBean> labTestsList = null;
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
						labTestsList = loadLabTestsBean(cacheKeyType, searchCriteriaBean);

						if(logger.isDebugEnabled()){
							logger.debug("Setting {} Lab Tests in the cache store..", ((labTestsList == null) ? 0 : labTestsList.size()));
						}
						
						if((labTestsList != null) && (labTestsList.size() > 0)){
							cacheObject = new CacheObject(labTestsList);
							cacheStore.put(cacheKey, cacheObject);
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

						labTestsList = loadLabTestsBean(cacheKeyType, searchCriteriaBean);
						
						if(logger.isDebugEnabled()){
							logger.debug("Setting {} Lab Tests in the cache store..", ((labTestsList == null) ? 0 : labTestsList.size()));
						}
						if((labTestsList != null) && (labTestsList.size() > 0)){
							cacheObject = new CacheObject(labTestsList);
							cacheStore.put(cacheKey, cacheObject);
						}	
					}
				}
			} else{
				labTestsList = cacheStore.get(cacheKey).labTestsList;
				
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

			if(labTestsList != null){
				int sizeOfList = labTestsList.size();
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(sizeOfList));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(sizeOfList));
			}else{
				searchResultsSummaryBean.setNumberOfRecordsReturned(Integer.toString(0));
				searchResultsSummaryBean.setTotalNumberOfRecordsFound(Integer.toString(0));
			}
			
			labTestsSearchResultsBean.setResultSummary(searchResultsSummaryBean);
			labTestsSearchResultsBean.setLabTests(labTestsList);
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
						
						if(labTestDetailsResultBean != null){
							cacheObject = new CacheObject(labTestDetailsResultBean.getLabTestDetails());
							cacheStore.put(cacheKey, cacheObject);
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
	
						labTestDetailsResultBean = loadLabTestDetailsBean(cacheKey);

						if(logger.isDebugEnabled()){
							logger.debug("Storing Lab Test --> Id: {} in the cache store..", labTestDetailsResultBean.getLabTestDetails().getId());
						}
						
						if(labTestDetailsResultBean != null){
							cacheObject = new CacheObject(labTestDetailsResultBean.getLabTestDetails());
							cacheStore.put(cacheKey, cacheObject);
						}
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
		
		LabTestDetailsResultBean labTestDetailsResultBean = productLabsDaoAdapter.loadLabTestDetailsBean(cacheKey);

		return labTestDetailsResultBean;
	}
	
	private List<LabTestDetailsBean> loadLabTestsBean(String cacheKeyType, SearchCriteriaBean searchCriteriaBean) 
								throws DiscoveryItemsNotFoundException, DiscoveryItemsProcessingException{
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "LabTestsCacheFactory.loadLabTestsBean(String, SearchCriteriaBean)");
		}

		List<LabTestDetailsBean> labTestsList = productLabsDaoAdapter.loadLabTestDetails(searchCriteriaBean);

		return labTestsList;
	}
	
    private class CacheObject{
		private long birthTimestamp;
		private LabTestDetailsBean labTestDetailsBean;
		private List<LabTestDetailsBean> labTestsList;
		
		public CacheObject(List<LabTestDetailsBean> labTestsList){
			this.labTestsList = labTestsList;
			this.birthTimestamp = System.currentTimeMillis();
		}
		
		public CacheObject(LabTestDetailsBean labTestDetailsBean){
			this.labTestDetailsBean = labTestDetailsBean;
			this.birthTimestamp = commonUtils.getInfinityTimestamp();
		}
	}
}