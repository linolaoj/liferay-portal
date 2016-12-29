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

package com.liferay.portal.search.web.internal.portlet.facet.asset.categories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.AssetCategoriesSearchFacet;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoriesSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetFieldDisplayContext;
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

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		// SEE com.liferay.portal.search.web.facet.BaseSearchFacet._toFacetConfiguration(JSONObject)
		PortletPreferences preferences = getPortletPreferences(
			themeDisplay, portletId);
		SearchFacet searchFacet = new AssetCategoriesSearchFacet();
		long companyId = themeDisplay.getCompanyId();
		FacetConfiguration facetConfiguration =
			searchFacet.getDefaultConfiguration(companyId);

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
	 		portletOriginalServletRequestSupplierFactory.get(renderRequest);
	 
		getAssetCategoriesParamValue(originalHttpServletRequestSupplier).ifPresent(
			assetCategoryIds -> searchContext.setAttribute(_PARAM, assetCategoryIds));
		
		setAssetCategories(searchContext, getAssetCategories(originalHttpServletRequestSupplier));

		addFacet(searchBuilder, facetConfiguration, searchContext);
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

		AssetCategoriesFacetPortletDisplayContext assetCategoriesFacetPortletDisplayContext;
		try {
			assetCategoriesFacetPortletDisplayContext = 
					buildDisplayContext(renderRequest, result);

			renderRequest.setAttribute(
					AssetCategoriesFacetPortletDisplayContext.ATTRIBUTE,
					assetCategoriesFacetPortletDisplayContext);
		} catch (PortalException e) {
			SessionErrors.add(renderRequest, "the-asset-category-cloud-not-be-found");
		}

		super.render(renderRequest, renderResponse);
	}
	
	private AssetCategoriesFacetPortletDisplayContext buildDisplayContext(
			RenderRequest renderRequest, PortletSharedSearchResult result) 
					throws PortalException {

		SearchFacet searchFacet = new AssetCategoriesSearchFacet();

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		// SEE com.liferay.portal.search.web.facet.BaseSearchFacet._toFacetConfiguration(JSONObject)
		PortletPreferences preferences = renderRequest.getPreferences();
		long companyId = themeDisplay.getCompanyId();
		FacetConfiguration facetConfiguration =
			searchFacet.getDefaultConfiguration(companyId);

		JSONObject dataJSONObject = facetConfiguration.getData();

		Facet facet = result.getFacet(searchFacet.getFieldName());
		String fieldParam = getFieldParam(renderRequest);
		Locale locale = themeDisplay.getLocale();
		int maxTerms = dataJSONObject.getInt("maxTerms", 10);
		int frequencyThreshold = dataJSONObject.getInt("frequencyThreshold");
		boolean showFrequencies = 
				dataJSONObject.getBoolean("showAssetCount", true);
		String displayStyle = 
				dataJSONObject.getString("displayStyle", "cloud");
		
		
		AssetCategoriesSearchFacetDisplayBuilder 
		assetCategoriesSearchFacetDisplayBuilder = 
			new AssetCategoriesSearchFacetDisplayBuilder();
		assetCategoriesSearchFacetDisplayBuilder.setFacet(facet);
		assetCategoriesSearchFacetDisplayBuilder.setPermissionChecker(
				themeDisplay.getPermissionChecker());
		assetCategoriesSearchFacetDisplayBuilder.setFieldParam(fieldParam);
		assetCategoriesSearchFacetDisplayBuilder.setLocale(locale);
		assetCategoriesSearchFacetDisplayBuilder.setDisplayStyle(displayStyle);
		assetCategoriesSearchFacetDisplayBuilder.setFrequencyThreshold(
				frequencyThreshold);
		assetCategoriesSearchFacetDisplayBuilder.setMaxTerms(maxTerms);
		assetCategoriesSearchFacetDisplayBuilder.setShowAssetCount(
				showFrequencies);
		
		AssetCategoriesSearchFacetDisplayContext 
		assetCategoriesSearchFacetDisplayContext = 
			assetCategoriesSearchFacetDisplayBuilder.build();

			return buildDisplayContext(
				 assetCategoriesSearchFacetDisplayContext);
	}

	private AssetCategoriesFacetPortletDisplayContext buildDisplayContext(
		AssetCategoriesSearchFacetDisplayContext 
		assetCategoriesSearchFacetDisplayContext) {
		AssetCategoriesFacetPortletDisplayContext 
		assetCategoryFacetPortletDisplayContext =
			new AssetCategoriesFacetPortletDisplayContext();

		assetCategoryFacetPortletDisplayContext.setDisplayStyle(
			assetCategoriesSearchFacetDisplayContext.getDisplayStyle());
		assetCategoryFacetPortletDisplayContext.setFieldParamInputName(
			assetCategoriesSearchFacetDisplayContext.getFieldParamInputName());
		assetCategoryFacetPortletDisplayContext.setFieldParamInputValue(
			assetCategoriesSearchFacetDisplayContext.getFieldParamInputValue());
		assetCategoryFacetPortletDisplayContext.setFrequencyThreshold(
			assetCategoriesSearchFacetDisplayContext.getFrequencyThreshold());
		assetCategoryFacetPortletDisplayContext.setMaxTerms(
			assetCategoriesSearchFacetDisplayContext.getMaxTerms());
		assetCategoryFacetPortletDisplayContext.setNothingSelected(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		assetCategoryFacetPortletDisplayContext.setShowAssetCount(
			assetCategoriesSearchFacetDisplayContext.isShowAssetCount());
		
		List<AssetCategoriesFacetPortletFieldDisplayContext> 
			fieldDisplayContexts = buildFieldDisplayContexts(
				assetCategoriesSearchFacetDisplayContext
				.getAssetCategoriesSearchFacetFieldDisplayContexts());

		assetCategoryFacetPortletDisplayContext.setFieldDisplayContexts(
				fieldDisplayContexts);

		return assetCategoryFacetPortletDisplayContext;
	}

	protected String getFieldParam(RenderRequest renderRequest) {
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		Optional<String> paramValue = getAssetCategoriesParamValue(
			originalHttpServletRequestSupplier);

		return paramValue.orElse(StringPool.BLANK);
	}

	private List<AssetCategoriesFacetPortletFieldDisplayContext> 
		buildFieldDisplayContexts(
			List<AssetCategoriesSearchFacetFieldDisplayContext> 
			assetCategorySearchFacetFieldDisplayContexts) {
			
			List<AssetCategoriesFacetPortletFieldDisplayContext> 
			fieldDisplayContexts = new ArrayList<>();

			for (AssetCategoriesSearchFacetFieldDisplayContext 
					assetCategorySearchFacetFieldDisplayContext : 
						assetCategorySearchFacetFieldDisplayContexts) {
				AssetCategoriesFacetPortletFieldDisplayContext 
				termDisplayContext =
					getFieldDisplayContext(
						assetCategorySearchFacetFieldDisplayContext);

				fieldDisplayContexts.add(termDisplayContext);
			}
			return fieldDisplayContexts;
	}

	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	protected void addFacet(
		SearchBuilder searchBuilder, FacetConfiguration facetConfiguration,
		SearchContext searchContext) {

		MultiValueFacet facet = new MultiValueFacet(searchContext);

		facet.setFacetConfiguration(facetConfiguration);

		searchBuilder.addFacet(facet);
	}

	protected void setAssetCategories(
		SearchContext searchContext, Optional<long[]> assetCategories) {

		assetCategories.ifPresent(assetCategory -> searchContext.setAssetCategoryIds(assetCategory));
	}

	protected Optional<long[]> getAssetCategories(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier) {

		Optional<String> paramValue = getAssetCategoriesParamValue(
			originalHttpServletRequestSupplier);

		Optional<Long> map = paramValue.map(Long::valueOf);

		Optional<long[]> map2 = map.map(l -> new long[] {l});

		return map2;
	}
		
	protected Optional<String> getAssetCategoriesParamValue(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier) {

		String paramValue = ParamUtil.getString(
			originalHttpServletRequestSupplier.get(), _PARAM);

		if (paramValue.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(paramValue);
	}

	protected PortletPreferences getPortletPreferences(
		ThemeDisplay themeDisplay, String portletId) {

		PortletPreferences portletPreferences =
			portletPreferencesLocalService.fetchPreferences(
				themeDisplay.getCompanyId(), 
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, 
				themeDisplay.getPlid(), portletId);

		return portletPreferences;
	}

	protected AssetCategoriesFacetPortletFieldDisplayContext getFieldDisplayContext(
		AssetCategoriesSearchFacetFieldDisplayContext assetCategoriesSearchFacetFieldDisplayContext) {

		AssetCategoriesFacetPortletFieldDisplayContext fieldDisplayContext =
			new AssetCategoriesFacetPortletFieldDisplayContext();

		fieldDisplayContext.setTitle(
			assetCategoriesSearchFacetFieldDisplayContext.getTitle());
		fieldDisplayContext.setFrequency(
			assetCategoriesSearchFacetFieldDisplayContext.getFrequency());
		fieldDisplayContext.setIsSelected(
			assetCategoriesSearchFacetFieldDisplayContext.isSelected());
		fieldDisplayContext.setAssetCategoryId(
			assetCategoriesSearchFacetFieldDisplayContext.getAssetCategoryId());

		return fieldDisplayContext;
	}
	
	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;

	private static final String _PARAM = "assetCategoryIds";

}
