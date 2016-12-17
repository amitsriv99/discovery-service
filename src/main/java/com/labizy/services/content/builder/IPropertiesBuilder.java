package com.labizy.services.content.builder;

import com.labizy.services.content.beans.PropertiesBean;
import com.labizy.services.content.exceptions.EnvironNotDefPropertiesBuilderException;

public interface IPropertiesBuilder {
	public PropertiesBean getCommonProperties();
	public PropertiesBean getEnvironProperties() throws EnvironNotDefPropertiesBuilderException;
}
