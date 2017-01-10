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

package com.liferay.portal.search.web.internal.portlet.facet.category;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoriesSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.util.ArrayUtil;
import com.liferay.portal.search.web.internal.util.MultiValueFacetUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.search.web.portlet.shared.search.SearchAwarePortlet;

import java.io.IOException;

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
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=" +
			CategoryFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			CategoryFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			CategoryFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + CategoryFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class CategoryFacetPortlet
	extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CategoryFacetPortletPreferences categoryFacetPortletPreferences =
			new CategoryFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		filter(categoryFacetPortletPreferences, portletSharedSearchSettings);

		aggregate(categoryFacetPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = buildDisplayContext(
				portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			AssetCategoriesSearchFacetDisplayContext.ATTRIBUTE,
			assetCategoriesSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void aggregate(
		CategoryFacetPortletPreferences categoryFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		AssetCategoriesFacetBuilder assetCategoriesFacetBuilder =
			new AssetCategoriesFacetBuilder();

		assetCategoriesFacetBuilder.setFrequencyThreshold(
			categoryFacetPortletPreferences.getFrequencyThreshold());
		assetCategoriesFacetBuilder.setMaxTerms(
			categoryFacetPortletPreferences.getMaxTerms());
		assetCategoriesFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		Facet facet = assetCategoriesFacetBuilder.build();

		portletSharedSearchSettings.addFacet(facet);
	}

	protected AssetCategoriesSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(
			AssetCategoriesFacetConstants.FIELD_NAME);

		AssetCategoriesFacetConfiguration assetCategoriesFacetConfiguration =
			new AssetCategoriesFacetConfigurationImpl(
				facet.getFacetConfiguration());

		int maxTerms = assetCategoriesFacetConfiguration.getMaxTerms();
		int frequencyThreshold =
			assetCategoriesFacetConfiguration.getFrequencyThreshold();

		CategoryFacetPortletPreferences categoryFacetPortletPreferences =
			new CategoryFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		boolean frequenciesVisible =
			categoryFacetPortletPreferences.isFrequenciesVisible();
		String displayStyle = categoryFacetPortletPreferences.getDisplayStyle();

		String paramName = categoryFacetPortletPreferences.getParamName();

		Optional<String[]> paramValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				paramName, renderRequest);

		// TODO Multiple checked checkboxes

		String paramValue = paramValuesOptional.map(
			a -> a[0]).orElse(StringPool.BLANK);

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		AssetCategoriesSearchFacetDisplayBuilder
			assetCategoriesSearchFacetDisplayBuilder =
				new AssetCategoriesSearchFacetDisplayBuilder();

		assetCategoriesSearchFacetDisplayBuilder.setFacet(facet);
		assetCategoriesSearchFacetDisplayBuilder.setPermissionChecker(
			themeDisplay.getPermissionChecker());
		assetCategoriesSearchFacetDisplayBuilder.setParamName(paramName);
		assetCategoriesSearchFacetDisplayBuilder.setParamValue(paramValue);
		assetCategoriesSearchFacetDisplayBuilder.setLocale(
			themeDisplay.getLocale());
		assetCategoriesSearchFacetDisplayBuilder.setDisplayStyle(displayStyle);
		assetCategoriesSearchFacetDisplayBuilder.setFrequencyThreshold(
			frequencyThreshold);
		assetCategoriesSearchFacetDisplayBuilder.setMaxTerms(maxTerms);
		assetCategoriesSearchFacetDisplayBuilder.setFrequenciesVisible(
			frequenciesVisible);

		try {
			return assetCategoriesSearchFacetDisplayBuilder.build();
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	protected void filter(
		CategoryFacetPortletPreferences categoryFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String paramName = categoryFacetPortletPreferences.getParamName();

		Optional<String[]> paramValuesOptional =
			portletSharedSearchSettings.getParameterValues(paramName);

		paramValuesOptional.ifPresent(
			paramValues -> {
				SearchContext searchContext =
					portletSharedSearchSettings.getSearchContext();

				searchContext.setAssetCategoryIds(
					ArrayUtil.toLongArray(paramValues));
				searchContext.setAttribute(
					AssetCategoriesFacetConstants.FIELD_NAME,
					MultiValueFacetUtil.getCompatibleValuesParam(paramValues));
			});
	}

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

}