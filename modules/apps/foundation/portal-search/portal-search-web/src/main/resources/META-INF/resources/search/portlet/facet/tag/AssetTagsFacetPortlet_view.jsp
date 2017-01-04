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

<%@page import="com.liferay.portal.kernel.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.search.web.internal.portlet.facet.asset.tag.AssetTagsFacetPortletTermDisplayContext"%>
<%@page import="com.liferay.portal.search.web.internal.portlet.facet.asset.tag.AssetTagsFacetPortletDisplayContext"%>

<%@ include file="/init.jsp" %>

<% 
AssetTagsFacetPortletDisplayContext  assetTagsFacetPortletDisplayContext =
					(AssetTagsFacetPortletDisplayContext)java.util.Objects.requireNonNull(
						request.getAttribute(AssetTagsFacetPortletDisplayContext.ATTRIBUTE));

String cssClass = "search-facet search-".concat(HtmlUtil.escapeAttribute(assetTagsFacetPortletDisplayContext.getDisplayStyle()));
String cssClassFacetTerm = "facet-term-" + renderResponse.getNamespace();
%>

<c:choose>
	<c:when test="<%= assetTagsFacetPortletDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetTagsFacetPortletDisplayContext.getFieldParamInputName()) %>" type="hidden" value="<%= assetTagsFacetPortletDisplayContext.getFieldParamInputValue() %>" />
	</c:when>
	<c:otherwise>
		<div class="panel panel-default">
			<div class="panel-heading">
				<div class="panel-title">
					<liferay-ui:message key="tags" />
				</div>
			</div>

			<div class="panel-body">
				<aui:form method="post" name="assetTagsFacetForm">
					<div class="asset-tags <%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(assetTagsFacetPortletDisplayContext.getFieldParamInputName()) %>" id="<%= renderResponse.getNamespace() %>facet">
						<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetTagsFacetPortletDisplayContext.getFieldParamInputName()) %>" type="hidden" value="<%= assetTagsFacetPortletDisplayContext.getFieldParamInputValue() %>" />
	
						<ul class="<%= assetTagsFacetPortletDisplayContext.isCloudWithCount() ? "tag-cloud" : "tag-list" %> list-unstyled">
							<li class="default facet-value">
								<a class="<%= assetTagsFacetPortletDisplayContext.isNothingSelected() ? "text-primary" : "text-default" %>" data-value="" href="javascript:;"><liferay-ui:message key="<%= HtmlUtil.escape(assetTagsFacetPortletDisplayContext.getFacetLabel()) %>" /></a>
							</li>
							
							<aui:fieldset>
								<%
								int i = 1;
								for (AssetTagsFacetPortletTermDisplayContext assetTagsFacetPortletTermDisplayContext :  assetTagsFacetPortletDisplayContext.getTermDisplayContexts()) {
									String termName = "term_" + i++;
									
								%>
									<aui:input autocomplete="off" name='<%= termName + "_value" %>' type="hidden" value="<%= assetTagsFacetPortletTermDisplayContext.getValue() %>"/>
									
									<li class="facet-value tag-popularity-<%= assetTagsFacetPortletTermDisplayContext.getPopularity() %>">
										
										<input 
											class="<%= cssClassFacetTerm %>" 
											data-term-id="<%= assetTagsFacetPortletTermDisplayContext.getValue() %>" 
											id="<portlet:namespace /><%= termName %>" 
											name="<portlet:namespace /><%= termName %>"
											onChange='<%= renderResponse.getNamespace() + "_applyFacet(event);" %>'
											type="checkbox" 
											<%= assetTagsFacetPortletTermDisplayContext.isSelected() ? "checked" : StringPool.BLANK %>
											/>
										
										<a class="<%= assetTagsFacetPortletTermDisplayContext.isSelected() ? "text-primary" : "text-default" %>" data-value="<%= HtmlUtil.escapeAttribute(assetTagsFacetPortletTermDisplayContext.getValue()) %>" href="javascript:;">
											<%= HtmlUtil.escape(assetTagsFacetPortletTermDisplayContext.getDisplayName()) %>
		
											<c:if test="<%= assetTagsFacetPortletTermDisplayContext.isFrequencyVisible() %>">
												<span class="frequency">(<%= assetTagsFacetPortletTermDisplayContext.getFrequency() %>)</span>
											</c:if>
										</a>
									</li>
		
								<%
								}
								%>
							</aui:fieldset>
						</ul>
					</div>
				</aui:form>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<aui:script>
	function <portlet:namespace />_removeParameters(key, parameterArray) {
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

	function <portlet:namespace />_addParameter(key, value, parameterArray) {
		key = encodeURI(key);
		value = encodeURI(value);

		parameterArray[parameterArray.length] = [key, value].join('=');

		return parameterArray;
	}

	function <portlet:namespace />_clearFacet(facetName) {
		var parameterArray = document.location.search.substr(1).split('&');

		var newParameters = <portlet:namespace />_removeParameters(facetName, parameterArray);

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

				var key = '<%= assetTagsFacetPortletDisplayContext.getFieldParamInputName() %>';

				var parameterArray = document.location.search.substr(1).split('&');

				var newParameters = <portlet:namespace />_removeParameters(key, parameterArray);

				if (selectedFacets.length > 0) {
					newParameters = <portlet:namespace />_addParameter(key, selectedFacets.join(','), newParameters);
				}

				document.location.search = newParameters.join('&');
			}
		},
		['aui-base']
	);
</aui:script>