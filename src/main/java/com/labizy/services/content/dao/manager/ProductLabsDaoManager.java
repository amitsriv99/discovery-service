package com.labizy.services.content.dao.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.content.beans.PropertiesBean;
import com.labizy.services.content.builder.PropertiesBuilder;
import com.labizy.services.content.dao.util.DatabaseConnection;
import com.labizy.services.content.exceptions.DataNotFoundException;
import com.labizy.services.content.exceptions.DatabaseConnectionException;
import com.labizy.services.content.exceptions.QueryExecutionException;
import com.labizy.services.content.utils.CommonUtils;
import com.labizy.services.content.utils.EncryptionDecryptionUtils;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductLabsDaoManager {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");
	
	private CommonUtils commonUtils;
	private String databaseName;
	private EncryptionDecryptionUtils encryptionDecryptionUtils;
	private DatabaseConnection databaseConnection;

	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void setEncryptionDecryptionUtils(EncryptionDecryptionUtils encryptionDecryptionUtils) {
		this.encryptionDecryptionUtils = encryptionDecryptionUtils;
	}

	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	private String getSearchTagsClause(String searchTags){
		if(StringUtils.isEmpty(searchTags)){
			return null;
		}
		
		String searchTagsClause = null;
		String[] searchTagsArr = searchTags.trim().replaceAll(" +", " ").split(" ");
		
		StringBuffer searchTagsClauseBuffer = new StringBuffer();
		
		boolean firstClauseAdded = false;
		
		for(int i = 0; i < searchTagsArr.length; i++){
			if(firstClauseAdded){
				searchTagsClauseBuffer.append(" OR ");
			}else{
				firstClauseAdded = true;
			}

			searchTagsClauseBuffer.append(" search_tags LIKE '%").append(searchTagsArr[i].trim()).append("%'");
		}
		
		searchTagsClause = " ( " + searchTagsClauseBuffer.toString() + " ) ";
		
		return searchTagsClause;
	}

	private String getLabsSearchClause(Map<String, String> searchCriteriaMap, boolean isLooselyMatched){
		if((searchCriteriaMap == null) || (searchCriteriaMap.isEmpty())){
			return null;
		}
		
		StringBuffer searchClauseBuffer = new StringBuffer();
		boolean firstSearchClauseAdded = false;

		for(Map.Entry<String, String> entry : searchCriteriaMap.entrySet()){
			if(firstSearchClauseAdded){
				searchClauseBuffer.append((isLooselyMatched) ? " OR " : " AND ");
			}else{
				firstSearchClauseAdded = true;
			}
			
			if(entry.getKey().equals("labId")){
				searchClauseBuffer.append(" labs_tb.lab_id = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("name")){
				searchClauseBuffer.append(" name = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("groupName")){
				searchClauseBuffer.append(" group_name = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("localityName")){
				searchClauseBuffer.append(" locality_name = '").append(entry.getValue()).append("'");
			}
			
			if(entry.getKey().equals("cityTownOrVillage")){
				searchClauseBuffer.append(" city_town_or_village = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("state")){
				searchClauseBuffer.append(" state = '").append(entry.getValue()).append("'");
			}
			
			if(entry.getKey().equals("country")){
				searchClauseBuffer.append(" country = '").append(entry.getValue()).append("'");
			}
		}
		
		String searchClause = searchClauseBuffer.toString().trim();
		if(! StringUtils.isEmpty(searchClause)){
			searchClause = "( " + searchClause + " )"; 
		}
		
		return searchClause;
	} 

	private String getProductsSearchClause(Map<String, String> searchCriteriaMap, boolean isLooselyMatched){
		if((searchCriteriaMap == null) || (searchCriteriaMap.isEmpty())){
			return null;
		}
		
		StringBuffer searchClauseBuffer = new StringBuffer();
		boolean firstSearchClauseAdded = false;
		
		for(Map.Entry<String, String> entry : searchCriteriaMap.entrySet()){
			if(firstSearchClauseAdded){
				searchClauseBuffer.append((isLooselyMatched) ? " OR " : " AND ");
			}else{
				firstSearchClauseAdded = true;
			}
			
			if(entry.getKey().equals("productId")){
				searchClauseBuffer.append(" product_tb.product_id = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("name")){
				searchClauseBuffer.append(" name = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("type")){
				searchClauseBuffer.append(" type = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("subType")){
				String value = entry.getValue().trim();
				
				if(StringUtils.isEmpty(value)){
					searchClauseBuffer.append(" sub_type IS NULL ");
				}else{
					searchClauseBuffer.append(" sub_type = '").append(entry.getValue()).append("'");
				}
			}
			
			if(entry.getKey().equals("searchTags")){
				searchClauseBuffer.append(getSearchTagsClause(entry.getValue()));
			}
		}
		
		String searchClause = searchClauseBuffer.toString().trim();
		if(! StringUtils.isEmpty(searchClause)){
			searchClause = "( " + searchClause + " )"; 
		}
		
		return searchClause;
	} 
	
	public List<Map<String, String>> searchProducts(Map<String, String> searchCriteriaMap, boolean isLooselyMatched)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		
		List<Map<String, String>> resultList = null;
		HashMap<String, String> result = null;
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "ProductLabsDaoManager.searchProducts(Map<String, String>, boolean)");
		}
		
		Connection connection = databaseConnection.getDatabaseConnection(databaseName);
		
		PreparedStatement preparedStatement = null;

		try{
			String sqlQuery = "SELECT product_tb.product_id AS product_id, name, type, sub_type, "
							+ "short_description, search_tags, status, is_product, is_package, is_service, "
							+ "thumbnail_image_url, rank, free_text, about_this_line1, about_this_line1_type, "
							+ "about_this_line2, about_this_line2_type, about_this_line3, about_this_line3_type, "
							+ "useful_tips, external_blog_url, medium_size_image1_url, medium_size_image1_text, "
							+ "medium_size_image2_url, medium_size_image2_text, medium_size_image3_url, "
							+ "medium_size_image3_text, large_size_image_url, large_size_image_text "
							+ "FROM product_tb, product_details_tb, product_ranking_tb "
							+ "WHERE product_tb.product_id = product_details_tb.product_id "
							+ "AND product_tb.product_id = product_ranking_tb.product_id "
							+ "AND (status IS NULL OR status <> 'DELETED')"; 	
			
			String searchClause = getProductsSearchClause(searchCriteriaMap, isLooselyMatched);
			
			if(! StringUtils.isEmpty(searchClause)){
				sqlQuery = sqlQuery + " AND " + searchClause;
			}
			
			String orderByClause = " ORDER BY rank DESC ";

			sqlQuery = sqlQuery + orderByClause;
			
			if(logger.isInfoEnabled()) {
				logger.info("SQL Search Query --> " + sqlQuery);
			}
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			ResultSet rs = preparedStatement.executeQuery();
			resultList = new ArrayList<Map<String, String>>();
			
			while(rs.next()){
				result = new HashMap<String, String>();

				String productId = rs.getNString("product_id");
				result.put("productId", productId);
			
				String name = rs.getNString("name");
				result.put("name", name);
				
				String type = rs.getNString("type");
				result.put("type", type);
				
				String subType = rs.getNString("sub_type");
				result.put("subType", subType);
				
				String shortDescription = rs.getNString("short_description");
				result.put("shortDescription", shortDescription);
				
				String searchTags = rs.getNString("search_tags");
				result.put("searchTags", searchTags);
				
				String status = rs.getNString("status");
				result.put("status", status);
				
				boolean isProduct = rs.getBoolean("is_product");
				result.put("isProduct", Boolean.toString(isProduct));
				
				boolean isPackage = rs.getBoolean("is_package");
				result.put("isPackage", Boolean.toString(isPackage));
				
				boolean isService = rs.getBoolean("is_service");
				result.put("isService", Boolean.toString(isService));
				
				String thumbnailImageUrl = rs.getNString("thumbnail_image_url");
				result.put("thumbnailImageUrl", thumbnailImageUrl);
				
				int rank = rs.getInt("rank");
				result.put("rank", Integer.toString(rank));
				
				String freeText = rs.getNString("free_text");
				result.put("freeText", freeText);
				
				String aboutThisLine1 = rs.getNString("about_this_line1");
				result.put("aboutThisLine1", aboutThisLine1);
				
				String aboutThisLine1Type = rs.getNString("about_this_line1_type");
				result.put("aboutThisLine1Type", aboutThisLine1Type);
				
				String aboutThisLine2 = rs.getNString("about_this_line2");
				result.put("aboutThisLine2", aboutThisLine2);
				
				String aboutThisLine2Type = rs.getNString("about_this_line2_type");
				result.put("aboutThisLine2Type", aboutThisLine2Type);
				
				String aboutThisLine3 = rs.getNString("about_this_line3");
				result.put("aboutThisLine3", aboutThisLine3);
				
				String aboutThisLine3Type = rs.getNString("about_this_line3_type");
				result.put("aboutThisLine3Type", aboutThisLine3Type);
				
				String usefulTips = rs.getNString("useful_tips");
				result.put("usefulTips", usefulTips);
				
				String externalBlogUrl = rs.getNString("external_blog_url");
				result.put("externalBlogUrl", externalBlogUrl);
				
				String mediumSizeImage1Url = rs.getNString("medium_size_image1_url");
				result.put("mediumSizeImage1Url", mediumSizeImage1Url);
				
				String mediumSizeImage1Text = rs.getNString("medium_size_image1_text");
				result.put("mediumSizeImage1Text", mediumSizeImage1Text);
				
				String mediumSizeImage2Url = rs.getNString("medium_size_image2_url");
				result.put("mediumSizeImage2Url", mediumSizeImage2Url);
				
				String mediumSizeImage2Text = rs.getNString("medium_size_image2_text");
				result.put("mediumSizeImage2Text", mediumSizeImage2Text);
				
				String mediumSizeImage3Url = rs.getNString("medium_size_image3_url");
				result.put("mediumSizeImage3Url", mediumSizeImage3Url);
				
				String mediumSizeImage3Text = rs.getNString("medium_size_image3_text");
				result.put("mediumSizeImage3Text", mediumSizeImage3Text);
				
				String largeSizeImageUrl = rs.getNString("large_size_image_url");
				result.put("largeSizeImageUrl", largeSizeImageUrl);
				
				String largeSizeImageText = rs.getNString("large_size_image_text");
				result.put("largeSizeImageText", largeSizeImageText);
			
				resultList.add(result);
			}
			
			if(resultList.isEmpty()){
				throw new DataNotFoundException("Products matching the search criteria either don't exist or they have been deleted.");
			}
		}catch (SQLException e){
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				preparedStatement = null;
				
				connection.close();
				connection = null;
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
		}
		
		return resultList;
	}

	public Map<String, String> getProductDetails(String productId)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "ProductLabsDaoManager.getProductDetails(String)");
		}

		Connection connection = databaseConnection.getDatabaseConnection(databaseName);
		
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT product_tb.product_id AS product_id, name, type, sub_type, "
										+ "short_description, search_tags, status, is_product, is_package, is_service, "
										+ "thumbnail_image_url, rank, free_text, about_this_line1, about_this_line1_type, "
										+ "about_this_line2, about_this_line2_type, about_this_line3, about_this_line3_type, "
										+ "useful_tips, external_blog_url, medium_size_image1_url, medium_size_image1_text, "
										+ "medium_size_image2_url, medium_size_image2_text, medium_size_image3_url, "
										+ "medium_size_image3_text, large_size_image_url, large_size_image_text "
										+ "FROM product_tb, product_details_tb, product_ranking_tb "
										+ "WHERE product_tb.product_id = product_details_tb.product_id "
										+ "AND product_tb.product_id = product_ranking_tb.product_id "
										+ "AND product_tb.product_id = ? "
										+ "AND (status IS NULL OR status <> 'DELETED')"; 	

			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, productId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("productId", productId);
				
				String name = rs.getNString("name");
				result.put("name", name);
				
				String type = rs.getNString("type");
				result.put("type", type);
				
				String subType = rs.getNString("sub_type");
				result.put("subType", subType);
				
				String shortDescription = rs.getNString("short_description");
				result.put("shortDescription", shortDescription);
				
				String searchTags = rs.getNString("search_tags");
				result.put("searchTags", searchTags);
				
				String status = rs.getNString("status");
				result.put("status", status);

				boolean isProduct = rs.getBoolean("is_product");
				result.put("isProduct", Boolean.toString(isProduct));

				boolean isPackage = rs.getBoolean("is_package");
				result.put("isPackage", Boolean.toString(isPackage));

				boolean isService = rs.getBoolean("is_service");
				result.put("isService", Boolean.toString(isService));

				String thumbnailImageUrl = rs.getNString("thumbnail_image_url");
				result.put("thumbnailImageUrl", thumbnailImageUrl);

				int rank = rs.getInt("rank");
				result.put("rank", Integer.toString(rank));

				String freeText = rs.getNString("free_text");
				result.put("freeText", freeText);

				String aboutThisLine1 = rs.getNString("about_this_line1");
				result.put("aboutThisLine1", aboutThisLine1);

				String aboutThisLine1Type = rs.getNString("about_this_line1_type");
				result.put("aboutThisLine1Type", aboutThisLine1Type);

				String aboutThisLine2 = rs.getNString("about_this_line2");
				result.put("aboutThisLine2", aboutThisLine2);

				String aboutThisLine2Type = rs.getNString("about_this_line2_type");
				result.put("aboutThisLine2Type", aboutThisLine2Type);

				String aboutThisLine3 = rs.getNString("about_this_line3");
				result.put("aboutThisLine3", aboutThisLine3);

				String aboutThisLine3Type = rs.getNString("about_this_line3_type");
				result.put("aboutThisLine3Type", aboutThisLine3Type);

				String usefulTips = rs.getNString("useful_tips");
				result.put("usefulTips", usefulTips);

				String externalBlogUrl = rs.getNString("external_blog_url");
				result.put("externalBlogUrl", externalBlogUrl);

				String mediumSizeImage1Url = rs.getNString("medium_size_image1_url");
				result.put("mediumSizeImage1Url", mediumSizeImage1Url);

				String mediumSizeImage1Text = rs.getNString("medium_size_image1_text");
				result.put("mediumSizeImage1Text", mediumSizeImage1Text);

				String mediumSizeImage2Url = rs.getNString("medium_size_image2_url");
				result.put("mediumSizeImage2Url", mediumSizeImage2Url);

				String mediumSizeImage2Text = rs.getNString("medium_size_image2_text");
				result.put("mediumSizeImage2Text", mediumSizeImage2Text);

				String mediumSizeImage3Url = rs.getNString("medium_size_image3_url");
				result.put("mediumSizeImage3Url", mediumSizeImage3Url);

				String mediumSizeImage3Text = rs.getNString("medium_size_image3_text");
				result.put("mediumSizeImage3Text", mediumSizeImage3Text);

				String largeSizeImageUrl = rs.getNString("large_size_image_url");
				result.put("largeSizeImageUrl", largeSizeImageUrl);

				String largeSizeImageText = rs.getNString("large_size_image_text");
				result.put("largeSizeImageText", largeSizeImageText);
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("Product either doesn't exist or has been deleted.");
			}
		}catch (SQLException e){
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				preparedStatement = null;
				
				connection.close();
				connection = null;
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
		}
		
		return result;
	}

	public List<Map<String, String>> searchLabs(Map<String, String> searchCriteriaMap, boolean isLooselyMatched)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		
		List<Map<String, String>> resultList = null;
		HashMap<String, String> result = null;
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "ProductLabsDaoManager.searchLabs(Map<String, String>, boolean)");
		}
		
		Connection connection = databaseConnection.getDatabaseConnection(databaseName);
		
		PreparedStatement preparedStatement = null;
		
		try{
			boolean geoCodeSearchExists = false;
			
			String latitude = null; 
			String longitude = null;
			String radialSearchUnit = null;
			String radialSearchUom = null;
			
			if(searchCriteriaMap.containsKey("latitude") && searchCriteriaMap.containsKey("longitude")){
				geoCodeSearchExists = true;
				
				if(searchCriteriaMap.containsKey("latitude")){
					latitude = searchCriteriaMap.get("latitude");
					searchCriteriaMap.remove("latitude");
				}
				
				if(searchCriteriaMap.containsKey("longitude")){
					longitude = searchCriteriaMap.get("longitude");
					searchCriteriaMap.remove("longitude");
				}
				
				if(searchCriteriaMap.containsKey("radialSearchUnit")){
					radialSearchUnit = searchCriteriaMap.get("radialSearchUnit");
					searchCriteriaMap.remove("radialSearchUnit");
				}
				if(StringUtils.isEmpty(radialSearchUnit)){
					radialSearchUnit = "5";
				}else{
					try{
						Float.parseFloat(radialSearchUnit);
					}catch(Exception e){
						radialSearchUnit = "5";
						logger.warn("The radialSearchUnit parameter value (" + radialSearchUnit + ") is not valid. It will be set to the default value instead.");
					}	
				}
				
				if(StringUtils.isEmpty(radialSearchUom)){
					radialSearchUom = searchCriteriaMap.get("radialSearchUom");
					searchCriteriaMap.remove("radialSearchUom");
				}
				if((StringUtils.isEmpty(radialSearchUom)) || ("kms".equalsIgnoreCase(radialSearchUom))){
					radialSearchUom = "kms";
					logger.warn("The radialSearchUom parameter value (" + radialSearchUom + ") is valid. It will be set to i.e. kms instead.");
				}else{
					radialSearchUom = "miles";
					logger.warn("The radialSearchUom parameter value (" + radialSearchUom + ") is not valid. It will be set to the default value i.e. miles instead.");
				}
			} else{
				if(searchCriteriaMap.containsKey("latitude")){
					searchCriteriaMap.remove("latitude");
				}
				
				if(searchCriteriaMap.containsKey("longitude")){
					searchCriteriaMap.remove("longitude");
				}
				
				if(searchCriteriaMap.containsKey("radialSearchUnit")){
					searchCriteriaMap.remove("radialSearchUnit");
				}

				if(StringUtils.isEmpty(radialSearchUom)){
					searchCriteriaMap.remove("radialSearchUom");
				}
			}
		
			String sqlQuery = "SELECT labs_tb.lab_id AS lab_id, name, group_name, parent_lab_id, status, "
										+ "short_description, thumbnail_image_url, address_line1, address_line2, locality_name, "
										+ "city_town_or_village, state, country, pin_code, landmark, lat AS latitude, lng AS longitude, "
										+ "useful_tips, external_reviews_url, medium_size_image1_url, medium_size_image1_text, "
										+ "medium_size_image2_url, medium_size_image2_text, medium_size_image3_url, "
										+ "medium_size_image3_text, large_size_image_url, large_size_image_text, rank, "
										+ commonUtils.getRadialDistanceQueryPart(latitude, longitude, radialSearchUom) 
										+ "FROM labs_tb, labs_details_tb, labs_ranking_tb "
										+ "WHERE labs_tb.lab_id = labs_details_tb.lab_id "
										+ "AND labs_tb.lab_id = labs_ranking_tb.lab_id "
										+ "AND (status IS NULL OR status <> 'DELETED')"; 
			
			String searchClause = getLabsSearchClause(searchCriteriaMap, isLooselyMatched);
			
			if(! StringUtils.isEmpty(searchClause)){
				sqlQuery = sqlQuery + " AND " + searchClause;
			}

			if(geoCodeSearchExists){
				sqlQuery = sqlQuery + " HAVING distance <= " + radialSearchUnit;	
			}
			
			String orderByClause = " ORDER BY rank DESC ";

			sqlQuery = sqlQuery + orderByClause;
			
			if(logger.isInfoEnabled()) {
				logger.info("SQL Search Query --> " + sqlQuery);
			}
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			ResultSet rs = preparedStatement.executeQuery();
			resultList = new ArrayList<Map<String, String>>();
			
			while(rs.next()){
				result = new HashMap<String, String>();
				
				String labId = rs.getNString("lab_id");
				result.put("labId", labId);
				
				String name = rs.getNString("name");
				result.put("name", name);
				
				String shortDescription = rs.getNString("short_description");
				result.put("shortDescription", shortDescription);
				
				String groupName = rs.getNString("group_name");
				result.put("groupName", groupName);
				
				String parentLabId = rs.getNString("parent_lab_id");
				result.put("parentLabId", parentLabId);
				
				String status = rs.getNString("status");
				result.put("status", status);
				
				String thumbnailImageUrl = rs.getNString("thumbnail_image_url");
				result.put("thumbnailImageUrl", thumbnailImageUrl);
				
				String addressLine1 = rs.getNString("address_line1");
				result.put("addressLine1", addressLine1);
				
				String addressLine2 = rs.getNString("address_line2");
				result.put("addressLine2", addressLine2);
				
				String localityName = rs.getNString("locality_name");
				result.put("localityName", localityName);
				
				String cityTownOrVillage = rs.getNString("city_town_or_village");
				result.put("cityTownOrVillage", cityTownOrVillage);
				
				String state = rs.getNString("state");
				result.put("state", state);

				String country = rs.getNString("country");
				result.put("country", country);

				String pinCode = rs.getNString("pin_code");
				result.put("cityTownOrVillage", pinCode);

				String landmark = rs.getNString("landmark");
				result.put("landmark", landmark);

				Float lat = rs.getFloat("latitude");
				result.put("latitude", Float.toString(lat));
				
				Float lng = rs.getFloat("longitude");
				result.put("longitude", Float.toString(lng));
				
				Float distance = rs.getFloat("distance");
				result.put("distance", Float.toString(distance));

				result.put("distanceUom", radialSearchUom);
				
				int rank = rs.getInt("rank");
				result.put("rank", Integer.toString(rank));
				
				String usefulTips = rs.getNString("useful_tips");
				result.put("usefulTips", usefulTips);
				
				String externalReviewsUrl = rs.getNString("external_reviews_url");
				result.put("externalReviewsUrl", externalReviewsUrl);
				
				String mediumSizeImage1Url = rs.getNString("medium_size_image1_url");
				result.put("mediumSizeImage1Url", mediumSizeImage1Url);
				
				String mediumSizeImage1Text = rs.getNString("medium_size_image1_text");
				result.put("mediumSizeImage1Text", mediumSizeImage1Text);
				
				String mediumSizeImage2Url = rs.getNString("medium_size_image2_url");
				result.put("mediumSizeImage2Url", mediumSizeImage2Url);
				
				String mediumSizeImage2Text = rs.getNString("medium_size_image2_text");
				result.put("mediumSizeImage2Text", mediumSizeImage2Text);
				
				String mediumSizeImage3Url = rs.getNString("medium_size_image3_url");
				result.put("mediumSizeImage3Url", mediumSizeImage3Url);
				
				String mediumSizeImage3Text = rs.getNString("medium_size_image3_text");
				result.put("mediumSizeImage3Text", mediumSizeImage3Text);
				
				String largeSizeImageUrl = rs.getNString("large_size_image_url");
				result.put("largeSizeImageUrl", largeSizeImageUrl);
				
				String largeSizeImageText = rs.getNString("large_size_image_text");
				result.put("largeSizeImageText", largeSizeImageText);
				
				resultList.add(result);
			}
			
			if(resultList.isEmpty()){
				throw new DataNotFoundException("Labs matching the search criteria either don't exist or they have been deleted.");
			}
		}catch (SQLException e){
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				preparedStatement = null;
				
				connection.close();
				connection = null;
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
		}
		
		return resultList;
	}
	
	public Map<String, String> getLabDetails(String labId)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		
		HashMap<String, String> result = null;
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "ProductLabsDaoManager.getLabDetails(String)");
		}
		
		Connection connection = databaseConnection.getDatabaseConnection(databaseName);
		
		PreparedStatement preparedStatement = null;
		
		try{
			String sqlQuery = "SELECT labs_tb.lab_id AS lab_id, name, group_name, parent_lab_id, status, "
										+ "short_description, thumbnail_image_url, address_line1, address_line2, locality_name, "
										+ "city_town_or_village, state, country, pin_code, landmark, lat AS latitude, lng AS longitude, "
										+ "useful_tips, external_reviews_url, medium_size_image1_url, medium_size_image1_text, "
										+ "medium_size_image2_url, medium_size_image2_text, medium_size_image3_url, "
										+ "medium_size_image3_text, large_size_image_url, large_size_image_text, rank, '-1' AS distance "
										+ "FROM labs_tb, labs_details_tb, labs_ranking_tb "
										+ "WHERE labs_tb.lab_id = labs_details_tb.lab_id "
										+ "AND labs_tb.lab_id = labs_ranking_tb.lab_id "
										+ "AND labs_tb.lab_id = ?  "
										+ "AND (status IS NULL OR status <> 'DELETED')"; 	
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, labId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();

			while(rs.next()){
				result.put("labId", labId);
				
				String name = rs.getNString("name");
				result.put("name", name);
				
				String shortDescription = rs.getNString("short_description");
				result.put("shortDescription", shortDescription);
				
				String groupName = rs.getNString("group_name");
				result.put("groupName", groupName);
				
				String parentLabId = rs.getNString("parent_lab_id");
				result.put("parentLabId", parentLabId);
				
				String status = rs.getNString("status");
				result.put("status", status);
				
				String thumbnailImageUrl = rs.getNString("thumbnail_image_url");
				result.put("thumbnailImageUrl", thumbnailImageUrl);
				
				String addressLine1 = rs.getNString("address_line1");
				result.put("addressLine1", addressLine1);
				
				String addressLine2 = rs.getNString("address_line2");
				result.put("addressLine2", addressLine2);
				
				String localityName = rs.getNString("locality_name");
				result.put("localityName", localityName);
				
				String cityTownOrVillage = rs.getNString("city_town_or_village");
				result.put("cityTownOrVillage", cityTownOrVillage);
				
				String state = rs.getNString("state");
				result.put("state", state);

				String country = rs.getNString("country");
				result.put("country", country);

				String pinCode = rs.getNString("pin_code");
				result.put("cityTownOrVillage", pinCode);

				String landmark = rs.getNString("landmark");
				result.put("landmark", landmark);
				
				Float lat = rs.getFloat("latitude");
				result.put("latitude", Float.toString(lat));
				
				Float lng = rs.getFloat("longitude");
				result.put("longitude", Float.toString(lng));
				
				Float distance = rs.getFloat("distance");
				result.put("distance", Float.toString(distance));

				result.put("distanceUom", "kms");
				
				int rank = rs.getInt("rank");
				result.put("rank", Integer.toString(rank));
				
				String usefulTips = rs.getNString("useful_tips");
				result.put("usefulTips", usefulTips);
				
				String externalReviewsUrl = rs.getNString("external_reviews_url");
				result.put("externalReviewsUrl", externalReviewsUrl);
				
				String mediumSizeImage1Url = rs.getNString("medium_size_image1_url");
				result.put("mediumSizeImage1Url", mediumSizeImage1Url);
				
				String mediumSizeImage1Text = rs.getNString("medium_size_image1_text");
				result.put("mediumSizeImage1Text", mediumSizeImage1Text);
				
				String mediumSizeImage2Url = rs.getNString("medium_size_image2_url");
				result.put("mediumSizeImage2Url", mediumSizeImage2Url);
				
				String mediumSizeImage2Text = rs.getNString("medium_size_image2_text");
				result.put("mediumSizeImage2Text", mediumSizeImage2Text);
				
				String mediumSizeImage3Url = rs.getNString("medium_size_image3_url");
				result.put("mediumSizeImage3Url", mediumSizeImage3Url);
				
				String mediumSizeImage3Text = rs.getNString("medium_size_image3_text");
				result.put("mediumSizeImage3Text", mediumSizeImage3Text);
				
				String largeSizeImageUrl = rs.getNString("large_size_image_url");
				result.put("largeSizeImageUrl", largeSizeImageUrl);
				
				String largeSizeImageText = rs.getNString("large_size_image_text");
				result.put("largeSizeImageText", largeSizeImageText);
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("Lab either doesn't exist or has been deleted.");
			}
		}catch (SQLException e){
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				preparedStatement = null;
				
				connection.close();
				connection = null;
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
		}
		
		return result;
	}

	private String getSplitWithSingleQuotedIds(String commaDelimitedIds){
		String result = null;
		StringBuffer buff = null;
		if(! StringUtils.isEmpty(commaDelimitedIds)){
			buff = new StringBuffer();
			String[] idsArr = commaDelimitedIds.split(",");
			for(int i = 0; i < idsArr.length; i++){
				String id = idsArr[i];
				if(! StringUtils.isEmpty(id)){
					buff.append(", '" + id.trim() + "'");
				}
			}
			
			result = (buff.length() > 1) ? buff.substring(1) : "''";
		}else{
			result = "''";
		}
		
		return result;
	}
	
	private String getLabsProductsSearchClause(Map<String, String> searchCriteriaMap, boolean isLooselyMatched){
		if((searchCriteriaMap == null) || (searchCriteriaMap.isEmpty())){
			return null;
		}
		
		StringBuffer searchClauseBuffer = new StringBuffer();
		boolean firstSearchClauseAdded = false;
		
		for(Map.Entry<String, String> entry : searchCriteriaMap.entrySet()){
			if(firstSearchClauseAdded){
				searchClauseBuffer.append((isLooselyMatched) ? " OR " : " AND ");
			}else{
				firstSearchClauseAdded = true;
			}
			
			if(entry.getKey().equals("productId")){
				searchClauseBuffer.append(" product_tb.product_id = '").append(entry.getValue()).append("'");
			}
			
			if(entry.getKey().equals("productIds")){
				searchClauseBuffer.append(" product_tb.product_id IN ( ").append(getSplitWithSingleQuotedIds(entry.getValue())).append(" )");
			}
			
			if(entry.getKey().equals("productName")){
				searchClauseBuffer.append(" product_tb.name = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("type")){
				searchClauseBuffer.append(" product_tb.type = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("subType")){
				String value = entry.getValue().trim();
				
				if(StringUtils.isEmpty(value)){
					searchClauseBuffer.append(" product_tb.sub_type IS NULL ");
				}else{
					searchClauseBuffer.append(" product_tb.sub_type = '").append(entry.getValue()).append("'");
				}
			}
			
			if(entry.getKey().equals("searchTags")){
				searchClauseBuffer.append(getSearchTagsClause(entry.getValue()));
			}

			if(entry.getKey().equals("labId")){
				searchClauseBuffer.append(" labs_tb.lab_id = '").append(entry.getValue()).append("'");
			}
			
			if(entry.getKey().equals("labIds")){
				searchClauseBuffer.append(" labs_tb.lab_id IN ( ").append(getSplitWithSingleQuotedIds(entry.getValue())).append(" )");
			}

			if(entry.getKey().equals("labName")){
				searchClauseBuffer.append(" labs_tb.name = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("groupName")){
				searchClauseBuffer.append(" labs_tb.group_name = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("localityName")){
				searchClauseBuffer.append(" labs_details_tb.locality_name = '").append(entry.getValue()).append("'");
			}
			
			if(entry.getKey().equals("cityTownOrVillage")){
				searchClauseBuffer.append(" labs_details_tb.city_town_or_village = '").append(entry.getValue()).append("'");
			}

			if(entry.getKey().equals("state")){
				searchClauseBuffer.append(" labs_details_tb.state = '").append(entry.getValue()).append("'");
			}
			
			if(entry.getKey().equals("country")){
				searchClauseBuffer.append(" labs_details_tb.country = '").append(entry.getValue()).append("'");
			}
		}
		
		String searchClause = searchClauseBuffer.toString().trim();
		if(! StringUtils.isEmpty(searchClause)){
			searchClause = "( " + searchClause + " )"; 
		}
		
		return searchClause;
	} 
	
	public List<Map<String, String>> searchLabsProducts(Map<String, String> searchCriteriaMap, boolean isLooselyMatched)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		
		List<Map<String, String>> resultList = null;
		HashMap<String, String> result = null;
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "ProductLabsDaoManager.searchLabsProducts(Map<String, String>, boolean)");
		}
		
		Connection connection = databaseConnection.getDatabaseConnection(databaseName);
		
		PreparedStatement preparedStatement = null;
		
		try{
			boolean geoCodeSearchExists = false;
			
			String latitude = null; 
			String longitude = null;
			String radialSearchUnit = null;
			String radialSearchUom = null;
			
			if(searchCriteriaMap.containsKey("latitude") && searchCriteriaMap.containsKey("longitude")){
				geoCodeSearchExists = true;
				
				if(searchCriteriaMap.containsKey("latitude")){
					latitude = searchCriteriaMap.get("latitude");
					searchCriteriaMap.remove("latitude");
				}
				
				if(searchCriteriaMap.containsKey("longitude")){
					longitude = searchCriteriaMap.get("longitude");
					searchCriteriaMap.remove("longitude");
				}
				
				if(searchCriteriaMap.containsKey("radialSearchUnit")){
					radialSearchUnit = searchCriteriaMap.get("radialSearchUnit");
					searchCriteriaMap.remove("radialSearchUnit");
				}
				if(StringUtils.isEmpty(radialSearchUnit)){
					radialSearchUnit = "5";
				}else{
					try{
						Float.parseFloat(radialSearchUnit);
					}catch(Exception e){
						radialSearchUnit = "5";
						logger.warn("The radialSearchUnit parameter value (" + radialSearchUnit + ") is not valid. It will be set to the default value instead.");
					}	
				}
				
				if(StringUtils.isEmpty(radialSearchUom)){
					radialSearchUom = searchCriteriaMap.get("radialSearchUom");
					searchCriteriaMap.remove("radialSearchUom");
				}
				if((StringUtils.isEmpty(radialSearchUom)) || ("kms".equalsIgnoreCase(radialSearchUom))){
					radialSearchUom = "kms";
					logger.warn("The radialSearchUom parameter value (" + radialSearchUom + ") is valid. It will be set to i.e. kms instead.");
				}else{
					radialSearchUom = "miles";
					logger.warn("The radialSearchUom parameter value (" + radialSearchUom + ") is not valid. It will be set to the default value i.e. miles instead.");
				}
			} else{
				if(searchCriteriaMap.containsKey("latitude")){
					searchCriteriaMap.remove("latitude");
				}
				
				if(searchCriteriaMap.containsKey("longitude")){
					searchCriteriaMap.remove("longitude");
				}
				
				if(searchCriteriaMap.containsKey("radialSearchUnit")){
					searchCriteriaMap.remove("radialSearchUnit");
				}

				if(StringUtils.isEmpty(radialSearchUom)){
					searchCriteriaMap.remove("radialSearchUom");
				}
			}
			
			String sqlQuery = "SELECT product_tb.product_id AS product_id, product_tb.name AS product_name, "
										+ "product_tb.type AS product_type, product_tb.sub_type AS product_sub_type, "
										+ "product_tb.short_description AS product_short_description, product_tb.search_tags AS product_search_tags, "
										+ "product_tb.status AS product_status, product_tb.is_product, product_tb.is_package, product_tb.is_service, "
										+ "product_tb.thumbnail_image_url AS product_thumbnail_image_url, product_ranking_tb.rank AS product_rank, "
										+ "unit_price, currency_code, uom, promo_type, promo_value, "
										+ "labs_tb.lab_id AS lab_id, labs_tb.name AS lab_name, labs_tb.group_name AS lab_group_name, "
										+ "labs_tb.short_description AS lab_short_description, labs_tb.parent_lab_id AS parent_lab_id, "
										+ "labs_tb.status AS lab_status, labs_tb.thumbnail_image_url AS lab_thumbnail_image_url, "
										+ "labs_details_tb.lat AS latitude, labs_details_tb.lng AS longitude, "
										+ "labs_ranking_tb.rank AS lab_rank, "
										+ commonUtils.getRadialDistanceQueryPart(latitude, longitude, radialSearchUom) 
										+ "FROM labs_tb, labs_details_tb, labs_ranking_tb, promo_pricing_tb, "
										+ "product_tb, product_ranking_tb, product_details_tb "
										+ "WHERE labs_tb.lab_id = labs_details_tb.lab_id "
										+ "AND labs_tb.lab_id = labs_ranking_tb.lab_id "
										+ "AND promo_pricing_tb.lab_id = labs_tb.lab_id "
										+ "AND product_tb.product_id = promo_pricing_tb.product_id "
										+ "AND product_tb.product_id = product_details_tb.product_id "
										+ "AND product_tb.product_id = product_ranking_tb.product_id "
										+ "AND (labs_tb.status IS NULL OR labs_tb.status <> 'DELETED') "
										+ "AND (product_tb.status IS NULL OR product_tb.status <> 'DELETED')"; 

			String rankBy = null;
			if(searchCriteriaMap.containsKey("rankBy")){
				rankBy = searchCriteriaMap.get("rankBy");
				if((StringUtils.isEmpty(rankBy) || (!"products".equalsIgnoreCase(rankBy)))){
					rankBy = "labs";
				}
				
				searchCriteriaMap.remove("rankBy");
			}
			
			String searchClause = getLabsProductsSearchClause(searchCriteriaMap, isLooselyMatched);
			
			if(! StringUtils.isEmpty(searchClause)){
				sqlQuery = sqlQuery + " AND " + searchClause;
			}

			if(geoCodeSearchExists){
				sqlQuery = sqlQuery + " HAVING distance <= " + radialSearchUnit;	
			}
			
			String orderByClause = null;
			if("labs".equalsIgnoreCase(rankBy)){
				orderByClause = " ORDER BY labs_ranking_tb.rank DESC, labs_tb.lab_id ASC, product_ranking_tb.rank DESC ";
			}else{
				orderByClause = " ORDER BY product_ranking_tb.rank DESC, product_tb.product_id ASC, labs_ranking_tb.rank DESC ";
			}
			
			sqlQuery = sqlQuery + orderByClause;
			
			if(logger.isInfoEnabled()) {
				logger.info("SQL Search Query --> " + sqlQuery);
			}
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			ResultSet rs = preparedStatement.executeQuery();
			resultList = new ArrayList<Map<String, String>>();
			
			while(rs.next()){
				result = new HashMap<String, String>();
				
				String productId = rs.getNString("product_id");
				result.put("productId", productId);
				
				String productName = rs.getNString("product_name");
				result.put("productName", productName);
				
				int productRank = rs.getInt("product_rank");
				result.put("productRank", Integer.toString(productRank));
				
				String productType = rs.getNString("product_type");
				result.put("productType", productType);

				String productSubType = rs.getNString("product_sub_type");
				result.put("productSubType", productSubType);

				String productShortDescription = rs.getNString("product_short_description");
				result.put("productShortDescription", productShortDescription);

				String productSearchTags = rs.getNString("product_search_tags");
				result.put("productSearchTags", productSearchTags);

				boolean isProduct = rs.getBoolean("is_product");
				result.put("isProduct", Boolean.toString(isProduct));
				
				boolean isService = rs.getBoolean("is_service");
				result.put("isService", Boolean.toString(isService));
				
				boolean isPackage = rs.getBoolean("is_package");
				result.put("isPackage", Boolean.toString(isPackage));
				
				String productStatus = rs.getNString("product_status");
				result.put("productStatus", productStatus);
				
				String productThumbnailImageUrl = rs.getNString("product_thumbnail_image_url");
				result.put("productThumbnailImageUrl", productThumbnailImageUrl);
				
				double unitPrice = rs.getDouble("unit_price");
				result.put("unitPrice", Double.toString(unitPrice));

				String currencyCode = rs.getNString("currency_code");
				result.put("currencyCode", currencyCode);

				String uom = rs.getNString("uom");
				result.put("uom", uom);

				String promoType = rs.getNString("promo_type");
				result.put("promoType", promoType);

				double promoValue = rs.getDouble("promo_value");
				result.put("promoValue", Double.toString(promoValue));
				
				String labId = rs.getNString("lab_id");
				result.put("labId", labId);
				
				String labName = rs.getNString("lab_name");
				result.put("labName", labName);
				
				String labShortDescription = rs.getNString("lab_short_description");
				result.put("labShortDescription", labShortDescription);
				
				String labGroupName = rs.getNString("lab_group_name");
				result.put("labGroupName", labGroupName);
				
				String parentLabId = rs.getNString("parent_lab_id");
				result.put("parentLabId", parentLabId);
				
				String labStatus = rs.getNString("lab_status");
				result.put("labStatus", labStatus);

				String labThumbnailImageUrl = rs.getNString("lab_thumbnail_image_url");
				result.put("labThumbnailImageUrl", labThumbnailImageUrl);

				Float lat = rs.getFloat("latitude");
				result.put("latitude", Float.toString(lat));
				
				Float lng = rs.getFloat("longitude");
				result.put("longitude", Float.toString(lng));
				
				Float distance = rs.getFloat("distance");
				result.put("distance", Float.toString(distance));

				result.put("distanceUom", radialSearchUom);
				
				int labRank = rs.getInt("lab_rank");
				result.put("labRank", Integer.toString(labRank));
				
				resultList.add(result);
			}
			
			if(resultList.isEmpty()){
				throw new DataNotFoundException("Labs and Products matching the search criteria either don't exist or they have been deleted.");
			}
		}catch (SQLException e){
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				preparedStatement = null;
				
				connection.close();
				connection = null;
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
		}
		
		return resultList;
	}

	public static void main(String[] args) throws Exception{
		System.out.println("Let's start..");
		
		ProductLabsDaoManager daoMgr = new ProductLabsDaoManager();
		
		String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
		String DATABASE_URL = "jdbc:mysql://localhost:3306/{0}";
	    String DATABASE_USERNAME = "bGFiaXp5X3VzZXI=";
	    String DATABASE_PASSWORD = "bGFiaXp5X3VzZXIwMDc=";

	    System.setProperty("environ", "local");
	    EncryptionDecryptionUtils encryptionDecryptionUtils = new EncryptionDecryptionUtils();
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
		
		PropertiesBean commonProperties = new PropertiesBean();
		Set<String> supportedEnvirons = new HashSet<String>();
		supportedEnvirons.add("local");
		supportedEnvirons.add("prod");
		supportedEnvirons.add("ppe");
		commonProperties.setSupportedEnvirons(supportedEnvirons);
		commonProperties.setEnvironSystemPropertyName("environ");
		propertiesBuilder.setCommonProperties(commonProperties);
		
		PropertiesBean localProperties = new PropertiesBean();
		localProperties.setDatabaseDriver(DATABASE_DRIVER);
		localProperties.setDatabaseUrl(DATABASE_URL);
		localProperties.setDatabaseUser(DATABASE_USERNAME);
		localProperties.setDatabasePassword(DATABASE_PASSWORD);
		propertiesBuilder.setLocalProperties(localProperties);
		
		CommonUtils commonUtils = new CommonUtils();
		commonUtils.setCommonProperties(commonProperties);
		propertiesBuilder.setCommonUtils(commonUtils);
		
		DatabaseConnection databaseConnection = new DatabaseConnection();
		databaseConnection.setPropertiesBuilder(propertiesBuilder);
		databaseConnection.setEncryptionDecryptionUtils(encryptionDecryptionUtils);
		
		daoMgr.setDatabaseConnection(databaseConnection);
		daoMgr.setCommonUtils(commonUtils);
		daoMgr.setDatabaseName("labizy_product_labs_db");
		daoMgr.setEncryptionDecryptionUtils(encryptionDecryptionUtils);
/*		
		String productId = "PRD-1487008527275-6353-4358";
		
		Map<String, String> result = null;
		result = daoMgr.getProductDetails(productId);
		System.out.println(result);
		
		List<Map<String, String>> results = null;
		results = daoMgr.searchProducts(null, true);
		System.out.println(results);
		
		String searchTagsClause = daoMgr.getSearchTagsClause("Thyro    Test Liver  ");
		System.out.println(searchTagsClause);
		
		Map<String, String> searchCriteriaMap1 = new HashMap<String, String>();
		searchCriteriaMap1.put("productId", "PRD-1487008527275-6353-4358");
		searchCriteriaMap1.put("type", "ThyroidTest");
		searchCriteriaMap1.put("searchTags", "Thyro    Test Liver  ");
		
		String searchClause = daoMgr.getProductsSearchClause(searchCriteriaMap1, true);
		System.out.println(searchClause);
		
		searchCriteriaMap1.put("subType", "");
		searchClause = daoMgr.getProductsSearchClause(searchCriteriaMap1, false);
		System.out.println(searchClause);
		
		results = daoMgr.searchProducts(searchCriteriaMap1, false);
		System.out.println(results);

		String labId = "LAB-1487008939288-6901-9191";
		result = daoMgr.getLabDetails(labId);
		System.out.println(result);
		
		Map<String, String> searchCriteriaMap2 = new HashMap<String, String>();
		searchCriteriaMap2.put("latitude", "12.92");
		searchCriteriaMap2.put("longitude", "77.58");
		searchCriteriaMap2.put("radialSearchUnit", "10");
		searchCriteriaMap2.put("radialSearchUom", "kms");
		searchCriteriaMap2.put("labId", "LAB-1487008939288-6901-9191");
		searchCriteriaMap2.put("country", "India");
		
		List<Map<String, String>> results2 = null;
		results2 = daoMgr.searchLabs(searchCriteriaMap2, true);
		System.out.println(results2);
*/		
		Map<String, String> searchCriteriaMap3 = new HashMap<String, String>();
		searchCriteriaMap3.put("latitude", "12.92");
		searchCriteriaMap3.put("longitude", "77.58");
		searchCriteriaMap3.put("radialSearchUnit", "10");
		searchCriteriaMap3.put("radialSearchUom", "kms");
		//searchCriteriaMap3.put("labId", "LAB-1487008939288-6901-9191");
		searchCriteriaMap3.put("labIds", "LAB-1487008939288-6901-9191, LAB-1487008939288-6901-9193");
		searchCriteriaMap3.put("country", "India");
		//searchCriteriaMap3.put("productId", "PRD-1487008527275-6353-4358");
		searchCriteriaMap3.put("type", "ThyroidTest");
		searchCriteriaMap3.put("searchTags", "Thyro    Test Liver  ");
		//searchCriteriaMap3.put("rankBy", "products");
		searchCriteriaMap3.put("rankBy", "labs");
		
		List<Map<String, String>> results3 = null;
		results3 = daoMgr.searchLabsProducts(searchCriteriaMap3, true);
		System.out.println(results3);
	
	}
}