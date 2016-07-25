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

package com.liferay.portal.search.web.internal.search.facet.portlet;

import com.liferay.portal.search.web.search.request.SearchResponse;

import java.io.Serializable;

/**
 * @author André de Oliveira
 * @author Rodrigo Paulino
 */
public class SearchFacetDisplayContext implements Serializable {

	public static final String ATTRIBUTE = "SearchFacetDisplayContext";

	public SearchFacetDisplayContext(
		String qParameter, SearchResponse searchResponse) {

		_qParameter = qParameter;
		_searchResponse = searchResponse;
	}

	public String getQ() {
		return _qParameter;
	}

	public SearchResponse getSearchResponse() {
		return _searchResponse;
	}

	private final String _qParameter;
	private final SearchResponse _searchResponse;

}