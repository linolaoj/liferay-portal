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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.util.NamespaceUtil" %>

<portlet:defineObjects />

<style>
	.facet-checkbox-label {
		display: block;
	}
</style>

<%
FolderSearchFacetDisplayContext folderSearchFacetDisplayContext = (FolderSearchFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(FolderSearchFacetDisplayContext.ATTRIBUTE));

String namespace = NamespaceUtil.randomNamespace("portlet_search_facet_folder", request);

String cssClassFacetTerm = "facet-term-" + namespace;
%>

<c:choose>
	<c:when test="<%= folderSearchFacetDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(folderSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= folderSearchFacetDisplayContext.getParamValue() %>" />
	</c:when>
	<c:otherwise>
		<liferay-ui:panel-container extended="true" id='<%= namespace + "facetFoldersPanelContainer" %>' markupView="lexicon" persistState="true">
			<liferay-ui:panel collapsible="true" cssClass="search-facet" id='<%= namespace + "facetFoldersPanel" %>' markupView="lexicon" persistState="true" title="portlet.folder-facet.title">
				<aui:form method="post" name="FoldersFacetForm">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(folderSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= folderSearchFacetDisplayContext.getParamValue() %>" />

					<aui:fieldset>
						<ul class="list-unstyled">

							<%
							int i = 1;

							for (FolderSearchFacetTermDisplayContext folderSearchFacetTermDisplayContext : folderSearchFacetDisplayContext.getTermDisplayContexts()) {
								String termName = "term_" + i++;
							%>

								<li class="facet-value">
									<label class="facet-checkbox-label" for="<portlet:namespace /><%= termName %>">
										<input
											class="<%= cssClassFacetTerm %>"
											data-term-id="<%= folderSearchFacetTermDisplayContext.getFolderId() %>"
											id="<portlet:namespace /><%= termName %>"
											name="<portlet:namespace /><%= termName %>"
											onChange='<%= renderResponse.getNamespace() + "_applyFacet(event);" %>'
											type="checkbox"
											<%= folderSearchFacetTermDisplayContext.isSelected() ? "checked" : StringPool.BLANK %>
										/>

										<span class="term-name">
											<%= HtmlUtil.escape(folderSearchFacetTermDisplayContext.getDisplayName()) %>
										</span>

										<c:if test="<%= folderSearchFacetTermDisplayContext.isFrequencyVisible() %>">
											<small class="term-count">
												(<%= folderSearchFacetTermDisplayContext.getFrequency() %>)
											</small>
										</c:if>
									</label>
								</li>

							<%
							}
							%>

						</ul>
					</aui:fieldset>

					<c:if test="<%= !folderSearchFacetDisplayContext.isNothingSelected() %>">
						<aui:a cssClass="text-default" href="javascript:;" onClick='<%= namespace + "_clearFacet('" + folderSearchFacetDisplayContext.getParamName() + "');" %>'><small><liferay-ui:message key="portlet.folder-facet.clear" /></small></aui:a>
					</c:if>
				</aui:form>
			</liferay-ui:panel>
		</liferay-ui:panel-container>
	</c:otherwise>
</c:choose>

<aui:script>
	function <%= namespace %>_removeParameters(key, parameterArray) {
		key = encodeURI(key);

		var newParameters = [];

		AUI.$.each(
			parameterArray,
			function(index, item) {
				var itemSplit = item.split('=');

				if (itemSplit) {
					if (itemSplit[0] != key) {
						newParameters.push(item);
					}
				}
			}
		);

		return newParameters;
	}

	function <%= namespace %>_addParameter(key, value, parameterArray) {
		key = encodeURI(key);
		value = encodeURI(value);

		parameterArray[parameterArray.length] = [key, value].join('=');

		return parameterArray;
	}

	function <%= namespace %>_clearFacet(facetName) {
		var parameterArray = document.location.search.substr(1).split('&');

		var newParameters = <%= namespace %>_removeParameters(facetName, parameterArray);

		document.location.search = newParameters.join('&');
	}

	Liferay.provide(
		window,
		'<portlet:namespace />_applyFacet',
		function(event) {
			var form = event.currentTarget.form;

			if (form) {
				var formCheckboxes = $('#' + form.id + ' input.' + '<%= cssClassFacetTerm %>');

				var selectedFacets = [];

				formCheckboxes.each(
					function(index, value) {
						if (value.checked) {
							var termId = value.getAttribute('data-term-id');

							selectedFacets.push(termId);
						}
					}
				);

				var key = '<%= folderSearchFacetDisplayContext.getParamName() %>';

				var parameterArray = document.location.search.substr(1).split('&');

				var newParameters = <%= namespace %>_removeParameters(key, parameterArray);

				if (selectedFacets.length > 0) {
					newParameters = <%= namespace %>_addParameter(key, selectedFacets.join(','), newParameters);
				}

				document.location.search = newParameters.join('&');
			}
		},
		['aui-base']
	);
</aui:script>