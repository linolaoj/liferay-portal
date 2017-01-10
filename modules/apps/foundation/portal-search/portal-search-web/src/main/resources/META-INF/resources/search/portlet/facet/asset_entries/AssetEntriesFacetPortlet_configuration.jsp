<%--
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
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.search.web.internal.portlet.facet.asset.entry.AssetEntriesFacetPortletPreferences" %>
<%@ page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
AssetEntriesFacetPortletPreferences assetEntriesFacetPortletPreferences = new com.liferay.portal.search.web.internal.portlet.facet.asset.entry.AssetEntriesFacetPortletPreferencesImpl(portletPreferences);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />
	<div class="portlet-configuration-body-content">
		<div class="container-fluid-1280">
		
			<aui:input label="param-name" name="<%= PortletPreferencesJspUtil.getInputName(assetEntriesFacetPortletPreferences.PREFERENCE_PARAM_NAME) %>" value="<%= assetEntriesFacetPortletPreferences.getParamName() %>" />
		
			<aui:input label="frequency-threshold" name="<%= PortletPreferencesJspUtil.getInputName(assetEntriesFacetPortletPreferences.PREFERENCE_FREQUENCY_THRESHOLD) %>" value="<%= assetEntriesFacetPortletPreferences.getFrequencyThreshold() %>" />
		
			<aui:input label="show-asset-count" name="<%= PortletPreferencesJspUtil.getInputName(assetEntriesFacetPortletPreferences.PREFERENCE_FREQUENCIES_VISIBLE) %>" type="checkbox" value="<%= assetEntriesFacetPortletPreferences.isFrequenciesVisible() %>" />
		
			<aui:input name='<%= PortletPreferencesJspUtil.getInputName(assetEntriesFacetPortletPreferences.PREFERENCE_ASSET_TYPES)  %>' type="hidden" value="<%= assetEntriesFacetPortletPreferences.getAssetTypes() %>"/>
		
			<liferay-ui:input-move-boxes
				leftBoxName="currentAssetTypes"
				leftList="<%= assetEntriesFacetPortletPreferences.getCurrentAssetTypes(themeDisplay.getCompanyId(), themeDisplay.getLocale()) %>"
				leftTitle="current"
				rightBoxName="availableAssetTypes"
				rightList="<%= assetEntriesFacetPortletPreferences.getAvailableAssetTypes(themeDisplay.getCompanyId(), themeDisplay.getLocale()) %>"
				rightTitle="available"
			/>
		</div>
	</div>
	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>
<aui:script>
	var form = AUI.$(document.<portlet:namespace />fm);

	$('#<portlet:namespace />fm').on(
		'submit',
		function(event) {
			event.preventDefault();

			form.fm('<%= PortletPreferencesJspUtil.getInputName(assetEntriesFacetPortletPreferences.PREFERENCE_ASSET_TYPES) %>').val(Liferay.Util.listSelect(form.fm('currentAssetTypes')));

			submitForm(form);
		}
	);
</aui:script>