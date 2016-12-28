package com.liferay.portal.search.web.internal.portlet.facet.site;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.search.web.internal.facet.ScopeSearchFacet;

@Component(
	immediate = true,
	property = {"javax.portlet.name=" + SiteFacetPortletKeys.PORTLET_NAME},
	service = ConfigurationAction.class
)
public class SiteFacetConfigurationAction extends DefaultConfigurationAction {
	
	@Override
	public String getJspPath(HttpServletRequest request) {
		return SiteFacetPortletKeys.CONFIGURATION_JSP_PATH;
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		ScopeSearchFacet searchFacet = new ScopeSearchFacet();  
		JSONObject facetJSONObject = searchFacet.getJSONData(actionRequest);

		setPreference(
			actionRequest, "scopeSearchFacetConfiguration", 
			facetJSONObject.toString());

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.search.web)",
		unbind = "-"
	)
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}
	
}
