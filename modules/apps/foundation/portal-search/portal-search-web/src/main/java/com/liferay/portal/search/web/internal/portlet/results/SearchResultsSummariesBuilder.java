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
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactoryImpl;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.document.DocumentFormChecker;
import com.liferay.portal.search.web.internal.document.DocumentFormCheckerImpl;
import com.liferay.portal.search.web.internal.portletsharedtask.LiferayPortletRequestUtil;
import com.liferay.portal.search.web.internal.result.display.builder.SearchResultSummaryDisplayBuilder;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.SearchResponse;

import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author André de Oliveira
 */
public class SearchResultsSummariesBuilder {

	public SearchResultsSummariesBuilder(
		PortletSharedSearchResponse portletSharedSearchResponse,
		AssetEntryLocalService assetEntryLocalService,
		BlogsEntryLocalService blogsEntryLocalService,
		ResourceActions resourceActions, Language language,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		DocumentFormChecker documentFormChecker = new DocumentFormCheckerImpl(
			themeDisplay);

		_searchResponse = portletSharedSearchResponse;

		_searchResultPreferences = new SearchResultPreferencesImpl(
			searchResultsPortletPreferences, documentFormChecker);

		_portletURLFactory = new PortletURLFactoryImpl(
			renderRequest, renderResponse);

		_searchResultsPortletPreferences = searchResultsPortletPreferences;
		_themeDisplay = themeDisplay;
		_assetEntryLocalService = assetEntryLocalService;
		_blogsEntryLocalService = blogsEntryLocalService;
		_resourceActions = resourceActions;
		_language = language;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchResultsSummariesHolder build() throws Exception {
		List<Document> documents = _searchResponse.getDocuments();

		SearchResultsSummariesHolder searchResultsSummariesHolder =
			new SearchResultsSummariesHolder(documents.size());

		for (Document document : documents) {
			SearchResultSummaryDisplayContext summary = buildSummary(
				document, _renderRequest);

			searchResultsSummariesHolder.put(document, summary);
		}

		return searchResultsSummariesHolder;
	}

	protected SearchResultSummaryDisplayContext buildSummary(
			Document document, RenderRequest renderRequest)
		throws Exception {

		SearchResultSummaryDisplayBuilder searchResultSummaryDisplayBuilder =
			new SearchResultSummaryDisplayBuilder();

		PortletURL portletURL = _portletURLFactory.getPortletURL();

		searchResultSummaryDisplayBuilder.setAssetEntryLocalService(
			_assetEntryLocalService);
		searchResultSummaryDisplayBuilder.setBlogsEntryLocalService(
			_blogsEntryLocalService);
		searchResultSummaryDisplayBuilder.setCoverImageRequested(true);
		searchResultSummaryDisplayBuilder.setCurrentURL(portletURL.toString());
		searchResultSummaryDisplayBuilder.setDocument(document);
		searchResultSummaryDisplayBuilder.setHighlightEnabled(
			_searchResultsPortletPreferences.isHighlightEnabled());
		searchResultSummaryDisplayBuilder.setLanguage(_language);
		searchResultSummaryDisplayBuilder.setLocale(_themeDisplay.getLocale());
		searchResultSummaryDisplayBuilder.setPortletURLFactory(
			_portletURLFactory);
		searchResultSummaryDisplayBuilder.setQueryTerms(
			_searchResponse.getHighlights());
		searchResultSummaryDisplayBuilder.setRenderRequest(_renderRequest);
		searchResultSummaryDisplayBuilder.setRenderResponse(_renderResponse);
		searchResultSummaryDisplayBuilder.setRequest(
			LiferayPortletRequestUtil.getHttpServletRequest(renderRequest));
		searchResultSummaryDisplayBuilder.setResourceActions(_resourceActions);
		searchResultSummaryDisplayBuilder.setSearchResultPreferences(
			_searchResultPreferences);
		searchResultSummaryDisplayBuilder.setThemeDisplay(_themeDisplay);

		return searchResultSummaryDisplayBuilder.build();
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final BlogsEntryLocalService _blogsEntryLocalService;
	private final Language _language;
	private final PortletURLFactoryImpl _portletURLFactory;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ResourceActions _resourceActions;
	private final SearchResponse _searchResponse;
	private final SearchResultPreferences _searchResultPreferences;
	private final SearchResultsPortletPreferences
		_searchResultsPortletPreferences;
	private final ThemeDisplay _themeDisplay;

}