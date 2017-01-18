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

/**
 * @author Lino Alves
 */
public class CalendarRangeSearchFacetTermDisplayContext 
	implements Serializable{
		
	public String getFromDayParam() {
		return _fromDayParam;
	}
	
	public int getFromDayValue() {
		return _fromDayValue;
	}

	public int getFromFirstDayOfWeek() {
		return _fromFirstDayOfWeek;
	}
	
	public String getFromMonthParam() {
		return _fromMonthParam;
	}
	
	public int getFromMonthValue() {
		return _fromMonthValue;
	}
	
	public String getFromName() {
		return _fromName;
	}
	
	public String getFromYearParam() {
		return _fromYearParam;
	}
	
	public int getFromYearValue() {
		return _fromYearValue;
	}
	
	public String getToDayParam() {
		return _toDayParam;
	}
	
	public int getToDayValue() {
		return _toDayValue;
	}
	
	public int getToFirstDayOfWeek() {
		return _toFirstDayOfWeek;
	}
	
	public String getToMonthParam() {
		return _toMonthParam;
	}
	
	public int getToMonthValue() {
		return _toMonthValue;
	}
	
	public String getToName() {
		return _toName;
	}
	
	public String getToYearParam() {
		return _toYearParam;
	}
	
	public int getToYearValue() {
		return _toYearValue;
	}
	
	public boolean isFromBeforeTo() {
		return _fromBeforeTo;
	}
	
	public void setFromBeforeTo(boolean fromBeforeTo) {
		_fromBeforeTo = fromBeforeTo;
	}
	
	public void setFromDayParam(String fromDayParam) {
		_fromDayParam = fromDayParam;
	}
	
	public void setFromDayValue(int fromDayValue) {
		_fromDayValue = fromDayValue;
	}
	
	public void setFromFirstDayOfWeek(int fromFirstDayOfWeek) {
		_fromFirstDayOfWeek = fromFirstDayOfWeek;
	}
	
	public void setFromMonthParam(String fromMonthParam) {
		_fromMonthParam = fromMonthParam;
	}
	
	public void setFromMonthValue(int fromMonthValue) {
		_fromMonthValue = fromMonthValue;
	}
	
	public void setFromName(String fromName) {
		_fromName = fromName;
	}
	
	public void setFromYearParam(String fromYearParam) {
		_fromYearParam = fromYearParam;
	}
	
	public void setFromYearValue(int fromYearValue) {
		_fromYearValue = fromYearValue;
	}
	
	public void setToDayParam(String toDayParam) {
		_toDayParam = toDayParam;
	}
	
	public void setToDayValue(int toDayValue) {
		_toDayValue = toDayValue;
	}
	
	public void setToFirstDayOfWeek(int toFirstDayOfWeek) {
		_toFirstDayOfWeek = toFirstDayOfWeek;
	}
	
	public void setToMonthParam(String toMonthParam) {
		_toMonthParam = toMonthParam;
	}
	
	public void setToMonthValue(int toMonthValue) {
		_toMonthValue = toMonthValue;
	}
	
	public void setToName(String toName) {
		_toName = toName;
	}
	
	public void setToYearParam(String toYearParam) {
		_toYearParam = toYearParam;
	}
	
	public void setToYearValue(int toYearValue) {
		_toYearValue = toYearValue;
	}
	
	private String _toYearParam;

	private int _toYearValue;
	
	private boolean _fromBeforeTo;
	
	private String _fromDayParam;
	
	private int _fromDayValue;
	
	private int _fromFirstDayOfWeek;
	
	private String _fromMonthParam;
	
	private int _fromMonthValue;
	
	private String _fromName;
	
	private String _fromYearParam;
	
	private int _fromYearValue;
	
	private String _toDayParam;
	
	private int _toDayValue;
	
	private int _toFirstDayOfWeek;
	
	private String _toMonthParam;
	
	private int _toMonthValue;
	
	private String _toName;
	
	private boolean _selected;

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	public boolean isSelected() {
		return _selected;
	}
}
