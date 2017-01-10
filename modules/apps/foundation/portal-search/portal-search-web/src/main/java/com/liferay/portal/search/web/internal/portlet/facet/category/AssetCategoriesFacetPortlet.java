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

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoriesSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.request.helper.OriginalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.PortletOriginalServletRequestSupplierFactory;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchHelper;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchResult;
import com.liferay.portal.search.web.internal.request.params.SearchParametersConfiguration;
import com.liferay.portal.search.web.portlet.SearchAwarePortlet;
import com.liferay.portal.search.web.search.builder.SearchBuilder;

/**
 * @author Lino Alves
 */
@Component(
		immediate = true,
		property = {
			"com.liferay.portlet.add-default-resource=true",
			"com.liferay.portlet.css-class-wrapper=" +
				AssetCategoriesFacetPortletKeys.CSS_CLASS_WRAPPER,
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
				AssetCategoriesFacetPortletKeys.DISPLAY_NAME,
			"javax.portlet.expiration-cache=0",
			"javax.portlet.init-param.template-path=/",
			"javax.portlet.init-param.view-template=" +
				AssetCategoriesFacetPortletKeys.VIEW_TEMPLATE,
			"javax.portlet.name=" + AssetCategoriesFacetPortletKeys.PORTLET_NAME,
			"javax.portlet.resource-bundle=content.Language",
			"javax.portlet.security-role-ref=guest,power-user,user",
			"javax.portlet.supports.mime-type=text/html"
		},
		service = {Portlet.class, SearchAwarePortlet.class}
)
public class AssetCategoriesFacetPortlet 
	extends MVCPortlet implements SearchAwarePortlet {
	
	@Override
	public void contribute(
		SearchBuilder searchBuilder, RenderRequest renderRequest,
		String portletId, SearchContext searchContext) {

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
				portletOriginalServletRequestSupplierFactory.get(renderRequest);

		AssetCategoriesFacetPortletPreferences assetCategoriesFacetPortletPreferences =
				new AssetCategoriesFacetPortletPreferencesImpl(
						getPortletPreferences(renderRequest, portletId));
		
		String paramName =  assetCategoriesFacetPortletPreferences.getParamName();
		
		setAssetCategories(searchContext, getAssetCategories(originalHttpServletRequestSupplier, paramName));

		
		getAssetCategoriesParamValue(originalHttpServletRequestSupplier, paramName).ifPresent(
				assetCategoryIds -> searchContext.setAttribute(AssetCategoriesFacetConstants.FIELD_NAME, assetCategoryIds));

		searchBuilder.addFacet(
				buildFacet(assetCategoriesFacetPortletPreferences, searchContext));
		
	}
	
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		// TODO Portlet configuration
		SearchParametersConfiguration searchParametersConfiguration = () -> "q";

		PortletSharedSearchResult result =
			portletSharedSearchHelper.search(
				renderRequest, renderResponse, searchParametersConfiguration);

		try {
			AssetCategoriesSearchFacetDisplayContext 
				assetCategoriesSearchFacetDisplayContext = 
					buildDisplayContext(renderRequest, result);

			renderRequest.setAttribute(
				AssetCategoriesSearchFacetDisplayContext.ATTRIBUTE,
				assetCategoriesSearchFacetDisplayContext);
		} catch (PortalException e) {
			SessionErrors.add(renderRequest, "the-asset-category-cloud-not-be-found");
		}

		super.render(renderRequest, renderResponse);
	}
	
	private AssetCategoriesSearchFacetDisplayContext buildDisplayContext(
			RenderRequest renderRequest, PortletSharedSearchResult result) 
					throws PortalException {

		Facet facet = result.getFacet(AssetCategoriesFacetConstants.FIELD_NAME);
		
		AssetCategoriesFacetConfiguration assetCategoriesFacetConfiguration =
				new AssetCategoriesFacetConfigurationImpl(facet.getFacetConfiguration());
		
		int maxTerms = assetCategoriesFacetConfiguration.getMaxTerms();
		int frequencyThreshold = assetCategoriesFacetConfiguration.getFrequencyThreshold();
		
		PortletPreferences portletPreferences = renderRequest.getPreferences();
		
		AssetCategoriesFacetPortletPreferences assetCategoriesFacetPortletConfiguration =
			new AssetCategoriesFacetPortletPreferencesImpl(portletPreferences);
		
		boolean frequenciesVisible = assetCategoriesFacetPortletConfiguration.isFrequenciesVisible();
		String displayStyle = assetCategoriesFacetPortletConfiguration.getDisplayStyle();
		
		String paramName  = assetCategoriesFacetPortletConfiguration.getParamName();
		
		String fieldParam = getFieldParam(renderRequest, paramName);

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		Locale locale = themeDisplay.getLocale();
		
		AssetCategoriesSearchFacetDisplayBuilder 
			assetCategoriesSearchFacetDisplayBuilder = 
				new AssetCategoriesSearchFacetDisplayBuilder();
		
		assetCategoriesSearchFacetDisplayBuilder.setFacet(facet);
		assetCategoriesSearchFacetDisplayBuilder.setPermissionChecker(
				themeDisplay.getPermissionChecker());
		assetCategoriesSearchFacetDisplayBuilder.setParamName(paramName);
		assetCategoriesSearchFacetDisplayBuilder.setParamValue(fieldParam);
		assetCategoriesSearchFacetDisplayBuilder.setLocale(locale);
		assetCategoriesSearchFacetDisplayBuilder.setDisplayStyle(displayStyle);
		assetCategoriesSearchFacetDisplayBuilder.setFrequencyThreshold(
				frequencyThreshold);
		assetCategoriesSearchFacetDisplayBuilder.setMaxTerms(maxTerms);
		assetCategoriesSearchFacetDisplayBuilder.setFrequenciesVisible(
				frequenciesVisible);
		
		AssetCategoriesSearchFacetDisplayContext 
			assetCategoriesSearchFacetDisplayContext = 
				assetCategoriesSearchFacetDisplayBuilder.build();

		return assetCategoriesSearchFacetDisplayContext;
	}
	
	protected MultiValueFacet buildFacet(
		AssetCategoriesFacetPortletPreferences assetCategoriesFacetPortletPreferences,
		SearchContext searchContext) {

		AssetCategoriesFacetBuilder assetCategoriesFacetBuilder = new AssetCategoriesFacetBuilder();

		assetCategoriesFacetBuilder.setFrequencyThreshold(
			assetCategoriesFacetPortletPreferences.getFrequencyThreshold());
		assetCategoriesFacetBuilder.setMaxTerms(
			assetCategoriesFacetPortletPreferences.getMaxTerms());
		assetCategoriesFacetBuilder.setSearchContext(searchContext);

		return assetCategoriesFacetBuilder.build();
	}
	
	protected Optional<long[]> getAssetCategories(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier, String paramName) {

		Optional<String> paramValue = getAssetCategoriesParamValue(
			originalHttpServletRequestSupplier, paramName);

		Optional<Long> map = paramValue.map(Long::valueOf);

		Optional<long[]> map2 = map.map(l -> new long[] {l});

		return map2;
	}
	
	protected Optional<String> getAssetCategoriesParamValue(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier, String paramName) {

		String paramValue = ParamUtil.getString(
			originalHttpServletRequestSupplier.get(), paramName);

		if (paramValue.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(paramValue);
	}

	protected String getFieldParam(RenderRequest renderRequest, String paramName) {
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		Optional<String> paramValue = getAssetCategoriesParamValue(
			originalHttpServletRequestSupplier, paramName);

		return paramValue.orElse(StringPool.BLANK);
	}
	
	protected PortletPreferences getPortletPreferences(
		RenderRequest renderRequest, String portletId) {

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		return portletPreferencesLocalService.fetchPreferences(
			themeDisplay.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, themeDisplay.getPlid(),
			portletId);
	}
	
	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}
	
	protected void setAssetCategories(
		SearchContext searchContext, Optional<long[]> assetCategories) {

		assetCategories.ifPresent(
				assetCategory -> searchContext.setAssetCategoryIds(assetCategory));
	}
	
	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;

}
