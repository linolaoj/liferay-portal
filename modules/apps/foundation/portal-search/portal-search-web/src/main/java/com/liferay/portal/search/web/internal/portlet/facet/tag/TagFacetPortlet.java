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

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetTagsSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetDisplayContext;
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
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=" +
			TagFacetPortletKeys.CSS_CLASS_WRAPPER,
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
			TagFacetPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			TagFacetPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + TagFacetPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, SearchAwarePortlet.class}
)
public class TagFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		TagFacetPortletPreferences tagFacetPortletPreferences =
			new TagFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		filter(tagFacetPortletPreferences, portletSharedSearchSettings);

		aggregate(tagFacetPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			AssetTagsSearchFacetDisplayContext.ATTRIBUTE,
			assetTagsSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected void aggregate(
		TagFacetPortletPreferences tagFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		AssetTagsFacetBuilder assetTagsFacetBuilder =
			new AssetTagsFacetBuilder();

		assetTagsFacetBuilder.setFrequencyThreshold(
			tagFacetPortletPreferences.getFrequencyThreshold());
		assetTagsFacetBuilder.setMaxTerms(
			tagFacetPortletPreferences.getMaxTerms());
		assetTagsFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		Facet facet = assetTagsFacetBuilder.build();

		portletSharedSearchSettings.addFacet(facet);
	}

	protected AssetTagsSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(
			AssetTagsFacetConstants.FIELD_NAME);

		String paramName = _PARAM;

		Optional<String[]> paramValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				paramName, renderRequest);

		Optional<List<String>> tagsOptional = paramValuesOptional.map(
			Arrays::asList);

		AssetTagsFacetConfiguration assetTagsFacetConfiguration =
			new AssetTagsFacetConfigurationImpl(facet.getFacetConfiguration());

		int frequencyThreshold =
			assetTagsFacetConfiguration.getFrequencyThreshold();
		int maxTerms = assetTagsFacetConfiguration.getMaxTerms();

		TagFacetPortletPreferences tagFacetPortletPreferences =
			new TagFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		boolean frequenciesVisible =
			tagFacetPortletPreferences.isFrequenciesVisible();
		String displayStyle = tagFacetPortletPreferences.getDisplayStyle();

		AssetTagsSearchFacetDisplayBuilder assetTagsSearchFacetDisplayBuilder =
			new AssetTagsSearchFacetDisplayBuilder();

		assetTagsSearchFacetDisplayBuilder.setDisplayStyle(displayStyle);
		assetTagsSearchFacetDisplayBuilder.setFacet(facet);
		assetTagsSearchFacetDisplayBuilder.setFrequenciesVisible(
			frequenciesVisible);
		assetTagsSearchFacetDisplayBuilder.setFrequencyThreshold(
			frequencyThreshold);
		assetTagsSearchFacetDisplayBuilder.setMaxTerms(maxTerms);
		assetTagsSearchFacetDisplayBuilder.setParamName(_PARAM);

		tagsOptional.ifPresent(
			assetTagsSearchFacetDisplayBuilder::setParamValues);

		AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext =
			assetTagsSearchFacetDisplayBuilder.build();

		return assetTagsSearchFacetDisplayContext;
	}

	protected void filter(
		TagFacetPortletPreferences tagFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String paramName = _PARAM;

		Optional<String[]> paramValuesOptional =
			portletSharedSearchSettings.getParameterValues(paramName);

		paramValuesOptional.ifPresent(
			paramValues -> {
				SearchContext searchContext =
					portletSharedSearchSettings.getSearchContext();

				searchContext.setAssetTagNames(paramValues);
				searchContext.setAttribute(
					AssetTagsFacetConstants.FIELD_NAME,
					MultiValueFacetUtil.getCompatibleValuesParam(paramValues));
			});
	}

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private static final String _PARAM = "tag";

}