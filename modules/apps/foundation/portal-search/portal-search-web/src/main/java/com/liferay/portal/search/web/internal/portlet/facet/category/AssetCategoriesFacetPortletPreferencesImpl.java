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

package com.liferay.portal.search.web.internal.portlet.facet.category;

import javax.portlet.PortletPreferences;

import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

/**
 * @author Lino Alves
 */
public class AssetCategoriesFacetPortletPreferencesImpl 
	implements AssetCategoriesFacetPortletPreferences{

	public AssetCategoriesFacetPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferences);
	}

	@Override
	public String getDisplayStyle() {
		return _portletPreferencesHelper.getString(
			AssetCategoriesFacetPortletPreferences.PREFERENCE_DISPLAY_STYLE).orElse(
				AssetCategoriesFacetPortletPreferences.DEFAULT_DISPLAY_STYLE);
	}
	@Override
	public int getFrequencyThreshold() {
		return _portletPreferencesHelper.getInteger(
			AssetCategoriesFacetPortletPreferences.PREFERENCE_FREQUENCY_THRESHOLD,
			AssetCategoriesFacetConfiguration.DEFAULT_FREQUENCY_THRESHOLD);
	}

	@Override
	public int getMaxTerms() {
		return _portletPreferencesHelper.getInteger(
			AssetCategoriesFacetPortletPreferences.PREFERENCE_MAX_TERMS,
			AssetCategoriesFacetConfiguration.DEFAULT_MAX_TERMS);
	}
	
	@Override
	public String getParamName() {
		return _portletPreferencesHelper.getString(
			AssetCategoriesFacetPortletPreferences.PREFERENCE_PARAM_NAME).orElse(
				AssetCategoriesFacetPortletPreferences.DEFAULT_PARAM_NAME);
	}
	
	@Override
	public boolean isFrequenciesVisible() {
		return _portletPreferencesHelper.getBoolean(
			AssetCategoriesFacetPortletPreferences.PREFERENCE_FREQUENCIES_VISIBLE,
			AssetCategoriesFacetPortletPreferences.DEFAULT_FREQUENCIES_VISIBLE);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}
