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
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.facet.display.builder.UserSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.UserSearchFacetDisplayContext;
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
		PortletSharedSearchSettings portletSharedSearchSettings) {

		UserFacetPortletPreferences userFacetPortletPreferences =
			new UserFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		filter(userFacetPortletPreferences, portletSharedSearchSettings);

		aggregate(userFacetPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			UserSearchFacetDisplayContext.ATTRIBUTE,
			userSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void aggregate(
		UserFacetPortletPreferences userFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		UserFacetBuilder userFacetBuilder = new UserFacetBuilder();

		userFacetBuilder.setFrequencyThreshold(
			userFacetPortletPreferences.getFrequencyThreshold());
		userFacetBuilder.setMaxTerms(userFacetPortletPreferences.getMaxTerms());
		userFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		Facet facet = userFacetBuilder.build();

		portletSharedSearchSettings.addFacet(facet);
	}

	protected UserSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(
			UserFacetConstants.FIELD_NAME);

		UserFacetConfiguration userFacetConfiguration =
			new UserFacetConfigurationImpl(facet.getFacetConfiguration());

		UserFacetPortletPreferences userFacetPortletPreferences =
			new UserFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		String paramName = userFacetPortletPreferences.getParamName();

		Optional<String[]> paramValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				paramName, renderRequest);

		// TODO Multiple checked checkboxes

		String paramValue = paramValuesOptional.map(
			a -> a[0]).orElse(StringPool.BLANK);

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

	protected void filter(
		UserFacetPortletPreferences userFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String paramName = userFacetPortletPreferences.getParamName();

		Optional<String[]> paramValuesOptional =
			portletSharedSearchSettings.getParameterValues(paramName);

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		paramValuesOptional.ifPresent(
			paramValues -> searchContext.setAttribute(
				UserFacetConstants.FIELD_NAME,
				MultiValueFacetUtil.getCompatibleValuesParam(paramValues)));
	}

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

}