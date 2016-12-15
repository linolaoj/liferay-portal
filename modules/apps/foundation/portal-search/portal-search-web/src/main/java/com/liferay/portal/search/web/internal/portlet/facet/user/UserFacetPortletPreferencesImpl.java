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

package com.liferay.portal.search.web.internal.portlet.facet.user;

import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Lino Alves
 */
public class UserFacetPortletPreferencesImpl
	implements UserFacetPortletPreferences {

	public UserFacetPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public int getFrequencyThreshold() {
		return _portletPreferencesHelper.getInteger(
			UserFacetPortletPreferences.PREFERENCE_FREQUENCY_THRESHOLD,
			UserFacetConfiguration.DEFAULT_FREQUENCY_THRESHOLD);
	}

	@Override
	public int getMaxTerms() {
		return _portletPreferencesHelper.getInteger(
			UserFacetPortletPreferences.PREFERENCE_MAX_TERMS,
			UserFacetConfiguration.DEFAULT_MAX_TERMS);
	}

	@Override
	public String getParamName() {
		return _portletPreferencesHelper.getString(
			UserFacetPortletPreferences.PREFERENCE_PARAM_NAME,
			UserFacetPortletPreferences.DEFAULT_PARAM_NAME);
	}

	@Override
	public boolean isFrequenciesVisible() {
		return _portletPreferencesHelper.getBoolean(
			UserFacetPortletPreferences.PREFERENCE_FREQUENCIES_VISIBLE,
			UserFacetPortletPreferences.DEFAULT_FREQUENCIES_VISIBLE);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}