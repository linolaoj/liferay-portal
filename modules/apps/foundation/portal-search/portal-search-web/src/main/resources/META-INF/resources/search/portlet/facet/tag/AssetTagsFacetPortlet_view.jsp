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

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.search.web.internal.portlet.facet.tag.AssetTagsFacetPortletDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.portlet.facet.tag.AssetTagsFacetPortletTermDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.util.NamespaceUtil" %>

<%
AssetTagsFacetPortletDisplayContext assetTagsFacetPortletDisplayContext =
	(AssetTagsFacetPortletDisplayContext)java.util.Objects.requireNonNull(
		request.getAttribute(AssetTagsFacetPortletDisplayContext.ATTRIBUTE));

String namespace = NamespaceUtil.randomNamespace("portlet_search_facet_tag", request);

String cssClassFacetTerm = "facet-term-" + namespace;

String paramName = assetTagsFacetPortletDisplayContext.getFieldParamInputName();
%>

<c:choose>
	<c:when test="<%= assetTagsFacetPortletDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetTagsFacetPortletDisplayContext.getFieldParamInputName()) %>" type="hidden" value="<%= assetTagsFacetPortletDisplayContext.getFieldParamInputValue() %>" />
	</c:when>
	<c:otherwise>
		<liferay-ui:panel-container extended="true" id='<%= namespace + "facetTagPanelContainer" %>' markupView="lexicon" persistState="true">
			<liferay-ui:panel collapsible="true" cssClass="search-facet" id='<%= namespace + "facetTagPanel" %>' markupView="lexicon" persistState="true" title="tags">
				<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetTagsFacetPortletDisplayContext.getFieldParamInputName()) %>" type="hidden" value="<%= assetTagsFacetPortletDisplayContext.getFieldParamInputValue() %>" />

				<c:if test="<%= !assetTagsFacetPortletDisplayContext.isNothingSelected() %>">
					<p><a class="text-default" data-value="0" href="javascript:;"><liferay-ui:message key="facet-portlet-clear" /></a></p>
				</c:if>

				<%
				int i = 1;

				for (AssetTagsFacetPortletTermDisplayContext assetTagsFacetPortletTermDisplayContext : assetTagsFacetPortletDisplayContext.getTermDisplayContexts()) {
					String termName = "term_" + i++;
				%>

					<aui:input autocomplete="off"
						name='<%= termName + "_value" %>'
						type="hidden"
						value="<%= assetTagsFacetPortletTermDisplayContext.getValue() %>"
					/>

					<aui:input
						checked="<%= assetTagsFacetPortletTermDisplayContext.isSelected() %>"
						cssClass="<%= cssClassFacetTerm %>"
						label="TODO inline above!"
						name="<%= termName %>"
						type="checkbox"
					>
						<span style="font-size: small; color: black;"><%= HtmlUtil.escape(assetTagsFacetPortletTermDisplayContext.getTerm()) %></span>
						<c:if test="<%= assetTagsFacetPortletTermDisplayContext.isFrequencyVisible() %>">
							<span style="font-size: x-small; color: gray;">&nbsp;(<%= assetTagsFacetPortletTermDisplayContext.getFrequency() %>)</span>
						</c:if>
					</aui:input>

				<%
				}
				%>

			</liferay-ui:panel>
		</liferay-ui:panel-container>
	</c:otherwise>
</c:choose>

<aui:script sandbox="<%= true %>">
	$('<%= "." + cssClassFacetTerm %>').on(
		'click',
		function(event) {
			var term = $(event.currentTarget);

			var inputName = term[0].getAttribute('name') + "_value";
			var input = $("[name='" + inputName + "']");
			var inputVal = input.val();

			<%= namespace %>_insertParam("<%= paramName %>", inputVal);
		}
	);

	function <%= namespace %>_insertParam(key, value) {
		key = encodeURI(key);
		value = encodeURI(value);

		var kvp = document.location.search.substr(1).split('&');

		var i = kvp.length;
		var x;

		while (i--) {
			x = kvp[i].split('=');

			if (x[0]==key) {
				x[1] = value;

				kvp[i] = x.join('=');

				break;
			}
		}

		if (i<0) {
			kvp[kvp.length] = [key,value].join('=');
		}

		//this will reload the page, it's likely better to store this until finished

		document.location.search = kvp.join('&');
	}
</aui:script>