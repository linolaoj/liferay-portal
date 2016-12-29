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

package com.liferay.portal.search.web.internal.portlet.facet.asset.categories;

import java.util.List;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.ListUtil;

/**
 * @author Lino Alves
 */
public class AssetCategoriesFacetPortletDisplayContext {

	public List<AssetCategoriesFacetPortletFieldDisplayContext>
	getFieldDisplayContexts() {
		return _assetCategoriesSearchFacetFieldDisplayContexts;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public String getFieldParamInputName() {
		return _fieldParamInputName;
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
		return ListUtil.isEmpty(_assetCategoriesSearchFacetFieldDisplayContexts);
	}

	public boolean isShowAssetCount() {
		return _showAssetCount;
	}

	public void setFieldDisplayContexts(
			List<AssetCategoriesFacetPortletFieldDisplayContext>
			assetCategoriesFacetPortletFieldDisplayContexts) {
				_assetCategoriesSearchFacetFieldDisplayContexts =
						assetCategoriesFacetPortletFieldDisplayContexts;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setFieldParamInputName(String fieldParamInputName) {
		_fieldParamInputName = fieldParamInputName;
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

	public static final String ATTRIBUTE = "AssetCategoriesFacetPortlet";
	
	private List<AssetCategoriesFacetPortletFieldDisplayContext>
	_assetCategoriesSearchFacetFieldDisplayContexts;
	private String _displayStyle;
	private String _fieldParam;
	private String _fieldParamInputName;
	private int _frequencyThreshold;
	private int _maxTerms;
	private boolean _nothingSelected;
	private boolean _showAssetCount;
	
}
