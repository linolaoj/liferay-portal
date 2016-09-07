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

package com.liferay.portal.search.web.internal.portlet;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.search.web.internal.display.context.PortalHttpServletRequestSupplier;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.SearchContainerSupplier;

import java.util.Optional;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author André de Oliveira
 */
public class SearchPortletSearchContainerSupplier
	implements SearchContainerSupplier {

	public SearchPortletSearchContainerSupplier(
		RenderRequest renderRequest, Language language,
		PortalHttpServletRequestSupplier requestSupplier, Html html,
		String keywords, PortletURLFactory portletURLFactory) {

		_renderRequest = renderRequest;
		_language = language;
		_requestSupplier = requestSupplier;
		_html = html;
		_keywords = keywords;
		_portletURLFactory = portletURLFactory;
	}

	@Override
	public SearchContainer<Document> getSearchContainer(
		Optional<String> startPageParamNameOptional,
		Optional<Integer> startPageOptional) {

		HttpServletRequest request = _requestSupplier.get();

		String emptyResultMessage = _language.format(
			request, "no-results-were-found-that-matched-the-keywords-x",
			"<strong>" + _html.escape(_keywords) + "</strong>", false);

		SearchContainer<Document> searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null, emptyResultMessage);

		return searchContainer;
	}

	protected PortletURL getPortletURL() {
		try {
			return _portletURLFactory.getPortletURL();
		}
		catch (PortletException pe) {
			throw new RuntimeException(pe);
		}
	}

	private final Html _html;
	private final String _keywords;
	private final Language _language;
	private final PortletURLFactory _portletURLFactory;
	private final RenderRequest _renderRequest;
	private final PortalHttpServletRequestSupplier _requestSupplier;

}