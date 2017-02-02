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
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.container.SearchContainerPortletURL;
import com.liferay.portal.search.web.internal.portletsharedtask.PortletSharedURLHelper;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearch;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchSettings;
import com.liferay.portal.search.web.portletsharedsearch.SearchAwarePortlet;
import com.liferay.portal.search.web.search.SearchResponse;

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

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearch.search(renderRequest);

		SearchResultsDisplayContext searchResultsDisplayContext =
			buildDisplayContext(
				portletSharedSearchResponse, renderRequest, renderResponse);

		renderRequest.setAttribute(
			SearchResultsDisplayContext.ATTRIBUTE, searchResultsDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected SearchResultsDisplayContext buildDisplayContext(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SearchResponse searchResponse = portletSharedSearchResponse;

		SearchResultsDisplayContext searchResultsDisplayContext =
			new SearchResultsDisplayContext();

		List<Document> documents = searchResponse.getDocuments();

		int startPage = searchResponse.getStartPage();
		
		searchResultsDisplayContext.setDocuments(documents);
		searchResultsDisplayContext.setStartPage(startPage);

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		String deltaParameterName =
			searchResultsPortletPreferences.getDeltaParameterName();
		
		int delta = getDelta(
			portletSharedSearchResponse, 
			searchResultsPortletPreferences, renderRequest);

		String startPageParameterName =
			searchResultsPortletPreferences.getStartPageParameterName();

		searchResultsDisplayContext.setStartPageParameterName(
			startPageParameterName);

		Optional<String> keywordsOptional = searchResponse.getKeywords();

		searchResultsDisplayContext.setKeywords(
			keywordsOptional.orElse(StringPool.BLANK));

		searchResultsDisplayContext.setSearchResultsSummariesHolder(
			buildSummaries(
				portletSharedSearchResponse, renderRequest, renderResponse));

		int totalHits = searchResponse.getTotalHits();

		searchResultsDisplayContext.setTotalHits(totalHits);

		searchResultsDisplayContext.setSearchContainer(
			buildSearchContainer(
				documents, totalHits, startPage, startPageParameterName,	 
				delta, deltaParameterName, renderRequest));

		return searchResultsDisplayContext;
	}

	protected SearchContainer<Document> buildSearchContainer(
			List<Document> documents, int totalHits, 
			int startPage, String startPageParameterName,
			int delta, String deltaParameterName, RenderRequest renderRequest)
		throws PortletException {

		PortletRequest portletRequest = renderRequest;
		DisplayTerms displayTerms = null;
		DisplayTerms searchTerms = null;
		String curParam = startPageParameterName;
		int cur = startPage;
		PortletURL iteratorURL = cleanPortletURL(
			renderRequest, startPageParameterName);
		List<String> headerNames = null;
		String emptyResultsMessage = null;
		String cssClass = null;

		SearchContainer<Document> searchContainer = new SearchContainer<>(
			portletRequest, displayTerms, searchTerms, curParam, cur, delta,
			iteratorURL, headerNames, emptyResultsMessage, cssClass);
		
		searchContainer.setDeltaParam(deltaParameterName);
		
		searchContainer.setResults(documents);
		searchContainer.setTotal(totalHits);

		return searchContainer;
	}

	protected SearchResultsSummariesHolder buildSummaries(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SearchResultsSummariesBuilder searchResultsSummariesBuilder =
			new SearchResultsSummariesBuilder(
				portletSharedSearchResponse, assetEntryLocalService,
				blogsEntryLocalService, resourceActions, language,
				renderRequest, renderResponse);

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

	protected int getDelta(
			PortletSharedSearchSettings portletSharedSearchSettings, 
			SearchResultsPortletPreferences searchResultsPortletPreferences) {
		
		String deltaParameterName =
			searchResultsPortletPreferences.getDeltaParameterName();
		
		Optional<String[]> deltaStringOptional = 
				portletSharedSearchSettings.getParameterValues(deltaParameterName);
		
		Optional<Integer> deltaOptional = 
			deltaStringOptional.map(deltaString -> Integer.parseInt(deltaString[0]));
		
		return deltaOptional.orElse(searchResultsPortletPreferences.getDelta());
	}
	
	protected int getDelta(
			PortletSharedSearchResponse portletSharedSearchResponse, 
			SearchResultsPortletPreferences searchResultsPortletPreferences,
			RenderRequest renderRequest) {
			
			String deltaParameterName =
				searchResultsPortletPreferences.getDeltaParameterName();
			
			Optional<String[]> deltaStringOptional = 
				portletSharedSearchResponse.getParameterValues(deltaParameterName, renderRequest);
			
			Optional<Integer> deltaOptional = 
				deltaStringOptional.map(deltaString -> Integer.parseInt(deltaString[0]));
			
			return deltaOptional.orElse(searchResultsPortletPreferences.getDelta());
		}
	
	protected void highlight(
		SearchResultsPortletPreferences searchResultsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		boolean highlightEnabled =
			searchResultsPortletPreferences.isHighlightEnabled();

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

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
		
		int delta = getDelta(
			portletSharedSearchSettings, searchResultsPortletPreferences);
		
		portletSharedSearchSettings.setDelta(delta);
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