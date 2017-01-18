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

import com.liferay.portal.kernel.json.JSONArray;

/**
 * @author Lino Alves
 */
public interface ModifiedFacetPortletPreferences {

	public static final String PREFERENCE_FREQUENCIES_VISIBLE =
			"frequenciesVisible";

	public static final String PREFERENCE_FREQUENCY_THRESHOLD =
		"frequencyThreshold";

	public static final String PREFERENCE_PARAM_NAME = "paramName";

	public static final String DEFAULT_PARAM_NAME = ModifiedFacetConstants.FIELD_NAME;

	public static final String PREFERENCE_RANGES = "ranges";

	public String getParamName();
	
	public JSONArray getRangesJSONArray();

	public String getRanges();

	
}
