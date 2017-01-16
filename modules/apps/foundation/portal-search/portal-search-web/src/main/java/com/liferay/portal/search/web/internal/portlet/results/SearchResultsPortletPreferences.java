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

package com.liferay.portal.search.web.internal.portlet.results;

import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;

/**
 * @author Lino Alves
 */
public interface SearchResultsPortletPreferences extends SearchResultPreferences{
	
	public static final boolean DEFAULT_DISPLAY_AS_FORM = true;

	public static final boolean DEFAULT_VIEW_IN_CONTEXT = true;
	
	public static final String PREFERENCE_DISPLAY_AS_FORM =
		"displayAsForm";
	
	public static final String PREFERENCE_VIEW_IN_CONTEXT =
		"viewInContext";

}
