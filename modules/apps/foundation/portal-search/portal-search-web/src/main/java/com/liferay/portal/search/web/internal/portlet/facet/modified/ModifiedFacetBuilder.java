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

package com.liferay.portal.search.web.internal.portlet.facet.modified;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.ModifiedFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;

/**
 * @author Lino Alves
 */
public class ModifiedFacetBuilder {
	
	public ModifiedFacet build() {
		ModifiedFacet modifiedFacet = new ModifiedFacet(
			_searchContext);

		modifiedFacet.setFacetConfiguration(buildFacetConfiguration());

		return modifiedFacet;
	}

	public void setSearchContext(SearchContext searchContext) {
		_searchContext = searchContext;
	}

	protected FacetConfiguration buildFacetConfiguration() {
		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setClassName(ModifiedFacet.class.getName());
		facetConfiguration.setFieldName(ModifiedFacetConstants.FIELD_NAME);
		facetConfiguration.setLabel("any-time");
		facetConfiguration.setOrder("OrderHitsDesc");
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.0);

		ModifiedFacetConfiguration modifiedFacetConfiguration =
			new ModifiedFacetConfigurationImpl(facetConfiguration);

		modifiedFacetConfiguration.setRangesJSONArray(getRangesJSONArray());
		
		return facetConfiguration;
	}

	protected JSONArray getRangesJSONArray() {

		JSONArray rangesJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < _LABELS.length; i++) {
			JSONObject range = JSONFactoryUtil.createJSONObject();

			range.put("label", _LABELS[i]);
			range.put("range", _RANGES[i]);

			rangesJSONArray.put(range);
		}

		return rangesJSONArray;
	}
	
	private SearchContext _searchContext;
	
	private static final String[] _LABELS = new String[] {
		"past-hour", "past-24-hours", "past-week", "past-month", "past-year"
	};

	private static final String[] _RANGES = new String[] {
		"[past-hour TO *]", "[past-24-hours TO *]", "[past-week TO *]",
		"[past-month TO *]", "[past-year TO *]"
	};
}
