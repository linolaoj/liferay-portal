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

<%@ include file="/facets/init.jsp" %>

<%
if (termCollectors.isEmpty()) {
	return;
}

AssetCategoriesSearchFacetDisplayBuilder assetCategoriesSearchFacetDisplayBuilder = new AssetCategoriesSearchFacetDisplayBuilder();
assetCategoriesSearchFacetDisplayBuilder.setFacet(facet);
assetCategoriesSearchFacetDisplayBuilder.setPermissionChecker(permissionChecker);
assetCategoriesSearchFacetDisplayBuilder.setFieldParam(fieldParam);
assetCategoriesSearchFacetDisplayBuilder.setLocale(locale);
assetCategoriesSearchFacetDisplayBuilder.setDisplayStyle(dataJSONObject.getString("displayStyle", "cloud"));
assetCategoriesSearchFacetDisplayBuilder.setFrequencyThreshold(dataJSONObject.getInt("frequencyThreshold"));
assetCategoriesSearchFacetDisplayBuilder.setMaxTerms(dataJSONObject.getInt("maxTerms", 10));
assetCategoriesSearchFacetDisplayBuilder.setShowAssetCount(dataJSONObject.getBoolean("showAssetCount", true));
AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = assetCategoriesSearchFacetDisplayBuilder.build();
%>

<c:choose>
	<c:when test="<%= assetCategoriesSearchFacetDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetCategoriesSearchFacetDisplayContext.getFieldParamInputName()) %>" type="hidden" value="<%= assetCategoriesSearchFacetDisplayContext.getFieldParamInputValue() %>" />
	</c:when>
	<c:otherwise>
		<div class="panel panel-default">
			<div class="panel-heading">
				<div class="panel-title">
					<liferay-ui:message key="categories" />
				</div>
			</div>

			<div class="panel-body">
				<div class="asset-tags <%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" id="<%= randomNamespace %>facet">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" type="hidden" value="<%= fieldParam %>" />

					<ul class="<%= (assetCategoriesSearchFacetDisplayContext.isShowAssetCount() && assetCategoriesSearchFacetDisplayContext.getDisplayStyle().equals("cloud")) ? "tag-cloud" : "tag-list" %> list-unstyled">
						<li class="default facet-value">
							<a data-value="<%= Validator.isNull(fieldParam) ? "text-primary" : "text-default" %>" href="javascript:;"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
						</li>

						<%
							for (AssetCategoriesSearchFacetFieldDisplayContext assetCategoriesSearchFacetFieldDisplayContext : assetCategoriesSearchFacetDisplayContext.getAssetCategoriesSearchFacetFieldDisplayContexts()) {
						%>

							<li class="facet-value tag-popularity-<%= assetCategoriesSearchFacetFieldDisplayContext.getPopularity() %>">
								<a class="<%= assetCategoriesSearchFacetFieldDisplayContext.isSelected() ? "text-primary" : "text-default" %>" data-value="<%= HtmlUtil.escapeAttribute(String.valueOf(assetCategoriesSearchFacetFieldDisplayContext.getAssetCategoryId())) %>" href="javascript:;">
									<%= HtmlUtil.escape(assetCategoriesSearchFacetFieldDisplayContext.getTitle()) %>

									<c:if test="<%= assetCategoriesSearchFacetDisplayContext.isShowAssetCount() %>">
										<span class="frequency">(<%= assetCategoriesSearchFacetFieldDisplayContext.getFrequency() %>)</span>
									</c:if>
								</a>
							</li>

						<%
						}
						%>

					</ul>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>