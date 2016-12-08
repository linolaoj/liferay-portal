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

package com.liferay.portal.search.web.internal.portlet.shared;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.portlet.shared.PortletSharedSearchResult;
import com.liferay.portal.search.web.search.SearchResultsData;

import java.util.Optional;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

/**
 * @author André de Oliveira
 */
public class PortletSharedSearchResultImpl
	implements PortletSharedSearchResult {

	@Override
	public Facet getFacet(String name) {
		return _searchContext.getFacet(name);
	}

	@Override
	public Optional<String[]> getParameterValues(String name) {
		return _portletSharedRequestHelper.getParameterValues(
			name, _renderRequest);
	}

	@Override
	public Optional<PortletPreferences> getPortletPreferences() {
		return Optional.ofNullable(_renderRequest.getPreferences());
	}

	@Override
	public SearchResultsData getSearchResultsData() {
		return _searchResultsData;
	}

	@Override
	public ThemeDisplay getThemeDisplay() {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(_renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	public void setPortletSharedRequestHelper(
		PortletSharedRequestHelper portletSharedRequestHelper) {

		_portletSharedRequestHelper = portletSharedRequestHelper;
	}

	public void setRenderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public void setSearchContext(SearchContext searchContext) {
		_searchContext = searchContext;
	}

	public void setSearchResultsData(SearchResultsData searchResultsData) {
		_searchResultsData = searchResultsData;
	}

	private PortletSharedRequestHelper _portletSharedRequestHelper;
	private RenderRequest _renderRequest;
	private SearchContext _searchContext;
	private SearchResultsData _searchResultsData;

}