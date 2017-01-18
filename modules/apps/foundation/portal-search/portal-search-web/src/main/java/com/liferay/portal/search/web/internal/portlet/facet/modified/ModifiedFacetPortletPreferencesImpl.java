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

package com.liferay.portal.search.web.internal.portlet.facet.modified;

import java.util.Optional;

import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.portlet.facet.type.TypeFacetPortletPreferences;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

/**
 * @author Lino Alves
 */
public class ModifiedFacetPortletPreferencesImpl implements ModifiedFacetPortletPreferences {

	public ModifiedFacetPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferences) {
		_portletPreferencesHelper = new PortletPreferencesHelper(
				portletPreferences);
	}

	@Override
	public String getParamName() {
		return _portletPreferencesHelper.getString(
			ModifiedFacetPortletPreferences.PREFERENCE_PARAM_NAME,
			ModifiedFacetPortletPreferences.DEFAULT_PARAM_NAME);
	}

	@Override
	public JSONArray getRangesJSONArray() {
		
		String currentRanges = getRanges();
		
		if(Validator.isNull(currentRanges)) {
			return getDefaultRangesJSONArray();
		}
		
		JSONArray rangesJSONArray = null;

		try {
			rangesJSONArray = JSONFactoryUtil.createJSONArray(currentRanges);
		} 
		catch (JSONException e) {
			rangesJSONArray = getDefaultRangesJSONArray();
		} 
		
		return rangesJSONArray;
	}
	
	protected JSONArray getDefaultRangesJSONArray() {

		JSONArray rangesJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < _LABELS.length; i++) {
			JSONObject range = JSONFactoryUtil.createJSONObject();

			range.put("label", _LABELS[i]);
			range.put("range", _RANGES[i]);

			rangesJSONArray.put(range);
		}

		return rangesJSONArray;
	}
	
	@Override
	public String getRanges() {
		return _portletPreferencesHelper.getString(
				ModifiedFacetPortletPreferences.PREFERENCE_RANGES,
				StringPool.BLANK);
	}

	private static final String[] _LABELS = new String[] {
		"past-hour", "past-24-hours", "past-week", "past-month", "past-year"
	};

	private static final String[] _RANGES = new String[] {
		"[past-hour TO *]", "[past-24-hours TO *]", "[past-week TO *]",
		"[past-month TO *]", "[past-year TO *]"
	};
	
	private final PortletPreferencesHelper _portletPreferencesHelper;
}
