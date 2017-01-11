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
String fieldParamSelection = ParamUtil.getString(request, facet.getFieldId() + "selection", "0");

int fromDay = ParamUtil.getInteger(request, HtmlUtil.escapeJS(facet.getFieldId()) + "dayFrom");
int fromMonth = ParamUtil.getInteger(request, HtmlUtil.escapeJS(facet.getFieldId()) + "monthFrom");
int fromYear = ParamUtil.getInteger(request, HtmlUtil.escapeJS(facet.getFieldId()) + "yearFrom");

Date fromDate = PortalUtil.getDate(fromMonth, fromDay, fromYear);

int toDay = ParamUtil.getInteger(request, HtmlUtil.escapeJS(facet.getFieldId()) + "dayTo");
int toMonth = ParamUtil.getInteger(request, HtmlUtil.escapeJS(facet.getFieldId()) + "monthTo");
int toYear = ParamUtil.getInteger(request, HtmlUtil.escapeJS(facet.getFieldId()) + "yearTo");

Date toDate = PortalUtil.getDate(toMonth, toDay, toYear);

JSONArray rangesJSONArray = dataJSONObject.getJSONArray("ranges");

int index = 1;
%>

<%
com.liferay.portal.search.web.internal.facet.display.builder.ModifiedSearchFacetDisplayBuilder modifiedSearchFacetDisplayBuilder = 
	new com.liferay.portal.search.web.internal.facet.display.builder.ModifiedSearchFacetDisplayBuilder();

modifiedSearchFacetDisplayBuilder.setFacet(facet);
modifiedSearchFacetDisplayBuilder.setParamName(facet.getFieldId());
modifiedSearchFacetDisplayBuilder.setParamValue(fieldParam);
modifiedSearchFacetDisplayBuilder.setParamSelection(fieldParamSelection);
modifiedSearchFacetDisplayBuilder.setRangesJSONArray(rangesJSONArray);

modifiedSearchFacetDisplayBuilder.setFormDay(fromDay);
modifiedSearchFacetDisplayBuilder.setFormMonth(fromMonth);
modifiedSearchFacetDisplayBuilder.setFormYear(fromYear);

modifiedSearchFacetDisplayBuilder.setToDay(toDay);
modifiedSearchFacetDisplayBuilder.setToMonth(toMonth);
modifiedSearchFacetDisplayBuilder.setToYear(toYear);

modifiedSearchFacetDisplayBuilder.setTimeZone(timeZone);
modifiedSearchFacetDisplayBuilder.setLocale(locale);

com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetDisplayContext modifiedSearchFacetDisplayContext = modifiedSearchFacetDisplayBuilder.build();

com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext defaultTermDisplayContext = modifiedSearchFacetDisplayContext.getDefaultTermDisplayContext();

com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext customRangeTermDisplayContext = modifiedSearchFacetDisplayContext.getCustomRangeTermDisplayContext();

com.liferay.portal.search.web.internal.facet.display.context.CalendarRangeSearchFacetTermDisplayContext calendarRangeSearchFacetTermDisplayContext = modifiedSearchFacetDisplayContext.getCalendarRangeTermDisplayContext();
%>


<div class="panel panel-default">
	<div class="panel-heading">
		<div class="panel-title">
			<liferay-ui:message key="time" />
		</div>
	</div>

	<div class="panel-body">
		<div class="<%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" id="<%= randomNamespace %>facet">
			<aui:form name="fm">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" type="hidden" value="<%= fieldParam %>" />
			<aui:input autocomplete="off" name='<%= HtmlUtil.escapeAttribute(facet.getFieldId()) + "selection" %>' type="hidden" value="<%= fieldParamSelection %>" />

			<aui:field-wrapper cssClass='<%= randomNamespace + "calendar calendar_" %>' label="" name="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>">
				<ul class="list-unstyled modified">
					<li class="default facet-value">
						<aui:a cssClass="<%= defaultTermDisplayContext.isSelected() ? "text-primary" : "text-default"  %>" href="javascript:;">
							<liferay-ui:message key="<%= HtmlUtil.escape(defaultTermDisplayContext.getLabel()) %>" />
						</aui:a>
					</li>

					<%
					for (com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext modifiedSearchFacetTermDisplayContext : 
							modifiedSearchFacetDisplayContext.getTermDisplayContexts()) {
						index = index + 1;
					%>

						<li class="facet-value">
							<aui:a cssClass="<%= modifiedSearchFacetTermDisplayContext.isSelected() ? "text-primary" :"text-default" %>" 
								data="<%= modifiedSearchFacetTermDisplayContext.getData() %>" href="javascript:;">
								<liferay-ui:message key="<%= modifiedSearchFacetTermDisplayContext.getLabel() %>" />

								<span class="frequency">(<%= modifiedSearchFacetTermDisplayContext.getFrequency() %>)</span>
							</aui:a>
						</li>

					<%
					}
					%>

					<li class="facet-value">

						<%
 						String customRangeCssClass = randomNamespace + "custom-range-toggle";

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

					<div class="<%= !customRangeTermDisplayContext.isSelected() ? "hide" : StringPool.BLANK %> modified-custom-range" id="<%= randomNamespace %>customRange">
						<div class="col-md-6" id="<%= randomNamespace %>customRangeFrom">
							<aui:field-wrapper label="from">
								<liferay-ui:input-date
									dayParam='<%= calendarRangeSearchFacetTermDisplayContext.getFromDayParam() %>'
									dayValue="<%= calendarRangeSearchFacetTermDisplayContext.getFromDayValue() %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= calendarRangeSearchFacetTermDisplayContext.getFromFirstDayOfWeek() %>"
									monthParam='<%= calendarRangeSearchFacetTermDisplayContext.getFromDayParam() %>'
									monthValue="<%= calendarRangeSearchFacetTermDisplayContext.getFromMonthValue() %>"
									name='<%= calendarRangeSearchFacetTermDisplayContext.getFromName() %>'
									yearParam='<%= calendarRangeSearchFacetTermDisplayContext.getFromYearParam() %>'
									yearValue="<%= calendarRangeSearchFacetTermDisplayContext.getFromYearValue() %>"
								/>
							</aui:field-wrapper>
						</div>

						<div class="col-md-6" id="<%= randomNamespace %>customRangeTo">
							<aui:field-wrapper label="to">
								<liferay-ui:input-date
									dayParam='<%= calendarRangeSearchFacetTermDisplayContext.getToDayParam() %>'
									dayValue="<%= calendarRangeSearchFacetTermDisplayContext.getToDayValue() %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= calendarRangeSearchFacetTermDisplayContext.getToFirstDayOfWeek() %>"
									monthParam='<%= calendarRangeSearchFacetTermDisplayContext.getToMonthParam() %>'
									monthValue="<%= calendarRangeSearchFacetTermDisplayContext.getToMonthValue() %>"
									name='<%= calendarRangeSearchFacetTermDisplayContext.getToName() %>'
									yearParam='<%= calendarRangeSearchFacetTermDisplayContext.getToYearParam() %>'
									yearValue="<%= calendarRangeSearchFacetTermDisplayContext.getToYearValue() %>"
								/>
							</aui:field-wrapper>
						</div>

						<%
						String taglibSearchCustomRange = "window['" + renderResponse.getNamespace() + HtmlUtil.escapeJS(facet.getFieldId()) + "searchCustomRange'](" + (index + 1) + ");";
						%>

						<aui:button disabled="<%= !calendarRangeSearchFacetTermDisplayContext.isFromBeforeTo() %>" name="searchCustomRangeButton" onClick="<%= taglibSearchCustomRange %>" value="search" />
					</div>
				</ul>
			</aui:field-wrapper>
			</aui:form>
		</div>
	</div>
</div>

<aui:script>
	function <portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>searchCustomRange(selection) {
		var A = AUI();
		var Lang = A.Lang;
		var LString = Lang.String;

		var form = AUI.$(document.<portlet:namespace />fm);

		var dayFrom = form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>dayFrom').val();
		var monthFrom = Lang.toInt(form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>monthFrom').val()) + 1;
		var yearFrom = form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>yearFrom').val();

		var dayTo = form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>dayTo').val();
		var monthTo = Lang.toInt(form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>monthTo').val()) + 1;
		var yearTo = form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>yearTo').val();

		var range = '[' + yearFrom + LString.padNumber(monthFrom, 2) + LString.padNumber(dayFrom, 2) + '000000 TO ' + yearTo + LString.padNumber(monthTo, 2) + LString.padNumber(dayTo, 2) + '235959]';

		form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>').val(range);
		form.fm('<%= HtmlUtil.escapeJS(facet.getFieldId()) %>selection').val(selection);

		submitForm(form);
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

	A.one('#<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>from').on('keydown', preventKeyboardDateChange);
	A.one('#<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>to').on('keydown', preventKeyboardDateChange);

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
				'<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>from': {
					<portlet:namespace />dateRange: true
				},
				'<portlet:namespace /><%= HtmlUtil.escapeJS(facet.getFieldId()) %>to': {
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

	A.one('.<%= randomNamespace %>custom-range-toggle').on(
		'click',
		function(event) {
			event.halt();

			A.one('#<%= randomNamespace + "customRange" %>').toggle();
		}
	);
</aui:script>