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

package com.liferay.portal.search.web.internal.portletsharedtask;

import com.liferay.portal.kernel.util.HttpUtil;

import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = PortletSharedURLHelper.class)
public class PortletSharedURLHelperImpl implements PortletSharedURLHelper {

	@Override
	public String getURLString(RenderRequest renderRequest) {
		HttpServletRequest httpServletRequest =
			portletSharedRequestHelper.getOriginalHttpServletRequest(
				renderRequest);

		// Must use HttpUtil, instead of @Reference Http
		// otherwise Component remains undeployed, with no stack trace.

		String urlString = HttpUtil.getCompleteURL(httpServletRequest);

		return urlString;
	}

	@Reference
	protected PortletSharedRequestHelper portletSharedRequestHelper;

}