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

package com.liferay.portal.search.web.internal.facet.display.builder;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetFieldDisplayContext;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Lino Alves
 */
public class AssetCategoriesSearchFacetDisplayBuilder {

	public AssetCategoriesSearchFacetDisplayContext build()
		throws PortalException {

		AssetCategoriesSearchFacetDisplayContext
		assetCategoriesSearchFacetDisplayContext =
			new AssetCategoriesSearchFacetDisplayContext();

		assetCategoriesSearchFacetDisplayContext.setDisplayStyle(_displayStyle);
		assetCategoriesSearchFacetDisplayContext.setFrequencyThreshold(
			_frequencyThreshold);
		assetCategoriesSearchFacetDisplayContext.setMaxTerms(_maxTerms);
		assetCategoriesSearchFacetDisplayContext.setShowAssetCount(
			_showAssetCount);
		assetCategoriesSearchFacetDisplayContext.setFacet(_facet);
		assetCategoriesSearchFacetDisplayContext.setFieldParamInputValue(
			_fieldParam);

		assetCategoriesSearchFacetDisplayContext.
		setAssetCategoriesSearchFacetFieldDisplayContexts(buildFields());

		return assetCategoriesSearchFacetDisplayContext;
	}

	public AssetCategoriesSearchFacetFieldDisplayContext buildField(
		int popularity, boolean isSelected, AssetCategory assetCategory,
		int frequency) {

		AssetCategoriesSearchFacetFieldDisplayContext
		assetCategoriesSearchFacetFieldDisplayContext =
			new AssetCategoriesSearchFacetFieldDisplayContext();

		assetCategoriesSearchFacetFieldDisplayContext.setFrequency(frequency);
		assetCategoriesSearchFacetFieldDisplayContext.setIsSelected(isSelected);
		assetCategoriesSearchFacetFieldDisplayContext.setTitle(
			assetCategory.getTitle(_locale));
		assetCategoriesSearchFacetFieldDisplayContext.setAssetCategoryId(
			assetCategory.getCategoryId());
		assetCategoriesSearchFacetFieldDisplayContext.setPopularity(popularity);

		return assetCategoriesSearchFacetFieldDisplayContext;
	}

	public List<AssetCategoriesSearchFacetFieldDisplayContext> buildFields()
		throws PortalException {

		List<AssetCategoriesSearchFacetFieldDisplayContext>
		assetCategoriesSearchFacetFieldDisplayContexts = new ArrayList<>();

		FacetCollector facetCollector = _facet.getFacetCollector();

		List<TermCollector> termCollectors =
			Collections.<TermCollector>emptyList();

		if (facetCollector != null) {
			termCollectors = facetCollector.getTermCollectors();
		}

		int maxCount = 1;
		int minCount = 1;

		if (_showAssetCount && _displayStyle.equals("cloud")) {

			// The cloud style may not list tags in the order of frequency,
			// so keep looking through the results until we reach the maximum
			// number of terms or we run out of terms.

			for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
				if (j >= _maxTerms) {
					break;
				}

				TermCollector termCollector = termCollectors.get(i);

				int frequency = termCollector.getFrequency();

				if (_frequencyThreshold > frequency) {
					j--;

					continue;
				}

				maxCount = Math.max(maxCount, frequency);
				minCount = Math.min(minCount, frequency);
			}
		}

		double multiplier = 1;

		if (maxCount != minCount) {
			multiplier = (double)5 / (maxCount - minCount);
		}

		for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
			if (j >= _maxTerms) {
				break;
			}

			TermCollector termCollector = termCollectors.get(i);

			long assetCategoryId = GetterUtil.getLong(termCollector.getTerm());

			if (assetCategoryId == 0) {
				continue;
			}

			AssetCategory curAssetCategory =
				AssetCategoryLocalServiceUtil.fetchAssetCategory(
					assetCategoryId);

			if ((curAssetCategory != null) &&
				AssetCategoryPermission.contains(
					_permissionChecker, curAssetCategory, ActionKeys.VIEW)) {

				int popularity = getPopularity(
					termCollector.getFrequency(), maxCount, minCount,
					multiplier);

				if (_frequencyThreshold > termCollector.getFrequency()) {
					j--;

					continue;
				}

				AssetCategoriesSearchFacetFieldDisplayContext
				assetCategoriesSearchFacetFieldDisplayContext = buildField(
					popularity, _fieldParam.equals(termCollector.getTerm()),
					curAssetCategory, termCollector.getFrequency());

				assetCategoriesSearchFacetFieldDisplayContexts.add(
					assetCategoriesSearchFacetFieldDisplayContext);
			}
		}

		return assetCategoriesSearchFacetFieldDisplayContexts;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public Facet getFacet() {
		return _facet;
	}

	public String getFieldParam() {
		return _fieldParam;
	}

	public int getFrequencyThreshold() {
		return _frequencyThreshold;
	}

	public Locale getLocale() {
		return _locale;
	}

	public int getMaxTerms() {
		return _maxTerms;
	}

	public PermissionChecker getPermissionChecker() {
		return _permissionChecker;
	}

	public int getPopularity(
		int frequency, int maxCount, int minCount, double multiplier) {

				int popularity = maxCount - (maxCount - (frequency - minCount));

				return (int)(1 + (popularity * multiplier));
	}

	public boolean isShowAssetCount() {
		return _showAssetCount;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFieldParam(String fieldParam) {
		_fieldParam = fieldParam;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setPermissionChecker(PermissionChecker permissionChecker) {
		_permissionChecker = permissionChecker;
	}

	public void setShowAssetCount(boolean showAssetCount) {
		_showAssetCount = showAssetCount;
	}

	private String _displayStyle;
	private Facet _facet;
	private String _fieldParam;
	private int _frequencyThreshold;
	private Locale _locale;
	private int _maxTerms;
	private PermissionChecker _permissionChecker;
	private boolean _showAssetCount;

}