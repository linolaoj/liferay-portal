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

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.internal.container.SearchContainerPortletURL;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.portletsharedtask.PortletSharedRequestHelper;
import com.liferay.portal.search.web.internal.portletsharedtask.PortletSharedURLHelper;
import com.liferay.portal.search.web.internal.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.search.SearchContainerOptions;
import com.liferay.portal.search.web.internal.search.SearchContainerSupplier;
import com.liferay.portal.search.web.internal.search.SearchContextSupplier;
import com.liferay.portal.search.web.internal.search.SearchImpl;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearch;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portletsharedsearch.SearchAwarePortlet;
import com.liferay.portal.search.web.portletsharedtask.PortletSharedTaskExecutor;
import com.liferay.portal.search.web.search.Search;
import com.liferay.portal.search.web.search.SearchResponse;
import com.liferay.portal.search.web.search.SearchSettingsContributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 */
@Component(service = PortletSharedSearch.class)
public class PortletSharedSearchImpl implements PortletSharedSearch {

	@Override
	public PortletSharedSearchResponse search(RenderRequest renderRequest) {
		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedTaskExecutor.executeOnlyOnce(
				() -> doSearch(renderRequest),
				PortletSharedSearchResponse.class.getSimpleName(),
				renderRequest);

		return portletSharedSearchResponse;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void addSearchAwarePortlet(
		SearchAwarePortlet searchAwarePortlet) {

		Class<?> clazz = searchAwarePortlet.getClass();

		String portletClassName = clazz.getName();

		_searchAwareFacetPortlets.put(portletClassName, searchAwarePortlet);
	}

	protected void contributeSearchSettings(
		Search search, Stream<Portlet> portletsStream,
		RenderRequest renderRequest) {

		Stream<Optional<SearchSettingsContributor>>
			searchSettingsContributorOptionalsStream = portletsStream.map(
				portlet -> getSearchSettingsContributor(
					portlet, renderRequest));

		searchSettingsContributorOptionalsStream.forEach(
			searchSettingsContributorOptional ->
				searchSettingsContributorOptional.ifPresent(
					search::addSearchSettingsContributor));
	}

	protected SearchContainer<Document> createSearchContainer(
		SearchContainerOptions searchContainerOptions,
		RenderRequest renderRequest) {

		Optional<String> paginationStartParameterNameOptional =
			searchContainerOptions.getPaginationStartParameterName();

		Optional<Integer> paginationStartOptional =
			searchContainerOptions.getPaginationStart();

		Optional<Integer> paginationDeltaOptional =
			searchContainerOptions.getPaginationDelta();

		PortletRequest portletRequest = renderRequest;
		DisplayTerms displayTerms = null;
		DisplayTerms searchTerms = null;
		String curParam = paginationStartParameterNameOptional.orElse(
			SearchContainer.DEFAULT_CUR_PARAM);
		int cur = paginationStartOptional.orElse(0);
		int delta = paginationDeltaOptional.orElse(
			SearchContainer.DEFAULT_DELTA);
		PortletURL iteratorURL = new SearchContainerPortletURL(
			portletSharedURLHelper.getURLString(renderRequest));
		List<String> headerNames = null;
		String emptyResultsMessage = null;
		String cssClass = null;

		SearchContainer<Document> searchContainer = new SearchContainer<>(
			portletRequest, displayTerms, searchTerms, curParam, cur, delta,
			iteratorURL, headerNames, emptyResultsMessage, cssClass);

		return searchContainer;
	}

	protected SearchContext createSearchContext(ThemeDisplay themeDisplay) {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setLayout(themeDisplay.getLayout());
		searchContext.setLocale(themeDisplay.getLocale());
		searchContext.setTimeZone(themeDisplay.getTimeZone());
		searchContext.setUserId(themeDisplay.getUserId());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setLocale(themeDisplay.getLocale());

		return searchContext;
	}

	protected PortletSharedSearchResponse doSearch(
		RenderRequest renderRequest) {

		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		ThemeDisplay themeDisplay = themeDisplaySupplier.getThemeDisplay();

		SearchContextSupplier searchContextSupplier =
			() -> createSearchContext(themeDisplay);

		SearchContainerSupplier searchContainerSupplier =
			searchContainerOptions -> createSearchContainer(
				searchContainerOptions, renderRequest);

		Search search = new SearchImpl(
			searchContextSupplier, searchContainerSupplier,
			facetedSearcherManager);

		Stream<Portlet> portletsStream = getExplicitlyAddedPortlets(
			themeDisplay);

		contributeSearchSettings(search, portletsStream, renderRequest);

		SearchResponse searchResponse = search.search();

		return new PortletSharedSearchResponseImpl(
			searchResponse, portletSharedRequestHelper);
	}

	protected Stream<Portlet> getExplicitlyAddedPortlets(
		ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		List<Portlet> portlets = layoutTypePortlet.getExplicitlyAddedPortlets();

		return portlets.stream();
	}

	protected Optional<SearchAwarePortlet> getSearchAwarePortlet(
		String portletClassName) {

		return Optional.ofNullable(
			_searchAwareFacetPortlets.get(portletClassName));
	}

	protected Optional<SearchSettingsContributor> getSearchSettingsContributor(
		Portlet portlet, RenderRequest renderRequest) {

		Optional<SearchAwarePortlet> searchAwarePortletOptional =
			getSearchAwarePortlet(portlet.getPortletClass());

		Optional<SearchSettingsContributor> searchSettingsContributorOptional =
			searchAwarePortletOptional.map(
				searchAwarePortlet -> getSearchSettingsContributor(
					searchAwarePortlet, portlet.getPortletId(), renderRequest));

		return searchSettingsContributorOptional;
	}

	protected SearchSettingsContributor getSearchSettingsContributor(
		SearchAwarePortlet searchAwarePortlet, String portletId,
		RenderRequest renderRequest) {

		Optional<PortletPreferences> portletPreferencesOptional =
			portletPreferencesLookup.getPortletPreferences(
				renderRequest, portletId);

		return searchSettings -> searchAwarePortlet.contribute(
			new PortletSharedSearchSettingsImpl(
				searchSettings, portletPreferencesOptional,
				portletSharedRequestHelper, renderRequest));
	}

	protected void removeSearchAwarePortlet(
		SearchAwarePortlet searchAwarePortlet) {

		Class<?> clazz = searchAwarePortlet.getClass();

		String portletClassName = clazz.getName();

		_searchAwareFacetPortlets.remove(portletClassName);
	}

	@Reference
	protected FacetedSearcherManager facetedSearcherManager;

	@Reference
	protected PortletPreferencesLookup portletPreferencesLookup;

	@Reference
	protected PortletSharedRequestHelper portletSharedRequestHelper;

	@Reference
	protected PortletSharedTaskExecutor portletSharedTaskExecutor;

	@Reference
	protected PortletSharedURLHelper portletSharedURLHelper;

	private final Map<String, SearchAwarePortlet> _searchAwareFacetPortlets =
		new HashMap<>();

}