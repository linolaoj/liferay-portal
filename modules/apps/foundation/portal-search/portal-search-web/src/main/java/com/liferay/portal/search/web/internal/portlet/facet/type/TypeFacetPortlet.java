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

package com.liferay.portal.search.web.internal.portlet.facet.type;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetEntriesSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.AssetEntriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.util.MultiValueFacetUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.search.web.portlet.shared.search.SearchAwarePortlet;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=" +
			TypeFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			TypeFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			TypeFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + TypeFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class TypeFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		TypeFacetPortletPreferences typeFacetPortletPreferences =
			new TypeFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		filter(typeFacetPortletPreferences, portletSharedSearchSettings);

		aggregate(typeFacetPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		AssetEntriesSearchFacetDisplayContext
			assetEntriesSearchFacetDisplayContext = buildDisplayContext(
				portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			AssetEntriesSearchFacetDisplayContext.ATTRIBUTE,
			assetEntriesSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void aggregate(
		TypeFacetPortletPreferences typeFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		long companyId = themeDisplay.getCompanyId();

		AssetEntriesFacetBuilder assetEntriesFacetBuilder =
			new AssetEntriesFacetBuilder();

		assetEntriesFacetBuilder.setCompanyId(companyId);
		assetEntriesFacetBuilder.setFrequencyThreshold(
			typeFacetPortletPreferences.getFrequencyThreshold());
		assetEntriesFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		Facet facet = assetEntriesFacetBuilder.build();

		portletSharedSearchSettings.addFacet(facet);
	}

	protected AssetEntriesSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		TypeFacetPortletPreferences typeFacetPortletPreferences =
			new TypeFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		Optional<String[]> assetTypesArray =
			typeFacetPortletPreferences.getAssetTypesArray();

		Facet facet = portletSharedSearchResponse.getFacet(
			AssetEntriesFacetConstants.FIELD_NAME);

		AssetEntriesFacetConfiguration assetEntriesFacetConfiguration =
			new AssetEntriesFacetConfigurationImpl(
				facet.getFacetConfiguration());

		String[] classNames = assetTypesArray.orElse(
			assetEntriesFacetConfiguration.getClassNames());

		String paramName = typeFacetPortletPreferences.getParamName();

		Optional<String[]> paramValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				paramName, renderRequest);

		Optional<List<String>> typesOptional = paramValuesOptional.map(
			Arrays::asList);

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		AssetEntriesSearchFacetDisplayBuilder
			assetEntriesSearchFacetDisplayBuilder =
				new AssetEntriesSearchFacetDisplayBuilder();

		assetEntriesSearchFacetDisplayBuilder.setClassNames(classNames);
		assetEntriesSearchFacetDisplayBuilder.setFacet(facet);
		assetEntriesSearchFacetDisplayBuilder.setFrequencyThreshold(
			assetEntriesFacetConfiguration.getFrequencyThreshold());
		assetEntriesSearchFacetDisplayBuilder.setFrequenciesVisible(
			typeFacetPortletPreferences.isFrequenciesVisible());
		assetEntriesSearchFacetDisplayBuilder.setLocale(
			themeDisplay.getLocale());
		assetEntriesSearchFacetDisplayBuilder.setParamName(paramName);

		typesOptional.ifPresent(
			assetEntriesSearchFacetDisplayBuilder::setParamValues);

		assetEntriesSearchFacetDisplayBuilder.setPermissionChecker(
			themeDisplay.getPermissionChecker());

		AssetEntriesSearchFacetDisplayContext
			assetEntriesSearchFacetDisplayContext =
				assetEntriesSearchFacetDisplayBuilder.build();

		return assetEntriesSearchFacetDisplayContext;
	}

	protected void filter(
		TypeFacetPortletPreferences typeFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String paramName = typeFacetPortletPreferences.getParamName();

		Optional<String[]> paramValuesOptional =
			portletSharedSearchSettings.getParameterValues(paramName);

		paramValuesOptional.ifPresent(
			paramValues -> {
				SearchContext searchContext =
					portletSharedSearchSettings.getSearchContext();

				searchContext.setAttribute(
					AssetEntriesFacetConstants.FIELD_NAME,
					MultiValueFacetUtil.getCompatibleValuesParam(paramValues));
				searchContext.setEntryClassNames(paramValues);
			});
	}

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

}