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

package com.liferay.dynamic.data.mapping.internal.model.listener;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.staging.model.listener.StagingModelListener;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(immediate = true, service = ModelListener.class)
public class DDMFormInstanceModelListener
	extends BaseModelListener<DDMFormInstance> {

	@Override
	public void onAfterCreate(DDMFormInstance ddmFormInstance)
		throws ModelListenerException {

		_stagingModelListener.onAfterCreate(ddmFormInstance);
	}

	@Override
	public void onAfterRemove(DDMFormInstance ddmFormInstance)
		throws ModelListenerException {

		_stagingModelListener.onAfterRemove(ddmFormInstance);

		_portalCache.remove(
			String.format("%d_readOnly", ddmFormInstance.getFormInstanceId()));
		_portalCache.remove(
			String.format("%d_editable", ddmFormInstance.getFormInstanceId()));
	}

	@Override
	public void onAfterUpdate(DDMFormInstance ddmFormInstance)
		throws ModelListenerException {

		_stagingModelListener.onAfterUpdate(ddmFormInstance);

		_portalCache.remove(
			String.format("%d_readOnly", ddmFormInstance.getFormInstanceId()));
		_portalCache.remove(
			String.format("%d_editable", ddmFormInstance.getFormInstanceId()));
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_portalCache = (PortalCache<String, String>)
			_multiVMPool.getPortalCache(DDMFormInstance.class.getName());
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<String, String> _portalCache;

	@Reference
	private StagingModelListener<DDMFormInstance> _stagingModelListener;

}