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

/**
 * @author Lino Alves
 */
public interface SearchResultsMapPortletPreferences {

	public static final double DEFAULT_LATITUDE = 0;

	public static final double DEFAULT_LONGITUDE = 0;

	public static final String PREFERENCE_LATITUDE = "latitude";

	public static final String PREFERENCE_LONGITUDE = "longitude";

	public double getLatitude();

	public double getLongitude();

}