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

<%@ page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.util.NamespaceUtil" %>
<portlet:defineObjects />

<style>
	.facet-checkbox-label {
		display: block;
	}
</style>

<%
ModifiedSearchFacetDisplayContext modifiedSearchFacetDisplayContext = 
	(ModifiedSearchFacetDisplayContext)java.util.Objects.requireNonNull(
			request.getAttribute(ModifiedSearchFacetDisplayContext.ATTRIBUTE));

String namespace = NamespaceUtil.randomNamespace("portlet_search_facet_modified", request);

String cssClassFacetTerm = "facet-term-" + namespace;
%>

<%
com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext customRangeTermDisplayContext = modifiedSearchFacetDisplayContext.getCustomRangeTermDisplayContext();

com.liferay.portal.search.web.internal.facet.display.context.CalendarRangeSearchFacetTermDisplayContext calendarRangeTermDisplayContext = modifiedSearchFacetDisplayContext.getCalendarRangeTermDisplayContext();

int i = 0;
%>

<liferay-ui:panel-container extended="<%= true %>" id='<%= namespace + "facetModifiedPanelContainer" %>' markupView="lexicon" persistState="<%= true %>">
	<liferay-ui:panel collapsible="<%= true %>" cssClass="<%= cssClassFacetTerm %>" id='<%= namespace + "facetModifiedPanel" %>' markupView="lexicon" persistState="<%= true %>" title="time">
		<aui:form method="post" name="fm">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= modifiedSearchFacetDisplayContext.getParamValue() %>" />
			<aui:input autocomplete="off" name='<%= HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) + "selection" %>' type="hidden" value="<%= modifiedSearchFacetDisplayContext.getParamValue() %>" />
			<aui:input autocomplete="off" name="inputFacetName" type="hidden" value="modified" />
	
			<aui:field-wrapper cssClass='<%= namespace + "calendar calendar_" %>' label="" name="<%= HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) %>">
				<ul class="list-unstyled modified">
					<%
					for (com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext modifiedSearchFacetTermDisplayContext : 
						modifiedSearchFacetDisplayContext.getTermDisplayContexts()) {
					%>
	
						<li class="facet-value" name="<%= renderResponse.getNamespace() + "ranger_"+i %>">
	
							<%
							String rangeCssClass = " text-default ";
	
							if (modifiedSearchFacetTermDisplayContext.isSelected()) {
								rangeCssClass = " text-primary ";
							}
	
							%>
	
							<input
								class="<%= cssClassFacetTerm + rangeCssClass%>"
								data-term-id="<%= modifiedSearchFacetTermDisplayContext.getRange() %>"
								id="<portlet:namespace /><%= modifiedSearchFacetTermDisplayContext.getLabel() %>"
								name="<portlet:namespace /><%= modifiedSearchFacetTermDisplayContext.getLabel() %>"
								onChange='<%= renderResponse.getNamespace() + "_applyFacet(event);" %>'
								type="checkbox"
								<%= modifiedSearchFacetTermDisplayContext.isSelected() ? "checked" : StringPool.BLANK %>
							/>
							
							<span class="term-name">
								<liferay-ui:message key="<%= modifiedSearchFacetTermDisplayContext.getLabel() %>" />
							</span>
							
							<small class="term-count">
								<span class="frequency">(<%= modifiedSearchFacetTermDisplayContext.getFrequency() %>)</span>
							</small>
							
						</li>
	
					<%
						i = i + 1;
					}
					%>
	
					<li class="facet-value">
	
						<%
						String customRangeCssClass = namespace + "custom-range-toggle";
	
						if (customRangeTermDisplayContext.isSelected()) {
							customRangeCssClass += " text-primary";
						}
						else {
							customRangeCssClass += " text-default";
						}
	
						%>
	
						<aui:a cssClass="<%= customRangeCssClass %>" href="javascript:;">
							<liferay-ui:message key="custom-range" />&hellip;
	
							<span class="frequency">(<%= customRangeTermDisplayContext.getFrequency() %>)</span>
						</aui:a>
					</li>
	
					<div class="<%= !calendarRangeTermDisplayContext.isSelected() ? "hide" : StringPool.BLANK %> modified-custom-range" id="<%= namespace %>customRange">
						<div class="col-md-6" id="<%= namespace %>customRangeFrom">
							<aui:field-wrapper label="from">
								<liferay-ui:input-date
									dayParam='<%= calendarRangeTermDisplayContext.getFromDayParam() %>'
									dayValue="<%= calendarRangeTermDisplayContext.getFromDayValue() %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= calendarRangeTermDisplayContext.getFromFirstDayOfWeek() %>"
									monthParam='<%= calendarRangeTermDisplayContext.getFromMonthParam() %>'
									monthValue="<%= calendarRangeTermDisplayContext.getFromMonthValue() %>"
									name='<%= calendarRangeTermDisplayContext.getFromName() %>'
									yearParam='<%= calendarRangeTermDisplayContext.getFromYearParam() %>'
									yearValue="<%= calendarRangeTermDisplayContext.getFromYearValue() %>"
								/>
							</aui:field-wrapper>
						</div>
	
						<div class="col-md-6" id="<%= namespace %>customRangeTo">
							<aui:field-wrapper label="to">
								<liferay-ui:input-date
									dayParam='<%= calendarRangeTermDisplayContext.getToDayParam() %>'
									dayValue="<%= calendarRangeTermDisplayContext.getFromDayValue() %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= calendarRangeTermDisplayContext.getToFirstDayOfWeek() %>"
									monthParam='<%= calendarRangeTermDisplayContext.getToMonthParam() %>'
									monthValue="<%= calendarRangeTermDisplayContext.getToMonthValue() %>"
									name='<%= calendarRangeTermDisplayContext.getToName() %>'
									yearParam='<%= calendarRangeTermDisplayContext.getToYearParam() %>'
									yearValue="<%= calendarRangeTermDisplayContext.getToYearValue() %>"
								/>
							</aui:field-wrapper>
						</div>
	
							<input
								class="<%= cssClassFacetTerm %>"
								data-term-id=""
								id="<portlet:namespace /><%= "customRange" %>"
								name="<portlet:namespace /><%= "customRange" %>"
								onChange='<%= renderResponse.getNamespace() + "_applyFacet(event);" %>'
								type="hidden"
								<%= calendarRangeTermDisplayContext.isSelected() ? "checked" : StringPool.BLANK %>
							/>
	
						<%
						String taglibSearchCustomRange = "window['" + renderResponse.getNamespace() + HtmlUtil.escapeJS(modifiedSearchFacetDisplayContext.getParamName()) + "searchCustomRange'](event);";
						%>
	
						<aui:button disabled="<%= (!calendarRangeTermDisplayContext.isFromBeforeTo()) %>" name="searchCustomRangeButton" onClick="<%= taglibSearchCustomRange %>" value="search" />
					</div>
				</ul>
			</aui:field-wrapper>
			<c:if test="<%= !modifiedSearchFacetDisplayContext.isNothingSelected() %>">
				<aui:a cssClass="text-default" href="javascript:;" onClick='<%= namespace + "_clearFacet('" + modifiedSearchFacetDisplayContext.getParamName() + "');" %>'><small><liferay-ui:message key="portlet.category-facet.clear" /></small></aui:a>
			</c:if>
		</aui:form>	
	</liferay-ui:panel>
</liferay-ui:panel-container>

<aui:script>
	function <portlet:namespace /><%= HtmlUtil.escapeJS(modifiedSearchFacetDisplayContext.getParamName()) %>searchCustomRange(event) {
		var A = AUI();
		var Lang = A.Lang;
		var LString = Lang.String;

		var form = AUI.$(document.<portlet:namespace />fm);

		var dayFrom = form.fm('<%= calendarRangeTermDisplayContext.getFromDayParam() %>').val();
		var monthFrom = Lang.toInt(form.fm('<%= calendarRangeTermDisplayContext.getFromMonthParam() %>').val()) + 1;
		var yearFrom = form.fm('<%= calendarRangeTermDisplayContext.getFromYearParam()%>').val();

		var dayTo = form.fm('<%= calendarRangeTermDisplayContext.getToDayParam() %>').val();
		var monthTo = Lang.toInt(form.fm('<%= calendarRangeTermDisplayContext.getToMonthParam() %>').val()) + 1;
		var yearTo = form.fm('<%= calendarRangeTermDisplayContext.getToYearParam() %>').val();

		var range = '[' + yearFrom + LString.padNumber(monthFrom, 2) + LString.padNumber(dayFrom, 2) + '000000 TO ' + yearTo + LString.padNumber(monthTo, 2) + LString.padNumber(dayTo, 2) + '235959]';

		form.fm('customRange').val(range);
		form.fm('customRange').attr("checked", true);
		form.fm('customRange').attr("data-term-id", range);
		form.fm('<%=HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) + "selection" %>').val('<%= i + 1 %>');
		form.fm('<%=HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName())%>').val(range);

		<portlet:namespace />_applyFacet(event);

	}
</aui:script>

<aui:script use="aui-form-validator">
	var Util = Liferay.Util;

	var customRangeFrom = Liferay.component('<%= renderResponse.getNamespace() %>modifiedfromDatePicker');
	var customRangeTo = Liferay.component('<%= renderResponse.getNamespace() %>modifiedtoDatePicker');
	var searchButton = A.one('#<portlet:namespace />searchCustomRangeButton');

	var preventKeyboardDateChange = function(event) {
		if (!event.isKey('TAB')) {
			event.preventDefault();
		}
	};

	A.one('#<portlet:namespace /><%= HtmlUtil.escapeJS(modifiedSearchFacetDisplayContext.getParamName()) %>from').on('keydown', preventKeyboardDateChange);
	A.one('#<portlet:namespace /><%= HtmlUtil.escapeJS(modifiedSearchFacetDisplayContext.getParamName()) %>to').on('keydown', preventKeyboardDateChange);

	var DEFAULTS_FORM_VALIDATOR = A.config.FormValidator;

	A.mix(
		DEFAULTS_FORM_VALIDATOR.STRINGS,
		{
			<portlet:namespace />dateRange: '<%= UnicodeLanguageUtil.get(request, "search-custom-range-invalid-date-range") %>'
		},
		true
	);

	A.mix(
		DEFAULTS_FORM_VALIDATOR.RULES,
		{
			<portlet:namespace />dateRange: function(val, fieldNode, ruleValue) {
				return A.Date.isGreaterOrEqual(customRangeTo.getDate(), customRangeFrom.getDate());
			}
		},
		true
	);

	var customRangeValidator = new A.FormValidator(
		{
			boundingBox: document.<portlet:namespace />fm,
			fieldContainer: 'div',
			on: {
				errorField: function(event) {
					Util.toggleDisabled(searchButton, true);
				},
				validField: function(event) {
					Util.toggleDisabled(searchButton, false);
				}
			},
			rules: {
				'<portlet:namespace /><%= HtmlUtil.escapeJS(modifiedSearchFacetDisplayContext.getParamName()) %>from': {
					<portlet:namespace />dateRange: true
				},
				'<portlet:namespace /><%= HtmlUtil.escapeJS(modifiedSearchFacetDisplayContext.getParamName()) %>to': {
					<portlet:namespace />dateRange: true
				}
			}
		}
	);

	var onRangeSelectionChange = function(event) {
		customRangeValidator.validate();
	};

	customRangeFrom.on('selectionChange', onRangeSelectionChange);
	customRangeTo.on('selectionChange', onRangeSelectionChange);

	A.one('.<%= namespace %>custom-range-toggle').on(
		'click',
		function(event) {
			event.halt();

			A.one('#<%= namespace + "customRange" %>').toggle();
		}
	);
</aui:script>

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

				var key = '<%= modifiedSearchFacetDisplayContext.getParamName() %>';

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