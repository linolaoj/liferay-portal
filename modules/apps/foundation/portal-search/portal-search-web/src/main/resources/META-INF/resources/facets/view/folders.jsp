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

int frequencyThreshold = dataJSONObject.getInt("frequencyThreshold");
int maxTerms = dataJSONObject.getInt("maxTerms", 10);
boolean showAssetCount = dataJSONObject.getBoolean("showAssetCount", true);

if (Validator.isNull(fieldParam)) {
	fieldParam = String.valueOf(0);
}

FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
	new FolderSearchFacetDisplayContext(facet, fieldParam, frequencyThreshold, maxTerms, showAssetCount, DLFolderLocalServiceUtil.getService());
%>

<div class="panel panel-default">
	<div class="panel-heading">
		<div class="panel-title">
			<liferay-ui:message key="folders" />
		</div>
	</div>

	<div class="panel-body">
		<div class="<%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" id="<%= randomNamespace %>facet">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" type="hidden" value="<%= fieldParam %>" />

			<ul class="folders list-unstyled">
				<li class="default facet-value">
					<a class="<%= Validator.isNull(fieldParam) ? "text-primary" : "text-default" %>" data-value="" href="javascript:;"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
				</li>

				<%
				List<FolderSearchFacetTermDisplayContext> folderSearchFacetTermDisplayContexts = folderSearchFacetDisplayContext.getTermDisplayContexts();

				for (FolderSearchFacetTermDisplayContext folderSearchFacetTermDisplayContext : folderSearchFacetTermDisplayContexts) {
					long curFolderId = folderSearchFacetTermDisplayContext.getFolderId();
					String descriptiveName = HtmlUtil.escape(folderSearchFacetTermDisplayContext.getDescriptiveName());
					boolean isShowCount = folderSearchFacetTermDisplayContext.isShowCount();
					int frequency = folderSearchFacetTermDisplayContext.getCount();
					boolean isSelected = folderSearchFacetTermDisplayContext.isSelected();
				%>

					<li class="facet-value">
						<a class="<%= isSelected ? "text-primary" : "text-default" %>" data-value="<%= curFolderId %>" href="javascript:;">
							<%= descriptiveName %>

							<c:if test="<%= isShowCount %>">
								<span class="frequency">(<%= frequency %>)</span>
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