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

package com.liferay.portal.search.web.internal.facet.display.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.facet.display.context.CalendarRangeSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext;

/**
 * @author Lino Alves
 */
public class ModifiedSearchFacetDisplayBuilder implements Serializable{

	public ModifiedSearchFacetDisplayContext build()
			throws PortalException {

		ModifiedSearchFacetDisplayContext
			modifiedSearchFacetDisplayContext =
				new ModifiedSearchFacetDisplayContext();
	
		modifiedSearchFacetDisplayContext.setParamName(_paramName);
		modifiedSearchFacetDisplayContext.setParamValue(_paramValue);
	
		modifiedSearchFacetDisplayContext.setDefaultTermDisplayContext(
				buildDefaultTermDisplay());
		
		modifiedSearchFacetDisplayContext.setNothingSelected(Validator.isNull(_paramValue));

		List<ModifiedSearchFacetTermDisplayContext> termDisplayContexts = 
				buildTermDisplayContexts(); 
		
		modifiedSearchFacetDisplayContext.setTermDisplayContexts(
			termDisplayContexts);
		
		modifiedSearchFacetDisplayContext.setCustomRangeTermDisplayContext(
			buildCustomRangeTermDisplayContext());
		
		modifiedSearchFacetDisplayContext.setCalendarRangeTermDisplayContext(
				buildCalendarRangeTermDisplayContext());
		
		return modifiedSearchFacetDisplayContext;
	}
	
	protected CalendarRangeSearchFacetTermDisplayContext buildCalendarRangeTermDisplayContext() {
		CalendarRangeSearchFacetTermDisplayContext calendarRangeTermDisplayContext = new CalendarRangeSearchFacetTermDisplayContext();

		Date fromDate = PortalUtil.getDate(_fromMonth, _fromDay, _fromYear);
		Date toDate = PortalUtil.getDate(_toMonth, _toDay, _toYear);
		
		Calendar fromCalendar = CalendarFactoryUtil.getCalendar(_timeZone, _locale);

		if (Validator.isNotNull(fromDate)) {
			fromCalendar.setTime(fromDate);
		}
		else {
			fromCalendar.add(Calendar.DATE, -1);
		}

		Calendar toCalendar = CalendarFactoryUtil.getCalendar(_timeZone, _locale);

		if (Validator.isNotNull(toDate)) {
			toCalendar.setTime(toDate);
		}
		
		int index = _rangesJSONArray.length() + 1;
		
		boolean selected = _paramSelection.equals(String.valueOf(index + 1));
		
		String facetFieldId = HtmlUtil.escapeJS(_facet.getFieldId());
		
		calendarRangeTermDisplayContext.setSelected(selected);
		
		calendarRangeTermDisplayContext.setFromDayParam( facetFieldId + "dayFrom");
		calendarRangeTermDisplayContext.setFromDayValue(fromCalendar.get(Calendar.DATE));
		calendarRangeTermDisplayContext.setFromFirstDayOfWeek(fromCalendar.getFirstDayOfWeek() - 1);
		calendarRangeTermDisplayContext.setFromMonthParam(facetFieldId + "monthFrom");
		calendarRangeTermDisplayContext.setFromMonthValue(fromCalendar.get(Calendar.MONTH));
		calendarRangeTermDisplayContext.setFromName(facetFieldId + "from");
		calendarRangeTermDisplayContext.setFromYearParam(facetFieldId + "yearFrom");
		calendarRangeTermDisplayContext.setFromYearValue(fromCalendar.get(Calendar.YEAR));
		
		calendarRangeTermDisplayContext.setToDayParam(facetFieldId + "dayTo");
		calendarRangeTermDisplayContext.setToDayValue(toCalendar.get(Calendar.DATE));
		calendarRangeTermDisplayContext.setToFirstDayOfWeek(toCalendar.getFirstDayOfWeek() - 1);
		calendarRangeTermDisplayContext.setToMonthParam(facetFieldId + "monthTo");
		calendarRangeTermDisplayContext.setToMonthValue(toCalendar.get(Calendar.MONTH));
		calendarRangeTermDisplayContext.setToName(facetFieldId + "to");
		calendarRangeTermDisplayContext.setToYearParam(facetFieldId + "yearTo");
		calendarRangeTermDisplayContext.setToYearValue(toCalendar.get(Calendar.YEAR));
		
		boolean fromBeforeTo = 
			fromCalendar.getTimeInMillis() < toCalendar.getTimeInMillis();
		
		calendarRangeTermDisplayContext.setFromBeforeTo(fromBeforeTo);
		
		return calendarRangeTermDisplayContext;
	}
	
	protected ModifiedSearchFacetTermDisplayContext 
		buildCustomRangeTermDisplayContext() {
		
		int index = _rangesJSONArray.length() + 1;		
		
		boolean selected = isSelected(index);
		Map<String, Object> data = new HashMap<>();
		
		FacetCollector facetCollector =_facet.getFacetCollector();
		
		TermCollector termCollector = null;
		if (selected) {
			termCollector = facetCollector.getTermCollector(_paramValue);
		}
		
		ModifiedSearchFacetTermDisplayContext
			customRangeTermDisplayContext = 
				buildTermDisplay(StringPool.BLANK, selected, 
					data, getFrequency(termCollector));
	
		return customRangeTermDisplayContext;
		
	}

	protected ModifiedSearchFacetTermDisplayContext 
		buildDefaultTermDisplay() {
		
		Map<String, Object> data = new HashMap<>();

		data.put("selection", 0);
		data.put("value", StringPool.BLANK);
		
		boolean selected = _paramSelection.equals("0");
		int frequency = 0;
		
		ModifiedSearchFacetTermDisplayContext
			defaultTermDisplayContext = buildTermDisplay(
					_facet.getFacetConfiguration().getLabel(),
					selected , data, frequency);
		
		return defaultTermDisplayContext;
	}
	
	protected ModifiedSearchFacetTermDisplayContext buildTermDisplay(
		String label, boolean selected, Map<String, Object> data,
		int frequency) {
	
		ModifiedSearchFacetTermDisplayContext
			modifiedSearchFacetTermDisplayContext =
				new ModifiedSearchFacetTermDisplayContext();
		
		modifiedSearchFacetTermDisplayContext.setFrequency(frequency);
		modifiedSearchFacetTermDisplayContext.setSelected(selected);
		modifiedSearchFacetTermDisplayContext.setLabel(label);
		modifiedSearchFacetTermDisplayContext.setData(data);
	
		if(data.containsKey("value")) {
			modifiedSearchFacetTermDisplayContext.setRange(
				data.get("value").toString());
		}
		
		return modifiedSearchFacetTermDisplayContext;
	}
	
	protected List<ModifiedSearchFacetTermDisplayContext> buildTermDisplayContexts()
		throws PortalException {
	
		List<ModifiedSearchFacetTermDisplayContext>
			modifiedSearchFacetTermDisplayContexts = new ArrayList<>();
	
		FacetCollector facetCollector = _facet.getFacetCollector();
	
		int index = 0;
		
		for (int i = 0; i < _rangesJSONArray.length(); i++) {
			JSONObject rangeJSONObject = _rangesJSONArray.getJSONObject(i);

			String label = rangeJSONObject.getString("label");
			String range = rangeJSONObject.getString("range");

			index = (i + 1);
			
			boolean selected = _paramSelection.equals(String.valueOf(index));
			
			Map<String, Object> data = new HashMap<>();

			data.put("selection", index);
			data.put("value", HtmlUtil.escape(range));
			
			TermCollector termCollector = null; 
			
			if(facetCollector != null) {
				termCollector = facetCollector.getTermCollector(range);
			}
			
			ModifiedSearchFacetTermDisplayContext
				modifiedSearchFacetTermDisplayContext = buildTermDisplay(
					label, selected,
					data, getFrequency(termCollector));

			modifiedSearchFacetTermDisplayContexts.add(
				modifiedSearchFacetTermDisplayContext);
		}
		
		return modifiedSearchFacetTermDisplayContexts;
	}
	
	protected int getFrequency(TermCollector termCollector) {
		if(termCollector != null){
			return termCollector.getFrequency();
		}
		
		return 0;
	}

	protected boolean isSelected(int index) {
		return _paramSelection.equals(String.valueOf(index));
	}
	
	public void setFacet(Facet facet) {
		_facet = facet;
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
	public void setRangesJSONArray(JSONArray rangesJSONArray) {
		_rangesJSONArray = rangesJSONArray;
	}

	public int getFromDay() {
		return _fromDay;
	}

	public int getFromMonth() {
		return _fromMonth;
	}
	
	public int getFormYear() {
		return _fromYear;
	}

	public int getToDay() {
		return _toDay;
	}

	public int getToMonth() {
		return _toMonth;
	}
	
	public int getToYear() {
		return _toYear;
	}
	
	public void setFormDay(int fromDay) {
		_fromDay = fromDay;
	}

	public void setFormMonth(int fromMonth) {
		_fromMonth = fromMonth;
	}

	public void setFormYear(int fromYear) {
		_fromYear = fromYear;
	}

	public void setToDay(int toDay) {
		_toDay = toDay;
	}

	public void setToMonth(int toMonth) {
		_toMonth = toMonth;
	}

	public void setToYear(int toYear) {
		_toYear = toYear;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public TimeZone getTimeZone() {
		return _timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	private int _fromDay;
	private int _fromMonth;
	private int _fromYear;
	private int _toDay;
	private int _toMonth;
	private int _toYear;
	
	private Facet _facet;
	
	private Locale _locale;
	
	private String _paramName;
	
	private String _paramSelection;
	
	private String _paramValue;
	
	private JSONArray _rangesJSONArray;
	
	private TimeZone _timeZone;
	
}