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

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.util.List;

/**
 * @author Lino Alves
 */
public class AssetCategoriesSearchFacetDisplayContext implements Serializable {

	public List<AssetCategoriesSearchFacetFieldDisplayContext>
	getAssetCategoriesSearchFacetFieldDisplayContexts() {
		return _assetCategoriesSearchFacetFieldDisplayContext;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public String getFieldParamInputName() {
		return _facet.getFieldId();
	}

	public String getFieldParamInputValue() {
		return _fieldParam;
	}

	public int getFrequencyThreshold() {
		return _frequencyThreshold;
	}

	public int getMaxTerms() {
		return _maxTerms;
	}

	public boolean isNothingSelected() {
		return _nothingSelected;
	}
	
	public boolean isRenderNothing() {
		return ListUtil.isEmpty(_assetCategoriesSearchFacetFieldDisplayContext);
	}

	public boolean isShowAssetCount() {
		return _showAssetCount;
	}

	public void setAssetCategoriesSearchFacetFieldDisplayContexts(
			List<AssetCategoriesSearchFacetFieldDisplayContext>
			assetCategoriesSearchFacetFieldDisplayContext) {
				_assetCategoriesSearchFacetFieldDisplayContext =
					assetCategoriesSearchFacetFieldDisplayContext;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFieldParamInputValue(String fieldParam) {
		_fieldParam = fieldParam;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setNothingSelected(boolean nothingSelected) {
		_nothingSelected = nothingSelected;
	}
	
	public void setShowAssetCount(boolean showAssetCount) {
		_showAssetCount = showAssetCount;
	}

	private List<AssetCategoriesSearchFacetFieldDisplayContext>
	_assetCategoriesSearchFacetFieldDisplayContext;
	private String _displayStyle;
	private Facet _facet;
	private String _fieldParam;
	private int _frequencyThreshold;
	private int _maxTerms;
	private boolean _nothingSelected;
	private boolean _showAssetCount;
}