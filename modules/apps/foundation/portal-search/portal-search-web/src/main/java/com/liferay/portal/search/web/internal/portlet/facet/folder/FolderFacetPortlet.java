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

package com.liferay.portal.search.web.internal.portlet.facet.folder;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.display.builder.FolderSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookup;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookupImpl;
import com.liferay.portal.search.web.internal.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.request.helper.OriginalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.PortletOriginalServletRequestSupplierFactory;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchHelper;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchResult;
import com.liferay.portal.search.web.internal.util.StringUtil;
import com.liferay.portal.search.web.portlet.SearchAwarePortlet;
import com.liferay.portal.search.web.portlet.SearchParametersBuilder;
import com.liferay.portal.search.web.search.builder.SearchBuilder;

import java.io.IOException;

import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

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
			FolderFacetPortletKeys.CSS_CLASS_WRAPPER,
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=" + FolderFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			FolderFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + FolderFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class FolderFacetPortlet
	extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		SearchBuilder searchBuilder,
		SearchParametersBuilder searchParametersBuilder,
		RenderRequest renderRequest, String portletId,
		SearchContext searchContext) {

		FolderFacetPortletPreferences folderFacetPortletPreferences =
			getPortletPreferences(renderRequest, portletId);

		String paramName = _PARAM;

		Optional<String> paramValue = getFolderParamValue(
			renderRequest, paramName);

		setFolders(searchContext, paramValue);

		searchBuilder.addFacet(
			buildFacet(folderFacetPortletPreferences, searchContext));
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResult portletSharedSearchResult =
			portletSharedSearchHelper.search(renderRequest, renderResponse);

		FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResult, renderRequest);

		renderRequest.setAttribute(
			FolderSearchFacetDisplayContext.ATTRIBUTE,
			folderSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected FolderSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResult portletSharedSearchResult,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResult.getFacet(
			FolderFacetConstants.FIELD_NAME);

		FolderTitleLookup folderTitleLookup = new FolderTitleLookupImpl(
			getRequest(renderRequest));

		FolderFacetConfiguration folderFacetConfiguration =
			new FolderFacetConfigurationImpl(facet.getFacetConfiguration());

		FolderFacetPortletPreferences folderFacetPortletPreferences =
			getPortletPreferences(renderRequest);

		String paramName = _PARAM;

		String paramValue = getFieldParam(renderRequest, paramName);

		FolderSearchFacetDisplayBuilder folderSearchFacetDisplayBuilder =
			new FolderSearchFacetDisplayBuilder();

		folderSearchFacetDisplayBuilder.setFacet(facet);
		folderSearchFacetDisplayBuilder.setFolderTitleLookup(folderTitleLookup);
		folderSearchFacetDisplayBuilder.setFrequenciesVisible(
			folderFacetPortletPreferences.isFrequenciesVisible());
		folderSearchFacetDisplayBuilder.setFrequencyThreshold(
			folderFacetConfiguration.getFrequencyThreshold());
		folderSearchFacetDisplayBuilder.setMaxTerms(
			folderFacetConfiguration.getMaxTerms());
		folderSearchFacetDisplayBuilder.setParamName(paramName);
		folderSearchFacetDisplayBuilder.setParamValue(paramValue);

		FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
			folderSearchFacetDisplayBuilder.build();

		return folderSearchFacetDisplayContext;
	}

	protected MultiValueFacet buildFacet(
		FolderFacetPortletPreferences folderFacetPortletPreferences,
		SearchContext searchContext) {

		FolderFacetBuilder folderFacetBuilder = new FolderFacetBuilder();

		folderFacetBuilder.setFrequencyThreshold(
			folderFacetPortletPreferences.getFrequencyThreshold());
		folderFacetBuilder.setMaxTerms(
			folderFacetPortletPreferences.getMaxTerms());
		folderFacetBuilder.setSearchContext(searchContext);

		return folderFacetBuilder.build();
	}

	protected String getFieldParam(
		RenderRequest renderRequest, String paramName) {

		Optional<String> paramValue = getFolderParamValue(
			renderRequest, paramName);

		return paramValue.orElse(StringPool.BLANK);
	}

	protected Optional<String> getFolderParamValue(
		RenderRequest renderRequest, String paramName) {

		return StringUtil.maybe(
			ParamUtil.getString(getRequest(renderRequest), paramName));
	}

	protected FolderFacetPortletPreferences getPortletPreferences(
		RenderRequest renderRequest) {

		return new FolderFacetPortletPreferencesImpl(
			Optional.ofNullable(renderRequest.getPreferences()));
	}

	protected FolderFacetPortletPreferences getPortletPreferences(
		RenderRequest renderRequest, String portletId) {

		return new FolderFacetPortletPreferencesImpl(
			portletPreferencesLookup.getPortletPreferences(
				renderRequest, portletId));
	}

	protected HttpServletRequest getRequest(RenderRequest renderRequest) {
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		return originalHttpServletRequestSupplier.get();
	}

	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	protected void setFolders(
		SearchContext searchContext, Optional<String> paramValue) {

		paramValue.ifPresent(
			folderId -> {
				searchContext.setAttribute(
					FolderFacetConstants.FIELD_NAME, folderId);
				searchContext.setFolderIds(new long[] {Long.valueOf(folderId)});
			});
	}

	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLookup portletPreferencesLookup;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;

	private static final String _PARAM = "folder";

}