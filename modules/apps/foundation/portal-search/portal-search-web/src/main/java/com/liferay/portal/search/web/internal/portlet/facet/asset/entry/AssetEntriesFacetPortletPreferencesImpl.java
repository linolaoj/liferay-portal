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

package com.liferay.portal.search.web.internal.portlet.facet.asset.entry;

import javax.portlet.PortletPreferences;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
/**
 * @author Lino Alves
 */
public class AssetEntriesFacetPortletPreferencesImpl 
	implements AssetEntriesFacetPortletPreferences {

	public AssetEntriesFacetPortletPreferencesImpl(
			PortletPreferences portletPreferences) {
		_portletPreferencesHelper = new PortletPreferencesHelper(
				portletPreferences);
	}

	@Override
	public int getFrequencyThreshold() {
		return _portletPreferencesHelper.getInteger(
			AssetEntriesFacetPortletPreferences.PREFERENCE_FREQUENCY_THRESHOLD,
			AssetEntriesFacetConfiguration.DEFAULT_FREQUENCY_THRESHOLD);
	}

	@Override
	public String getParamName() {
		return _portletPreferencesHelper.getString(
				AssetEntriesFacetPortletPreferences.PREFERENCE_PARAM_NAME).orElse(
					AssetEntriesFacetPortletPreferences.DEFAULT_PARAM_NAME);
	}
	
	@Override
	public boolean isFrequenciesVisible() {
		return _portletPreferencesHelper.getBoolean(
			AssetEntriesFacetPortletPreferences.PREFERENCE_FREQUENCIES_VISIBLE,
			AssetEntriesFacetPortletPreferences.DEFAULT_FREQUENCIES_VISIBLE);
	}
	
	@Override
	public String getAssetTypes() {
		return _portletPreferencesHelper.getString(
			AssetEntriesFacetPortletPreferences.PREFERENCE_ASSET_TYPES).orElse("");
	}
	
	@Override
	public Optional<String[]> getAssetTypesArray() {
		Optional<String> assetTypes = _portletPreferencesHelper.getString(
			AssetEntriesFacetPortletPreferences.PREFERENCE_ASSET_TYPES);
		
		return assetTypes.map(s -> StringUtil.split(s));
	}
	
	public String[] getAllAssetTypes(long companyId) {
		
		ArrayList<String> classNames = new ArrayList<String>();
		
		for (AssetRendererFactory<?> assetRendererFactory : AssetRendererFactoryRegistryUtil.getAssetRendererFactories(companyId)) {
			String className = assetRendererFactory.getClassName();
			classNames.add(className);
		}
		
		return ArrayUtil.toStringArray(classNames);
	}
	
	@Override
	public List<KeyValuePair> getAvailableAssetTypes(long companyId, Locale locale) {
		
		List<KeyValuePair> availableAssetTypes = new ArrayList<KeyValuePair>();
		
		String[] assetTypes = getAssetTypesArray().orElse(getAllAssetTypes(companyId));
		
		for (String className : getAllAssetTypes(companyId)) {

			if (!ArrayUtil.contains(assetTypes, className)) {
				availableAssetTypes.add(
					new KeyValuePair(
						className, 
						ResourceActionsUtil.getModelResource(locale, className)));
			}
		}
		return availableAssetTypes;
	}

	@Override
	public List<KeyValuePair> getCurrentAssetTypes(long companyId, Locale locale){
		List<KeyValuePair> currentAssetTypes = new ArrayList<KeyValuePair>();
		
		String[] assetTypes = getAssetTypesArray().orElse(getAllAssetTypes(companyId));
		
		for (String assetType : assetTypes) {

			currentAssetTypes.add(
				new KeyValuePair(
					assetType, 
					ResourceActionsUtil.getModelResource(locale, assetType)));
		}
		return currentAssetTypes;
	}
	
	private final PortletPreferencesHelper _portletPreferencesHelper;
}
