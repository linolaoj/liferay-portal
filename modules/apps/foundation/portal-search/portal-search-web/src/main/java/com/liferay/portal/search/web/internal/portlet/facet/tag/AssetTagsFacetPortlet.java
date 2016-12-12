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

package com.liferay.portal.search.web.internal.portlet.facet.tag;

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

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.AssetTagsSearchFacet;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.portlet.PortletRequestThemeDisplaySupplier;
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
				AssetTagsFacetPortletKeys.CSS_CLASS_WRAPPER,
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
				AssetTagsFacetPortletKeys.DISPLAY_NAME,
			"javax.portlet.expiration-cache=0",
			"javax.portlet.init-param.template-path=/",
			"javax.portlet.init-param.view-template=" +
				AssetTagsFacetPortletKeys.VIEW_TEMPLATE,
			"javax.portlet.name=" + AssetTagsFacetPortletKeys.PORTLET_NAME,
			"javax.portlet.resource-bundle=content.Language",
			"javax.portlet.security-role-ref=guest,power-user,user",
			"javax.portlet.supports.mime-type=text/html"
		},
		service = {Portlet.class, SearchAwarePortlet.class}
	)
public class AssetTagsFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		SearchBuilder searchBuilder, RenderRequest renderRequest,
		String portletId, SearchContext searchContext) {

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		PortletPreferences preferences = getPortletPreferences(
			themeDisplay, portletId);
		
		SearchFacet searchFacet = new AssetTagsSearchFacet();
		
		long companyId = themeDisplay.getCompanyId();
		
		FacetConfiguration facetConfiguration =
			searchFacet.getDefaultConfiguration(companyId);

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		setTags(searchContext, getTags(originalHttpServletRequestSupplier));
		
		getTagParamValue(
				originalHttpServletRequestSupplier).ifPresent(
						assetTagNames -> searchContext.setAttribute("assetTagNames", assetTagNames));
		
		addFacet(searchBuilder, facetConfiguration, searchContext);
	}
	
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		SearchParametersConfiguration searchParametersConfiguration = () -> "q";
		
		PortletSharedSearchResult result =
			portletSharedSearchHelper.search(
				renderRequest, renderResponse, searchParametersConfiguration);

		AssetTagsFacetPortletDisplayContext assetTagsFacetPortletDisplayContext =
			buildDisplayContext(renderRequest, result);

		renderRequest.setAttribute(
			AssetTagsFacetPortletDisplayContext.ATTRIBUTE,
			assetTagsFacetPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void addFacet(
			SearchBuilder searchBuilder, FacetConfiguration facetConfiguration,
			SearchContext searchContext) {

			MultiValueFacet facet = new MultiValueFacet(searchContext);

			facet.setFacetConfiguration(facetConfiguration);

			searchBuilder.addFacet(facet);
		}
	
	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}
	
	protected AssetTagsFacetPortletTermDisplayContext getTermDisplayContext(
			boolean showFrequencies,
			AssetTagsSearchFacetTermDisplayContext assetTagsSearchFacetTermDisplayContext) {

			AssetTagsFacetPortletTermDisplayContext termDisplayContext =
				new AssetTagsFacetPortletTermDisplayContext();

			termDisplayContext.setTerm(
					assetTagsSearchFacetTermDisplayContext.getDisplayName());
			termDisplayContext.setFrequency(
				assetTagsSearchFacetTermDisplayContext.getFrequency());
			termDisplayContext.setFrequencyVisible(showFrequencies);
			termDisplayContext.setSelected(
					assetTagsSearchFacetTermDisplayContext.isSelected());
			termDisplayContext.setValue(
				assetTagsSearchFacetTermDisplayContext.getValue());

			return termDisplayContext;
	}
	
	protected PortletPreferences getPortletPreferences(
			ThemeDisplay themeDisplay, String portletId) {

			PortletPreferences portletPreferences =
				portletPreferencesLocalService.fetchPreferences(
					themeDisplay.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, themeDisplay.getPlid(),
					portletId);

			return portletPreferences;
	}
	
	protected Optional<String[]> getTags(
			OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier) {

			Optional<String> paramValue = getTagParamValue(
				originalHttpServletRequestSupplier);

			Optional<String[]> map = paramValue.map(s -> new String[] {s});

			return map;
	}
	
	protected Optional<String> getTagParamValue(
			OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier) {

			String paramValue = ParamUtil.getString(
				originalHttpServletRequestSupplier.get(), _PARAM);

			if (paramValue.isEmpty()) {
				return Optional.empty();
			}

			return Optional.of(paramValue);
	}
	
	protected void setTags(
			SearchContext searchContext, Optional<String[]> tagsOptional) {

			tagsOptional.ifPresent(tags -> searchContext.setAssetTagNames(tags));
	}
	
	private AssetTagsFacetPortletDisplayContext buildDisplayContext(
			String fieldParam, boolean showFrequencies,
			AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext) {
			
			AssetTagsFacetPortletDisplayContext assetTagsFacetPortletDisplayContext =
				new AssetTagsFacetPortletDisplayContext();

			List<AssetTagsFacetPortletTermDisplayContext> termDisplayContexts =
				buildTermDisplayContexts(
					assetTagsSearchFacetDisplayContext.getTermDisplayContexts(),
					showFrequencies);

			assetTagsFacetPortletDisplayContext.setTerms(termDisplayContexts);

			boolean renderNothing = termDisplayContexts.isEmpty();
			boolean nothingSelected = assetTagsSearchFacetDisplayContext.isNothingSelected();

			assetTagsFacetPortletDisplayContext.setFieldParamInputName(_PARAM);
			assetTagsFacetPortletDisplayContext.setFieldParamInputValue(fieldParam);
			assetTagsFacetPortletDisplayContext.setRenderNothing(renderNothing);
			assetTagsFacetPortletDisplayContext.setNothingSelected(nothingSelected);

			return assetTagsFacetPortletDisplayContext;
	}
	
	private AssetTagsFacetPortletDisplayContext buildDisplayContext(
			RenderRequest renderRequest, PortletSharedSearchResult result) {

			SearchFacet searchFacet = new AssetTagsSearchFacet();

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
			int countThreshold = dataJSONObject.getInt("frequencyThreshold");
			int maxTerms = dataJSONObject.getInt("maxTerms");
			boolean showFrequencies = dataJSONObject.getBoolean("showAssetCount", true);
			
			AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext =
				new AssetTagsSearchFacetDisplayContext(
					facet, fieldParam,"display Style",countThreshold, maxTerms, showFrequencies);

			return buildDisplayContext(
				fieldParam, showFrequencies, assetTagsSearchFacetDisplayContext);
	}
	
	protected String getFieldParam(RenderRequest renderRequest) {
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		Optional<String> paramValue = getTagParamValue(
			originalHttpServletRequestSupplier);

		return paramValue.orElse(StringPool.BLANK);
	}
	
	private List<AssetTagsFacetPortletTermDisplayContext> buildTermDisplayContexts(
			List<AssetTagsSearchFacetTermDisplayContext> assetTagsSearchFacetTermDisplayContexts,
			boolean showCounts) {
			List<AssetTagsFacetPortletTermDisplayContext> termDisplayContexts = new ArrayList<>();

			for (AssetTagsSearchFacetTermDisplayContext assetTagsSearchFacetTermDisplayContext : assetTagsSearchFacetTermDisplayContexts) {
				AssetTagsFacetPortletTermDisplayContext termDisplayContext =
					getTermDisplayContext(
						showCounts, assetTagsSearchFacetTermDisplayContext);

				termDisplayContexts.add(termDisplayContext);
			}
			return termDisplayContexts;
		}
	
	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;
	
	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;
	
	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;
	
	private static final String _PARAM = "tag";
	
}
