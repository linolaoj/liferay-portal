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
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.search.web.internal.portlet.facet.user.UserFacetPortletPreferences" %>
<%@ page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
UserFacetPortletPreferences userFacetPortletPreferences = new com.liferay.portal.search.web.internal.portlet.facet.user.UserFacetPortletPreferencesImpl(java.util.Optional.of(portletPreferences));
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />
	<div class="portlet-configuration-body-content">
		<div class="container-fluid-1280">
			<aui:input label="portlet.user-facet.configuration.user-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(UserFacetPortletPreferences.PREFERENCE_PARAM_NAME) %>" value="<%= userFacetPortletPreferences.getParamName() %>" />

			<aui:input label="max-terms" name="<%= PortletPreferencesJspUtil.getInputName(UserFacetPortletPreferences.PREFERENCE_MAX_TERMS) %>" value="<%= userFacetPortletPreferences.getMaxTerms() %>" />

			<aui:input label="frequency-threshold" name="<%= PortletPreferencesJspUtil.getInputName(UserFacetPortletPreferences.PREFERENCE_FREQUENCY_THRESHOLD) %>" value="<%= userFacetPortletPreferences.getFrequencyThreshold() %>" />

			<aui:input label="show-asset-count" name="<%= PortletPreferencesJspUtil.getInputName(UserFacetPortletPreferences.PREFERENCE_FREQUENCIES_VISIBLE) %>" type="checkbox" value="<%= userFacetPortletPreferences.isFrequenciesVisible() %>" />
		</div>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>