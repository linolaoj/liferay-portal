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

<%
com.liferay.portal.search.web.internal.facet.ScopeSearchFacet searchFacet = new com.liferay.portal.search.web.internal.facet.ScopeSearchFacet();

String data = portletPreferences.getValue("scopeSearchFacetConfiguration", ""); 
com.liferay.portal.kernel.json.JSONObject dataJSONObject = com.liferay.portal.kernel.json.JSONFactoryUtil.createJSONObject(data);

int frequencyThreshold = dataJSONObject.getInt("frequencyThreshold");
int maxTerms = dataJSONObject.getInt("maxTerms", 10);
boolean showAssetCount = dataJSONObject.getBoolean("showAssetCount", true);
%>

<aui:input label="frequency-threshold" name='<%= searchFacet.getClassName() + "frequencyThreshold" %>' value="<%= frequencyThreshold %>" />

<aui:input label="max-terms" name='<%= searchFacet.getClassName() + "maxTerms" %>' value="<%= maxTerms %>" />

<aui:input label="show-asset-count" name='<%= searchFacet.getClassName() + "showAssetCount" %>' type="checkbox" value="<%= showAssetCount %>" />