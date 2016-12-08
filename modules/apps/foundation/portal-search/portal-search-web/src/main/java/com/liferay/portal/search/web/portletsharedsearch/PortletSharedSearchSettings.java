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

package com.liferay.portal.search.web.portletsharedsearch;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public interface PortletSharedSearchSettings {

	public void addFacet(Facet facet);

	public Optional<String> getParameter(String name);

	public Optional<String[]> getParameterValues(String name);

	public Optional<PortletPreferences> getPortletPreferences();

	public SearchContext getSearchContext();

	public ThemeDisplay getThemeDisplay();

	public void setKeywords(String keywords);

	public void setStartPage(int startPage);

	public void setStartPageParameterName(String startPageParameterName);

}