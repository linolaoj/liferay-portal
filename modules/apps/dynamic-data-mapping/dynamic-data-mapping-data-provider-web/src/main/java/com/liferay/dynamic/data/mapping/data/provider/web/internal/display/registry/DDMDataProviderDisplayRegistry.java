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

package com.liferay.dynamic.data.mapping.data.provider.web.internal.display.registry;

import aQute.bnd.annotation.ProviderType;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderDisplay;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Lino Alves
 */
@Component(immediate = true, service = DDMDataProviderDisplayRegistry.class)
@ProviderType
public class DDMDataProviderDisplayRegistry {

	public DDMDataProviderDisplay getDDMDataProviderDisplay(String portletId) {
		return _getDDMDataProviderDisplay(portletId);
	}

	public List<DDMDataProviderDisplay> getgetDDMDataProviderDisplays() {
		return _getDDMDataProviderDisplays();
	}

	public String[] getPortletIds() {
		return _getPortletIds();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setDDMDataProviderDisplay(
		DDMDataProviderDisplay ddmDataProviderDisplay) {

		_ddmDataProviderDisplay.put(
			ddmDataProviderDisplay.getPortletId(), ddmDataProviderDisplay);
	}

	protected void unsetDDMDataProviderDisplay(
		DDMDataProviderDisplay ddmDataProviderDisplay) {

		_ddmDataProviderDisplay.remove(ddmDataProviderDisplay.getPortletId());
	}

	private DDMDataProviderDisplay _getDDMDataProviderDisplay(
		String portletId) {

		return _ddmDataProviderDisplay.get(portletId);
	}

	private List<DDMDataProviderDisplay> _getDDMDataProviderDisplays() {
		return ListUtil.fromMapValues(_ddmDataProviderDisplay);
	}

	private String[] _getPortletIds() {
		Set<String> portletIds = _ddmDataProviderDisplay.keySet();

		return portletIds.toArray(new String[portletIds.size()]);
	}

	private final Map<String, DDMDataProviderDisplay> _ddmDataProviderDisplay =
		new ConcurrentHashMap<>();

}