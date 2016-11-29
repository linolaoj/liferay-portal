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

package com.liferay.portal.search.web.internal.portlet.facet.site;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.facet.display.context.ScopeSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.ScopeSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.util.ArrayUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.search.web.portlet.shared.search.SearchAwarePortlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
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
			SiteFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			SiteFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			SiteFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + SiteFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class SiteFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SiteFacetPortletPreferences siteFacetPortletPreferences =
			new SiteFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		filter(siteFacetPortletPreferences, portletSharedSearchSettings);

		aggregate(siteFacetPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		SiteFacetPortletDisplayContext siteFacetPortletDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			SiteFacetPortletDisplayContext.ATTRIBUTE,
			siteFacetPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void aggregate(
		SiteFacetPortletPreferences siteFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ScopeFacetBuilder scopeFacetBuilder = new ScopeFacetBuilder();

		scopeFacetBuilder.setFrequencyThreshold(
			siteFacetPortletPreferences.getFrequencyThreshold());
		scopeFacetBuilder.setMaxTerms(
			siteFacetPortletPreferences.getMaxTerms());
		scopeFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		Facet facet = scopeFacetBuilder.build();

		portletSharedSearchSettings.addFacet(facet);
	}

	protected SiteFacetPortletDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(
			ScopeFacetConstants.FIELD_NAME);

		String paramName = _PARAM;

		Optional<String[]> paramValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				paramName, renderRequest);

		// TODO Multiple checked checkboxes

		String paramValue = paramValuesOptional.map(
			a -> a[0]).orElse(StringPool.BLANK);

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		Locale locale = themeDisplay.getLocale();

		ScopeFacetConfiguration siteFacetConfiguration =
			new ScopeFacetConfigurationImpl(facet.getFacetConfiguration());

		int countThreshold = siteFacetConfiguration.getFrequencyThreshold();
		int maxTerms = siteFacetConfiguration.getMaxTerms();

		SiteFacetPortletPreferences siteFacetPortletConfiguration =
			new SiteFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		boolean frequenciesVisible =
			siteFacetPortletConfiguration.isFrequenciesVisible();

		ScopeSearchFacetDisplayContext scopeSearchFacetDisplayContext =
			new ScopeSearchFacetDisplayContext(
				facet, paramValue, locale, countThreshold, maxTerms,
				frequenciesVisible, groupLocalService);

		SiteFacetPortletDisplayContext siteFacetPortletDisplayContext =
			new SiteFacetPortletDisplayContext();

		List<SiteFacetPortletTermDisplayContext> termDisplayContexts =
			buildTermDisplayContexts(
				scopeSearchFacetDisplayContext.getTermDisplayContexts(),
				frequenciesVisible);

		siteFacetPortletDisplayContext.setTerms(termDisplayContexts);

		boolean renderNothing = termDisplayContexts.isEmpty();

		siteFacetPortletDisplayContext.setNothingSelected(
			scopeSearchFacetDisplayContext.isNothingSelected());
		siteFacetPortletDisplayContext.setParamName(paramName);
		siteFacetPortletDisplayContext.setParamValue(paramValue);
		siteFacetPortletDisplayContext.setRenderNothing(renderNothing);

		return siteFacetPortletDisplayContext;
	}

	protected List<SiteFacetPortletTermDisplayContext> buildTermDisplayContexts(
		List<ScopeSearchFacetTermDisplayContext>
			scopeSearchFacetTermDisplayContexts,
		boolean showCounts) {

		List<SiteFacetPortletTermDisplayContext> termDisplayContexts =
			new ArrayList<>();

		for (ScopeSearchFacetTermDisplayContext
				scopeSearchFacetTermDisplayContext :
					scopeSearchFacetTermDisplayContexts) {

			SiteFacetPortletTermDisplayContext termDisplayContext =
				getTermDisplayContext(
					showCounts, scopeSearchFacetTermDisplayContext);

			termDisplayContexts.add(termDisplayContext);
		}

		return termDisplayContexts;
	}

	protected void filter(
		SiteFacetPortletPreferences siteFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String paramName = _PARAM;

		Optional<String[]> paramValuesOptional =
			portletSharedSearchSettings.getParameterValues(paramName);

		paramValuesOptional.ifPresent(paramValues -> {
			SearchContext searchContext =
				portletSharedSearchSettings.getSearchContext();

			searchContext.setGroupIds(ArrayUtil.toLongArray(paramValues));
		});
	}

	protected String getDescriptiveName(
		ScopeSearchFacetTermDisplayContext scopeSearchFacetTermDisplayContext) {

		try {
			return scopeSearchFacetTermDisplayContext.getDescriptiveName();
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	protected SiteFacetPortletTermDisplayContext getTermDisplayContext(
		boolean showCounts,
		ScopeSearchFacetTermDisplayContext scopeSearchFacetTermDisplayContext) {

		SiteFacetPortletTermDisplayContext termDisplayContext =
			new SiteFacetPortletTermDisplayContext();

		termDisplayContext.setTerm(
			getDescriptiveName(scopeSearchFacetTermDisplayContext));
		termDisplayContext.setFrequency(
			scopeSearchFacetTermDisplayContext.getCount());
		termDisplayContext.setFrequencyVisible(showCounts);
		termDisplayContext.setSelected(
			scopeSearchFacetTermDisplayContext.isSelected());
		termDisplayContext.setValue(
			String.valueOf(scopeSearchFacetTermDisplayContext.getGroupId()));

		return termDisplayContext;
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private static final String _PARAM = "site";

}