package com.liferay.portal.search.web.internal.portlet.facet.folder;

import java.io.IOException;
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
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.facet.display.builder.FolderSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookup;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookupImpl;
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
public class FolderFacetPortlet extends MVCPortlet implements SearchAwarePortlet {

	@Override
	public void contribute(
		SearchBuilder searchBuilder, RenderRequest renderRequest,
		String portletId, SearchContext searchContext) {

		OriginalHttpServletRequestSupplier originalHttpServletRequestSupplier =
			portletOriginalServletRequestSupplierFactory.get(renderRequest);

		setFolders(searchContext, getFolders(originalHttpServletRequestSupplier));

		FolderFacetPortletPreferences folderFacetPortletPreferences =
			new FolderFacetPortletPreferencesImpl(
				getPortletPreferences(renderRequest, portletId));

		getFolderParamValue(originalHttpServletRequestSupplier).ifPresent(
				folderId -> searchContext.setAttribute("folderId", folderId));
		
		searchBuilder.addFacet(
			buildFacet(folderFacetPortletPreferences, searchContext));
	}
	
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		// TODO Portlet configuration

		SearchParametersConfiguration searchParametersConfiguration = () -> "q";

		PortletSharedSearchResult result = portletSharedSearchHelper.search(
			renderRequest, renderResponse, searchParametersConfiguration);

		try {
			FolderSearchFacetDisplayContext folderSearchFacetDisplayContext = 
					buildDisplayContext(renderRequest, result);

			renderRequest.setAttribute(
				FolderSearchFacetDisplayContext.ATTRIBUTE,
				folderSearchFacetDisplayContext);

		} catch (SearchException e) {
			SessionErrors.add(renderRequest, "the-folder-could-not-be-found");
		}
		
		super.render(renderRequest, renderResponse);
	}
	
	protected FolderSearchFacetDisplayContext buildDisplayContext(
		RenderRequest renderRequest, PortletSharedSearchResult result) throws SearchException {

		Facet facet = result.getFacet(FolderFacetConstants.FIELD_NAME);

		String fieldParam = getFieldParam(renderRequest);

		FolderFacetConfiguration folderFacetConfiguration =
			new FolderFacetConfigurationImpl(facet.getFacetConfiguration());

		int frequencyThreshold = folderFacetConfiguration.getFrequencyThreshold();
		int maxTerms = folderFacetConfiguration.getMaxTerms();

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		FolderFacetPortletPreferences folderFacetPortletConfiguration =
			new FolderFacetPortletPreferencesImpl(portletPreferences);

		boolean frequenciesVisible =
			folderFacetPortletConfiguration.isFrequenciesVisible();
		
		FolderTitleLookup folderTitleLookup = 
				new FolderTitleLookupImpl(PortalUtil.getHttpServletRequest(renderRequest)); 
		
		FolderSearchFacetDisplayBuilder folderSearchFacetDisplayBuilder =
			new FolderSearchFacetDisplayBuilder();
			
		folderSearchFacetDisplayBuilder.setFacet(facet);
		folderSearchFacetDisplayBuilder.setFolderTitleLookup(folderTitleLookup);
		folderSearchFacetDisplayBuilder.setFrequenciesVisible(frequenciesVisible);
		folderSearchFacetDisplayBuilder.setFrequencyThreshold(frequencyThreshold);
		folderSearchFacetDisplayBuilder.setMaxTerms(maxTerms);
		folderSearchFacetDisplayBuilder.setParamName(_PARAM);
		folderSearchFacetDisplayBuilder.setParamValue(fieldParam);
			
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
	
	protected PortletPreferences getPortletPreferences(
		RenderRequest renderRequest, String portletId) {

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		return portletPreferencesLocalService.fetchPreferences(
			themeDisplay.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, themeDisplay.getPlid(),
			portletId);
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
	
	protected void setFolders(
			SearchContext searchContext, Optional<long[]> FoldersOptional) {

			FoldersOptional.ifPresent(folderId -> searchContext.setFolderIds(folderId));
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
