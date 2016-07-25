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

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.search.facet.util.FacetFactory;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 */
@Component(service = SearchFacetPortletContributeHelper.class)
public class SearchFacetPortletContributeHelper {

	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		Optional<String> searchFacetClassNameOptional = getSearchFacetClassName(
			portletSharedSearchSettings.getPortletPreferences());

		Optional<FacetConfiguration> facetConfigurationOptional =
			getFacetConfiguration(
				searchFacetClassNameOptional, themeDisplay.getCompanyId());

		Optional<Facet> facetOptional = facetConfigurationOptional.map(
			facetConfiguration -> createFacet(
				facetConfiguration,
				portletSharedSearchSettings.getSearchContext()));

		facetOptional.ifPresent(portletSharedSearchSettings::addFacet);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void addFacetFactory(FacetFactory facetFactory) {
		String facetFactoryFacetClassName = facetFactory.getFacetClassName();

		_facetFactories.put(facetFactoryFacetClassName, facetFactory);
	}

	protected Facet createFacet(
		FacetConfiguration facetConfiguration, SearchContext searchContext) {

		String facetConfigurationClassName = facetConfiguration.getClassName();

		FacetFactory facetFactory = getFacetFactory(
			facetConfigurationClassName);

		Facet facet = createFacet(facetFactory, searchContext);

		facet.setFacetConfiguration(facetConfiguration);

		return facet;
	}

	protected Facet createFacet(
		FacetFactory facetFactory, SearchContext searchContext) {

		try {
			return facetFactory.newInstance(searchContext);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Optional<FacetConfiguration> getFacetConfiguration(
		Optional<String> searchFacetClassNameOptional, long companyId) {

		Optional<SearchFacet> searchFacetOptional =
			searchFacetClassNameOptional.map(
				searchFacetPortletRenderHelper::getSearchFacet);

		return searchFacetOptional.map(
			searchFacet -> searchFacet.getDefaultConfiguration(companyId));
	}

	protected FacetFactory getFacetFactory(String facetFactoryFacetClassName) {
		return _facetFactories.get(facetFactoryFacetClassName);
	}

	protected Optional<String> getSearchFacetClassName(
		Optional<PortletPreferences> portletPreferencesOptional) {

		SearchFacetConfiguration searchFacetConfiguration =
			new SearchFacetConfigurationImpl(portletPreferencesOptional);

		return searchFacetConfiguration.getSearchFacetClassName();
	}

	protected void removeFacetFactory(FacetFactory facetFactory) {
		String facetFactoryFacetClassName = facetFactory.getFacetClassName();

		_facetFactories.remove(facetFactoryFacetClassName);
	}

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

	@Reference
	protected SearchFacetPortletRenderHelper searchFacetPortletRenderHelper;

	private final Map<String, FacetFactory> _facetFactories =
		new ConcurrentHashMap<>();

}