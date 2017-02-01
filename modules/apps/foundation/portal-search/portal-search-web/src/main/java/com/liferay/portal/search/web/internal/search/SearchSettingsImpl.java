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

package com.liferay.portal.search.web.internal.search;

import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.search.web.search.SearchSettings;

import java.util.Optional;

/**
 * @author André de Oliveira
 */
public class SearchSettingsImpl
	implements SearchSettings, SearchContainerOptions {

	public SearchSettingsImpl(SearchContext searchContext) {
		_searchContext = searchContext;
	}

	@Override
	public void addFacet(Facet facet) {
		_searchContext.addFacet(facet);
	}

	@Override
	public QueryConfig getQueryConfig() {
		return _searchContext.getQueryConfig();
	}

	@Override
	public SearchContext getSearchContext() {
		return _searchContext;
	}

	@Override
	public Optional<Integer> getStartPage() {
		return Optional.ofNullable(_startPage);
	}

	@Override
	public Optional<String> getStartPageParameterName() {
		return Optional.ofNullable(_startPageParameterName);
	}

	@Override
	public void setKeywords(String keywords) {
		_searchContext.setKeywords(keywords);
	}

	@Override
	public void setStartPage(int startPage) {
		_startPage = startPage;
	}

	@Override
	public void setStartPageParameterName(String startPageParamName) {
		_startPageParameterName = startPageParamName;
	}

	private final SearchContext _searchContext;
	private Integer _startPage;
	private String _startPageParameterName;

}