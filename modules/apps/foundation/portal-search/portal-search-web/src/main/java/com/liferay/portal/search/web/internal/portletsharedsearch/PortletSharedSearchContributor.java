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

package com.liferay.portal.search.web.internal.portletsharedsearch;

import com.liferay.portal.search.web.internal.portletsharedtask.PortletSharedRequestHelper;
import com.liferay.portal.search.web.portletsharedsearch.PortletSharedSearchSettings;
import com.liferay.portal.search.web.portletsharedsearch.SearchAwarePortlet;
import com.liferay.portal.search.web.search.SearchSettings;
import com.liferay.portal.search.web.search.SearchSettingsContributor;

import java.util.Optional;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

/**
 * @author André de Oliveira
 */
public class PortletSharedSearchContributor
	implements SearchSettingsContributor {

	public PortletSharedSearchContributor(
		SearchAwarePortlet searchAwarePortlet, RenderRequest renderRequest,
		Optional<PortletPreferences> portletPreferencesOptional,
		PortletSharedRequestHelper portletSharedRequestHelper) {

		_searchAwarePortlet = searchAwarePortlet;
		_renderRequest = renderRequest;
		_portletPreferences = portletPreferencesOptional.orElse(null);
		_portletSharedRequestHelper = portletSharedRequestHelper;
	}

	@Override
	public void contribute(SearchSettings searchSettings) {
		PortletSharedSearchSettings portletSharedSearchSettings =
			new PortletSharedSearchSettingsImpl(
				searchSettings, _portletPreferences,
				_portletSharedRequestHelper, _renderRequest);

		_searchAwarePortlet.contribute(portletSharedSearchSettings);
	}

	private final PortletPreferences _portletPreferences;
	private final PortletSharedRequestHelper _portletSharedRequestHelper;
	private final RenderRequest _renderRequest;
	private final SearchAwarePortlet _searchAwarePortlet;

}