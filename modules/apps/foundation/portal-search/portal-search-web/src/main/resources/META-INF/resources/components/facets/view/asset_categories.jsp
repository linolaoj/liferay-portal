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

<%@ include file="/components/facets/init.jsp" %>

<%
com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoriesSearchFacetDisplayBuilder assetCategoriesSearchFacetDisplayBuilder = new com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoriesSearchFacetDisplayBuilder();

assetCategoriesSearchFacetDisplayBuilder.setFacet(facet);
assetCategoriesSearchFacetDisplayBuilder.setPermissionChecker(themeDisplay.getPermissionChecker());
assetCategoriesSearchFacetDisplayBuilder.setParamName(facet.getFieldId());
assetCategoriesSearchFacetDisplayBuilder.setParamValue(fieldParam);
assetCategoriesSearchFacetDisplayBuilder.setLocale(locale);
assetCategoriesSearchFacetDisplayBuilder.setDisplayStyle(dataJSONObject.getString("displayStyle", "cloud"));
assetCategoriesSearchFacetDisplayBuilder.setFrequencyThreshold(dataJSONObject.getInt("frequencyThreshold"));
assetCategoriesSearchFacetDisplayBuilder.setMaxTerms(dataJSONObject.getInt("maxTerms", 10));
assetCategoriesSearchFacetDisplayBuilder.setFrequenciesVisible(dataJSONObject.getBoolean("showAssetCount", true));

com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = assetCategoriesSearchFacetDisplayBuilder.build();
%>
<c:choose>
	<c:when test="<%= assetCategoriesSearchFacetDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetCategoriesSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= assetCategoriesSearchFacetDisplayContext.getParamValue() %>" />
	</c:when>
	<c:otherwise>
		<liferay-ui:panel-container extended="<%= true %>" id='<%= randomNamespace + "facetAssetCategoriesPanelContainer" %>' markupView="lexicon" persistState="<%= true %>">
			<liferay-ui:panel collapsible="<%= true %>" cssClass="<%= cssClass %>" id='<%= randomNamespace + "facetAssetCategoriesPanel" %>' markupView="lexicon" persistState="<%= true %>" title="categories">
				<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetCategoriesSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= assetCategoriesSearchFacetDisplayContext.getParamValue() %>" />
				<aui:input autocomplete="off" name="inputFacetName" type="hidden" value="assetCategoryId" />
		
				<ul class="<%= (assetCategoriesSearchFacetDisplayContext.getDisplayStyle().equals("cloud")) ? "tag-cloud" : "tag-list" %> list-unstyled">
					<li class="default facet-value">
						<a data-value="<%= assetCategoriesSearchFacetDisplayContext.isNothingSelected() ? "text-primary" : "text-default" %>" href="javascript:;"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
					</li>
		
					<%
					for (com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetTermDisplayContext assetCategoriesSearchFacetTermDisplayContext : assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts()) {
					%>
		
						<li class="facet-value tag-popularity-<%= assetCategoriesSearchFacetTermDisplayContext.getPopularity() %>" name="<%= renderResponse.getNamespace()+"assetCategoryId_"+ String.valueOf(assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId()) %>">
							<a class="<%= assetCategoriesSearchFacetTermDisplayContext.isSelected() ? "text-primary" : "text-default" %>" data-value="<%= HtmlUtil.escapeAttribute(String.valueOf(assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId())) %>" href="javascript:;">
								<%= HtmlUtil.escape(assetCategoriesSearchFacetTermDisplayContext.getTitle()) %>
			
								<c:if test="<%= assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible() %>">
									<span class="frequency">(<%= assetCategoriesSearchFacetTermDisplayContext.getFrequency() %>)</span>
								</c:if>
							</a>
						</li>
			
						<aui:input autocomplete="off"
							name="<%= "assetCategoryId_"+ String.valueOf(assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId())+"_value" %>"
							type="hidden"
							value="<%= HtmlUtil.escapeAttribute(String.valueOf(assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId())) %>"
						/>
		
					<%
					}
					%>
		
				</ul>
			</liferay-ui:panel>
		</liferay-ui:panel-container>
	</c:otherwise>
</c:choose>