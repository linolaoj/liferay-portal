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

package com.liferay.portal.search.web.internal.request.helper;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.demo.DemoData;
import com.liferay.portal.search.web.internal.display.context.KeywordsSupplier;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactoryImpl;
import com.liferay.portal.search.web.internal.display.context.SearchContributor;
import com.liferay.portal.search.web.internal.display.context.SearchDisplayContextHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.results.data.SearchResultsDataSupplier;
import com.liferay.portal.search.web.internal.results.search.SearchResultsDataSupplierImpl;
import com.liferay.portal.search.web.portlet.SearchAwarePortlet;
import com.liferay.portal.search.web.search.builder.SearchBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = SearchLiferayPortletRequestHelper.class)
public class SearchLiferayPortletRequestHelperImpl
	implements SearchLiferayPortletRequestHelper {

	@Override
	public void search(
		KeywordsSupplier keywordsSupplier, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		HttpServletRequestSupplier httpServletRequestSupplier =
			new LiferayPortletHttpServletRequestSupplier(renderRequest);

		SearchHttpServletRequestHelper searchHttpServletRequestHelper =
			createSearchHttpServletRequestHelper(httpServletRequestSupplier);

		SearchResultsDataSupplier searchResultsDataSupplier =
			createSearchResultsDataSupplier(
				keywordsSupplier, httpServletRequestSupplier::get,
				renderRequest, renderResponse);

		searchHttpServletRequestHelper.setResults(searchResultsDataSupplier);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void addSearchAwarePortlet(
		SearchAwarePortlet searchAwarePortlet) {

		String portletClassName = searchAwarePortlet.getClass().getName();

		_searchAwareFacetPortlets.put(
			portletClassName, searchAwarePortlet);
	}

	protected SearchHttpServletRequestHelper
		createSearchHttpServletRequestHelper(
			HttpServletRequestSupplier httpServletRequestSupplier) {

		return new SearchHttpServletRequestHelper(
			new PortalOriginalHttpServletRequestSupplier(
				httpServletRequestSupplier, portal));
	}

	protected SearchResultsDataSupplier createSearchResultsDataSupplier(
		KeywordsSupplier keywordsSupplier,
		SearchDisplayContextHttpServletRequestSupplier requestSupplier,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		if (false) return DemoData::new;

		return new SearchResultsDataSupplierImpl(
			keywordsSupplier,
			new PortletURLFactoryImpl(renderRequest, renderResponse),
			requestSupplier, renderRequest,
			() -> getSearchContributors(renderRequest), facetedSearcherManager,
			language);
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

	protected Optional<SearchContributor> getSearchContributor(
		Portlet portlet, ThemeDisplay themeDisplay) {

		String portletClassName = portlet.getPortletClass();

		Optional<SearchAwarePortlet> searchAwarePortletOptional =
			getSearchAwarePortlet(portletClassName);

		String portletId = portlet.getPortletId();

		Optional<SearchContributor> searchContributorOptional =
			searchAwarePortletOptional.map(
				searchAwarePortlet ->
					getSearchContributor(
						searchAwarePortlet, themeDisplay, portletId));

		return searchContributorOptional;
	}

	protected SearchContributor getSearchContributor(
		SearchAwarePortlet searchAwarePortlet, ThemeDisplay themeDisplay,
		String portletId) {
	
		return new SearchContributor() {
	
			@Override
			public void contribute(
				SearchBuilder searchBuilder, SearchContext searchContext) {
	
				searchAwarePortlet.contribute(
					searchBuilder, themeDisplay, portletId, searchContext);
			}
	
		};
	}

	protected Collection<SearchContributor> getSearchContributors(
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		Stream<Portlet> portlets = getExplicitlyAddedPortlets(themeDisplay);

		Stream<Optional<SearchContributor>> searchContributorOptionals =
			portlets.map(
				portlet -> getSearchContributor(portlet, themeDisplay));

		searchContributorOptionals = searchContributorOptionals.filter(
			Optional::isPresent);

		Stream<SearchContributor> searchContributors =
			searchContributorOptionals.map(Optional::get);

		return searchContributors.collect(Collectors.toList());
	}

	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		return (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
	}

	protected void removeSearchAwarePortlet(
		SearchAwarePortlet searchAwarePortlet) {

		String portletClassName = searchAwarePortlet.getClass().getName();

		_searchAwareFacetPortlets.remove(portletClassName);
	}

	@Reference
	protected FacetedSearcherManager facetedSearcherManager;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	private final Map<String, SearchAwarePortlet>
		_searchAwareFacetPortlets = new HashMap<>();

}