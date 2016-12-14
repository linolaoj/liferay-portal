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
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.FolderSearchFacet;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookup;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookupImpl;
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
			"javax.portlet.display-name=" +
				FolderFacetPortletKeys.DISPLAY_NAME,
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
public class FolderFacetPortlet extends MVCPortlet implements SearchAwarePortlet{

	@Override
	public void contribute(
		SearchBuilder searchBuilder, RenderRequest renderRequest,
		String portletId, SearchContext searchContext) {

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		// SEE com.liferay.portal.search.web.facet.BaseSearchFacet._toFacetConfiguration(JSONObject)
		PortletPreferences preferences = getPortletPreferences(
			themeDisplay, portletId);
		
		SearchFacet searchFacet = new FolderSearchFacet();
		
		long companyId = themeDisplay.getCompanyId();
		
		FacetConfiguration facetConfiguration =
			searchFacet.getDefaultConfiguration(companyId);

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		getFolderParamValue(originalHttpServletRequestSupplier).ifPresent(
				folderId -> searchContext.setAttribute("folderId", folderId));
		
		setFolders(searchContext, getFolders(originalHttpServletRequestSupplier));

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

		try {
			FolderFacetPortletDisplayContext folderFacetPortletDisplayContext =
					buildDisplayContext(renderRequest, result);
			
			renderRequest.setAttribute(
				FolderFacetPortletDisplayContext.ATTRIBUTE,
				folderFacetPortletDisplayContext);

		} catch (SearchException e) {
			SessionErrors.add(renderRequest, "the-folder-could-not-be-found");
		}
	
		super.render(renderRequest, renderResponse);
	}
	
	private FolderFacetPortletDisplayContext buildDisplayContext(
			RenderRequest renderRequest, PortletSharedSearchResult result) 
					throws SearchException {

			SearchFacet searchFacet = new FolderSearchFacet();

			ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

			PortletPreferences preferences = renderRequest.getPreferences();
			long companyId = themeDisplay.getCompanyId();
			FacetConfiguration facetConfiguration =
				searchFacet.getDefaultConfiguration(companyId);

			JSONObject dataJSONObject = facetConfiguration.getData();

			Facet facet = result.getFacet(searchFacet.getFieldName());
			String fieldParam = getFieldParam(renderRequest);

			int countThreshold = dataJSONObject.getInt("frequencyThreshold");
			int maxTerms = dataJSONObject.getInt("maxTerms");
			boolean showFrequencies = dataJSONObject.getBoolean("showAssetCount", true);
			
			FolderTitleLookup folderTitleLookup = 
					new FolderTitleLookupImpl(PortalUtil.getHttpServletRequest(renderRequest)); 
			
			FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
				new FolderSearchFacetDisplayContext(
					facet, fieldParam, countThreshold, maxTerms, showFrequencies, folderTitleLookup);

			return buildDisplayContext(
				fieldParam, showFrequencies, folderSearchFacetDisplayContext);
	}
	
	private FolderFacetPortletDisplayContext buildDisplayContext(
			String fieldParam, boolean showFrequencies,
			FolderSearchFacetDisplayContext folderSearchFacetDisplayContext) {
			FolderFacetPortletDisplayContext folderFacetPortletDisplayContext =
				new FolderFacetPortletDisplayContext();

			List<FolderFacetPortletTermDisplayContext> termDisplayContexts =
				buildTermDisplayContexts(
					folderSearchFacetDisplayContext.getTermDisplayContexts(),
					showFrequencies);

			folderFacetPortletDisplayContext.setTerms(termDisplayContexts);

			boolean renderNothing = termDisplayContexts.isEmpty();
			boolean nothingSelected = folderSearchFacetDisplayContext.isNothingSelected();

			folderFacetPortletDisplayContext.setFieldParamInputName(_PARAM);
			folderFacetPortletDisplayContext.setFieldParamInputValue(fieldParam);
			folderFacetPortletDisplayContext.setRenderNothing(renderNothing);
			folderFacetPortletDisplayContext.setNothingSelected(nothingSelected);

			return folderFacetPortletDisplayContext;
	}
	
	private List<FolderFacetPortletTermDisplayContext> buildTermDisplayContexts(
			List<FolderSearchFacetTermDisplayContext> folderSearchFacetTermDisplayContexts,
			boolean showCounts) {
			List<FolderFacetPortletTermDisplayContext> termDisplayContexts = new ArrayList<>();

			for (FolderSearchFacetTermDisplayContext folderSearchFacetTermDisplayContext : folderSearchFacetTermDisplayContexts) {
				FolderFacetPortletTermDisplayContext termDisplayContext =
					getTermDisplayContext(
						showCounts, folderSearchFacetTermDisplayContext);

				termDisplayContexts.add(termDisplayContext);
			}
			return termDisplayContexts;
	}
	
	protected FolderFacetPortletTermDisplayContext getTermDisplayContext(
			boolean showFrequencies,
			FolderSearchFacetTermDisplayContext folderSearchFacetTermDisplayContext) {

			FolderFacetPortletTermDisplayContext termDisplayContext =
				new FolderFacetPortletTermDisplayContext();

			termDisplayContext.setTerm(
				getDisplayName(folderSearchFacetTermDisplayContext));
			termDisplayContext.setFrequency(
				folderSearchFacetTermDisplayContext.getFrequency());
			termDisplayContext.setFrequencyVisible(showFrequencies);
			termDisplayContext.setSelected(
				folderSearchFacetTermDisplayContext.isSelected());
			termDisplayContext.setValue(
				String.valueOf(folderSearchFacetTermDisplayContext.getFolderId()));

			return termDisplayContext;
	}
	
	protected String getDisplayName(
			FolderSearchFacetTermDisplayContext folderSearchFacetTermDisplayContext) {

			try {
				return folderSearchFacetTermDisplayContext.getDisplayName();
			}
			catch (PortalException e) {
				throw new RuntimeException(e);
			}
	}
	
	protected String getFieldParam(RenderRequest renderRequest) {
		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		Optional<String> paramValue = getFolderParamValue(
			originalHttpServletRequestSupplier);

		return paramValue.orElse(StringPool.BLANK);
	}
	
	protected void addFacet(
			SearchBuilder searchBuilder, FacetConfiguration facetConfiguration,
			SearchContext searchContext) {

			MultiValueFacet facet = new MultiValueFacet(searchContext);

			facet.setFacetConfiguration(facetConfiguration);

			searchBuilder.addFacet(facet);
		}
	
	protected void setFolders(
			SearchContext searchContext, Optional<long[]> FoldersOptional) {

			FoldersOptional.ifPresent(folderId -> searchContext.setFolderIds(folderId));
	}

	protected Optional<long[]> getFolders(
			OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier) {

			Optional<String> paramValue = getFolderParamValue(
				originalHttpServletRequestSupplier);

			Optional<Long> map = paramValue.map(Long::valueOf);

			Optional<long[]> map2 = map.map(l -> new long[] {l});

			return map2;
	}
	
	protected Optional<String> getFolderParamValue(
			OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier) {

			String paramValue = ParamUtil.getString(
				originalHttpServletRequestSupplier.get(), _PARAM);

			if (paramValue.isEmpty()) {
				return Optional.empty();
			}

			return Optional.of(paramValue);
	}
	
	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
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
	
	@Reference
	protected PortletOriginalServletRequestSupplierFactory
		portletOriginalServletRequestSupplierFactory;

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

	@Reference
	protected PortletSharedSearchHelper portletSharedSearchHelper;

	private static final String _PARAM = "folderId";
}
