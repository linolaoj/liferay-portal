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
import com.liferay.portal.search.web.internal.search.request.SearchContainerBuilder;
import com.liferay.portal.search.web.internal.search.request.SearchContextBuilder;
import com.liferay.portal.search.web.internal.search.request.SearchRequestImpl;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portletsharedsearch.SearchAwarePortlet;
import com.liferay.portal.search.web.portletsharedtask.PortletSharedTaskExecutor;
import com.liferay.portal.search.web.search.request.SearchRequest;
import com.liferay.portal.search.web.search.request.SearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;
import com.liferay.portal.search.web.search.request.SearchSettingsContributor;

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
@Component(service = PortletSharedSearchRequest.class)
public class PortletSharedSearchRequestImpl
	implements PortletSharedSearchRequest {

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

	protected SearchContainer<Document> buildSearchContainer(
		SearchSettings searchSettings, RenderRequest renderRequest) {

		Optional<String> paginationStartParameterNameOptional =
			searchSettings.getPaginationStartParameterName();

		Optional<Integer> paginationStartOptional =
			searchSettings.getPaginationStart();

		Optional<Integer> paginationDeltaOptional =
			searchSettings.getPaginationDelta();

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

	protected SearchContext buildSearchContext(ThemeDisplay themeDisplay) {
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

	protected void contributeSearchSettings(
		SearchRequest searchRequest, Stream<Portlet> portletsStream,
		RenderRequest renderRequest) {

		Stream<Optional<SearchSettingsContributor>>
			searchSettingsContributorOptionalsStream = portletsStream.map(
				portlet -> getSearchSettingsContributor(
					portlet, renderRequest));

		searchSettingsContributorOptionalsStream.forEach(
			searchSettingsContributorOptional ->
				searchSettingsContributorOptional.ifPresent(
					searchRequest::addSearchSettingsContributor));
	}

	protected PortletSharedSearchResponse doSearch(
		RenderRequest renderRequest) {

		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		ThemeDisplay themeDisplay = themeDisplaySupplier.getThemeDisplay();

		SearchContextBuilder searchContextBuilder =
			() -> buildSearchContext(themeDisplay);

		SearchContainerBuilder searchContainerBuilder =
			searchSettings -> buildSearchContainer(
				searchSettings, renderRequest);

		SearchRequest searchRequest = new SearchRequestImpl(
			searchContextBuilder, searchContainerBuilder,
			facetedSearcherManager);

		Stream<Portlet> portletsStream = getExplicitlyAddedPortlets(
			themeDisplay);

		contributeSearchSettings(searchRequest, portletsStream, renderRequest);

		SearchResponse searchResponse = searchRequest.search();

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