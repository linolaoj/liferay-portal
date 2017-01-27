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

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.container.SearchContainerPortletURL;
import com.liferay.portal.search.web.internal.portletsharedtask.PortletSharedURLHelper;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearch;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchResult;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchSettings;
import com.liferay.portal.search.web.portletsharedsearch.SearchAwarePortlet;
import com.liferay.portal.search.web.search.SearchResultsData;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=" +
			SearchResultsPortletKeys.CSS_CLASS_WRAPPER,
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=" +
			SearchResultsPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			SearchResultsPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + SearchResultsPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class SearchResultsPortlet
	extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		paginate(searchResultsPortletPreferences, portletSharedSearchSettings);

		highlight(searchResultsPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResult portletSharedSearchResult =
			portletSharedSearch.search(renderRequest);

		SearchResultsDisplayContext searchResultsDisplayContext =
			buildDisplayContext(
				portletSharedSearchResult, renderRequest, renderResponse);

		renderRequest.setAttribute(
			SearchResultsDisplayContext.ATTRIBUTE, searchResultsDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected SearchResultsDisplayContext buildDisplayContext(
			PortletSharedSearchResult portletSharedSearchResult,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SearchResultsData searchResultsData =
			portletSharedSearchResult.getSearchResultsData();

		SearchResultsDisplayContext searchResultsDisplayContext =
			new SearchResultsDisplayContext();

		List<Document> documents = searchResultsData.getDocuments();

		int startPage = searchResultsData.getStartPage();

		searchResultsDisplayContext.setDocuments(documents);
		searchResultsDisplayContext.setStartPage(startPage);

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearch.getPortletPreferences(renderRequest));

		String startPageParameterName =
			searchResultsPortletPreferences.getStartPageParameterName();

		searchResultsDisplayContext.setStartPageParameterName(
			startPageParameterName);

		Optional<String> keywordsOptional = searchResultsData.getKeywords();

		searchResultsDisplayContext.setKeywords(
			keywordsOptional.orElse(StringPool.BLANK));

		searchResultsDisplayContext.setSearchResultsSummariesHolder(
			buildSummaries(
				portletSharedSearchResult, renderRequest, renderResponse));

		int totalHits = searchResultsData.getTotalHits();

		searchResultsDisplayContext.setTotalHits(totalHits);

		searchResultsDisplayContext.setSearchContainer(
			buildSearchContainer(
				documents, totalHits, startPage, startPageParameterName,
				renderRequest));

		return searchResultsDisplayContext;
	}

	protected SearchContainer<Document> buildSearchContainer(
			List<Document> documents, int totalHits, int startPage,
			String startPageParameterName, RenderRequest renderRequest)
		throws PortletException {

		PortletRequest portletRequest = renderRequest;
		DisplayTerms displayTerms = null;
		DisplayTerms searchTerms = null;
		String curParam = startPageParameterName;
		int cur = startPage;
		int delta = SearchContainer.DEFAULT_DELTA;
		PortletURL iteratorURL = cleanPortletURL(
			renderRequest, startPageParameterName);
		List<String> headerNames = null;
		String emptyResultsMessage = null;
		String cssClass = null;

		SearchContainer<Document> searchContainer = new SearchContainer<>(
			portletRequest, displayTerms, searchTerms, curParam, cur, delta,
			iteratorURL, headerNames, emptyResultsMessage, cssClass);

		searchContainer.setResults(documents);
		searchContainer.setTotal(totalHits);

		return searchContainer;
	}

	protected SearchResultsSummariesHolder buildSummaries(
			PortletSharedSearchResult portletSharedSearchResult,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SearchResultsSummariesBuilder searchResultsSummariesBuilder =
			new SearchResultsSummariesBuilder(
				portletSharedSearch, portletSharedSearchResult,
				assetEntryLocalService, blogsEntryLocalService, resourceActions,
				language, renderRequest, renderResponse);

		try {
			return searchResultsSummariesBuilder.build();
		}
		catch (PortletException pe) {
			throw pe;
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	protected PortletURL cleanPortletURL(
		RenderRequest renderRequest, String startPageParameterName) {

		String urlString = portletSharedURLHelper.getURLString(renderRequest);

		urlString = HttpUtil.removeParameter(urlString, startPageParameterName);

		return new SearchContainerPortletURL(urlString);
	}

	protected void highlight(
		SearchResultsPortletPreferences searchResultsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		boolean highlightEnabled =
			searchResultsPortletPreferences.isHighlightEnabled();

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(highlightEnabled);
	}

	protected void paginate(
		SearchResultsPortletPreferences searchResultsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String startPageParameterName =
			searchResultsPortletPreferences.getStartPageParameterName();

		portletSharedSearchSettings.setStartPageParameterName(
			startPageParameterName);

		Optional<String> startPageParameterValueOptional =
			portletSharedSearchSettings.getParameter(startPageParameterName);

		Optional<Integer> startPageOptional =
			startPageParameterValueOptional.map(Integer::valueOf);

		startPageOptional.ifPresent(portletSharedSearchSettings::setStartPage);
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Reference
	protected Language language;

	@Reference
	protected PortletSharedSearch portletSharedSearch;

	@Reference
	protected PortletSharedURLHelper portletSharedURLHelper;

	@Reference
	protected ResourceActions resourceActions;

}