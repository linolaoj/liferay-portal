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

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.search.request.SearchResponse;

import java.io.IOException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 * @author Rodrigo Paulino
 */
@Component(service = SearchFacetPortletRenderHelper.class)
public class SearchFacetPortletRenderHelper {

	public SearchFacet getSearchFacet(String searchFacetClassName) {
		SearchFacet searchFacet = _searchFacets.get(searchFacetClassName);

		Optional<SearchFacet> searchFacetOptional = Optional.ofNullable(
			searchFacet);

		return searchFacetOptional.orElseThrow(
			() -> new IllegalArgumentException(searchFacetClassName));
	}

	public void render(RenderRequest renderRequest)
		throws IOException, PortletException {

		SearchFacetConfiguration searchFacetConfigurationImpl =
			new SearchFacetConfigurationImpl(
				Optional.ofNullable(renderRequest.getPreferences()));

		SearchResponse searchResponse = portletSharedSearchRequest.search(
			renderRequest);

		Optional<String> searchFacetClassNameOptional =
			searchFacetConfigurationImpl.getSearchFacetClassName();

		Optional<SearchFacet> searchFacetOptional =
			searchFacetClassNameOptional.map(this::getSearchFacet);

		searchFacetOptional.ifPresent(
			searchFacet -> {
				renderRequest.setAttribute(
					"search.jsp-search-facet", searchFacet);

				Facet facet = searchResponse.getFacet(
					searchFacet.getFieldName());

				renderRequest.setAttribute("search.jsp-facet", facet);
			});

		Optional<String> keywordsOptional = searchResponse.getKeywords();

		SearchFacetDisplayContext displayContext =
			new SearchFacetDisplayContext(
				keywordsOptional.orElse(StringPool.BLANK), searchResponse);

		renderRequest.setAttribute(
			SearchFacetDisplayContext.ATTRIBUTE, displayContext);
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
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private final Map<String, SearchFacet> _searchFacets =
		new ConcurrentHashMap<>();

}