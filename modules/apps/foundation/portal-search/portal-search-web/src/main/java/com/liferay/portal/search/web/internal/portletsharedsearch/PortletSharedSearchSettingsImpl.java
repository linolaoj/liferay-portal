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

package com.liferay.portal.search.web.internal.portletsharedsearch;

import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.portletsharedtask.PortletSharedRequestHelper;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchSettings;
import com.liferay.portal.search.web.search.SearchSettings;

import java.util.Optional;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

/**
 * @author André de Oliveira
 */
public class PortletSharedSearchSettingsImpl
	implements PortletSharedSearchSettings {

	public PortletSharedSearchSettingsImpl(
		SearchSettings searchSettings,
		Optional<PortletPreferences> portletPreferencesOptional,
		PortletSharedRequestHelper portletSharedRequestHelper,
		RenderRequest renderRequest) {

		_searchSettings = searchSettings;
		_portletPreferencesOptional = portletPreferencesOptional;
		_portletSharedRequestHelper = portletSharedRequestHelper;
		_renderRequest = renderRequest;
	}

	@Override
	public void addFacet(Facet facet) {
		_searchSettings.addFacet(facet);
	}

	@Override
	public Optional<String> getParameter(String name) {
		return _portletSharedRequestHelper.getParameter(name, _renderRequest);
	}

	@Override
	public Optional<String[]> getParameterValues(String name) {
		return _portletSharedRequestHelper.getParameterValues(
			name, _renderRequest);
	}

	@Override
	public Optional<PortletPreferences> getPortletPreferences() {
		return _portletPreferencesOptional;
	}

	@Override
	public QueryConfig getQueryConfig() {
		return _searchSettings.getQueryConfig();
	}

	@Override
	public SearchContext getSearchContext() {
		return _searchSettings.getSearchContext();
	}

	@Override
	public ThemeDisplay getThemeDisplay() {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(_renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	@Override
	public void setDelta(int delta) {
		_searchSettings.setDelta(delta);
	}
	
	@Override
	public void setDeltaParameterName(String deltaParameterName) {
		_searchSettings.setDeltaParameterName(deltaParameterName);
	}
	
	@Override
	public void setKeywords(String keywords) {
		_searchSettings.setKeywords(keywords);
	}

	@Override
	public void setStartPage(int startPage) {
		_searchSettings.setStartPage(startPage);
	}

	@Override
	public void setStartPageParameterName(String startPageParameterName) {
		_searchSettings.setStartPageParameterName(startPageParameterName);
	}

	private final Optional<PortletPreferences> _portletPreferencesOptional;
	private final PortletSharedRequestHelper _portletSharedRequestHelper;
	private final RenderRequest _renderRequest;
	private final SearchSettings _searchSettings;

}