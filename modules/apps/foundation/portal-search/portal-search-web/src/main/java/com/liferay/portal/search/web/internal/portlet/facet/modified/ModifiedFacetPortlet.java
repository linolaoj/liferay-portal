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

package com.liferay.portal.search.web.internal.portlet.facet.modified;

import java.io.IOException;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.ModifiedFacet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.util.StringUtil;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.display.builder.ModifiedSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.request.helper.OriginalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.PortletOriginalServletRequestSupplierFactory;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchHelper;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchResult;
import com.liferay.portal.search.web.portlet.SearchAwarePortlet;
import com.liferay.portal.search.web.portlet.SearchParametersBuilder;
import com.liferay.portal.search.web.search.builder.SearchBuilder;

/**
 * @author Lino Alves
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=" +
			ModifiedFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			ModifiedFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			ModifiedFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + ModifiedFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class ModifiedFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
			SearchBuilder searchBuilder, 
			SearchParametersBuilder searchParametersBuilder,
			RenderRequest renderRequest, String portletId, 
			SearchContext searchContext) {

		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences =
				getPortletPreferences(renderRequest, portletId);

		String paramName = modifiedFacetPortletPreferences.getParamName();

		Optional<String> paramValue = getModifiedParamValue(
			renderRequest, paramName);

		setModified(searchContext, paramValue);

		searchBuilder.addFacet(
			buildFacet(
				modifiedFacetPortletPreferences, searchContext));	
	}
	
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResult portletSharedSearchResult =
			portletSharedSearchHelper.search(renderRequest, renderResponse);

		try {
		
			ModifiedSearchFacetDisplayContext  modifiedSearchFacetDisplayContext =
				buildDisplayContext(portletSharedSearchResult, renderRequest);
			
			renderRequest.setAttribute(
					ModifiedSearchFacetDisplayContext.ATTRIBUTE,
					modifiedSearchFacetDisplayContext);
		
		} catch (PortalException e) {
			throw new RuntimeException(e);
		}

		super.render(renderRequest, renderResponse);
	}
	
	protected ModifiedFacet buildFacet(
		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences,
		SearchContext searchContext) {

		ModifiedFacetBuilder modifiedFacetBuilder =
			new ModifiedFacetBuilder();

		modifiedFacetBuilder.setSearchContext(searchContext);

		return modifiedFacetBuilder.build();
	}
	
	protected ModifiedSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResult portletSharedSearchResult,
		RenderRequest renderRequest) throws PortalException {

		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences =
			getPortletPreferences(renderRequest);

		Facet facet = portletSharedSearchResult.getFacet(
			ModifiedFacetConstants.FIELD_NAME);

		ModifiedFacetConfiguration modifiedFacetConfiguration =
			new ModifiedFacetConfigurationImpl(
				facet.getFacetConfiguration());
		
		JSONArray rangesJSONArray = modifiedFacetConfiguration.getRangesJSONArray();
		
		modifiedFacetPortletPreferences.updateRangeLabels(rangesJSONArray);
		
		String paramName = modifiedFacetPortletPreferences.getParamName();

		String fieldParam = getFieldParam(renderRequest, paramName);

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		String fieldParamSelection = ParamUtil.getString(renderRequest, facet.getFieldId() + "selection", "0");
		
		String escapedParamName = HtmlUtil.escapeJS(paramName);
		
		int fromDay = ParamUtil.getInteger(
			renderRequest, escapedParamName + "dayFrom");
		int fromMonth = ParamUtil.getInteger(
			renderRequest, escapedParamName + "monthFrom");
		int fromYear = ParamUtil.getInteger(
			renderRequest, escapedParamName + "yearFrom");

		int toDay = ParamUtil.getInteger(
			renderRequest, escapedParamName + "dayTo");
		int toMonth = ParamUtil.getInteger(
			renderRequest, escapedParamName + "monthTo");
		int toYear = ParamUtil.getInteger(
			renderRequest, escapedParamName + "yearTo");
		
		ModifiedSearchFacetDisplayBuilder
			modifiedSearchFacetDisplayBuilder =
				new ModifiedSearchFacetDisplayBuilder();

		modifiedSearchFacetDisplayBuilder.setFacet(facet);
		modifiedSearchFacetDisplayBuilder.setParamName(paramName);
		modifiedSearchFacetDisplayBuilder.setParamValue(fieldParam);
		modifiedSearchFacetDisplayBuilder.setParamSelection(fieldParamSelection);
		modifiedSearchFacetDisplayBuilder.setRangesJSONArray(rangesJSONArray);
		modifiedSearchFacetDisplayBuilder.setFormDay(fromDay);
		modifiedSearchFacetDisplayBuilder.setFormMonth(fromMonth);
		modifiedSearchFacetDisplayBuilder.setFormYear(fromYear);
		modifiedSearchFacetDisplayBuilder.setToDay(toDay);
		modifiedSearchFacetDisplayBuilder.setToMonth(toMonth);
		modifiedSearchFacetDisplayBuilder.setToYear(toYear);
		modifiedSearchFacetDisplayBuilder.setTimeZone(themeDisplay.getTimeZone());
		modifiedSearchFacetDisplayBuilder.setLocale(themeDisplay.getLocale());
		
		ModifiedSearchFacetDisplayContext
			modifiedSearchFacetDisplayContext =
				modifiedSearchFacetDisplayBuilder.build();

		return modifiedSearchFacetDisplayContext;
	}
	
	protected String getFieldParam(
		RenderRequest renderRequest, String paramName) {

		Optional<String> paramValue = getModifiedParamValue(
			renderRequest, paramName);

		return paramValue.orElse(StringPool.BLANK);
	}
	
	protected Optional<String> getModifiedParamValue(
		RenderRequest renderRequest, String paramName) {
		
		return StringUtil.maybe(
				ParamUtil.getString(getRequest(renderRequest), paramName));
	}
	
	protected ModifiedFacetPortletPreferencesImpl getPortletPreferences(
		RenderRequest renderRequest) {

		return new ModifiedFacetPortletPreferencesImpl(
			Optional.ofNullable(renderRequest.getPreferences()));
	}
	
	protected HttpServletRequest getRequest(RenderRequest renderRequest) {
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		return originalHttpServletRequestSupplier.get();
	}
	
	protected ModifiedFacetPortletPreferences getPortletPreferences(
		RenderRequest renderRequest, String portletId) {

		return new ModifiedFacetPortletPreferencesImpl(
			portletPreferencesLookup.getPortletPreferences(
				renderRequest, portletId));
	}
	
	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	protected void setModified(SearchContext searchContext, Optional<String> paramValue) {
		paramValue.ifPresent(
				modified -> {
					searchContext.setAttribute(
						ModifiedFacetConstants.FIELD_NAME, modified);
				});
	}
	
	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLookup portletPreferencesLookup;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;
}
