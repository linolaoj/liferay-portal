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

package com.liferay.portal.search.web.internal.portlet.facet.asset.entry;

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
import com.liferay.portal.kernel.search.facet.AssetEntriesFacet;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetEntriesSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.AssetEntriesSearchFacetDisplayContext;
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
			AssetEntriesFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			AssetEntriesFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			AssetEntriesFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + AssetEntriesFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class AssetEntriesFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		SearchBuilder searchBuilder, RenderRequest renderRequest,
		String portletId, SearchContext searchContext) {

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		AssetEntriesFacetPortletPreferences assetEntriesFacetPortletPreferences =
			new AssetEntriesFacetPortletPreferencesImpl(
				getPortletPreferences(renderRequest, portletId));
		
		String paramName =  assetEntriesFacetPortletPreferences.getParamName();
		
		setAssetEntries(searchContext, 
			getAssetEntries(originalHttpServletRequestSupplier, paramName));
		
		getAssetEntriesParamValue(
				originalHttpServletRequestSupplier, paramName).ifPresent(
					assetType -> searchContext.setAttribute(
						AssetEntriesFacetConstants.FIELD_NAME, assetType));

		searchBuilder.addFacet(
			buildFacet(assetEntriesFacetPortletPreferences, searchContext));
		
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

		AssetEntriesSearchFacetDisplayContext 
			assetEntriesSearchFacetDisplayContext = 
				buildDisplayContext(renderRequest, result);

		renderRequest.setAttribute(
			AssetEntriesSearchFacetDisplayContext.ATTRIBUTE,
			assetEntriesSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}
	
	protected AssetEntriesSearchFacetDisplayContext buildDisplayContext(
			RenderRequest renderRequest, PortletSharedSearchResult result) {

		Facet facet = result.getFacet(AssetEntriesFacetConstants.FIELD_NAME);
		
		AssetEntriesFacetConfiguration assetEntriesFacetConfiguration =
			new AssetEntriesFacetConfigurationImpl(
				facet.getFacetConfiguration());
		
		int frequencyThreshold = 
			assetEntriesFacetConfiguration.getFrequencyThreshold();
		String[] classNames = assetEntriesFacetConfiguration.getClassNames();
		
		PortletPreferences portletPreferences = renderRequest.getPreferences();
		
		AssetEntriesFacetPortletPreferences 
			assetEntriesFacetPortletConfiguration =
				new AssetEntriesFacetPortletPreferencesImpl(portletPreferences);
		
		boolean frequenciesVisible = 
			assetEntriesFacetPortletConfiguration.isFrequenciesVisible();
		
		String paramName  = assetEntriesFacetPortletConfiguration.getParamName();
		
		String fieldParam = getFieldParam(renderRequest, paramName);

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		Locale locale = themeDisplay.getLocale();
		
		classNames = 
			assetEntriesFacetPortletConfiguration.getAssetTypesArray().orElse(classNames);
		
		AssetEntriesSearchFacetDisplayBuilder 
			assetEntriesSearchFacetDisplayBuilder = 
				new AssetEntriesSearchFacetDisplayBuilder();
		
		assetEntriesSearchFacetDisplayBuilder.setClassNames(classNames);
		assetEntriesSearchFacetDisplayBuilder.setFacet(facet);
		assetEntriesSearchFacetDisplayBuilder.setPermissionChecker(
			themeDisplay.getPermissionChecker());
		assetEntriesSearchFacetDisplayBuilder.setParamName(paramName);
		assetEntriesSearchFacetDisplayBuilder.setParamValue(fieldParam);
		assetEntriesSearchFacetDisplayBuilder.setLocale(locale);
		assetEntriesSearchFacetDisplayBuilder.setFrequencyThreshold(
			frequencyThreshold);
		assetEntriesSearchFacetDisplayBuilder.setFrequenciesVisible(
			frequenciesVisible);
		
		AssetEntriesSearchFacetDisplayContext 
			assetEntriesSearchFacetDisplayContext = 
				assetEntriesSearchFacetDisplayBuilder.build();

		return assetEntriesSearchFacetDisplayContext;
	}
	
	protected AssetEntriesFacet buildFacet(
			AssetEntriesFacetPortletPreferences assetEntriesFacetPortletPreferences,
		SearchContext searchContext) {

		AssetEntriesFacetBuilder assetEntriesFacetBuilder = 
			new AssetEntriesFacetBuilder();

		assetEntriesFacetBuilder.setFrequencyThreshold(
			assetEntriesFacetPortletPreferences.getFrequencyThreshold());
		assetEntriesFacetBuilder.setSearchContext(searchContext);

		return assetEntriesFacetBuilder.build();
	}
	
	protected Optional<String[]> getAssetEntries(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier,
		String paramName) {

		Optional<String> paramValue = getAssetEntriesParamValue(
			originalHttpServletRequestSupplier, paramName);

		Optional<String[]> map = paramValue.map(s -> new String[] {s});

		return map;
	}
	
	protected Optional<String> getAssetEntriesParamValue(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier,
		String paramName) {

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

		Optional<String> paramValue = getAssetEntriesParamValue(
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
	
	protected void setAssetEntries(
		SearchContext searchContext, Optional<String[]> assetTypes) {

		assetTypes.ifPresent(
				entryClassNames -> searchContext.setEntryClassNames(entryClassNames ));
	}
	
	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;
	
}
