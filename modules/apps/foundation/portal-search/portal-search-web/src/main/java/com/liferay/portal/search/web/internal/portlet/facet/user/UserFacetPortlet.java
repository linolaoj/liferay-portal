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

package com.liferay.portal.search.web.internal.portlet.facet.user;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.display.builder.UserSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.UserSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.request.helper.OriginalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.request.helper.PortletOriginalServletRequestSupplierFactory;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchHelper;
import com.liferay.portal.search.web.internal.request.helper.PortletSharedSearchResult;
import com.liferay.portal.search.web.portlet.SearchAwarePortlet;
import com.liferay.portal.search.web.portlet.SearchParametersBuilder;
import com.liferay.portal.search.web.search.builder.SearchBuilder;

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
			UserFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			UserFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			UserFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + UserFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class UserFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		SearchBuilder searchBuilder,
		SearchParametersBuilder searchParametersBuilder,
		RenderRequest renderRequest, String portletId,
		SearchContext searchContext) {

		UserFacetPortletPreferences userFacetPortletPreferences =
			getPortletPreferences(renderRequest, portletId);

		String paramName = userFacetPortletPreferences.getParamName();

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		Optional<String> paramValue = getUserParamValue(
			originalHttpServletRequestSupplier, paramName);

		setUsers(searchContext, paramValue);

		searchBuilder.addFacet(
			buildFacet(userFacetPortletPreferences, searchContext));
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResult portletSharedSearchResult =
			portletSharedSearchHelper.search(renderRequest, renderResponse);

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			buildDisplayContext(renderRequest, portletSharedSearchResult);

		renderRequest.setAttribute(
			UserSearchFacetDisplayContext.ATTRIBUTE,
			userSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void addFacet(
		SearchBuilder searchBuilder, FacetConfiguration facetConfiguration,
		SearchContext searchContext) {

		MultiValueFacet facet = new MultiValueFacet(searchContext);

		facet.setFacetConfiguration(facetConfiguration);

		searchBuilder.addFacet(facet);
	}

	protected UserSearchFacetDisplayContext buildDisplayContext(
		RenderRequest renderRequest, PortletSharedSearchResult result) {

		Facet facet = result.getFacet(UserFacetConstants.FIELD_NAME);

		UserFacetConfiguration userFacetConfiguration =
			new UserFacetConfigurationImpl(facet.getFacetConfiguration());

		UserFacetPortletPreferences userFacetPortletPreferences =
			getPortletPreferences(renderRequest);

		String paramName = userFacetPortletPreferences.getParamName();

		String paramValue = getFieldParam(renderRequest, paramName);

		UserSearchFacetDisplayBuilder userSearchFacetDisplayBuilder =
			new UserSearchFacetDisplayBuilder();

		userSearchFacetDisplayBuilder.setFacet(facet);
		userSearchFacetDisplayBuilder.setFrequenciesVisible(
			userFacetPortletPreferences.isFrequenciesVisible());
		userSearchFacetDisplayBuilder.setFrequencyThreshold(
			userFacetConfiguration.getFrequencyThreshold());
		userSearchFacetDisplayBuilder.setMaxTerms(
			userFacetConfiguration.getMaxTerms());
		userSearchFacetDisplayBuilder.setParamName(paramName);
		userSearchFacetDisplayBuilder.setParamValue(paramValue);

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			userSearchFacetDisplayBuilder.build();

		return userSearchFacetDisplayContext;
	}

	protected Facet buildFacet(
		UserFacetPortletPreferences userFacetPortletPreferences,
		SearchContext searchContext) {

		UserFacetBuilder userFacetBuilder = new UserFacetBuilder();

		userFacetBuilder.setFrequencyThreshold(
			userFacetPortletPreferences.getFrequencyThreshold());
		userFacetBuilder.setMaxTerms(userFacetPortletPreferences.getMaxTerms());
		userFacetBuilder.setSearchContext(searchContext);

		return userFacetBuilder.build();
	}

	protected String getFieldParam(
		RenderRequest renderRequest, String paramName) {

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		Optional<String> paramValue = getUserParamValue(
			originalHttpServletRequestSupplier, paramName);

		return paramValue.orElse(StringPool.BLANK);
	}

	protected UserFacetPortletPreferences getPortletPreferences(
		RenderRequest renderRequest) {

		return new UserFacetPortletPreferencesImpl(
			Optional.of(renderRequest.getPreferences()));
	}

	protected UserFacetPortletPreferences getPortletPreferences(
		RenderRequest renderRequest, String portletId) {

		return new UserFacetPortletPreferencesImpl(
			portletPreferencesLookup.getPortletPreferences(
				renderRequest, portletId));
	}

	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	protected Optional<String> getUserParamValue(
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier,
		String paramName) {

		String paramValue = ParamUtil.getString(
			originalHttpServletRequestSupplier.get(), paramName);

		if (paramValue.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(paramValue);
	}

	protected void setUsers(
		SearchContext searchContext, Optional<String> usersOptional) {

		usersOptional.ifPresent(
			userName -> searchContext.setAttribute("userName", userName));
	}

	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLookup portletPreferencesLookup;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;

}