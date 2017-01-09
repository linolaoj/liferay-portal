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
com.liferay.portal.search.web.internal.facet.display.builder.FolderSearchFacetDisplayBuilder folderSearchFacetDisplayBuilder = new com.liferay.portal.search.web.internal.facet.display.builder.FolderSearchFacetDisplayBuilder();

folderSearchFacetDisplayBuilder.setFacet(facet);
folderSearchFacetDisplayBuilder.setFolderTitleLookup(new com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookupImpl(request));
folderSearchFacetDisplayBuilder.setFrequenciesVisible(dataJSONObject.getBoolean("showAssetCount", true));
folderSearchFacetDisplayBuilder.setFrequencyThreshold(dataJSONObject.getInt("frequencyThreshold"));
folderSearchFacetDisplayBuilder.setMaxTerms(dataJSONObject.getInt("maxTerms", 10));
folderSearchFacetDisplayBuilder.setParamName(facet.getFieldId());
folderSearchFacetDisplayBuilder.setParamValue(fieldParam);

com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext folderSearchFacetDisplayContext = folderSearchFacetDisplayBuilder.build();
%>

<c:choose>
	<c:when test="<%= folderSearchFacetDisplayContext.isRenderNothing() %>">
		<c:if test="<%= folderSearchFacetDisplayContext.getParamValue() != null %>">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(folderSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= folderSearchFacetDisplayContext.getParamValue() %>" />
		</c:if>
	</c:when>
	<c:otherwise>
		<div class="panel panel-default">
			<div class="panel-heading">
				<div class="panel-title">
					<liferay-ui:message key="folders" />
				</div>
			</div>

			<div class="panel-body">
				<div class="<%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" id="<%= randomNamespace %>facet">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(folderSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= folderSearchFacetDisplayContext.getParamValue() %>" />

					<ul class="folders list-unstyled">
						<li class="default facet-value">
							<a class="<%= folderSearchFacetDisplayContext.isNothingSelected() ? "text-primary" : "text-default" %>" data-value="" href="javascript:;"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
						</li>

						<%
						java.util.List<com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext> folderSearchFacetTermDisplayContexts = folderSearchFacetDisplayContext.getTermDisplayContexts();

						for (com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext folderSearchFacetTermDisplayContext : folderSearchFacetTermDisplayContexts) {
						%>

							<li class="facet-value">
								<a class="<%= folderSearchFacetTermDisplayContext.isSelected() ? "text-primary" : "text-default" %>" data-value="<%= folderSearchFacetTermDisplayContext.getFolderId() %>" href="javascript:;">
									<%= HtmlUtil.escape(folderSearchFacetTermDisplayContext.getDisplayName()) %>

									<c:if test="<%= folderSearchFacetTermDisplayContext.isFrequencyVisible() %>">
										<span class="frequency">(<%= folderSearchFacetTermDisplayContext.getFrequency() %>)</span>
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