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

int index = rangesJSONArray.length();
%>

<%
com.liferay.portal.search.web.internal.facet.display.builder.ModifiedSearchFacetDisplayBuilder modifiedSearchFacetDisplayBuilder = new com.liferay.portal.search.web.internal.facet.display.builder.ModifiedSearchFacetDisplayBuilder(); 

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

com.liferay.portal.search.web.internal.facet.display.context.CalendarRangeSearchFacetTermDisplayContext calendarRangeTermDisplayContext = modifiedSearchFacetDisplayContext.getCalendarRangeTermDisplayContext();

%>

<liferay-ui:panel-container extended="<%= true %>" id='<%= randomNamespace + "facetModifiedPanelContainer" %>' markupView="lexicon" persistState="<%= true %>">
	<liferay-ui:panel collapsible="<%= true %>" cssClass="<%= cssClass %>" id='<%= randomNamespace + "facetModifiedPanel" %>' markupView="lexicon" persistState="<%= true %>" title="time">
		<aui:form method="post" name="fm">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= modifiedSearchFacetDisplayContext.getParamValue() %>" />
			<aui:input autocomplete="off" name='<%= HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) + "selection" %>' type="hidden" value="<%= modifiedSearchFacetDisplayContext.getParamValue() %>" />
			<aui:input autocomplete="off" name="inputFacetName" type="hidden" value="modified" />
	
			<aui:field-wrapper cssClass='<%= randomNamespace + "calendar calendar_" %>' label="" name="<%= HtmlUtil.escapeAttribute(modifiedSearchFacetDisplayContext.getParamName()) %>">
				<ul class="list-unstyled modified">
					<li class="default facet-value">
	
						<%
						String defaultRangeCssClass = "text-default";
	
						if (defaultTermDisplayContext.isSelected()) {
							defaultRangeCssClass = "text-primary";
						}
						%>
	
						<aui:a cssClass="<%= defaultRangeCssClass %>" href="javascript:;">
							<liferay-ui:message key="<%= HtmlUtil.escape(defaultTermDisplayContext.getLabel()) %>" />
						</aui:a>
					</li>
	
					<%
					int i = 0;
					for (com.liferay.portal.search.web.internal.facet.display.context.ModifiedSearchFacetTermDisplayContext modifiedSearchFacetTermDisplayContext : 
						modifiedSearchFacetDisplayContext.getTermDisplayContexts()) {
					%>
	
						<li class="facet-value" name="<%= renderResponse.getNamespace() + "ranger_"+i %>">
	
							<%
							String rangeCssClass = "text-default";
	
							if (modifiedSearchFacetTermDisplayContext.isSelected()) {
								rangeCssClass = "text-primary";
							}
	
							%>
	
							<aui:a cssClass="<%= rangeCssClass %>" data="<%= modifiedSearchFacetTermDisplayContext.getData() %>" href="javascript:;">
								<liferay-ui:message key="<%= modifiedSearchFacetTermDisplayContext.getLabel() %>" />
								
								<span class="frequency">(<%= modifiedSearchFacetTermDisplayContext.getFrequency() %>)</span>
							</aui:a>
						</li>
	
						<aui:input autocomplete="off"
							name="<%= "ranger_"+i+"_value" %>"
							type="hidden"
							value="<%= modifiedSearchFacetTermDisplayContext.getRange() %>"
						/>
	
					<%
						i = i + 1;
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
	
					<div class="<%= !calendarRangeTermDisplayContext.isSelected() ? "hide" : StringPool.BLANK %> modified-custom-range" id="<%= randomNamespace %>customRange">
						<div class="col-md-6" id="<%= randomNamespace %>customRangeFrom">
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
	
						<div class="col-md-6" id="<%= randomNamespace %>customRangeTo">
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
	
						<%
						String taglibSearchCustomRange = "window['" + renderResponse.getNamespace() + HtmlUtil.escapeJS(facet.getFieldId()) + "searchCustomRange'](" + (index + 1) + ");";
						%>
	
						<aui:button disabled="<%= (!calendarRangeTermDisplayContext.isFromBeforeTo()) %>" name="searchCustomRangeButton" onClick="<%= taglibSearchCustomRange %>" value="search" />
					</div>
				</ul>
			</aui:field-wrapper>
		</aui:form>	
	</liferay-ui:panel>
</liferay-ui:panel-container>

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