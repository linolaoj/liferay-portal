/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lino Alves
 */
public class ModifiedSearchFacetDisplayContext 
	implements Serializable{
	
	public static final String ATTRIBUTE = "modifiedSearchFacetDisplayContext";

	public CalendarRangeSearchFacetTermDisplayContext getCalendarRangeTermDisplayContext() {
		return _calendarRangeTermDisplayContext;
	}

	public ModifiedSearchFacetTermDisplayContext 
		getCustomRangeTermDisplayContext() {
		return _customRangeTermDisplayContext;
	}

	public ModifiedSearchFacetTermDisplayContext 
		getDefaultTermDisplayContext() {
		return _defaultTermDisplayContext;
	}
	
	public String getParamName() {
		return _paramName;
	}

	public String getParamSelection() {
		return _paramSelection;
	}

	public String getParamValue() {
		return _paramValue;
	}

	public List<ModifiedSearchFacetTermDisplayContext>
		getTermDisplayContexts() {
		return _modifiedSearchFacetTermDisplayContext;
	}

	public boolean isNothingSelected() {
		return _nothingSelected;
	}
	
	public void setCalendarRangeTermDisplayContext(
		CalendarRangeSearchFacetTermDisplayContext calendarRangeTermDisplayContext) {
		_calendarRangeTermDisplayContext = calendarRangeTermDisplayContext;
	}

	public void setCustomRangeTermDisplayContext(
		ModifiedSearchFacetTermDisplayContext customRangeTermDisplayContext) {
		_customRangeTermDisplayContext = customRangeTermDisplayContext;
	}
	
	public void setDefaultTermDisplayContext(
		ModifiedSearchFacetTermDisplayContext defaultTermDisplayContext) {
		_defaultTermDisplayContext = defaultTermDisplayContext;
	}
	
	public void setParamName(String paramName) {
		_paramName = paramName;
	}
	
	public void setParamSelection(String paramSelection) {
		_paramSelection = paramSelection;
	}
	
	public void setParamValue(String paramValue) {
		_paramValue = paramValue;
	}
	
	public void setTermDisplayContexts(
		List<ModifiedSearchFacetTermDisplayContext>
			modifiedSearchFacetTermDisplayContext) {
		_modifiedSearchFacetTermDisplayContext =
			modifiedSearchFacetTermDisplayContext;
	}
	
	public void setNothingSelected(boolean nothingSelected) {
		_nothingSelected = nothingSelected;
	}
	
	private List<ModifiedSearchFacetTermDisplayContext>
	_modifiedSearchFacetTermDisplayContext;

	private ModifiedSearchFacetTermDisplayContext _defaultTermDisplayContext;
	
	private ModifiedSearchFacetTermDisplayContext _customRangeTermDisplayContext;
	
	private CalendarRangeSearchFacetTermDisplayContext _calendarRangeTermDisplayContext;
	
	private String _paramValue;
	
	private String _paramName;
	
	private String _paramSelection;
	
	private boolean _nothingSelected;

}