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

package com.liferay.portal.search.web.internal.search.facet.portlet;

import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.web.components.facet.SearchFacetConfigurationImpl;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.request.helper.LiferayPortletHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.OriginalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.PortalOriginalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.SearchLiferayPortletRequestHelper;
import com.liferay.portal.search.web.internal.request.params.SearchParameters;
import com.liferay.portal.search.web.internal.request.params.SearchParametersConfiguration;
import com.liferay.portal.search.web.internal.request.params.SearchParametersImpl;

import java.io.IOException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 * @author Rodrigo Paulino
 */
@Component(service=SearchFacetPortletRenderHelper.class)
public class SearchFacetPortletRenderHelper {

	public SearchFacet getSearchFacet(String searchFacetClassName) {
		SearchFacet searchFacet = _searchFacets.get(searchFacetClassName);

		Optional<SearchFacet> searchFacetOptional = Optional.ofNullable(
			searchFacet);

		return searchFacetOptional.orElseThrow(
			() -> new IllegalArgumentException(searchFacetClassName));
	}

	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletPreferences preferences = renderRequest.getPreferences();

		SearchFacetConfigurationImpl searchFacetConfigurationImpl =
			new SearchFacetConfigurationImpl(preferences);

		SearchParameters searchParameters = createSearchParameters(
			renderRequest, searchFacetConfigurationImpl);

		searchLiferayPortletRequestHelper.search(
			searchParameters::getQParameter, renderRequest, renderResponse);

		Optional<String> searchFacetClassNameOptional =
			searchFacetConfigurationImpl.getSearchFacetClassName();

		Optional<SearchFacet> searchFacetOptional =
			searchFacetClassNameOptional.map(this::getSearchFacet);

		searchFacetOptional.ifPresent(
			searchFacet ->
				renderRequest.setAttribute(
					"search.jsp-search-facet", searchFacet));
	}

	protected SearchParametersImpl createSearchParameters(
		RenderRequest renderRequest,
		SearchParametersConfiguration searchParametersConfiguration) {

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			new PortalOriginalHttpServletRequestSupplier(
				new LiferayPortletHttpServletRequestSupplier(renderRequest),
				portal);

		return new SearchParametersImpl(
			originalHttpServletRequestSupplier, searchParametersConfiguration);
	}


	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void addSearchFacet(SearchFacet searchFacet) {
		String searchFacetClassName = searchFacet.getClassName();

		_searchFacets.put(searchFacetClassName, searchFacet);
	}

	protected void removeSearchFacet(SearchFacet searchFacet) {
		String searchFacetClassName = searchFacet.getClassName();

		_searchFacets.remove(searchFacetClassName);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected SearchLiferayPortletRequestHelper
		searchLiferayPortletRequestHelper;

	private final Map<String, SearchFacet> _searchFacets =
		new ConcurrentHashMap<>();

}