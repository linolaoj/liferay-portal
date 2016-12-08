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

package com.liferay.portal.search.web.internal.portlet.shared;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import java.util.Optional;
import java.util.function.Supplier;

import javax.portlet.RenderRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = PortletSharedTaskExecutor.class)
public class PortletSharedTaskExecutorImpl
	implements PortletSharedTaskExecutor {

	@Override
	public synchronized <T> T executeOnlyOnce(
		Supplier<T> supplier, String attributeSuffix,
		RenderRequest renderRequest) {

		String attributeName = getRequestSharedAttributeName(attributeSuffix);

		Optional<T> attributeValueOptional =
			portletSharedRequestHelper.getAttribute(
				attributeName, renderRequest);

		return attributeValueOptional.orElseGet(
			() -> {
				T attributeValue = supplier.get();

				portletSharedRequestHelper.setAttribute(
					attributeName, attributeValue, renderRequest);

				return attributeValue;
			});
	}

	protected String getRequestSharedAttributeName(String attributeSuffix) {
		String[] requestSharedAttributes = props.getArray(
			PropsKeys.REQUEST_SHARED_ATTRIBUTES);

		return requestSharedAttributes[0].concat(attributeSuffix);
	}

	@Reference
	protected PortletSharedRequestHelper portletSharedRequestHelper;

	@Reference
	protected Props props;

}