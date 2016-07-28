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

package com.liferay.portal.search.web.components.facet;

import com.liferay.portal.search.web.facet.BaseJSPSearchFacet;
import com.liferay.portal.search.web.facet.ResourceHelper;
import com.liferay.portal.search.web.internal.request.params.SearchParameters;
import com.liferay.portal.search.web.internal.request.params.SearchParametersImpl;
import com.liferay.taglib.servlet.PipingServletResponse;

import java.io.IOException;

import java.util.Optional;

import javax.portlet.PortletPreferences;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * @author André de Oliveira
 * @author Rodrigo Paulino
 */
public class SearchFacetDisplayContext {

	public SearchFacetDisplayContext(
		HttpServletRequest request, PortletPreferences portletPreferences) {

		_request = request;

		_parameters = createSearchParameters(request, portletPreferences);

		_searchFacet = Optional.ofNullable(
			(BaseJSPSearchFacet)request.getAttribute(
				"search.jsp-search-facet"));
	}

	public String getQ() {
		return _parameters.getQParameter();
	}

	public void includeView(PageContext pageContext)
		throws IOException, ServletException {

		if (!_searchFacet.isPresent()) {
			throw new IllegalStateException(
				"Please select a Facet to display from Portlet Configuration.");
		}

		includeView(_searchFacet.get(), pageContext);
	}

	public boolean isSearchFacetConfigured() {
		return _searchFacet.isPresent();
	}

	protected SearchParametersImpl createSearchParameters(
		HttpServletRequest renderRequest, PortletPreferences preferences) {

		return new SearchParametersImpl(
			() -> renderRequest, new SearchFacetConfigurationImpl(preferences));
	}

	protected void includeView(
			BaseJSPSearchFacet searchFacet, PageContext pageContext)
		throws IOException, ServletException {

		ResourceHelper.include(
			"/components" + searchFacet.getDisplayJspPath(), _request,
			new PipingServletResponse(pageContext),
			pageContext.getServletContext());
	}

	private final SearchParameters _parameters;
	private final HttpServletRequest _request;
	private final Optional<BaseJSPSearchFacet> _searchFacet;

}