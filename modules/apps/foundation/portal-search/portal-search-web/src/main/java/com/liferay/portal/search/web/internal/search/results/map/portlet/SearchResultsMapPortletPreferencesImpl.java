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

package com.liferay.portal.search.web.internal.search.results.map.portlet;

import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Lino Alves
 */
public class SearchResultsMapPortletPreferencesImpl
	implements SearchResultsMapPortletPreferences {

	public SearchResultsMapPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public double getLatitude() {
		Optional<Double> latitude = _portletPreferencesHelper.getString(
			SearchResultsMapPortletPreferences.PREFERENCE_LATITUDE).map(
				lat -> Double.parseDouble(lat));

		return latitude.orElse(
			Double.valueOf(
				SearchResultsMapPortletPreferences.DEFAULT_LATITUDE));
	}

	@Override
	public double getLongitude() {
		Optional<Double> longitude = _portletPreferencesHelper.getString(
			SearchResultsMapPortletPreferences.PREFERENCE_LONGITUDE).map(
				lng -> Double.parseDouble(lng));

		return longitude.orElse(
			Double.valueOf(
				SearchResultsMapPortletPreferences.DEFAULT_LONGITUDE));
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}